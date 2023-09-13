package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureDecoratorConfiguration;

public class WaterDepthThresholdConfiguration implements WorldGenFeatureDecoratorConfiguration {

    public static final Codec<WaterDepthThresholdConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("max_water_depth").forGetter((waterdepththresholdconfiguration) -> {
            return waterdepththresholdconfiguration.maxWaterDepth;
        })).apply(instance, WaterDepthThresholdConfiguration::new);
    });
    public final int maxWaterDepth;

    public WaterDepthThresholdConfiguration(int i) {
        this.maxWaterDepth = i;
    }
}
