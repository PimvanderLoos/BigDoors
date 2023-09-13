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
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.WorldGenWoodlandMansionPieces;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;

public class WorldGenWoodlandMansion extends StructureGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenWoodlandMansion(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec, WorldGenWoodlandMansion::pieceGeneratorSupplier, WorldGenWoodlandMansion::afterPlace);
    }

    @Override
    protected boolean linearSeparation() {
        return false;
    }

    private static Optional<PieceGenerator<WorldGenFeatureEmptyConfiguration>> pieceGeneratorSupplier(PieceGeneratorSupplier.a<WorldGenFeatureEmptyConfiguration> piecegeneratorsupplier_a) {
        SeededRandom seededrandom = new SeededRandom(new LegacyRandomSource(0L));

        seededrandom.setLargeFeatureSeed(piecegeneratorsupplier_a.seed(), piecegeneratorsupplier_a.chunkPos().x, piecegeneratorsupplier_a.chunkPos().z);
        EnumBlockRotation enumblockrotation = EnumBlockRotation.getRandom(seededrandom);
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

        int i = piecegeneratorsupplier_a.chunkPos().getBlockX(7);
        int j = piecegeneratorsupplier_a.chunkPos().getBlockZ(7);
        int[] aint = piecegeneratorsupplier_a.getCornerHeights(i, b0, j, b1);
        int k = Math.min(Math.min(aint[0], aint[1]), Math.min(aint[2], aint[3]));

        if (k < 60) {
            return Optional.empty();
        } else if (!piecegeneratorsupplier_a.validBiome().test(piecegeneratorsupplier_a.chunkGenerator().getNoiseBiome(QuartPos.fromBlock(i), QuartPos.fromBlock(aint[0]), QuartPos.fromBlock(j)))) {
            return Optional.empty();
        } else {
            BlockPosition blockposition = new BlockPosition(piecegeneratorsupplier_a.chunkPos().getMiddleBlockX(), k + 1, piecegeneratorsupplier_a.chunkPos().getMiddleBlockZ());

            return Optional.of((structurepiecesbuilder, piecegenerator_a) -> {
                List<WorldGenWoodlandMansionPieces.i> list = Lists.newLinkedList();

                WorldGenWoodlandMansionPieces.generateMansion(piecegenerator_a.structureManager(), blockposition, enumblockrotation, list, seededrandom);
                Objects.requireNonNull(structurepiecesbuilder);
                list.forEach(structurepiecesbuilder::addPiece);
            });
        }
    }

    private static void afterPlace(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, PiecesContainer piecescontainer) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        int i = generatoraccessseed.getMinBuildHeight();
        StructureBoundingBox structureboundingbox1 = piecescontainer.calculateBoundingBox();
        int j = structureboundingbox1.minY();

        for (int k = structureboundingbox.minX(); k <= structureboundingbox.maxX(); ++k) {
            for (int l = structureboundingbox.minZ(); l <= structureboundingbox.maxZ(); ++l) {
                blockposition_mutableblockposition.set(k, j, l);
                if (!generatoraccessseed.isEmptyBlock(blockposition_mutableblockposition) && structureboundingbox1.isInside(blockposition_mutableblockposition) && piecescontainer.isInsidePiece(blockposition_mutableblockposition)) {
                    for (int i1 = j - 1; i1 > i; --i1) {
                        blockposition_mutableblockposition.setY(i1);
                        if (!generatoraccessseed.isEmptyBlock(blockposition_mutableblockposition) && !generatoraccessseed.getBlockState(blockposition_mutableblockposition).getMaterial().isLiquid()) {
                            break;
                        }

                        generatoraccessseed.setBlock(blockposition_mutableblockposition, Blocks.COBBLESTONE.defaultBlockState(), 2);
                    }
                }
            }
        }

    }
}
