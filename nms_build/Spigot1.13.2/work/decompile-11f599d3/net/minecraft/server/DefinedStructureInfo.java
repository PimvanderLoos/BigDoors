package net.minecraft.server;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public class DefinedStructureInfo {

    private EnumBlockMirror a;
    private EnumBlockRotation b;
    private BlockPosition c;
    private boolean d;
    @Nullable
    private Block e;
    @Nullable
    private ChunkCoordIntPair f;
    @Nullable
    private StructureBoundingBox g;
    private boolean h;
    private boolean i;
    private float j;
    @Nullable
    private Random k;
    @Nullable
    private Long l;
    @Nullable
    private Integer m;
    private int n;

    public DefinedStructureInfo() {
        this.a = EnumBlockMirror.NONE;
        this.b = EnumBlockRotation.NONE;
        this.c = new BlockPosition(0, 0, 0);
        this.h = true;
        this.i = true;
        this.j = 1.0F;
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
        definedstructureinfo.k = this.k;
        definedstructureinfo.l = this.l;
        definedstructureinfo.m = this.m;
        definedstructureinfo.n = this.n;
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

    public DefinedStructureInfo a(BlockPosition blockposition) {
        this.c = blockposition;
        return this;
    }

    public DefinedStructureInfo a(boolean flag) {
        this.d = flag;
        return this;
    }

    public DefinedStructureInfo a(Block block) {
        this.e = block;
        return this;
    }

    public DefinedStructureInfo a(ChunkCoordIntPair chunkcoordintpair) {
        this.f = chunkcoordintpair;
        return this;
    }

    public DefinedStructureInfo a(StructureBoundingBox structureboundingbox) {
        this.g = structureboundingbox;
        return this;
    }

    public DefinedStructureInfo a(@Nullable Long olong) {
        this.l = olong;
        return this;
    }

    public DefinedStructureInfo a(@Nullable Random random) {
        this.k = random;
        return this;
    }

    public DefinedStructureInfo a(float f) {
        this.j = f;
        return this;
    }

    public EnumBlockMirror b() {
        return this.a;
    }

    public DefinedStructureInfo c(boolean flag) {
        this.h = flag;
        return this;
    }

    public EnumBlockRotation c() {
        return this.b;
    }

    public BlockPosition d() {
        return this.c;
    }

    public Random b(@Nullable BlockPosition blockposition) {
        return this.k != null ? this.k : (this.l != null ? (this.l == 0L ? new Random(SystemUtils.getMonotonicMillis()) : new Random(this.l)) : (blockposition == null ? new Random(SystemUtils.getMonotonicMillis()) : SeededRandom.a(blockposition.getX(), blockposition.getZ(), 0L, 987234911L)));
    }

    public float g() {
        return this.j;
    }

    public boolean h() {
        return this.d;
    }

    @Nullable
    public Block i() {
        return this.e;
    }

    @Nullable
    public StructureBoundingBox j() {
        if (this.g == null && this.f != null) {
            this.l();
        }

        return this.g;
    }

    public boolean k() {
        return this.h;
    }

    void l() {
        if (this.f != null) {
            this.g = this.b(this.f);
        }

    }

    public boolean m() {
        return this.i;
    }

    public List<DefinedStructure.BlockInfo> a(List<List<DefinedStructure.BlockInfo>> list, @Nullable BlockPosition blockposition) {
        this.m = 8;
        if (this.m != null && this.m >= 0 && this.m < list.size()) {
            return (List) list.get(this.m);
        } else {
            this.m = this.b(blockposition).nextInt(list.size());
            return (List) list.get(this.m);
        }
    }

    @Nullable
    private StructureBoundingBox b(@Nullable ChunkCoordIntPair chunkcoordintpair) {
        if (chunkcoordintpair == null) {
            return this.g;
        } else {
            int i = chunkcoordintpair.x * 16;
            int j = chunkcoordintpair.z * 16;

            return new StructureBoundingBox(i, 0, j, i + 16 - 1, 255, j + 16 - 1);
        }
    }
}
