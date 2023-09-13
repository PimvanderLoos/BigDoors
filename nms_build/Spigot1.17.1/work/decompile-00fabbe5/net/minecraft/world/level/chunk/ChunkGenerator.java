package net.minecraft.world.level.chunk;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportSystemDetails;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPosition;
import net.minecraft.data.worldgen.StructureFeatures;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.server.level.RegionLimitedWorldAccess;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.level.BlockColumn;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSettingsGeneration;
import net.minecraft.world.level.biome.BiomeSettingsMobs;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.BaseStoneSource;
import net.minecraft.world.level.levelgen.ChunkGeneratorAbstract;
import net.minecraft.world.level.levelgen.ChunkProviderDebug;
import net.minecraft.world.level.levelgen.ChunkProviderFlat;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.SingleBaseStoneSource;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.WorldGenStage;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldGenCarverWrapper;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.StructureSettingsFeature;
import net.minecraft.world.level.levelgen.feature.configurations.StructureSettingsStronghold;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public abstract class ChunkGenerator {

    public static final Codec<ChunkGenerator> CODEC;
    protected final WorldChunkManager biomeSource;
    protected final WorldChunkManager runtimeBiomeSource;
    private final StructureSettings settings;
    private final long strongholdSeed;
    private final List<ChunkCoordIntPair> strongholdPositions;
    private final BaseStoneSource defaultBaseStoneSource;

    public ChunkGenerator(WorldChunkManager worldchunkmanager, StructureSettings structuresettings) {
        this(worldchunkmanager, worldchunkmanager, structuresettings, 0L);
    }

    public ChunkGenerator(WorldChunkManager worldchunkmanager, WorldChunkManager worldchunkmanager1, StructureSettings structuresettings, long i) {
        this.strongholdPositions = Lists.newArrayList();
        this.biomeSource = worldchunkmanager;
        this.runtimeBiomeSource = worldchunkmanager1;
        this.settings = structuresettings;
        this.strongholdSeed = i;
        this.defaultBaseStoneSource = new SingleBaseStoneSource(Blocks.STONE.getBlockData());
    }

    private void h() {
        if (this.strongholdPositions.isEmpty()) {
            StructureSettingsStronghold structuresettingsstronghold = this.settings.b();

            if (structuresettingsstronghold != null && structuresettingsstronghold.c() != 0) {
                List<BiomeBase> list = Lists.newArrayList();
                Iterator iterator = this.biomeSource.b().iterator();

                while (iterator.hasNext()) {
                    BiomeBase biomebase = (BiomeBase) iterator.next();

                    if (biomebase.e().a(StructureGenerator.STRONGHOLD)) {
                        list.add(biomebase);
                    }
                }

                int i = structuresettingsstronghold.a();
                int j = structuresettingsstronghold.c();
                int k = structuresettingsstronghold.b();
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
                    int i2 = SectionPosition.a(k1, 8);
                    int j2 = SectionPosition.a(l1, 8);

                    Objects.requireNonNull(list);
                    BlockPosition blockposition = worldchunkmanager.a(i2, 0, j2, 112, list::contains, random);

                    if (blockposition != null) {
                        k1 = SectionPosition.a(blockposition.getX());
                        l1 = SectionPosition.a(blockposition.getZ());
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

    protected abstract Codec<? extends ChunkGenerator> a();

    public abstract ChunkGenerator withSeed(long i);

    public void createBiomes(IRegistry<BiomeBase> iregistry, IChunkAccess ichunkaccess) {
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();

        ((ProtoChunk) ichunkaccess).a(new BiomeStorage(iregistry, ichunkaccess, chunkcoordintpair, this.runtimeBiomeSource));
    }

    public void doCarving(long i, BiomeManager biomemanager, IChunkAccess ichunkaccess, WorldGenStage.Features worldgenstage_features) {
        BiomeManager biomemanager1 = biomemanager.a(this.biomeSource);
        SeededRandom seededrandom = new SeededRandom();
        boolean flag = true;
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        CarvingContext carvingcontext = new CarvingContext(this, ichunkaccess);
        Aquifer aquifer = this.a(ichunkaccess);
        BitSet bitset = ((ProtoChunk) ichunkaccess).b(worldgenstage_features);

        for (int j = -8; j <= 8; ++j) {
            for (int k = -8; k <= 8; ++k) {
                ChunkCoordIntPair chunkcoordintpair1 = new ChunkCoordIntPair(chunkcoordintpair.x + j, chunkcoordintpair.z + k);
                BiomeSettingsGeneration biomesettingsgeneration = this.biomeSource.getBiome(QuartPos.a(chunkcoordintpair1.d()), 0, QuartPos.a(chunkcoordintpair1.e())).e();
                List<Supplier<WorldGenCarverWrapper<?>>> list = biomesettingsgeneration.a(worldgenstage_features);
                ListIterator listiterator = list.listIterator();

                while (listiterator.hasNext()) {
                    int l = listiterator.nextIndex();
                    WorldGenCarverWrapper<?> worldgencarverwrapper = (WorldGenCarverWrapper) ((Supplier) listiterator.next()).get();

                    seededrandom.c(i + (long) l, chunkcoordintpair1.x, chunkcoordintpair1.z);
                    if (worldgencarverwrapper.a((Random) seededrandom)) {
                        Objects.requireNonNull(biomemanager1);
                        worldgencarverwrapper.a(carvingcontext, ichunkaccess, biomemanager1::a, seededrandom, aquifer, chunkcoordintpair1, bitset);
                    }
                }
            }
        }

    }

    protected Aquifer a(IChunkAccess ichunkaccess) {
        return Aquifer.a(this.getSeaLevel(), Blocks.WATER.getBlockData());
    }

    @Nullable
    public BlockPosition findNearestMapFeature(WorldServer worldserver, StructureGenerator<?> structuregenerator, BlockPosition blockposition, int i, boolean flag) {
        if (!this.biomeSource.a(structuregenerator)) {
            return null;
        } else if (structuregenerator == StructureGenerator.STRONGHOLD) {
            this.h();
            BlockPosition blockposition1 = null;
            double d0 = Double.MAX_VALUE;
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
            Iterator iterator = this.strongholdPositions.iterator();

            while (iterator.hasNext()) {
                ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair) iterator.next();

                blockposition_mutableblockposition.d(SectionPosition.a(chunkcoordintpair.x, 8), 32, SectionPosition.a(chunkcoordintpair.z, 8));
                double d1 = blockposition_mutableblockposition.j(blockposition);

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
            StructureSettingsFeature structuresettingsfeature = this.settings.a(structuregenerator);

            return structuresettingsfeature == null ? null : structuregenerator.getNearestGeneratedFeature(worldserver, worldserver.getStructureManager(), blockposition, i, flag, worldserver.getSeed(), structuresettingsfeature);
        }
    }

    public void addDecorations(RegionLimitedWorldAccess regionlimitedworldaccess, StructureManager structuremanager) {
        ChunkCoordIntPair chunkcoordintpair = regionlimitedworldaccess.a();
        int i = chunkcoordintpair.d();
        int j = chunkcoordintpair.e();
        BlockPosition blockposition = new BlockPosition(i, regionlimitedworldaccess.getMinBuildHeight(), j);
        BiomeBase biomebase = this.biomeSource.b(chunkcoordintpair);
        SeededRandom seededrandom = new SeededRandom();
        long k = seededrandom.a(regionlimitedworldaccess.getSeed(), i, j);

        try {
            biomebase.a(structuremanager, this, regionlimitedworldaccess, k, seededrandom, blockposition);
        } catch (Exception exception) {
            CrashReport crashreport = CrashReport.a(exception, "Biome decoration");

            crashreport.a("Generation").a("CenterX", (Object) chunkcoordintpair.x).a("CenterZ", (Object) chunkcoordintpair.z).a("Seed", (Object) k).a("Biome", (Object) biomebase);
            throw new ReportedException(crashreport);
        }
    }

    public abstract void buildBase(RegionLimitedWorldAccess regionlimitedworldaccess, IChunkAccess ichunkaccess);

    public void addMobs(RegionLimitedWorldAccess regionlimitedworldaccess) {}

    public StructureSettings getSettings() {
        return this.settings;
    }

    public int getSpawnHeight(LevelHeightAccessor levelheightaccessor) {
        return 64;
    }

    public WorldChunkManager getWorldChunkManager() {
        return this.runtimeBiomeSource;
    }

    public int getGenerationDepth() {
        return 256;
    }

    public WeightedRandomList<BiomeSettingsMobs.c> getMobsFor(BiomeBase biomebase, StructureManager structuremanager, EnumCreatureType enumcreaturetype, BlockPosition blockposition) {
        return biomebase.b().a(enumcreaturetype);
    }

    public void createStructures(IRegistryCustom iregistrycustom, StructureManager structuremanager, IChunkAccess ichunkaccess, DefinedStructureManager definedstructuremanager, long i) {
        BiomeBase biomebase = this.biomeSource.b(ichunkaccess.getPos());

        this.a(StructureFeatures.STRONGHOLD, iregistrycustom, structuremanager, ichunkaccess, definedstructuremanager, i, biomebase);
        Iterator iterator = biomebase.e().a().iterator();

        while (iterator.hasNext()) {
            Supplier<StructureFeature<?, ?>> supplier = (Supplier) iterator.next();

            this.a((StructureFeature) supplier.get(), iregistrycustom, structuremanager, ichunkaccess, definedstructuremanager, i, biomebase);
        }

    }

    private void a(StructureFeature<?, ?> structurefeature, IRegistryCustom iregistrycustom, StructureManager structuremanager, IChunkAccess ichunkaccess, DefinedStructureManager definedstructuremanager, long i, BiomeBase biomebase) {
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        SectionPosition sectionposition = SectionPosition.a(ichunkaccess);
        StructureStart<?> structurestart = structuremanager.a(sectionposition, structurefeature.feature, ichunkaccess);
        int j = structurestart != null ? structurestart.i() : 0;
        StructureSettingsFeature structuresettingsfeature = this.settings.a(structurefeature.feature);

        if (structuresettingsfeature != null) {
            StructureStart<?> structurestart1 = structurefeature.a(iregistrycustom, this, this.biomeSource, definedstructuremanager, i, chunkcoordintpair, biomebase, j, structuresettingsfeature, ichunkaccess);

            structuremanager.a(sectionposition, structurefeature.feature, structurestart1, ichunkaccess);
        }

    }

    public void storeStructures(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, IChunkAccess ichunkaccess) {
        boolean flag = true;
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        int i = chunkcoordintpair.x;
        int j = chunkcoordintpair.z;
        int k = chunkcoordintpair.d();
        int l = chunkcoordintpair.e();
        SectionPosition sectionposition = SectionPosition.a(ichunkaccess);

        for (int i1 = i - 8; i1 <= i + 8; ++i1) {
            for (int j1 = j - 8; j1 <= j + 8; ++j1) {
                long k1 = ChunkCoordIntPair.pair(i1, j1);
                Iterator iterator = generatoraccessseed.getChunkAt(i1, j1).g().values().iterator();

                while (iterator.hasNext()) {
                    StructureStart structurestart = (StructureStart) iterator.next();

                    try {
                        if (structurestart.e() && structurestart.c().a(k, l, k + 15, l + 15)) {
                            structuremanager.a(sectionposition, structurestart.k(), k1, ichunkaccess);
                            PacketDebug.a(generatoraccessseed, structurestart);
                        }
                    } catch (Exception exception) {
                        CrashReport crashreport = CrashReport.a(exception, "Generating structure reference");
                        CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Structure");

                        crashreportsystemdetails.a("Id", () -> {
                            return IRegistry.STRUCTURE_FEATURE.getKey(structurestart.k()).toString();
                        });
                        crashreportsystemdetails.a("Name", () -> {
                            return structurestart.k().g();
                        });
                        crashreportsystemdetails.a("Class", () -> {
                            return structurestart.k().getClass().getCanonicalName();
                        });
                        throw new ReportedException(crashreport);
                    }
                }
            }
        }

    }

    public abstract CompletableFuture<IChunkAccess> buildNoise(Executor executor, StructureManager structuremanager, IChunkAccess ichunkaccess);

    public int getSeaLevel() {
        return 63;
    }

    public int getMinY() {
        return 0;
    }

    public abstract int getBaseHeight(int i, int j, HeightMap.Type heightmap_type, LevelHeightAccessor levelheightaccessor);

    public abstract BlockColumn getBaseColumn(int i, int j, LevelHeightAccessor levelheightaccessor);

    public int b(int i, int j, HeightMap.Type heightmap_type, LevelHeightAccessor levelheightaccessor) {
        return this.getBaseHeight(i, j, heightmap_type, levelheightaccessor);
    }

    public int c(int i, int j, HeightMap.Type heightmap_type, LevelHeightAccessor levelheightaccessor) {
        return this.getBaseHeight(i, j, heightmap_type, levelheightaccessor) - 1;
    }

    public boolean a(ChunkCoordIntPair chunkcoordintpair) {
        this.h();
        return this.strongholdPositions.contains(chunkcoordintpair);
    }

    public BaseStoneSource g() {
        return this.defaultBaseStoneSource;
    }

    static {
        IRegistry.a(IRegistry.CHUNK_GENERATOR, "noise", (Object) ChunkGeneratorAbstract.CODEC);
        IRegistry.a(IRegistry.CHUNK_GENERATOR, "flat", (Object) ChunkProviderFlat.CODEC);
        IRegistry.a(IRegistry.CHUNK_GENERATOR, "debug", (Object) ChunkProviderDebug.CODEC);
        CODEC = IRegistry.CHUNK_GENERATOR.dispatchStable(ChunkGenerator::a, Function.identity());
    }
}
