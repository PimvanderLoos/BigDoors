package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class SmallDripstoneConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<SmallDripstoneConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.intRange(0, 100).fieldOf("max_placements").orElse(5).forGetter((smalldripstoneconfiguration) -> {
            return smalldripstoneconfiguration.maxPlacements;
        }), Codec.intRange(0, 20).fieldOf("empty_space_search_radius").orElse(10).forGetter((smalldripstoneconfiguration) -> {
            return smalldripstoneconfiguration.emptySpaceSearchRadius;
        }), Codec.intRange(0, 20).fieldOf("max_offset_from_origin").orElse(2).forGetter((smalldripstoneconfiguration) -> {
            return smalldripstoneconfiguration.maxOffsetFromOrigin;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_taller_dripstone").orElse(0.2F).forGetter((smalldripstoneconfiguration) -> {
            return smalldripstoneconfiguration.chanceOfTallerDripstone;
        })).apply(instance, SmallDripstoneConfiguration::new);
    });
    public final int maxPlacements;
    public final int emptySpaceSearchRadius;
    public final int maxOffsetFromOrigin;
    public final float chanceOfTallerDripstone;

    public SmallDripstoneConfiguration(int i, int j, int k, float f) {
        this.maxPlacements = i;
        this.emptySpaceSearchRadius = j;
        this.maxOffsetFromOrigin = k;
        this.chanceOfTallerDripstone = f;
    }
}
