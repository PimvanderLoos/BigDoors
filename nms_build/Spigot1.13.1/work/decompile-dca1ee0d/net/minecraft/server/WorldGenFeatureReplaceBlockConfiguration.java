package net.minecraft.server;

import java.util.function.Predicate;

public class WorldGenFeatureReplaceBlockConfiguration implements WorldGenFeatureConfiguration {

    public final Predicate<IBlockData> a;
    public final IBlockData b;

    public WorldGenFeatureReplaceBlockConfiguration(Predicate<IBlockData> predicate, IBlockData iblockdata) {
        this.a = predicate;
        this.b = iblockdata;
    }
}
