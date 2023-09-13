package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.HeightMap;

public class SurfaceRelativeThresholdFilter extends PlacementFilter {

    public static final Codec<SurfaceRelativeThresholdFilter> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(HeightMap.Type.CODEC.fieldOf("heightmap").forGetter((surfacerelativethresholdfilter) -> {
            return surfacerelativethresholdfilter.heightmap;
        }), Codec.INT.optionalFieldOf("min_inclusive", Integer.MIN_VALUE).forGetter((surfacerelativethresholdfilter) -> {
            return surfacerelativethresholdfilter.minInclusive;
        }), Codec.INT.optionalFieldOf("max_inclusive", Integer.MAX_VALUE).forGetter((surfacerelativethresholdfilter) -> {
            return surfacerelativethresholdfilter.maxInclusive;
        })).apply(instance, SurfaceRelativeThresholdFilter::new);
    });
    private final HeightMap.Type heightmap;
    private final int minInclusive;
    private final int maxInclusive;

    private SurfaceRelativeThresholdFilter(HeightMap.Type heightmap_type, int i, int j) {
        this.heightmap = heightmap_type;
        this.minInclusive = i;
        this.maxInclusive = j;
    }

    public static SurfaceRelativeThresholdFilter of(HeightMap.Type heightmap_type, int i, int j) {
        return new SurfaceRelativeThresholdFilter(heightmap_type, i, j);
    }

    @Override
    protected boolean shouldPlace(PlacementContext placementcontext, RandomSource randomsource, BlockPosition blockposition) {
        long i = (long) placementcontext.getHeight(this.heightmap, blockposition.getX(), blockposition.getZ());
        long j = i + (long) this.minInclusive;
        long k = i + (long) this.maxInclusive;

        return j <= (long) blockposition.getY() && (long) blockposition.getY() <= k;
    }

    @Override
    public PlacementModifierType<?> type() {
        return PlacementModifierType.SURFACE_RELATIVE_THRESHOLD_FILTER;
    }
}
