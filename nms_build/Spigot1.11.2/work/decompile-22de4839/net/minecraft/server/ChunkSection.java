package net.minecraft.server;

public class ChunkSection {

    private final int yPos;
    private int nonEmptyBlockCount;
    private int tickingBlockCount;
    private final DataPaletteBlock blockIds;
    private NibbleArray emittedLight;
    private NibbleArray skyLight;

    public ChunkSection(int i, boolean flag) {
        this.yPos = i;
        this.blockIds = new DataPaletteBlock();
        this.emittedLight = new NibbleArray();
        if (flag) {
            this.skyLight = new NibbleArray();
        }

    }

    public IBlockData getType(int i, int j, int k) {
        return this.blockIds.a(i, j, k);
    }

    public void setType(int i, int j, int k, IBlockData iblockdata) {
        IBlockData iblockdata1 = this.getType(i, j, k);
        Block block = iblockdata1.getBlock();
        Block block1 = iblockdata.getBlock();

        if (block != Blocks.AIR) {
            --this.nonEmptyBlockCount;
            if (block.isTicking()) {
                --this.tickingBlockCount;
            }
        }

        if (block1 != Blocks.AIR) {
            ++this.nonEmptyBlockCount;
            if (block1.isTicking()) {
                ++this.tickingBlockCount;
            }
        }

        this.blockIds.setBlock(i, j, k, iblockdata);
    }

    public boolean a() {
        return this.nonEmptyBlockCount == 0;
    }

    public boolean shouldTick() {
        return this.tickingBlockCount > 0;
    }

    public int getYPosition() {
        return this.yPos;
    }

    public void a(int i, int j, int k, int l) {
        this.skyLight.a(i, j, k, l);
    }

    public int b(int i, int j, int k) {
        return this.skyLight.a(i, j, k);
    }

    public void b(int i, int j, int k, int l) {
        this.emittedLight.a(i, j, k, l);
    }

    public int c(int i, int j, int k) {
        return this.emittedLight.a(i, j, k);
    }

    public void recalcBlockCounts() {
        this.nonEmptyBlockCount = 0;
        this.tickingBlockCount = 0;

        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                for (int k = 0; k < 16; ++k) {
                    Block block = this.getType(i, j, k).getBlock();

                    if (block != Blocks.AIR) {
                        ++this.nonEmptyBlockCount;
                        if (block.isTicking()) {
                            ++this.tickingBlockCount;
                        }
                    }
                }
            }
        }

    }

    public DataPaletteBlock getBlocks() {
        return this.blockIds;
    }

    public NibbleArray getEmittedLightArray() {
        return this.emittedLight;
    }

    public NibbleArray getSkyLightArray() {
        return this.skyLight;
    }

    public void a(NibbleArray nibblearray) {
        this.emittedLight = nibblearray;
    }

    public void b(NibbleArray nibblearray) {
        this.skyLight = nibblearray;
    }
}
