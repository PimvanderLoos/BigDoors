package net.minecraft.server;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ChunkProviderDebug extends ChunkGeneratorAbstract<GeneratorSettingsDebug> {

    private static final List<IBlockData> h = (List) StreamSupport.stream(IRegistry.BLOCK.spliterator(), false).flatMap((block) -> {
        return block.getStates().a().stream();
    }).collect(Collectors.toList());
    private static final int i = MathHelper.f(MathHelper.c((float) ChunkProviderDebug.h.size()));
    private static final int j = MathHelper.f((float) ChunkProviderDebug.h.size() / (float) ChunkProviderDebug.i);
    protected static final IBlockData f = Blocks.AIR.getBlockData();
    protected static final IBlockData g = Blocks.BARRIER.getBlockData();
    private final GeneratorSettingsDebug k;

    public ChunkProviderDebug(GeneratorAccess generatoraccess, WorldChunkManager worldchunkmanager, GeneratorSettingsDebug generatorsettingsdebug) {
        super(generatoraccess, worldchunkmanager);
        this.k = generatorsettingsdebug;
    }

    public void createChunk(IChunkAccess ichunkaccess) {
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        int i = chunkcoordintpair.x;
        int j = chunkcoordintpair.z;
        BiomeBase[] abiomebase = this.c.getBiomeBlock(i * 16, j * 16, 16, 16);

        ichunkaccess.a(abiomebase);
        ichunkaccess.a(HeightMap.Type.WORLD_SURFACE_WG, HeightMap.Type.OCEAN_FLOOR_WG);
        ichunkaccess.a(ChunkStatus.BASE);
    }

    public void addFeatures(RegionLimitedWorldAccess regionlimitedworldaccess, WorldGenStage.Features worldgenstage_features) {}

    public GeneratorSettingsDebug getSettings() {
        return this.k;
    }

    public double[] a(int i, int j) {
        return new double[0];
    }

    public int getSpawnHeight() {
        return this.a.getSeaLevel() + 1;
    }

    public void addDecorations(RegionLimitedWorldAccess regionlimitedworldaccess) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        int i = regionlimitedworldaccess.a();
        int j = regionlimitedworldaccess.b();

        for (int k = 0; k < 16; ++k) {
            for (int l = 0; l < 16; ++l) {
                int i1 = (i << 4) + k;
                int j1 = (j << 4) + l;

                regionlimitedworldaccess.setTypeAndData(blockposition_mutableblockposition.c(i1, 60, j1), ChunkProviderDebug.g, 2);
                IBlockData iblockdata = b(i1, j1);

                if (iblockdata != null) {
                    regionlimitedworldaccess.setTypeAndData(blockposition_mutableblockposition.c(i1, 70, j1), iblockdata, 2);
                }
            }
        }

    }

    public void addMobs(RegionLimitedWorldAccess regionlimitedworldaccess) {}

    public static IBlockData b(int i, int j) {
        IBlockData iblockdata = ChunkProviderDebug.f;

        if (i > 0 && j > 0 && i % 2 != 0 && j % 2 != 0) {
            i /= 2;
            j /= 2;
            if (i <= ChunkProviderDebug.i && j <= ChunkProviderDebug.j) {
                int k = MathHelper.a(i * ChunkProviderDebug.i + j);

                if (k < ChunkProviderDebug.h.size()) {
                    iblockdata = (IBlockData) ChunkProviderDebug.h.get(k);
                }
            }
        }

        return iblockdata;
    }

    public List<BiomeBase.BiomeMeta> getMobsFor(EnumCreatureType enumcreaturetype, BlockPosition blockposition) {
        BiomeBase biomebase = this.a.getBiome(blockposition);

        return biomebase.getMobs(enumcreaturetype);
    }

    public int a(World world, boolean flag, boolean flag1) {
        return 0;
    }
}
