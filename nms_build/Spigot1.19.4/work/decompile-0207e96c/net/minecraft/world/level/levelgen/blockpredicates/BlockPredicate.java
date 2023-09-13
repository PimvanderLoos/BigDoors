package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.BiPredicate;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;

public interface BlockPredicate extends BiPredicate<GeneratorAccessSeed, BlockPosition> {

    Codec<BlockPredicate> CODEC = BuiltInRegistries.BLOCK_PREDICATE_TYPE.byNameCodec().dispatch(BlockPredicate::type, BlockPredicateType::codec);
    BlockPredicate ONLY_IN_AIR_PREDICATE = matchesBlocks(Blocks.AIR);
    BlockPredicate ONLY_IN_AIR_OR_WATER_PREDICATE = matchesBlocks(Blocks.AIR, Blocks.WATER);

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

    static BlockPredicate matchesBlocks(BaseBlockPosition baseblockposition, List<Block> list) {
        return new MatchingBlocksPredicate(baseblockposition, HolderSet.direct(Block::builtInRegistryHolder, list));
    }

    static BlockPredicate matchesBlocks(List<Block> list) {
        return matchesBlocks(BaseBlockPosition.ZERO, list);
    }

    static BlockPredicate matchesBlocks(BaseBlockPosition baseblockposition, Block... ablock) {
        return matchesBlocks(baseblockposition, List.of(ablock));
    }

    static BlockPredicate matchesBlocks(Block... ablock) {
        return matchesBlocks(BaseBlockPosition.ZERO, ablock);
    }

    static BlockPredicate matchesTag(BaseBlockPosition baseblockposition, TagKey<Block> tagkey) {
        return new MatchingBlockTagPredicate(baseblockposition, tagkey);
    }

    static BlockPredicate matchesTag(TagKey<Block> tagkey) {
        return matchesTag(BaseBlockPosition.ZERO, tagkey);
    }

    static BlockPredicate matchesFluids(BaseBlockPosition baseblockposition, List<FluidType> list) {
        return new MatchingFluidsPredicate(baseblockposition, HolderSet.direct(FluidType::builtInRegistryHolder, list));
    }

    static BlockPredicate matchesFluids(BaseBlockPosition baseblockposition, FluidType... afluidtype) {
        return matchesFluids(baseblockposition, List.of(afluidtype));
    }

    static BlockPredicate matchesFluids(FluidType... afluidtype) {
        return matchesFluids(BaseBlockPosition.ZERO, afluidtype);
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

    static BlockPredicate noFluid() {
        return noFluid(BaseBlockPosition.ZERO);
    }

    static BlockPredicate noFluid(BaseBlockPosition baseblockposition) {
        return matchesFluids(baseblockposition, FluidTypes.EMPTY);
    }

    static BlockPredicate insideWorld(BaseBlockPosition baseblockposition) {
        return new InsideWorldBoundsPredicate(baseblockposition);
    }

    static BlockPredicate alwaysTrue() {
        return TrueBlockPredicate.INSTANCE;
    }
}
