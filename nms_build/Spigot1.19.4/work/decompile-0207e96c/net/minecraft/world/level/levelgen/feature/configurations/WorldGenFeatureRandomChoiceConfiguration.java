package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class WorldGenFeatureRandomChoiceConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureRandomChoiceConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.apply2(WorldGenFeatureRandomChoiceConfiguration::new, WeightedPlacedFeature.CODEC.listOf().fieldOf("features").forGetter((worldgenfeaturerandomchoiceconfiguration) -> {
            return worldgenfeaturerandomchoiceconfiguration.features;
        }), PlacedFeature.CODEC.fieldOf("default").forGetter((worldgenfeaturerandomchoiceconfiguration) -> {
            return worldgenfeaturerandomchoiceconfiguration.defaultFeature;
        }));
    });
    public final List<WeightedPlacedFeature> features;
    public final Holder<PlacedFeature> defaultFeature;

    public WorldGenFeatureRandomChoiceConfiguration(List<WeightedPlacedFeature> list, Holder<PlacedFeature> holder) {
        this.features = list;
        this.defaultFeature = holder;
    }

    @Override
    public Stream<WorldGenFeatureConfigured<?, ?>> getFeatures() {
        return Stream.concat(this.features.stream().flatMap((weightedplacedfeature) -> {
            return ((PlacedFeature) weightedplacedfeature.feature.value()).getFeatures();
        }), ((PlacedFeature) this.defaultFeature.value()).getFeatures());
    }
}
