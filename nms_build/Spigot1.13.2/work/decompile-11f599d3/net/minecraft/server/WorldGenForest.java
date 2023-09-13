package net.minecraft.server;

import java.util.Random;
import java.util.Set;

public class WorldGenForest extends WorldGenTreeAbstract<WorldGenFeatureEmptyConfiguration> {

    private static final IBlockData a = Blocks.BIRCH_LOG.getBlockData();
    private static final IBlockData b = Blocks.BIRCH_LEAVES.getBlockData();
    private final boolean c;

    public WorldGenForest(boolean flag, boolean flag1) {
        super(flag);
        this.c = flag1;
    }

    public boolean a(Set<BlockPosition> set, GeneratorAccess generatoraccess, Random random, BlockPosition blockposition) {
        int i = random.nextInt(3) + 5;

        if (this.c) {
            i += random.nextInt(7);
        }

        boolean flag = true;

        if (blockposition.getY() >= 1 && blockposition.getY() + i + 1 <= 256) {
            int j;
            int k;

            for (int l = blockposition.getY(); l <= blockposition.getY() + 1 + i; ++l) {
                byte b0 = 1;

                if (l == blockposition.getY()) {
                    b0 = 0;
                }

                if (l >= blockposition.getY() + 1 + i - 2) {
                    b0 = 2;
                }

                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

                for (j = blockposition.getX() - b0; j <= blockposition.getX() + b0 && flag; ++j) {
                    for (k = blockposition.getZ() - b0; k <= blockposition.getZ() + b0 && flag; ++k) {
                        if (l >= 0 && l < 256) {
                            if (!this.a(generatoraccess.getType(blockposition_mutableblockposition.c(j, l, k)).getBlock())) {
                                flag = false;
                            }
                        } else {
                            flag = false;
                        }
                    }
                }
            }

            if (!flag) {
                return false;
            } else {
                Block block = generatoraccess.getType(blockposition.down()).getBlock();

                if ((block == Blocks.GRASS_BLOCK || Block.d(block) || block == Blocks.FARMLAND) && blockposition.getY() < 256 - i - 1) {
                    this.a(generatoraccess, blockposition.down());

                    int i1;

                    for (i1 = blockposition.getY() - 3 + i; i1 <= blockposition.getY() + i; ++i1) {
                        int j1 = i1 - (blockposition.getY() + i);

                        j = 1 - j1 / 2;

                        for (k = blockposition.getX() - j; k <= blockposition.getX() + j; ++k) {
                            int k1 = k - blockposition.getX();

                            for (int l1 = blockposition.getZ() - j; l1 <= blockposition.getZ() + j; ++l1) {
                                int i2 = l1 - blockposition.getZ();

                                if (Math.abs(k1) != j || Math.abs(i2) != j || random.nextInt(2) != 0 && j1 != 0) {
                                    BlockPosition blockposition1 = new BlockPosition(k, i1, l1);
                                    IBlockData iblockdata = generatoraccess.getType(blockposition1);

                                    if (iblockdata.isAir() || iblockdata.a(TagsBlock.LEAVES)) {
                                        this.a(generatoraccess, blockposition1, WorldGenForest.b);
                                    }
                                }
                            }
                        }
                    }

                    for (i1 = 0; i1 < i; ++i1) {
                        IBlockData iblockdata1 = generatoraccess.getType(blockposition.up(i1));

                        if (iblockdata1.isAir() || iblockdata1.a(TagsBlock.LEAVES)) {
                            this.a(set, generatoraccess, blockposition.up(i1), WorldGenForest.a);
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }
}
