package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;

public class WorldGenFeatureChanceDecoratorRangeConfiguration implements WorldGenFeatureDecoratorConfiguration, WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureChanceDecoratorRangeConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(HeightProvider.CODEC.fieldOf("height").forGetter((worldgenfeaturechancedecoratorrangeconfiguration) -> {
            return worldgenfeaturechancedecoratorrangeconfiguration.height;
        })).apply(instance, WorldGenFeatureChanceDecoratorRangeConfiguration::new);
    });
    public final HeightProvider height;

    public WorldGenFeatureChanceDecoratorRangeConfiguration(HeightProvider heightprovider) {
        this.height = heightprovider;
    }
}
