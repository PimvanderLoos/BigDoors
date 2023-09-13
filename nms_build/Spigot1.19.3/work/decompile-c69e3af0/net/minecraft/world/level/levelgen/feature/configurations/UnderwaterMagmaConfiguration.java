package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class UnderwaterMagmaConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<UnderwaterMagmaConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.intRange(0, 512).fieldOf("floor_search_range").forGetter((underwatermagmaconfiguration) -> {
            return underwatermagmaconfiguration.floorSearchRange;
        }), Codec.intRange(0, 64).fieldOf("placement_radius_around_floor").forGetter((underwatermagmaconfiguration) -> {
            return underwatermagmaconfiguration.placementRadiusAroundFloor;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("placement_probability_per_valid_position").forGetter((underwatermagmaconfiguration) -> {
            return underwatermagmaconfiguration.placementProbabilityPerValidPosition;
        })).apply(instance, UnderwaterMagmaConfiguration::new);
    });
    public final int floorSearchRange;
    public final int placementRadiusAroundFloor;
    public final float placementProbabilityPerValidPosition;

    public UnderwaterMagmaConfiguration(int i, int j, float f) {
        this.floorSearchRange = i;
        this.placementRadiusAroundFloor = j;
        this.placementProbabilityPerValidPosition = f;
    }
}
