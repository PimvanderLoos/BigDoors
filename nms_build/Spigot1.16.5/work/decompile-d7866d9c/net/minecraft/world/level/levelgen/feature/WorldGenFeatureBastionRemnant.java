package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureVillageConfiguration;

public class WorldGenFeatureBastionRemnant extends WorldGenFeatureJigsaw {

    public WorldGenFeatureBastionRemnant(Codec<WorldGenFeatureVillageConfiguration> codec) {
        super(codec, 33, false, false);
    }

    protected boolean a(ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, long i, SeededRandom seededrandom, int j, int k, BiomeBase biomebase, ChunkCoordIntPair chunkcoordintpair, WorldGenFeatureVillageConfiguration worldgenfeaturevillageconfiguration) {
        return seededrandom.nextInt(5) >= 2;
    }
}
