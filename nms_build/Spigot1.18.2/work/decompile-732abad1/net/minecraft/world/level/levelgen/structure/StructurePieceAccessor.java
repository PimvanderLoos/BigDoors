package net.minecraft.world.level.levelgen.structure;

import javax.annotation.Nullable;

public interface StructurePieceAccessor {

    void addPiece(StructurePiece structurepiece);

    @Nullable
    StructurePiece findCollisionPiece(StructureBoundingBox structureboundingbox);
}
