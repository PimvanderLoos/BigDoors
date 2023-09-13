package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
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
        Random random = definedstructureinfo.getRandom(definedstructure_blockinfo1.pos);
        IBlockData iblockdata = definedstructure_blockinfo1.state;
        BlockPosition blockposition2 = definedstructure_blockinfo1.pos;
        IBlockData iblockdata1 = null;

        if (!iblockdata.is(Blocks.STONE_BRICKS) && !iblockdata.is(Blocks.STONE) && !iblockdata.is(Blocks.CHISELED_STONE_BRICKS)) {
            if (iblockdata.is((Tag) TagsBlock.STAIRS)) {
                iblockdata1 = this.maybeReplaceStairs(random, definedstructure_blockinfo1.state);
            } else if (iblockdata.is((Tag) TagsBlock.SLABS)) {
                iblockdata1 = this.maybeReplaceSlab(random);
            } else if (iblockdata.is((Tag) TagsBlock.WALLS)) {
                iblockdata1 = this.maybeReplaceWall(random);
            } else if (iblockdata.is(Blocks.OBSIDIAN)) {
                iblockdata1 = this.maybeReplaceObsidian(random);
            }
        } else {
            iblockdata1 = this.maybeReplaceFullStoneBlock(random);
        }

        return iblockdata1 != null ? new DefinedStructure.BlockInfo(blockposition2, iblockdata1, definedstructure_blockinfo1.nbt) : definedstructure_blockinfo1;
    }

    @Nullable
    private IBlockData maybeReplaceFullStoneBlock(Random random) {
        if (random.nextFloat() >= 0.5F) {
            return null;
        } else {
            IBlockData[] aiblockdata = new IBlockData[]{Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), getRandomFacingStairs(random, Blocks.STONE_BRICK_STAIRS)};
            IBlockData[] aiblockdata1 = new IBlockData[]{Blocks.MOSSY_STONE_BRICKS.defaultBlockState(), getRandomFacingStairs(random, Blocks.MOSSY_STONE_BRICK_STAIRS)};

            return this.getRandomBlock(random, aiblockdata, aiblockdata1);
        }
    }

    @Nullable
    private IBlockData maybeReplaceStairs(Random random, IBlockData iblockdata) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.getValue(BlockStairs.FACING);
        BlockPropertyHalf blockpropertyhalf = (BlockPropertyHalf) iblockdata.getValue(BlockStairs.HALF);

        if (random.nextFloat() >= 0.5F) {
            return null;
        } else {
            IBlockData[] aiblockdata = new IBlockData[]{(IBlockData) ((IBlockData) Blocks.MOSSY_STONE_BRICK_STAIRS.defaultBlockState().setValue(BlockStairs.FACING, enumdirection)).setValue(BlockStairs.HALF, blockpropertyhalf), Blocks.MOSSY_STONE_BRICK_SLAB.defaultBlockState()};

            return this.getRandomBlock(random, DefinedStructureProcessorBlockAge.NON_MOSSY_REPLACEMENTS, aiblockdata);
        }
    }

    @Nullable
    private IBlockData maybeReplaceSlab(Random random) {
        return random.nextFloat() < this.mossiness ? Blocks.MOSSY_STONE_BRICK_SLAB.defaultBlockState() : null;
    }

    @Nullable
    private IBlockData maybeReplaceWall(Random random) {
        return random.nextFloat() < this.mossiness ? Blocks.MOSSY_STONE_BRICK_WALL.defaultBlockState() : null;
    }

    @Nullable
    private IBlockData maybeReplaceObsidian(Random random) {
        return random.nextFloat() < 0.15F ? Blocks.CRYING_OBSIDIAN.defaultBlockState() : null;
    }

    private static IBlockData getRandomFacingStairs(Random random, Block block) {
        return (IBlockData) ((IBlockData) block.defaultBlockState().setValue(BlockStairs.FACING, EnumDirection.EnumDirectionLimit.HORIZONTAL.getRandomDirection(random))).setValue(BlockStairs.HALF, BlockPropertyHalf.values()[random.nextInt(BlockPropertyHalf.values().length)]);
    }

    private IBlockData getRandomBlock(Random random, IBlockData[] aiblockdata, IBlockData[] aiblockdata1) {
        return random.nextFloat() < this.mossiness ? getRandomBlock(random, aiblockdata1) : getRandomBlock(random, aiblockdata);
    }

    private static IBlockData getRandomBlock(Random random, IBlockData[] aiblockdata) {
        return aiblockdata[random.nextInt(aiblockdata.length)];
    }

    @Override
    protected DefinedStructureStructureProcessorType<?> getType() {
        return DefinedStructureStructureProcessorType.BLOCK_AGE;
    }
}
