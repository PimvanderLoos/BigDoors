package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class ChunkProviderDebug implements ChunkGenerator {

    private static final List<IBlockData> c = Lists.newArrayList();
    private static final int d;
    private static final int e;
    protected static final IBlockData a = Blocks.AIR.getBlockData();
    protected static final IBlockData b = Blocks.BARRIER.getBlockData();
    private final World f;

    public ChunkProviderDebug(World world) {
        this.f = world;
    }

    public Chunk getOrCreateChunk(int i, int j) {
        ChunkSnapshot chunksnapshot = new ChunkSnapshot();

        int k;

        for (int l = 0; l < 16; ++l) {
            for (int i1 = 0; i1 < 16; ++i1) {
                int j1 = i * 16 + l;

                k = j * 16 + i1;
                chunksnapshot.a(l, 60, i1, ChunkProviderDebug.b);
                IBlockData iblockdata = c(j1, k);

                if (iblockdata != null) {
                    chunksnapshot.a(l, 70, i1, iblockdata);
                }
            }
        }

        Chunk chunk = new Chunk(this.f, chunksnapshot, i, j);

        chunk.initLighting();
        BiomeBase[] abiomebase = this.f.getWorldChunkManager().getBiomeBlock((BiomeBase[]) null, i * 16, j * 16, 16, 16);
        byte[] abyte = chunk.getBiomeIndex();

        for (k = 0; k < abyte.length; ++k) {
            abyte[k] = (byte) BiomeBase.a(abiomebase[k]);
        }

        chunk.initLighting();
        return chunk;
    }

    public static IBlockData c(int i, int j) {
        IBlockData iblockdata = ChunkProviderDebug.a;

        if (i > 0 && j > 0 && i % 2 != 0 && j % 2 != 0) {
            i /= 2;
            j /= 2;
            if (i <= ChunkProviderDebug.d && j <= ChunkProviderDebug.e) {
                int k = MathHelper.a(i * ChunkProviderDebug.d + j);

                if (k < ChunkProviderDebug.c.size()) {
                    iblockdata = (IBlockData) ChunkProviderDebug.c.get(k);
                }
            }
        }

        return iblockdata;
    }

    public void recreateStructures(int i, int j) {}

    public boolean a(Chunk chunk, int i, int j) {
        return false;
    }

    public List<BiomeBase.BiomeMeta> getMobsFor(EnumCreatureType enumcreaturetype, BlockPosition blockposition) {
        BiomeBase biomebase = this.f.getBiome(blockposition);

        return biomebase.getMobs(enumcreaturetype);
    }

    @Nullable
    public BlockPosition findNearestMapFeature(World world, String s, BlockPosition blockposition, boolean flag) {
        return null;
    }

    public boolean a(World world, String s, BlockPosition blockposition) {
        return false;
    }

    public void recreateStructures(Chunk chunk, int i, int j) {}

    static {
        Iterator iterator = Block.REGISTRY.iterator();

        while (iterator.hasNext()) {
            Block block = (Block) iterator.next();

            ChunkProviderDebug.c.addAll(block.s().a());
        }

        d = MathHelper.f(MathHelper.c((float) ChunkProviderDebug.c.size()));
        e = MathHelper.f((float) ChunkProviderDebug.c.size() / (float) ChunkProviderDebug.d);
    }
}
