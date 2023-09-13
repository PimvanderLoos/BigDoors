package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorldGenDecoratorHeightAverageConfiguration implements WorldGenFeatureDecoratorConfiguration {

    public static final Codec<WorldGenDecoratorHeightAverageConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("count").forGetter((worldgendecoratorheightaverageconfiguration) -> {
            return worldgendecoratorheightaverageconfiguration.b;
        }), Codec.INT.fieldOf("baseline").forGetter((worldgendecoratorheightaverageconfiguration) -> {
            return worldgendecoratorheightaverageconfiguration.c;
        }), Codec.INT.fieldOf("spread").forGetter((worldgendecoratorheightaverageconfiguration) -> {
            return worldgendecoratorheightaverageconfiguration.d;
        })).apply(instance, WorldGenDecoratorHeightAverageConfiguration::new);
    });
    public final int b;
    public final int c;
    public final int d;

    public WorldGenDecoratorHeightAverageConfiguration(int i, int j, int k) {
        this.b = i;
        this.c = j;
        this.d = k;
    }
}
