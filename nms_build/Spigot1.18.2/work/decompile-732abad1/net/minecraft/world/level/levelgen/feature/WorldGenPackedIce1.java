package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureCircleConfiguration;

public class WorldGenPackedIce1 extends WorldGenFeatureDisk {

    public WorldGenPackedIce1(Codec<WorldGenFeatureCircleConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureCircleConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        ChunkGenerator chunkgenerator = featureplacecontext.chunkGenerator();
        Random random = featureplacecontext.random();
        WorldGenFeatureCircleConfiguration worldgenfeaturecircleconfiguration = (WorldGenFeatureCircleConfiguration) featureplacecontext.config();

        BlockPosition blockposition;

        for (blockposition = featureplacecontext.origin(); generatoraccessseed.isEmptyBlock(blockposition) && blockposition.getY() > generatoraccessseed.getMinBuildHeight() + 2; blockposition = blockposition.below()) {
            ;
        }

        return !generatoraccessseed.getBlockState(blockposition).is(Blocks.SNOW_BLOCK) ? false : super.place(new FeaturePlaceContext<>(featureplacecontext.topFeature(), generatoraccessseed, featureplacecontext.chunkGenerator(), featureplacecontext.random(), blockposition, (WorldGenFeatureCircleConfiguration) featureplacecontext.config()));
    }
}
