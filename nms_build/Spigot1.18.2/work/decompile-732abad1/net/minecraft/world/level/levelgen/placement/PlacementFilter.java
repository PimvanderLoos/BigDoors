package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;

public abstract class PlacementFilter extends PlacementModifier {

    public PlacementFilter() {}

    @Override
    public final Stream<BlockPosition> getPositions(PlacementContext placementcontext, Random random, BlockPosition blockposition) {
        return this.shouldPlace(placementcontext, random, blockposition) ? Stream.of(blockposition) : Stream.of();
    }

    protected abstract boolean shouldPlace(PlacementContext placementcontext, Random random, BlockPosition blockposition);
}
