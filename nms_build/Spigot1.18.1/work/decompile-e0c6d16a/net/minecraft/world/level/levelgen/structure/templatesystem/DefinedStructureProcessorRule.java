package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.block.state.IBlockData;

public class DefinedStructureProcessorRule extends DefinedStructureProcessor {

    public static final Codec<DefinedStructureProcessorRule> CODEC = DefinedStructureProcessorPredicates.CODEC.listOf().fieldOf("rules").xmap(DefinedStructureProcessorRule::new, (definedstructureprocessorrule) -> {
        return definedstructureprocessorrule.rules;
    }).codec();
    private final ImmutableList<DefinedStructureProcessorPredicates> rules;

    public DefinedStructureProcessorRule(List<? extends DefinedStructureProcessorPredicates> list) {
        this.rules = ImmutableList.copyOf(list);
    }

    @Nullable
    @Override
    public DefinedStructure.BlockInfo processBlock(IWorldReader iworldreader, BlockPosition blockposition, BlockPosition blockposition1, DefinedStructure.BlockInfo definedstructure_blockinfo, DefinedStructure.BlockInfo definedstructure_blockinfo1, DefinedStructureInfo definedstructureinfo) {
        Random random = new Random(MathHelper.getSeed(definedstructure_blockinfo1.pos));
        IBlockData iblockdata = iworldreader.getBlockState(definedstructure_blockinfo1.pos);
        UnmodifiableIterator unmodifiableiterator = this.rules.iterator();

        DefinedStructureProcessorPredicates definedstructureprocessorpredicates;

        do {
            if (!unmodifiableiterator.hasNext()) {
                return definedstructure_blockinfo1;
            }

            definedstructureprocessorpredicates = (DefinedStructureProcessorPredicates) unmodifiableiterator.next();
        } while (!definedstructureprocessorpredicates.test(definedstructure_blockinfo1.state, iblockdata, definedstructure_blockinfo.pos, definedstructure_blockinfo1.pos, blockposition1, random));

        return new DefinedStructure.BlockInfo(definedstructure_blockinfo1.pos, definedstructureprocessorpredicates.getOutputState(), definedstructureprocessorpredicates.getOutputTag());
    }

    @Override
    protected DefinedStructureStructureProcessorType<?> getType() {
        return DefinedStructureStructureProcessorType.RULE;
    }
}
