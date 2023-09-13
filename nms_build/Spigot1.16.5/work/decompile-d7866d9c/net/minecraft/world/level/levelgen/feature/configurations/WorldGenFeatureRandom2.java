package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;

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
