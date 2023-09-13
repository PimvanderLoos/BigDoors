package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorldGenDecoratorFrequencyExtraChanceConfiguration implements WorldGenFeatureDecoratorConfiguration {

    public static final Codec<WorldGenDecoratorFrequencyExtraChanceConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("count").forGetter((worldgendecoratorfrequencyextrachanceconfiguration) -> {
            return worldgendecoratorfrequencyextrachanceconfiguration.b;
        }), Codec.FLOAT.fieldOf("extra_chance").forGetter((worldgendecoratorfrequencyextrachanceconfiguration) -> {
            return worldgendecoratorfrequencyextrachanceconfiguration.c;
        }), Codec.INT.fieldOf("extra_count").forGetter((worldgendecoratorfrequencyextrachanceconfiguration) -> {
            return worldgendecoratorfrequencyextrachanceconfiguration.d;
        })).apply(instance, WorldGenDecoratorFrequencyExtraChanceConfiguration::new);
    });
    public final int b;
    public final float c;
    public final int d;

    public WorldGenDecoratorFrequencyExtraChanceConfiguration(int i, float f, int j) {
        this.b = i;
        this.c = f;
        this.d = j;
    }
}
