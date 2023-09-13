package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public record WorldGenFeatureRandomPatchConfiguration(int tries, int xzSpread, int ySpread, Holder<PlacedFeature> feature) implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureRandomPatchConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(ExtraCodecs.POSITIVE_INT.fieldOf("tries").orElse(128).forGetter(WorldGenFeatureRandomPatchConfiguration::tries), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("xz_spread").orElse(7).forGetter(WorldGenFeatureRandomPatchConfiguration::xzSpread), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("y_spread").orElse(3).forGetter(WorldGenFeatureRandomPatchConfiguration::ySpread), PlacedFeature.CODEC.fieldOf("feature").forGetter(WorldGenFeatureRandomPatchConfiguration::feature)).apply(instance, WorldGenFeatureRandomPatchConfiguration::new);
    });
}
