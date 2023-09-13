package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureReplaceBlockConfiguration;

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
