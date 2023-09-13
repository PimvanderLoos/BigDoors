package net.minecraft.server;

import com.mojang.serialization.Codec;

public class WorldGenFeatureRadiusConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureRadiusConfiguration> a = Codec.INT.fieldOf("radius").xmap(WorldGenFeatureRadiusConfiguration::new, (worldgenfeatureradiusconfiguration) -> {
        return worldgenfeatureradiusconfiguration.b;
    }).codec();
    public final int b;

    public WorldGenFeatureRadiusConfiguration(int i) {
        this.b = i;
    }
}
