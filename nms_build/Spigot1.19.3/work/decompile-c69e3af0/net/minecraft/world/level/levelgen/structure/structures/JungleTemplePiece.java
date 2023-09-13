package net.minecraft.world.level.levelgen.structure.structures;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.BlockLever;
import net.minecraft.world.level.block.BlockRedstoneWire;
import net.minecraft.world.level.block.BlockRepeater;
import net.minecraft.world.level.block.BlockStairs;
import net.minecraft.world.level.block.BlockTripwire;
import net.minecraft.world.level.block.BlockTripwireHook;
import net.minecraft.world.level.block.BlockVine;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.BlockPiston;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockPropertyAttachPosition;
import net.minecraft.world.level.block.state.properties.BlockPropertyRedstoneSide;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.WorldGenScatteredPiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.WorldGenFeatureStructurePieceType;
import net.minecraft.world.level.storage.loot.LootTables;

public class JungleTemplePiece extends WorldGenScatteredPiece {

    public static final int WIDTH = 12;
    public static final int DEPTH = 15;
    private boolean placedMainChest;
    private boolean placedHiddenChest;
    private boolean placedTrap1;
    private boolean placedTrap2;
    private static final JungleTemplePiece.a STONE_SELECTOR = new JungleTemplePiece.a();

    public JungleTemplePiece(RandomSource randomsource, int i, int j) {
        super(WorldGenFeatureStructurePieceType.JUNGLE_PYRAMID_PIECE, i, 64, j, 12, 10, 15, getRandomHorizontalDirection(randomsource));
    }

    public JungleTemplePiece(NBTTagCompound nbttagcompound) {
        super(WorldGenFeatureStructurePieceType.JUNGLE_PYRAMID_PIECE, nbttagcompound);
        this.placedMainChest = nbttagcompound.getBoolean("placedMainChest");
        this.placedHiddenChest = nbttagcompound.getBoolean("placedHiddenChest");
        this.placedTrap1 = nbttagcompound.getBoolean("placedTrap1");
        this.placedTrap2 = nbttagcompound.getBoolean("placedTrap2");
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(structurepieceserializationcontext, nbttagcompound);
        nbttagcompound.putBoolean("placedMainChest", this.placedMainChest);
        nbttagcompound.putBoolean("placedHiddenChest", this.placedHiddenChest);
        nbttagcompound.putBoolean("placedTrap1", this.placedTrap1);
        nbttagcompound.putBoolean("placedTrap2", this.placedTrap2);
    }

    @Override
    public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
        if (this.updateAverageGroundHeight(generatoraccessseed, structureboundingbox, 0)) {
            this.generateBox(generatoraccessseed, structureboundingbox, 0, -4, 0, this.width - 1, 0, this.depth - 1, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 1, 2, 9, 2, 2, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 1, 12, 9, 2, 12, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 1, 3, 2, 2, 11, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.generateBox(generatoraccessseed, structureboundingbox, 9, 1, 3, 9, 2, 11, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 3, 1, 10, 6, 1, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 3, 13, 10, 6, 13, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 3, 2, 1, 6, 12, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.generateBox(generatoraccessseed, structureboundingbox, 10, 3, 2, 10, 6, 12, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 3, 2, 9, 3, 12, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 6, 2, 9, 6, 12, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.generateBox(generatoraccessseed, structureboundingbox, 3, 7, 3, 8, 7, 11, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.generateBox(generatoraccessseed, structureboundingbox, 4, 8, 4, 7, 8, 10, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.generateAirBox(generatoraccessseed, structureboundingbox, 3, 1, 3, 8, 2, 11);
            this.generateAirBox(generatoraccessseed, structureboundingbox, 4, 3, 6, 7, 3, 9);
            this.generateAirBox(generatoraccessseed, structureboundingbox, 2, 4, 2, 9, 5, 12);
            this.generateAirBox(generatoraccessseed, structureboundingbox, 4, 6, 5, 7, 6, 9);
            this.generateAirBox(generatoraccessseed, structureboundingbox, 5, 7, 6, 6, 7, 8);
            this.generateAirBox(generatoraccessseed, structureboundingbox, 5, 1, 2, 6, 2, 2);
            this.generateAirBox(generatoraccessseed, structureboundingbox, 5, 2, 12, 6, 2, 12);
            this.generateAirBox(generatoraccessseed, structureboundingbox, 5, 5, 1, 6, 5, 1);
            this.generateAirBox(generatoraccessseed, structureboundingbox, 5, 5, 13, 6, 5, 13);
            this.placeBlock(generatoraccessseed, Blocks.AIR.defaultBlockState(), 1, 5, 5, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.AIR.defaultBlockState(), 10, 5, 5, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.AIR.defaultBlockState(), 1, 5, 9, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.AIR.defaultBlockState(), 10, 5, 9, structureboundingbox);

            int i;

            for (i = 0; i <= 14; i += 14) {
                this.generateBox(generatoraccessseed, structureboundingbox, 2, 4, i, 2, 5, i, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
                this.generateBox(generatoraccessseed, structureboundingbox, 4, 4, i, 4, 5, i, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
                this.generateBox(generatoraccessseed, structureboundingbox, 7, 4, i, 7, 5, i, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
                this.generateBox(generatoraccessseed, structureboundingbox, 9, 4, i, 9, 5, i, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            }

            this.generateBox(generatoraccessseed, structureboundingbox, 5, 6, 0, 6, 6, 0, false, randomsource, JungleTemplePiece.STONE_SELECTOR);

            for (i = 0; i <= 11; i += 11) {
                for (int j = 2; j <= 12; j += 2) {
                    this.generateBox(generatoraccessseed, structureboundingbox, i, 4, j, i, 5, j, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
                }

                this.generateBox(generatoraccessseed, structureboundingbox, i, 6, 5, i, 6, 5, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
                this.generateBox(generatoraccessseed, structureboundingbox, i, 6, 9, i, 6, 9, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            }

            this.generateBox(generatoraccessseed, structureboundingbox, 2, 7, 2, 2, 9, 2, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.generateBox(generatoraccessseed, structureboundingbox, 9, 7, 2, 9, 9, 2, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 7, 12, 2, 9, 12, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.generateBox(generatoraccessseed, structureboundingbox, 9, 7, 12, 9, 9, 12, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.generateBox(generatoraccessseed, structureboundingbox, 4, 9, 4, 4, 9, 4, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.generateBox(generatoraccessseed, structureboundingbox, 7, 9, 4, 7, 9, 4, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.generateBox(generatoraccessseed, structureboundingbox, 4, 9, 10, 4, 9, 10, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.generateBox(generatoraccessseed, structureboundingbox, 7, 9, 10, 7, 9, 10, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 9, 7, 6, 9, 7, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            IBlockData iblockdata = (IBlockData) Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(BlockStairs.FACING, EnumDirection.EAST);
            IBlockData iblockdata1 = (IBlockData) Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(BlockStairs.FACING, EnumDirection.WEST);
            IBlockData iblockdata2 = (IBlockData) Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(BlockStairs.FACING, EnumDirection.SOUTH);
            IBlockData iblockdata3 = (IBlockData) Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(BlockStairs.FACING, EnumDirection.NORTH);

            this.placeBlock(generatoraccessseed, iblockdata3, 5, 9, 6, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata3, 6, 9, 6, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata2, 5, 9, 8, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata2, 6, 9, 8, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata3, 4, 0, 0, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata3, 5, 0, 0, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata3, 6, 0, 0, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata3, 7, 0, 0, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata3, 4, 1, 8, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata3, 4, 2, 9, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata3, 4, 3, 10, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata3, 7, 1, 8, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata3, 7, 2, 9, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata3, 7, 3, 10, structureboundingbox);
            this.generateBox(generatoraccessseed, structureboundingbox, 4, 1, 9, 4, 1, 9, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.generateBox(generatoraccessseed, structureboundingbox, 7, 1, 9, 7, 1, 9, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.generateBox(generatoraccessseed, structureboundingbox, 4, 1, 10, 7, 2, 10, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 4, 5, 6, 4, 5, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.placeBlock(generatoraccessseed, iblockdata, 4, 4, 5, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata1, 7, 4, 5, structureboundingbox);

            int k;

            for (k = 0; k < 4; ++k) {
                this.placeBlock(generatoraccessseed, iblockdata2, 5, 0 - k, 6 + k, structureboundingbox);
                this.placeBlock(generatoraccessseed, iblockdata2, 6, 0 - k, 6 + k, structureboundingbox);
                this.generateAirBox(generatoraccessseed, structureboundingbox, 5, 0 - k, 7 + k, 6, 0 - k, 9 + k);
            }

            this.generateAirBox(generatoraccessseed, structureboundingbox, 1, -3, 12, 10, -1, 13);
            this.generateAirBox(generatoraccessseed, structureboundingbox, 1, -3, 1, 3, -1, 13);
            this.generateAirBox(generatoraccessseed, structureboundingbox, 1, -3, 1, 9, -1, 5);

            for (k = 1; k <= 13; k += 2) {
                this.generateBox(generatoraccessseed, structureboundingbox, 1, -3, k, 1, -2, k, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            }

            for (k = 2; k <= 12; k += 2) {
                this.generateBox(generatoraccessseed, structureboundingbox, 1, -1, k, 3, -1, k, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            }

            this.generateBox(generatoraccessseed, structureboundingbox, 2, -2, 1, 5, -2, 1, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.generateBox(generatoraccessseed, structureboundingbox, 7, -2, 1, 9, -2, 1, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.generateBox(generatoraccessseed, structureboundingbox, 6, -3, 1, 6, -3, 1, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.generateBox(generatoraccessseed, structureboundingbox, 6, -1, 1, 6, -1, 1, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.placeBlock(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.TRIPWIRE_HOOK.defaultBlockState().setValue(BlockTripwireHook.FACING, EnumDirection.EAST)).setValue(BlockTripwireHook.ATTACHED, true), 1, -3, 8, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.TRIPWIRE_HOOK.defaultBlockState().setValue(BlockTripwireHook.FACING, EnumDirection.WEST)).setValue(BlockTripwireHook.ATTACHED, true), 4, -3, 8, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) ((IBlockData) ((IBlockData) Blocks.TRIPWIRE.defaultBlockState().setValue(BlockTripwire.EAST, true)).setValue(BlockTripwire.WEST, true)).setValue(BlockTripwire.ATTACHED, true), 2, -3, 8, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) ((IBlockData) ((IBlockData) Blocks.TRIPWIRE.defaultBlockState().setValue(BlockTripwire.EAST, true)).setValue(BlockTripwire.WEST, true)).setValue(BlockTripwire.ATTACHED, true), 3, -3, 8, structureboundingbox);
            IBlockData iblockdata4 = (IBlockData) ((IBlockData) Blocks.REDSTONE_WIRE.defaultBlockState().setValue(BlockRedstoneWire.NORTH, BlockPropertyRedstoneSide.SIDE)).setValue(BlockRedstoneWire.SOUTH, BlockPropertyRedstoneSide.SIDE);

            this.placeBlock(generatoraccessseed, iblockdata4, 5, -3, 7, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata4, 5, -3, 6, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata4, 5, -3, 5, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata4, 5, -3, 4, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata4, 5, -3, 3, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata4, 5, -3, 2, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.REDSTONE_WIRE.defaultBlockState().setValue(BlockRedstoneWire.NORTH, BlockPropertyRedstoneSide.SIDE)).setValue(BlockRedstoneWire.WEST, BlockPropertyRedstoneSide.SIDE), 5, -3, 1, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.REDSTONE_WIRE.defaultBlockState().setValue(BlockRedstoneWire.EAST, BlockPropertyRedstoneSide.SIDE)).setValue(BlockRedstoneWire.WEST, BlockPropertyRedstoneSide.SIDE), 4, -3, 1, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 3, -3, 1, structureboundingbox);
            if (!this.placedTrap1) {
                this.placedTrap1 = this.createDispenser(generatoraccessseed, structureboundingbox, randomsource, 3, -2, 1, EnumDirection.NORTH, LootTables.JUNGLE_TEMPLE_DISPENSER);
            }

            this.placeBlock(generatoraccessseed, (IBlockData) Blocks.VINE.defaultBlockState().setValue(BlockVine.SOUTH, true), 3, -2, 2, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.TRIPWIRE_HOOK.defaultBlockState().setValue(BlockTripwireHook.FACING, EnumDirection.NORTH)).setValue(BlockTripwireHook.ATTACHED, true), 7, -3, 1, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.TRIPWIRE_HOOK.defaultBlockState().setValue(BlockTripwireHook.FACING, EnumDirection.SOUTH)).setValue(BlockTripwireHook.ATTACHED, true), 7, -3, 5, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) ((IBlockData) ((IBlockData) Blocks.TRIPWIRE.defaultBlockState().setValue(BlockTripwire.NORTH, true)).setValue(BlockTripwire.SOUTH, true)).setValue(BlockTripwire.ATTACHED, true), 7, -3, 2, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) ((IBlockData) ((IBlockData) Blocks.TRIPWIRE.defaultBlockState().setValue(BlockTripwire.NORTH, true)).setValue(BlockTripwire.SOUTH, true)).setValue(BlockTripwire.ATTACHED, true), 7, -3, 3, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) ((IBlockData) ((IBlockData) Blocks.TRIPWIRE.defaultBlockState().setValue(BlockTripwire.NORTH, true)).setValue(BlockTripwire.SOUTH, true)).setValue(BlockTripwire.ATTACHED, true), 7, -3, 4, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.REDSTONE_WIRE.defaultBlockState().setValue(BlockRedstoneWire.EAST, BlockPropertyRedstoneSide.SIDE)).setValue(BlockRedstoneWire.WEST, BlockPropertyRedstoneSide.SIDE), 8, -3, 6, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.REDSTONE_WIRE.defaultBlockState().setValue(BlockRedstoneWire.WEST, BlockPropertyRedstoneSide.SIDE)).setValue(BlockRedstoneWire.SOUTH, BlockPropertyRedstoneSide.SIDE), 9, -3, 6, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.REDSTONE_WIRE.defaultBlockState().setValue(BlockRedstoneWire.NORTH, BlockPropertyRedstoneSide.SIDE)).setValue(BlockRedstoneWire.SOUTH, BlockPropertyRedstoneSide.UP), 9, -3, 5, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 9, -3, 4, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata4, 9, -2, 4, structureboundingbox);
            if (!this.placedTrap2) {
                this.placedTrap2 = this.createDispenser(generatoraccessseed, structureboundingbox, randomsource, 9, -2, 3, EnumDirection.WEST, LootTables.JUNGLE_TEMPLE_DISPENSER);
            }

            this.placeBlock(generatoraccessseed, (IBlockData) Blocks.VINE.defaultBlockState().setValue(BlockVine.EAST, true), 8, -1, 3, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) Blocks.VINE.defaultBlockState().setValue(BlockVine.EAST, true), 8, -2, 3, structureboundingbox);
            if (!this.placedMainChest) {
                this.placedMainChest = this.createChest(generatoraccessseed, structureboundingbox, randomsource, 8, -3, 3, LootTables.JUNGLE_TEMPLE);
            }

            this.placeBlock(generatoraccessseed, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 9, -3, 2, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 8, -3, 1, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 4, -3, 5, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 5, -2, 5, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 5, -1, 5, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 6, -3, 5, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 7, -2, 5, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 7, -1, 5, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 8, -3, 5, structureboundingbox);
            this.generateBox(generatoraccessseed, structureboundingbox, 9, -1, 1, 9, -1, 5, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.generateAirBox(generatoraccessseed, structureboundingbox, 8, -3, 8, 10, -1, 10);
            this.placeBlock(generatoraccessseed, Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 8, -2, 11, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 9, -2, 11, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 10, -2, 11, structureboundingbox);
            IBlockData iblockdata5 = (IBlockData) ((IBlockData) Blocks.LEVER.defaultBlockState().setValue(BlockLever.FACING, EnumDirection.NORTH)).setValue(BlockLever.FACE, BlockPropertyAttachPosition.WALL);

            this.placeBlock(generatoraccessseed, iblockdata5, 8, -2, 12, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata5, 9, -2, 12, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata5, 10, -2, 12, structureboundingbox);
            this.generateBox(generatoraccessseed, structureboundingbox, 8, -3, 8, 8, -3, 10, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.generateBox(generatoraccessseed, structureboundingbox, 10, -3, 8, 10, -3, 10, false, randomsource, JungleTemplePiece.STONE_SELECTOR);
            this.placeBlock(generatoraccessseed, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 10, -2, 9, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata4, 8, -2, 9, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata4, 8, -2, 10, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) Blocks.REDSTONE_WIRE.defaultBlockState().setValue(BlockRedstoneWire.NORTH, BlockPropertyRedstoneSide.SIDE)).setValue(BlockRedstoneWire.SOUTH, BlockPropertyRedstoneSide.SIDE)).setValue(BlockRedstoneWire.EAST, BlockPropertyRedstoneSide.SIDE)).setValue(BlockRedstoneWire.WEST, BlockPropertyRedstoneSide.SIDE), 10, -1, 9, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) Blocks.STICKY_PISTON.defaultBlockState().setValue(BlockPiston.FACING, EnumDirection.UP), 9, -2, 8, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) Blocks.STICKY_PISTON.defaultBlockState().setValue(BlockPiston.FACING, EnumDirection.WEST), 10, -2, 8, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) Blocks.STICKY_PISTON.defaultBlockState().setValue(BlockPiston.FACING, EnumDirection.WEST), 10, -1, 8, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) Blocks.REPEATER.defaultBlockState().setValue(BlockRepeater.FACING, EnumDirection.NORTH), 10, -2, 10, structureboundingbox);
            if (!this.placedHiddenChest) {
                this.placedHiddenChest = this.createChest(generatoraccessseed, structureboundingbox, randomsource, 9, -3, 10, LootTables.JUNGLE_TEMPLE);
            }

        }
    }

    private static class a extends StructurePiece.StructurePieceBlockSelector {

        a() {}

        @Override
        public void next(RandomSource randomsource, int i, int j, int k, boolean flag) {
            if (randomsource.nextFloat() < 0.4F) {
                this.next = Blocks.COBBLESTONE.defaultBlockState();
            } else {
                this.next = Blocks.MOSSY_COBBLESTONE.defaultBlockState();
            }

        }
    }
}
