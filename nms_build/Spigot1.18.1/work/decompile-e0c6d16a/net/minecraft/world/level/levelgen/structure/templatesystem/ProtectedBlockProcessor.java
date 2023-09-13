package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;

public class ProtectedBlockProcessor extends DefinedStructureProcessor {

    public final MinecraftKey cannotReplace;
    public static final Codec<ProtectedBlockProcessor> CODEC = MinecraftKey.CODEC.xmap(ProtectedBlockProcessor::new, (protectedblockprocessor) -> {
        return protectedblockprocessor.cannotReplace;
    });

    public ProtectedBlockProcessor(MinecraftKey minecraftkey) {
        this.cannotReplace = minecraftkey;
    }

    @Nullable
    @Override
    public DefinedStructure.BlockInfo processBlock(IWorldReader iworldreader, BlockPosition blockposition, BlockPosition blockposition1, DefinedStructure.BlockInfo definedstructure_blockinfo, DefinedStructure.BlockInfo definedstructure_blockinfo1, DefinedStructureInfo definedstructureinfo) {
        return WorldGenerator.isReplaceable(this.cannotReplace).test(iworldreader.getBlockState(definedstructure_blockinfo1.pos)) ? definedstructure_blockinfo1 : null;
    }

    @Override
    protected DefinedStructureStructureProcessorType<?> getType() {
        return DefinedStructureStructureProcessorType.PROTECTED_BLOCKS;
    }
}
