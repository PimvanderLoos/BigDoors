package net.minecraft.server;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

public abstract class DefinedStructurePiece extends StructurePiece {

    private static final DefinedStructureInfo d = new DefinedStructureInfo();
    protected DefinedStructure a;
    protected DefinedStructureInfo b;
    protected BlockPosition c;

    public DefinedStructurePiece() {
        this.b = DefinedStructurePiece.d.a(true).a(Blocks.AIR);
    }

    public DefinedStructurePiece(int i) {
        super(i);
        this.b = DefinedStructurePiece.d.a(true).a(Blocks.AIR);
    }

    protected void a(DefinedStructure definedstructure, BlockPosition blockposition, DefinedStructureInfo definedstructureinfo) {
        this.a = definedstructure;
        this.a(EnumDirection.NORTH);
        this.c = blockposition;
        this.b = definedstructureinfo;
        this.b();
    }

    protected void a(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInt("TPX", this.c.getX());
        nbttagcompound.setInt("TPY", this.c.getY());
        nbttagcompound.setInt("TPZ", this.c.getZ());
    }

    protected void a(NBTTagCompound nbttagcompound, DefinedStructureManager definedstructuremanager) {
        this.c = new BlockPosition(nbttagcompound.getInt("TPX"), nbttagcompound.getInt("TPY"), nbttagcompound.getInt("TPZ"));
    }

    public boolean a(GeneratorAccess generatoraccess, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair) {
        this.b.a(structureboundingbox);
        if (this.a.a(generatoraccess, this.c, this.b, 2)) {
            Map<BlockPosition, String> map = this.a.a(this.c, this.b);
            Iterator iterator = map.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<BlockPosition, String> entry = (Entry) iterator.next();
                String s = (String) entry.getValue();

                this.a(s, (BlockPosition) entry.getKey(), generatoraccess, random, structureboundingbox);
            }
        }

        return true;
    }

    protected abstract void a(String s, BlockPosition blockposition, GeneratorAccess generatoraccess, Random random, StructureBoundingBox structureboundingbox);

    private void b() {
        EnumBlockRotation enumblockrotation = this.b.c();
        BlockPosition blockposition = this.b.d();
        BlockPosition blockposition1 = this.a.a(enumblockrotation);
        EnumBlockMirror enumblockmirror = this.b.b();
        int i = blockposition.getX();
        int j = blockposition.getZ();
        int k = blockposition1.getX() - 1;
        int l = blockposition1.getY() - 1;
        int i1 = blockposition1.getZ() - 1;

        switch (enumblockrotation) {
        case NONE:
            this.n = new StructureBoundingBox(0, 0, 0, k, l, i1);
            break;
        case CLOCKWISE_180:
            this.n = new StructureBoundingBox(i + i - k, 0, j + j - i1, i + i, l, j + j);
            break;
        case COUNTERCLOCKWISE_90:
            this.n = new StructureBoundingBox(i - j, 0, i + j - i1, i - j + k, l, i + j);
            break;
        case CLOCKWISE_90:
            this.n = new StructureBoundingBox(i + j - k, 0, j - i, i + j, l, j - i + i1);
        }

        BlockPosition blockposition2;

        switch (enumblockmirror) {
        case NONE:
        default:
            break;
        case FRONT_BACK:
            blockposition2 = BlockPosition.ZERO;
            if (enumblockrotation != EnumBlockRotation.CLOCKWISE_90 && enumblockrotation != EnumBlockRotation.COUNTERCLOCKWISE_90) {
                if (enumblockrotation == EnumBlockRotation.CLOCKWISE_180) {
                    blockposition2 = blockposition2.shift(EnumDirection.EAST, k);
                } else {
                    blockposition2 = blockposition2.shift(EnumDirection.WEST, k);
                }
            } else {
                blockposition2 = blockposition2.shift(enumblockrotation.a(EnumDirection.WEST), i1);
            }

            this.n.a(blockposition2.getX(), 0, blockposition2.getZ());
            break;
        case LEFT_RIGHT:
            blockposition2 = BlockPosition.ZERO;
            if (enumblockrotation != EnumBlockRotation.CLOCKWISE_90 && enumblockrotation != EnumBlockRotation.COUNTERCLOCKWISE_90) {
                if (enumblockrotation == EnumBlockRotation.CLOCKWISE_180) {
                    blockposition2 = blockposition2.shift(EnumDirection.SOUTH, i1);
                } else {
                    blockposition2 = blockposition2.shift(EnumDirection.NORTH, i1);
                }
            } else {
                blockposition2 = blockposition2.shift(enumblockrotation.a(EnumDirection.NORTH), k);
            }

            this.n.a(blockposition2.getX(), 0, blockposition2.getZ());
        }

        this.n.a(this.c.getX(), this.c.getY(), this.c.getZ());
    }

    public void a(int i, int j, int k) {
        super.a(i, j, k);
        this.c = this.c.a(i, j, k);
    }
}
