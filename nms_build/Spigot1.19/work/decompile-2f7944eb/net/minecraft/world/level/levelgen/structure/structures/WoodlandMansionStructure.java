package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class WoodlandMansionStructure extends Structure {

    public static final Codec<WoodlandMansionStructure> CODEC = simpleCodec(WoodlandMansionStructure::new);

    public WoodlandMansionStructure(Structure.c structure_c) {
        super(structure_c);
    }

    @Override
    public Optional<Structure.b> findGenerationPoint(Structure.a structure_a) {
        EnumBlockRotation enumblockrotation = EnumBlockRotation.getRandom(structure_a.random());
        BlockPosition blockposition = this.getLowestYIn5by5BoxOffset7Blocks(structure_a, enumblockrotation);

        return blockposition.getY() < 60 ? Optional.empty() : Optional.of(new Structure.b(blockposition, (structurepiecesbuilder) -> {
            this.generatePieces(structurepiecesbuilder, structure_a, blockposition, enumblockrotation);
        }));
    }

    private void generatePieces(StructurePiecesBuilder structurepiecesbuilder, Structure.a structure_a, BlockPosition blockposition, EnumBlockRotation enumblockrotation) {
        List<WoodlandMansionPieces.i> list = Lists.newLinkedList();

        WoodlandMansionPieces.generateMansion(structure_a.structureTemplateManager(), blockposition, enumblockrotation, list, structure_a.random());
        Objects.requireNonNull(structurepiecesbuilder);
        list.forEach(structurepiecesbuilder::addPiece);
    }

    @Override
    public void afterPlace(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, PiecesContainer piecescontainer) {
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

    @Override
    public StructureType<?> type() {
        return StructureType.WOODLAND_MANSION;
    }
}
