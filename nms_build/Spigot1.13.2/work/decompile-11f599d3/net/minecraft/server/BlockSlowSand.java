package net.minecraft.server;

import java.util.Random;

public class BlockSlowSand extends Block {

    protected static final VoxelShape a = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D);

    public BlockSlowSand(Block.Info block_info) {
        super(block_info);
    }

    public VoxelShape f(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockSlowSand.a;
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        entity.motX *= 0.4D;
        entity.motZ *= 0.4D;
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        BlockBubbleColumn.a(world, blockposition.up(), false);
    }

    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        world.getBlockTickList().a(blockposition, this, this.a((IWorldReader) world));
    }

    public int a(IWorldReader iworldreader) {
        return 20;
    }

    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1) {
        world.getBlockTickList().a(blockposition, this, this.a((IWorldReader) world));
    }

    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
