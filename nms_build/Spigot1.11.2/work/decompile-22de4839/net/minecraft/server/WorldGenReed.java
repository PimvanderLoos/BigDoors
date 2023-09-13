package net.minecraft.server;

import java.util.Random;

public class WorldGenReed extends WorldGenerator {

    public WorldGenReed() {}

    public boolean generate(World world, Random random, BlockPosition blockposition) {
        for (int i = 0; i < 20; ++i) {
            BlockPosition blockposition1 = blockposition.a(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));

            if (world.isEmpty(blockposition1)) {
                BlockPosition blockposition2 = blockposition1.down();

                if (world.getType(blockposition2.west()).getMaterial() == Material.WATER || world.getType(blockposition2.east()).getMaterial() == Material.WATER || world.getType(blockposition2.north()).getMaterial() == Material.WATER || world.getType(blockposition2.south()).getMaterial() == Material.WATER) {
                    int j = 2 + random.nextInt(random.nextInt(3) + 1);

                    for (int k = 0; k < j; ++k) {
                        if (Blocks.REEDS.b(world, blockposition1)) {
                            world.setTypeAndData(blockposition1.up(k), Blocks.REEDS.getBlockData(), 2);
                        }
                    }
                }
            }
        }

        return true;
    }
}
