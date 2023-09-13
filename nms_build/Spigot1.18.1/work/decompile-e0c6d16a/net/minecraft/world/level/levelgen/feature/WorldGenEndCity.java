package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.WorldGenEndCityPieces;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;

public class WorldGenEndCity extends StructureGenerator<WorldGenFeatureEmptyConfiguration> {

    private static final int RANDOM_SALT = 10387313;

    public WorldGenEndCity(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec, WorldGenEndCity::pieceGeneratorSupplier);
    }

    @Override
    protected boolean linearSeparation() {
        return false;
    }

    private static int getYPositionForFeature(ChunkCoordIntPair chunkcoordintpair, ChunkGenerator chunkgenerator, LevelHeightAccessor levelheightaccessor) {
        Random random = new Random((long) (chunkcoordintpair.x + chunkcoordintpair.z * 10387313));
        EnumBlockRotation enumblockrotation = EnumBlockRotation.getRandom(random);
        byte b0 = 5;
        byte b1 = 5;

        if (enumblockrotation == EnumBlockRotation.CLOCKWISE_90) {
            b0 = -5;
        } else if (enumblockrotation == EnumBlockRotation.CLOCKWISE_180) {
            b0 = -5;
            b1 = -5;
        } else if (enumblockrotation == EnumBlockRotation.COUNTERCLOCKWISE_90) {
            b1 = -5;
        }

        int i = chunkcoordintpair.getBlockX(7);
        int j = chunkcoordintpair.getBlockZ(7);
        int k = chunkgenerator.getFirstOccupiedHeight(i, j, HeightMap.Type.WORLD_SURFACE_WG, levelheightaccessor);
        int l = chunkgenerator.getFirstOccupiedHeight(i, j + b1, HeightMap.Type.WORLD_SURFACE_WG, levelheightaccessor);
        int i1 = chunkgenerator.getFirstOccupiedHeight(i + b0, j, HeightMap.Type.WORLD_SURFACE_WG, levelheightaccessor);
        int j1 = chunkgenerator.getFirstOccupiedHeight(i + b0, j + b1, HeightMap.Type.WORLD_SURFACE_WG, levelheightaccessor);

        return Math.min(Math.min(k, l), Math.min(i1, j1));
    }

    private static Optional<PieceGenerator<WorldGenFeatureEmptyConfiguration>> pieceGeneratorSupplier(PieceGeneratorSupplier.a<WorldGenFeatureEmptyConfiguration> piecegeneratorsupplier_a) {
        int i = getYPositionForFeature(piecegeneratorsupplier_a.chunkPos(), piecegeneratorsupplier_a.chunkGenerator(), piecegeneratorsupplier_a.heightAccessor());

        if (i < 60) {
            return Optional.empty();
        } else {
            BlockPosition blockposition = piecegeneratorsupplier_a.chunkPos().getMiddleBlockPosition(i);

            return !piecegeneratorsupplier_a.validBiome().test(piecegeneratorsupplier_a.chunkGenerator().getNoiseBiome(QuartPos.fromBlock(blockposition.getX()), QuartPos.fromBlock(blockposition.getY()), QuartPos.fromBlock(blockposition.getZ()))) ? Optional.empty() : Optional.of((structurepiecesbuilder, piecegenerator_a) -> {
                EnumBlockRotation enumblockrotation = EnumBlockRotation.getRandom(piecegenerator_a.random());
                List<StructurePiece> list = Lists.newArrayList();

                WorldGenEndCityPieces.startHouseTower(piecegenerator_a.structureManager(), blockposition, enumblockrotation, list, piecegenerator_a.random());
                Objects.requireNonNull(structurepiecesbuilder);
                list.forEach(structurepiecesbuilder::addPiece);
            });
        }
    }
}
