package net.minecraft.world.level.levelgen.structure.pieces;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;

public class StructurePiecesBuilder implements StructurePieceAccessor {

    private final List<StructurePiece> pieces = Lists.newArrayList();

    public StructurePiecesBuilder() {}

    @Override
    public void addPiece(StructurePiece structurepiece) {
        this.pieces.add(structurepiece);
    }

    @Nullable
    @Override
    public StructurePiece findCollisionPiece(StructureBoundingBox structureboundingbox) {
        return StructurePiece.findCollisionPiece(this.pieces, structureboundingbox);
    }

    /** @deprecated */
    @Deprecated
    public void offsetPiecesVertically(int i) {
        Iterator iterator = this.pieces.iterator();

        while (iterator.hasNext()) {
            StructurePiece structurepiece = (StructurePiece) iterator.next();

            structurepiece.move(0, i, 0);
        }

    }

    /** @deprecated */
    @Deprecated
    public int moveBelowSeaLevel(int i, int j, RandomSource randomsource, int k) {
        int l = i - k;
        StructureBoundingBox structureboundingbox = this.getBoundingBox();
        int i1 = structureboundingbox.getYSpan() + j + 1;

        if (i1 < l) {
            i1 += randomsource.nextInt(l - i1);
        }

        int j1 = i1 - structureboundingbox.maxY();

        this.offsetPiecesVertically(j1);
        return j1;
    }

    /** @deprecated */
    public void moveInsideHeights(RandomSource randomsource, int i, int j) {
        StructureBoundingBox structureboundingbox = this.getBoundingBox();
        int k = j - i + 1 - structureboundingbox.getYSpan();
        int l;

        if (k > 1) {
            l = i + randomsource.nextInt(k);
        } else {
            l = i;
        }

        int i1 = l - structureboundingbox.minY();

        this.offsetPiecesVertically(i1);
    }

    public PiecesContainer build() {
        return new PiecesContainer(this.pieces);
    }

    public void clear() {
        this.pieces.clear();
    }

    public boolean isEmpty() {
        return this.pieces.isEmpty();
    }

    public StructureBoundingBox getBoundingBox() {
        return StructurePiece.createBoundingBox(this.pieces.stream());
    }
}
