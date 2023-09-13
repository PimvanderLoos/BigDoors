package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class WorldGenFeatureComposite extends WorldGenerator<WorldGenFeatureCompositeConfiguration> {

    public WorldGenFeatureComposite(Codec<WorldGenFeatureCompositeConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureCompositeConfiguration worldgenfeaturecompositeconfiguration) {
        return worldgenfeaturecompositeconfiguration.c.a(generatoraccessseed, structuremanager, chunkgenerator, random, blockposition, worldgenfeaturecompositeconfiguration.b);
    }

    public String toString() {
        return String.format("< %s [%s] >", this.getClass().getSimpleName(), IRegistry.FEATURE.getKey(this));
    }
}
