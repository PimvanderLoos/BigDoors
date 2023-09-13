package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.biome.BiomeSettingsMobs;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.feature.configurations.StructureSettingsFeature;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureVillageConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;

public class WorldGenFeaturePillagerOutpost extends WorldGenFeatureJigsaw {

    public static final WeightedRandomList<BiomeSettingsMobs.c> OUTPOST_ENEMIES = WeightedRandomList.create((WeightedEntry[]) (new BiomeSettingsMobs.c(EntityTypes.PILLAGER, 1, 1, 1)));

    public WorldGenFeaturePillagerOutpost(Codec<WorldGenFeatureVillageConfiguration> codec) {
        super(codec, 0, true, true, WorldGenFeaturePillagerOutpost::checkLocation);
    }

    private static boolean checkLocation(PieceGeneratorSupplier.a<WorldGenFeatureVillageConfiguration> piecegeneratorsupplier_a) {
        int i = piecegeneratorsupplier_a.chunkPos().x >> 4;
        int j = piecegeneratorsupplier_a.chunkPos().z >> 4;
        SeededRandom seededrandom = new SeededRandom(new LegacyRandomSource(0L));

        seededrandom.setSeed((long) (i ^ j << 4) ^ piecegeneratorsupplier_a.seed());
        seededrandom.nextInt();
        return seededrandom.nextInt(5) != 0 ? false : !isNearVillage(piecegeneratorsupplier_a.chunkGenerator(), piecegeneratorsupplier_a.seed(), piecegeneratorsupplier_a.chunkPos());
    }

    private static boolean isNearVillage(ChunkGenerator chunkgenerator, long i, ChunkCoordIntPair chunkcoordintpair) {
        StructureSettingsFeature structuresettingsfeature = chunkgenerator.getSettings().getConfig(StructureGenerator.VILLAGE);

        if (structuresettingsfeature == null) {
            return false;
        } else {
            int j = chunkcoordintpair.x;
            int k = chunkcoordintpair.z;

            for (int l = j - 10; l <= j + 10; ++l) {
                for (int i1 = k - 10; i1 <= k + 10; ++i1) {
                    ChunkCoordIntPair chunkcoordintpair1 = StructureGenerator.VILLAGE.getPotentialFeatureChunk(structuresettingsfeature, i, l, i1);

                    if (l == chunkcoordintpair1.x && i1 == chunkcoordintpair1.z) {
                        return true;
                    }
                }
            }

            return false;
        }
    }
}
