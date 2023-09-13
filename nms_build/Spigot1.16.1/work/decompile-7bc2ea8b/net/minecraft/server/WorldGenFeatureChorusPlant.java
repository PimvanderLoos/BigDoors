package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class WorldGenFeatureChorusPlant extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenFeatureChorusPlant(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        if (generatoraccessseed.isEmpty(blockposition.up()) && generatoraccessseed.getType(blockposition).a(Blocks.END_STONE)) {
            BlockChorusFlower.a(generatoraccessseed, blockposition.up(), random, 8);
            return true;
        } else {
            return false;
        }
    }
}
