package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.HeightMap;

public class SurfaceWaterDepthFilter extends PlacementFilter {

    public static final Codec<SurfaceWaterDepthFilter> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("max_water_depth").forGetter((surfacewaterdepthfilter) -> {
            return surfacewaterdepthfilter.maxWaterDepth;
        })).apply(instance, SurfaceWaterDepthFilter::new);
    });
    private final int maxWaterDepth;

    private SurfaceWaterDepthFilter(int i) {
        this.maxWaterDepth = i;
    }

    public static SurfaceWaterDepthFilter forMaxDepth(int i) {
        return new SurfaceWaterDepthFilter(i);
    }

    @Override
    protected boolean shouldPlace(PlacementContext placementcontext, RandomSource randomsource, BlockPosition blockposition) {
        int i = placementcontext.getHeight(HeightMap.Type.OCEAN_FLOOR, blockposition.getX(), blockposition.getZ());
        int j = placementcontext.getHeight(HeightMap.Type.WORLD_SURFACE, blockposition.getX(), blockposition.getZ());

        return j - i <= this.maxWaterDepth;
    }

    @Override
    public PlacementModifierType<?> type() {
        return PlacementModifierType.SURFACE_WATER_DEPTH_FILTER;
    }
}
