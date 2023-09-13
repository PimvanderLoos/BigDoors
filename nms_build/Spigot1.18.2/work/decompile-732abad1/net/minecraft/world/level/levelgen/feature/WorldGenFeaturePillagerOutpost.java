package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureVillageConfiguration;
import net.minecraft.world.level.levelgen.structure.BuiltinStructureSets;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;

public class WorldGenFeaturePillagerOutpost extends WorldGenFeatureJigsaw {

    public WorldGenFeaturePillagerOutpost(Codec<WorldGenFeatureVillageConfiguration> codec) {
        super(codec, 0, true, true, WorldGenFeaturePillagerOutpost::checkLocation);
    }

    private static boolean checkLocation(PieceGeneratorSupplier.a<WorldGenFeatureVillageConfiguration> piecegeneratorsupplier_a) {
        ChunkCoordIntPair chunkcoordintpair = piecegeneratorsupplier_a.chunkPos();
        int i = chunkcoordintpair.x >> 4;
        int j = chunkcoordintpair.z >> 4;
        SeededRandom seededrandom = new SeededRandom(new LegacyRandomSource(0L));

        seededrandom.setSeed((long) (i ^ j << 4) ^ piecegeneratorsupplier_a.seed());
        seededrandom.nextInt();
        return seededrandom.nextInt(5) != 0 ? false : !piecegeneratorsupplier_a.chunkGenerator().hasFeatureChunkInRange(BuiltinStructureSets.VILLAGES, piecegeneratorsupplier_a.seed(), chunkcoordintpair.x, chunkcoordintpair.z, 10);
    }
}
