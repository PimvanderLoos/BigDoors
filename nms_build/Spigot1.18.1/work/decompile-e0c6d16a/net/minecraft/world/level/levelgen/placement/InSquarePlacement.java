package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;

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
    public Stream<BlockPosition> getPositions(PlacementContext placementcontext, Random random, BlockPosition blockposition) {
        int i = random.nextInt(16) + blockposition.getX();
        int j = random.nextInt(16) + blockposition.getZ();

        return Stream.of(new BlockPosition(i, blockposition.getY(), j));
    }

    @Override
    public PlacementModifierType<?> type() {
        return PlacementModifierType.IN_SQUARE;
    }
}
