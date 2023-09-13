package net.minecraft.world.level.levelgen.structure.templatesystem;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IWorldReader;

public abstract class DefinedStructureProcessor {

    public DefinedStructureProcessor() {}

    @Nullable
    public abstract DefinedStructure.BlockInfo processBlock(IWorldReader iworldreader, BlockPosition blockposition, BlockPosition blockposition1, DefinedStructure.BlockInfo definedstructure_blockinfo, DefinedStructure.BlockInfo definedstructure_blockinfo1, DefinedStructureInfo definedstructureinfo);

    protected abstract DefinedStructureStructureProcessorType<?> getType();

    public void finalizeStructure(GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1, DefinedStructureInfo definedstructureinfo, List<DefinedStructure.BlockInfo> list) {}
}
