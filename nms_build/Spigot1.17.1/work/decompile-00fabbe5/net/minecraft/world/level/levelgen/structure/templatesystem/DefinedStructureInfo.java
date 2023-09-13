package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.block.EnumBlockMirror;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;

public class DefinedStructureInfo {

    private EnumBlockMirror mirror;
    private EnumBlockRotation rotation;
    private BlockPosition rotationPivot;
    private boolean ignoreEntities;
    @Nullable
    private StructureBoundingBox boundingBox;
    private boolean keepLiquids;
    @Nullable
    private Random random;
    @Nullable
    private int palette;
    private final List<DefinedStructureProcessor> processors;
    private boolean knownShape;
    private boolean finalizeEntities;

    public DefinedStructureInfo() {
        this.mirror = EnumBlockMirror.NONE;
        this.rotation = EnumBlockRotation.NONE;
        this.rotationPivot = BlockPosition.ZERO;
        this.keepLiquids = true;
        this.processors = Lists.newArrayList();
    }

    public DefinedStructureInfo a() {
        DefinedStructureInfo definedstructureinfo = new DefinedStructureInfo();

        definedstructureinfo.mirror = this.mirror;
        definedstructureinfo.rotation = this.rotation;
        definedstructureinfo.rotationPivot = this.rotationPivot;
        definedstructureinfo.ignoreEntities = this.ignoreEntities;
        definedstructureinfo.boundingBox = this.boundingBox;
        definedstructureinfo.keepLiquids = this.keepLiquids;
        definedstructureinfo.random = this.random;
        definedstructureinfo.palette = this.palette;
        definedstructureinfo.processors.addAll(this.processors);
        definedstructureinfo.knownShape = this.knownShape;
        definedstructureinfo.finalizeEntities = this.finalizeEntities;
        return definedstructureinfo;
    }

    public DefinedStructureInfo a(EnumBlockMirror enumblockmirror) {
        this.mirror = enumblockmirror;
        return this;
    }

    public DefinedStructureInfo a(EnumBlockRotation enumblockrotation) {
        this.rotation = enumblockrotation;
        return this;
    }

    public DefinedStructureInfo a(BlockPosition blockposition) {
        this.rotationPivot = blockposition;
        return this;
    }

    public DefinedStructureInfo a(boolean flag) {
        this.ignoreEntities = flag;
        return this;
    }

    public DefinedStructureInfo a(StructureBoundingBox structureboundingbox) {
        this.boundingBox = structureboundingbox;
        return this;
    }

    public DefinedStructureInfo a(@Nullable Random random) {
        this.random = random;
        return this;
    }

    public DefinedStructureInfo b(boolean flag) {
        this.keepLiquids = flag;
        return this;
    }

    public DefinedStructureInfo c(boolean flag) {
        this.knownShape = flag;
        return this;
    }

    public DefinedStructureInfo b() {
        this.processors.clear();
        return this;
    }

    public DefinedStructureInfo a(DefinedStructureProcessor definedstructureprocessor) {
        this.processors.add(definedstructureprocessor);
        return this;
    }

    public DefinedStructureInfo b(DefinedStructureProcessor definedstructureprocessor) {
        this.processors.remove(definedstructureprocessor);
        return this;
    }

    public EnumBlockMirror c() {
        return this.mirror;
    }

    public EnumBlockRotation d() {
        return this.rotation;
    }

    public BlockPosition e() {
        return this.rotationPivot;
    }

    public Random b(@Nullable BlockPosition blockposition) {
        return this.random != null ? this.random : (blockposition == null ? new Random(SystemUtils.getMonotonicMillis()) : new Random(MathHelper.a((BaseBlockPosition) blockposition)));
    }

    public boolean f() {
        return this.ignoreEntities;
    }

    @Nullable
    public StructureBoundingBox g() {
        return this.boundingBox;
    }

    public boolean h() {
        return this.knownShape;
    }

    public List<DefinedStructureProcessor> i() {
        return this.processors;
    }

    public boolean j() {
        return this.keepLiquids;
    }

    public DefinedStructure.a a(List<DefinedStructure.a> list, @Nullable BlockPosition blockposition) {
        int i = list.size();

        if (i == 0) {
            throw new IllegalStateException("No palettes");
        } else {
            return (DefinedStructure.a) list.get(this.b(blockposition).nextInt(i));
        }
    }

    public DefinedStructureInfo d(boolean flag) {
        this.finalizeEntities = flag;
        return this;
    }

    public boolean k() {
        return this.finalizeEntities;
    }
}
