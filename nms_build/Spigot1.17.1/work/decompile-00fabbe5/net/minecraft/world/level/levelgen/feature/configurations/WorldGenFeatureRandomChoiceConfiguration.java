package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureRandomChoiceConfigurationWeight;

public class WorldGenFeatureRandomChoiceConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureRandomChoiceConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.apply2(WorldGenFeatureRandomChoiceConfiguration::new, WorldGenFeatureRandomChoiceConfigurationWeight.CODEC.listOf().fieldOf("features").forGetter((worldgenfeaturerandomchoiceconfiguration) -> {
            return worldgenfeaturerandomchoiceconfiguration.features;
        }), WorldGenFeatureConfigured.CODEC.fieldOf("default").forGetter((worldgenfeaturerandomchoiceconfiguration) -> {
            return worldgenfeaturerandomchoiceconfiguration.defaultFeature;
        }));
    });
    public final List<WorldGenFeatureRandomChoiceConfigurationWeight> features;
    public final Supplier<WorldGenFeatureConfigured<?, ?>> defaultFeature;

    public WorldGenFeatureRandomChoiceConfiguration(List<WorldGenFeatureRandomChoiceConfigurationWeight> list, WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured) {
        this(list, () -> {
            return worldgenfeatureconfigured;
        });
    }

    private WorldGenFeatureRandomChoiceConfiguration(List<WorldGenFeatureRandomChoiceConfigurationWeight> list, Supplier<WorldGenFeatureConfigured<?, ?>> supplier) {
        this.features = list;
        this.defaultFeature = supplier;
    }

    @Override
    public Stream<WorldGenFeatureConfigured<?, ?>> ab_() {
        return Stream.concat(this.features.stream().flatMap((worldgenfeaturerandomchoiceconfigurationweight) -> {
            return ((WorldGenFeatureConfigured) worldgenfeaturerandomchoiceconfigurationweight.feature.get()).d();
        }), ((WorldGenFeatureConfigured) this.defaultFeature.get()).d());
    }
}
