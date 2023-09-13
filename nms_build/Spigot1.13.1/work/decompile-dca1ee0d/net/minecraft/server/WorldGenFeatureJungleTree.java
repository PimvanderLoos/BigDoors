package net.minecraft.server;

import java.util.Random;

public class WorldGenFeatureJungleTree extends WorldGenTrees {

    public WorldGenFeatureJungleTree(boolean flag, int i, IBlockData iblockdata, IBlockData iblockdata1, boolean flag1) {
        super(flag, i, iblockdata, iblockdata1, flag1);
    }

    protected int a(Random random) {
        return this.a + random.nextInt(7);
    }
}
