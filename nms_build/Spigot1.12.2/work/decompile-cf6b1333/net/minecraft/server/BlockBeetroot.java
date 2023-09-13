package net.minecraft.server;

import java.util.Random;

public class BlockBeetroot extends BlockCrops {

    public static final BlockStateInteger a = BlockStateInteger.of("age", 0, 3);
    private static final AxisAlignedBB[] d = new AxisAlignedBB[] { new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D)};

    public BlockBeetroot() {}

    protected BlockStateInteger e() {
        return BlockBeetroot.a;
    }

    public int g() {
        return 3;
    }

    protected Item h() {
        return Items.BEETROOT_SEEDS;
    }

    protected Item i() {
        return Items.BEETROOT;
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (random.nextInt(3) == 0) {
            this.e(world, blockposition, iblockdata);
        } else {
            super.b(world, blockposition, iblockdata, random);
        }

    }

    protected int b(World world) {
        return super.b(world) / 3;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockBeetroot.a});
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockBeetroot.d[((Integer) iblockdata.get(this.e())).intValue()];
    }
}
