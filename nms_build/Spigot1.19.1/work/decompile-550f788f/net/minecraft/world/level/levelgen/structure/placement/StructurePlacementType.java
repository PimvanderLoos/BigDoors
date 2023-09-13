package net.minecraft.world.level.levelgen.structure.placement;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;

public interface StructurePlacementType<SP extends StructurePlacement> {

    StructurePlacementType<RandomSpreadStructurePlacement> RANDOM_SPREAD = register("random_spread", RandomSpreadStructurePlacement.CODEC);
    StructurePlacementType<ConcentricRingsStructurePlacement> CONCENTRIC_RINGS = register("concentric_rings", ConcentricRingsStructurePlacement.CODEC);

    Codec<SP> codec();

    private static <SP extends StructurePlacement> StructurePlacementType<SP> register(String s, Codec<SP> codec) {
        return (StructurePlacementType) IRegistry.register(IRegistry.STRUCTURE_PLACEMENT_TYPE, s, () -> {
            return codec;
        });
    }
}
