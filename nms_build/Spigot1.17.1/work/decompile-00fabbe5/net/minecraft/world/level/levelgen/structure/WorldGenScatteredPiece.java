package net.minecraft.world.level.levelgen.structure;

import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureStructurePieceType;

public abstract class WorldGenScatteredPiece extends StructurePiece {

    protected final int width;
    protected final int height;
    protected final int depth;
    protected int heightPosition = -1;

    protected WorldGenScatteredPiece(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, int i, int j, int k, int l, int i1, int j1, EnumDirection enumdirection) {
        super(worldgenfeaturestructurepiecetype, 0, StructurePiece.a(i, j, k, enumdirection, l, i1, j1));
        this.width = l;
        this.height = i1;
        this.depth = j1;
        this.a(enumdirection);
    }

    protected WorldGenScatteredPiece(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, NBTTagCompound nbttagcompound) {
        super(worldgenfeaturestructurepiecetype, nbttagcompound);
        this.width = nbttagcompound.getInt("Width");
        this.height = nbttagcompound.getInt("Height");
        this.depth = nbttagcompound.getInt("Depth");
        this.heightPosition = nbttagcompound.getInt("HPos");
    }

    @Override
    protected void a(WorldServer worldserver, NBTTagCompound nbttagcompound) {
        nbttagcompound.setInt("Width", this.width);
        nbttagcompound.setInt("Height", this.height);
        nbttagcompound.setInt("Depth", this.depth);
        nbttagcompound.setInt("HPos", this.heightPosition);
    }

    protected boolean a(GeneratorAccess generatoraccess, StructureBoundingBox structureboundingbox, int i) {
        if (this.heightPosition >= 0) {
            return true;
        } else {
            int j = 0;
            int k = 0;
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            for (int l = this.boundingBox.i(); l <= this.boundingBox.l(); ++l) {
                for (int i1 = this.boundingBox.g(); i1 <= this.boundingBox.j(); ++i1) {
                    blockposition_mutableblockposition.d(i1, 64, l);
                    if (structureboundingbox.b((BaseBlockPosition) blockposition_mutableblockposition)) {
                        j += generatoraccess.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, blockposition_mutableblockposition).getY();
                        ++k;
                    }
                }
            }

            if (k == 0) {
                return false;
            } else {
                this.heightPosition = j / k;
                this.boundingBox.a(0, this.heightPosition - this.boundingBox.h() + i, 0);
                return true;
            }
        }
    }
}
