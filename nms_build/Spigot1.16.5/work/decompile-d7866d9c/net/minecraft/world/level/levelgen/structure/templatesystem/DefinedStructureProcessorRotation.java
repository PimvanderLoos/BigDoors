package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.IWorldReader;

public class DefinedStructureProcessorRotation extends DefinedStructureProcessor {

    public static final Codec<DefinedStructureProcessorRotation> a = Codec.FLOAT.fieldOf("integrity").orElse(1.0F).xmap(DefinedStructureProcessorRotation::new, (definedstructureprocessorrotation) -> {
        return definedstructureprocessorrotation.b;
    }).codec();
    private final float b;

    public DefinedStructureProcessorRotation(float f) {
        this.b = f;
    }

    @Nullable
    @Override
    public DefinedStructure.BlockInfo a(IWorldReader iworldreader, BlockPosition blockposition, BlockPosition blockposition1, DefinedStructure.BlockInfo definedstructure_blockinfo, DefinedStructure.BlockInfo definedstructure_blockinfo1, DefinedStructureInfo definedstructureinfo) {
        Random random = definedstructureinfo.b(definedstructure_blockinfo1.a);

        return this.b < 1.0F && random.nextFloat() > this.b ? null : definedstructure_blockinfo1;
    }

    @Override
    protected DefinedStructureStructureProcessorType<?> a() {
        return DefinedStructureStructureProcessorType.b;
    }
}
