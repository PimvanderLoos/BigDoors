package net.minecraft.server;

import java.util.Random;

public class BlockStationary extends BlockFluids {

    protected BlockStationary(Material material) {
        super(material);
        this.a(false);
        if (material == Material.LAVA) {
            this.a(true);
        }

    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (!this.e(world, blockposition, iblockdata)) {
            this.f(world, blockposition, iblockdata);
        }

    }

    private void f(World world, BlockPosition blockposition, IBlockData iblockdata) {
        BlockFlowing blockflowing = a(this.material);

        world.setTypeAndData(blockposition, blockflowing.getBlockData().set(BlockStationary.LEVEL, iblockdata.get(BlockStationary.LEVEL)), 2);
        world.a(blockposition, (Block) blockflowing, this.a(world));
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (this.material == Material.LAVA) {
            if (world.getGameRules().getBoolean("doFireTick")) {
                int i = random.nextInt(3);

                if (i > 0) {
                    BlockPosition blockposition1 = blockposition;

                    for (int j = 0; j < i; ++j) {
                        blockposition1 = blockposition1.a(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);
                        if (blockposition1.getY() >= 0 && blockposition1.getY() < 256 && !world.isLoaded(blockposition1)) {
                            return;
                        }

                        Block block = world.getType(blockposition1).getBlock();

                        if (block.material == Material.AIR) {
                            if (this.c(world, blockposition1)) {
                                world.setTypeUpdate(blockposition1, Blocks.FIRE.getBlockData());
                                return;
                            }
                        } else if (block.material.isSolid()) {
                            return;
                        }
                    }
                } else {
                    for (int k = 0; k < 3; ++k) {
                        BlockPosition blockposition2 = blockposition.a(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);

                        if (blockposition2.getY() >= 0 && blockposition2.getY() < 256 && !world.isLoaded(blockposition2)) {
                            return;
                        }

                        if (world.isEmpty(blockposition2.up()) && this.d(world, blockposition2)) {
                            world.setTypeUpdate(blockposition2.up(), Blocks.FIRE.getBlockData());
                        }
                    }
                }

            }
        }
    }

    protected boolean c(World world, BlockPosition blockposition) {
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            if (this.d(world, blockposition.shift(enumdirection))) {
                return true;
            }
        }

        return false;
    }

    private boolean d(World world, BlockPosition blockposition) {
        return blockposition.getY() >= 0 && blockposition.getY() < 256 && !world.isLoaded(blockposition) ? false : world.getType(blockposition).getMaterial().isBurnable();
    }
}
