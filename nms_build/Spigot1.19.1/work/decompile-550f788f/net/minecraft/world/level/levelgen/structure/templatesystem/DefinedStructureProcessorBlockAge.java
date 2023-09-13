package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockStairs;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockPropertyHalf;

public class DefinedStructureProcessorBlockAge extends DefinedStructureProcessor {

    public static final Codec<DefinedStructureProcessorBlockAge> CODEC = Codec.FLOAT.fieldOf("mossiness").xmap(DefinedStructureProcessorBlockAge::new, (definedstructureprocessorblockage) -> {
        return definedstructureprocessorblockage.mossiness;
    }).codec();
    private static final float PROBABILITY_OF_REPLACING_FULL_BLOCK = 0.5F;
    private static final float PROBABILITY_OF_REPLACING_STAIRS = 0.5F;
    private static final float PROBABILITY_OF_REPLACING_OBSIDIAN = 0.15F;
    private static final IBlockData[] NON_MOSSY_REPLACEMENTS = new IBlockData[]{Blocks.STONE_SLAB.defaultBlockState(), Blocks.STONE_BRICK_SLAB.defaultBlockState()};
    private final float mossiness;

    public DefinedStructureProcessorBlockAge(float f) {
        this.mossiness = f;
    }

    @Nullable
    @Override
    public DefinedStructure.BlockInfo processBlock(IWorldReader iworldreader, BlockPosition blockposition, BlockPosition blockposition1, DefinedStructure.BlockInfo definedstructure_blockinfo, DefinedStructure.BlockInfo definedstructure_blockinfo1, DefinedStructureInfo definedstructureinfo) {
        RandomSource randomsource = definedstructureinfo.getRandom(definedstructure_blockinfo1.pos);
        IBlockData iblockdata = definedstructure_blockinfo1.state;
        BlockPosition blockposition2 = definedstructure_blockinfo1.pos;
        IBlockData iblockdata1 = null;

        if (!iblockdata.is(Blocks.STONE_BRICKS) && !iblockdata.is(Blocks.STONE) && !iblockdata.is(Blocks.CHISELED_STONE_BRICKS)) {
            if (iblockdata.is(TagsBlock.STAIRS)) {
                iblockdata1 = this.maybeReplaceStairs(randomsource, definedstructure_blockinfo1.state);
            } else if (iblockdata.is(TagsBlock.SLABS)) {
                iblockdata1 = this.maybeReplaceSlab(randomsource);
            } else if (iblockdata.is(TagsBlock.WALLS)) {
                iblockdata1 = this.maybeReplaceWall(randomsource);
            } else if (iblockdata.is(Blocks.OBSIDIAN)) {
                iblockdata1 = this.maybeReplaceObsidian(randomsource);
            }
        } else {
            iblockdata1 = this.maybeReplaceFullStoneBlock(randomsource);
        }

        return iblockdata1 != null ? new DefinedStructure.BlockInfo(blockposition2, iblockdata1, definedstructure_blockinfo1.nbt) : definedstructure_blockinfo1;
    }

    @Nullable
    private IBlockData maybeReplaceFullStoneBlock(RandomSource randomsource) {
        if (randomsource.nextFloat() >= 0.5F) {
            return null;
        } else {
            IBlockData[] aiblockdata = new IBlockData[]{Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), getRandomFacingStairs(randomsource, Blocks.STONE_BRICK_STAIRS)};
            IBlockData[] aiblockdata1 = new IBlockData[]{Blocks.MOSSY_STONE_BRICKS.defaultBlockState(), getRandomFacingStairs(randomsource, Blocks.MOSSY_STONE_BRICK_STAIRS)};

            return this.getRandomBlock(randomsource, aiblockdata, aiblockdata1);
        }
    }

    @Nullable
    private IBlockData maybeReplaceStairs(RandomSource randomsource, IBlockData iblockdata) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.getValue(BlockStairs.FACING);
        BlockPropertyHalf blockpropertyhalf = (BlockPropertyHalf) iblockdata.getValue(BlockStairs.HALF);

        if (randomsource.nextFloat() >= 0.5F) {
            return null;
        } else {
            IBlockData[] aiblockdata = new IBlockData[]{(IBlockData) ((IBlockData) Blocks.MOSSY_STONE_BRICK_STAIRS.defaultBlockState().setValue(BlockStairs.FACING, enumdirection)).setValue(BlockStairs.HALF, blockpropertyhalf), Blocks.MOSSY_STONE_BRICK_SLAB.defaultBlockState()};

            return this.getRandomBlock(randomsource, DefinedStructureProcessorBlockAge.NON_MOSSY_REPLACEMENTS, aiblockdata);
        }
    }

    @Nullable
    private IBlockData maybeReplaceSlab(RandomSource randomsource) {
        return randomsource.nextFloat() < this.mossiness ? Blocks.MOSSY_STONE_BRICK_SLAB.defaultBlockState() : null;
    }

    @Nullable
    private IBlockData maybeReplaceWall(RandomSource randomsource) {
        return randomsource.nextFloat() < this.mossiness ? Blocks.MOSSY_STONE_BRICK_WALL.defaultBlockState() : null;
    }

    @Nullable
    private IBlockData maybeReplaceObsidian(RandomSource randomsource) {
        return randomsource.nextFloat() < 0.15F ? Blocks.CRYING_OBSIDIAN.defaultBlockState() : null;
    }

    private static IBlockData getRandomFacingStairs(RandomSource randomsource, Block block) {
        return (IBlockData) ((IBlockData) block.defaultBlockState().setValue(BlockStairs.FACING, EnumDirection.EnumDirectionLimit.HORIZONTAL.getRandomDirection(randomsource))).setValue(BlockStairs.HALF, BlockPropertyHalf.values()[randomsource.nextInt(BlockPropertyHalf.values().length)]);
    }

    private IBlockData getRandomBlock(RandomSource randomsource, IBlockData[] aiblockdata, IBlockData[] aiblockdata1) {
        return randomsource.nextFloat() < this.mossiness ? getRandomBlock(randomsource, aiblockdata1) : getRandomBlock(randomsource, aiblockdata);
    }

    private static IBlockData getRandomBlock(RandomSource randomsource, IBlockData[] aiblockdata) {
        return aiblockdata[randomsource.nextInt(aiblockdata.length)];
    }

    @Override
    protected DefinedStructureStructureProcessorType<?> getType() {
        return DefinedStructureStructureProcessorType.BLOCK_AGE;
    }
}
