package net.minecraft.server;

import java.util.Random;

public class BlockFalling extends Block {

    public static boolean instaFall;

    public BlockFalling(Block.Info block_info) {
        super(block_info);
    }

    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1) {
        world.getBlockTickList().a(blockposition, this, this.a((IWorldReader) world));
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        generatoraccess.getBlockTickList().a(blockposition, this, this.a((IWorldReader) generatoraccess));
        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (!world.isClientSide) {
            this.b(world, blockposition);
        }

    }

    private void b(World world, BlockPosition blockposition) {
        if (canFallThrough(world.getType(blockposition.down())) && blockposition.getY() >= 0) {
            boolean flag = true;

            if (!BlockFalling.instaFall && world.areChunksLoadedBetween(blockposition.a(-32, -32, -32), blockposition.a(32, 32, 32))) {
                if (!world.isClientSide) {
                    EntityFallingBlock entityfallingblock = new EntityFallingBlock(world, (double) blockposition.getX() + 0.5D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.5D, world.getType(blockposition));

                    this.a(entityfallingblock);
                    world.addEntity(entityfallingblock);
                }
            } else {
                if (world.getType(blockposition).getBlock() == this) {
                    world.setAir(blockposition);
                }

                BlockPosition blockposition1;

                for (blockposition1 = blockposition.down(); canFallThrough(world.getType(blockposition1)) && blockposition1.getY() > 0; blockposition1 = blockposition1.down()) {
                    ;
                }

                if (blockposition1.getY() > 0) {
                    world.setTypeUpdate(blockposition1.up(), this.getBlockData());
                }
            }

        }
    }

    protected void a(EntityFallingBlock entityfallingblock) {}

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
