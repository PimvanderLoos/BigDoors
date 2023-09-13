package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class DefinedStructureInfo {

    private EnumBlockMirror a;
    private EnumBlockRotation b;
    private boolean c;
    @Nullable
    private Block d;
    @Nullable
    private ChunkCoordIntPair e;
    @Nullable
    private StructureBoundingBox f;
    private boolean g;
    private float h;
    @Nullable
    private Random i;
    @Nullable
    private Long j;

    public DefinedStructureInfo() {
        this.a = EnumBlockMirror.NONE;
        this.b = EnumBlockRotation.NONE;
        this.g = true;
        this.h = 1.0F;
    }

    public DefinedStructureInfo a() {
        DefinedStructureInfo definedstructureinfo = new DefinedStructureInfo();

        definedstructureinfo.a = this.a;
        definedstructureinfo.b = this.b;
        definedstructureinfo.c = this.c;
        definedstructureinfo.d = this.d;
        definedstructureinfo.e = this.e;
        definedstructureinfo.f = this.f;
        definedstructureinfo.g = this.g;
        definedstructureinfo.h = this.h;
        definedstructureinfo.i = this.i;
        definedstructureinfo.j = this.j;
        return definedstructureinfo;
    }

    public DefinedStructureInfo a(EnumBlockMirror enumblockmirror) {
        this.a = enumblockmirror;
        return this;
    }

    public DefinedStructureInfo a(EnumBlockRotation enumblockrotation) {
        this.b = enumblockrotation;
        return this;
    }

    public DefinedStructureInfo a(boolean flag) {
        this.c = flag;
        return this;
    }

    public DefinedStructureInfo a(Block block) {
        this.d = block;
        return this;
    }

    public DefinedStructureInfo a(ChunkCoordIntPair chunkcoordintpair) {
        this.e = chunkcoordintpair;
        return this;
    }

    public DefinedStructureInfo a(StructureBoundingBox structureboundingbox) {
        this.f = structureboundingbox;
        return this;
    }

    public DefinedStructureInfo a(@Nullable Long olong) {
        this.j = olong;
        return this;
    }

    public DefinedStructureInfo a(@Nullable Random random) {
        this.i = random;
        return this;
    }

    public DefinedStructureInfo a(float f) {
        this.h = f;
        return this;
    }

    public EnumBlockMirror b() {
        return this.a;
    }

    public DefinedStructureInfo b(boolean flag) {
        this.g = flag;
        return this;
    }

    public EnumBlockRotation c() {
        return this.b;
    }

    public Random a(@Nullable BlockPosition blockposition) {
        if (this.i != null) {
            return this.i;
        } else if (this.j != null) {
            return this.j.longValue() == 0L ? new Random(System.currentTimeMillis()) : new Random(this.j.longValue());
        } else if (blockposition == null) {
            return new Random(System.currentTimeMillis());
        } else {
            int i = blockposition.getX();
            int j = blockposition.getZ();

            return new Random((long) (i * i * 4987142 + i * 5947611) + (long) (j * j) * 4392871L + (long) (j * 389711) ^ 987234911L);
        }
    }

    public float f() {
        return this.h;
    }

    public boolean g() {
        return this.c;
    }

    @Nullable
    public Block h() {
        return this.d;
    }

    @Nullable
    public StructureBoundingBox i() {
        if (this.f == null && this.e != null) {
            this.k();
        }

        return this.f;
    }

    public boolean j() {
        return this.g;
    }

    void k() {
        this.f = this.b(this.e);
    }

    @Nullable
    private StructureBoundingBox b(@Nullable ChunkCoordIntPair chunkcoordintpair) {
        if (chunkcoordintpair == null) {
            return null;
        } else {
            int i = chunkcoordintpair.x * 16;
            int j = chunkcoordintpair.z * 16;

            return new StructureBoundingBox(i, 0, j, i + 16 - 1, 255, j + 16 - 1);
        }
    }
}
