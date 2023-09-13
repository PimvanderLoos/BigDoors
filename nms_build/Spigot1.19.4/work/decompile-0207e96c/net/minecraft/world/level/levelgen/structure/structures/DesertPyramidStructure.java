package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.SystemUtils;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.data.loot.packs.UpdateOneTwentyBuiltInLootTables;
import net.minecraft.util.ArraySetSorted;
import net.minecraft.util.RandomSource;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.SinglePieceStructure;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;

public class DesertPyramidStructure extends SinglePieceStructure {

    public static final Codec<DesertPyramidStructure> CODEC = simpleCodec(DesertPyramidStructure::new);

    public DesertPyramidStructure(Structure.c structure_c) {
        super(DesertPyramidPiece::new, 21, 21, structure_c);
    }

    @Override
    public void afterPlace(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, PiecesContainer piecescontainer) {
        if (generatoraccessseed.enabledFeatures().contains(FeatureFlags.UPDATE_1_20)) {
            Set<BlockPosition> set = ArraySetSorted.create(BaseBlockPosition::compareTo);
            Iterator iterator = piecescontainer.pieces().iterator();

            while (iterator.hasNext()) {
                StructurePiece structurepiece = (StructurePiece) iterator.next();

                if (structurepiece instanceof DesertPyramidPiece) {
                    DesertPyramidPiece desertpyramidpiece = (DesertPyramidPiece) structurepiece;

                    set.addAll(desertpyramidpiece.getPotentialSuspiciousSandWorldPositions());
                }
            }

            ObjectArrayList<BlockPosition> objectarraylist = new ObjectArrayList(set.stream().toList());

            SystemUtils.shuffle(objectarraylist, randomsource);
            int i = Math.min(set.size(), randomsource.nextInt(5, 8));
            ObjectListIterator objectlistiterator = objectarraylist.iterator();

            while (objectlistiterator.hasNext()) {
                BlockPosition blockposition = (BlockPosition) objectlistiterator.next();

                if (i > 0) {
                    --i;
                    generatoraccessseed.setBlock(blockposition, Blocks.SUSPICIOUS_SAND.defaultBlockState(), 2);
                    generatoraccessseed.getBlockEntity(blockposition, TileEntityTypes.SUSPICIOUS_SAND).ifPresent((suspicioussandblockentity) -> {
                        suspicioussandblockentity.setLootTable(UpdateOneTwentyBuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY, blockposition.asLong());
                    });
                } else {
                    generatoraccessseed.setBlock(blockposition, Blocks.SAND.defaultBlockState(), 2);
                }
            }

        }
    }

    @Override
    public StructureType<?> type() {
        return StructureType.DESERT_PYRAMID;
    }
}
