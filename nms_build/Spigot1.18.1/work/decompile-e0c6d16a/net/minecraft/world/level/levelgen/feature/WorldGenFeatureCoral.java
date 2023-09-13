package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockCoralFanWallAbstract;
import net.minecraft.world.level.block.BlockSeaPickle;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;

public abstract class WorldGenFeatureCoral extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenFeatureCoral(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureEmptyConfiguration> featureplacecontext) {
        Random random = featureplacecontext.random();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        BlockPosition blockposition = featureplacecontext.origin();
        IBlockData iblockdata = ((Block) TagsBlock.CORAL_BLOCKS.getRandomElement(random)).defaultBlockState();

        return this.placeFeature(generatoraccessseed, random, blockposition, iblockdata);
    }

    protected abstract boolean placeFeature(GeneratorAccess generatoraccess, Random random, BlockPosition blockposition, IBlockData iblockdata);

    protected boolean placeCoralBlock(GeneratorAccess generatoraccess, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        BlockPosition blockposition1 = blockposition.above();
        IBlockData iblockdata1 = generatoraccess.getBlockState(blockposition);

        if ((iblockdata1.is(Blocks.WATER) || iblockdata1.is((Tag) TagsBlock.CORALS)) && generatoraccess.getBlockState(blockposition1).is(Blocks.WATER)) {
            generatoraccess.setBlock(blockposition, iblockdata, 3);
            if (random.nextFloat() < 0.25F) {
                generatoraccess.setBlock(blockposition1, ((Block) TagsBlock.CORALS.getRandomElement(random)).defaultBlockState(), 2);
            } else if (random.nextFloat() < 0.05F) {
                generatoraccess.setBlock(blockposition1, (IBlockData) Blocks.SEA_PICKLE.defaultBlockState().setValue(BlockSeaPickle.PICKLES, random.nextInt(4) + 1), 2);
            }

            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                EnumDirection enumdirection = (EnumDirection) iterator.next();

                if (random.nextFloat() < 0.2F) {
                    BlockPosition blockposition2 = blockposition.relative(enumdirection);

                    if (generatoraccess.getBlockState(blockposition2).is(Blocks.WATER)) {
                        IBlockData iblockdata2 = ((Block) TagsBlock.WALL_CORALS.getRandomElement(random)).defaultBlockState();

                        if (iblockdata2.hasProperty(BlockCoralFanWallAbstract.FACING)) {
                            iblockdata2 = (IBlockData) iblockdata2.setValue(BlockCoralFanWallAbstract.FACING, enumdirection);
                        }

                        generatoraccess.setBlock(blockposition2, iblockdata2, 2);
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }
}
