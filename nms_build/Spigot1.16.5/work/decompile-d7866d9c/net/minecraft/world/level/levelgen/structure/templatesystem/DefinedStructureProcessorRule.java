package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.block.state.IBlockData;

public class DefinedStructureProcessorRule extends DefinedStructureProcessor {

    public static final Codec<DefinedStructureProcessorRule> a = DefinedStructureProcessorPredicates.a.listOf().fieldOf("rules").xmap(DefinedStructureProcessorRule::new, (definedstructureprocessorrule) -> {
        return definedstructureprocessorrule.b;
    }).codec();
    private final ImmutableList<DefinedStructureProcessorPredicates> b;

    public DefinedStructureProcessorRule(List<? extends DefinedStructureProcessorPredicates> list) {
        this.b = ImmutableList.copyOf(list);
    }

    @Nullable
    @Override
    public DefinedStructure.BlockInfo a(IWorldReader iworldreader, BlockPosition blockposition, BlockPosition blockposition1, DefinedStructure.BlockInfo definedstructure_blockinfo, DefinedStructure.BlockInfo definedstructure_blockinfo1, DefinedStructureInfo definedstructureinfo) {
        Random random = new Random(MathHelper.a((BaseBlockPosition) definedstructure_blockinfo1.a));
        IBlockData iblockdata = iworldreader.getType(definedstructure_blockinfo1.a);
        UnmodifiableIterator unmodifiableiterator = this.b.iterator();

        DefinedStructureProcessorPredicates definedstructureprocessorpredicates;

        do {
            if (!unmodifiableiterator.hasNext()) {
                return definedstructure_blockinfo1;
            }

            definedstructureprocessorpredicates = (DefinedStructureProcessorPredicates) unmodifiableiterator.next();
        } while (!definedstructureprocessorpredicates.a(definedstructure_blockinfo1.b, iblockdata, definedstructure_blockinfo.a, definedstructure_blockinfo1.a, blockposition1, random));

        return new DefinedStructure.BlockInfo(definedstructure_blockinfo1.a, definedstructureprocessorpredicates.a(), definedstructureprocessorpredicates.b());
    }

    @Override
    protected DefinedStructureStructureProcessorType<?> a() {
        return DefinedStructureStructureProcessorType.e;
    }
}
