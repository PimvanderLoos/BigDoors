package net.minecraft.server;

import java.util.Random;

public class BiomeVoidDecorator extends BiomeDecorator {

    public BiomeVoidDecorator() {}

    public void a(World world, Random random, BiomeBase biomebase, BlockPosition blockposition) {
        BlockPosition blockposition1 = world.getSpawn();
        boolean flag = true;
        double d0 = blockposition1.n(blockposition.a(8, blockposition1.getY(), 8));

        if (d0 <= 1024.0D) {
            BlockPosition blockposition2 = new BlockPosition(blockposition1.getX() - 16, blockposition1.getY() - 1, blockposition1.getZ() - 16);
            BlockPosition blockposition3 = new BlockPosition(blockposition1.getX() + 16, blockposition1.getY() - 1, blockposition1.getZ() + 16);
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(blockposition2);

            for (int i = blockposition.getZ(); i < blockposition.getZ() + 16; ++i) {
                for (int j = blockposition.getX(); j < blockposition.getX() + 16; ++j) {
                    if (i >= blockposition2.getZ() && i <= blockposition3.getZ() && j >= blockposition2.getX() && j <= blockposition3.getX()) {
                        blockposition_mutableblockposition.c(j, blockposition_mutableblockposition.getY(), i);
                        if (blockposition1.getX() == j && blockposition1.getZ() == i) {
                            world.setTypeAndData(blockposition_mutableblockposition, Blocks.COBBLESTONE.getBlockData(), 2);
                        } else {
                            world.setTypeAndData(blockposition_mutableblockposition, Blocks.STONE.getBlockData(), 2);
                        }
                    }
                }
            }

        }
    }
}
