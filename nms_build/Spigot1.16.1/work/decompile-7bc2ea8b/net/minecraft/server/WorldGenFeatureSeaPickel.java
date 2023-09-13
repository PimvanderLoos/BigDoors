package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class WorldGenFeatureSeaPickel extends WorldGenerator<WorldGenFeatureKelpConfiguration> {

    public WorldGenFeatureSeaPickel(Codec<WorldGenFeatureKelpConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureKelpConfiguration worldgenfeaturekelpconfiguration) {
        int i = 0;

        for (int j = 0; j < worldgenfeaturekelpconfiguration.b; ++j) {
            int k = random.nextInt(8) - random.nextInt(8);
            int l = random.nextInt(8) - random.nextInt(8);
            int i1 = generatoraccessseed.a(HeightMap.Type.OCEAN_FLOOR, blockposition.getX() + k, blockposition.getZ() + l);
            BlockPosition blockposition1 = new BlockPosition(blockposition.getX() + k, i1, blockposition.getZ() + l);
            IBlockData iblockdata = (IBlockData) Blocks.SEA_PICKLE.getBlockData().set(BlockSeaPickle.a, random.nextInt(4) + 1);

            if (generatoraccessseed.getType(blockposition1).a(Blocks.WATER) && iblockdata.canPlace(generatoraccessseed, blockposition1)) {
                generatoraccessseed.setTypeAndData(blockposition1, iblockdata, 2);
                ++i;
            }
        }

        return i > 0;
    }
}
