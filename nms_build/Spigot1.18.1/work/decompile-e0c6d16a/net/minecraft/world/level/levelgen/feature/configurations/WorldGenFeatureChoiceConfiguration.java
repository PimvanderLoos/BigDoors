package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class WorldGenFeatureChoiceConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureChoiceConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(PlacedFeature.CODEC.fieldOf("feature_true").forGetter((worldgenfeaturechoiceconfiguration) -> {
            return worldgenfeaturechoiceconfiguration.featureTrue;
        }), PlacedFeature.CODEC.fieldOf("feature_false").forGetter((worldgenfeaturechoiceconfiguration) -> {
            return worldgenfeaturechoiceconfiguration.featureFalse;
        })).apply(instance, WorldGenFeatureChoiceConfiguration::new);
    });
    public final Supplier<PlacedFeature> featureTrue;
    public final Supplier<PlacedFeature> featureFalse;

    public WorldGenFeatureChoiceConfiguration(Supplier<PlacedFeature> supplier, Supplier<PlacedFeature> supplier1) {
        this.featureTrue = supplier;
        this.featureFalse = supplier1;
    }

    @Override
    public Stream<WorldGenFeatureConfigured<?, ?>> getFeatures() {
        return Stream.concat(((PlacedFeature) this.featureTrue.get()).getFeatures(), ((PlacedFeature) this.featureFalse.get()).getFeatures());
    }
}
