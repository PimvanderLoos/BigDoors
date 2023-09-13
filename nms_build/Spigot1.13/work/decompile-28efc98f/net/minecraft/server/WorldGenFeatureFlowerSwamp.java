package net.minecraft.server;

import java.util.Random;

public class WorldGenFeatureFlowerSwamp extends WorldGenFlowers {

    public WorldGenFeatureFlowerSwamp() {}

    public IBlockData a(Random random, BlockPosition blockposition) {
        return Blocks.BLUE_ORCHID.getBlockData();
    }
}
