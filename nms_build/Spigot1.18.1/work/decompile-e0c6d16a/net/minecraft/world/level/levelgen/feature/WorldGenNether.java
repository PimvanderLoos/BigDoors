package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.core.QuartPos;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.biome.BiomeSettingsMobs;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.WorldGenNetherPieces;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class WorldGenNether extends StructureGenerator<WorldGenFeatureEmptyConfiguration> {

    public static final WeightedRandomList<BiomeSettingsMobs.c> FORTRESS_ENEMIES = WeightedRandomList.create((WeightedEntry[])(new BiomeSettingsMobs.c(EntityTypes.BLAZE, 10, 2, 3), new BiomeSettingsMobs.c(EntityTypes.ZOMBIFIED_PIGLIN, 5, 4, 4), new BiomeSettingsMobs.c(EntityTypes.WITHER_SKELETON, 8, 5, 5), new BiomeSettingsMobs.c(EntityTypes.SKELETON, 2, 5, 5), new BiomeSettingsMobs.c(EntityTypes.MAGMA_CUBE, 3, 4, 4)));

    public WorldGenNether(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec, PieceGeneratorSupplier.simple(WorldGenNether::checkLocation, WorldGenNether::generatePieces));
    }

    private static boolean checkLocation(PieceGeneratorSupplier.a<WorldGenFeatureEmptyConfiguration> piecegeneratorsupplier_a) {
        SeededRandom seededrandom = new SeededRandom(new LegacyRandomSource(0L));

        seededrandom.setLargeFeatureSeed(piecegeneratorsupplier_a.seed(), piecegeneratorsupplier_a.chunkPos().x, piecegeneratorsupplier_a.chunkPos().z);
        return seededrandom.nextInt(5) >= 2 ? false : piecegeneratorsupplier_a.validBiome().test(piecegeneratorsupplier_a.chunkGenerator().getNoiseBiome(QuartPos.fromBlock(piecegeneratorsupplier_a.chunkPos().getMiddleBlockX()), QuartPos.fromBlock(64), QuartPos.fromBlock(piecegeneratorsupplier_a.chunkPos().getMiddleBlockZ())));
    }

    private static void generatePieces(StructurePiecesBuilder structurepiecesbuilder, PieceGenerator.a<WorldGenFeatureEmptyConfiguration> piecegenerator_a) {
        WorldGenNetherPieces.WorldGenNetherPiece15 worldgennetherpieces_worldgennetherpiece15 = new WorldGenNetherPieces.WorldGenNetherPiece15(piecegenerator_a.random(), piecegenerator_a.chunkPos().getBlockX(2), piecegenerator_a.chunkPos().getBlockZ(2));

        structurepiecesbuilder.addPiece(worldgennetherpieces_worldgennetherpiece15);
        worldgennetherpieces_worldgennetherpiece15.addChildren(worldgennetherpieces_worldgennetherpiece15, structurepiecesbuilder, piecegenerator_a.random());
        List list = worldgennetherpieces_worldgennetherpiece15.pendingChildren;

        while (!list.isEmpty()) {
            int i = piecegenerator_a.random().nextInt(list.size());
            StructurePiece structurepiece = (StructurePiece) list.remove(i);

            structurepiece.addChildren(worldgennetherpieces_worldgennetherpiece15, structurepiecesbuilder, piecegenerator_a.random());
        }

        structurepiecesbuilder.moveInsideHeights(piecegenerator_a.random(), 48, 70);
    }
}
