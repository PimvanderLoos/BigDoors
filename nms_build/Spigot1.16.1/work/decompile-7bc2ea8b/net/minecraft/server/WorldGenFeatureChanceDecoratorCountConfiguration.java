package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorldGenFeatureChanceDecoratorCountConfiguration implements WorldGenFeatureDecoratorConfiguration {

    public static final Codec<WorldGenFeatureChanceDecoratorCountConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("count").forGetter((worldgenfeaturechancedecoratorcountconfiguration) -> {
            return worldgenfeaturechancedecoratorcountconfiguration.b;
        }), Codec.INT.fieldOf("bottom_offset").withDefault(0).forGetter((worldgenfeaturechancedecoratorcountconfiguration) -> {
            return worldgenfeaturechancedecoratorcountconfiguration.c;
        }), Codec.INT.fieldOf("top_offset").withDefault(0).forGetter((worldgenfeaturechancedecoratorcountconfiguration) -> {
            return worldgenfeaturechancedecoratorcountconfiguration.d;
        }), Codec.INT.fieldOf("maximum").withDefault(0).forGetter((worldgenfeaturechancedecoratorcountconfiguration) -> {
            return worldgenfeaturechancedecoratorcountconfiguration.e;
        })).apply(instance, WorldGenFeatureChanceDecoratorCountConfiguration::new);
    });
    public final int b;
    public final int c;
    public final int d;
    public final int e;

    public WorldGenFeatureChanceDecoratorCountConfiguration(int i, int j, int k, int l) {
        this.b = i;
        this.c = j;
        this.d = k;
        this.e = l;
    }
}
