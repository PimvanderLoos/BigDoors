package net.minecraft.world.level.chunk;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
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
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.SectionPosition;
import net.minecraft.data.worldgen.StructureFeatures;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.RegionLimitedWorldAccess;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
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
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.WorldGenStage;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.StructureSettingsFeature;
import net.minecraft.world.level.levelgen.feature.configurations.StructureSettingsStronghold;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public abstract class ChunkGenerator implements BiomeManager.Provider {

    public static final Codec<ChunkGenerator> CODEC;
    protected final WorldChunkManager biomeSource;
    protected final WorldChunkManager runtimeBiomeSource;
    private final StructureSettings settings;
    public final long strongholdSeed;
    private final List<ChunkCoordIntPair> strongholdPositions;

    public ChunkGenerator(WorldChunkManager worldchunkmanager, StructureSettings structuresettings) {
        this(worldchunkmanager, worldchunkmanager, structuresettings, 0L);
    }

    public ChunkGenerator(WorldChunkManager worldchunkmanager, WorldChunkManager worldchunkmanager1, StructureSettings structuresettings, long i) {
        this.strongholdPositions = Lists.newArrayList();
        this.biomeSource = worldchunkmanager;
        this.runtimeBiomeSource = worldchunkmanager1;
        this.settings = structuresettings;
        this.strongholdSeed = i;
    }

    private void generateStrongholds() {
        if (this.strongholdPositions.isEmpty()) {
            StructureSettingsStronghold structuresettingsstronghold = this.settings.stronghold();

            if (structuresettingsstronghold != null && structuresettingsstronghold.count() != 0) {
                List<BiomeBase> list = Lists.newArrayList();
                Iterator iterator = this.biomeSource.possibleBiomes().iterator();

                while (iterator.hasNext()) {
                    BiomeBase biomebase = (BiomeBase) iterator.next();

                    if (validStrongholdBiome(biomebase)) {
                        list.add(biomebase);
                    }
                }

                int i = structuresettingsstronghold.distance();
                int j = structuresettingsstronghold.count();
                int k = structuresettingsstronghold.spread();
                Random random = new Random();

                random.setSeed(this.strongholdSeed);
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

                    Objects.requireNonNull(list);
                    BlockPosition blockposition = worldchunkmanager.findBiomeHorizontal(i2, 0, j2, 112, list::contains, random, this.climateSampler());

                    if (blockposition != null) {
                        k1 = SectionPosition.blockToSectionCoord(blockposition.getX());
                        l1 = SectionPosition.blockToSectionCoord(blockposition.getZ());
                    }

                    this.strongholdPositions.add(new ChunkCoordIntPair(k1, l1));
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

            }
        }
    }

    private static boolean validStrongholdBiome(BiomeBase biomebase) {
        BiomeBase.Geography biomebase_geography = biomebase.getBiomeCategory();

        return biomebase_geography != BiomeBase.Geography.OCEAN && biomebase_geography != BiomeBase.Geography.RIVER && biomebase_geography != BiomeBase.Geography.BEACH && biomebase_geography != BiomeBase.Geography.SWAMP && biomebase_geography != BiomeBase.Geography.NETHER && biomebase_geography != BiomeBase.Geography.THEEND;
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
    public BiomeBase getNoiseBiome(int i, int j, int k) {
        return this.getBiomeSource().getNoiseBiome(i, j, k, this.climateSampler());
    }

    public abstract void applyCarvers(RegionLimitedWorldAccess regionlimitedworldaccess, long i, BiomeManager biomemanager, StructureManager structuremanager, IChunkAccess ichunkaccess, WorldGenStage.Features worldgenstage_features);

    @Nullable
    public BlockPosition findNearestMapFeature(WorldServer worldserver, StructureGenerator<?> structuregenerator, BlockPosition blockposition, int i, boolean flag) {
        if (structuregenerator == StructureGenerator.STRONGHOLD) {
            this.generateStrongholds();
            BlockPosition blockposition1 = null;
            double d0 = Double.MAX_VALUE;
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
            Iterator iterator = this.strongholdPositions.iterator();

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
        } else {
            StructureSettingsFeature structuresettingsfeature = this.settings.getConfig(structuregenerator);
            ImmutableMultimap<StructureFeature<?, ?>, ResourceKey<BiomeBase>> immutablemultimap = this.settings.structures(structuregenerator);

            if (structuresettingsfeature != null && !immutablemultimap.isEmpty()) {
                IRegistry<BiomeBase> iregistry = worldserver.registryAccess().registryOrThrow(IRegistry.BIOME_REGISTRY);
                Set<ResourceKey<BiomeBase>> set = (Set) this.runtimeBiomeSource.possibleBiomes().stream().flatMap((biomebase) -> {
                    return iregistry.getResourceKey(biomebase).stream();
                }).collect(Collectors.toSet());
                Stream stream = immutablemultimap.values().stream();

                Objects.requireNonNull(set);
                return stream.noneMatch(set::contains) ? null : structuregenerator.getNearestGeneratedFeature(worldserver, worldserver.structureFeatureManager(), blockposition, i, flag, worldserver.getSeed(), structuresettingsfeature);
            } else {
                return null;
            }
        }
    }

    public void applyBiomeDecoration(GeneratorAccessSeed generatoraccessseed, IChunkAccess ichunkaccess, StructureManager structuremanager) {
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();

        if (!SharedConstants.debugVoidTerrain(chunkcoordintpair)) {
            SectionPosition sectionposition = SectionPosition.of(chunkcoordintpair, generatoraccessseed.getMinSection());
            BlockPosition blockposition = sectionposition.origin();
            Map<Integer, List<StructureGenerator<?>>> map = (Map) IRegistry.STRUCTURE_FEATURE.stream().collect(Collectors.groupingBy((structuregenerator) -> {
                return structuregenerator.step().ordinal();
            }));
            List<WorldChunkManager.b> list = this.biomeSource.featuresPerStep();
            SeededRandom seededrandom = new SeededRandom(new XoroshiroRandomSource(RandomSupport.seedUniquifier()));
            long i = seededrandom.setDecorationSeed(generatoraccessseed.getSeed(), blockposition.getX(), blockposition.getZ());
            Set<BiomeBase> set = new ObjectArraySet();

            if (this instanceof ChunkProviderFlat) {
                set.addAll(this.biomeSource.possibleBiomes());
            } else {
                ChunkCoordIntPair.rangeClosed(sectionposition.chunk(), 1).forEach((chunkcoordintpair1) -> {
                    IChunkAccess ichunkaccess1 = generatoraccessseed.getChunk(chunkcoordintpair1.x, chunkcoordintpair1.z);
                    ChunkSection[] achunksection = ichunkaccess1.getSections();
                    int j = achunksection.length;

                    for (int k = 0; k < j; ++k) {
                        ChunkSection chunksection = achunksection[k];
                        DataPaletteBlock datapaletteblock = chunksection.getBiomes();

                        Objects.requireNonNull(set);
                        datapaletteblock.getAll(set::add);
                    }

                });
                set.retainAll(this.biomeSource.possibleBiomes());
            }

            int j = list.size();

            try {
                IRegistry<PlacedFeature> iregistry = generatoraccessseed.registryAccess().registryOrThrow(IRegistry.PLACED_FEATURE_REGISTRY);
                IRegistry<StructureGenerator<?>> iregistry1 = generatoraccessseed.registryAccess().registryOrThrow(IRegistry.STRUCTURE_FEATURE_REGISTRY);
                int k = Math.max(WorldGenStage.Decoration.values().length, j);

                for (int l = 0; l < k; ++l) {
                    int i1 = 0;
                    Iterator iterator;
                    CrashReportSystemDetails crashreportsystemdetails;

                    if (structuremanager.shouldGenerateFeatures()) {
                        List<StructureGenerator<?>> list1 = (List) map.getOrDefault(l, Collections.emptyList());

                        for (iterator = list1.iterator(); iterator.hasNext(); ++i1) {
                            StructureGenerator<?> structuregenerator = (StructureGenerator) iterator.next();

                            seededrandom.setFeatureSeed(i, i1, l);
                            Supplier supplier = () -> {
                                Optional optional = iregistry1.getResourceKey(structuregenerator).map(Object::toString);

                                Objects.requireNonNull(structuregenerator);
                                return (String) optional.orElseGet(structuregenerator::toString);
                            };

                            try {
                                generatoraccessseed.setCurrentlyGenerating(supplier);
                                structuremanager.startsForFeature(sectionposition, structuregenerator).forEach((structurestart) -> {
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
                            List<List<Supplier<PlacedFeature>>> list2 = biomebase.getGenerationSettings().features();

                            if (l < list2.size()) {
                                List<Supplier<PlacedFeature>> list3 = (List) list2.get(l);
                                WorldChunkManager.b worldchunkmanager_b = (WorldChunkManager.b) list.get(l);

                                list3.stream().map(Supplier::get).forEach((placedfeature) -> {
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
                                Optional optional = iregistry.getResourceKey(placedfeature).map(Object::toString);

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

    public StructureSettings getSettings() {
        return this.settings;
    }

    public int getSpawnHeight(LevelHeightAccessor levelheightaccessor) {
        return 64;
    }

    public WorldChunkManager getBiomeSource() {
        return this.runtimeBiomeSource;
    }

    public abstract int getGenDepth();

    public WeightedRandomList<BiomeSettingsMobs.c> getMobsAt(BiomeBase biomebase, StructureManager structuremanager, EnumCreatureType enumcreaturetype, BlockPosition blockposition) {
        return biomebase.getMobSettings().getMobs(enumcreaturetype);
    }

    public void createStructures(IRegistryCustom iregistrycustom, StructureManager structuremanager, IChunkAccess ichunkaccess, DefinedStructureManager definedstructuremanager, long i) {
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        SectionPosition sectionposition = SectionPosition.bottomOf(ichunkaccess);
        StructureSettingsFeature structuresettingsfeature = this.settings.getConfig(StructureGenerator.STRONGHOLD);

        if (structuresettingsfeature != null) {
            StructureStart<?> structurestart = structuremanager.getStartForFeature(sectionposition, StructureGenerator.STRONGHOLD, ichunkaccess);

            if (structurestart == null || !structurestart.isValid()) {
                StructureStart<?> structurestart1 = StructureFeatures.STRONGHOLD.generate(iregistrycustom, this, this.biomeSource, definedstructuremanager, i, chunkcoordintpair, fetchReferences(structuremanager, ichunkaccess, sectionposition, StructureGenerator.STRONGHOLD), structuresettingsfeature, ichunkaccess, ChunkGenerator::validStrongholdBiome);

                structuremanager.setStartForFeature(sectionposition, StructureGenerator.STRONGHOLD, structurestart1, ichunkaccess);
            }
        }

        IRegistry<BiomeBase> iregistry = iregistrycustom.registryOrThrow(IRegistry.BIOME_REGISTRY);
        Iterator iterator = IRegistry.STRUCTURE_FEATURE.iterator();

        label37:
        while (iterator.hasNext()) {
            StructureGenerator<?> structuregenerator = (StructureGenerator) iterator.next();

            if (structuregenerator != StructureGenerator.STRONGHOLD) {
                StructureSettingsFeature structuresettingsfeature1 = this.settings.getConfig(structuregenerator);

                if (structuresettingsfeature1 != null) {
                    StructureStart<?> structurestart2 = structuremanager.getStartForFeature(sectionposition, structuregenerator, ichunkaccess);

                    if (structurestart2 == null || !structurestart2.isValid()) {
                        int j = fetchReferences(structuremanager, ichunkaccess, sectionposition, structuregenerator);
                        UnmodifiableIterator unmodifiableiterator = this.settings.structures(structuregenerator).asMap().entrySet().iterator();

                        StructureStart structurestart3;

                        do {
                            if (!unmodifiableiterator.hasNext()) {
                                structuremanager.setStartForFeature(sectionposition, structuregenerator, StructureStart.INVALID_START, ichunkaccess);
                                continue label37;
                            }

                            Entry<StructureFeature<?, ?>, Collection<ResourceKey<BiomeBase>>> entry = (Entry) unmodifiableiterator.next();

                            structurestart3 = ((StructureFeature) entry.getKey()).generate(iregistrycustom, this, this.biomeSource, definedstructuremanager, i, chunkcoordintpair, j, structuresettingsfeature1, ichunkaccess, (biomebase) -> {
                                Collection collection = (Collection) entry.getValue();

                                Objects.requireNonNull(collection);
                                return this.validBiome(iregistry, collection::contains, biomebase);
                            });
                        } while (!structurestart3.isValid());

                        structuremanager.setStartForFeature(sectionposition, structuregenerator, structurestart3, ichunkaccess);
                    }
                }
            }
        }

    }

    private static int fetchReferences(StructureManager structuremanager, IChunkAccess ichunkaccess, SectionPosition sectionposition, StructureGenerator<?> structuregenerator) {
        StructureStart<?> structurestart = structuremanager.getStartForFeature(sectionposition, structuregenerator, ichunkaccess);

        return structurestart != null ? structurestart.getReferences() : 0;
    }

    protected boolean validBiome(IRegistry<BiomeBase> iregistry, Predicate<ResourceKey<BiomeBase>> predicate, BiomeBase biomebase) {
        return iregistry.getResourceKey(biomebase).filter(predicate).isPresent();
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

                        crashreportsystemdetails.setDetail("Id", () -> {
                            return IRegistry.STRUCTURE_FEATURE.getKey(structurestart.getFeature()).toString();
                        });
                        crashreportsystemdetails.setDetail("Name", () -> {
                            return structurestart.getFeature().getFeatureName();
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

    public boolean hasStronghold(ChunkCoordIntPair chunkcoordintpair) {
        this.generateStrongholds();
        return this.strongholdPositions.contains(chunkcoordintpair);
    }

    static {
        IRegistry.register(IRegistry.CHUNK_GENERATOR, "noise", ChunkGeneratorAbstract.CODEC);
        IRegistry.register(IRegistry.CHUNK_GENERATOR, "flat", ChunkProviderFlat.CODEC);
        IRegistry.register(IRegistry.CHUNK_GENERATOR, "debug", ChunkProviderDebug.CODEC);
        CODEC = IRegistry.CHUNK_GENERATOR.byNameCodec().dispatchStable(ChunkGenerator::codec, Function.identity());
    }
}
