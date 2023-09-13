package net.minecraft.server;

import java.util.Random;

public class BlockObserver extends BlockDirectional {

    public static final BlockStateBoolean a = BlockStateBoolean.of("powered");

    public BlockObserver() {
        super(Material.STONE);
        this.w(this.blockStateList.getBlockData().set(BlockObserver.FACING, EnumDirection.SOUTH).set(BlockObserver.a, Boolean.valueOf(false)));
        this.a(CreativeModeTab.d);
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockObserver.FACING, BlockObserver.a});
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return iblockdata.set(BlockObserver.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockObserver.FACING)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockObserver.FACING)));
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (((Boolean) iblockdata.get(BlockObserver.a)).booleanValue()) {
            world.setTypeAndData(blockposition, iblockdata.set(BlockObserver.a, Boolean.valueOf(false)), 2);
        } else {
            world.setTypeAndData(blockposition, iblockdata.set(BlockObserver.a, Boolean.valueOf(true)), 2);
            world.a(blockposition, (Block) this, 2);
        }

        this.e(world, blockposition, iblockdata);
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {}

    public void b(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (!world.isClientSide && blockposition.shift((EnumDirection) iblockdata.get(BlockObserver.FACING)).equals(blockposition1)) {
            this.d(iblockdata, world, blockposition);
        }

    }

    private void d(IBlockData iblockdata, World world, BlockPosition blockposition) {
        if (!((Boolean) iblockdata.get(BlockObserver.a)).booleanValue()) {
            if (!world.b(blockposition, (Block) this)) {
                world.a(blockposition, (Block) this, 2);
            }

        }
    }

    protected void e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockObserver.FACING);
        BlockPosition blockposition1 = blockposition.shift(enumdirection.opposite());

        world.a(blockposition1, (Block) this, blockposition);
        world.a(blockposition1, (Block) this, enumdirection);
    }

    public boolean isPowerSource(IBlockData iblockdata) {
        return true;
    }

    public int c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return iblockdata.a(iblockaccess, blockposition, enumdirection);
    }

    public int b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return ((Boolean) iblockdata.get(BlockObserver.a)).booleanValue() && iblockdata.get(BlockObserver.FACING) == enumdirection ? 15 : 0;
    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (!world.isClientSide) {
            if (((Boolean) iblockdata.get(BlockObserver.a)).booleanValue()) {
                this.b(world, blockposition, iblockdata, world.random);
            }

            this.d(iblockdata, world, blockposition);
        }

    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (((Boolean) iblockdata.get(BlockObserver.a)).booleanValue() && world.b(blockposition, (Block) this)) {
            this.e(world, blockposition, iblockdata.set(BlockObserver.a, Boolean.valueOf(false)));
        }

    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        return this.getBlockData().set(BlockObserver.FACING, EnumDirection.a(blockposition, entityliving).opposite());
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | ((EnumDirection) iblockdata.get(BlockObserver.FACING)).a();

        if (((Boolean) iblockdata.get(BlockObserver.a)).booleanValue()) {
            i |= 8;
        }

        return i;
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockObserver.FACING, EnumDirection.fromType1(i & 7));
    }
}
