package net.minecraft.world.level.chunk;

import com.google.common.base.Stopwatch;
import com.mojang.datafixers.Products.P1;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportSystemDetails;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.SystemUtils;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.SectionPosition;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.RegionLimitedWorldAccess;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSettingsMobs;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.levelgen.ChunkGeneratorAbstract;
import net.minecraft.world.level.levelgen.ChunkProviderDebug;
import net.minecraft.world.level.levelgen.ChunkProviderFlat;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.WorldGenStage;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;

public abstract class ChunkGenerator implements BiomeManager.Provider {

    private static final Logger LOGGER;
    public static final Codec<ChunkGenerator> CODEC;
    public final IRegistry<StructureSet> structureSets;
    protected final WorldChunkManager biomeSource;
    protected final WorldChunkManager runtimeBiomeSource;
    public final Optional<HolderSet<StructureSet>> structureOverrides;
    private final Map<StructureFeature<?, ?>, List<StructurePlacement>> placementsForFeature;
    private final Map<ConcentricRingsStructurePlacement, CompletableFuture<List<ChunkCoordIntPair>>> ringPositions;
    private boolean hasGeneratedPositions;
    /** @deprecated */
    @Deprecated
    public final long ringPlacementSeed;

    protected static final <T extends ChunkGenerator> P1<Mu<T>, IRegistry<StructureSet>> commonCodec(Instance<T> instance) {
        return instance.group(RegistryOps.retrieveRegistry(IRegistry.STRUCTURE_SET_REGISTRY).forGetter((chunkgenerator) -> {
            return chunkgenerator.structureSets;
        }));
    }

    public ChunkGenerator(IRegistry<StructureSet> iregistry, Optional<HolderSet<StructureSet>> optional, WorldChunkManager worldchunkmanager) {
        this(iregistry, optional, worldchunkmanager, worldchunkmanager, 0L);
    }

    public ChunkGenerator(IRegistry<StructureSet> iregistry, Optional<HolderSet<StructureSet>> optional, WorldChunkManager worldchunkmanager, WorldChunkManager worldchunkmanager1, long i) {
        this.placementsForFeature = new Object2ObjectOpenHashMap();
        this.ringPositions = new Object2ObjectArrayMap();
        this.structureSets = iregistry;
        this.biomeSource = worldchunkmanager;
        this.runtimeBiomeSource = worldchunkmanager1;
        this.structureOverrides = optional;
        this.ringPlacementSeed = i;
    }

    public Stream<Holder<StructureSet>> possibleStructureSets() {
        return this.structureOverrides.isPresent() ? ((HolderSet) this.structureOverrides.get()).stream() : this.structureSets.holders().map(Holder::hackyErase);
    }

    private void generatePositions() {
        Set<Holder<BiomeBase>> set = this.runtimeBiomeSource.possibleBiomes();

        this.possibleStructureSets().forEach((holder) -> {
            StructureSet structureset = (StructureSet) holder.value();
            Iterator iterator = structureset.structures().iterator();

            while (iterator.hasNext()) {
                StructureSet.a structureset_a = (StructureSet.a) iterator.next();

                ((List) this.placementsForFeature.computeIfAbsent((StructureFeature) structureset_a.structure().value(), (structurefeature) -> {
                    return new ArrayList();
                })).add(structureset.placement());
            }

            StructurePlacement structureplacement = structureset.placement();

            if (structureplacement instanceof ConcentricRingsStructurePlacement) {
                ConcentricRingsStructurePlacement concentricringsstructureplacement = (ConcentricRingsStructurePlacement) structureplacement;

                if (structureset.structures().stream().anyMatch((structureset_a1) -> {
                    Objects.requireNonNull(set);
                    return structureset_a1.generatesInMatchingBiome(set::contains);
                })) {
                    this.ringPositions.put(concentricringsstructureplacement, this.generateRingPositions(holder, concentricringsstructureplacement));
                }
            }

        });
    }

    private CompletableFuture<List<ChunkCoordIntPair>> generateRingPositions(Holder<StructureSet> holder, ConcentricRingsStructurePlacement concentricringsstructureplacement) {
        return concentricringsstructureplacement.count() == 0 ? CompletableFuture.completedFuture(List.of()) : CompletableFuture.supplyAsync(SystemUtils.wrapThreadWithTaskName("placement calculation", () -> {
            Stopwatch stopwatch = Stopwatch.createStarted(SystemUtils.TICKER);
            List<ChunkCoordIntPair> list = new ArrayList();
            Set<Holder<BiomeBase>> set = (Set) ((StructureSet) holder.value()).structures().stream().flatMap((structureset_a) -> {
                return ((StructureFeature) structureset_a.structure().value()).biomes().stream();
            }).collect(Collectors.toSet());
            int i = concentricringsstructureplacement.distance();
            int j = concentricringsstructureplacement.count();
            int k = concentricringsstructureplacement.spread();
            Random random = new Random();

            random.setSeed(this.ringPlacementSeed);
            double d0 = random.nextDouble() * 3.141592653589793D * 2.0D;
            int l = 0;
            int i1 = 0;

            for (int j1 = 0; j1 < j; ++j1) {
                double d1 = (double) (4 * i + i * i1 * 6) + (random.nextDouble() - 0.5D) * (double) i * 2.5D;
                int k1 = (int) Math.round(Math.cos(d0) * d1);
                int l1 = (int) Math.round(Math.sin(d0) * d1);
                WorldChunkManager worldchunkmanager = this.biomeSource;
                int i2 = SectionPosition.sectionToBlockCoord(k1, 8);
                int j2 = SectionPosition.sectionToBlockCoord(l1, 8);

                Objects.requireNonNull(set);
                Pair<BlockPosition, Holder<BiomeBase>> pair = worldchunkmanager.findBiomeHorizontal(i2, 0, j2, 112, set::contains, random, this.climateSampler());

                if (pair != null) {
                    BlockPosition blockposition = (BlockPosition) pair.getFirst();

                    k1 = SectionPosition.blockToSectionCoord(blockposition.getX());
                    l1 = SectionPosition.blockToSectionCoord(blockposition.getZ());
                }

                list.add(new ChunkCoordIntPair(k1, l1));
                d0 += 6.283185307179586D / (double) k;
                ++l;
                if (l == k) {
                    ++i1;
                    l = 0;
                    k += 2 * k / (i1 + 1);
                    k = Math.min(k, j - j1);
                    d0 += random.nextDouble() * 3.141592653589793D * 2.0D;
                }
            }

            double d2 = (double) stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) / 1000.0D;

            ChunkGenerator.LOGGER.debug("Calculation for {} took {}s", holder, d2);
            return list;
        }), SystemUtils.backgroundExecutor());
    }

    protected abstract Codec<? extends ChunkGenerator> codec();

    public Optional<ResourceKey<Codec<? extends ChunkGenerator>>> getTypeNameForDataFixer() {
        return IRegistry.CHUNK_GENERATOR.getResourceKey(this.codec());
    }

    public abstract ChunkGenerator withSeed(long i);

    public CompletableFuture<IChunkAccess> createBiomes(IRegistry<BiomeBase> iregistry, Executor executor, Blender blender, StructureManager structuremanager, IChunkAccess ichunkaccess) {
        return CompletableFuture.supplyAsync(SystemUtils.wrapThreadWithTaskName("init_biomes", () -> {
            WorldChunkManager worldchunkmanager = this.runtimeBiomeSource;

            Objects.requireNonNull(this.runtimeBiomeSource);
            ichunkaccess.fillBiomesFromNoise(worldchunkmanager::getNoiseBiome, this.climateSampler());
            return ichunkaccess;
        }), SystemUtils.backgroundExecutor());
    }

    public abstract Climate.Sampler climateSampler();

    @Override
    public Holder<BiomeBase> getNoiseBiome(int i, int j, int k) {
        return this.getBiomeSource().getNoiseBiome(i, j, k, this.climateSampler());
    }

    public abstract void applyCarvers(RegionLimitedWorldAccess regionlimitedworldaccess, long i, BiomeManager biomemanager, StructureManager structuremanager, IChunkAccess ichunkaccess, WorldGenStage.Features worldgenstage_features);

    @Nullable
    public Pair<BlockPosition, Holder<StructureFeature<?, ?>>> findNearestMapFeature(WorldServer worldserver, HolderSet<StructureFeature<?, ?>> holderset, BlockPosition blockposition, int i, boolean flag) {
        Set<Holder<BiomeBase>> set = (Set) holderset.stream().flatMap((holder) -> {
            return ((StructureFeature) holder.value()).biomes().stream();
        }).collect(Collectors.toSet());

        if (set.isEmpty()) {
            return null;
        } else {
            Set<Holder<BiomeBase>> set1 = this.runtimeBiomeSource.possibleBiomes();

            if (Collections.disjoint(set1, set)) {
                return null;
            } else {
                Pair<BlockPosition, Holder<StructureFeature<?, ?>>> pair = null;
                double d0 = Double.MAX_VALUE;
                Map<StructurePlacement, Set<Holder<StructureFeature<?, ?>>>> map = new Object2ObjectArrayMap();
                Iterator iterator = holderset.iterator();

                StructurePlacement structureplacement;

                while (iterator.hasNext()) {
                    Holder<StructureFeature<?, ?>> holder = (Holder) iterator.next();
                    Stream stream = set1.stream();
                    HolderSet holderset1 = ((StructureFeature) holder.value()).biomes();

                    Objects.requireNonNull(holderset1);
                    if (!stream.noneMatch(holderset1::contains)) {
                        Iterator iterator1 = this.getPlacementsForFeature(holder).iterator();

                        while (iterator1.hasNext()) {
                            structureplacement = (StructurePlacement) iterator1.next();
                            ((Set) map.computeIfAbsent(structureplacement, (structureplacement1) -> {
                                return new ObjectArraySet();
                            })).add(holder);
                        }
                    }
                }

                List<Entry<StructurePlacement, Set<Holder<StructureFeature<?, ?>>>>> list = new ArrayList(map.size());
                Iterator iterator2 = map.entrySet().iterator();

                while (iterator2.hasNext()) {
                    Entry<StructurePlacement, Set<Holder<StructureFeature<?, ?>>>> entry = (Entry) iterator2.next();

                    structureplacement = (StructurePlacement) entry.getKey();
                    if (structureplacement instanceof ConcentricRingsStructurePlacement) {
                        ConcentricRingsStructurePlacement concentricringsstructureplacement = (ConcentricRingsStructurePlacement) structureplacement;
                        BlockPosition blockposition1 = this.getNearestGeneratedStructure(blockposition, concentricringsstructureplacement);
                        double d1 = blockposition.distSqr(blockposition1);

                        if (d1 < d0) {
                            d0 = d1;
                            pair = Pair.of(blockposition1, (Holder) ((Set) entry.getValue()).iterator().next());
                        }
                    } else if (structureplacement instanceof RandomSpreadStructurePlacement) {
                        list.add(entry);
                    }
                }

                if (!list.isEmpty()) {
                    int j = SectionPosition.blockToSectionCoord(blockposition.getX());
                    int k = SectionPosition.blockToSectionCoord(blockposition.getZ());

                    for (int l = 0; l <= i; ++l) {
                        boolean flag1 = false;
                        Iterator iterator3 = list.iterator();

                        while (iterator3.hasNext()) {
                            Entry<StructurePlacement, Set<Holder<StructureFeature<?, ?>>>> entry1 = (Entry) iterator3.next();
                            RandomSpreadStructurePlacement randomspreadstructureplacement = (RandomSpreadStructurePlacement) entry1.getKey();
                            Pair<BlockPosition, Holder<StructureFeature<?, ?>>> pair1 = getNearestGeneratedStructure((Set) entry1.getValue(), worldserver, worldserver.structureFeatureManager(), j, k, l, flag, worldserver.getSeed(), randomspreadstructureplacement);

                            if (pair1 != null) {
                                flag1 = true;
                                double d2 = blockposition.distSqr((BaseBlockPosition) pair1.getFirst());

                                if (d2 < d0) {
                                    d0 = d2;
                                    pair = pair1;
                                }
                            }
                        }

                        if (flag1) {
                            return pair;
                        }
                    }
                }

                return pair;
            }
        }
    }

    @Nullable
    private BlockPosition getNearestGeneratedStructure(BlockPosition blockposition, ConcentricRingsStructurePlacement concentricringsstructureplacement) {
        List<ChunkCoordIntPair> list = this.getRingPositionsFor(concentricringsstructureplacement);

        if (list == null) {
            throw new IllegalStateException("Somehow tried to find structures for a placement that doesn't exist");
        } else {
            BlockPosition blockposition1 = null;
            double d0 = Double.MAX_VALUE;
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair) iterator.next();

                blockposition_mutableblockposition.set(SectionPosition.sectionToBlockCoord(chunkcoordintpair.x, 8), 32, SectionPosition.sectionToBlockCoord(chunkcoordintpair.z, 8));
                double d1 = blockposition_mutableblockposition.distSqr(blockposition);

                if (blockposition1 == null) {
                    blockposition1 = new BlockPosition(blockposition_mutableblockposition);
                    d0 = d1;
                } else if (d1 < d0) {
                    blockposition1 = new BlockPosition(blockposition_mutableblockposition);
                    d0 = d1;
                }
            }

            return blockposition1;
        }
    }

    @Nullable
    private static Pair<BlockPosition, Holder<StructureFeature<?, ?>>> getNearestGeneratedStructure(Set<Holder<StructureFeature<?, ?>>> set, IWorldReader iworldreader, StructureManager structuremanager, int i, int j, int k, boolean flag, long l, RandomSpreadStructurePlacement randomspreadstructureplacement) {
        int i1 = randomspreadstructureplacement.spacing();

        for (int j1 = -k; j1 <= k; ++j1) {
            boolean flag1 = j1 == -k || j1 == k;

            for (int k1 = -k; k1 <= k; ++k1) {
                boolean flag2 = k1 == -k || k1 == k;

                if (flag1 || flag2) {
                    int l1 = i + i1 * j1;
                    int i2 = j + i1 * k1;
                    ChunkCoordIntPair chunkcoordintpair = randomspreadstructureplacement.getPotentialFeatureChunk(l, l1, i2);
                    Iterator iterator = set.iterator();

                    while (iterator.hasNext()) {
                        Holder<StructureFeature<?, ?>> holder = (Holder) iterator.next();
                        StructureCheckResult structurecheckresult = structuremanager.checkStructurePresence(chunkcoordintpair, (StructureFeature) holder.value(), flag);

                        if (structurecheckresult != StructureCheckResult.START_NOT_PRESENT) {
                            if (!flag && structurecheckresult == StructureCheckResult.START_PRESENT) {
                                return Pair.of(StructureGenerator.getLocatePos(randomspreadstructureplacement, chunkcoordintpair), holder);
                            }

                            IChunkAccess ichunkaccess = iworldreader.getChunk(chunkcoordintpair.x, chunkcoordintpair.z, ChunkStatus.STRUCTURE_STARTS);
                            StructureStart structurestart = structuremanager.getStartForFeature(SectionPosition.bottomOf(ichunkaccess), (StructureFeature) holder.value(), ichunkaccess);

                            if (structurestart != null && structurestart.isValid()) {
                                if (flag && structurestart.canBeReferenced()) {
                                    structuremanager.addReference(structurestart);
                                    return Pair.of(StructureGenerator.getLocatePos(randomspreadstructureplacement, structurestart.getChunkPos()), holder);
                                }

                                if (!flag) {
                                    return Pair.of(StructureGenerator.getLocatePos(randomspreadstructureplacement, structurestart.getChunkPos()), holder);
                                }
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    public void applyBiomeDecoration(GeneratorAccessSeed generatoraccessseed, IChunkAccess ichunkaccess, StructureManager structuremanager) {
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();

        if (!SharedConstants.debugVoidTerrain(chunkcoordintpair)) {
            SectionPosition sectionposition = SectionPosition.of(chunkcoordintpair, generatoraccessseed.getMinSection());
            BlockPosition blockposition = sectionposition.origin();
            IRegistry<StructureFeature<?, ?>> iregistry = generatoraccessseed.registryAccess().registryOrThrow(IRegistry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);
            Map<Integer, List<StructureFeature<?, ?>>> map = (Map) iregistry.stream().collect(Collectors.groupingBy((structurefeature) -> {
                return structurefeature.feature.step().ordinal();
            }));
            List<WorldChunkManager.b> list = this.biomeSource.featuresPerStep();
            SeededRandom seededrandom = new SeededRandom(new XoroshiroRandomSource(RandomSupport.seedUniquifier()));
            long i = seededrandom.setDecorationSeed(generatoraccessseed.getSeed(), blockposition.getX(), blockposition.getZ());
            Set<BiomeBase> set = new ObjectArraySet();

            if (this instanceof ChunkProviderFlat) {
                Stream stream = this.biomeSource.possibleBiomes().stream().map(Holder::value);

                Objects.requireNonNull(set);
                stream.forEach(set::add);
            } else {
                ChunkCoordIntPair.rangeClosed(sectionposition.chunk(), 1).forEach((chunkcoordintpair1) -> {
                    IChunkAccess ichunkaccess1 = generatoraccessseed.getChunk(chunkcoordintpair1.x, chunkcoordintpair1.z);
                    ChunkSection[] achunksection = ichunkaccess1.getSections();
                    int j = achunksection.length;

                    for (int k = 0; k < j; ++k) {
                        ChunkSection chunksection = achunksection[k];

                        chunksection.getBiomes().getAll((holder) -> {
                            set.add((BiomeBase) holder.value());
                        });
                    }

                });
                set.retainAll((Collection) this.biomeSource.possibleBiomes().stream().map(Holder::value).collect(Collectors.toSet()));
            }

            int j = list.size();

            try {
                IRegistry<PlacedFeature> iregistry1 = generatoraccessseed.registryAccess().registryOrThrow(IRegistry.PLACED_FEATURE_REGISTRY);
                int k = Math.max(WorldGenStage.Decoration.values().length, j);

                for (int l = 0; l < k; ++l) {
                    int i1 = 0;
                    Iterator iterator;
                    CrashReportSystemDetails crashreportsystemdetails;

                    if (structuremanager.shouldGenerateFeatures()) {
                        List<StructureFeature<?, ?>> list1 = (List) map.getOrDefault(l, Collections.emptyList());

                        for (iterator = list1.iterator(); iterator.hasNext(); ++i1) {
                            StructureFeature<?, ?> structurefeature = (StructureFeature) iterator.next();

                            seededrandom.setFeatureSeed(i, i1, l);
                            Supplier supplier = () -> {
                                Optional optional = iregistry.getResourceKey(structurefeature).map(Object::toString);

                                Objects.requireNonNull(structurefeature);
                                return (String) optional.orElseGet(structurefeature::toString);
                            };

                            try {
                                generatoraccessseed.setCurrentlyGenerating(supplier);
                                structuremanager.startsForFeature(sectionposition, structurefeature).forEach((structurestart) -> {
                                    structurestart.placeInChunk(generatoraccessseed, structuremanager, this, seededrandom, getWritableArea(ichunkaccess), chunkcoordintpair);
                                });
                            } catch (Exception exception) {
                                CrashReport crashreport = CrashReport.forThrowable(exception, "Feature placement");

                                crashreportsystemdetails = crashreport.addCategory("Feature");
                                Objects.requireNonNull(supplier);
                                crashreportsystemdetails.setDetail("Description", supplier::get);
                                throw new ReportedException(crashreport);
                            }
                        }
                    }

                    if (l < j) {
                        IntArraySet intarrayset = new IntArraySet();

                        iterator = set.iterator();

                        while (iterator.hasNext()) {
                            BiomeBase biomebase = (BiomeBase) iterator.next();
                            List<HolderSet<PlacedFeature>> list2 = biomebase.getGenerationSettings().features();

                            if (l < list2.size()) {
                                HolderSet<PlacedFeature> holderset = (HolderSet) list2.get(l);
                                WorldChunkManager.b worldchunkmanager_b = (WorldChunkManager.b) list.get(l);

                                holderset.stream().map(Holder::value).forEach((placedfeature) -> {
                                    intarrayset.add(worldchunkmanager_b.indexMapping().applyAsInt(placedfeature));
                                });
                            }
                        }

                        int j1 = intarrayset.size();
                        int[] aint = intarrayset.toIntArray();

                        Arrays.sort(aint);
                        WorldChunkManager.b worldchunkmanager_b1 = (WorldChunkManager.b) list.get(l);

                        for (int k1 = 0; k1 < j1; ++k1) {
                            int l1 = aint[k1];
                            PlacedFeature placedfeature = (PlacedFeature) worldchunkmanager_b1.features().get(l1);
                            Supplier<String> supplier1 = () -> {
                                Optional optional = iregistry1.getResourceKey(placedfeature).map(Object::toString);

                                Objects.requireNonNull(placedfeature);
                                return (String) optional.orElseGet(placedfeature::toString);
                            };

                            seededrandom.setFeatureSeed(i, l1, l);

                            try {
                                generatoraccessseed.setCurrentlyGenerating(supplier1);
                                placedfeature.placeWithBiomeCheck(generatoraccessseed, this, seededrandom, blockposition);
                            } catch (Exception exception1) {
                                CrashReport crashreport1 = CrashReport.forThrowable(exception1, "Feature placement");

                                crashreportsystemdetails = crashreport1.addCategory("Feature");
                                Objects.requireNonNull(supplier1);
                                crashreportsystemdetails.setDetail("Description", supplier1::get);
                                throw new ReportedException(crashreport1);
                            }
                        }
                    }
                }

                generatoraccessseed.setCurrentlyGenerating((Supplier) null);
            } catch (Exception exception2) {
                CrashReport crashreport2 = CrashReport.forThrowable(exception2, "Biome decoration");

                crashreport2.addCategory("Generation").setDetail("CenterX", (Object) chunkcoordintpair.x).setDetail("CenterZ", (Object) chunkcoordintpair.z).setDetail("Seed", (Object) i);
                throw new ReportedException(crashreport2);
            }
        }
    }

    public boolean hasFeatureChunkInRange(ResourceKey<StructureSet> resourcekey, long i, int j, int k, int l) {
        StructureSet structureset = (StructureSet) this.structureSets.get(resourcekey);

        if (structureset == null) {
            return false;
        } else {
            StructurePlacement structureplacement = structureset.placement();

            for (int i1 = j - l; i1 <= j + l; ++i1) {
                for (int j1 = k - l; j1 <= k + l; ++j1) {
                    if (structureplacement.isFeatureChunk(this, i, i1, j1)) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    private static StructureBoundingBox getWritableArea(IChunkAccess ichunkaccess) {
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        int i = chunkcoordintpair.getMinBlockX();
        int j = chunkcoordintpair.getMinBlockZ();
        LevelHeightAccessor levelheightaccessor = ichunkaccess.getHeightAccessorForGeneration();
        int k = levelheightaccessor.getMinBuildHeight() + 1;
        int l = levelheightaccessor.getMaxBuildHeight() - 1;

        return new StructureBoundingBox(i, k, j, i + 15, l, j + 15);
    }

    public abstract void buildSurface(RegionLimitedWorldAccess regionlimitedworldaccess, StructureManager structuremanager, IChunkAccess ichunkaccess);

    public abstract void spawnOriginalMobs(RegionLimitedWorldAccess regionlimitedworldaccess);

    public int getSpawnHeight(LevelHeightAccessor levelheightaccessor) {
        return 64;
    }

    public WorldChunkManager getBiomeSource() {
        return this.runtimeBiomeSource;
    }

    public abstract int getGenDepth();

    public WeightedRandomList<BiomeSettingsMobs.c> getMobsAt(Holder<BiomeBase> holder, StructureManager structuremanager, EnumCreatureType enumcreaturetype, BlockPosition blockposition) {
        Map<StructureFeature<?, ?>, LongSet> map = structuremanager.getAllStructuresAt(blockposition);
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<StructureFeature<?, ?>, LongSet> entry = (Entry) iterator.next();
            StructureFeature<?, ?> structurefeature = (StructureFeature) entry.getKey();
            StructureSpawnOverride structurespawnoverride = (StructureSpawnOverride) structurefeature.spawnOverrides.get(enumcreaturetype);

            if (structurespawnoverride != null) {
                MutableBoolean mutableboolean = new MutableBoolean(false);
                Predicate<StructureStart> predicate = structurespawnoverride.boundingBox() == StructureSpawnOverride.a.PIECE ? (structurestart) -> {
                    return structuremanager.structureHasPieceAt(blockposition, structurestart);
                } : (structurestart) -> {
                    return structurestart.getBoundingBox().isInside(blockposition);
                };

                structuremanager.fillStartsForFeature(structurefeature, (LongSet) entry.getValue(), (structurestart) -> {
                    if (mutableboolean.isFalse() && predicate.test(structurestart)) {
                        mutableboolean.setTrue();
                    }

                });
                if (mutableboolean.isTrue()) {
                    return structurespawnoverride.spawns();
                }
            }
        }

        return ((BiomeBase) holder.value()).getMobSettings().getMobs(enumcreaturetype);
    }

    public static Stream<StructureFeature<?, ?>> allConfigurations(IRegistry<StructureFeature<?, ?>> iregistry, StructureGenerator<?> structuregenerator) {
        return iregistry.stream().filter((structurefeature) -> {
            return structurefeature.feature == structuregenerator;
        });
    }

    public void createStructures(IRegistryCustom iregistrycustom, StructureManager structuremanager, IChunkAccess ichunkaccess, DefinedStructureManager definedstructuremanager, long i) {
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        SectionPosition sectionposition = SectionPosition.bottomOf(ichunkaccess);

        this.possibleStructureSets().forEach((holder) -> {
            StructurePlacement structureplacement = ((StructureSet) holder.value()).placement();
            List<StructureSet.a> list = ((StructureSet) holder.value()).structures();
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                StructureSet.a structureset_a = (StructureSet.a) iterator.next();
                StructureStart structurestart = structuremanager.getStartForFeature(sectionposition, (StructureFeature) structureset_a.structure().value(), ichunkaccess);

                if (structurestart != null && structurestart.isValid()) {
                    return;
                }
            }

            if (structureplacement.isFeatureChunk(this, i, chunkcoordintpair.x, chunkcoordintpair.z)) {
                if (list.size() == 1) {
                    this.tryGenerateStructure((StructureSet.a) list.get(0), structuremanager, iregistrycustom, definedstructuremanager, i, ichunkaccess, chunkcoordintpair, sectionposition);
                } else {
                    ArrayList<StructureSet.a> arraylist = new ArrayList(list.size());

                    arraylist.addAll(list);
                    SeededRandom seededrandom = new SeededRandom(new LegacyRandomSource(0L));

                    seededrandom.setLargeFeatureSeed(i, chunkcoordintpair.x, chunkcoordintpair.z);
                    int j = 0;

                    StructureSet.a structureset_a1;

                    for (Iterator iterator1 = arraylist.iterator(); iterator1.hasNext(); j += structureset_a1.weight()) {
                        structureset_a1 = (StructureSet.a) iterator1.next();
                    }

                    while (!arraylist.isEmpty()) {
                        int k = seededrandom.nextInt(j);
                        int l = 0;
                        Iterator iterator2 = arraylist.iterator();

                        while (true) {
                            if (iterator2.hasNext()) {
                                StructureSet.a structureset_a2 = (StructureSet.a) iterator2.next();

                                k -= structureset_a2.weight();
                                if (k >= 0) {
                                    ++l;
                                    continue;
                                }
                            }

                            StructureSet.a structureset_a3 = (StructureSet.a) arraylist.get(l);

                            if (this.tryGenerateStructure(structureset_a3, structuremanager, iregistrycustom, definedstructuremanager, i, ichunkaccess, chunkcoordintpair, sectionposition)) {
                                return;
                            }

                            arraylist.remove(l);
                            j -= structureset_a3.weight();
                            break;
                        }
                    }

                }
            }
        });
    }

    private boolean tryGenerateStructure(StructureSet.a structureset_a, StructureManager structuremanager, IRegistryCustom iregistrycustom, DefinedStructureManager definedstructuremanager, long i, IChunkAccess ichunkaccess, ChunkCoordIntPair chunkcoordintpair, SectionPosition sectionposition) {
        StructureFeature<?, ?> structurefeature = (StructureFeature) structureset_a.structure().value();
        int j = fetchReferences(structuremanager, ichunkaccess, sectionposition, structurefeature);
        HolderSet<BiomeBase> holderset = structurefeature.biomes();
        Predicate<Holder<BiomeBase>> predicate = (holder) -> {
            return holderset.contains(this.adjustBiome(holder));
        };
        StructureStart structurestart = structurefeature.generate(iregistrycustom, this, this.biomeSource, definedstructuremanager, i, chunkcoordintpair, j, ichunkaccess, predicate);

        if (structurestart.isValid()) {
            structuremanager.setStartForFeature(sectionposition, structurefeature, structurestart, ichunkaccess);
            return true;
        } else {
            return false;
        }
    }

    private static int fetchReferences(StructureManager structuremanager, IChunkAccess ichunkaccess, SectionPosition sectionposition, StructureFeature<?, ?> structurefeature) {
        StructureStart structurestart = structuremanager.getStartForFeature(sectionposition, structurefeature, ichunkaccess);

        return structurestart != null ? structurestart.getReferences() : 0;
    }

    protected Holder<BiomeBase> adjustBiome(Holder<BiomeBase> holder) {
        return holder;
    }

    public void createReferences(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, IChunkAccess ichunkaccess) {
        boolean flag = true;
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        int i = chunkcoordintpair.x;
        int j = chunkcoordintpair.z;
        int k = chunkcoordintpair.getMinBlockX();
        int l = chunkcoordintpair.getMinBlockZ();
        SectionPosition sectionposition = SectionPosition.bottomOf(ichunkaccess);

        for (int i1 = i - 8; i1 <= i + 8; ++i1) {
            for (int j1 = j - 8; j1 <= j + 8; ++j1) {
                long k1 = ChunkCoordIntPair.asLong(i1, j1);
                Iterator iterator = generatoraccessseed.getChunk(i1, j1).getAllStarts().values().iterator();

                while (iterator.hasNext()) {
                    StructureStart structurestart = (StructureStart) iterator.next();

                    try {
                        if (structurestart.isValid() && structurestart.getBoundingBox().intersects(k, l, k + 15, l + 15)) {
                            structuremanager.addReferenceForFeature(sectionposition, structurestart.getFeature(), k1, ichunkaccess);
                            PacketDebug.sendStructurePacket(generatoraccessseed, structurestart);
                        }
                    } catch (Exception exception) {
                        CrashReport crashreport = CrashReport.forThrowable(exception, "Generating structure reference");
                        CrashReportSystemDetails crashreportsystemdetails = crashreport.addCategory("Structure");
                        Optional<? extends IRegistry<StructureFeature<?, ?>>> optional = generatoraccessseed.registryAccess().registry(IRegistry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);

                        crashreportsystemdetails.setDetail("Id", () -> {
                            return (String) optional.map((iregistry) -> {
                                return iregistry.getKey(structurestart.getFeature()).toString();
                            }).orElse("UNKNOWN");
                        });
                        crashreportsystemdetails.setDetail("Name", () -> {
                            return IRegistry.STRUCTURE_FEATURE.getKey(structurestart.getFeature().feature).toString();
                        });
                        crashreportsystemdetails.setDetail("Class", () -> {
                            return structurestart.getFeature().getClass().getCanonicalName();
                        });
                        throw new ReportedException(crashreport);
                    }
                }
            }
        }

    }

    public abstract CompletableFuture<IChunkAccess> fillFromNoise(Executor executor, Blender blender, StructureManager structuremanager, IChunkAccess ichunkaccess);

    public abstract int getSeaLevel();

    public abstract int getMinY();

    public abstract int getBaseHeight(int i, int j, HeightMap.Type heightmap_type, LevelHeightAccessor levelheightaccessor);

    public abstract net.minecraft.world.level.BlockColumn getBaseColumn(int i, int j, LevelHeightAccessor levelheightaccessor);

    public int getFirstFreeHeight(int i, int j, HeightMap.Type heightmap_type, LevelHeightAccessor levelheightaccessor) {
        return this.getBaseHeight(i, j, heightmap_type, levelheightaccessor);
    }

    public int getFirstOccupiedHeight(int i, int j, HeightMap.Type heightmap_type, LevelHeightAccessor levelheightaccessor) {
        return this.getBaseHeight(i, j, heightmap_type, levelheightaccessor) - 1;
    }

    public void ensureStructuresGenerated() {
        if (!this.hasGeneratedPositions) {
            this.generatePositions();
            this.hasGeneratedPositions = true;
        }

    }

    @Nullable
    public List<ChunkCoordIntPair> getRingPositionsFor(ConcentricRingsStructurePlacement concentricringsstructureplacement) {
        this.ensureStructuresGenerated();
        CompletableFuture<List<ChunkCoordIntPair>> completablefuture = (CompletableFuture) this.ringPositions.get(concentricringsstructureplacement);

        return completablefuture != null ? (List) completablefuture.join() : null;
    }

    private List<StructurePlacement> getPlacementsForFeature(Holder<StructureFeature<?, ?>> holder) {
        this.ensureStructuresGenerated();
        return (List) this.placementsForFeature.getOrDefault(holder.value(), List.of());
    }

    public abstract void addDebugScreenInfo(List<String> list, BlockPosition blockposition);

    static {
        IRegistry.register(IRegistry.CHUNK_GENERATOR, "noise", ChunkGeneratorAbstract.CODEC);
        IRegistry.register(IRegistry.CHUNK_GENERATOR, "flat", ChunkProviderFlat.CODEC);
        IRegistry.register(IRegistry.CHUNK_GENERATOR, "debug", ChunkProviderDebug.CODEC);
        LOGGER = LogUtils.getLogger();
        CODEC = IRegistry.CHUNK_GENERATOR.byNameCodec().dispatchStable(ChunkGenerator::codec, Function.identity());
    }
}
