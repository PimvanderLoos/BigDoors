package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.RandomSource;
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
        RandomSource randomsource = featureplacecontext.random();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        BlockPosition blockposition = featureplacecontext.origin();
        Optional<Block> optional = BuiltInRegistries.BLOCK.getTag(TagsBlock.CORAL_BLOCKS).flatMap((holderset_named) -> {
            return holderset_named.getRandomElement(randomsource);
        }).map(Holder::value);

        return optional.isEmpty() ? false : this.placeFeature(generatoraccessseed, randomsource, blockposition, ((Block) optional.get()).defaultBlockState());
    }

    protected abstract boolean placeFeature(GeneratorAccess generatoraccess, RandomSource randomsource, BlockPosition blockposition, IBlockData iblockdata);

    protected boolean placeCoralBlock(GeneratorAccess generatoraccess, RandomSource randomsource, BlockPosition blockposition, IBlockData iblockdata) {
        BlockPosition blockposition1 = blockposition.above();
        IBlockData iblockdata1 = generatoraccess.getBlockState(blockposition);

        if ((iblockdata1.is(Blocks.WATER) || iblockdata1.is(TagsBlock.CORALS)) && generatoraccess.getBlockState(blockposition1).is(Blocks.WATER)) {
            generatoraccess.setBlock(blockposition, iblockdata, 3);
            if (randomsource.nextFloat() < 0.25F) {
                BuiltInRegistries.BLOCK.getTag(TagsBlock.CORALS).flatMap((holderset_named) -> {
                    return holderset_named.getRandomElement(randomsource);
                }).map(Holder::value).ifPresent((block) -> {
                    generatoraccess.setBlock(blockposition1, block.defaultBlockState(), 2);
                });
            } else if (randomsource.nextFloat() < 0.05F) {
                generatoraccess.setBlock(blockposition1, (IBlockData) Blocks.SEA_PICKLE.defaultBlockState().setValue(BlockSeaPickle.PICKLES, randomsource.nextInt(4) + 1), 2);
            }

            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                EnumDirection enumdirection = (EnumDirection) iterator.next();

                if (randomsource.nextFloat() < 0.2F) {
                    BlockPosition blockposition2 = blockposition.relative(enumdirection);

                    if (generatoraccess.getBlockState(blockposition2).is(Blocks.WATER)) {
                        BuiltInRegistries.BLOCK.getTag(TagsBlock.WALL_CORALS).flatMap((holderset_named) -> {
                            return holderset_named.getRandomElement(randomsource);
                        }).map(Holder::value).ifPresent((block) -> {
                            IBlockData iblockdata2 = block.defaultBlockState();

                            if (iblockdata2.hasProperty(BlockCoralFanWallAbstract.FACING)) {
                                iblockdata2 = (IBlockData) iblockdata2.setValue(BlockCoralFanWallAbstract.FACING, enumdirection);
                            }

                            generatoraccess.setBlock(blockposition2, iblockdata2, 2);
                        });
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }
}
