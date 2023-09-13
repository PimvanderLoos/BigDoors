package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureChoiceConfiguration;

public class WorldGenFeatureChoice extends WorldGenerator<WorldGenFeatureChoiceConfiguration> {

    public WorldGenFeatureChoice(Codec<WorldGenFeatureChoiceConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureChoiceConfiguration worldgenfeaturechoiceconfiguration) {
        boolean flag = random.nextBoolean();

        return flag ? ((WorldGenFeatureConfigured) worldgenfeaturechoiceconfiguration.b.get()).a(generatoraccessseed, chunkgenerator, random, blockposition) : ((WorldGenFeatureConfigured) worldgenfeaturechoiceconfiguration.c.get()).a(generatoraccessseed, chunkgenerator, random, blockposition);
    }
}
