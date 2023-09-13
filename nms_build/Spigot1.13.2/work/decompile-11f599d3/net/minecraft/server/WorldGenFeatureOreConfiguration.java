package net.minecraft.server;

import java.util.function.Predicate;

public class WorldGenFeatureOreConfiguration implements WorldGenFeatureConfiguration {

    public static final Predicate<IBlockData> a = (iblockdata) -> {
        if (iblockdata == null) {
            return false;
        } else {
            Block block = iblockdata.getBlock();

            return block == Blocks.STONE || block == Blocks.GRANITE || block == Blocks.DIORITE || block == Blocks.ANDESITE;
        }
    };
    public final Predicate<IBlockData> b;
    public final int c;
    public final IBlockData d;

    public WorldGenFeatureOreConfiguration(Predicate<IBlockData> predicate, IBlockData iblockdata, int i) {
        this.c = i;
        this.d = iblockdata;
        this.b = predicate;
    }
}
