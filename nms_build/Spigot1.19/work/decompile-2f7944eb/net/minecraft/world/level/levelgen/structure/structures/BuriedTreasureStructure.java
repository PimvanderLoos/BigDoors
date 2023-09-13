package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class BuriedTreasureStructure extends Structure {

    public static final Codec<BuriedTreasureStructure> CODEC = simpleCodec(BuriedTreasureStructure::new);

    public BuriedTreasureStructure(Structure.c structure_c) {
        super(structure_c);
    }

    @Override
    public Optional<Structure.b> findGenerationPoint(Structure.a structure_a) {
        return onTopOfChunkCenter(structure_a, HeightMap.Type.OCEAN_FLOOR_WG, (structurepiecesbuilder) -> {
            generatePieces(structurepiecesbuilder, structure_a);
        });
    }

    private static void generatePieces(StructurePiecesBuilder structurepiecesbuilder, Structure.a structure_a) {
        BlockPosition blockposition = new BlockPosition(structure_a.chunkPos().getBlockX(9), 90, structure_a.chunkPos().getBlockZ(9));

        structurepiecesbuilder.addPiece(new BuriedTreasurePieces.a(blockposition));
    }

    @Override
    public StructureType<?> type() {
        return StructureType.BURIED_TREASURE;
    }
}
