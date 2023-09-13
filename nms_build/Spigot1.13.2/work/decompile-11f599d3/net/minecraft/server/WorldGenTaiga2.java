package net.minecraft.server;

import java.util.Random;
import java.util.Set;

public class WorldGenTaiga2 extends WorldGenTreeAbstract<WorldGenFeatureEmptyConfiguration> {

    private static final IBlockData a = Blocks.SPRUCE_LOG.getBlockData();
    private static final IBlockData b = Blocks.SPRUCE_LEAVES.getBlockData();

    public WorldGenTaiga2(boolean flag) {
        super(flag);
    }

    public boolean a(Set<BlockPosition> set, GeneratorAccess generatoraccess, Random random, BlockPosition blockposition) {
        int i = random.nextInt(4) + 6;
        int j = 1 + random.nextInt(2);
        int k = i - j;
        int l = 2 + random.nextInt(2);
        boolean flag = true;

        if (blockposition.getY() >= 1 && blockposition.getY() + i + 1 <= 256) {
            int i1;
            int j1;

            for (int k1 = blockposition.getY(); k1 <= blockposition.getY() + 1 + i && flag; ++k1) {
                if (k1 - blockposition.getY() < j) {
                    i1 = 0;
                } else {
                    i1 = l;
                }

                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

                for (int l1 = blockposition.getX() - i1; l1 <= blockposition.getX() + i1 && flag; ++l1) {
                    for (j1 = blockposition.getZ() - i1; j1 <= blockposition.getZ() + i1 && flag; ++j1) {
                        if (k1 >= 0 && k1 < 256) {
                            IBlockData iblockdata = generatoraccess.getType(blockposition_mutableblockposition.c(l1, k1, j1));

                            if (!iblockdata.isAir() && !iblockdata.a(TagsBlock.LEAVES)) {
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
                    i1 = random.nextInt(2);
                    int i2 = 1;
                    byte b0 = 0;

                    int j2;

                    for (j1 = 0; j1 <= k; ++j1) {
                        j2 = blockposition.getY() + i - j1;

                        for (int k2 = blockposition.getX() - i1; k2 <= blockposition.getX() + i1; ++k2) {
                            int l2 = k2 - blockposition.getX();

                            for (int i3 = blockposition.getZ() - i1; i3 <= blockposition.getZ() + i1; ++i3) {
                                int j3 = i3 - blockposition.getZ();

                                if (Math.abs(l2) != i1 || Math.abs(j3) != i1 || i1 <= 0) {
                                    BlockPosition blockposition1 = new BlockPosition(k2, j2, i3);

                                    if (!generatoraccess.getType(blockposition1).f(generatoraccess, blockposition1)) {
                                        this.a(generatoraccess, blockposition1, WorldGenTaiga2.b);
                                    }
                                }
                            }
                        }

                        if (i1 >= i2) {
                            i1 = b0;
                            b0 = 1;
                            ++i2;
                            if (i2 > l) {
                                i2 = l;
                            }
                        } else {
                            ++i1;
                        }
                    }

                    j1 = random.nextInt(3);

                    for (j2 = 0; j2 < i - j1; ++j2) {
                        IBlockData iblockdata1 = generatoraccess.getType(blockposition.up(j2));

                        if (iblockdata1.isAir() || iblockdata1.a(TagsBlock.LEAVES)) {
                            this.a(set, generatoraccess, blockposition.up(j2), WorldGenTaiga2.a);
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
