package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorldGenFeatureDecoratorNoiseConfiguration implements WorldGenFeatureDecoratorConfiguration {

    public static final Codec<WorldGenFeatureDecoratorNoiseConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.DOUBLE.fieldOf("noise_level").forGetter((worldgenfeaturedecoratornoiseconfiguration) -> {
            return worldgenfeaturedecoratornoiseconfiguration.c;
        }), Codec.INT.fieldOf("below_noise").forGetter((worldgenfeaturedecoratornoiseconfiguration) -> {
            return worldgenfeaturedecoratornoiseconfiguration.d;
        }), Codec.INT.fieldOf("above_noise").forGetter((worldgenfeaturedecoratornoiseconfiguration) -> {
            return worldgenfeaturedecoratornoiseconfiguration.e;
        })).apply(instance, WorldGenFeatureDecoratorNoiseConfiguration::new);
    });
    public final double c;
    public final int d;
    public final int e;

    public WorldGenFeatureDecoratorNoiseConfiguration(double d0, int i, int j) {
        this.c = d0;
        this.d = i;
        this.e = j;
    }
}
