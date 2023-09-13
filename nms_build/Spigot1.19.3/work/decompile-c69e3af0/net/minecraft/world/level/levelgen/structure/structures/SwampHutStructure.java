package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class SwampHutStructure extends Structure {

    public static final Codec<SwampHutStructure> CODEC = simpleCodec(SwampHutStructure::new);

    public SwampHutStructure(Structure.c structure_c) {
        super(structure_c);
    }

    @Override
    public Optional<Structure.b> findGenerationPoint(Structure.a structure_a) {
        return onTopOfChunkCenter(structure_a, HeightMap.Type.WORLD_SURFACE_WG, (structurepiecesbuilder) -> {
            generatePieces(structurepiecesbuilder, structure_a);
        });
    }

    private static void generatePieces(StructurePiecesBuilder structurepiecesbuilder, Structure.a structure_a) {
        structurepiecesbuilder.addPiece(new SwampHutPiece(structure_a.random(), structure_a.chunkPos().getMinBlockX(), structure_a.chunkPos().getMinBlockZ()));
    }

    @Override
    public StructureType<?> type() {
        return StructureType.SWAMP_HUT;
    }
}
