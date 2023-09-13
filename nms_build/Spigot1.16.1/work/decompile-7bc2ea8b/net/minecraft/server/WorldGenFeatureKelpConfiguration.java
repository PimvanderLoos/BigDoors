package net.minecraft.server;

import com.mojang.serialization.Codec;

public class WorldGenFeatureKelpConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureKelpConfiguration> a = Codec.INT.fieldOf("count").xmap(WorldGenFeatureKelpConfiguration::new, (worldgenfeaturekelpconfiguration) -> {
        return worldgenfeaturekelpconfiguration.b;
    }).codec();
    public final int b;

    public WorldGenFeatureKelpConfiguration(int i) {
        this.b = i;
    }
}
