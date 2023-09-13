package net.minecraft.world.level.levelgen.structure;

import java.util.Optional;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public abstract class SinglePieceStructure extends Structure {

    private final SinglePieceStructure.a constructor;
    private final int width;
    private final int depth;

    protected SinglePieceStructure(SinglePieceStructure.a singlepiecestructure_a, int i, int j, Structure.c structure_c) {
        super(structure_c);
        this.constructor = singlepiecestructure_a;
        this.width = i;
        this.depth = j;
    }

    @Override
    public Optional<Structure.b> findGenerationPoint(Structure.a structure_a) {
        return getLowestY(structure_a, this.width, this.depth) < structure_a.chunkGenerator().getSeaLevel() ? Optional.empty() : onTopOfChunkCenter(structure_a, HeightMap.Type.WORLD_SURFACE_WG, (structurepiecesbuilder) -> {
            this.generatePieces(structurepiecesbuilder, structure_a);
        });
    }

    private void generatePieces(StructurePiecesBuilder structurepiecesbuilder, Structure.a structure_a) {
        ChunkCoordIntPair chunkcoordintpair = structure_a.chunkPos();

        structurepiecesbuilder.addPiece(this.constructor.construct(structure_a.random(), chunkcoordintpair.getMinBlockX(), chunkcoordintpair.getMinBlockZ()));
    }

    @FunctionalInterface
    protected interface a {

        StructurePiece construct(SeededRandom seededrandom, int i, int j);
    }
}
