package net.minecraft.server;

import java.util.Random;

public class BiomeForestMutated extends BiomeForest {

    public BiomeForestMutated(BiomeBase.a biomebase_a) {
        super(BiomeForest.Type.BIRCH, biomebase_a);
    }

    public WorldGenTreeAbstract a(Random random) {
        return random.nextBoolean() ? BiomeForest.x : BiomeForest.y;
    }
}
