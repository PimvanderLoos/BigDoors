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

    public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
        this.b.a(structureboundingbox);
        this.a.a(world, this.c, this.b, 18);
        Map map = this.a.a(this.c, this.b);
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            String s = (String) entry.getValue();

            this.a(s, (BlockPosition) entry.getKey(), world, random, structureboundingbox);
        }

        return true;
    }

    protected abstract void a(String s, BlockPosition blockposition, World world, Random random, StructureBoundingBox structureboundingbox);

    private void b() {
        EnumBlockRotation enumblockrotation = this.b.c();
        BlockPosition blockposition = this.a.a(enumblockrotation);
        EnumBlockMirror enumblockmirror = this.b.b();

        this.l = new StructureBoundingBox(0, 0, 0, blockposition.getX(), blockposition.getY() - 1, blockposition.getZ());
        switch (enumblockrotation) {
        case NONE:
        default:
            break;

        case CLOCKWISE_90:
            this.l.a(-blockposition.getX(), 0, 0);
            break;

        case COUNTERCLOCKWISE_90:
            this.l.a(0, 0, -blockposition.getZ());
            break;

        case CLOCKWISE_180:
            this.l.a(-blockposition.getX(), 0, -blockposition.getZ());
        }

        BlockPosition blockposition1;

        switch (enumblockmirror) {
        case NONE:
        default:
            break;

        case FRONT_BACK:
            blockposition1 = BlockPosition.ZERO;
            if (enumblockrotation != EnumBlockRotation.CLOCKWISE_90 && enumblockrotation != EnumBlockRotation.COUNTERCLOCKWISE_90) {
                if (enumblockrotation == EnumBlockRotation.CLOCKWISE_180) {
                    blockposition1 = blockposition1.shift(EnumDirection.EAST, blockposition.getX());
                } else {
                    blockposition1 = blockposition1.shift(EnumDirection.WEST, blockposition.getX());
                }
            } else {
                blockposition1 = blockposition1.shift(enumblockrotation.a(EnumDirection.WEST), blockposition.getZ());
            }

            this.l.a(blockposition1.getX(), 0, blockposition1.getZ());
            break;

        case LEFT_RIGHT:
            blockposition1 = BlockPosition.ZERO;
            if (enumblockrotation != EnumBlockRotation.CLOCKWISE_90 && enumblockrotation != EnumBlockRotation.COUNTERCLOCKWISE_90) {
                if (enumblockrotation == EnumBlockRotation.CLOCKWISE_180) {
                    blockposition1 = blockposition1.shift(EnumDirection.SOUTH, blockposition.getZ());
                } else {
                    blockposition1 = blockposition1.shift(EnumDirection.NORTH, blockposition.getZ());
                }
            } else {
                blockposition1 = blockposition1.shift(enumblockrotation.a(EnumDirection.NORTH), blockposition.getX());
            }

            this.l.a(blockposition1.getX(), 0, blockposition1.getZ());
        }

        this.l.a(this.c.getX(), this.c.getY(), this.c.getZ());
    }

    public void a(int i, int j, int k) {
        super.a(i, j, k);
        this.c = this.c.a(i, j, k);
    }
}
