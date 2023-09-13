package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;

public class InSquarePlacement extends PlacementModifier {

    private static final InSquarePlacement INSTANCE = new InSquarePlacement();
    public static final Codec<InSquarePlacement> CODEC = Codec.unit(() -> {
        return InSquarePlacement.INSTANCE;
    });

    public InSquarePlacement() {}

    public static InSquarePlacement spread() {
        return InSquarePlacement.INSTANCE;
    }

    @Override
    public Stream<BlockPosition> getPositions(PlacementContext placementcontext, RandomSource randomsource, BlockPosition blockposition) {
        int i = randomsource.nextInt(16) + blockposition.getX();
        int j = randomsource.nextInt(16) + blockposition.getZ();

        return Stream.of(new BlockPosition(i, blockposition.getY(), j));
    }

    @Override
    public PlacementModifierType<?> type() {
        return PlacementModifierType.IN_SQUARE;
    }
}
