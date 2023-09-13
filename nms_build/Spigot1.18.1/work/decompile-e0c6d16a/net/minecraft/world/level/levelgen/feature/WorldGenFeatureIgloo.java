package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.structure.WorldGenIglooPiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class WorldGenFeatureIgloo extends StructureGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenFeatureIgloo(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec, PieceGeneratorSupplier.simple(PieceGeneratorSupplier.checkForBiomeOnTop(HeightMap.Type.WORLD_SURFACE_WG), WorldGenFeatureIgloo::generatePieces));
    }

    private static void generatePieces(StructurePiecesBuilder structurepiecesbuilder, PieceGenerator.a<WorldGenFeatureEmptyConfiguration> piecegenerator_a) {
        BlockPosition blockposition = new BlockPosition(piecegenerator_a.chunkPos().getMinBlockX(), 90, piecegenerator_a.chunkPos().getMinBlockZ());
        EnumBlockRotation enumblockrotation = EnumBlockRotation.getRandom(piecegenerator_a.random());

        WorldGenIglooPiece.addPieces(piecegenerator_a.structureManager(), blockposition, enumblockrotation, structurepiecesbuilder, piecegenerator_a.random());
    }
}
