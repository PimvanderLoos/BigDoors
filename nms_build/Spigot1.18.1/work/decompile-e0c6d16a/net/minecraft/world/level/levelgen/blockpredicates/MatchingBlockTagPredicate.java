package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsInstance;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

public class MatchingBlockTagPredicate extends StateTestingPredicate {

    final Tag<Block> tag;
    public static final Codec<MatchingBlockTagPredicate> CODEC = RecordCodecBuilder.create((instance) -> {
        return stateTestingCodec(instance).and(Tag.codec(() -> {
            return TagsInstance.getInstance().getOrEmpty(IRegistry.BLOCK_REGISTRY);
        }).fieldOf("tag").forGetter((matchingblocktagpredicate) -> {
            return matchingblocktagpredicate.tag;
        })).apply(instance, MatchingBlockTagPredicate::new);
    });

    protected MatchingBlockTagPredicate(BaseBlockPosition baseblockposition, Tag<Block> tag) {
        super(baseblockposition);
        this.tag = tag;
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
