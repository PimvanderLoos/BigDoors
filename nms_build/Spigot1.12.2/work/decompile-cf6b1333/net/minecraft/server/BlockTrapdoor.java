package net.minecraft.server;

import javax.annotation.Nullable;

public class BlockTrapdoor extends Block {

    public static final BlockStateDirection FACING = BlockFacingHorizontal.FACING;
    public static final BlockStateBoolean OPEN = BlockStateBoolean.of("open");
    public static final BlockStateEnum<BlockTrapdoor.EnumTrapdoorHalf> HALF = BlockStateEnum.of("half", BlockTrapdoor.EnumTrapdoorHalf.class);
    protected static final AxisAlignedBB d = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.1875D, 1.0D, 1.0D);
    protected static final AxisAlignedBB e = new AxisAlignedBB(0.8125D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB f = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.1875D);
    protected static final AxisAlignedBB g = new AxisAlignedBB(0.0D, 0.0D, 0.8125D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB B = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.1875D, 1.0D);
    protected static final AxisAlignedBB C = new AxisAlignedBB(0.0D, 0.8125D, 0.0D, 1.0D, 1.0D, 1.0D);

    protected BlockTrapdoor(Material material) {
        super(material);
        this.w(this.blockStateList.getBlockData().set(BlockTrapdoor.FACING, EnumDirection.NORTH).set(BlockTrapdoor.OPEN, Boolean.valueOf(false)).set(BlockTrapdoor.HALF, BlockTrapdoor.EnumTrapdoorHalf.BOTTOM));
        this.a(CreativeModeTab.d);
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        AxisAlignedBB axisalignedbb;

        if (((Boolean) iblockdata.get(BlockTrapdoor.OPEN)).booleanValue()) {
            switch ((EnumDirection) iblockdata.get(BlockTrapdoor.FACING)) {
            case NORTH:
            default:
                axisalignedbb = BlockTrapdoor.g;
                break;

            case SOUTH:
                axisalignedbb = BlockTrapdoor.f;
                break;

            case WEST:
                axisalignedbb = BlockTrapdoor.e;
                break;

            case EAST:
                axisalignedbb = BlockTrapdoor.d;
            }
        } else if (iblockdata.get(BlockTrapdoor.HALF) == BlockTrapdoor.EnumTrapdoorHalf.TOP) {
            axisalignedbb = BlockTrapdoor.C;
        } else {
            axisalignedbb = BlockTrapdoor.B;
        }

        return axisalignedbb;
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return !((Boolean) iblockaccess.getType(blockposition).get(BlockTrapdoor.OPEN)).booleanValue();
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (this.material == Material.ORE) {
            return false;
        } else {
            iblockdata = iblockdata.a((IBlockState) BlockTrapdoor.OPEN);
            world.setTypeAndData(blockposition, iblockdata, 2);
            this.a(entityhuman, world, blockposition, ((Boolean) iblockdata.get(BlockTrapdoor.OPEN)).booleanValue());
            return true;
        }
    }

    protected void a(@Nullable EntityHuman entityhuman, World world, BlockPosition blockposition, boolean flag) {
        int i;

        if (flag) {
            i = this.material == Material.ORE ? 1037 : 1007;
            world.a(entityhuman, i, blockposition, 0);
        } else {
            i = this.material == Material.ORE ? 1036 : 1013;
            world.a(entityhuman, i, blockposition, 0);
        }

    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (!world.isClientSide) {
            boolean flag = world.isBlockIndirectlyPowered(blockposition);

            if (flag || block.getBlockData().m()) {
                boolean flag1 = ((Boolean) iblockdata.get(BlockTrapdoor.OPEN)).booleanValue();

                if (flag1 != flag) {
                    world.setTypeAndData(blockposition, iblockdata.set(BlockTrapdoor.OPEN, Boolean.valueOf(flag)), 2);
                    this.a((EntityHuman) null, world, blockposition, flag);
                }
            }

        }
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        IBlockData iblockdata = this.getBlockData();

        if (enumdirection.k().c()) {
            iblockdata = iblockdata.set(BlockTrapdoor.FACING, enumdirection).set(BlockTrapdoor.OPEN, Boolean.valueOf(false));
            iblockdata = iblockdata.set(BlockTrapdoor.HALF, f1 > 0.5F ? BlockTrapdoor.EnumTrapdoorHalf.TOP : BlockTrapdoor.EnumTrapdoorHalf.BOTTOM);
        } else {
            iblockdata = iblockdata.set(BlockTrapdoor.FACING, entityliving.getDirection().opposite()).set(BlockTrapdoor.OPEN, Boolean.valueOf(false));
            iblockdata = iblockdata.set(BlockTrapdoor.HALF, enumdirection == EnumDirection.UP ? BlockTrapdoor.EnumTrapdoorHalf.BOTTOM : BlockTrapdoor.EnumTrapdoorHalf.TOP);
        }

        if (world.isBlockIndirectlyPowered(blockposition)) {
            iblockdata = iblockdata.set(BlockTrapdoor.OPEN, Boolean.valueOf(true));
        }

        return iblockdata;
    }

    public boolean canPlace(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        return true;
    }

    protected static EnumDirection b(int i) {
        switch (i & 3) {
        case 0:
            return EnumDirection.NORTH;

        case 1:
            return EnumDirection.SOUTH;

        case 2:
            return EnumDirection.WEST;

        case 3:
        default:
            return EnumDirection.EAST;
        }
    }

    protected static int a(EnumDirection enumdirection) {
        switch (enumdirection) {
        case NORTH:
            return 0;

        case SOUTH:
            return 1;

        case WEST:
            return 2;

        case EAST:
        default:
            return 3;
        }
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockTrapdoor.FACING, b(i)).set(BlockTrapdoor.OPEN, Boolean.valueOf((i & 4) != 0)).set(BlockTrapdoor.HALF, (i & 8) == 0 ? BlockTrapdoor.EnumTrapdoorHalf.BOTTOM : BlockTrapdoor.EnumTrapdoorHalf.TOP);
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | a((EnumDirection) iblockdata.get(BlockTrapdoor.FACING));

        if (((Boolean) iblockdata.get(BlockTrapdoor.OPEN)).booleanValue()) {
            i |= 4;
        }

        if (iblockdata.get(BlockTrapdoor.HALF) == BlockTrapdoor.EnumTrapdoorHalf.TOP) {
            i |= 8;
        }

        return i;
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return iblockdata.set(BlockTrapdoor.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockTrapdoor.FACING)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockTrapdoor.FACING)));
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockTrapdoor.FACING, BlockTrapdoor.OPEN, BlockTrapdoor.HALF});
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return (enumdirection == EnumDirection.UP && iblockdata.get(BlockTrapdoor.HALF) == BlockTrapdoor.EnumTrapdoorHalf.TOP || enumdirection == EnumDirection.DOWN && iblockdata.get(BlockTrapdoor.HALF) == BlockTrapdoor.EnumTrapdoorHalf.BOTTOM) && !((Boolean) iblockdata.get(BlockTrapdoor.OPEN)).booleanValue() ? EnumBlockFaceShape.SOLID : EnumBlockFaceShape.UNDEFINED;
    }

    public static enum EnumTrapdoorHalf implements INamable {

        TOP("top"), BOTTOM("bottom");

        private final String c;

        private EnumTrapdoorHalf(String s) {
            this.c = s;
        }

        public String toString() {
            return this.c;
        }

        public String getName() {
            return this.c;
        }
    }
}
