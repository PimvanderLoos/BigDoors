package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;

public class WorldGenFeatureEmptyConfiguration2 implements WorldGenFeatureDecoratorConfiguration {

    public static final Codec<WorldGenFeatureEmptyConfiguration2> a = Codec.unit(() -> {
        return WorldGenFeatureEmptyConfiguration2.c;
    });
    public static final WorldGenFeatureEmptyConfiguration2 c = new WorldGenFeatureEmptyConfiguration2();

    public WorldGenFeatureEmptyConfiguration2() {}
}
