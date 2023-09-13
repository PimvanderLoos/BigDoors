package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class WorldGenPackedIce1 extends WorldGenFeatureDisk {

    public WorldGenPackedIce1(Codec<WorldGenFeatureCircleConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureCircleConfiguration worldgenfeaturecircleconfiguration) {
        while (generatoraccessseed.isEmpty(blockposition) && blockposition.getY() > 2) {
            blockposition = blockposition.down();
        }

        if (!generatoraccessseed.getType(blockposition).a(Blocks.SNOW_BLOCK)) {
            return false;
        } else {
            return super.a(generatoraccessseed, chunkgenerator, random, blockposition, worldgenfeaturecircleconfiguration);
        }
    }
}
