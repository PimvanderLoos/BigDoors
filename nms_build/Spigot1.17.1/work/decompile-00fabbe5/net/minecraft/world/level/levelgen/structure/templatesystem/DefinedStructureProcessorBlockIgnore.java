package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class DefinedStructureProcessorBlockIgnore extends DefinedStructureProcessor {

    public static final Codec<DefinedStructureProcessorBlockIgnore> CODEC = IBlockData.CODEC.xmap(BlockBase.BlockData::getBlock, Block::getBlockData).listOf().fieldOf("blocks").xmap(DefinedStructureProcessorBlockIgnore::new, (definedstructureprocessorblockignore) -> {
        return definedstructureprocessorblockignore.toIgnore;
    }).codec();
    public static final DefinedStructureProcessorBlockIgnore STRUCTURE_BLOCK = new DefinedStructureProcessorBlockIgnore(ImmutableList.of(Blocks.STRUCTURE_BLOCK));
    public static final DefinedStructureProcessorBlockIgnore AIR = new DefinedStructureProcessorBlockIgnore(ImmutableList.of(Blocks.AIR));
    public static final DefinedStructureProcessorBlockIgnore STRUCTURE_AND_AIR = new DefinedStructureProcessorBlockIgnore(ImmutableList.of(Blocks.AIR, Blocks.STRUCTURE_BLOCK));
    private final ImmutableList<Block> toIgnore;

    public DefinedStructureProcessorBlockIgnore(List<Block> list) {
        this.toIgnore = ImmutableList.copyOf(list);
    }

    @Nullable
    @Override
    public DefinedStructure.BlockInfo a(IWorldReader iworldreader, BlockPosition blockposition, BlockPosition blockposition1, DefinedStructure.BlockInfo definedstructure_blockinfo, DefinedStructure.BlockInfo definedstructure_blockinfo1, DefinedStructureInfo definedstructureinfo) {
        return this.toIgnore.contains(definedstructure_blockinfo1.state.getBlock()) ? null : definedstructure_blockinfo1;
    }

    @Override
    protected DefinedStructureStructureProcessorType<?> a() {
        return DefinedStructureStructureProcessorType.BLOCK_IGNORE;
    }
}
