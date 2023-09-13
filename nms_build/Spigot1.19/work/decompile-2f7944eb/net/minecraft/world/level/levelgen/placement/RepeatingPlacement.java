package net.minecraft.world.level.levelgen.placement;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;

public abstract class RepeatingPlacement extends PlacementModifier {

    public RepeatingPlacement() {}

    protected abstract int count(RandomSource randomsource, BlockPosition blockposition);

    @Override
    public Stream<BlockPosition> getPositions(PlacementContext placementcontext, RandomSource randomsource, BlockPosition blockposition) {
        return IntStream.range(0, this.count(randomsource, blockposition)).mapToObj((i) -> {
            return blockposition;
        });
    }
}
