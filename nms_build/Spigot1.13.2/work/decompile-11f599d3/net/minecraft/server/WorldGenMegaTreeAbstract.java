package net.minecraft.server;

import java.util.Random;

public abstract class WorldGenMegaTreeAbstract<T extends WorldGenFeatureConfiguration> extends WorldGenTreeAbstract<T> {

    protected final int a;
    protected final IBlockData b;
    protected final IBlockData c;
    protected int d;

    public WorldGenMegaTreeAbstract(boolean flag, int i, int j, IBlockData iblockdata, IBlockData iblockdata1) {
        super(flag);
        this.a = i;
        this.d = j;
        this.b = iblockdata;
        this.c = iblockdata1;
    }

    protected int a(Random random) {
        int i = random.nextInt(3) + this.a;

        if (this.d > 1) {
            i += random.nextInt(this.d);
        }

        return i;
    }

    private boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, int i) {
        boolean flag = true;

        if (blockposition.getY() >= 1 && blockposition.getY() + i + 1 <= 256) {
            for (int j = 0; j <= 1 + i; ++j) {
                byte b0 = 2;

                if (j == 0) {
                    b0 = 1;
                } else if (j >= 1 + i - 2) {
                    b0 = 2;
                }

                for (int k = -b0; k <= b0 && flag; ++k) {
                    for (int l = -b0; l <= b0 && flag; ++l) {
                        if (blockposition.getY() + j < 0 || blockposition.getY() + j >= 256 || !this.a(iblockaccess.getType(blockposition.a(k, j, l)).getBlock())) {
                            flag = false;
                        }
                    }
                }
            }

            return flag;
        } else {
            return false;
        }
    }

    private boolean b(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.down();
        Block block = generatoraccess.getType(blockposition1).getBlock();

        if ((block == Blocks.GRASS_BLOCK || Block.d(block)) && blockposition.getY() >= 2) {
            this.a(generatoraccess, blockposition1);
            this.a(generatoraccess, blockposition1.east());
            this.a(generatoraccess, blockposition1.south());
            this.a(generatoraccess, blockposition1.south().east());
            return true;
        } else {
            return false;
        }
    }

    protected boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition, int i) {
        return this.a((IBlockAccess) generatoraccess, blockposition, i) && this.b(generatoraccess, blockposition);
    }

    protected void b(GeneratorAccess generatoraccess, BlockPosition blockposition, int i) {
        int j = i * i;

        for (int k = -i; k <= i + 1; ++k) {
            for (int l = -i; l <= i + 1; ++l) {
                int i1 = Math.min(Math.abs(k), Math.abs(k - 1));
                int j1 = Math.min(Math.abs(l), Math.abs(l - 1));

                if (i1 + j1 < 7 && i1 * i1 + j1 * j1 <= j) {
                    BlockPosition blockposition1 = blockposition.a(k, 0, l);
                    IBlockData iblockdata = generatoraccess.getType(blockposition1);

                    if (iblockdata.isAir() || iblockdata.a(TagsBlock.LEAVES)) {
                        this.a(generatoraccess, blockposition1, this.c);
                    }
                }
            }
        }

    }

    protected void c(GeneratorAccess generatoraccess, BlockPosition blockposition, int i) {
        int j = i * i;

        for (int k = -i; k <= i; ++k) {
            for (int l = -i; l <= i; ++l) {
                if (k * k + l * l <= j) {
                    BlockPosition blockposition1 = blockposition.a(k, 0, l);
                    IBlockData iblockdata = generatoraccess.getType(blockposition1);

                    if (iblockdata.isAir() || iblockdata.a(TagsBlock.LEAVES)) {
                        this.a(generatoraccess, blockposition1, this.c);
                    }
                }
            }
        }

    }
}
