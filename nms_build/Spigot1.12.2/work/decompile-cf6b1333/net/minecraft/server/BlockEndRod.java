package net.minecraft.server;

public class BlockEndRod extends BlockDirectional {

    protected static final AxisAlignedBB a = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D);
    protected static final AxisAlignedBB b = new AxisAlignedBB(0.375D, 0.375D, 0.0D, 0.625D, 0.625D, 1.0D);
    protected static final AxisAlignedBB c = new AxisAlignedBB(0.0D, 0.375D, 0.375D, 1.0D, 0.625D, 0.625D);

    protected BlockEndRod() {
        super(Material.ORIENTABLE);
        this.w(this.blockStateList.getBlockData().set(BlockEndRod.FACING, EnumDirection.UP));
        this.a(CreativeModeTab.c);
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return iblockdata.set(BlockEndRod.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockEndRod.FACING)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.set(BlockEndRod.FACING, enumblockmirror.b((EnumDirection) iblockdata.get(BlockEndRod.FACING)));
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        switch (((EnumDirection) iblockdata.get(BlockEndRod.FACING)).k()) {
        case X:
        default:
            return BlockEndRod.c;

        case Z:
            return BlockEndRod.b;

        case Y:
            return BlockEndRod.a;
        }
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        return true;
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        IBlockData iblockdata = world.getType(blockposition.shift(enumdirection.opposite()));

        if (iblockdata.getBlock() == Blocks.END_ROD) {
            EnumDirection enumdirection1 = (EnumDirection) iblockdata.get(BlockEndRod.FACING);

            if (enumdirection1 == enumdirection) {
                return this.getBlockData().set(BlockEndRod.FACING, enumdirection.opposite());
            }
        }

        return this.getBlockData().set(BlockEndRod.FACING, enumdirection);
    }

    public IBlockData fromLegacyData(int i) {
        IBlockData iblockdata = this.getBlockData();

        iblockdata = iblockdata.set(BlockEndRod.FACING, EnumDirection.fromType1(i));
        return iblockdata;
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((EnumDirection) iblockdata.get(BlockEndRod.FACING)).a();
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockEndRod.FACING});
    }

    public EnumPistonReaction h(IBlockData iblockdata) {
        return EnumPistonReaction.NORMAL;
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }
}
