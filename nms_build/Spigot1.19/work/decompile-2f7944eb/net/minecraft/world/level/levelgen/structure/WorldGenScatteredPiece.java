package net.minecraft.world.level.levelgen.structure;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.WorldGenFeatureStructurePieceType;

public abstract class WorldGenScatteredPiece extends StructurePiece {

    protected final int width;
    protected final int height;
    protected final int depth;
    protected int heightPosition = -1;

    protected WorldGenScatteredPiece(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, int i, int j, int k, int l, int i1, int j1, EnumDirection enumdirection) {
        super(worldgenfeaturestructurepiecetype, 0, StructurePiece.makeBoundingBox(i, j, k, enumdirection, l, i1, j1));
        this.width = l;
        this.height = i1;
        this.depth = j1;
        this.setOrientation(enumdirection);
    }

    protected WorldGenScatteredPiece(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, NBTTagCompound nbttagcompound) {
        super(worldgenfeaturestructurepiecetype, nbttagcompound);
        this.width = nbttagcompound.getInt("Width");
        this.height = nbttagcompound.getInt("Height");
        this.depth = nbttagcompound.getInt("Depth");
        this.heightPosition = nbttagcompound.getInt("HPos");
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound) {
        nbttagcompound.putInt("Width", this.width);
        nbttagcompound.putInt("Height", this.height);
        nbttagcompound.putInt("Depth", this.depth);
        nbttagcompound.putInt("HPos", this.heightPosition);
    }

    protected boolean updateAverageGroundHeight(GeneratorAccess generatoraccess, StructureBoundingBox structureboundingbox, int i) {
        if (this.heightPosition >= 0) {
            return true;
        } else {
            int j = 0;
            int k = 0;
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            for (int l = this.boundingBox.minZ(); l <= this.boundingBox.maxZ(); ++l) {
                for (int i1 = this.boundingBox.minX(); i1 <= this.boundingBox.maxX(); ++i1) {
                    blockposition_mutableblockposition.set(i1, 64, l);
                    if (structureboundingbox.isInside(blockposition_mutableblockposition)) {
                        j += generatoraccess.getHeightmapPos(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, blockposition_mutableblockposition).getY();
                        ++k;
                    }
                }
            }

            if (k == 0) {
                return false;
            } else {
                this.heightPosition = j / k;
                this.boundingBox.move(0, this.heightPosition - this.boundingBox.minY() + i, 0);
                return true;
            }
        }
    }

    protected boolean updateHeightPositionToLowestGroundHeight(GeneratorAccess generatoraccess, int i) {
        if (this.heightPosition >= 0) {
            return true;
        } else {
            int j = generatoraccess.getMaxBuildHeight();
            boolean flag = false;
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            for (int k = this.boundingBox.minZ(); k <= this.boundingBox.maxZ(); ++k) {
                for (int l = this.boundingBox.minX(); l <= this.boundingBox.maxX(); ++l) {
                    blockposition_mutableblockposition.set(l, 0, k);
                    j = Math.min(j, generatoraccess.getHeightmapPos(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, blockposition_mutableblockposition).getY());
                    flag = true;
                }
            }

            if (!flag) {
                return false;
            } else {
                this.heightPosition = j;
                this.boundingBox.move(0, this.heightPosition - this.boundingBox.minY() + i, 0);
                return true;
            }
        }
    }
}
