package net.minecraft.server;

import com.mojang.serialization.Codec;

public class WorldGenFeatureEmptyConfiguration2 implements WorldGenFeatureDecoratorConfiguration {

    public static final Codec<WorldGenFeatureEmptyConfiguration2> a = Codec.unit(() -> {
        return WorldGenFeatureEmptyConfiguration2.b;
    });
    public static final WorldGenFeatureEmptyConfiguration2 b = new WorldGenFeatureEmptyConfiguration2();

    public WorldGenFeatureEmptyConfiguration2() {}
}
