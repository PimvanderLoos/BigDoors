package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureDecoratorConfiguration;

public class WorldGenDecoratorNoiseConfiguration implements WorldGenFeatureDecoratorConfiguration {

    public static final Codec<WorldGenDecoratorNoiseConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("noise_to_count_ratio").forGetter((worldgendecoratornoiseconfiguration) -> {
            return worldgendecoratornoiseconfiguration.noiseToCountRatio;
        }), Codec.DOUBLE.fieldOf("noise_factor").forGetter((worldgendecoratornoiseconfiguration) -> {
            return worldgendecoratornoiseconfiguration.noiseFactor;
        }), Codec.DOUBLE.fieldOf("noise_offset").orElse(0.0D).forGetter((worldgendecoratornoiseconfiguration) -> {
            return worldgendecoratornoiseconfiguration.noiseOffset;
        })).apply(instance, WorldGenDecoratorNoiseConfiguration::new);
    });
    public final int noiseToCountRatio;
    public final double noiseFactor;
    public final double noiseOffset;

    public WorldGenDecoratorNoiseConfiguration(int i, double d0, double d1) {
        this.noiseToCountRatio = i;
        this.noiseFactor = d0;
        this.noiseOffset = d1;
    }
}
