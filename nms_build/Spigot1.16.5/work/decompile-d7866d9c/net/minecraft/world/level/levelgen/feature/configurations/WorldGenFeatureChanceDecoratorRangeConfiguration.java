package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorldGenFeatureChanceDecoratorRangeConfiguration implements WorldGenFeatureDecoratorConfiguration {

    public static final Codec<WorldGenFeatureChanceDecoratorRangeConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("bottom_offset").orElse(0).forGetter((worldgenfeaturechancedecoratorrangeconfiguration) -> {
            return worldgenfeaturechancedecoratorrangeconfiguration.c;
        }), Codec.INT.fieldOf("top_offset").orElse(0).forGetter((worldgenfeaturechancedecoratorrangeconfiguration) -> {
            return worldgenfeaturechancedecoratorrangeconfiguration.d;
        }), Codec.INT.fieldOf("maximum").orElse(0).forGetter((worldgenfeaturechancedecoratorrangeconfiguration) -> {
            return worldgenfeaturechancedecoratorrangeconfiguration.e;
        })).apply(instance, WorldGenFeatureChanceDecoratorRangeConfiguration::new);
    });
    public final int c;
    public final int d;
    public final int e;

    public WorldGenFeatureChanceDecoratorRangeConfiguration(int i, int j, int k) {
        this.c = i;
        this.d = j;
        this.e = k;
    }
}
