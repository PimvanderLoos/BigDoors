package net.minecraft.server;

import java.util.Iterator;

public class BlockLadder extends Block {

    public static final BlockStateDirection FACING = BlockFacingHorizontal.FACING;
    protected static final AxisAlignedBB b = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.1875D, 1.0D, 1.0D);
    protected static final AxisAlignedBB c = new AxisAlignedBB(0.8125D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB d = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.1875D);
    protected static final AxisAlignedBB e = new AxisAlignedBB(0.0D, 0.0D, 0.8125D, 1.0D, 1.0D, 1.0D);

    protected BlockLadder() {
        super(Material.ORIENTABLE);
        this.w(this.blockStateList.getBlockData().set(BlockLadder.FACING, EnumDirection.NORTH));
        this.a(CreativeModeTab.c);
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        switch ((EnumDirection) iblockdata.get(BlockLadder.FACING)) {
        case NORTH:
            return BlockLadder.e;

        case SOUTH:
            return BlockLadder.d;

        case WEST:
            return BlockLadder.c;

        case EAST:
        default:
            return BlockLadder.b;
        }
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public boolean canPlace(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        return this.a(world, blockposition.west(), enumdirection) ? true : (this.a(world, blockposition.east(), enumdirection) ? true : (this.a(world, blockposition.north(), enumdirection) ? true : this.a(world, blockposition.south(), enumdirection)));
    }

    private boolean a(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        IBlockData iblockdata = world.getType(blockposition);
        boolean flag = c(iblockdata.getBlock());

        return !flag && iblockdata.d(world, blockposition, enumdirection) == EnumBlockFaceShape.SOLID && !iblockdata.m();
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        if (enumdirection.k().c() && this.a(world, blockposition.shift(enumdirection.opposite()), enumdirection)) {
            return this.getBlockData().set(BlockLadder.FACING, enumdirection);
        } else {
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            EnumDirection enumdirection1;

            do {
                if (!iterator.hasNext()) {
                    return this.getBlockData();
                }

                enumdirection1 = (EnumDirection) iterator.next();
            } while (!this.a(world, blockposition.shift(enumdirection1.opposite()), enumdirection1));

            return this.getBlockData().set(BlockLadder.FACING, enumdirection1);
        }
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockLadder.FACING);

        if (!this.a(world, blockposition.shift(enumdirection.opposite()), enumdirection)) {
            this.b(world, blockposition, iblockdata, 0);
            world.setAir(blockposition);
        }

        super.a(iblockdata, world, blockposition, block, blockposition1);
    }

    public IBlockData fromLegacyData(int i) {
        EnumDirection enumdirection = EnumDirection.fromType1(i);

        if (enumdirection.k() == EnumDirection.EnumAxis.Y) {
            enumdirection = EnumDirection.NORTH;
        }

        return this.getBlockData().set(BlockLadder.FACING, enumdirection);
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((EnumDirection) iblockdata.get(BlockLadder.FACING)).a();
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return iblockdata.set(BlockLadder.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockLadder.FACING)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockLadder.FACING)));
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockLadder.FACING});
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }
}
