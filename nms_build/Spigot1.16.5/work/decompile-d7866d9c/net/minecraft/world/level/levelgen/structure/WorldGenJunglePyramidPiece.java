package net.minecraft.world.level.levelgen.structure;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
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
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureStructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import net.minecraft.world.level.storage.loot.LootTables;

public class WorldGenJunglePyramidPiece extends WorldGenScatteredPiece {

    private boolean e;
    private boolean f;
    private boolean g;
    private boolean h;
    private static final WorldGenJunglePyramidPiece.a i = new WorldGenJunglePyramidPiece.a();

    public WorldGenJunglePyramidPiece(Random random, int i, int j) {
        super(WorldGenFeatureStructurePieceType.G, random, i, 64, j, 12, 10, 15);
    }

    public WorldGenJunglePyramidPiece(DefinedStructureManager definedstructuremanager, NBTTagCompound nbttagcompound) {
        super(WorldGenFeatureStructurePieceType.G, nbttagcompound);
        this.e = nbttagcompound.getBoolean("placedMainChest");
        this.f = nbttagcompound.getBoolean("placedHiddenChest");
        this.g = nbttagcompound.getBoolean("placedTrap1");
        this.h = nbttagcompound.getBoolean("placedTrap2");
    }

    @Override
    protected void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        nbttagcompound.setBoolean("placedMainChest", this.e);
        nbttagcompound.setBoolean("placedHiddenChest", this.f);
        nbttagcompound.setBoolean("placedTrap1", this.g);
        nbttagcompound.setBoolean("placedTrap2", this.h);
    }

    @Override
    public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
        if (!this.a(generatoraccessseed, structureboundingbox, 0)) {
            return false;
        } else {
            this.a(generatoraccessseed, structureboundingbox, 0, -4, 0, this.a - 1, 0, this.c - 1, false, random, WorldGenJunglePyramidPiece.i);
            this.a(generatoraccessseed, structureboundingbox, 2, 1, 2, 9, 2, 2, false, random, WorldGenJunglePyramidPiece.i);
            this.a(generatoraccessseed, structureboundingbox, 2, 1, 12, 9, 2, 12, false, random, WorldGenJunglePyramidPiece.i);
            this.a(generatoraccessseed, structureboundingbox, 2, 1, 3, 2, 2, 11, false, random, WorldGenJunglePyramidPiece.i);
            this.a(generatoraccessseed, structureboundingbox, 9, 1, 3, 9, 2, 11, false, random, WorldGenJunglePyramidPiece.i);
            this.a(generatoraccessseed, structureboundingbox, 1, 3, 1, 10, 6, 1, false, random, WorldGenJunglePyramidPiece.i);
            this.a(generatoraccessseed, structureboundingbox, 1, 3, 13, 10, 6, 13, false, random, WorldGenJunglePyramidPiece.i);
            this.a(generatoraccessseed, structureboundingbox, 1, 3, 2, 1, 6, 12, false, random, WorldGenJunglePyramidPiece.i);
            this.a(generatoraccessseed, structureboundingbox, 10, 3, 2, 10, 6, 12, false, random, WorldGenJunglePyramidPiece.i);
            this.a(generatoraccessseed, structureboundingbox, 2, 3, 2, 9, 3, 12, false, random, WorldGenJunglePyramidPiece.i);
            this.a(generatoraccessseed, structureboundingbox, 2, 6, 2, 9, 6, 12, false, random, WorldGenJunglePyramidPiece.i);
            this.a(generatoraccessseed, structureboundingbox, 3, 7, 3, 8, 7, 11, false, random, WorldGenJunglePyramidPiece.i);
            this.a(generatoraccessseed, structureboundingbox, 4, 8, 4, 7, 8, 10, false, random, WorldGenJunglePyramidPiece.i);
            this.b(generatoraccessseed, structureboundingbox, 3, 1, 3, 8, 2, 11);
            this.b(generatoraccessseed, structureboundingbox, 4, 3, 6, 7, 3, 9);
            this.b(generatoraccessseed, structureboundingbox, 2, 4, 2, 9, 5, 12);
            this.b(generatoraccessseed, structureboundingbox, 4, 6, 5, 7, 6, 9);
            this.b(generatoraccessseed, structureboundingbox, 5, 7, 6, 6, 7, 8);
            this.b(generatoraccessseed, structureboundingbox, 5, 1, 2, 6, 2, 2);
            this.b(generatoraccessseed, structureboundingbox, 5, 2, 12, 6, 2, 12);
            this.b(generatoraccessseed, structureboundingbox, 5, 5, 1, 6, 5, 1);
            this.b(generatoraccessseed, structureboundingbox, 5, 5, 13, 6, 5, 13);
            this.a(generatoraccessseed, Blocks.AIR.getBlockData(), 1, 5, 5, structureboundingbox);
            this.a(generatoraccessseed, Blocks.AIR.getBlockData(), 10, 5, 5, structureboundingbox);
            this.a(generatoraccessseed, Blocks.AIR.getBlockData(), 1, 5, 9, structureboundingbox);
            this.a(generatoraccessseed, Blocks.AIR.getBlockData(), 10, 5, 9, structureboundingbox);

            int i;

            for (i = 0; i <= 14; i += 14) {
                this.a(generatoraccessseed, structureboundingbox, 2, 4, i, 2, 5, i, false, random, WorldGenJunglePyramidPiece.i);
                this.a(generatoraccessseed, structureboundingbox, 4, 4, i, 4, 5, i, false, random, WorldGenJunglePyramidPiece.i);
                this.a(generatoraccessseed, structureboundingbox, 7, 4, i, 7, 5, i, false, random, WorldGenJunglePyramidPiece.i);
                this.a(generatoraccessseed, structureboundingbox, 9, 4, i, 9, 5, i, false, random, WorldGenJunglePyramidPiece.i);
            }

            this.a(generatoraccessseed, structureboundingbox, 5, 6, 0, 6, 6, 0, false, random, WorldGenJunglePyramidPiece.i);

            for (i = 0; i <= 11; i += 11) {
                for (int j = 2; j <= 12; j += 2) {
                    this.a(generatoraccessseed, structureboundingbox, i, 4, j, i, 5, j, false, random, WorldGenJunglePyramidPiece.i);
                }

                this.a(generatoraccessseed, structureboundingbox, i, 6, 5, i, 6, 5, false, random, WorldGenJunglePyramidPiece.i);
                this.a(generatoraccessseed, structureboundingbox, i, 6, 9, i, 6, 9, false, random, WorldGenJunglePyramidPiece.i);
            }

            this.a(generatoraccessseed, structureboundingbox, 2, 7, 2, 2, 9, 2, false, random, WorldGenJunglePyramidPiece.i);
            this.a(generatoraccessseed, structureboundingbox, 9, 7, 2, 9, 9, 2, false, random, WorldGenJunglePyramidPiece.i);
            this.a(generatoraccessseed, structureboundingbox, 2, 7, 12, 2, 9, 12, false, random, WorldGenJunglePyramidPiece.i);
            this.a(generatoraccessseed, structureboundingbox, 9, 7, 12, 9, 9, 12, false, random, WorldGenJunglePyramidPiece.i);
            this.a(generatoraccessseed, structureboundingbox, 4, 9, 4, 4, 9, 4, false, random, WorldGenJunglePyramidPiece.i);
            this.a(generatoraccessseed, structureboundingbox, 7, 9, 4, 7, 9, 4, false, random, WorldGenJunglePyramidPiece.i);
            this.a(generatoraccessseed, structureboundingbox, 4, 9, 10, 4, 9, 10, false, random, WorldGenJunglePyramidPiece.i);
            this.a(generatoraccessseed, structureboundingbox, 7, 9, 10, 7, 9, 10, false, random, WorldGenJunglePyramidPiece.i);
            this.a(generatoraccessseed, structureboundingbox, 5, 9, 7, 6, 9, 7, false, random, WorldGenJunglePyramidPiece.i);
            IBlockData iblockdata = (IBlockData) Blocks.COBBLESTONE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.EAST);
            IBlockData iblockdata1 = (IBlockData) Blocks.COBBLESTONE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.WEST);
            IBlockData iblockdata2 = (IBlockData) Blocks.COBBLESTONE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.SOUTH);
            IBlockData iblockdata3 = (IBlockData) Blocks.COBBLESTONE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.NORTH);

            this.a(generatoraccessseed, iblockdata3, 5, 9, 6, structureboundingbox);
            this.a(generatoraccessseed, iblockdata3, 6, 9, 6, structureboundingbox);
            this.a(generatoraccessseed, iblockdata2, 5, 9, 8, structureboundingbox);
            this.a(generatoraccessseed, iblockdata2, 6, 9, 8, structureboundingbox);
            this.a(generatoraccessseed, iblockdata3, 4, 0, 0, structureboundingbox);
            this.a(generatoraccessseed, iblockdata3, 5, 0, 0, structureboundingbox);
            this.a(generatoraccessseed, iblockdata3, 6, 0, 0, structureboundingbox);
            this.a(generatoraccessseed, iblockdata3, 7, 0, 0, structureboundingbox);
            this.a(generatoraccessseed, iblockdata3, 4, 1, 8, structureboundingbox);
            this.a(generatoraccessseed, iblockdata3, 4, 2, 9, structureboundingbox);
            this.a(generatoraccessseed, iblockdata3, 4, 3, 10, structureboundingbox);
            this.a(generatoraccessseed, iblockdata3, 7, 1, 8, structureboundingbox);
            this.a(generatoraccessseed, iblockdata3, 7, 2, 9, structureboundingbox);
            this.a(generatoraccessseed, iblockdata3, 7, 3, 10, structureboundingbox);
            this.a(generatoraccessseed, structureboundingbox, 4, 1, 9, 4, 1, 9, false, random, WorldGenJunglePyramidPiece.i);
            this.a(generatoraccessseed, structureboundingbox, 7, 1, 9, 7, 1, 9, false, random, WorldGenJunglePyramidPiece.i);
            this.a(generatoraccessseed, structureboundingbox, 4, 1, 10, 7, 2, 10, false, random, WorldGenJunglePyramidPiece.i);
            this.a(generatoraccessseed, structureboundingbox, 5, 4, 5, 6, 4, 5, false, random, WorldGenJunglePyramidPiece.i);
            this.a(generatoraccessseed, iblockdata, 4, 4, 5, structureboundingbox);
            this.a(generatoraccessseed, iblockdata1, 7, 4, 5, structureboundingbox);

            int k;

            for (k = 0; k < 4; ++k) {
                this.a(generatoraccessseed, iblockdata2, 5, 0 - k, 6 + k, structureboundingbox);
                this.a(generatoraccessseed, iblockdata2, 6, 0 - k, 6 + k, structureboundingbox);
                this.b(generatoraccessseed, structureboundingbox, 5, 0 - k, 7 + k, 6, 0 - k, 9 + k);
            }

            this.b(generatoraccessseed, structureboundingbox, 1, -3, 12, 10, -1, 13);
            this.b(generatoraccessseed, structureboundingbox, 1, -3, 1, 3, -1, 13);
            this.b(generatoraccessseed, structureboundingbox, 1, -3, 1, 9, -1, 5);

            for (k = 1; k <= 13; k += 2) {
                this.a(generatoraccessseed, structureboundingbox, 1, -3, k, 1, -2, k, false, random, WorldGenJunglePyramidPiece.i);
            }

            for (k = 2; k <= 12; k += 2) {
                this.a(generatoraccessseed, structureboundingbox, 1, -1, k, 3, -1, k, false, random, WorldGenJunglePyramidPiece.i);
            }

            this.a(generatoraccessseed, structureboundingbox, 2, -2, 1, 5, -2, 1, false, random, WorldGenJunglePyramidPiece.i);
            this.a(generatoraccessseed, structureboundingbox, 7, -2, 1, 9, -2, 1, false, random, WorldGenJunglePyramidPiece.i);
            this.a(generatoraccessseed, structureboundingbox, 6, -3, 1, 6, -3, 1, false, random, WorldGenJunglePyramidPiece.i);
            this.a(generatoraccessseed, structureboundingbox, 6, -1, 1, 6, -1, 1, false, random, WorldGenJunglePyramidPiece.i);
            this.a(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.TRIPWIRE_HOOK.getBlockData().set(BlockTripwireHook.FACING, EnumDirection.EAST)).set(BlockTripwireHook.ATTACHED, true), 1, -3, 8, structureboundingbox);
            this.a(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.TRIPWIRE_HOOK.getBlockData().set(BlockTripwireHook.FACING, EnumDirection.WEST)).set(BlockTripwireHook.ATTACHED, true), 4, -3, 8, structureboundingbox);
            this.a(generatoraccessseed, (IBlockData) ((IBlockData) ((IBlockData) Blocks.TRIPWIRE.getBlockData().set(BlockTripwire.EAST, true)).set(BlockTripwire.WEST, true)).set(BlockTripwire.ATTACHED, true), 2, -3, 8, structureboundingbox);
            this.a(generatoraccessseed, (IBlockData) ((IBlockData) ((IBlockData) Blocks.TRIPWIRE.getBlockData().set(BlockTripwire.EAST, true)).set(BlockTripwire.WEST, true)).set(BlockTripwire.ATTACHED, true), 3, -3, 8, structureboundingbox);
            IBlockData iblockdata4 = (IBlockData) ((IBlockData) Blocks.REDSTONE_WIRE.getBlockData().set(BlockRedstoneWire.NORTH, BlockPropertyRedstoneSide.SIDE)).set(BlockRedstoneWire.SOUTH, BlockPropertyRedstoneSide.SIDE);

            this.a(generatoraccessseed, iblockdata4, 5, -3, 7, structureboundingbox);
            this.a(generatoraccessseed, iblockdata4, 5, -3, 6, structureboundingbox);
            this.a(generatoraccessseed, iblockdata4, 5, -3, 5, structureboundingbox);
            this.a(generatoraccessseed, iblockdata4, 5, -3, 4, structureboundingbox);
            this.a(generatoraccessseed, iblockdata4, 5, -3, 3, structureboundingbox);
            this.a(generatoraccessseed, iblockdata4, 5, -3, 2, structureboundingbox);
            this.a(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.REDSTONE_WIRE.getBlockData().set(BlockRedstoneWire.NORTH, BlockPropertyRedstoneSide.SIDE)).set(BlockRedstoneWire.WEST, BlockPropertyRedstoneSide.SIDE), 5, -3, 1, structureboundingbox);
            this.a(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.REDSTONE_WIRE.getBlockData().set(BlockRedstoneWire.EAST, BlockPropertyRedstoneSide.SIDE)).set(BlockRedstoneWire.WEST, BlockPropertyRedstoneSide.SIDE), 4, -3, 1, structureboundingbox);
            this.a(generatoraccessseed, Blocks.MOSSY_COBBLESTONE.getBlockData(), 3, -3, 1, structureboundingbox);
            if (!this.g) {
                this.g = this.a(generatoraccessseed, structureboundingbox, random, 3, -2, 1, EnumDirection.NORTH, LootTables.B);
            }

            this.a(generatoraccessseed, (IBlockData) Blocks.VINE.getBlockData().set(BlockVine.SOUTH, true), 3, -2, 2, structureboundingbox);
            this.a(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.TRIPWIRE_HOOK.getBlockData().set(BlockTripwireHook.FACING, EnumDirection.NORTH)).set(BlockTripwireHook.ATTACHED, true), 7, -3, 1, structureboundingbox);
            this.a(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.TRIPWIRE_HOOK.getBlockData().set(BlockTripwireHook.FACING, EnumDirection.SOUTH)).set(BlockTripwireHook.ATTACHED, true), 7, -3, 5, structureboundingbox);
            this.a(generatoraccessseed, (IBlockData) ((IBlockData) ((IBlockData) Blocks.TRIPWIRE.getBlockData().set(BlockTripwire.NORTH, true)).set(BlockTripwire.SOUTH, true)).set(BlockTripwire.ATTACHED, true), 7, -3, 2, structureboundingbox);
            this.a(generatoraccessseed, (IBlockData) ((IBlockData) ((IBlockData) Blocks.TRIPWIRE.getBlockData().set(BlockTripwire.NORTH, true)).set(BlockTripwire.SOUTH, true)).set(BlockTripwire.ATTACHED, true), 7, -3, 3, structureboundingbox);
            this.a(generatoraccessseed, (IBlockData) ((IBlockData) ((IBlockData) Blocks.TRIPWIRE.getBlockData().set(BlockTripwire.NORTH, true)).set(BlockTripwire.SOUTH, true)).set(BlockTripwire.ATTACHED, true), 7, -3, 4, structureboundingbox);
            this.a(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.REDSTONE_WIRE.getBlockData().set(BlockRedstoneWire.EAST, BlockPropertyRedstoneSide.SIDE)).set(BlockRedstoneWire.WEST, BlockPropertyRedstoneSide.SIDE), 8, -3, 6, structureboundingbox);
            this.a(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.REDSTONE_WIRE.getBlockData().set(BlockRedstoneWire.WEST, BlockPropertyRedstoneSide.SIDE)).set(BlockRedstoneWire.SOUTH, BlockPropertyRedstoneSide.SIDE), 9, -3, 6, structureboundingbox);
            this.a(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.REDSTONE_WIRE.getBlockData().set(BlockRedstoneWire.NORTH, BlockPropertyRedstoneSide.SIDE)).set(BlockRedstoneWire.SOUTH, BlockPropertyRedstoneSide.UP), 9, -3, 5, structureboundingbox);
            this.a(generatoraccessseed, Blocks.MOSSY_COBBLESTONE.getBlockData(), 9, -3, 4, structureboundingbox);
            this.a(generatoraccessseed, iblockdata4, 9, -2, 4, structureboundingbox);
            if (!this.h) {
                this.h = this.a(generatoraccessseed, structureboundingbox, random, 9, -2, 3, EnumDirection.WEST, LootTables.B);
            }

            this.a(generatoraccessseed, (IBlockData) Blocks.VINE.getBlockData().set(BlockVine.EAST, true), 8, -1, 3, structureboundingbox);
            this.a(generatoraccessseed, (IBlockData) Blocks.VINE.getBlockData().set(BlockVine.EAST, true), 8, -2, 3, structureboundingbox);
            if (!this.e) {
                this.e = this.a(generatoraccessseed, structureboundingbox, random, 8, -3, 3, LootTables.A);
            }

            this.a(generatoraccessseed, Blocks.MOSSY_COBBLESTONE.getBlockData(), 9, -3, 2, structureboundingbox);
            this.a(generatoraccessseed, Blocks.MOSSY_COBBLESTONE.getBlockData(), 8, -3, 1, structureboundingbox);
            this.a(generatoraccessseed, Blocks.MOSSY_COBBLESTONE.getBlockData(), 4, -3, 5, structureboundingbox);
            this.a(generatoraccessseed, Blocks.MOSSY_COBBLESTONE.getBlockData(), 5, -2, 5, structureboundingbox);
            this.a(generatoraccessseed, Blocks.MOSSY_COBBLESTONE.getBlockData(), 5, -1, 5, structureboundingbox);
            this.a(generatoraccessseed, Blocks.MOSSY_COBBLESTONE.getBlockData(), 6, -3, 5, structureboundingbox);
            this.a(generatoraccessseed, Blocks.MOSSY_COBBLESTONE.getBlockData(), 7, -2, 5, structureboundingbox);
            this.a(generatoraccessseed, Blocks.MOSSY_COBBLESTONE.getBlockData(), 7, -1, 5, structureboundingbox);
            this.a(generatoraccessseed, Blocks.MOSSY_COBBLESTONE.getBlockData(), 8, -3, 5, structureboundingbox);
            this.a(generatoraccessseed, structureboundingbox, 9, -1, 1, 9, -1, 5, false, random, WorldGenJunglePyramidPiece.i);
            this.b(generatoraccessseed, structureboundingbox, 8, -3, 8, 10, -1, 10);
            this.a(generatoraccessseed, Blocks.CHISELED_STONE_BRICKS.getBlockData(), 8, -2, 11, structureboundingbox);
            this.a(generatoraccessseed, Blocks.CHISELED_STONE_BRICKS.getBlockData(), 9, -2, 11, structureboundingbox);
            this.a(generatoraccessseed, Blocks.CHISELED_STONE_BRICKS.getBlockData(), 10, -2, 11, structureboundingbox);
            IBlockData iblockdata5 = (IBlockData) ((IBlockData) Blocks.LEVER.getBlockData().set(BlockLever.FACING, EnumDirection.NORTH)).set(BlockLever.FACE, BlockPropertyAttachPosition.WALL);

            this.a(generatoraccessseed, iblockdata5, 8, -2, 12, structureboundingbox);
            this.a(generatoraccessseed, iblockdata5, 9, -2, 12, structureboundingbox);
            this.a(generatoraccessseed, iblockdata5, 10, -2, 12, structureboundingbox);
            this.a(generatoraccessseed, structureboundingbox, 8, -3, 8, 8, -3, 10, false, random, WorldGenJunglePyramidPiece.i);
            this.a(generatoraccessseed, structureboundingbox, 10, -3, 8, 10, -3, 10, false, random, WorldGenJunglePyramidPiece.i);
            this.a(generatoraccessseed, Blocks.MOSSY_COBBLESTONE.getBlockData(), 10, -2, 9, structureboundingbox);
            this.a(generatoraccessseed, iblockdata4, 8, -2, 9, structureboundingbox);
            this.a(generatoraccessseed, iblockdata4, 8, -2, 10, structureboundingbox);
            this.a(generatoraccessseed, (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) Blocks.REDSTONE_WIRE.getBlockData().set(BlockRedstoneWire.NORTH, BlockPropertyRedstoneSide.SIDE)).set(BlockRedstoneWire.SOUTH, BlockPropertyRedstoneSide.SIDE)).set(BlockRedstoneWire.EAST, BlockPropertyRedstoneSide.SIDE)).set(BlockRedstoneWire.WEST, BlockPropertyRedstoneSide.SIDE), 10, -1, 9, structureboundingbox);
            this.a(generatoraccessseed, (IBlockData) Blocks.STICKY_PISTON.getBlockData().set(BlockPiston.FACING, EnumDirection.UP), 9, -2, 8, structureboundingbox);
            this.a(generatoraccessseed, (IBlockData) Blocks.STICKY_PISTON.getBlockData().set(BlockPiston.FACING, EnumDirection.WEST), 10, -2, 8, structureboundingbox);
            this.a(generatoraccessseed, (IBlockData) Blocks.STICKY_PISTON.getBlockData().set(BlockPiston.FACING, EnumDirection.WEST), 10, -1, 8, structureboundingbox);
            this.a(generatoraccessseed, (IBlockData) Blocks.REPEATER.getBlockData().set(BlockRepeater.FACING, EnumDirection.NORTH), 10, -2, 10, structureboundingbox);
            if (!this.f) {
                this.f = this.a(generatoraccessseed, structureboundingbox, random, 9, -3, 10, LootTables.A);
            }

            return true;
        }
    }

    static class a extends StructurePiece.StructurePieceBlockSelector {

        private a() {}

        @Override
        public void a(Random random, int i, int j, int k, boolean flag) {
            if (random.nextFloat() < 0.4F) {
                this.a = Blocks.COBBLESTONE.getBlockData();
            } else {
                this.a = Blocks.MOSSY_COBBLESTONE.getBlockData();
            }

        }
    }
}
