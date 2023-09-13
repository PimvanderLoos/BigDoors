package net.minecraft.server;

import java.util.Random;

public abstract class BlockLeaves extends Block {

    public static final BlockStateBoolean DECAYABLE = BlockStateBoolean.of("decayable");
    public static final BlockStateBoolean CHECK_DECAY = BlockStateBoolean.of("check_decay");
    protected boolean c;
    int[] d;

    public BlockLeaves() {
        super(Material.LEAVES);
        this.a(true);
        this.a(CreativeModeTab.c);
        this.c(0.2F);
        this.e(1);
        this.a(SoundEffectType.c);
    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        boolean flag = true;
        boolean flag1 = true;
        int i = blockposition.getX();
        int j = blockposition.getY();
        int k = blockposition.getZ();

        if (world.areChunksLoadedBetween(new BlockPosition(i - 2, j - 2, k - 2), new BlockPosition(i + 2, j + 2, k + 2))) {
            for (int l = -1; l <= 1; ++l) {
                for (int i1 = -1; i1 <= 1; ++i1) {
                    for (int j1 = -1; j1 <= 1; ++j1) {
                        BlockPosition blockposition1 = blockposition.a(l, i1, j1);
                        IBlockData iblockdata1 = world.getType(blockposition1);

                        if (iblockdata1.getMaterial() == Material.LEAVES && !((Boolean) iblockdata1.get(BlockLeaves.CHECK_DECAY)).booleanValue()) {
                            world.setTypeAndData(blockposition1, iblockdata1.set(BlockLeaves.CHECK_DECAY, Boolean.valueOf(true)), 4);
                        }
                    }
                }
            }
        }

    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (!world.isClientSide) {
            if (((Boolean) iblockdata.get(BlockLeaves.CHECK_DECAY)).booleanValue() && ((Boolean) iblockdata.get(BlockLeaves.DECAYABLE)).booleanValue()) {
                boolean flag = true;
                boolean flag1 = true;
                int i = blockposition.getX();
                int j = blockposition.getY();
                int k = blockposition.getZ();
                boolean flag2 = true;
                boolean flag3 = true;
                boolean flag4 = true;

                if (this.d == null) {
                    this.d = new int['\u8000'];
                }

                if (world.areChunksLoadedBetween(new BlockPosition(i - 5, j - 5, k - 5), new BlockPosition(i + 5, j + 5, k + 5))) {
                    BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

                    int l;
                    int i1;
                    int j1;

                    for (l = -4; l <= 4; ++l) {
                        for (i1 = -4; i1 <= 4; ++i1) {
                            for (j1 = -4; j1 <= 4; ++j1) {
                                IBlockData iblockdata1 = world.getType(blockposition_mutableblockposition.c(i + l, j + i1, k + j1));
                                Block block = iblockdata1.getBlock();

                                if (block != Blocks.LOG && block != Blocks.LOG2) {
                                    if (iblockdata1.getMaterial() == Material.LEAVES) {
                                        this.d[(l + 16) * 1024 + (i1 + 16) * 32 + j1 + 16] = -2;
                                    } else {
                                        this.d[(l + 16) * 1024 + (i1 + 16) * 32 + j1 + 16] = -1;
                                    }
                                } else {
                                    this.d[(l + 16) * 1024 + (i1 + 16) * 32 + j1 + 16] = 0;
                                }
                            }
                        }
                    }

                    for (l = 1; l <= 4; ++l) {
                        for (i1 = -4; i1 <= 4; ++i1) {
                            for (j1 = -4; j1 <= 4; ++j1) {
                                for (int k1 = -4; k1 <= 4; ++k1) {
                                    if (this.d[(i1 + 16) * 1024 + (j1 + 16) * 32 + k1 + 16] == l - 1) {
                                        if (this.d[(i1 + 16 - 1) * 1024 + (j1 + 16) * 32 + k1 + 16] == -2) {
                                            this.d[(i1 + 16 - 1) * 1024 + (j1 + 16) * 32 + k1 + 16] = l;
                                        }

                                        if (this.d[(i1 + 16 + 1) * 1024 + (j1 + 16) * 32 + k1 + 16] == -2) {
                                            this.d[(i1 + 16 + 1) * 1024 + (j1 + 16) * 32 + k1 + 16] = l;
                                        }

                                        if (this.d[(i1 + 16) * 1024 + (j1 + 16 - 1) * 32 + k1 + 16] == -2) {
                                            this.d[(i1 + 16) * 1024 + (j1 + 16 - 1) * 32 + k1 + 16] = l;
                                        }

                                        if (this.d[(i1 + 16) * 1024 + (j1 + 16 + 1) * 32 + k1 + 16] == -2) {
                                            this.d[(i1 + 16) * 1024 + (j1 + 16 + 1) * 32 + k1 + 16] = l;
                                        }

                                        if (this.d[(i1 + 16) * 1024 + (j1 + 16) * 32 + (k1 + 16 - 1)] == -2) {
                                            this.d[(i1 + 16) * 1024 + (j1 + 16) * 32 + (k1 + 16 - 1)] = l;
                                        }

                                        if (this.d[(i1 + 16) * 1024 + (j1 + 16) * 32 + k1 + 16 + 1] == -2) {
                                            this.d[(i1 + 16) * 1024 + (j1 + 16) * 32 + k1 + 16 + 1] = l;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                int l1 = this.d[16912];

                if (l1 >= 0) {
                    world.setTypeAndData(blockposition, iblockdata.set(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false)), 4);
                } else {
                    this.b(world, blockposition);
                }
            }

        }
    }

    private void b(World world, BlockPosition blockposition) {
        this.b(world, blockposition, world.getType(blockposition), 0);
        world.setAir(blockposition);
    }

    public int a(Random random) {
        return random.nextInt(20) == 0 ? 1 : 0;
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Item.getItemOf(Blocks.SAPLING);
    }

    public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {
        if (!world.isClientSide) {
            int j = this.x(iblockdata);

            if (i > 0) {
                j -= 2 << i;
                if (j < 10) {
                    j = 10;
                }
            }

            if (world.random.nextInt(j) == 0) {
                Item item = this.getDropType(iblockdata, world.random, i);

                a(world, blockposition, new ItemStack(item, 1, this.getDropData(iblockdata)));
            }

            j = 200;
            if (i > 0) {
                j -= 10 << i;
                if (j < 40) {
                    j = 40;
                }
            }

            this.a(world, blockposition, iblockdata, j);
        }

    }

    protected void a(World world, BlockPosition blockposition, IBlockData iblockdata, int i) {}

    protected int x(IBlockData iblockdata) {
        return 20;
    }

    public boolean b(IBlockData iblockdata) {
        return !this.c;
    }

    public boolean t(IBlockData iblockdata) {
        return false;
    }

    public abstract BlockWood.EnumLogVariant b(int i);
}
