package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.structure.WorldGenDesertPyramidPiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class WorldGenFeatureDesertPyramid extends StructureGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenFeatureDesertPyramid(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec, PieceGeneratorSupplier.simple(WorldGenFeatureDesertPyramid::checkLocation, WorldGenFeatureDesertPyramid::generatePieces));
    }

    private static <C extends WorldGenFeatureConfiguration> boolean checkLocation(PieceGeneratorSupplier.a<C> piecegeneratorsupplier_a) {
        return !piecegeneratorsupplier_a.validBiomeOnTop(HeightMap.Type.WORLD_SURFACE_WG) ? false : piecegeneratorsupplier_a.getLowestY(21, 21) >= piecegeneratorsupplier_a.chunkGenerator().getSeaLevel();
    }

    private static void generatePieces(StructurePiecesBuilder structurepiecesbuilder, PieceGenerator.a<WorldGenFeatureEmptyConfiguration> piecegenerator_a) {
        structurepiecesbuilder.addPiece(new WorldGenDesertPyramidPiece(piecegenerator_a.random(), piecegenerator_a.chunkPos().getMinBlockX(), piecegenerator_a.chunkPos().getMinBlockZ()));
    }
}
