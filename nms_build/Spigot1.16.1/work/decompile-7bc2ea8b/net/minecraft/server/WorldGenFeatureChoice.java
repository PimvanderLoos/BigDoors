package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class WorldGenFeatureChoice extends WorldGenerator<WorldGenFeatureChoiceConfiguration> {

    public WorldGenFeatureChoice(Codec<WorldGenFeatureChoiceConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureChoiceConfiguration worldgenfeaturechoiceconfiguration) {
        boolean flag = random.nextBoolean();

        return flag ? worldgenfeaturechoiceconfiguration.b.a(generatoraccessseed, structuremanager, chunkgenerator, random, blockposition) : worldgenfeaturechoiceconfiguration.c.a(generatoraccessseed, structuremanager, chunkgenerator, random, blockposition);
    }
}
