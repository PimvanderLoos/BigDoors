package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class WorldGenFeatureRandom2 implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureRandom2> a = WorldGenFeatureConfigured.c.fieldOf("features").xmap(WorldGenFeatureRandom2::new, (worldgenfeaturerandom2) -> {
        return worldgenfeaturerandom2.b;
    }).codec();
    public final List<Supplier<WorldGenFeatureConfigured<?, ?>>> b;

    public WorldGenFeatureRandom2(List<Supplier<WorldGenFeatureConfigured<?, ?>>> list) {
        this.b = list;
    }

    @Override
    public Stream<WorldGenFeatureConfigured<?, ?>> an_() {
        return this.b.stream().flatMap((supplier) -> {
            return ((WorldGenFeatureConfigured) supplier.get()).d();
        });
    }
}
