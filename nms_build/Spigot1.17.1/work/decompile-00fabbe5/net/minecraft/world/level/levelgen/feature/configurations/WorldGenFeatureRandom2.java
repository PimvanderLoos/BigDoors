package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;

public class WorldGenFeatureRandom2 implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureRandom2> CODEC = ExtraCodecs.a(WorldGenFeatureConfigured.LIST_CODEC).fieldOf("features").xmap(WorldGenFeatureRandom2::new, (worldgenfeaturerandom2) -> {
        return worldgenfeaturerandom2.features;
    }).codec();
    public final List<Supplier<WorldGenFeatureConfigured<?, ?>>> features;

    public WorldGenFeatureRandom2(List<Supplier<WorldGenFeatureConfigured<?, ?>>> list) {
        this.features = list;
    }

    @Override
    public Stream<WorldGenFeatureConfigured<?, ?>> ab_() {
        return this.features.stream().flatMap((supplier) -> {
            return ((WorldGenFeatureConfigured) supplier.get()).d();
        });
    }
}
