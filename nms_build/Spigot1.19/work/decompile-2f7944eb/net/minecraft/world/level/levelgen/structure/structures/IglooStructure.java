package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class IglooStructure extends Structure {

    public static final Codec<IglooStructure> CODEC = simpleCodec(IglooStructure::new);

    public IglooStructure(Structure.c structure_c) {
        super(structure_c);
    }

    @Override
    public Optional<Structure.b> findGenerationPoint(Structure.a structure_a) {
        return onTopOfChunkCenter(structure_a, HeightMap.Type.WORLD_SURFACE_WG, (structurepiecesbuilder) -> {
            this.generatePieces(structurepiecesbuilder, structure_a);
        });
    }

    private void generatePieces(StructurePiecesBuilder structurepiecesbuilder, Structure.a structure_a) {
        ChunkCoordIntPair chunkcoordintpair = structure_a.chunkPos();
        SeededRandom seededrandom = structure_a.random();
        BlockPosition blockposition = new BlockPosition(chunkcoordintpair.getMinBlockX(), 90, chunkcoordintpair.getMinBlockZ());
        EnumBlockRotation enumblockrotation = EnumBlockRotation.getRandom(seededrandom);

        IglooPieces.addPieces(structure_a.structureTemplateManager(), blockposition, enumblockrotation, structurepiecesbuilder, seededrandom);
    }

    @Override
    public StructureType<?> type() {
        return StructureType.IGLOO;
    }
}
