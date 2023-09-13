package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;

public class BlockVine extends Block {

    public static final BlockStateBoolean UP = BlockStateBoolean.of("up");
    public static final BlockStateBoolean NORTH = BlockStateBoolean.of("north");
    public static final BlockStateBoolean EAST = BlockStateBoolean.of("east");
    public static final BlockStateBoolean SOUTH = BlockStateBoolean.of("south");
    public static final BlockStateBoolean WEST = BlockStateBoolean.of("west");
    public static final BlockStateBoolean[] f = new BlockStateBoolean[] { BlockVine.UP, BlockVine.NORTH, BlockVine.SOUTH, BlockVine.WEST, BlockVine.EAST};
    protected static final AxisAlignedBB g = new AxisAlignedBB(0.0D, 0.9375D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB B = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0625D, 1.0D, 1.0D);
    protected static final AxisAlignedBB C = new AxisAlignedBB(0.9375D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB D = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.0625D);
    protected static final AxisAlignedBB E = new AxisAlignedBB(0.0D, 0.0D, 0.9375D, 1.0D, 1.0D, 1.0D);

    public BlockVine() {
        super(Material.REPLACEABLE_PLANT);
        this.w(this.blockStateList.getBlockData().set(BlockVine.UP, Boolean.valueOf(false)).set(BlockVine.NORTH, Boolean.valueOf(false)).set(BlockVine.EAST, Boolean.valueOf(false)).set(BlockVine.SOUTH, Boolean.valueOf(false)).set(BlockVine.WEST, Boolean.valueOf(false)));
        this.a(true);
        this.a(CreativeModeTab.c);
    }

    @Nullable
    public AxisAlignedBB a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockVine.k;
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        iblockdata = iblockdata.c(iblockaccess, blockposition);
        int i = 0;
        AxisAlignedBB axisalignedbb = BlockVine.j;

        if (((Boolean) iblockdata.get(BlockVine.UP)).booleanValue()) {
            axisalignedbb = BlockVine.g;
            ++i;
        }

        if (((Boolean) iblockdata.get(BlockVine.NORTH)).booleanValue()) {
            axisalignedbb = BlockVine.D;
            ++i;
        }

        if (((Boolean) iblockdata.get(BlockVine.EAST)).booleanValue()) {
            axisalignedbb = BlockVine.C;
            ++i;
        }

        if (((Boolean) iblockdata.get(BlockVine.SOUTH)).booleanValue()) {
            axisalignedbb = BlockVine.E;
            ++i;
        }

        if (((Boolean) iblockdata.get(BlockVine.WEST)).booleanValue()) {
            axisalignedbb = BlockVine.B;
            ++i;
        }

        return i == 1 ? axisalignedbb : BlockVine.j;
    }

    public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.up();

        return iblockdata.set(BlockVine.UP, Boolean.valueOf(iblockaccess.getType(blockposition1).d(iblockaccess, blockposition1, EnumDirection.DOWN) == EnumBlockFaceShape.SOLID));
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return true;
    }

    public boolean canPlace(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        return enumdirection != EnumDirection.DOWN && enumdirection != EnumDirection.UP && this.a(world, blockposition, enumdirection);
    }

    public boolean a(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        Block block = world.getType(blockposition.up()).getBlock();

        return this.c(world, blockposition.shift(enumdirection.opposite()), enumdirection) && (block == Blocks.AIR || block == Blocks.VINE || this.c(world, blockposition.up(), EnumDirection.UP));
    }

    private boolean c(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        IBlockData iblockdata = world.getType(blockposition);

        return iblockdata.d(world, blockposition, enumdirection) == EnumBlockFaceShape.SOLID && !e(iblockdata.getBlock());
    }

    protected static boolean e(Block block) {
        return block instanceof BlockShulkerBox || block == Blocks.BEACON || block == Blocks.cauldron || block == Blocks.GLASS || block == Blocks.STAINED_GLASS || block == Blocks.PISTON || block == Blocks.STICKY_PISTON || block == Blocks.PISTON_HEAD || block == Blocks.TRAPDOOR;
    }

    private boolean e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        IBlockData iblockdata1 = iblockdata;
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();
            BlockStateBoolean blockstateboolean = getDirection(enumdirection);

            if (((Boolean) iblockdata.get(blockstateboolean)).booleanValue() && !this.a(world, blockposition, enumdirection.opposite())) {
                IBlockData iblockdata2 = world.getType(blockposition.up());

                if (iblockdata2.getBlock() != this || !((Boolean) iblockdata2.get(blockstateboolean)).booleanValue()) {
                    iblockdata = iblockdata.set(blockstateboolean, Boolean.valueOf(false));
                }
            }
        }

        if (x(iblockdata) == 0) {
            return false;
        } else {
            if (iblockdata1 != iblockdata) {
                world.setTypeAndData(blockposition, iblockdata, 2);
            }

            return true;
        }
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (!world.isClientSide && !this.e(world, blockposition, iblockdata)) {
            this.b(world, blockposition, iblockdata, 0);
            world.setAir(blockposition);
        }

    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (!world.isClientSide) {
            if (world.random.nextInt(4) == 0) {
                boolean flag = true;
                int i = 5;
                boolean flag1 = false;

                label178:
                for (int j = -4; j <= 4; ++j) {
                    for (int k = -4; k <= 4; ++k) {
                        for (int l = -1; l <= 1; ++l) {
                            if (world.getType(blockposition.a(j, l, k)).getBlock() == this) {
                                --i;
                                if (i <= 0) {
                                    flag1 = true;
                                    break label178;
                                }
                            }
                        }
                    }
                }

                EnumDirection enumdirection = EnumDirection.a(random);
                BlockPosition blockposition1 = blockposition.up();

                if (enumdirection == EnumDirection.UP && blockposition.getY() < 255 && world.isEmpty(blockposition1)) {
                    IBlockData iblockdata1 = iblockdata;
                    Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                    while (iterator.hasNext()) {
                        EnumDirection enumdirection1 = (EnumDirection) iterator.next();

                        if (random.nextBoolean() && this.a(world, blockposition1, enumdirection1.opposite())) {
                            iblockdata1 = iblockdata1.set(getDirection(enumdirection1), Boolean.valueOf(true));
                        } else {
                            iblockdata1 = iblockdata1.set(getDirection(enumdirection1), Boolean.valueOf(false));
                        }
                    }

                    if (((Boolean) iblockdata1.get(BlockVine.NORTH)).booleanValue() || ((Boolean) iblockdata1.get(BlockVine.EAST)).booleanValue() || ((Boolean) iblockdata1.get(BlockVine.SOUTH)).booleanValue() || ((Boolean) iblockdata1.get(BlockVine.WEST)).booleanValue()) {
                        world.setTypeAndData(blockposition1, iblockdata1, 2);
                    }

                } else {
                    IBlockData iblockdata2;
                    Block block;
                    BlockPosition blockposition2;

                    if (enumdirection.k().c() && !((Boolean) iblockdata.get(getDirection(enumdirection))).booleanValue()) {
                        if (!flag1) {
                            blockposition2 = blockposition.shift(enumdirection);
                            iblockdata2 = world.getType(blockposition2);
                            block = iblockdata2.getBlock();
                            if (block.material == Material.AIR) {
                                EnumDirection enumdirection2 = enumdirection.e();
                                EnumDirection enumdirection3 = enumdirection.f();
                                boolean flag2 = ((Boolean) iblockdata.get(getDirection(enumdirection2))).booleanValue();
                                boolean flag3 = ((Boolean) iblockdata.get(getDirection(enumdirection3))).booleanValue();
                                BlockPosition blockposition3 = blockposition2.shift(enumdirection2);
                                BlockPosition blockposition4 = blockposition2.shift(enumdirection3);

                                if (flag2 && this.a(world, blockposition3.shift(enumdirection2), enumdirection2)) {
                                    world.setTypeAndData(blockposition2, this.getBlockData().set(getDirection(enumdirection2), Boolean.valueOf(true)), 2);
                                } else if (flag3 && this.a(world, blockposition4.shift(enumdirection3), enumdirection3)) {
                                    world.setTypeAndData(blockposition2, this.getBlockData().set(getDirection(enumdirection3), Boolean.valueOf(true)), 2);
                                } else if (flag2 && world.isEmpty(blockposition3) && this.a(world, blockposition3, enumdirection)) {
                                    world.setTypeAndData(blockposition3, this.getBlockData().set(getDirection(enumdirection.opposite()), Boolean.valueOf(true)), 2);
                                } else if (flag3 && world.isEmpty(blockposition4) && this.a(world, blockposition4, enumdirection)) {
                                    world.setTypeAndData(blockposition4, this.getBlockData().set(getDirection(enumdirection.opposite()), Boolean.valueOf(true)), 2);
                                }
                            } else if (iblockdata2.d(world, blockposition2, enumdirection) == EnumBlockFaceShape.SOLID) {
                                world.setTypeAndData(blockposition, iblockdata.set(getDirection(enumdirection), Boolean.valueOf(true)), 2);
                            }

                        }
                    } else {
                        if (blockposition.getY() > 1) {
                            blockposition2 = blockposition.down();
                            iblockdata2 = world.getType(blockposition2);
                            block = iblockdata2.getBlock();
                            IBlockData iblockdata3;
                            Iterator iterator1;
                            EnumDirection enumdirection4;

                            if (block.material == Material.AIR) {
                                iblockdata3 = iblockdata;
                                iterator1 = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                                while (iterator1.hasNext()) {
                                    enumdirection4 = (EnumDirection) iterator1.next();
                                    if (random.nextBoolean()) {
                                        iblockdata3 = iblockdata3.set(getDirection(enumdirection4), Boolean.valueOf(false));
                                    }
                                }

                                if (((Boolean) iblockdata3.get(BlockVine.NORTH)).booleanValue() || ((Boolean) iblockdata3.get(BlockVine.EAST)).booleanValue() || ((Boolean) iblockdata3.get(BlockVine.SOUTH)).booleanValue() || ((Boolean) iblockdata3.get(BlockVine.WEST)).booleanValue()) {
                                    world.setTypeAndData(blockposition2, iblockdata3, 2);
                                }
                            } else if (block == this) {
                                iblockdata3 = iblockdata2;
                                iterator1 = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                                while (iterator1.hasNext()) {
                                    enumdirection4 = (EnumDirection) iterator1.next();
                                    BlockStateBoolean blockstateboolean = getDirection(enumdirection4);

                                    if (random.nextBoolean() && ((Boolean) iblockdata.get(blockstateboolean)).booleanValue()) {
                                        iblockdata3 = iblockdata3.set(blockstateboolean, Boolean.valueOf(true));
                                    }
                                }

                                if (((Boolean) iblockdata3.get(BlockVine.NORTH)).booleanValue() || ((Boolean) iblockdata3.get(BlockVine.EAST)).booleanValue() || ((Boolean) iblockdata3.get(BlockVine.SOUTH)).booleanValue() || ((Boolean) iblockdata3.get(BlockVine.WEST)).booleanValue()) {
                                    world.setTypeAndData(blockposition2, iblockdata3, 2);
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        IBlockData iblockdata = this.getBlockData().set(BlockVine.UP, Boolean.valueOf(false)).set(BlockVine.NORTH, Boolean.valueOf(false)).set(BlockVine.EAST, Boolean.valueOf(false)).set(BlockVine.SOUTH, Boolean.valueOf(false)).set(BlockVine.WEST, Boolean.valueOf(false));

        return enumdirection.k().c() ? iblockdata.set(getDirection(enumdirection.opposite()), Boolean.valueOf(true)) : iblockdata;
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Items.a;
    }

    public int a(Random random) {
        return 0;
    }

    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, @Nullable TileEntity tileentity, ItemStack itemstack) {
        if (!world.isClientSide && itemstack.getItem() == Items.SHEARS) {
            entityhuman.b(StatisticList.a((Block) this));
            a(world, blockposition, new ItemStack(Blocks.VINE, 1, 0));
        } else {
            super.a(world, entityhuman, blockposition, iblockdata, tileentity, itemstack);
        }

    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockVine.SOUTH, Boolean.valueOf((i & 1) > 0)).set(BlockVine.WEST, Boolean.valueOf((i & 2) > 0)).set(BlockVine.NORTH, Boolean.valueOf((i & 4) > 0)).set(BlockVine.EAST, Boolean.valueOf((i & 8) > 0));
    }

    public int toLegacyData(IBlockData iblockdata) {
        int i = 0;

        if (((Boolean) iblockdata.get(BlockVine.SOUTH)).booleanValue()) {
            i |= 1;
        }

        if (((Boolean) iblockdata.get(BlockVine.WEST)).booleanValue()) {
            i |= 2;
        }

        if (((Boolean) iblockdata.get(BlockVine.NORTH)).booleanValue()) {
            i |= 4;
        }

        if (((Boolean) iblockdata.get(BlockVine.EAST)).booleanValue()) {
            i |= 8;
        }

        return i;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockVine.UP, BlockVine.NORTH, BlockVine.EAST, BlockVine.SOUTH, BlockVine.WEST});
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
        case CLOCKWISE_180:
            return iblockdata.set(BlockVine.NORTH, iblockdata.get(BlockVine.SOUTH)).set(BlockVine.EAST, iblockdata.get(BlockVine.WEST)).set(BlockVine.SOUTH, iblockdata.get(BlockVine.NORTH)).set(BlockVine.WEST, iblockdata.get(BlockVine.EAST));

        case COUNTERCLOCKWISE_90:
            return iblockdata.set(BlockVine.NORTH, iblockdata.get(BlockVine.EAST)).set(BlockVine.EAST, iblockdata.get(BlockVine.SOUTH)).set(BlockVine.SOUTH, iblockdata.get(BlockVine.WEST)).set(BlockVine.WEST, iblockdata.get(BlockVine.NORTH));

        case CLOCKWISE_90:
            return iblockdata.set(BlockVine.NORTH, iblockdata.get(BlockVine.WEST)).set(BlockVine.EAST, iblockdata.get(BlockVine.NORTH)).set(BlockVine.SOUTH, iblockdata.get(BlockVine.EAST)).set(BlockVine.WEST, iblockdata.get(BlockVine.SOUTH));

        default:
            return iblockdata;
        }
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        switch (enumblockmirror) {
        case LEFT_RIGHT:
            return iblockdata.set(BlockVine.NORTH, iblockdata.get(BlockVine.SOUTH)).set(BlockVine.SOUTH, iblockdata.get(BlockVine.NORTH));

        case FRONT_BACK:
            return iblockdata.set(BlockVine.EAST, iblockdata.get(BlockVine.WEST)).set(BlockVine.WEST, iblockdata.get(BlockVine.EAST));

        default:
            return super.a(iblockdata, enumblockmirror);
        }
    }

    public static BlockStateBoolean getDirection(EnumDirection enumdirection) {
        switch (enumdirection) {
        case UP:
            return BlockVine.UP;

        case NORTH:
            return BlockVine.NORTH;

        case SOUTH:
            return BlockVine.SOUTH;

        case WEST:
            return BlockVine.WEST;

        case EAST:
            return BlockVine.EAST;

        default:
            throw new IllegalArgumentException(enumdirection + " is an invalid choice");
        }
    }

    public static int x(IBlockData iblockdata) {
        int i = 0;
        BlockStateBoolean[] ablockstateboolean = BlockVine.f;
        int j = ablockstateboolean.length;

        for (int k = 0; k < j; ++k) {
            BlockStateBoolean blockstateboolean = ablockstateboolean[k];

            if (((Boolean) iblockdata.get(blockstateboolean)).booleanValue()) {
                ++i;
            }
        }

        return i;
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }
}
