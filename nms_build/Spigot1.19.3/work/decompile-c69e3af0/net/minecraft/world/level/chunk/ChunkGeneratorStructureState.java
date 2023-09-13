package net.minecraft.world.level.chunk;

import com.google.common.base.Stopwatch;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.SectionPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.slf4j.Logger;

public class ChunkGeneratorStructureState {

    private static final Logger LOGGER = LogUtils.getLogger();
    private final RandomState randomState;
    private final WorldChunkManager biomeSource;
    private final long levelSeed;
    private final long concentricRingsSeed;
    private final Map<Structure, List<StructurePlacement>> placementsForStructure = new Object2ObjectOpenHashMap();
    private final Map<ConcentricRingsStructurePlacement, CompletableFuture<List<ChunkCoordIntPair>>> ringPositions = new Object2ObjectArrayMap();
    private boolean hasGeneratedPositions;
    private final List<Holder<StructureSet>> possibleStructureSets;

    public static ChunkGeneratorStructureState createForFlat(RandomState randomstate, long i, WorldChunkManager worldchunkmanager, Stream<Holder<StructureSet>> stream) {
        List<Holder<StructureSet>> list = stream.filter((holder) -> {
            return hasBiomesForStructureSet((StructureSet) holder.value(), worldchunkmanager);
        }).toList();

        return new ChunkGeneratorStructureState(randomstate, worldchunkmanager, i, 0L, list);
    }

    public static ChunkGeneratorStructureState createForNormal(RandomState randomstate, long i, WorldChunkManager worldchunkmanager, HolderLookup<StructureSet> holderlookup) {
        List<Holder<StructureSet>> list = (List) holderlookup.listElements().filter((holder_c) -> {
            return hasBiomesForStructureSet((StructureSet) holder_c.value(), worldchunkmanager);
        }).collect(Collectors.toUnmodifiableList());

        return new ChunkGeneratorStructureState(randomstate, worldchunkmanager, i, i, list);
    }

    private static boolean hasBiomesForStructureSet(StructureSet structureset, WorldChunkManager worldchunkmanager) {
        Stream<Holder<BiomeBase>> stream = structureset.structures().stream().flatMap((structureset_a) -> {
            Structure structure = (Structure) structureset_a.structure().value();

            return structure.biomes().stream();
        });
        Set set = worldchunkmanager.possibleBiomes();

        Objects.requireNonNull(set);
        return stream.anyMatch(set::contains);
    }

    private ChunkGeneratorStructureState(RandomState randomstate, WorldChunkManager worldchunkmanager, long i, long j, List<Holder<StructureSet>> list) {
        this.randomState = randomstate;
        this.levelSeed = i;
        this.biomeSource = worldchunkmanager;
        this.concentricRingsSeed = j;
        this.possibleStructureSets = list;
    }

    public List<Holder<StructureSet>> possibleStructureSets() {
        return this.possibleStructureSets;
    }

    private void generatePositions() {
        Set<Holder<BiomeBase>> set = this.biomeSource.possibleBiomes();

        this.possibleStructureSets().forEach((holder) -> {
            StructureSet structureset = (StructureSet) holder.value();
            boolean flag = false;
            Iterator iterator = structureset.structures().iterator();

            while (iterator.hasNext()) {
                StructureSet.a structureset_a = (StructureSet.a) iterator.next();
                Structure structure = (Structure) structureset_a.structure().value();
                Stream stream = structure.biomes().stream();

                Objects.requireNonNull(set);
                if (stream.anyMatch(set::contains)) {
                    ((List) this.placementsForStructure.computeIfAbsent(structure, (structure1) -> {
                        return new ArrayList();
                    })).add(structureset.placement());
                    flag = true;
                }
            }

            if (flag) {
                StructurePlacement structureplacement = structureset.placement();

                if (structureplacement instanceof ConcentricRingsStructurePlacement) {
                    ConcentricRingsStructurePlacement concentricringsstructureplacement = (ConcentricRingsStructurePlacement) structureplacement;

                    this.ringPositions.put(concentricringsstructureplacement, this.generateRingPositions(holder, concentricringsstructureplacement));
                }
            }

        });
    }

    private CompletableFuture<List<ChunkCoordIntPair>> generateRingPositions(Holder<StructureSet> holder, ConcentricRingsStructurePlacement concentricringsstructureplacement) {
        if (concentricringsstructureplacement.count() == 0) {
            return CompletableFuture.completedFuture(List.of());
        } else {
            Stopwatch stopwatch = Stopwatch.createStarted(SystemUtils.TICKER);
            int i = concentricringsstructureplacement.distance();
            int j = concentricringsstructureplacement.count();
            List<CompletableFuture<ChunkCoordIntPair>> list = new ArrayList(j);
            int k = concentricringsstructureplacement.spread();
            HolderSet<BiomeBase> holderset = concentricringsstructureplacement.preferredBiomes();
            RandomSource randomsource = RandomSource.create();

            randomsource.setSeed(this.concentricRingsSeed);
            double d0 = randomsource.nextDouble() * 3.141592653589793D * 2.0D;
            int l = 0;
            int i1 = 0;

            for (int j1 = 0; j1 < j; ++j1) {
                double d1 = (double) (4 * i + i * i1 * 6) + (randomsource.nextDouble() - 0.5D) * (double) i * 2.5D;
                int k1 = (int) Math.round(Math.cos(d0) * d1);
                int l1 = (int) Math.round(Math.sin(d0) * d1);
                RandomSource randomsource1 = randomsource.fork();

                list.add(CompletableFuture.supplyAsync(() -> {
                    WorldChunkManager worldchunkmanager = this.biomeSource;
                    int i2 = SectionPosition.sectionToBlockCoord(k1, 8);
                    int j2 = SectionPosition.sectionToBlockCoord(l1, 8);

                    Objects.requireNonNull(holderset);
                    Pair<BlockPosition, Holder<BiomeBase>> pair = worldchunkmanager.findBiomeHorizontal(i2, 0, j2, 112, holderset::contains, randomsource1, this.randomState.sampler());

                    if (pair != null) {
                        BlockPosition blockposition = (BlockPosition) pair.getFirst();

                        return new ChunkCoordIntPair(SectionPosition.blockToSectionCoord(blockposition.getX()), SectionPosition.blockToSectionCoord(blockposition.getZ()));
                    } else {
                        return new ChunkCoordIntPair(k1, l1);
                    }
                }, SystemUtils.backgroundExecutor()));
                d0 += 6.283185307179586D / (double) k;
                ++l;
                if (l == k) {
                    ++i1;
                    l = 0;
                    k += 2 * k / (i1 + 1);
                    k = Math.min(k, j - j1);
                    d0 += randomsource.nextDouble() * 3.141592653589793D * 2.0D;
                }
            }

            return SystemUtils.sequence(list).thenApply((list1) -> {
                double d2 = (double) stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) / 1000.0D;

                ChunkGeneratorStructureState.LOGGER.debug("Calculation for {} took {}s", holder, d2);
                return list1;
            });
        }
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

    public List<StructurePlacement> getPlacementsForStructure(Holder<Structure> holder) {
        this.ensureStructuresGenerated();
        return (List) this.placementsForStructure.getOrDefault(holder.value(), List.of());
    }

    public RandomState randomState() {
        return this.randomState;
    }

    public boolean hasStructureChunkInRange(Holder<StructureSet> holder, int i, int j, int k) {
        StructurePlacement structureplacement = ((StructureSet) holder.value()).placement();

        for (int l = i - k; l <= i + k; ++l) {
            for (int i1 = j - k; i1 <= j + k; ++i1) {
                if (structureplacement.isStructureChunk(this, l, i1)) {
                    return true;
                }
            }
        }

        return false;
    }

    public long getLevelSeed() {
        return this.levelSeed;
    }
}
