package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class WorldGenFeatureComposite extends WorldGenerator<WorldGenFeatureCompositeConfiguration> {

    public WorldGenFeatureComposite(Codec<WorldGenFeatureCompositeConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureCompositeConfiguration worldgenfeaturecompositeconfiguration) {
        MutableBoolean mutableboolean = new MutableBoolean();

        worldgenfeaturecompositeconfiguration.c.a(new WorldGenDecoratorContext(generatoraccessseed, chunkgenerator), random, blockposition).forEach((blockposition1) -> {
            if (((WorldGenFeatureConfigured) worldgenfeaturecompositeconfiguration.b.get()).a(generatoraccessseed, chunkgenerator, random, blockposition1)) {
                mutableboolean.setTrue();
            }

        });
        return mutableboolean.isTrue();
    }

    public String toString() {
        return String.format("< %s [%s] >", this.getClass().getSimpleName(), IRegistry.FEATURE.getKey(this));
    }
}
