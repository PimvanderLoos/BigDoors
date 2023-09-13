package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.biome.BiomeSettingsMobs;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.structure.WorldGenWitchHut;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class WorldGenFeatureSwampHut extends StructureGenerator<WorldGenFeatureEmptyConfiguration> {

    public static final WeightedRandomList<BiomeSettingsMobs.c> SWAMPHUT_ENEMIES = WeightedRandomList.create((WeightedEntry[]) (new BiomeSettingsMobs.c(EntityTypes.WITCH, 1, 1, 1)));
    public static final WeightedRandomList<BiomeSettingsMobs.c> SWAMPHUT_ANIMALS = WeightedRandomList.create((WeightedEntry[]) (new BiomeSettingsMobs.c(EntityTypes.CAT, 1, 1, 1)));

    public WorldGenFeatureSwampHut(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec, PieceGeneratorSupplier.simple(PieceGeneratorSupplier.checkForBiomeOnTop(HeightMap.Type.WORLD_SURFACE_WG), WorldGenFeatureSwampHut::generatePieces));
    }

    private static void generatePieces(StructurePiecesBuilder structurepiecesbuilder, PieceGenerator.a<WorldGenFeatureEmptyConfiguration> piecegenerator_a) {
        structurepiecesbuilder.addPiece(new WorldGenWitchHut(piecegenerator_a.random(), piecegenerator_a.chunkPos().getMinBlockX(), piecegenerator_a.chunkPos().getMinBlockZ()));
    }
}
