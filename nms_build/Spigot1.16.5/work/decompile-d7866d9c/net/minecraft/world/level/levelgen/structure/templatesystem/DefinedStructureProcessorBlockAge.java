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

    public static final Codec<DefinedStructureProcessorBlockAge> a = Codec.FLOAT.fieldOf("mossiness").xmap(DefinedStructureProcessorBlockAge::new, (definedstructureprocessorblockage) -> {
        return definedstructureprocessorblockage.b;
    }).codec();
    private final float b;

    public DefinedStructureProcessorBlockAge(float f) {
        this.b = f;
    }

    @Nullable
    @Override
    public DefinedStructure.BlockInfo a(IWorldReader iworldreader, BlockPosition blockposition, BlockPosition blockposition1, DefinedStructure.BlockInfo definedstructure_blockinfo, DefinedStructure.BlockInfo definedstructure_blockinfo1, DefinedStructureInfo definedstructureinfo) {
        Random random = definedstructureinfo.b(definedstructure_blockinfo1.a);
        IBlockData iblockdata = definedstructure_blockinfo1.b;
        BlockPosition blockposition2 = definedstructure_blockinfo1.a;
        IBlockData iblockdata1 = null;

        if (!iblockdata.a(Blocks.STONE_BRICKS) && !iblockdata.a(Blocks.STONE) && !iblockdata.a(Blocks.CHISELED_STONE_BRICKS)) {
            if (iblockdata.a((Tag) TagsBlock.STAIRS)) {
                iblockdata1 = this.a(random, definedstructure_blockinfo1.b);
            } else if (iblockdata.a((Tag) TagsBlock.SLABS)) {
                iblockdata1 = this.b(random);
            } else if (iblockdata.a((Tag) TagsBlock.WALLS)) {
                iblockdata1 = this.c(random);
            } else if (iblockdata.a(Blocks.OBSIDIAN)) {
                iblockdata1 = this.d(random);
            }
        } else {
            iblockdata1 = this.a(random);
        }

        return iblockdata1 != null ? new DefinedStructure.BlockInfo(blockposition2, iblockdata1, definedstructure_blockinfo1.c) : definedstructure_blockinfo1;
    }

    @Nullable
    private IBlockData a(Random random) {
        if (random.nextFloat() >= 0.5F) {
            return null;
        } else {
            IBlockData[] aiblockdata = new IBlockData[]{Blocks.CRACKED_STONE_BRICKS.getBlockData(), a(random, Blocks.STONE_BRICK_STAIRS)};
            IBlockData[] aiblockdata1 = new IBlockData[]{Blocks.MOSSY_STONE_BRICKS.getBlockData(), a(random, Blocks.MOSSY_STONE_BRICK_STAIRS)};

            return this.a(random, aiblockdata, aiblockdata1);
        }
    }

    @Nullable
    private IBlockData a(Random random, IBlockData iblockdata) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockStairs.FACING);
        BlockPropertyHalf blockpropertyhalf = (BlockPropertyHalf) iblockdata.get(BlockStairs.HALF);

        if (random.nextFloat() >= 0.5F) {
            return null;
        } else {
            IBlockData[] aiblockdata = new IBlockData[]{Blocks.STONE_SLAB.getBlockData(), Blocks.STONE_BRICK_SLAB.getBlockData()};
            IBlockData[] aiblockdata1 = new IBlockData[]{(IBlockData) ((IBlockData) Blocks.MOSSY_STONE_BRICK_STAIRS.getBlockData().set(BlockStairs.FACING, enumdirection)).set(BlockStairs.HALF, blockpropertyhalf), Blocks.MOSSY_STONE_BRICK_SLAB.getBlockData()};

            return this.a(random, aiblockdata, aiblockdata1);
        }
    }

    @Nullable
    private IBlockData b(Random random) {
        return random.nextFloat() < this.b ? Blocks.MOSSY_STONE_BRICK_SLAB.getBlockData() : null;
    }

    @Nullable
    private IBlockData c(Random random) {
        return random.nextFloat() < this.b ? Blocks.MOSSY_STONE_BRICK_WALL.getBlockData() : null;
    }

    @Nullable
    private IBlockData d(Random random) {
        return random.nextFloat() < 0.15F ? Blocks.CRYING_OBSIDIAN.getBlockData() : null;
    }

    private static IBlockData a(Random random, Block block) {
        return (IBlockData) ((IBlockData) block.getBlockData().set(BlockStairs.FACING, EnumDirection.EnumDirectionLimit.HORIZONTAL.a(random))).set(BlockStairs.HALF, BlockPropertyHalf.values()[random.nextInt(BlockPropertyHalf.values().length)]);
    }

    private IBlockData a(Random random, IBlockData[] aiblockdata, IBlockData[] aiblockdata1) {
        return random.nextFloat() < this.b ? a(random, aiblockdata1) : a(random, aiblockdata);
    }

    private static IBlockData a(Random random, IBlockData[] aiblockdata) {
        return aiblockdata[random.nextInt(aiblockdata.length)];
    }

    @Override
    protected DefinedStructureStructureProcessorType<?> a() {
        return DefinedStructureStructureProcessorType.g;
    }
}
