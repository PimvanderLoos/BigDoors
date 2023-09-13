package net.minecraft.world.level.levelgen.feature.configurations;

import java.util.stream.Stream;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;

public interface WorldGenFeatureConfiguration {

    WorldGenFeatureEmptyConfiguration NONE = WorldGenFeatureEmptyConfiguration.INSTANCE;

    default Stream<WorldGenFeatureConfigured<?, ?>> ab_() {
        return Stream.empty();
    }
}
