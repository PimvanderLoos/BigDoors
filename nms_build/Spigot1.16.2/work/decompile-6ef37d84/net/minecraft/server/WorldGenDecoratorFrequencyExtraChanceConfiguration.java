package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorldGenDecoratorFrequencyExtraChanceConfiguration implements WorldGenFeatureDecoratorConfiguration {

    public static final Codec<WorldGenDecoratorFrequencyExtraChanceConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("count").forGetter((worldgendecoratorfrequencyextrachanceconfiguration) -> {
            return worldgendecoratorfrequencyextrachanceconfiguration.c;
        }), Codec.FLOAT.fieldOf("extra_chance").forGetter((worldgendecoratorfrequencyextrachanceconfiguration) -> {
            return worldgendecoratorfrequencyextrachanceconfiguration.d;
        }), Codec.INT.fieldOf("extra_count").forGetter((worldgendecoratorfrequencyextrachanceconfiguration) -> {
            return worldgendecoratorfrequencyextrachanceconfiguration.e;
        })).apply(instance, WorldGenDecoratorFrequencyExtraChanceConfiguration::new);
    });
    public final int c;
    public final float d;
    public final int e;

    public WorldGenDecoratorFrequencyExtraChanceConfiguration(int i, float f, int j) {
        this.c = i;
        this.d = f;
        this.e = j;
    }
}
