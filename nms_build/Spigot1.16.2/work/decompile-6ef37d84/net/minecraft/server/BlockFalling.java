package net.minecraft.server;

import java.util.Random;

public class BlockFalling extends Block {

    public BlockFalling(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        world.getBlockTickList().a(blockposition, this, this.c());
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        generatoraccess.getBlockTickList().a(blockposition, this, this.c());
        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public void tickAlways(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if (canFallThrough(worldserver.getType(blockposition.down())) && blockposition.getY() >= 0) {
            EntityFallingBlock entityfallingblock = new EntityFallingBlock(worldserver, (double) blockposition.getX() + 0.5D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.5D, worldserver.getType(blockposition));

            this.a(entityfallingblock);
            worldserver.addEntity(entityfallingblock);
        }
    }

    protected void a(EntityFallingBlock entityfallingblock) {}

    protected int c() {
        return 2;
    }

    public static boolean canFallThrough(IBlockData iblockdata) {
        Material material = iblockdata.getMaterial();

        return iblockdata.isAir() || iblockdata.a((Tag) TagsBlock.FIRE) || material.isLiquid() || material.isReplaceable();
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1, EntityFallingBlock entityfallingblock) {}

    public void a(World world, BlockPosition blockposition, EntityFallingBlock entityfallingblock) {}
}
