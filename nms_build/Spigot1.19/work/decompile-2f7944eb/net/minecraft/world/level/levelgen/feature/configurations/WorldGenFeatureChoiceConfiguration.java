package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
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
    public final Holder<PlacedFeature> featureTrue;
    public final Holder<PlacedFeature> featureFalse;

    public WorldGenFeatureChoiceConfiguration(Holder<PlacedFeature> holder, Holder<PlacedFeature> holder1) {
        this.featureTrue = holder;
        this.featureFalse = holder1;
    }

    @Override
    public Stream<WorldGenFeatureConfigured<?, ?>> getFeatures() {
        return Stream.concat(((PlacedFeature) this.featureTrue.value()).getFeatures(), ((PlacedFeature) this.featureFalse.value()).getFeatures());
    }
}
