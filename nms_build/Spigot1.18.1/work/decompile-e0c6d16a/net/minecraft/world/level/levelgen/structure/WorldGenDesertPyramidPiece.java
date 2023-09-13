package net.minecraft.world.level.levelgen.structure;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.BlockStairs;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureStructurePieceType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.storage.loot.LootTables;

public class WorldGenDesertPyramidPiece extends WorldGenScatteredPiece {

    public static final int WIDTH = 21;
    public static final int DEPTH = 21;
    private final boolean[] hasPlacedChest = new boolean[4];

    public WorldGenDesertPyramidPiece(Random random, int i, int j) {
        super(WorldGenFeatureStructurePieceType.DESERT_PYRAMID_PIECE, i, 64, j, 21, 15, 21, getRandomHorizontalDirection(random));
    }

    public WorldGenDesertPyramidPiece(NBTTagCompound nbttagcompound) {
        super(WorldGenFeatureStructurePieceType.DESERT_PYRAMID_PIECE, nbttagcompound);
        this.hasPlacedChest[0] = nbttagcompound.getBoolean("hasPlacedChest0");
        this.hasPlacedChest[1] = nbttagcompound.getBoolean("hasPlacedChest1");
        this.hasPlacedChest[2] = nbttagcompound.getBoolean("hasPlacedChest2");
        this.hasPlacedChest[3] = nbttagcompound.getBoolean("hasPlacedChest3");
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(structurepieceserializationcontext, nbttagcompound);
        nbttagcompound.putBoolean("hasPlacedChest0", this.hasPlacedChest[0]);
        nbttagcompound.putBoolean("hasPlacedChest1", this.hasPlacedChest[1]);
        nbttagcompound.putBoolean("hasPlacedChest2", this.hasPlacedChest[2]);
        nbttagcompound.putBoolean("hasPlacedChest3", this.hasPlacedChest[3]);
    }

    @Override
    public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
        if (this.updateHeightPositionToLowestGroundHeight(generatoraccessseed, -random.nextInt(3))) {
            this.generateBox(generatoraccessseed, structureboundingbox, 0, -4, 0, this.width - 1, 0, this.depth - 1, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);

            int i;

            for (i = 1; i <= 9; ++i) {
                this.generateBox(generatoraccessseed, structureboundingbox, i, i, i, this.width - 1 - i, i, this.depth - 1 - i, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
                this.generateBox(generatoraccessseed, structureboundingbox, i + 1, i, i + 1, this.width - 2 - i, i, this.depth - 2 - i, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            }

            for (i = 0; i < this.width; ++i) {
                for (int j = 0; j < this.depth; ++j) {
                    boolean flag = true;

                    this.fillColumnDown(generatoraccessseed, Blocks.SANDSTONE.defaultBlockState(), i, -5, j, structureboundingbox);
                }
            }

            IBlockData iblockdata = (IBlockData) Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(BlockStairs.FACING, EnumDirection.NORTH);
            IBlockData iblockdata1 = (IBlockData) Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(BlockStairs.FACING, EnumDirection.SOUTH);
            IBlockData iblockdata2 = (IBlockData) Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(BlockStairs.FACING, EnumDirection.EAST);
            IBlockData iblockdata3 = (IBlockData) Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(BlockStairs.FACING, EnumDirection.WEST);

            this.generateBox(generatoraccessseed, structureboundingbox, 0, 0, 0, 4, 9, 4, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 10, 1, 3, 10, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
            this.placeBlock(generatoraccessseed, iblockdata, 2, 10, 0, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata1, 2, 10, 4, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata2, 0, 10, 2, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata3, 4, 10, 2, structureboundingbox);
            this.generateBox(generatoraccessseed, structureboundingbox, this.width - 5, 0, 0, this.width - 1, 9, 4, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, this.width - 4, 10, 1, this.width - 2, 10, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
            this.placeBlock(generatoraccessseed, iblockdata, this.width - 3, 10, 0, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata1, this.width - 3, 10, 4, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata2, this.width - 5, 10, 2, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata3, this.width - 1, 10, 2, structureboundingbox);
            this.generateBox(generatoraccessseed, structureboundingbox, 8, 0, 0, 12, 4, 4, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 9, 1, 0, 11, 3, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.placeBlock(generatoraccessseed, Blocks.CUT_SANDSTONE.defaultBlockState(), 9, 1, 1, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.CUT_SANDSTONE.defaultBlockState(), 9, 2, 1, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.CUT_SANDSTONE.defaultBlockState(), 9, 3, 1, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.CUT_SANDSTONE.defaultBlockState(), 10, 3, 1, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.CUT_SANDSTONE.defaultBlockState(), 11, 3, 1, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.CUT_SANDSTONE.defaultBlockState(), 11, 2, 1, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.CUT_SANDSTONE.defaultBlockState(), 11, 1, 1, structureboundingbox);
            this.generateBox(generatoraccessseed, structureboundingbox, 4, 1, 1, 8, 3, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 4, 1, 2, 8, 2, 2, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 12, 1, 1, 16, 3, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 12, 1, 2, 16, 2, 2, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 4, 5, this.width - 6, 4, this.depth - 6, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 9, 4, 9, 11, 4, 11, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 8, 1, 8, 8, 3, 8, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 12, 1, 8, 12, 3, 8, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 8, 1, 12, 8, 3, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 12, 1, 12, 12, 3, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 1, 5, 4, 4, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, this.width - 5, 1, 5, this.width - 2, 4, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 6, 7, 9, 6, 7, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, this.width - 7, 7, 9, this.width - 7, 7, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 5, 9, 5, 7, 11, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, this.width - 6, 5, 9, this.width - 6, 7, 11, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
            this.placeBlock(generatoraccessseed, Blocks.AIR.defaultBlockState(), 5, 5, 10, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.AIR.defaultBlockState(), 5, 6, 10, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.AIR.defaultBlockState(), 6, 6, 10, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.AIR.defaultBlockState(), this.width - 6, 5, 10, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.AIR.defaultBlockState(), this.width - 6, 6, 10, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.AIR.defaultBlockState(), this.width - 7, 6, 10, structureboundingbox);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 4, 4, 2, 6, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, this.width - 3, 4, 4, this.width - 3, 6, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.placeBlock(generatoraccessseed, iblockdata, 2, 4, 5, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata, 2, 3, 4, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata, this.width - 3, 4, 5, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata, this.width - 3, 3, 4, structureboundingbox);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 1, 3, 2, 2, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, this.width - 3, 1, 3, this.width - 2, 2, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
            this.placeBlock(generatoraccessseed, Blocks.SANDSTONE.defaultBlockState(), 1, 1, 2, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.SANDSTONE.defaultBlockState(), this.width - 2, 1, 2, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.SANDSTONE_SLAB.defaultBlockState(), 1, 2, 2, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.SANDSTONE_SLAB.defaultBlockState(), this.width - 2, 2, 2, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata3, 2, 1, 2, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata2, this.width - 3, 1, 2, structureboundingbox);
            this.generateBox(generatoraccessseed, structureboundingbox, 4, 3, 5, 4, 3, 17, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, this.width - 5, 3, 5, this.width - 5, 3, 17, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 3, 1, 5, 4, 2, 16, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, this.width - 6, 1, 5, this.width - 5, 2, 16, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);

            int k;

            for (k = 5; k <= 17; k += 2) {
                this.placeBlock(generatoraccessseed, Blocks.CUT_SANDSTONE.defaultBlockState(), 4, 1, k, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 4, 2, k, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.CUT_SANDSTONE.defaultBlockState(), this.width - 5, 1, k, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.CHISELED_SANDSTONE.defaultBlockState(), this.width - 5, 2, k, structureboundingbox);
            }

            this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 7, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 8, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 9, 0, 9, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 11, 0, 9, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 8, 0, 10, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 12, 0, 10, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 7, 0, 10, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 13, 0, 10, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 9, 0, 11, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 11, 0, 11, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 12, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 13, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.BLUE_TERRACOTTA.defaultBlockState(), 10, 0, 10, structureboundingbox);

            for (k = 0; k <= this.width - 1; k += this.width - 1) {
                this.placeBlock(generatoraccessseed, Blocks.CUT_SANDSTONE.defaultBlockState(), k, 2, 1, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), k, 2, 2, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.CUT_SANDSTONE.defaultBlockState(), k, 2, 3, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.CUT_SANDSTONE.defaultBlockState(), k, 3, 1, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), k, 3, 2, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.CUT_SANDSTONE.defaultBlockState(), k, 3, 3, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), k, 4, 1, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.CHISELED_SANDSTONE.defaultBlockState(), k, 4, 2, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), k, 4, 3, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.CUT_SANDSTONE.defaultBlockState(), k, 5, 1, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), k, 5, 2, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.CUT_SANDSTONE.defaultBlockState(), k, 5, 3, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), k, 6, 1, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.CHISELED_SANDSTONE.defaultBlockState(), k, 6, 2, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), k, 6, 3, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), k, 7, 1, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), k, 7, 2, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), k, 7, 3, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.CUT_SANDSTONE.defaultBlockState(), k, 8, 1, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.CUT_SANDSTONE.defaultBlockState(), k, 8, 2, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.CUT_SANDSTONE.defaultBlockState(), k, 8, 3, structureboundingbox);
            }

            for (k = 2; k <= this.width - 3; k += this.width - 3 - 2) {
                this.placeBlock(generatoraccessseed, Blocks.CUT_SANDSTONE.defaultBlockState(), k - 1, 2, 0, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), k, 2, 0, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.CUT_SANDSTONE.defaultBlockState(), k + 1, 2, 0, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.CUT_SANDSTONE.defaultBlockState(), k - 1, 3, 0, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), k, 3, 0, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.CUT_SANDSTONE.defaultBlockState(), k + 1, 3, 0, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), k - 1, 4, 0, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.CHISELED_SANDSTONE.defaultBlockState(), k, 4, 0, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), k + 1, 4, 0, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.CUT_SANDSTONE.defaultBlockState(), k - 1, 5, 0, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), k, 5, 0, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.CUT_SANDSTONE.defaultBlockState(), k + 1, 5, 0, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), k - 1, 6, 0, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.CHISELED_SANDSTONE.defaultBlockState(), k, 6, 0, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), k + 1, 6, 0, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), k - 1, 7, 0, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), k, 7, 0, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), k + 1, 7, 0, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.CUT_SANDSTONE.defaultBlockState(), k - 1, 8, 0, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.CUT_SANDSTONE.defaultBlockState(), k, 8, 0, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.CUT_SANDSTONE.defaultBlockState(), k + 1, 8, 0, structureboundingbox);
            }

            this.generateBox(generatoraccessseed, structureboundingbox, 8, 4, 0, 12, 6, 0, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
            this.placeBlock(generatoraccessseed, Blocks.AIR.defaultBlockState(), 8, 6, 0, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.AIR.defaultBlockState(), 12, 6, 0, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 9, 5, 0, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 10, 5, 0, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 11, 5, 0, structureboundingbox);
            this.generateBox(generatoraccessseed, structureboundingbox, 8, -14, 8, 12, -11, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 8, -10, 8, 12, -10, 12, Blocks.CHISELED_SANDSTONE.defaultBlockState(), Blocks.CHISELED_SANDSTONE.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 8, -9, 8, 12, -9, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 8, -8, 8, 12, -1, 12, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 9, -11, 9, 11, -1, 11, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.placeBlock(generatoraccessseed, Blocks.STONE_PRESSURE_PLATE.defaultBlockState(), 10, -11, 10, structureboundingbox);
            this.generateBox(generatoraccessseed, structureboundingbox, 9, -13, 9, 11, -13, 11, Blocks.TNT.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.placeBlock(generatoraccessseed, Blocks.AIR.defaultBlockState(), 8, -11, 10, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.AIR.defaultBlockState(), 8, -10, 10, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 7, -10, 10, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.CUT_SANDSTONE.defaultBlockState(), 7, -11, 10, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.AIR.defaultBlockState(), 12, -11, 10, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.AIR.defaultBlockState(), 12, -10, 10, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 13, -10, 10, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.CUT_SANDSTONE.defaultBlockState(), 13, -11, 10, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.AIR.defaultBlockState(), 10, -11, 8, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.AIR.defaultBlockState(), 10, -10, 8, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 10, -10, 7, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.CUT_SANDSTONE.defaultBlockState(), 10, -11, 7, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.AIR.defaultBlockState(), 10, -11, 12, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.AIR.defaultBlockState(), 10, -10, 12, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 10, -10, 13, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.CUT_SANDSTONE.defaultBlockState(), 10, -11, 13, structureboundingbox);
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                EnumDirection enumdirection = (EnumDirection) iterator.next();

                if (!this.hasPlacedChest[enumdirection.get2DDataValue()]) {
                    int l = enumdirection.getStepX() * 2;
                    int i1 = enumdirection.getStepZ() * 2;

                    this.hasPlacedChest[enumdirection.get2DDataValue()] = this.createChest(generatoraccessseed, structureboundingbox, random, 10 + l, -11, 10 + i1, LootTables.DESERT_PYRAMID);
                }
            }

        }
    }
}
