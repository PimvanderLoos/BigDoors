package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;

public class WorldGenFeaturePillagerOutpost extends WorldGenFeatureJigsaw {

    private static final List<BiomeSettingsMobs.c> u = ImmutableList.of(new BiomeSettingsMobs.c(EntityTypes.PILLAGER, 1, 1, 1));

    public WorldGenFeaturePillagerOutpost(Codec<WorldGenFeatureVillageConfiguration> codec) {
        super(codec, 0, true, true);
    }

    @Override
    public List<BiomeSettingsMobs.c> c() {
        return WorldGenFeaturePillagerOutpost.u;
    }

    protected boolean a(ChunkGenerator chunkgenerator, WorldChunkManager worldchunkmanager, long i, SeededRandom seededrandom, int j, int k, BiomeBase biomebase, ChunkCoordIntPair chunkcoordintpair, WorldGenFeatureVillageConfiguration worldgenfeaturevillageconfiguration) {
        int l = j >> 4;
        int i1 = k >> 4;

        seededrandom.setSeed((long) (l ^ i1 << 4) ^ i);
        seededrandom.nextInt();
        return seededrandom.nextInt(5) != 0 ? false : !this.a(chunkgenerator, i, seededrandom, j, k);
    }

    private boolean a(ChunkGenerator chunkgenerator, long i, SeededRandom seededrandom, int j, int k) {
        StructureSettingsFeature structuresettingsfeature = chunkgenerator.getSettings().a(StructureGenerator.VILLAGE);

        if (structuresettingsfeature == null) {
            return false;
        } else {
            for (int l = j - 10; l <= j + 10; ++l) {
                for (int i1 = k - 10; i1 <= k + 10; ++i1) {
                    ChunkCoordIntPair chunkcoordintpair = StructureGenerator.VILLAGE.a(structuresettingsfeature, i, seededrandom, l, i1);

                    if (l == chunkcoordintpair.x && i1 == chunkcoordintpair.z) {
                        return true;
                    }
                }
            }

            return false;
        }
    }
}
