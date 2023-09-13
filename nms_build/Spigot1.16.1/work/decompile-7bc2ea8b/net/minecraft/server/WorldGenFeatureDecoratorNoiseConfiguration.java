package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorldGenFeatureDecoratorNoiseConfiguration implements WorldGenFeatureDecoratorConfiguration {

    public static final Codec<WorldGenFeatureDecoratorNoiseConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.DOUBLE.fieldOf("noise_level").forGetter((worldgenfeaturedecoratornoiseconfiguration) -> {
            return worldgenfeaturedecoratornoiseconfiguration.b;
        }), Codec.INT.fieldOf("below_noise").forGetter((worldgenfeaturedecoratornoiseconfiguration) -> {
            return worldgenfeaturedecoratornoiseconfiguration.c;
        }), Codec.INT.fieldOf("above_noise").forGetter((worldgenfeaturedecoratornoiseconfiguration) -> {
            return worldgenfeaturedecoratornoiseconfiguration.d;
        })).apply(instance, WorldGenFeatureDecoratorNoiseConfiguration::new);
    });
    public final double b;
    public final int c;
    public final int d;

    public WorldGenFeatureDecoratorNoiseConfiguration(double d0, int i, int j) {
        this.b = d0;
        this.c = i;
        this.d = j;
    }
}
