package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.world.level.block.state.IBlockData;

public class BlockNetherVinesUtil {

    public static boolean a(IBlockData iblockdata) {
        return iblockdata.isAir();
    }

    public static int a(Random random) {
        double d0 = 1.0D;

        int i;

        for (i = 0; random.nextDouble() < d0; ++i) {
            d0 *= 0.826D;
        }

        return i;
    }
}
