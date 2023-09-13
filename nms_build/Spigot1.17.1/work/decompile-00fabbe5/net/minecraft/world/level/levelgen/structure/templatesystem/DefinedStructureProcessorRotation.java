package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.IWorldReader;

public class DefinedStructureProcessorRotation extends DefinedStructureProcessor {

    public static final Codec<DefinedStructureProcessorRotation> CODEC = Codec.FLOAT.fieldOf("integrity").orElse(1.0F).xmap(DefinedStructureProcessorRotation::new, (definedstructureprocessorrotation) -> {
        return definedstructureprocessorrotation.integrity;
    }).codec();
    private final float integrity;

    public DefinedStructureProcessorRotation(float f) {
        this.integrity = f;
    }

    @Nullable
    @Override
    public DefinedStructure.BlockInfo a(IWorldReader iworldreader, BlockPosition blockposition, BlockPosition blockposition1, DefinedStructure.BlockInfo definedstructure_blockinfo, DefinedStructure.BlockInfo definedstructure_blockinfo1, DefinedStructureInfo definedstructureinfo) {
        Random random = definedstructureinfo.b(definedstructure_blockinfo1.pos);

        return this.integrity < 1.0F && random.nextFloat() > this.integrity ? null : definedstructure_blockinfo1;
    }

    @Override
    protected DefinedStructureStructureProcessorType<?> a() {
        return DefinedStructureStructureProcessorType.BLOCK_ROT;
    }
}
