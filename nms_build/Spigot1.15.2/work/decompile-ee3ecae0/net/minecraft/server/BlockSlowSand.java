package net.minecraft.server;

import java.util.Random;

public class BlockSlowSand extends Block {

    protected static final VoxelShape a = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D);

    public BlockSlowSand(Block.Info block_info) {
        super(block_info);
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockSlowSand.a;
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        BlockBubbleColumn.a(worldserver, blockposition.up(), false);
    }

    @Override
    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        world.getBlockTickList().a(blockposition, this, this.a((IWorldReader) world));
    }

    @Override
    public boolean isOccluding(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return true;
    }

    @Override
    public int a(IWorldReader iworldreader) {
        return 20;
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        world.getBlockTickList().a(blockposition, this, this.a((IWorldReader) world));
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EntityTypes<?> entitytypes) {
        return true;
    }
}
