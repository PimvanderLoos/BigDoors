package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
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
    private RandomSource random;
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

    public DefinedStructureInfo copy() {
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

    public DefinedStructureInfo setMirror(EnumBlockMirror enumblockmirror) {
        this.mirror = enumblockmirror;
        return this;
    }

    public DefinedStructureInfo setRotation(EnumBlockRotation enumblockrotation) {
        this.rotation = enumblockrotation;
        return this;
    }

    public DefinedStructureInfo setRotationPivot(BlockPosition blockposition) {
        this.rotationPivot = blockposition;
        return this;
    }

    public DefinedStructureInfo setIgnoreEntities(boolean flag) {
        this.ignoreEntities = flag;
        return this;
    }

    public DefinedStructureInfo setBoundingBox(StructureBoundingBox structureboundingbox) {
        this.boundingBox = structureboundingbox;
        return this;
    }

    public DefinedStructureInfo setRandom(@Nullable RandomSource randomsource) {
        this.random = randomsource;
        return this;
    }

    public DefinedStructureInfo setKeepLiquids(boolean flag) {
        this.keepLiquids = flag;
        return this;
    }

    public DefinedStructureInfo setKnownShape(boolean flag) {
        this.knownShape = flag;
        return this;
    }

    public DefinedStructureInfo clearProcessors() {
        this.processors.clear();
        return this;
    }

    public DefinedStructureInfo addProcessor(DefinedStructureProcessor definedstructureprocessor) {
        this.processors.add(definedstructureprocessor);
        return this;
    }

    public DefinedStructureInfo popProcessor(DefinedStructureProcessor definedstructureprocessor) {
        this.processors.remove(definedstructureprocessor);
        return this;
    }

    public EnumBlockMirror getMirror() {
        return this.mirror;
    }

    public EnumBlockRotation getRotation() {
        return this.rotation;
    }

    public BlockPosition getRotationPivot() {
        return this.rotationPivot;
    }

    public RandomSource getRandom(@Nullable BlockPosition blockposition) {
        return this.random != null ? this.random : (blockposition == null ? RandomSource.create(SystemUtils.getMillis()) : RandomSource.create(MathHelper.getSeed(blockposition)));
    }

    public boolean isIgnoreEntities() {
        return this.ignoreEntities;
    }

    @Nullable
    public StructureBoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    public boolean getKnownShape() {
        return this.knownShape;
    }

    public List<DefinedStructureProcessor> getProcessors() {
        return this.processors;
    }

    public boolean shouldKeepLiquids() {
        return this.keepLiquids;
    }

    public DefinedStructure.a getRandomPalette(List<DefinedStructure.a> list, @Nullable BlockPosition blockposition) {
        int i = list.size();

        if (i == 0) {
            throw new IllegalStateException("No palettes");
        } else {
            return (DefinedStructure.a) list.get(this.getRandom(blockposition).nextInt(i));
        }
    }

    public DefinedStructureInfo setFinalizeEntities(boolean flag) {
        this.finalizeEntities = flag;
        return this;
    }

    public boolean shouldFinalizeEntities() {
        return this.finalizeEntities;
    }
}
