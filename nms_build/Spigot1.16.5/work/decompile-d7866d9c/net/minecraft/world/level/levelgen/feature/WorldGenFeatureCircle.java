package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureCircleConfiguration;

public class WorldGenFeatureCircle extends WorldGenFeatureDisk {

    public WorldGenFeatureCircle(Codec<WorldGenFeatureCircleConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureCircleConfiguration worldgenfeaturecircleconfiguration) {
        return !generatoraccessseed.getFluid(blockposition).a((Tag) TagsFluid.WATER) ? false : super.a(generatoraccessseed, chunkgenerator, random, blockposition, worldgenfeaturecircleconfiguration);
    }
}
