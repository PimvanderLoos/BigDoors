package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class OceanMonumentStructure extends Structure {

    public static final Codec<OceanMonumentStructure> CODEC = simpleCodec(OceanMonumentStructure::new);

    public OceanMonumentStructure(Structure.c structure_c) {
        super(structure_c);
    }

    @Override
    public Optional<Structure.b> findGenerationPoint(Structure.a structure_a) {
        int i = structure_a.chunkPos().getBlockX(9);
        int j = structure_a.chunkPos().getBlockZ(9);
        Set<Holder<BiomeBase>> set = structure_a.biomeSource().getBiomesWithin(i, structure_a.chunkGenerator().getSeaLevel(), j, 29, structure_a.randomState().sampler());
        Iterator iterator = set.iterator();

        Holder holder;

        do {
            if (!iterator.hasNext()) {
                return onTopOfChunkCenter(structure_a, HeightMap.Type.OCEAN_FLOOR_WG, (structurepiecesbuilder) -> {
                    generatePieces(structurepiecesbuilder, structure_a);
                });
            }

            holder = (Holder) iterator.next();
        } while (holder.is(BiomeTags.REQUIRED_OCEAN_MONUMENT_SURROUNDING));

        return Optional.empty();
    }

    private static StructurePiece createTopPiece(ChunkCoordIntPair chunkcoordintpair, SeededRandom seededrandom) {
        int i = chunkcoordintpair.getMinBlockX() - 29;
        int j = chunkcoordintpair.getMinBlockZ() - 29;
        EnumDirection enumdirection = EnumDirection.EnumDirectionLimit.HORIZONTAL.getRandomDirection(seededrandom);

        return new OceanMonumentPieces.h(seededrandom, i, j, enumdirection);
    }

    private static void generatePieces(StructurePiecesBuilder structurepiecesbuilder, Structure.a structure_a) {
        structurepiecesbuilder.addPiece(createTopPiece(structure_a.chunkPos(), structure_a.random()));
    }

    public static PiecesContainer regeneratePiecesAfterLoad(ChunkCoordIntPair chunkcoordintpair, long i, PiecesContainer piecescontainer) {
        if (piecescontainer.isEmpty()) {
            return piecescontainer;
        } else {
            SeededRandom seededrandom = new SeededRandom(new LegacyRandomSource(RandomSupport.generateUniqueSeed()));

            seededrandom.setLargeFeatureSeed(i, chunkcoordintpair.x, chunkcoordintpair.z);
            StructurePiece structurepiece = (StructurePiece) piecescontainer.pieces().get(0);
            StructureBoundingBox structureboundingbox = structurepiece.getBoundingBox();
            int j = structureboundingbox.minX();
            int k = structureboundingbox.minZ();
            EnumDirection enumdirection = EnumDirection.EnumDirectionLimit.HORIZONTAL.getRandomDirection(seededrandom);
            EnumDirection enumdirection1 = (EnumDirection) Objects.requireNonNullElse(structurepiece.getOrientation(), enumdirection);
            OceanMonumentPieces.h oceanmonumentpieces_h = new OceanMonumentPieces.h(seededrandom, j, k, enumdirection1);
            StructurePiecesBuilder structurepiecesbuilder = new StructurePiecesBuilder();

            structurepiecesbuilder.addPiece(oceanmonumentpieces_h);
            return structurepiecesbuilder.build();
        }
    }

    @Override
    public StructureType<?> type() {
        return StructureType.OCEAN_MONUMENT;
    }
}
