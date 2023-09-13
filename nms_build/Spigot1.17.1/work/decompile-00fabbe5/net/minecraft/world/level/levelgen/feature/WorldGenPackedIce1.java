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
    public boolean generate(FeaturePlaceContext<WorldGenFeatureCircleConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        ChunkGenerator chunkgenerator = featureplacecontext.b();
        Random random = featureplacecontext.c();
        WorldGenFeatureCircleConfiguration worldgenfeaturecircleconfiguration = (WorldGenFeatureCircleConfiguration) featureplacecontext.e();

        BlockPosition blockposition;

        for (blockposition = featureplacecontext.d(); generatoraccessseed.isEmpty(blockposition) && blockposition.getY() > generatoraccessseed.getMinBuildHeight() + 2; blockposition = blockposition.down()) {
            ;
        }

        return !generatoraccessseed.getType(blockposition).a(Blocks.SNOW_BLOCK) ? false : super.generate(new FeaturePlaceContext<>(generatoraccessseed, chunkgenerator, random, blockposition, worldgenfeaturecircleconfiguration));
    }
}
