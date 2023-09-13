package net.minecraft.server;

import java.util.Random;

public class BlockFalling extends Block {

    public BlockFalling(Block.Info block_info) {
        super(block_info);
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        world.getBlockTickList().a(blockposition, this, this.a((IWorldReader) world));
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        generatoraccess.getBlockTickList().a(blockposition, this, this.a((IWorldReader) generatoraccess));
        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if (canFallThrough(worldserver.getType(blockposition.down())) && blockposition.getY() >= 0) {
            EntityFallingBlock entityfallingblock = new EntityFallingBlock(worldserver, (double) blockposition.getX() + 0.5D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.5D, worldserver.getType(blockposition));

            this.a(entityfallingblock);
            worldserver.addEntity(entityfallingblock);
        }
    }

    protected void a(EntityFallingBlock entityfallingblock) {}

    @Override
    public int a(IWorldReader iworldreader) {
        return 2;
    }

    public static boolean canFallThrough(IBlockData iblockdata) {
        Block block = iblockdata.getBlock();
        Material material = iblockdata.getMaterial();

        return iblockdata.isAir() || block == Blocks.FIRE || material.isLiquid() || material.isReplaceable();
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1) {}

    public void a(World world, BlockPosition blockposition) {}
}
