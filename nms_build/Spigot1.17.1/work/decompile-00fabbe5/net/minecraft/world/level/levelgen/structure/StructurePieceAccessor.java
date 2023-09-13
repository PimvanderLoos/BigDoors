package net.minecraft.world.level.levelgen.structure;

import javax.annotation.Nullable;

public interface StructurePieceAccessor {

    void a(StructurePiece structurepiece);

    @Nullable
    StructurePiece a(StructureBoundingBox structureboundingbox);
}
