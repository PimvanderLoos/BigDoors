package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeSettingsMobs;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.WorldGenMonumentPieces;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class WorldGenMonument extends StructureGenerator<WorldGenFeatureEmptyConfiguration> {

    public static final WeightedRandomList<BiomeSettingsMobs.c> MONUMENT_ENEMIES = WeightedRandomList.create((WeightedEntry[]) (new BiomeSettingsMobs.c(EntityTypes.GUARDIAN, 1, 2, 4)));

    public WorldGenMonument(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec, PieceGeneratorSupplier.simple(WorldGenMonument::checkLocation, WorldGenMonument::generatePieces));
    }

    @Override
    protected boolean linearSeparation() {
        return false;
    }

    private static boolean checkLocation(PieceGeneratorSupplier.a<WorldGenFeatureEmptyConfiguration> piecegeneratorsupplier_a) {
        int i = piecegeneratorsupplier_a.chunkPos().getBlockX(9);
        int j = piecegeneratorsupplier_a.chunkPos().getBlockZ(9);
        Set<BiomeBase> set = piecegeneratorsupplier_a.biomeSource().getBiomesWithin(i, piecegeneratorsupplier_a.chunkGenerator().getSeaLevel(), j, 29, piecegeneratorsupplier_a.chunkGenerator().climateSampler());
        Iterator iterator = set.iterator();

        BiomeBase biomebase;

        do {
            if (!iterator.hasNext()) {
                return piecegeneratorsupplier_a.validBiomeOnTop(HeightMap.Type.OCEAN_FLOOR_WG);
            }

            biomebase = (BiomeBase) iterator.next();
        } while (biomebase.getBiomeCategory() == BiomeBase.Geography.OCEAN || biomebase.getBiomeCategory() == BiomeBase.Geography.RIVER);

        return false;
    }

    private static StructurePiece createTopPiece(ChunkCoordIntPair chunkcoordintpair, SeededRandom seededrandom) {
        int i = chunkcoordintpair.getMinBlockX() - 29;
        int j = chunkcoordintpair.getMinBlockZ() - 29;
        EnumDirection enumdirection = EnumDirection.EnumDirectionLimit.HORIZONTAL.getRandomDirection(seededrandom);

        return new WorldGenMonumentPieces.WorldGenMonumentPiece1(seededrandom, i, j, enumdirection);
    }

    private static void generatePieces(StructurePiecesBuilder structurepiecesbuilder, PieceGenerator.a<WorldGenFeatureEmptyConfiguration> piecegenerator_a) {
        structurepiecesbuilder.addPiece(createTopPiece(piecegenerator_a.chunkPos(), piecegenerator_a.random()));
    }

    public static PiecesContainer regeneratePiecesAfterLoad(ChunkCoordIntPair chunkcoordintpair, long i, PiecesContainer piecescontainer) {
        if (piecescontainer.isEmpty()) {
            return piecescontainer;
        } else {
            SeededRandom seededrandom = new SeededRandom(new LegacyRandomSource(RandomSupport.seedUniquifier()));

            seededrandom.setLargeFeatureSeed(i, chunkcoordintpair.x, chunkcoordintpair.z);
            StructurePiece structurepiece = (StructurePiece) piecescontainer.pieces().get(0);
            StructureBoundingBox structureboundingbox = structurepiece.getBoundingBox();
            int j = structureboundingbox.minX();
            int k = structureboundingbox.minZ();
            EnumDirection enumdirection = EnumDirection.EnumDirectionLimit.HORIZONTAL.getRandomDirection(seededrandom);
            EnumDirection enumdirection1 = (EnumDirection) Objects.requireNonNullElse(structurepiece.getOrientation(), enumdirection);
            WorldGenMonumentPieces.WorldGenMonumentPiece1 worldgenmonumentpieces_worldgenmonumentpiece1 = new WorldGenMonumentPieces.WorldGenMonumentPiece1(seededrandom, j, k, enumdirection1);
            StructurePiecesBuilder structurepiecesbuilder = new StructurePiecesBuilder();

            structurepiecesbuilder.addPiece(worldgenmonumentpieces_worldgenmonumentpiece1);
            return structurepiecesbuilder.build();
        }
    }
}
