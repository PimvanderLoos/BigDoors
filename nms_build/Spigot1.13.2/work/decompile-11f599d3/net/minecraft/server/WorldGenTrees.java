package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class WorldGenTrees extends WorldGenTreeAbstract<WorldGenFeatureEmptyConfiguration> {

    private static final IBlockData b = Blocks.OAK_LOG.getBlockData();
    private static final IBlockData c = Blocks.OAK_LEAVES.getBlockData();
    protected final int a;
    private final boolean d;
    private final IBlockData aH;
    private final IBlockData aI;

    public WorldGenTrees(boolean flag) {
        this(flag, 4, WorldGenTrees.b, WorldGenTrees.c, false);
    }

    public WorldGenTrees(boolean flag, int i, IBlockData iblockdata, IBlockData iblockdata1, boolean flag1) {
        super(flag);
        this.a = i;
        this.aH = iblockdata;
        this.aI = iblockdata1;
        this.d = flag1;
    }

    public boolean a(Set<BlockPosition> set, GeneratorAccess generatoraccess, Random random, BlockPosition blockposition) {
        int i = this.a(random);
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
                    boolean flag1 = true;
                    boolean flag2 = false;

                    int i1;
                    int j1;
                    int k1;
                    BlockPosition blockposition1;

                    for (j = blockposition.getY() - 3 + i; j <= blockposition.getY() + i; ++j) {
                        k = j - (blockposition.getY() + i);
                        i1 = 1 - k / 2;

                        for (int l1 = blockposition.getX() - i1; l1 <= blockposition.getX() + i1; ++l1) {
                            j1 = l1 - blockposition.getX();

                            for (k1 = blockposition.getZ() - i1; k1 <= blockposition.getZ() + i1; ++k1) {
                                int i2 = k1 - blockposition.getZ();

                                if (Math.abs(j1) != i1 || Math.abs(i2) != i1 || random.nextInt(2) != 0 && k != 0) {
                                    blockposition1 = new BlockPosition(l1, j, k1);
                                    IBlockData iblockdata = generatoraccess.getType(blockposition1);
                                    Material material = iblockdata.getMaterial();

                                    if (iblockdata.isAir() || iblockdata.a(TagsBlock.LEAVES) || material == Material.REPLACEABLE_PLANT) {
                                        this.a(generatoraccess, blockposition1, this.aI);
                                    }
                                }
                            }
                        }
                    }

                    for (j = 0; j < i; ++j) {
                        IBlockData iblockdata1 = generatoraccess.getType(blockposition.up(j));
                        Material material1 = iblockdata1.getMaterial();

                        if (iblockdata1.isAir() || iblockdata1.a(TagsBlock.LEAVES) || material1 == Material.REPLACEABLE_PLANT) {
                            this.a(set, generatoraccess, blockposition.up(j), this.aH);
                            if (this.d && j > 0) {
                                if (random.nextInt(3) > 0 && generatoraccess.isEmpty(blockposition.a(-1, j, 0))) {
                                    this.a(generatoraccess, blockposition.a(-1, j, 0), BlockVine.EAST);
                                }

                                if (random.nextInt(3) > 0 && generatoraccess.isEmpty(blockposition.a(1, j, 0))) {
                                    this.a(generatoraccess, blockposition.a(1, j, 0), BlockVine.WEST);
                                }

                                if (random.nextInt(3) > 0 && generatoraccess.isEmpty(blockposition.a(0, j, -1))) {
                                    this.a(generatoraccess, blockposition.a(0, j, -1), BlockVine.SOUTH);
                                }

                                if (random.nextInt(3) > 0 && generatoraccess.isEmpty(blockposition.a(0, j, 1))) {
                                    this.a(generatoraccess, blockposition.a(0, j, 1), BlockVine.NORTH);
                                }
                            }
                        }
                    }

                    if (this.d) {
                        for (j = blockposition.getY() - 3 + i; j <= blockposition.getY() + i; ++j) {
                            k = j - (blockposition.getY() + i);
                            i1 = 2 - k / 2;
                            BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = new BlockPosition.MutableBlockPosition();

                            for (j1 = blockposition.getX() - i1; j1 <= blockposition.getX() + i1; ++j1) {
                                for (k1 = blockposition.getZ() - i1; k1 <= blockposition.getZ() + i1; ++k1) {
                                    blockposition_mutableblockposition1.c(j1, j, k1);
                                    if (generatoraccess.getType(blockposition_mutableblockposition1).a(TagsBlock.LEAVES)) {
                                        BlockPosition blockposition2 = blockposition_mutableblockposition1.west();

                                        blockposition1 = blockposition_mutableblockposition1.east();
                                        BlockPosition blockposition3 = blockposition_mutableblockposition1.north();
                                        BlockPosition blockposition4 = blockposition_mutableblockposition1.south();

                                        if (random.nextInt(4) == 0 && generatoraccess.getType(blockposition2).isAir()) {
                                            this.b(generatoraccess, blockposition2, BlockVine.EAST);
                                        }

                                        if (random.nextInt(4) == 0 && generatoraccess.getType(blockposition1).isAir()) {
                                            this.b(generatoraccess, blockposition1, BlockVine.WEST);
                                        }

                                        if (random.nextInt(4) == 0 && generatoraccess.getType(blockposition3).isAir()) {
                                            this.b(generatoraccess, blockposition3, BlockVine.SOUTH);
                                        }

                                        if (random.nextInt(4) == 0 && generatoraccess.getType(blockposition4).isAir()) {
                                            this.b(generatoraccess, blockposition4, BlockVine.NORTH);
                                        }
                                    }
                                }
                            }
                        }

                        if (random.nextInt(5) == 0 && i > 5) {
                            for (j = 0; j < 2; ++j) {
                                Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                                while (iterator.hasNext()) {
                                    EnumDirection enumdirection = (EnumDirection) iterator.next();

                                    if (random.nextInt(4 - j) == 0) {
                                        EnumDirection enumdirection1 = enumdirection.opposite();

                                        this.a(generatoraccess, random.nextInt(3), blockposition.a(enumdirection1.getAdjacentX(), i - 5 + j, enumdirection1.getAdjacentZ()), enumdirection);
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

    protected int a(Random random) {
        return this.a + random.nextInt(3);
    }

    private void a(GeneratorAccess generatoraccess, int i, BlockPosition blockposition, EnumDirection enumdirection) {
        this.a(generatoraccess, blockposition, (IBlockData) ((IBlockData) Blocks.COCOA.getBlockData().set(BlockCocoa.AGE, i)).set(BlockCocoa.FACING, enumdirection));
    }

    private void a(GeneratorAccess generatoraccess, BlockPosition blockposition, BlockStateBoolean blockstateboolean) {
        this.a(generatoraccess, blockposition, (IBlockData) Blocks.VINE.getBlockData().set(blockstateboolean, true));
    }

    private void b(GeneratorAccess generatoraccess, BlockPosition blockposition, BlockStateBoolean blockstateboolean) {
        this.a(generatoraccess, blockposition, blockstateboolean);
        int i = 4;

        for (blockposition = blockposition.down(); generatoraccess.getType(blockposition).isAir() && i > 0; --i) {
            this.a(generatoraccess, blockposition, blockstateboolean);
            blockposition = blockposition.down();
        }

    }
}
