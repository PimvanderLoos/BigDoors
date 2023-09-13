package net.minecraft.server;

import java.util.Random;

public class BlockFalling extends Block {

    public static boolean instaFall;

    public BlockFalling() {
        super(Material.SAND);
        this.a(CreativeModeTab.b);
    }

    public BlockFalling(Material material) {
        super(material);
    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        world.a(blockposition, (Block) this, this.a(world));
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        world.a(blockposition, (Block) this, this.a(world));
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (!world.isClientSide) {
            this.b(world, blockposition);
        }

    }

    private void b(World world, BlockPosition blockposition) {
        if (x(world.getType(blockposition.down())) && blockposition.getY() >= 0) {
            boolean flag = true;

            if (!BlockFalling.instaFall && world.areChunksLoadedBetween(blockposition.a(-32, -32, -32), blockposition.a(32, 32, 32))) {
                if (!world.isClientSide) {
                    EntityFallingBlock entityfallingblock = new EntityFallingBlock(world, (double) blockposition.getX() + 0.5D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.5D, world.getType(blockposition));

                    this.a(entityfallingblock);
                    world.addEntity(entityfallingblock);
                }
            } else {
                world.setAir(blockposition);

                BlockPosition blockposition1;

                for (blockposition1 = blockposition.down(); x(world.getType(blockposition1)) && blockposition1.getY() > 0; blockposition1 = blockposition1.down()) {
                    ;
                }

                if (blockposition1.getY() > 0) {
                    world.setTypeUpdate(blockposition1.up(), this.getBlockData());
                }
            }

        }
    }

    protected void a(EntityFallingBlock entityfallingblock) {}

    public int a(World world) {
        return 2;
    }

    public static boolean x(IBlockData iblockdata) {
        Block block = iblockdata.getBlock();
        Material material = iblockdata.getMaterial();

        return block == Blocks.FIRE || material == Material.AIR || material == Material.WATER || material == Material.LAVA;
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1) {}

    public void a_(World world, BlockPosition blockposition) {}
}
