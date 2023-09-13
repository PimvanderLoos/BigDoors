package net.minecraft.world.level.levelgen.placement;

import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;

public abstract class PlacementFilter extends PlacementModifier {

    public PlacementFilter() {}

    @Override
    public final Stream<BlockPosition> getPositions(PlacementContext placementcontext, RandomSource randomsource, BlockPosition blockposition) {
        return this.shouldPlace(placementcontext, randomsource, blockposition) ? Stream.of(blockposition) : Stream.of();
    }

    protected abstract boolean shouldPlace(PlacementContext placementcontext, RandomSource randomsource, BlockPosition blockposition);
}
