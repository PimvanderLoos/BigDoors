package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class WorldGenFeatureCircle extends WorldGenFeatureDisk {

    public WorldGenFeatureCircle(Codec<WorldGenFeatureCircleConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureCircleConfiguration worldgenfeaturecircleconfiguration) {
        return !generatoraccessseed.getFluid(blockposition).a((Tag) TagsFluid.WATER) ? false : super.a(generatoraccessseed, chunkgenerator, random, blockposition, worldgenfeaturecircleconfiguration);
    }
}
