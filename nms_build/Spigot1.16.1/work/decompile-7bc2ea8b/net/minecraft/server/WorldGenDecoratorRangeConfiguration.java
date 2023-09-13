package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorldGenDecoratorRangeConfiguration implements WorldGenFeatureDecoratorConfiguration {

    public static final Codec<WorldGenDecoratorRangeConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("min").forGetter((worldgendecoratorrangeconfiguration) -> {
            return worldgendecoratorrangeconfiguration.b;
        }), Codec.INT.fieldOf("max").forGetter((worldgendecoratorrangeconfiguration) -> {
            return worldgendecoratorrangeconfiguration.c;
        })).apply(instance, WorldGenDecoratorRangeConfiguration::new);
    });
    public final int b;
    public final int c;

    public WorldGenDecoratorRangeConfiguration(int i, int j) {
        this.b = i;
        this.c = j;
    }
}
