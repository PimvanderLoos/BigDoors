package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfigurationChance;
import net.minecraft.world.level.levelgen.structure.WorldGenBuriedTreasurePieces;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class WorldGenBuriedTreasure extends StructureGenerator<WorldGenFeatureConfigurationChance> {

    private static final int RANDOM_SALT = 10387320;

    public WorldGenBuriedTreasure(Codec<WorldGenFeatureConfigurationChance> codec) {
        super(codec, PieceGeneratorSupplier.simple(WorldGenBuriedTreasure::checkLocation, WorldGenBuriedTreasure::generatePieces));
    }

    private static boolean checkLocation(PieceGeneratorSupplier.a<WorldGenFeatureConfigurationChance> piecegeneratorsupplier_a) {
        SeededRandom seededrandom = new SeededRandom(new LegacyRandomSource(0L));

        seededrandom.setLargeFeatureWithSalt(piecegeneratorsupplier_a.seed(), piecegeneratorsupplier_a.chunkPos().x, piecegeneratorsupplier_a.chunkPos().z, 10387320);
        return seededrandom.nextFloat() < ((WorldGenFeatureConfigurationChance) piecegeneratorsupplier_a.config()).probability && piecegeneratorsupplier_a.validBiomeOnTop(HeightMap.Type.OCEAN_FLOOR_WG);
    }

    private static void generatePieces(StructurePiecesBuilder structurepiecesbuilder, PieceGenerator.a<WorldGenFeatureConfigurationChance> piecegenerator_a) {
        BlockPosition blockposition = new BlockPosition(piecegenerator_a.chunkPos().getBlockX(9), 90, piecegenerator_a.chunkPos().getBlockZ(9));

        structurepiecesbuilder.addPiece(new WorldGenBuriedTreasurePieces.a(blockposition));
    }

    @Override
    public BlockPosition getLocatePos(ChunkCoordIntPair chunkcoordintpair) {
        return new BlockPosition(chunkcoordintpair.getBlockX(9), 0, chunkcoordintpair.getBlockZ(9));
    }
}
