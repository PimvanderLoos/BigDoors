package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

class MatchingBlocksPredicate extends StateTestingPredicate {

    private final List<Block> blocks;
    public static final Codec<MatchingBlocksPredicate> CODEC = RecordCodecBuilder.create((instance) -> {
        return stateTestingCodec(instance).and(IRegistry.BLOCK.byNameCodec().listOf().fieldOf("blocks").forGetter((matchingblockspredicate) -> {
            return matchingblockspredicate.blocks;
        })).apply(instance, MatchingBlocksPredicate::new);
    });

    public MatchingBlocksPredicate(BaseBlockPosition baseblockposition, List<Block> list) {
        super(baseblockposition);
        this.blocks = list;
    }

    @Override
    protected boolean test(IBlockData iblockdata) {
        return this.blocks.contains(iblockdata.getBlock());
    }

    @Override
    public BlockPredicateType<?> type() {
        return BlockPredicateType.MATCHING_BLOCKS;
    }
}
