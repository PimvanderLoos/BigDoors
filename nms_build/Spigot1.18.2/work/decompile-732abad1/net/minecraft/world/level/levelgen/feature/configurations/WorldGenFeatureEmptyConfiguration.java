package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;

public class WorldGenFeatureEmptyConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureEmptyConfiguration> CODEC = Codec.unit(() -> {
        return WorldGenFeatureEmptyConfiguration.INSTANCE;
    });
    public static final WorldGenFeatureEmptyConfiguration INSTANCE = new WorldGenFeatureEmptyConfiguration();

    public WorldGenFeatureEmptyConfiguration() {}
}
