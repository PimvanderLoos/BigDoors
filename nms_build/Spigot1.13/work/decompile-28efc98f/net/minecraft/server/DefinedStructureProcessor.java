package net.minecraft.server;

import javax.annotation.Nullable;

public interface DefinedStructureProcessor {

    @Nullable
    DefinedStructure.BlockInfo a(IBlockAccess iblockaccess, BlockPosition blockposition, DefinedStructure.BlockInfo definedstructure_blockinfo);
}
