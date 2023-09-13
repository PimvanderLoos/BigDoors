package net.minecraft.server;

import com.mojang.serialization.Codec;

public class WorldGenDecoratorFrequencyConfiguration implements WorldGenFeatureDecoratorConfiguration {

    public static final Codec<WorldGenDecoratorFrequencyConfiguration> a = Codec.INT.fieldOf("count").xmap(WorldGenDecoratorFrequencyConfiguration::new, (worldgendecoratorfrequencyconfiguration) -> {
        return worldgendecoratorfrequencyconfiguration.b;
    }).codec();
    public final int b;

    public WorldGenDecoratorFrequencyConfiguration(int i) {
        this.b = i;
    }
}
