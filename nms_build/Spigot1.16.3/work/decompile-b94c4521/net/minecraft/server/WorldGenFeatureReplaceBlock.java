package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class WorldGenFeatureReplaceBlock extends WorldGenerator<WorldGenFeatureReplaceBlockConfiguration> {

    public WorldGenFeatureReplaceBlock(Codec<WorldGenFeatureReplaceBlockConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureReplaceBlockConfiguration worldgenfeaturereplaceblockconfiguration) {
        if (generatoraccessseed.getType(blockposition).a(worldgenfeaturereplaceblockconfiguration.b.getBlock())) {
            generatoraccessseed.setTypeAndData(blockposition, worldgenfeaturereplaceblockconfiguration.c, 2);
        }

        return true;
    }
}
