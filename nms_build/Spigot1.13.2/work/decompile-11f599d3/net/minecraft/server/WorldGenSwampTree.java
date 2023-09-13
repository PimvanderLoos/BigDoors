package net.minecraft.server;

import java.util.Random;
import java.util.Set;

public class WorldGenSwampTree extends WorldGenTreeAbstract<WorldGenFeatureEmptyConfiguration> {

    private static final IBlockData a = Blocks.OAK_LOG.getBlockData();
    private static final IBlockData b = Blocks.OAK_LEAVES.getBlockData();

    public WorldGenSwampTree() {
        super(false);
    }

    public boolean a(Set<BlockPosition> set, GeneratorAccess generatoraccess, Random random, BlockPosition blockposition) {
        int i;

        for (i = random.nextInt(4) + 5; generatoraccess.getFluid(blockposition.down()).a(TagsFluid.WATER); blockposition = blockposition.down()) {
            ;
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
                    b0 = 3;
                }

                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

                for (j = blockposition.getX() - b0; j <= blockposition.getX() + b0 && flag; ++j) {
                    for (k = blockposition.getZ() - b0; k <= blockposition.getZ() + b0 && flag; ++k) {
                        if (l >= 0 && l < 256) {
                            IBlockData iblockdata = generatoraccess.getType(blockposition_mutableblockposition.c(j, l, k));
                            Block block = iblockdata.getBlock();

                            if (!iblockdata.isAir() && !iblockdata.a(TagsBlock.LEAVES)) {
                                if (block == Blocks.WATER) {
                                    if (l > blockposition.getY()) {
                                        flag = false;
                                    }
                                } else {
                                    flag = false;
                                }
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
                Block block1 = generatoraccess.getType(blockposition.down()).getBlock();

                if ((block1 == Blocks.GRASS_BLOCK || Block.d(block1)) && blockposition.getY() < 256 - i - 1) {
                    this.a(generatoraccess, blockposition.down());

                    int i1;
                    int j1;
                    int k1;
                    int l1;
                    BlockPosition blockposition1;

                    for (j1 = blockposition.getY() - 3 + i; j1 <= blockposition.getY() + i; ++j1) {
                        i1 = j1 - (blockposition.getY() + i);
                        j = 2 - i1 / 2;

                        for (k = blockposition.getX() - j; k <= blockposition.getX() + j; ++k) {
                            k1 = k - blockposition.getX();

                            for (l1 = blockposition.getZ() - j; l1 <= blockposition.getZ() + j; ++l1) {
                                int i2 = l1 - blockposition.getZ();

                                if (Math.abs(k1) != j || Math.abs(i2) != j || random.nextInt(2) != 0 && i1 != 0) {
                                    blockposition1 = new BlockPosition(k, j1, l1);
                                    if (!generatoraccess.getType(blockposition1).f(generatoraccess, blockposition1)) {
                                        this.a(generatoraccess, blockposition1, WorldGenSwampTree.b);
                                    }
                                }
                            }
                        }
                    }

                    for (j1 = 0; j1 < i; ++j1) {
                        IBlockData iblockdata1 = generatoraccess.getType(blockposition.up(j1));
                        Block block2 = iblockdata1.getBlock();

                        if (iblockdata1.isAir() || iblockdata1.a(TagsBlock.LEAVES) || block2 == Blocks.WATER) {
                            this.a(set, generatoraccess, blockposition.up(j1), WorldGenSwampTree.a);
                        }
                    }

                    for (j1 = blockposition.getY() - 3 + i; j1 <= blockposition.getY() + i; ++j1) {
                        i1 = j1 - (blockposition.getY() + i);
                        j = 2 - i1 / 2;
                        BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = new BlockPosition.MutableBlockPosition();

                        for (k1 = blockposition.getX() - j; k1 <= blockposition.getX() + j; ++k1) {
                            for (l1 = blockposition.getZ() - j; l1 <= blockposition.getZ() + j; ++l1) {
                                blockposition_mutableblockposition1.c(k1, j1, l1);
                                if (generatoraccess.getType(blockposition_mutableblockposition1).a(TagsBlock.LEAVES)) {
                                    BlockPosition blockposition2 = blockposition_mutableblockposition1.west();

                                    blockposition1 = blockposition_mutableblockposition1.east();
                                    BlockPosition blockposition3 = blockposition_mutableblockposition1.north();
                                    BlockPosition blockposition4 = blockposition_mutableblockposition1.south();

                                    if (random.nextInt(4) == 0 && generatoraccess.getType(blockposition2).isAir()) {
                                        this.a(generatoraccess, blockposition2, BlockVine.EAST);
                                    }

                                    if (random.nextInt(4) == 0 && generatoraccess.getType(blockposition1).isAir()) {
                                        this.a(generatoraccess, blockposition1, BlockVine.WEST);
                                    }

                                    if (random.nextInt(4) == 0 && generatoraccess.getType(blockposition3).isAir()) {
                                        this.a(generatoraccess, blockposition3, BlockVine.SOUTH);
                                    }

                                    if (random.nextInt(4) == 0 && generatoraccess.getType(blockposition4).isAir()) {
                                        this.a(generatoraccess, blockposition4, BlockVine.NORTH);
                                    }
                                }
                            }
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

    private void a(GeneratorAccess generatoraccess, BlockPosition blockposition, BlockStateBoolean blockstateboolean) {
        IBlockData iblockdata = (IBlockData) Blocks.VINE.getBlockData().set(blockstateboolean, true);

        this.a(generatoraccess, blockposition, iblockdata);
        int i = 4;

        for (blockposition = blockposition.down(); generatoraccess.getType(blockposition).isAir() && i > 0; --i) {
            this.a(generatoraccess, blockposition, iblockdata);
            blockposition = blockposition.down();
        }

    }
}
