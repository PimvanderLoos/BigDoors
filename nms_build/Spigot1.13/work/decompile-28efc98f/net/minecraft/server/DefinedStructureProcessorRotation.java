package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class DefinedStructureProcessorRotation implements DefinedStructureProcessor {

    private final float a;
    private final Random b;

    public DefinedStructureProcessorRotation(BlockPosition blockposition, DefinedStructureInfo definedstructureinfo) {
        this.a = definedstructureinfo.g();
        this.b = definedstructureinfo.b(blockposition);
    }

    @Nullable
    public DefinedStructure.BlockInfo a(IBlockAccess iblockaccess, BlockPosition blockposition, DefinedStructure.BlockInfo definedstructure_blockinfo) {
        return this.a < 1.0F && this.b.nextFloat() > this.a ? null : definedstructure_blockinfo;
    }
}
