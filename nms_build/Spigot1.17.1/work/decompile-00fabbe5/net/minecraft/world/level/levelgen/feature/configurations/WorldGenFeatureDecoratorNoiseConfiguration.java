package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorldGenFeatureDecoratorNoiseConfiguration implements WorldGenFeatureDecoratorConfiguration {

    public static final Codec<WorldGenFeatureDecoratorNoiseConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.DOUBLE.fieldOf("noise_level").forGetter((worldgenfeaturedecoratornoiseconfiguration) -> {
            return worldgenfeaturedecoratornoiseconfiguration.noiseLevel;
        }), Codec.INT.fieldOf("below_noise").forGetter((worldgenfeaturedecoratornoiseconfiguration) -> {
            return worldgenfeaturedecoratornoiseconfiguration.belowNoise;
        }), Codec.INT.fieldOf("above_noise").forGetter((worldgenfeaturedecoratornoiseconfiguration) -> {
            return worldgenfeaturedecoratornoiseconfiguration.aboveNoise;
        })).apply(instance, WorldGenFeatureDecoratorNoiseConfiguration::new);
    });
    public final double noiseLevel;
    public final int belowNoise;
    public final int aboveNoise;

    public WorldGenFeatureDecoratorNoiseConfiguration(double d0, int i, int j) {
        this.noiseLevel = d0;
        this.belowNoise = i;
        this.aboveNoise = j;
    }
}
