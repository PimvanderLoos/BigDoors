package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

class MatchingBlocksPredicate extends StateTestingPredicate {

    private final HolderSet<Block> blocks;
    public static final Codec<MatchingBlocksPredicate> CODEC = RecordCodecBuilder.create((instance) -> {
        return stateTestingCodec(instance).and(RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("blocks").forGetter((matchingblockspredicate) -> {
            return matchingblockspredicate.blocks;
        })).apply(instance, MatchingBlocksPredicate::new);
    });

    public MatchingBlocksPredicate(BaseBlockPosition baseblockposition, HolderSet<Block> holderset) {
        super(baseblockposition);
        this.blocks = holderset;
    }

    @Override
    protected boolean test(IBlockData iblockdata) {
        return iblockdata.is(this.blocks);
    }

    @Override
    public BlockPredicateType<?> type() {
        return BlockPredicateType.MATCHING_BLOCKS;
    }
}
