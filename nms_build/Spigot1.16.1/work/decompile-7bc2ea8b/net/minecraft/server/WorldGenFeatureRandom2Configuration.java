package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class WorldGenFeatureRandom2Configuration extends WorldGenerator<WorldGenFeatureRandom2> {

    public WorldGenFeatureRandom2Configuration(Codec<WorldGenFeatureRandom2> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureRandom2 worldgenfeaturerandom2) {
        int i = random.nextInt(worldgenfeaturerandom2.b.size());
        WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured = (WorldGenFeatureConfigured) worldgenfeaturerandom2.b.get(i);

        return worldgenfeatureconfigured.a(generatoraccessseed, structuremanager, chunkgenerator, random, blockposition);
    }
}
