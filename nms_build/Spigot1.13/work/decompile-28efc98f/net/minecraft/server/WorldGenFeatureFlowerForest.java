package net.minecraft.server;

import java.util.Random;

public class WorldGenFeatureFlowerForest extends WorldGenFlowers {

    private static final Block[] a = new Block[] { Blocks.DANDELION, Blocks.POPPY, Blocks.BLUE_ORCHID, Blocks.ALLIUM, Blocks.AZURE_BLUET, Blocks.RED_TULIP, Blocks.ORANGE_TULIP, Blocks.WHITE_TULIP, Blocks.PINK_TULIP, Blocks.OXEYE_DAISY};

    public WorldGenFeatureFlowerForest() {}

    public IBlockData a(Random random, BlockPosition blockposition) {
        double d0 = MathHelper.a((1.0D + BiomeBase.aJ.a((double) blockposition.getX() / 48.0D, (double) blockposition.getZ() / 48.0D)) / 2.0D, 0.0D, 0.9999D);
        Block block = WorldGenFeatureFlowerForest.a[(int) (d0 * (double) WorldGenFeatureFlowerForest.a.length)];

        return block == Blocks.BLUE_ORCHID ? Blocks.POPPY.getBlockData() : block.getBlockData();
    }
}
