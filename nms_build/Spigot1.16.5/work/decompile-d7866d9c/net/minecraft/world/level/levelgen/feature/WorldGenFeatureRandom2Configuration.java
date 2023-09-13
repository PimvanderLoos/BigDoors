package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRandom2;

public class WorldGenFeatureRandom2Configuration extends WorldGenerator<WorldGenFeatureRandom2> {

    public WorldGenFeatureRandom2Configuration(Codec<WorldGenFeatureRandom2> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureRandom2 worldgenfeaturerandom2) {
        int i = random.nextInt(worldgenfeaturerandom2.b.size());
        WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured = (WorldGenFeatureConfigured) ((Supplier) worldgenfeaturerandom2.b.get(i)).get();

        return worldgenfeatureconfigured.a(generatoraccessseed, chunkgenerator, random, blockposition);
    }
}
