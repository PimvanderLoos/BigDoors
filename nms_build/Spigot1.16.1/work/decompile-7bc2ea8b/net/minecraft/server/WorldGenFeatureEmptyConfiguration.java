package net.minecraft.server;

import com.mojang.serialization.Codec;

public class WorldGenFeatureEmptyConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureEmptyConfiguration> a = Codec.unit(() -> {
        return WorldGenFeatureEmptyConfiguration.b;
    });
    public static final WorldGenFeatureEmptyConfiguration b = new WorldGenFeatureEmptyConfiguration();

    public WorldGenFeatureEmptyConfiguration() {}
}
