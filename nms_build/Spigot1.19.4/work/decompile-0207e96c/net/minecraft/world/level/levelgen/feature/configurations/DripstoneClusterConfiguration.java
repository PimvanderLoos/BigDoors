package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;

public class DripstoneClusterConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<DripstoneClusterConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.intRange(1, 512).fieldOf("floor_to_ceiling_search_range").forGetter((dripstoneclusterconfiguration) -> {
            return dripstoneclusterconfiguration.floorToCeilingSearchRange;
        }), IntProvider.codec(1, 128).fieldOf("height").forGetter((dripstoneclusterconfiguration) -> {
            return dripstoneclusterconfiguration.height;
        }), IntProvider.codec(1, 128).fieldOf("radius").forGetter((dripstoneclusterconfiguration) -> {
            return dripstoneclusterconfiguration.radius;
        }), Codec.intRange(0, 64).fieldOf("max_stalagmite_stalactite_height_diff").forGetter((dripstoneclusterconfiguration) -> {
            return dripstoneclusterconfiguration.maxStalagmiteStalactiteHeightDiff;
        }), Codec.intRange(1, 64).fieldOf("height_deviation").forGetter((dripstoneclusterconfiguration) -> {
            return dripstoneclusterconfiguration.heightDeviation;
        }), IntProvider.codec(0, 128).fieldOf("dripstone_block_layer_thickness").forGetter((dripstoneclusterconfiguration) -> {
            return dripstoneclusterconfiguration.dripstoneBlockLayerThickness;
        }), FloatProvider.codec(0.0F, 2.0F).fieldOf("density").forGetter((dripstoneclusterconfiguration) -> {
            return dripstoneclusterconfiguration.density;
        }), FloatProvider.codec(0.0F, 2.0F).fieldOf("wetness").forGetter((dripstoneclusterconfiguration) -> {
            return dripstoneclusterconfiguration.wetness;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_dripstone_column_at_max_distance_from_center").forGetter((dripstoneclusterconfiguration) -> {
            return dripstoneclusterconfiguration.chanceOfDripstoneColumnAtMaxDistanceFromCenter;
        }), Codec.intRange(1, 64).fieldOf("max_distance_from_edge_affecting_chance_of_dripstone_column").forGetter((dripstoneclusterconfiguration) -> {
            return dripstoneclusterconfiguration.maxDistanceFromEdgeAffectingChanceOfDripstoneColumn;
        }), Codec.intRange(1, 64).fieldOf("max_distance_from_center_affecting_height_bias").forGetter((dripstoneclusterconfiguration) -> {
            return dripstoneclusterconfiguration.maxDistanceFromCenterAffectingHeightBias;
        })).apply(instance, DripstoneClusterConfiguration::new);
    });
    public final int floorToCeilingSearchRange;
    public final IntProvider height;
    public final IntProvider radius;
    public final int maxStalagmiteStalactiteHeightDiff;
    public final int heightDeviation;
    public final IntProvider dripstoneBlockLayerThickness;
    public final FloatProvider density;
    public final FloatProvider wetness;
    public final float chanceOfDripstoneColumnAtMaxDistanceFromCenter;
    public final int maxDistanceFromEdgeAffectingChanceOfDripstoneColumn;
    public final int maxDistanceFromCenterAffectingHeightBias;

    public DripstoneClusterConfiguration(int i, IntProvider intprovider, IntProvider intprovider1, int j, int k, IntProvider intprovider2, FloatProvider floatprovider, FloatProvider floatprovider1, float f, int l, int i1) {
        this.floorToCeilingSearchRange = i;
        this.height = intprovider;
        this.radius = intprovider1;
        this.maxStalagmiteStalactiteHeightDiff = j;
        this.heightDeviation = k;
        this.dripstoneBlockLayerThickness = intprovider2;
        this.density = floatprovider;
        this.wetness = floatprovider1;
        this.chanceOfDripstoneColumnAtMaxDistanceFromCenter = f;
        this.maxDistanceFromEdgeAffectingChanceOfDripstoneColumn = l;
        this.maxDistanceFromCenterAffectingHeightBias = i1;
    }
}
