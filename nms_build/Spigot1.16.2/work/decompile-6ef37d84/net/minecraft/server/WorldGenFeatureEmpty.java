package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class WorldGenFeatureEmpty extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenFeatureEmpty(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        return true;
    }
}
