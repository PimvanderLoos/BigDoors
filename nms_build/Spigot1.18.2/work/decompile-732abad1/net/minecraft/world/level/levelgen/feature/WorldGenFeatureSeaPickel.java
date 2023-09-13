package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.BlockSeaPickle;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenDecoratorFrequencyConfiguration;

public class WorldGenFeatureSeaPickel extends WorldGenerator<WorldGenDecoratorFrequencyConfiguration> {

    public WorldGenFeatureSeaPickel(Codec<WorldGenDecoratorFrequencyConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenDecoratorFrequencyConfiguration> featureplacecontext) {
        int i = 0;
        Random random = featureplacecontext.random();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        BlockPosition blockposition = featureplacecontext.origin();
        int j = ((WorldGenDecoratorFrequencyConfiguration) featureplacecontext.config()).count().sample(random);

        for (int k = 0; k < j; ++k) {
            int l = random.nextInt(8) - random.nextInt(8);
            int i1 = random.nextInt(8) - random.nextInt(8);
            int j1 = generatoraccessseed.getHeight(HeightMap.Type.OCEAN_FLOOR, blockposition.getX() + l, blockposition.getZ() + i1);
            BlockPosition blockposition1 = new BlockPosition(blockposition.getX() + l, j1, blockposition.getZ() + i1);
            IBlockData iblockdata = (IBlockData) Blocks.SEA_PICKLE.defaultBlockState().setValue(BlockSeaPickle.PICKLES, random.nextInt(4) + 1);

            if (generatoraccessseed.getBlockState(blockposition1).is(Blocks.WATER) && iblockdata.canSurvive(generatoraccessseed, blockposition1)) {
                generatoraccessseed.setBlock(blockposition1, iblockdata, 2);
                ++i;
            }
        }

        return i > 0;
    }
}
