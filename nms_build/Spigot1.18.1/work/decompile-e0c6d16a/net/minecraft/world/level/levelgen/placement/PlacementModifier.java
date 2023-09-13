package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;

public abstract class PlacementModifier {

    public static final Codec<PlacementModifier> CODEC = IRegistry.PLACEMENT_MODIFIERS.byNameCodec().dispatch(PlacementModifier::type, PlacementModifierType::codec);

    public PlacementModifier() {}

    public abstract Stream<BlockPosition> getPositions(PlacementContext placementcontext, Random random, BlockPosition blockposition);

    public abstract PlacementModifierType<?> type();
}
