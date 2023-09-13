package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeSettingsMobs;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.feature.configurations.StructureSettingsFeature;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureVillageConfiguration;

public class WorldGenFeaturePillagerOutpost extends WorldGenFeatureJigsaw {

    private static final WeightedRandomList<BiomeSettingsMobs.c> OUTPOST_ENEMIES = WeightedRandomList.a((WeightedEntry[]) (new BiomeSettingsMobs.c(EntityTypes.PILLAGER, 1, 1, 1)));

    public WorldGenFeaturePillagerOutpost(Codec<WorldGenFeatureVillageConfiguration> codec) {
        super(codec, 0, true, true);
    }

    @Override
    public WeightedRandomList<BiomeSettingsMobs.c> c() {
        return WorldGenFeaturePillagerOutpost.OUTPOST_ENEMIES;
    }

    protected boolean a(ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, long i, SeededRandom seededrandom, ChunkCoordIntPair chunkcoordintpair, BiomeBase biomebase, ChunkCoordIntPair chunkcoordintpair1, WorldGenFeatureVillageConfiguration worldgenfeaturevillageconfiguration, LevelHeightAccessor levelheightaccessor) {
        int j = chunkcoordintpair.x >> 4;
        int k = chunkcoordintpair.z >> 4;

        seededrandom.setSeed((long) (j ^ k << 4) ^ i);
        seededrandom.nextInt();
        return seededrandom.nextInt(5) != 0 ? false : !this.a(chunkgenerator, i, seededrandom, chunkcoordintpair);
    }

    private boolean a(ChunkGenerator chunkgenerator, long i, SeededRandom seededrandom, ChunkCoordIntPair chunkcoordintpair) {
        StructureSettingsFeature structuresettingsfeature = chunkgenerator.getSettings().a(StructureGenerator.VILLAGE);

        if (structuresettingsfeature == null) {
            return false;
        } else {
            int j = chunkcoordintpair.x;
            int k = chunkcoordintpair.z;

            for (int l = j - 10; l <= j + 10; ++l) {
                for (int i1 = k - 10; i1 <= k + 10; ++i1) {
                    ChunkCoordIntPair chunkcoordintpair1 = StructureGenerator.VILLAGE.a(structuresettingsfeature, i, seededrandom, l, i1);

                    if (l == chunkcoordintpair1.x && i1 == chunkcoordintpair1.z) {
                        return true;
                    }
                }
            }

            return false;
        }
    }
}
