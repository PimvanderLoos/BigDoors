package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import java.util.stream.Stream;
import net.minecraft.core.HolderSet;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class WorldGenFeatureRandom2 implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureRandom2> CODEC = ExtraCodecs.nonEmptyHolderSet(PlacedFeature.LIST_CODEC).fieldOf("features").xmap(WorldGenFeatureRandom2::new, (worldgenfeaturerandom2) -> {
        return worldgenfeaturerandom2.features;
    }).codec();
    public final HolderSet<PlacedFeature> features;

    public WorldGenFeatureRandom2(HolderSet<PlacedFeature> holderset) {
        this.features = holderset;
    }

    @Override
    public Stream<WorldGenFeatureConfigured<?, ?>> getFeatures() {
        return this.features.stream().flatMap((holder) -> {
            return ((PlacedFeature) holder.value()).getFeatures();
        });
    }
}
