package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;

public class WorldGenFeatureChoiceConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureChoiceConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureConfigured.CODEC.fieldOf("feature_true").forGetter((worldgenfeaturechoiceconfiguration) -> {
            return worldgenfeaturechoiceconfiguration.featureTrue;
        }), WorldGenFeatureConfigured.CODEC.fieldOf("feature_false").forGetter((worldgenfeaturechoiceconfiguration) -> {
            return worldgenfeaturechoiceconfiguration.featureFalse;
        })).apply(instance, WorldGenFeatureChoiceConfiguration::new);
    });
    public final Supplier<WorldGenFeatureConfigured<?, ?>> featureTrue;
    public final Supplier<WorldGenFeatureConfigured<?, ?>> featureFalse;

    public WorldGenFeatureChoiceConfiguration(Supplier<WorldGenFeatureConfigured<?, ?>> supplier, Supplier<WorldGenFeatureConfigured<?, ?>> supplier1) {
        this.featureTrue = supplier;
        this.featureFalse = supplier1;
    }

    @Override
    public Stream<WorldGenFeatureConfigured<?, ?>> ab_() {
        return Stream.concat(((WorldGenFeatureConfigured) this.featureTrue.get()).d(), ((WorldGenFeatureConfigured) this.featureFalse.get()).d());
    }
}
