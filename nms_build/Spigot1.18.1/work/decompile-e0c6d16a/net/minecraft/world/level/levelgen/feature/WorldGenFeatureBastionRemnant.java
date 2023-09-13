package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureVillageConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;

public class WorldGenFeatureBastionRemnant extends WorldGenFeatureJigsaw {

    private static final int BASTION_SPAWN_HEIGHT = 33;

    public WorldGenFeatureBastionRemnant(Codec<WorldGenFeatureVillageConfiguration> codec) {
        super(codec, 33, false, false, WorldGenFeatureBastionRemnant::checkLocation);
    }

    private static boolean checkLocation(PieceGeneratorSupplier.a<WorldGenFeatureVillageConfiguration> piecegeneratorsupplier_a) {
        SeededRandom seededrandom = new SeededRandom(new LegacyRandomSource(0L));

        seededrandom.setLargeFeatureSeed(piecegeneratorsupplier_a.seed(), piecegeneratorsupplier_a.chunkPos().x, piecegeneratorsupplier_a.chunkPos().z);
        return seededrandom.nextInt(5) >= 2;
    }
}
