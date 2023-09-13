package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

public class MatchingBlockTagPredicate extends StateTestingPredicate {

    final TagKey<Block> tag;
    public static final Codec<MatchingBlockTagPredicate> CODEC = RecordCodecBuilder.create((instance) -> {
        return stateTestingCodec(instance).and(TagKey.codec(Registries.BLOCK).fieldOf("tag").forGetter((matchingblocktagpredicate) -> {
            return matchingblocktagpredicate.tag;
        })).apply(instance, MatchingBlockTagPredicate::new);
    });

    protected MatchingBlockTagPredicate(BaseBlockPosition baseblockposition, TagKey<Block> tagkey) {
        super(baseblockposition);
        this.tag = tagkey;
    }

    @Override
    protected boolean test(IBlockData iblockdata) {
        return iblockdata.is(this.tag);
    }

    @Override
    public BlockPredicateType<?> type() {
        return BlockPredicateType.MATCHING_BLOCK_TAG;
    }
}
