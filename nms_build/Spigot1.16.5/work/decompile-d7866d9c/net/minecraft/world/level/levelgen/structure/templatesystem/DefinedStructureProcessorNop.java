package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.IWorldReader;

public class DefinedStructureProcessorNop extends DefinedStructureProcessor {

    public static final Codec<DefinedStructureProcessorNop> a = Codec.unit(() -> {
        return DefinedStructureProcessorNop.b;
    });
    public static final DefinedStructureProcessorNop b = new DefinedStructureProcessorNop();

    private DefinedStructureProcessorNop() {}

    @Nullable
    @Override
    public DefinedStructure.BlockInfo a(IWorldReader iworldreader, BlockPosition blockposition, BlockPosition blockposition1, DefinedStructure.BlockInfo definedstructure_blockinfo, DefinedStructure.BlockInfo definedstructure_blockinfo1, DefinedStructureInfo definedstructureinfo) {
        return definedstructure_blockinfo1;
    }

    @Override
    protected DefinedStructureStructureProcessorType<?> a() {
        return DefinedStructureStructureProcessorType.f;
    }
}
