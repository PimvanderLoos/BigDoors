package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkProviderFlat extends ChunkGeneratorAbstract<GeneratorSettingsFlat> {

    private static final Logger f = LogManager.getLogger();
    private final GeneratorSettingsFlat g;
    private final BiomeBase h;
    private final MobSpawnerPhantom i = new MobSpawnerPhantom();

    public ChunkProviderFlat(GeneratorAccess generatoraccess, WorldChunkManager worldchunkmanager, GeneratorSettingsFlat generatorsettingsflat) {
        super(generatoraccess, worldchunkmanager);
        this.g = generatorsettingsflat;
        this.h = this.g();
    }

    private BiomeBase g() {
        BiomeBase biomebase = this.g.t();
        ChunkProviderFlat.a chunkproviderflat_a = new ChunkProviderFlat.a(biomebase.q(), biomebase.c(), biomebase.p(), biomebase.h(), biomebase.l(), biomebase.getTemperature(), biomebase.getHumidity(), biomebase.n(), biomebase.o(), biomebase.s());
        Map<String, Map<String, String>> map = this.g.u();
        Iterator iterator = map.keySet().iterator();

        int i;

        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            WorldGenFeatureComposite<?, ?>[] aworldgenfeaturecomposite = (WorldGenFeatureComposite[]) GeneratorSettingsFlat.u.get(s);

            if (aworldgenfeaturecomposite != null) {
                WorldGenFeatureComposite[] aworldgenfeaturecomposite1 = aworldgenfeaturecomposite;

                i = aworldgenfeaturecomposite.length;

                for (int j = 0; j < i; ++j) {
                    WorldGenFeatureComposite<?, ?> worldgenfeaturecomposite = aworldgenfeaturecomposite1[j];

                    chunkproviderflat_a.a((WorldGenStage.Decoration) GeneratorSettingsFlat.t.get(worldgenfeaturecomposite), worldgenfeaturecomposite);
                    WorldGenerator<?> worldgenerator = worldgenfeaturecomposite.a();

                    if (worldgenerator instanceof StructureGenerator) {
                        WorldGenFeatureConfiguration worldgenfeatureconfiguration = biomebase.b((StructureGenerator) worldgenerator);

                        chunkproviderflat_a.a((StructureGenerator) worldgenerator, worldgenfeatureconfiguration != null ? worldgenfeatureconfiguration : (WorldGenFeatureConfiguration) GeneratorSettingsFlat.v.get(worldgenfeaturecomposite));
                    }
                }
            }
        }

        boolean flag = (!this.g.y() || biomebase == Biomes.THE_VOID) && map.containsKey("decoration");

        if (flag) {
            List<WorldGenStage.Decoration> list = Lists.newArrayList();

            list.add(WorldGenStage.Decoration.UNDERGROUND_STRUCTURES);
            list.add(WorldGenStage.Decoration.SURFACE_STRUCTURES);
            WorldGenStage.Decoration[] aworldgenstage_decoration = WorldGenStage.Decoration.values();
            int k = aworldgenstage_decoration.length;

            for (i = 0; i < k; ++i) {
                WorldGenStage.Decoration worldgenstage_decoration = aworldgenstage_decoration[i];

                if (!list.contains(worldgenstage_decoration)) {
                    Iterator iterator1 = biomebase.a(worldgenstage_decoration).iterator();

                    while (iterator1.hasNext()) {
                        WorldGenFeatureComposite<?, ?> worldgenfeaturecomposite1 = (WorldGenFeatureComposite) iterator1.next();

                        chunkproviderflat_a.a(worldgenstage_decoration, worldgenfeaturecomposite1);
                    }
                }
            }
        }

        return chunkproviderflat_a;
    }

    public void createChunk(IChunkAccess ichunkaccess) {
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        int i = chunkcoordintpair.x;
        int j = chunkcoordintpair.z;
        BiomeBase[] abiomebase = this.c.getBiomeBlock(i * 16, j * 16, 16, 16);

        ichunkaccess.a(abiomebase);
        this.a(i, j, ichunkaccess);
        ichunkaccess.a(HeightMap.Type.WORLD_SURFACE_WG, HeightMap.Type.OCEAN_FLOOR_WG);
        ichunkaccess.a(ChunkStatus.BASE);
    }

    public void addFeatures(RegionLimitedWorldAccess regionlimitedworldaccess, WorldGenStage.Features worldgenstage_features) {
        boolean flag = true;
        int i = regionlimitedworldaccess.a();
        int j = regionlimitedworldaccess.b();
        BitSet bitset = new BitSet(65536);
        SeededRandom seededrandom = new SeededRandom();

        for (int k = i - 8; k <= i + 8; ++k) {
            for (int l = j - 8; l <= j + 8; ++l) {
                List<WorldGenCarverWrapper<?>> list = this.h.a(WorldGenStage.Features.AIR);
                ListIterator listiterator = list.listIterator();

                while (listiterator.hasNext()) {
                    int i1 = listiterator.nextIndex();
                    WorldGenCarverWrapper<?> worldgencarverwrapper = (WorldGenCarverWrapper) listiterator.next();

                    seededrandom.c(regionlimitedworldaccess.getMinecraftWorld().getSeed() + (long) i1, k, l);
                    if (worldgencarverwrapper.a(regionlimitedworldaccess, seededrandom, k, l, WorldGenFeatureConfiguration.e)) {
                        worldgencarverwrapper.a(regionlimitedworldaccess, seededrandom, k, l, i, j, bitset, WorldGenFeatureConfiguration.e);
                    }
                }
            }
        }

    }

    public GeneratorSettingsFlat getSettings() {
        return this.g;
    }

    public double[] a(int i, int j) {
        return new double[0];
    }

    public int getSpawnHeight() {
        IChunkAccess ichunkaccess = this.a.getChunkAt(0, 0);

        return ichunkaccess.a(HeightMap.Type.MOTION_BLOCKING, 8, 8);
    }

    public void addDecorations(RegionLimitedWorldAccess regionlimitedworldaccess) {
        int i = regionlimitedworldaccess.a();
        int j = regionlimitedworldaccess.b();
        int k = i * 16;
        int l = j * 16;
        BlockPosition blockposition = new BlockPosition(k, 0, l);
        SeededRandom seededrandom = new SeededRandom();
        long i1 = seededrandom.a(regionlimitedworldaccess.getSeed(), k, l);
        WorldGenStage.Decoration[] aworldgenstage_decoration = WorldGenStage.Decoration.values();
        int j1 = aworldgenstage_decoration.length;

        for (int k1 = 0; k1 < j1; ++k1) {
            WorldGenStage.Decoration worldgenstage_decoration = aworldgenstage_decoration[k1];

            this.h.a(worldgenstage_decoration, this, regionlimitedworldaccess, i1, seededrandom, blockposition);
        }

    }

    public void addMobs(RegionLimitedWorldAccess regionlimitedworldaccess) {}

    public void a(int i, int j, IChunkAccess ichunkaccess) {
        IBlockData[] aiblockdata = this.g.A();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int k = 0; k < aiblockdata.length; ++k) {
            IBlockData iblockdata = aiblockdata[k];

            if (iblockdata != null) {
                for (int l = 0; l < 16; ++l) {
                    for (int i1 = 0; i1 < 16; ++i1) {
                        ichunkaccess.setType(blockposition_mutableblockposition.c(l, k, i1), iblockdata, false);
                    }
                }
            }
        }

    }

    public List<BiomeBase.BiomeMeta> getMobsFor(EnumCreatureType enumcreaturetype, BlockPosition blockposition) {
        BiomeBase biomebase = this.a.getBiome(blockposition);

        return biomebase.getMobs(enumcreaturetype);
    }

    public int a(World world, boolean flag, boolean flag1) {
        byte b0 = 0;
        int i = b0 + this.i.a(world, flag, flag1);

        return i;
    }

    public boolean canSpawnStructure(BiomeBase biomebase, StructureGenerator<? extends WorldGenFeatureConfiguration> structuregenerator) {
        return this.h.a(structuregenerator);
    }

    @Nullable
    public WorldGenFeatureConfiguration getFeatureConfiguration(BiomeBase biomebase, StructureGenerator<? extends WorldGenFeatureConfiguration> structuregenerator) {
        return this.h.b(structuregenerator);
    }

    @Nullable
    public BlockPosition findNearestMapFeature(World world, String s, BlockPosition blockposition, int i, boolean flag) {
        return !this.g.u().keySet().contains(s) ? null : super.findNearestMapFeature(world, s, blockposition, i, flag);
    }

    class a extends BiomeBase {

        protected a(WorldGenSurfaceComposite worldgensurfacecomposite, BiomeBase.Precipitation biomebase_precipitation, BiomeBase.Geography biomebase_geography, float f, float f1, float f2, float f3, int i, int j, String s) {
            super((new BiomeBase.a()).a(worldgensurfacecomposite).a(biomebase_precipitation).a(biomebase_geography).a(f).b(f1).c(f2).d(f3).a(i).b(j).a(s));
        }
    }
}
