package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorldGenDecoratorFrequencyChanceConfiguration implements WorldGenFeatureDecoratorConfiguration {

    public static final Codec<WorldGenDecoratorFrequencyChanceConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("count").forGetter((worldgendecoratorfrequencychanceconfiguration) -> {
            return worldgendecoratorfrequencychanceconfiguration.b;
        }), Codec.FLOAT.fieldOf("chance").forGetter((worldgendecoratorfrequencychanceconfiguration) -> {
            return worldgendecoratorfrequencychanceconfiguration.c;
        })).apply(instance, WorldGenDecoratorFrequencyChanceConfiguration::new);
    });
    public final int b;
    public final float c;

    public WorldGenDecoratorFrequencyChanceConfiguration(int i, float f) {
        this.b = i;
        this.c = f;
    }
}
