package net.minecraft.server;

import java.util.Random;

public class BlockGrassPath extends Block {

    protected static final AxisAlignedBB a = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.9375D, 1.0D);

    protected BlockGrassPath() {
        super(Material.EARTH);
        this.e(255);
    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        super.onPlace(world, blockposition, iblockdata);
        this.b(world, blockposition);
    }

    private void b(World world, BlockPosition blockposition) {
        if (world.getType(blockposition.up()).getMaterial().isBuildable()) {
            BlockSoil.b(world, blockposition);
        }

    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockGrassPath.a;
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Blocks.DIRT.getDropType(Blocks.DIRT.getBlockData().set(BlockDirt.VARIANT, BlockDirt.EnumDirtVariant.DIRT), random, i);
    }

    public ItemStack a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(this);
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        super.a(iblockdata, world, blockposition, block, blockposition1);
        this.b(world, blockposition);
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return enumdirection == EnumDirection.DOWN ? EnumBlockFaceShape.SOLID : EnumBlockFaceShape.UNDEFINED;
    }
}
