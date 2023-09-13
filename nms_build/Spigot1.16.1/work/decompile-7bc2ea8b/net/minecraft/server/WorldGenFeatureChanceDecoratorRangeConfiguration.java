package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorldGenFeatureChanceDecoratorRangeConfiguration implements WorldGenFeatureDecoratorConfiguration {

    public static final Codec<WorldGenFeatureChanceDecoratorRangeConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.FLOAT.fieldOf("chance").forGetter((worldgenfeaturechancedecoratorrangeconfiguration) -> {
            return worldgenfeaturechancedecoratorrangeconfiguration.b;
        }), Codec.INT.fieldOf("bottom_offset").withDefault(0).forGetter((worldgenfeaturechancedecoratorrangeconfiguration) -> {
            return worldgenfeaturechancedecoratorrangeconfiguration.c;
        }), Codec.INT.fieldOf("top_offset").withDefault(0).forGetter((worldgenfeaturechancedecoratorrangeconfiguration) -> {
            return worldgenfeaturechancedecoratorrangeconfiguration.d;
        }), Codec.INT.fieldOf("top").withDefault(0).forGetter((worldgenfeaturechancedecoratorrangeconfiguration) -> {
            return worldgenfeaturechancedecoratorrangeconfiguration.e;
        })).apply(instance, WorldGenFeatureChanceDecoratorRangeConfiguration::new);
    });
    public final float b;
    public final int c;
    public final int d;
    public final int e;

    public WorldGenFeatureChanceDecoratorRangeConfiguration(float f, int i, int j, int k) {
        this.b = f;
        this.c = i;
        this.d = j;
        this.e = k;
    }
}
