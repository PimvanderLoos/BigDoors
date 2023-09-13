package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.BlockSeaPickle;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenDecoratorFrequencyConfiguration;

public class WorldGenFeatureSeaPickel extends WorldGenerator<WorldGenDecoratorFrequencyConfiguration> {

    public WorldGenFeatureSeaPickel(Codec<WorldGenDecoratorFrequencyConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenDecoratorFrequencyConfiguration worldgendecoratorfrequencyconfiguration) {
        int i = 0;
        int j = worldgendecoratorfrequencyconfiguration.a().a(random);

        for (int k = 0; k < j; ++k) {
            int l = random.nextInt(8) - random.nextInt(8);
            int i1 = random.nextInt(8) - random.nextInt(8);
            int j1 = generatoraccessseed.a(HeightMap.Type.OCEAN_FLOOR, blockposition.getX() + l, blockposition.getZ() + i1);
            BlockPosition blockposition1 = new BlockPosition(blockposition.getX() + l, j1, blockposition.getZ() + i1);
            IBlockData iblockdata = (IBlockData) Blocks.SEA_PICKLE.getBlockData().set(BlockSeaPickle.a, random.nextInt(4) + 1);

            if (generatoraccessseed.getType(blockposition1).a(Blocks.WATER) && iblockdata.canPlace(generatoraccessseed, blockposition1)) {
                generatoraccessseed.setTypeAndData(blockposition1, iblockdata, 2);
                ++i;
            }
        }

        return i > 0;
    }
}
