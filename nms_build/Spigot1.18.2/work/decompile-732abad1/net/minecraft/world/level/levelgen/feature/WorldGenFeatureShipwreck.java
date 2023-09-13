package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureShipwreckConfiguration;
import net.minecraft.world.level.levelgen.structure.WorldGenShipwreck;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class WorldGenFeatureShipwreck extends StructureGenerator<WorldGenFeatureShipwreckConfiguration> {

    public WorldGenFeatureShipwreck(Codec<WorldGenFeatureShipwreckConfiguration> codec) {
        super(codec, PieceGeneratorSupplier.simple(WorldGenFeatureShipwreck::checkLocation, WorldGenFeatureShipwreck::generatePieces));
    }

    private static boolean checkLocation(PieceGeneratorSupplier.a<WorldGenFeatureShipwreckConfiguration> piecegeneratorsupplier_a) {
        HeightMap.Type heightmap_type = ((WorldGenFeatureShipwreckConfiguration) piecegeneratorsupplier_a.config()).isBeached ? HeightMap.Type.WORLD_SURFACE_WG : HeightMap.Type.OCEAN_FLOOR_WG;

        return piecegeneratorsupplier_a.validBiomeOnTop(heightmap_type);
    }

    private static void generatePieces(StructurePiecesBuilder structurepiecesbuilder, PieceGenerator.a<WorldGenFeatureShipwreckConfiguration> piecegenerator_a) {
        EnumBlockRotation enumblockrotation = EnumBlockRotation.getRandom(piecegenerator_a.random());
        BlockPosition blockposition = new BlockPosition(piecegenerator_a.chunkPos().getMinBlockX(), 90, piecegenerator_a.chunkPos().getMinBlockZ());

        WorldGenShipwreck.addPieces(piecegenerator_a.structureManager(), blockposition, enumblockrotation, structurepiecesbuilder, piecegenerator_a.random(), (WorldGenFeatureShipwreckConfiguration) piecegenerator_a.config());
    }
}
