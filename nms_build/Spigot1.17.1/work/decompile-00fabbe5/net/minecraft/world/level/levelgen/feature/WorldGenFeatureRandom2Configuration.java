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

    @Override
    public boolean generate(FeaturePlaceContext<WorldGenFeatureRandom2> featureplacecontext) {
        Random random = featureplacecontext.c();
        WorldGenFeatureRandom2 worldgenfeaturerandom2 = (WorldGenFeatureRandom2) featureplacecontext.e();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        BlockPosition blockposition = featureplacecontext.d();
        ChunkGenerator chunkgenerator = featureplacecontext.b();
        int i = random.nextInt(worldgenfeaturerandom2.features.size());
        WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured = (WorldGenFeatureConfigured) ((Supplier) worldgenfeaturerandom2.features.get(i)).get();

        return worldgenfeatureconfigured.a(generatoraccessseed, chunkgenerator, random, blockposition);
    }
}
