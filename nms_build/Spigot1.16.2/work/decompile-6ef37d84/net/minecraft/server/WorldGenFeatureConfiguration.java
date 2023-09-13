package net.minecraft.server;

import java.util.stream.Stream;

public interface WorldGenFeatureConfiguration {

    WorldGenFeatureEmptyConfiguration k = WorldGenFeatureEmptyConfiguration.b;

    default Stream<WorldGenFeatureConfigured<?, ?>> an_() {
        return Stream.empty();
    }
}
