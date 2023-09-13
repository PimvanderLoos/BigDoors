package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class WorldGenFeatureBlock extends WorldGenerator<WorldGenFeatureBlockConfiguration> {

    public WorldGenFeatureBlock(Codec<WorldGenFeatureBlockConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureBlockConfiguration worldgenfeatureblockconfiguration) {
        if (worldgenfeatureblockconfiguration.c.contains(generatoraccessseed.getType(blockposition.down())) && worldgenfeatureblockconfiguration.d.contains(generatoraccessseed.getType(blockposition)) && worldgenfeatureblockconfiguration.e.contains(generatoraccessseed.getType(blockposition.up()))) {
            generatoraccessseed.setTypeAndData(blockposition, worldgenfeatureblockconfiguration.b, 2);
            return true;
        } else {
            return false;
        }
    }
}
