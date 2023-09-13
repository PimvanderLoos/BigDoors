package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;

public class BlockPredicateFilter extends PlacementFilter {

    public static final Codec<BlockPredicateFilter> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BlockPredicate.CODEC.fieldOf("predicate").forGetter((blockpredicatefilter) -> {
            return blockpredicatefilter.predicate;
        })).apply(instance, BlockPredicateFilter::new);
    });
    private final BlockPredicate predicate;

    private BlockPredicateFilter(BlockPredicate blockpredicate) {
        this.predicate = blockpredicate;
    }

    public static BlockPredicateFilter forPredicate(BlockPredicate blockpredicate) {
        return new BlockPredicateFilter(blockpredicate);
    }

    @Override
    protected boolean shouldPlace(PlacementContext placementcontext, RandomSource randomsource, BlockPosition blockposition) {
        return this.predicate.test(placementcontext.getLevel(), blockposition);
    }

    @Override
    public PlacementModifierType<?> type() {
        return PlacementModifierType.BLOCK_PREDICATE_FILTER;
    }
}
