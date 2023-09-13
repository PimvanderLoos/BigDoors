package net.minecraft.server;

import javax.annotation.Nullable;

public class BlockFenceGate extends BlockFacingHorizontal {

    public static final BlockStateBoolean OPEN = BlockStateBoolean.of("open");
    public static final BlockStateBoolean POWERED = BlockStateBoolean.of("powered");
    public static final BlockStateBoolean IN_WALL = BlockStateBoolean.of("in_wall");
    protected static final AxisAlignedBB d = new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D);
    protected static final AxisAlignedBB e = new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 1.0D);
    protected static final AxisAlignedBB f = new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 0.8125D, 0.625D);
    protected static final AxisAlignedBB g = new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 0.8125D, 1.0D);
    protected static final AxisAlignedBB B = new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 1.5D, 0.625D);
    protected static final AxisAlignedBB C = new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.5D, 1.0D);

    public BlockFenceGate(BlockWood.EnumLogVariant blockwood_enumlogvariant) {
        super(Material.WOOD, blockwood_enumlogvariant.c());
        this.w(this.blockStateList.getBlockData().set(BlockFenceGate.OPEN, Boolean.valueOf(false)).set(BlockFenceGate.POWERED, Boolean.valueOf(false)).set(BlockFenceGate.IN_WALL, Boolean.valueOf(false)));
        this.a(CreativeModeTab.d);
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        iblockdata = this.updateState(iblockdata, iblockaccess, blockposition);
        return ((Boolean) iblockdata.get(BlockFenceGate.IN_WALL)).booleanValue() ? (((EnumDirection) iblockdata.get(BlockFenceGate.FACING)).k() == EnumDirection.EnumAxis.X ? BlockFenceGate.g : BlockFenceGate.f) : (((EnumDirection) iblockdata.get(BlockFenceGate.FACING)).k() == EnumDirection.EnumAxis.X ? BlockFenceGate.e : BlockFenceGate.d);
    }

    public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        EnumDirection.EnumAxis enumdirection_enumaxis = ((EnumDirection) iblockdata.get(BlockFenceGate.FACING)).k();

        if (enumdirection_enumaxis == EnumDirection.EnumAxis.Z && (iblockaccess.getType(blockposition.west()).getBlock() == Blocks.COBBLESTONE_WALL || iblockaccess.getType(blockposition.east()).getBlock() == Blocks.COBBLESTONE_WALL) || enumdirection_enumaxis == EnumDirection.EnumAxis.X && (iblockaccess.getType(blockposition.north()).getBlock() == Blocks.COBBLESTONE_WALL || iblockaccess.getType(blockposition.south()).getBlock() == Blocks.COBBLESTONE_WALL)) {
            iblockdata = iblockdata.set(BlockFenceGate.IN_WALL, Boolean.valueOf(true));
        }

        return iblockdata;
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return iblockdata.set(BlockFenceGate.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockFenceGate.FACING)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockFenceGate.FACING)));
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        return world.getType(blockposition.down()).getMaterial().isBuildable() ? super.canPlace(world, blockposition) : false;
    }

    @Nullable
    public AxisAlignedBB a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return ((Boolean) iblockdata.get(BlockFenceGate.OPEN)).booleanValue() ? BlockFenceGate.k : (((EnumDirection) iblockdata.get(BlockFenceGate.FACING)).k() == EnumDirection.EnumAxis.Z ? BlockFenceGate.B : BlockFenceGate.C);
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return ((Boolean) iblockaccess.getType(blockposition).get(BlockFenceGate.OPEN)).booleanValue();
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        boolean flag = world.isBlockIndirectlyPowered(blockposition);

        return this.getBlockData().set(BlockFenceGate.FACING, entityliving.getDirection()).set(BlockFenceGate.OPEN, Boolean.valueOf(flag)).set(BlockFenceGate.POWERED, Boolean.valueOf(flag)).set(BlockFenceGate.IN_WALL, Boolean.valueOf(false));
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (((Boolean) iblockdata.get(BlockFenceGate.OPEN)).booleanValue()) {
            iblockdata = iblockdata.set(BlockFenceGate.OPEN, Boolean.valueOf(false));
            world.setTypeAndData(blockposition, iblockdata, 10);
        } else {
            EnumDirection enumdirection1 = EnumDirection.fromAngle((double) entityhuman.yaw);

            if (iblockdata.get(BlockFenceGate.FACING) == enumdirection1.opposite()) {
                iblockdata = iblockdata.set(BlockFenceGate.FACING, enumdirection1);
            }

            iblockdata = iblockdata.set(BlockFenceGate.OPEN, Boolean.valueOf(true));
            world.setTypeAndData(blockposition, iblockdata, 10);
        }

        world.a(entityhuman, ((Boolean) iblockdata.get(BlockFenceGate.OPEN)).booleanValue() ? 1008 : 1014, blockposition, 0);
        return true;
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (!world.isClientSide) {
            boolean flag = world.isBlockIndirectlyPowered(blockposition);

            if (((Boolean) iblockdata.get(BlockFenceGate.POWERED)).booleanValue() != flag) {
                world.setTypeAndData(blockposition, iblockdata.set(BlockFenceGate.POWERED, Boolean.valueOf(flag)).set(BlockFenceGate.OPEN, Boolean.valueOf(flag)), 2);
                if (((Boolean) iblockdata.get(BlockFenceGate.OPEN)).booleanValue() != flag) {
                    world.a((EntityHuman) null, flag ? 1008 : 1014, blockposition, 0);
                }
            }

        }
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockFenceGate.FACING, EnumDirection.fromType2(i)).set(BlockFenceGate.OPEN, Boolean.valueOf((i & 4) != 0)).set(BlockFenceGate.POWERED, Boolean.valueOf((i & 8) != 0));
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | ((EnumDirection) iblockdata.get(BlockFenceGate.FACING)).get2DRotationValue();

        if (((Boolean) iblockdata.get(BlockFenceGate.POWERED)).booleanValue()) {
            i |= 8;
        }

        if (((Boolean) iblockdata.get(BlockFenceGate.OPEN)).booleanValue()) {
            i |= 4;
        }

        return i;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockFenceGate.FACING, BlockFenceGate.OPEN, BlockFenceGate.POWERED, BlockFenceGate.IN_WALL});
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return enumdirection != EnumDirection.UP && enumdirection != EnumDirection.DOWN ? (((EnumDirection) iblockdata.get(BlockFenceGate.FACING)).k() == enumdirection.e().k() ? EnumBlockFaceShape.MIDDLE_POLE : EnumBlockFaceShape.UNDEFINED) : EnumBlockFaceShape.UNDEFINED;
    }
}
