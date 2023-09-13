package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.structure.NoiseAffectingStructureFeature;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.WorldGenStrongholdPieces;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class WorldGenStronghold extends NoiseAffectingStructureFeature<WorldGenFeatureEmptyConfiguration> {

    public WorldGenStronghold(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec, PieceGeneratorSupplier.simple(WorldGenStronghold::checkLocation, WorldGenStronghold::generatePieces));
    }

    private static boolean checkLocation(PieceGeneratorSupplier.a<WorldGenFeatureEmptyConfiguration> piecegeneratorsupplier_a) {
        return piecegeneratorsupplier_a.chunkGenerator().hasStronghold(piecegeneratorsupplier_a.chunkPos());
    }

    private static void generatePieces(StructurePiecesBuilder structurepiecesbuilder, PieceGenerator.a<WorldGenFeatureEmptyConfiguration> piecegenerator_a) {
        int i = 0;

        WorldGenStrongholdPieces.WorldGenStrongholdStart worldgenstrongholdpieces_worldgenstrongholdstart;

        do {
            structurepiecesbuilder.clear();
            piecegenerator_a.random().setLargeFeatureSeed(piecegenerator_a.seed() + (long) (i++), piecegenerator_a.chunkPos().x, piecegenerator_a.chunkPos().z);
            WorldGenStrongholdPieces.resetPieces();
            worldgenstrongholdpieces_worldgenstrongholdstart = new WorldGenStrongholdPieces.WorldGenStrongholdStart(piecegenerator_a.random(), piecegenerator_a.chunkPos().getBlockX(2), piecegenerator_a.chunkPos().getBlockZ(2));
            structurepiecesbuilder.addPiece(worldgenstrongholdpieces_worldgenstrongholdstart);
            worldgenstrongholdpieces_worldgenstrongholdstart.addChildren(worldgenstrongholdpieces_worldgenstrongholdstart, structurepiecesbuilder, piecegenerator_a.random());
            List list = worldgenstrongholdpieces_worldgenstrongholdstart.pendingChildren;

            while (!list.isEmpty()) {
                int j = piecegenerator_a.random().nextInt(list.size());
                StructurePiece structurepiece = (StructurePiece) list.remove(j);

                structurepiece.addChildren(worldgenstrongholdpieces_worldgenstrongholdstart, structurepiecesbuilder, piecegenerator_a.random());
            }

            structurepiecesbuilder.moveBelowSeaLevel(piecegenerator_a.chunkGenerator().getSeaLevel(), piecegenerator_a.chunkGenerator().getMinY(), piecegenerator_a.random(), 10);
        } while (structurepiecesbuilder.isEmpty() || worldgenstrongholdpieces_worldgenstrongholdstart.portalRoomPiece == null);

    }
}
