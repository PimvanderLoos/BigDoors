package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;

public class RangeConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<RangeConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(HeightProvider.CODEC.fieldOf("height").forGetter((rangeconfiguration) -> {
            return rangeconfiguration.height;
        })).apply(instance, RangeConfiguration::new);
    });
    public final HeightProvider height;

    public RangeConfiguration(HeightProvider heightprovider) {
        this.height = heightprovider;
    }
}
