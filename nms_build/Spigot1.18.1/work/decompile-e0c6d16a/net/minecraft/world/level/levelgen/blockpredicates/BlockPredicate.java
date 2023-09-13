package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.BiPredicate;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IRegistry;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.FluidType;

public interface BlockPredicate extends BiPredicate<GeneratorAccessSeed, BlockPosition> {

    Codec<BlockPredicate> CODEC = IRegistry.BLOCK_PREDICATE_TYPES.byNameCodec().dispatch(BlockPredicate::type, BlockPredicateType::codec);
    BlockPredicate ONLY_IN_AIR_PREDICATE = matchesBlock(Blocks.AIR, BlockPosition.ZERO);
    BlockPredicate ONLY_IN_AIR_OR_WATER_PREDICATE = matchesBlocks(List.of(Blocks.AIR, Blocks.WATER), BlockPosition.ZERO);

    BlockPredicateType<?> type();

    static BlockPredicate allOf(List<BlockPredicate> list) {
        return new AllOfPredicate(list);
    }

    static BlockPredicate allOf(BlockPredicate... ablockpredicate) {
        return allOf(List.of(ablockpredicate));
    }

    static BlockPredicate allOf(BlockPredicate blockpredicate, BlockPredicate blockpredicate1) {
        return allOf(List.of(blockpredicate, blockpredicate1));
    }

    static BlockPredicate anyOf(List<BlockPredicate> list) {
        return new AnyOfPredicate(list);
    }

    static BlockPredicate anyOf(BlockPredicate... ablockpredicate) {
        return anyOf(List.of(ablockpredicate));
    }

    static BlockPredicate anyOf(BlockPredicate blockpredicate, BlockPredicate blockpredicate1) {
        return anyOf(List.of(blockpredicate, blockpredicate1));
    }

    static BlockPredicate matchesBlocks(List<Block> list, BaseBlockPosition baseblockposition) {
        return new MatchingBlocksPredicate(baseblockposition, list);
    }

    static BlockPredicate matchesBlocks(List<Block> list) {
        return matchesBlocks(list, BaseBlockPosition.ZERO);
    }

    static BlockPredicate matchesBlock(Block block, BaseBlockPosition baseblockposition) {
        return matchesBlocks(List.of(block), baseblockposition);
    }

    static BlockPredicate matchesTag(Tag<Block> tag, BaseBlockPosition baseblockposition) {
        return new MatchingBlockTagPredicate(baseblockposition, tag);
    }

    static BlockPredicate matchesTag(Tag<Block> tag) {
        return matchesTag(tag, BaseBlockPosition.ZERO);
    }

    static BlockPredicate matchesFluids(List<FluidType> list, BaseBlockPosition baseblockposition) {
        return new MatchingFluidsPredicate(baseblockposition, list);
    }

    static BlockPredicate matchesFluid(FluidType fluidtype, BaseBlockPosition baseblockposition) {
        return matchesFluids(List.of(fluidtype), baseblockposition);
    }

    static BlockPredicate not(BlockPredicate blockpredicate) {
        return new NotPredicate(blockpredicate);
    }

    static BlockPredicate replaceable(BaseBlockPosition baseblockposition) {
        return new ReplaceablePredicate(baseblockposition);
    }

    static BlockPredicate replaceable() {
        return replaceable(BaseBlockPosition.ZERO);
    }

    static BlockPredicate wouldSurvive(IBlockData iblockdata, BaseBlockPosition baseblockposition) {
        return new WouldSurvivePredicate(baseblockposition, iblockdata);
    }

    static BlockPredicate hasSturdyFace(BaseBlockPosition baseblockposition, EnumDirection enumdirection) {
        return new HasSturdyFacePredicate(baseblockposition, enumdirection);
    }

    static BlockPredicate hasSturdyFace(EnumDirection enumdirection) {
        return hasSturdyFace(BaseBlockPosition.ZERO, enumdirection);
    }

    static BlockPredicate solid(BaseBlockPosition baseblockposition) {
        return new SolidPredicate(baseblockposition);
    }

    static BlockPredicate solid() {
        return solid(BaseBlockPosition.ZERO);
    }

    static BlockPredicate insideWorld(BaseBlockPosition baseblockposition) {
        return new InsideWorldBoundsPredicate(baseblockposition);
    }

    static BlockPredicate alwaysTrue() {
        return TrueBlockPredicate.INSTANCE;
    }
}
