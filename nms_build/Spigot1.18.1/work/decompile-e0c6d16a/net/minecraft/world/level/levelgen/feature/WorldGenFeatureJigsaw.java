package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.BlockPosition;
import net.minecraft.data.worldgen.WorldGenFeaturePieces;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureVillageConfiguration;
import net.minecraft.world.level.levelgen.feature.structures.WorldGenFeatureDefinedStructureJigsawPlacement;
import net.minecraft.world.level.levelgen.structure.NoiseAffectingStructureFeature;
import net.minecraft.world.level.levelgen.structure.WorldGenFeaturePillagerOutpostPoolPiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;

public class WorldGenFeatureJigsaw extends NoiseAffectingStructureFeature<WorldGenFeatureVillageConfiguration> {

    public WorldGenFeatureJigsaw(Codec<WorldGenFeatureVillageConfiguration> codec, int i, boolean flag, boolean flag1, Predicate<PieceGeneratorSupplier.a<WorldGenFeatureVillageConfiguration>> predicate) {
        super(codec, (piecegeneratorsupplier_a) -> {
            if (!predicate.test(piecegeneratorsupplier_a)) {
                return Optional.empty();
            } else {
                BlockPosition blockposition = new BlockPosition(piecegeneratorsupplier_a.chunkPos().getMinBlockX(), i, piecegeneratorsupplier_a.chunkPos().getMinBlockZ());

                WorldGenFeaturePieces.bootstrap();
                return WorldGenFeatureDefinedStructureJigsawPlacement.addPieces(piecegeneratorsupplier_a, WorldGenFeaturePillagerOutpostPoolPiece::new, blockposition, flag, flag1);
            }
        });
    }
}
