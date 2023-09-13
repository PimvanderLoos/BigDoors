package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;

public abstract class RepeatingPlacement extends PlacementModifier {

    public RepeatingPlacement() {}

    protected abstract int count(Random random, BlockPosition blockposition);

    @Override
    public Stream<BlockPosition> getPositions(PlacementContext placementcontext, Random random, BlockPosition blockposition) {
        return IntStream.range(0, this.count(random, blockposition)).mapToObj((i) -> {
            return blockposition;
        });
    }
}
