package net.minecraft.server;

import java.util.Random;

public class WorldGenFeatureBlock extends WorldGenerator<WorldGenFeatureBlockConfiguration> {

    public WorldGenFeatureBlock() {}

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureBlockConfiguration worldgenfeatureblockconfiguration) {
        if (worldgenfeatureblockconfiguration.b.contains(generatoraccess.getType(blockposition.down())) && worldgenfeatureblockconfiguration.c.contains(generatoraccess.getType(blockposition)) && worldgenfeatureblockconfiguration.d.contains(generatoraccess.getType(blockposition.up()))) {
            generatoraccess.setTypeAndData(blockposition, worldgenfeatureblockconfiguration.a, 2);
            return true;
        } else {
            return false;
        }
    }
}
