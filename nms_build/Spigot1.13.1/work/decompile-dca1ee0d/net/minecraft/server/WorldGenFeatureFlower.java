package net.minecraft.server;

import java.util.Random;

public class WorldGenFeatureFlower extends WorldGenFlowers {

    public WorldGenFeatureFlower() {}

    public IBlockData a(Random random, BlockPosition blockposition) {
        return random.nextFloat() > 0.6666667F ? Blocks.DANDELION.getBlockData() : Blocks.POPPY.getBlockData();
    }
}
