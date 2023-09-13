package net.minecraft.server;

public class ChunkSnapshot {

    private static final IBlockData a = Blocks.AIR.getBlockData();
    private final char[] b = new char[65536];

    public ChunkSnapshot() {}

    public IBlockData a(int i, int j, int k) {
        IBlockData iblockdata = (IBlockData) Block.REGISTRY_ID.fromId(this.b[b(i, j, k)]);

        return iblockdata == null ? ChunkSnapshot.a : iblockdata;
    }

    public void a(int i, int j, int k, IBlockData iblockdata) {
        this.b[b(i, j, k)] = (char) Block.REGISTRY_ID.getId(iblockdata);
    }

    private static int b(int i, int j, int k) {
        return i << 12 | k << 8 | j;
    }

    public int a(int i, int j) {
        int k = (i << 12 | j << 8) + 256 - 1;

        for (int l = 255; l >= 0; --l) {
            IBlockData iblockdata = (IBlockData) Block.REGISTRY_ID.fromId(this.b[k + l]);

            if (iblockdata != null && iblockdata != ChunkSnapshot.a) {
                return l;
            }
        }

        return 0;
    }
}
