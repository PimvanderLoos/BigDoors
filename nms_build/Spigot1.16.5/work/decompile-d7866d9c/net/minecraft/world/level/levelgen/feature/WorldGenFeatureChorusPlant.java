package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.BlockChorusFlower;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;

public class WorldGenFeatureChorusPlant extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenFeatureChorusPlant(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        if (generatoraccessseed.isEmpty(blockposition) && generatoraccessseed.getType(blockposition.down()).a(Blocks.END_STONE)) {
            BlockChorusFlower.a(generatoraccessseed, blockposition, random, 8);
            return true;
        } else {
            return false;
        }
    }
}
