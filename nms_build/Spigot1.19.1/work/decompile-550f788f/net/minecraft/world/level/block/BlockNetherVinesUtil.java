package net.minecraft.world.level.block;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.IBlockData;

public class BlockNetherVinesUtil {

    private static final double BONEMEAL_GROW_PROBABILITY_DECREASE_RATE = 0.826D;
    public static final double GROW_PER_TICK_PROBABILITY = 0.1D;

    public BlockNetherVinesUtil() {}

    public static boolean isValidGrowthState(IBlockData iblockdata) {
        return iblockdata.isAir();
    }

    public static int getBlocksToGrowWhenBonemealed(RandomSource randomsource) {
        double d0 = 1.0D;

        int i;

        for (i = 0; randomsource.nextDouble() < d0; ++i) {
            d0 *= 0.826D;
        }

        return i;
    }
}
