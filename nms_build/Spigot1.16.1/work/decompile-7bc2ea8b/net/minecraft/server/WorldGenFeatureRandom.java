package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class WorldGenFeatureRandom extends WorldGenerator<WorldGenFeatureRandomConfiguration> {

    public WorldGenFeatureRandom(Codec<WorldGenFeatureRandomConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureRandomConfiguration worldgenfeaturerandomconfiguration) {
        int i = random.nextInt(5) - 3 + worldgenfeaturerandomconfiguration.c;

        for (int j = 0; j < i; ++j) {
            int k = random.nextInt(worldgenfeaturerandomconfiguration.b.size());
            WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured = (WorldGenFeatureConfigured) worldgenfeaturerandomconfiguration.b.get(k);

            worldgenfeatureconfigured.a(generatoraccessseed, structuremanager, chunkgenerator, random, blockposition);
        }

        return true;
    }
}
