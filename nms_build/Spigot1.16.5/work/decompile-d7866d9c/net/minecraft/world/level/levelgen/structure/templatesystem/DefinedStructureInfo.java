package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.block.EnumBlockMirror;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;

public class DefinedStructureInfo {

    private EnumBlockMirror a;
    private EnumBlockRotation b;
    private BlockPosition c;
    private boolean d;
    @Nullable
    private ChunkCoordIntPair e;
    @Nullable
    private StructureBoundingBox f;
    private boolean g;
    @Nullable
    private Random h;
    @Nullable
    private int i;
    private final List<DefinedStructureProcessor> j;
    private boolean k;
    private boolean l;

    public DefinedStructureInfo() {
        this.a = EnumBlockMirror.NONE;
        this.b = EnumBlockRotation.NONE;
        this.c = BlockPosition.ZERO;
        this.g = true;
        this.j = Lists.newArrayList();
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
        definedstructureinfo.j.addAll(this.j);
        definedstructureinfo.k = this.k;
        definedstructureinfo.l = this.l;
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

    public DefinedStructureInfo a(ChunkCoordIntPair chunkcoordintpair) {
        this.e = chunkcoordintpair;
        return this;
    }

    public DefinedStructureInfo a(StructureBoundingBox structureboundingbox) {
        this.f = structureboundingbox;
        return this;
    }

    public DefinedStructureInfo a(@Nullable Random random) {
        this.h = random;
        return this;
    }

    public DefinedStructureInfo c(boolean flag) {
        this.k = flag;
        return this;
    }

    public DefinedStructureInfo b() {
        this.j.clear();
        return this;
    }

    public DefinedStructureInfo a(DefinedStructureProcessor definedstructureprocessor) {
        this.j.add(definedstructureprocessor);
        return this;
    }

    public DefinedStructureInfo b(DefinedStructureProcessor definedstructureprocessor) {
        this.j.remove(definedstructureprocessor);
        return this;
    }

    public EnumBlockMirror c() {
        return this.a;
    }

    public EnumBlockRotation d() {
        return this.b;
    }

    public BlockPosition e() {
        return this.c;
    }

    public Random b(@Nullable BlockPosition blockposition) {
        return this.h != null ? this.h : (blockposition == null ? new Random(SystemUtils.getMonotonicMillis()) : new Random(MathHelper.a((BaseBlockPosition) blockposition)));
    }

    public boolean g() {
        return this.d;
    }

    @Nullable
    public StructureBoundingBox h() {
        if (this.f == null && this.e != null) {
            this.k();
        }

        return this.f;
    }

    public boolean i() {
        return this.k;
    }

    public List<DefinedStructureProcessor> j() {
        return this.j;
    }

    void k() {
        if (this.e != null) {
            this.f = this.b(this.e);
        }

    }

    public boolean l() {
        return this.g;
    }

    public DefinedStructure.a a(List<DefinedStructure.a> list, @Nullable BlockPosition blockposition) {
        int i = list.size();

        if (i == 0) {
            throw new IllegalStateException("No palettes");
        } else {
            return (DefinedStructure.a) list.get(this.b(blockposition).nextInt(i));
        }
    }

    @Nullable
    private StructureBoundingBox b(@Nullable ChunkCoordIntPair chunkcoordintpair) {
        if (chunkcoordintpair == null) {
            return this.f;
        } else {
            int i = chunkcoordintpair.x * 16;
            int j = chunkcoordintpair.z * 16;

            return new StructureBoundingBox(i, 0, j, i + 16 - 1, 255, j + 16 - 1);
        }
    }

    public DefinedStructureInfo d(boolean flag) {
        this.l = flag;
        return this;
    }

    public boolean m() {
        return this.l;
    }
}
