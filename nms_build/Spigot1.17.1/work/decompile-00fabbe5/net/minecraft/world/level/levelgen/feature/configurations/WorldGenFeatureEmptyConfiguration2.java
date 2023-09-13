package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;

public class WorldGenFeatureEmptyConfiguration2 implements WorldGenFeatureDecoratorConfiguration {

    public static final Codec<WorldGenFeatureEmptyConfiguration2> CODEC = Codec.unit(() -> {
        return WorldGenFeatureEmptyConfiguration2.INSTANCE;
    });
    public static final WorldGenFeatureEmptyConfiguration2 INSTANCE = new WorldGenFeatureEmptyConfiguration2();

    public WorldGenFeatureEmptyConfiguration2() {}
}
