package net.minecraft.server;

import com.mojang.serialization.Codec;

public class WorldGenFeatureBastionRemnant extends WorldGenFeatureJigsaw {

    public WorldGenFeatureBastionRemnant(Codec<WorldGenFeatureVillageConfiguration> codec) {
        super(codec, 33, false, false);
    }

    protected boolean a(ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, long i, SeededRandom seededrandom, int j, int k, BiomeBase biomebase, ChunkCoordIntPair chunkcoordintpair, WorldGenFeatureVillageConfiguration worldgenfeaturevillageconfiguration) {
        return seededrandom.nextInt(5) >= 2;
    }
}
