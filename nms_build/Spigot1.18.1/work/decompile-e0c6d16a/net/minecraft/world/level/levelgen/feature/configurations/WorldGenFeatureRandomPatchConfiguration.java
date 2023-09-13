package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public record WorldGenFeatureRandomPatchConfiguration(int b, int c, int d, Supplier<PlacedFeature> e) implements WorldGenFeatureConfiguration {

    private final int tries;
    private final int xzSpread;
    private final int ySpread;
    private final Supplier<PlacedFeature> feature;
    public static final Codec<WorldGenFeatureRandomPatchConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(ExtraCodecs.POSITIVE_INT.fieldOf("tries").orElse(128).forGetter(WorldGenFeatureRandomPatchConfiguration::tries), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("xz_spread").orElse(7).forGetter(WorldGenFeatureRandomPatchConfiguration::xzSpread), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("y_spread").orElse(3).forGetter(WorldGenFeatureRandomPatchConfiguration::ySpread), PlacedFeature.CODEC.fieldOf("feature").forGetter(WorldGenFeatureRandomPatchConfiguration::feature)).apply(instance, WorldGenFeatureRandomPatchConfiguration::new);
    });

    public WorldGenFeatureRandomPatchConfiguration(int i, int j, int k, Supplier<PlacedFeature> supplier) {
        this.tries = i;
        this.xzSpread = j;
        this.ySpread = k;
        this.feature = supplier;
    }

    public int tries() {
        return this.tries;
    }

    public int xzSpread() {
        return this.xzSpread;
    }

    public int ySpread() {
        return this.ySpread;
    }

    public Supplier<PlacedFeature> feature() {
        return this.feature;
    }
}
