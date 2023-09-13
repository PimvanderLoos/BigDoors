package net.minecraft.server;

import javax.annotation.Nullable;

public abstract class DefinedStructureProcessor {

    public DefinedStructureProcessor() {}

    @Nullable
    public abstract DefinedStructure.BlockInfo a(IWorldReader iworldreader, BlockPosition blockposition, BlockPosition blockposition1, DefinedStructure.BlockInfo definedstructure_blockinfo, DefinedStructure.BlockInfo definedstructure_blockinfo1, DefinedStructureInfo definedstructureinfo);

    protected abstract DefinedStructureStructureProcessorType<?> a();
}
