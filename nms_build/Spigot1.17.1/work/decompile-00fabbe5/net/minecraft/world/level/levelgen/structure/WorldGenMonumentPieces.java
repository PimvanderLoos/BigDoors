package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.monster.EntityGuardianElder;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureStructurePieceType;

public class WorldGenMonumentPieces {

    private WorldGenMonumentPieces() {}

    private static class WorldGenMonumentPieceSelector4 implements WorldGenMonumentPieces.IWorldGenMonumentPieceSelector {

        WorldGenMonumentPieceSelector4() {}

        @Override
        public boolean a(WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker) {
            if (worldgenmonumentpieces_worldgenmonumentstatetracker.hasOpening[EnumDirection.NORTH.b()] && !worldgenmonumentpieces_worldgenmonumentstatetracker.connections[EnumDirection.NORTH.b()].claimed && worldgenmonumentpieces_worldgenmonumentstatetracker.hasOpening[EnumDirection.UP.b()] && !worldgenmonumentpieces_worldgenmonumentstatetracker.connections[EnumDirection.UP.b()].claimed) {
                WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker1 = worldgenmonumentpieces_worldgenmonumentstatetracker.connections[EnumDirection.NORTH.b()];

                return worldgenmonumentpieces_worldgenmonumentstatetracker1.hasOpening[EnumDirection.UP.b()] && !worldgenmonumentpieces_worldgenmonumentstatetracker1.connections[EnumDirection.UP.b()].claimed;
            } else {
                return false;
            }
        }

        @Override
        public WorldGenMonumentPieces.WorldGenMonumentPiece a(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker, Random random) {
            worldgenmonumentpieces_worldgenmonumentstatetracker.claimed = true;
            worldgenmonumentpieces_worldgenmonumentstatetracker.connections[EnumDirection.NORTH.b()].claimed = true;
            worldgenmonumentpieces_worldgenmonumentstatetracker.connections[EnumDirection.UP.b()].claimed = true;
            worldgenmonumentpieces_worldgenmonumentstatetracker.connections[EnumDirection.NORTH.b()].connections[EnumDirection.UP.b()].claimed = true;
            return new WorldGenMonumentPieces.WorldGenMonumentPiece6(enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker);
        }
    }

    private static class WorldGenMonumentPieceSelector6 implements WorldGenMonumentPieces.IWorldGenMonumentPieceSelector {

        WorldGenMonumentPieceSelector6() {}

        @Override
        public boolean a(WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker) {
            if (worldgenmonumentpieces_worldgenmonumentstatetracker.hasOpening[EnumDirection.EAST.b()] && !worldgenmonumentpieces_worldgenmonumentstatetracker.connections[EnumDirection.EAST.b()].claimed && worldgenmonumentpieces_worldgenmonumentstatetracker.hasOpening[EnumDirection.UP.b()] && !worldgenmonumentpieces_worldgenmonumentstatetracker.connections[EnumDirection.UP.b()].claimed) {
                WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker1 = worldgenmonumentpieces_worldgenmonumentstatetracker.connections[EnumDirection.EAST.b()];

                return worldgenmonumentpieces_worldgenmonumentstatetracker1.hasOpening[EnumDirection.UP.b()] && !worldgenmonumentpieces_worldgenmonumentstatetracker1.connections[EnumDirection.UP.b()].claimed;
            } else {
                return false;
            }
        }

        @Override
        public WorldGenMonumentPieces.WorldGenMonumentPiece a(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker, Random random) {
            worldgenmonumentpieces_worldgenmonumentstatetracker.claimed = true;
            worldgenmonumentpieces_worldgenmonumentstatetracker.connections[EnumDirection.EAST.b()].claimed = true;
            worldgenmonumentpieces_worldgenmonumentstatetracker.connections[EnumDirection.UP.b()].claimed = true;
            worldgenmonumentpieces_worldgenmonumentstatetracker.connections[EnumDirection.EAST.b()].connections[EnumDirection.UP.b()].claimed = true;
            return new WorldGenMonumentPieces.WorldGenMonumentPiece4(enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker);
        }
    }

    private static class WorldGenMonumentPieceSelector3 implements WorldGenMonumentPieces.IWorldGenMonumentPieceSelector {

        WorldGenMonumentPieceSelector3() {}

        @Override
        public boolean a(WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker) {
            return worldgenmonumentpieces_worldgenmonumentstatetracker.hasOpening[EnumDirection.NORTH.b()] && !worldgenmonumentpieces_worldgenmonumentstatetracker.connections[EnumDirection.NORTH.b()].claimed;
        }

        @Override
        public WorldGenMonumentPieces.WorldGenMonumentPiece a(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker, Random random) {
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker1 = worldgenmonumentpieces_worldgenmonumentstatetracker;

            if (!worldgenmonumentpieces_worldgenmonumentstatetracker.hasOpening[EnumDirection.NORTH.b()] || worldgenmonumentpieces_worldgenmonumentstatetracker.connections[EnumDirection.NORTH.b()].claimed) {
                worldgenmonumentpieces_worldgenmonumentstatetracker1 = worldgenmonumentpieces_worldgenmonumentstatetracker.connections[EnumDirection.SOUTH.b()];
            }

            worldgenmonumentpieces_worldgenmonumentstatetracker1.claimed = true;
            worldgenmonumentpieces_worldgenmonumentstatetracker1.connections[EnumDirection.NORTH.b()].claimed = true;
            return new WorldGenMonumentPieces.WorldGenMonumentPiece7(enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker1);
        }
    }

    private static class WorldGenMonumentPieceSelector7 implements WorldGenMonumentPieces.IWorldGenMonumentPieceSelector {

        WorldGenMonumentPieceSelector7() {}

        @Override
        public boolean a(WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker) {
            return worldgenmonumentpieces_worldgenmonumentstatetracker.hasOpening[EnumDirection.EAST.b()] && !worldgenmonumentpieces_worldgenmonumentstatetracker.connections[EnumDirection.EAST.b()].claimed;
        }

        @Override
        public WorldGenMonumentPieces.WorldGenMonumentPiece a(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker, Random random) {
            worldgenmonumentpieces_worldgenmonumentstatetracker.claimed = true;
            worldgenmonumentpieces_worldgenmonumentstatetracker.connections[EnumDirection.EAST.b()].claimed = true;
            return new WorldGenMonumentPieces.WorldGenMonumentPiece3(enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker);
        }
    }

    private static class WorldGenMonumentPieceSelector5 implements WorldGenMonumentPieces.IWorldGenMonumentPieceSelector {

        WorldGenMonumentPieceSelector5() {}

        @Override
        public boolean a(WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker) {
            return worldgenmonumentpieces_worldgenmonumentstatetracker.hasOpening[EnumDirection.UP.b()] && !worldgenmonumentpieces_worldgenmonumentstatetracker.connections[EnumDirection.UP.b()].claimed;
        }

        @Override
        public WorldGenMonumentPieces.WorldGenMonumentPiece a(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker, Random random) {
            worldgenmonumentpieces_worldgenmonumentstatetracker.claimed = true;
            worldgenmonumentpieces_worldgenmonumentstatetracker.connections[EnumDirection.UP.b()].claimed = true;
            return new WorldGenMonumentPieces.WorldGenMonumentPiece5(enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker);
        }
    }

    private static class WorldGenMonumentPieceSelector1 implements WorldGenMonumentPieces.IWorldGenMonumentPieceSelector {

        WorldGenMonumentPieceSelector1() {}

        @Override
        public boolean a(WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker) {
            return !worldgenmonumentpieces_worldgenmonumentstatetracker.hasOpening[EnumDirection.WEST.b()] && !worldgenmonumentpieces_worldgenmonumentstatetracker.hasOpening[EnumDirection.EAST.b()] && !worldgenmonumentpieces_worldgenmonumentstatetracker.hasOpening[EnumDirection.NORTH.b()] && !worldgenmonumentpieces_worldgenmonumentstatetracker.hasOpening[EnumDirection.SOUTH.b()] && !worldgenmonumentpieces_worldgenmonumentstatetracker.hasOpening[EnumDirection.UP.b()];
        }

        @Override
        public WorldGenMonumentPieces.WorldGenMonumentPiece a(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker, Random random) {
            worldgenmonumentpieces_worldgenmonumentstatetracker.claimed = true;
            return new WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT(enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker);
        }
    }

    private static class WorldGenMonumentPieceSelector2 implements WorldGenMonumentPieces.IWorldGenMonumentPieceSelector {

        WorldGenMonumentPieceSelector2() {}

        @Override
        public boolean a(WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker) {
            return true;
        }

        @Override
        public WorldGenMonumentPieces.WorldGenMonumentPiece a(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker, Random random) {
            worldgenmonumentpieces_worldgenmonumentstatetracker.claimed = true;
            return new WorldGenMonumentPieces.WorldGenMonumentPieceSimple(enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker, random);
        }
    }

    private interface IWorldGenMonumentPieceSelector {

        boolean a(WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker);

        WorldGenMonumentPieces.WorldGenMonumentPiece a(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker, Random random);
    }

    private static class WorldGenMonumentStateTracker {

        final int index;
        final WorldGenMonumentPieces.WorldGenMonumentStateTracker[] connections = new WorldGenMonumentPieces.WorldGenMonumentStateTracker[6];
        final boolean[] hasOpening = new boolean[6];
        boolean claimed;
        boolean isSource;
        private int scanIndex;

        public WorldGenMonumentStateTracker(int i) {
            this.index = i;
        }

        public void a(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker) {
            this.connections[enumdirection.b()] = worldgenmonumentpieces_worldgenmonumentstatetracker;
            worldgenmonumentpieces_worldgenmonumentstatetracker.connections[enumdirection.opposite().b()] = this;
        }

        public void a() {
            for (int i = 0; i < 6; ++i) {
                this.hasOpening[i] = this.connections[i] != null;
            }

        }

        public boolean a(int i) {
            if (this.isSource) {
                return true;
            } else {
                this.scanIndex = i;

                for (int j = 0; j < 6; ++j) {
                    if (this.connections[j] != null && this.hasOpening[j] && this.connections[j].scanIndex != i && this.connections[j].a(i)) {
                        return true;
                    }
                }

                return false;
            }
        }

        public boolean b() {
            return this.index >= 75;
        }

        public int c() {
            int i = 0;

            for (int j = 0; j < 6; ++j) {
                if (this.hasOpening[j]) {
                    ++i;
                }
            }

            return i;
        }
    }

    public static class WorldGenMonumentPiecePenthouse extends WorldGenMonumentPieces.WorldGenMonumentPiece {

        public WorldGenMonumentPiecePenthouse(EnumDirection enumdirection, StructureBoundingBox structureboundingbox) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_PENTHOUSE, enumdirection, 1, structureboundingbox);
        }

        public WorldGenMonumentPiecePenthouse(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_PENTHOUSE, nbttagcompound);
        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            this.a(generatoraccessseed, structureboundingbox, 2, -1, 2, 11, -1, 11, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 0, -1, 0, 1, -1, 11, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_GRAY, false);
            this.a(generatoraccessseed, structureboundingbox, 12, -1, 0, 13, -1, 11, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_GRAY, false);
            this.a(generatoraccessseed, structureboundingbox, 2, -1, 0, 11, -1, 1, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_GRAY, false);
            this.a(generatoraccessseed, structureboundingbox, 2, -1, 12, 11, -1, 13, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_GRAY, false);
            this.a(generatoraccessseed, structureboundingbox, 0, 0, 0, 0, 0, 13, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 13, 0, 0, 13, 0, 13, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 0, 0, 12, 0, 0, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 0, 13, 12, 0, 13, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_LIGHT, false);

            for (int i = 2; i <= 11; i += 3) {
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.LAMP_BLOCK, 0, 0, i, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.LAMP_BLOCK, 13, 0, i, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.LAMP_BLOCK, i, 0, 0, structureboundingbox);
            }

            this.a(generatoraccessseed, structureboundingbox, 2, 0, 3, 4, 0, 9, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 9, 0, 3, 11, 0, 9, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 4, 0, 9, 9, 0, 11, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_LIGHT, false);
            this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_LIGHT, 5, 0, 8, structureboundingbox);
            this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_LIGHT, 8, 0, 8, structureboundingbox);
            this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_LIGHT, 10, 0, 10, structureboundingbox);
            this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_LIGHT, 3, 0, 10, structureboundingbox);
            this.a(generatoraccessseed, structureboundingbox, 3, 0, 3, 3, 0, 7, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_BLACK, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_BLACK, false);
            this.a(generatoraccessseed, structureboundingbox, 10, 0, 3, 10, 0, 7, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_BLACK, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_BLACK, false);
            this.a(generatoraccessseed, structureboundingbox, 6, 0, 10, 7, 0, 10, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_BLACK, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_BLACK, false);
            byte b0 = 3;

            for (int j = 0; j < 2; ++j) {
                for (int k = 2; k <= 8; k += 3) {
                    this.a(generatoraccessseed, structureboundingbox, b0, 0, k, b0, 2, k, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_LIGHT, false);
                }

                b0 = 10;
            }

            this.a(generatoraccessseed, structureboundingbox, 5, 0, 10, 5, 2, 10, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 8, 0, 10, 8, 2, 10, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 6, -1, 7, 7, -1, 8, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_BLACK, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.BASE_BLACK, false);
            this.a(generatoraccessseed, structureboundingbox, 6, -1, 3, 7, -1, 4);
            this.a(generatoraccessseed, structureboundingbox, 6, 1, 6);
            return true;
        }
    }

    public static class WorldGenMonumentPiece8 extends WorldGenMonumentPieces.WorldGenMonumentPiece {

        private int mainDesign;

        public WorldGenMonumentPiece8(EnumDirection enumdirection, StructureBoundingBox structureboundingbox, int i) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_WING_ROOM, enumdirection, 1, structureboundingbox);
            this.mainDesign = i & 1;
        }

        public WorldGenMonumentPiece8(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_WING_ROOM, nbttagcompound);
        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            if (this.mainDesign == 0) {
                int i;

                for (i = 0; i < 4; ++i) {
                    this.a(generatoraccessseed, structureboundingbox, 10 - i, 3 - i, 20 - i, 12 + i, 3 - i, 20, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, false);
                }

                this.a(generatoraccessseed, structureboundingbox, 7, 0, 6, 15, 0, 16, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 6, 0, 6, 6, 3, 20, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 16, 0, 6, 16, 3, 20, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 7, 1, 7, 7, 1, 20, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 15, 1, 7, 15, 1, 20, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 7, 1, 6, 9, 3, 6, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 13, 1, 6, 15, 3, 6, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 8, 1, 7, 9, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 13, 1, 7, 14, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 9, 0, 5, 13, 0, 5, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 10, 0, 7, 12, 0, 7, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_BLACK, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_BLACK, false);
                this.a(generatoraccessseed, structureboundingbox, 8, 0, 10, 8, 0, 12, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_BLACK, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_BLACK, false);
                this.a(generatoraccessseed, structureboundingbox, 14, 0, 10, 14, 0, 12, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_BLACK, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_BLACK, false);

                for (i = 18; i >= 7; i -= 3) {
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece8.LAMP_BLOCK, 6, 3, i, structureboundingbox);
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece8.LAMP_BLOCK, 16, 3, i, structureboundingbox);
                }

                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece8.LAMP_BLOCK, 10, 0, 10, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece8.LAMP_BLOCK, 12, 0, 10, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece8.LAMP_BLOCK, 10, 0, 12, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece8.LAMP_BLOCK, 12, 0, 12, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece8.LAMP_BLOCK, 8, 3, 6, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece8.LAMP_BLOCK, 14, 3, 6, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, 4, 2, 4, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece8.LAMP_BLOCK, 4, 1, 4, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, 4, 0, 4, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, 18, 2, 4, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece8.LAMP_BLOCK, 18, 1, 4, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, 18, 0, 4, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, 4, 2, 18, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece8.LAMP_BLOCK, 4, 1, 18, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, 4, 0, 18, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, 18, 2, 18, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece8.LAMP_BLOCK, 18, 1, 18, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, 18, 0, 18, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, 9, 7, 20, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, 13, 7, 20, structureboundingbox);
                this.a(generatoraccessseed, structureboundingbox, 6, 0, 21, 7, 4, 21, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 15, 0, 21, 16, 4, 21, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 11, 2, 16);
            } else if (this.mainDesign == 1) {
                this.a(generatoraccessseed, structureboundingbox, 9, 3, 18, 13, 3, 20, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 9, 0, 18, 9, 2, 18, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 13, 0, 18, 13, 2, 18, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, false);
                byte b0 = 9;
                boolean flag = true;
                boolean flag1 = true;

                int j;

                for (j = 0; j < 2; ++j) {
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, b0, 6, 20, structureboundingbox);
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece8.LAMP_BLOCK, b0, 5, 20, structureboundingbox);
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, b0, 4, 20, structureboundingbox);
                    b0 = 13;
                }

                this.a(generatoraccessseed, structureboundingbox, 7, 3, 7, 15, 3, 14, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, false);
                b0 = 10;

                for (j = 0; j < 2; ++j) {
                    this.a(generatoraccessseed, structureboundingbox, b0, 0, 10, b0, 6, 10, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, b0, 0, 12, b0, 6, 12, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, false);
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece8.LAMP_BLOCK, b0, 0, 10, structureboundingbox);
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece8.LAMP_BLOCK, b0, 0, 12, structureboundingbox);
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece8.LAMP_BLOCK, b0, 4, 10, structureboundingbox);
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece8.LAMP_BLOCK, b0, 4, 12, structureboundingbox);
                    b0 = 12;
                }

                b0 = 8;

                for (j = 0; j < 2; ++j) {
                    this.a(generatoraccessseed, structureboundingbox, b0, 0, 7, b0, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, b0, 0, 14, b0, 2, 14, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_LIGHT, false);
                    b0 = 14;
                }

                this.a(generatoraccessseed, structureboundingbox, 8, 3, 8, 8, 3, 13, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_BLACK, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_BLACK, false);
                this.a(generatoraccessseed, structureboundingbox, 14, 3, 8, 14, 3, 13, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_BLACK, WorldGenMonumentPieces.WorldGenMonumentPiece8.BASE_BLACK, false);
                this.a(generatoraccessseed, structureboundingbox, 11, 5, 13);
            }

            return true;
        }
    }

    public static class WorldGenMonumentPiece2 extends WorldGenMonumentPieces.WorldGenMonumentPiece {

        public WorldGenMonumentPiece2(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_CORE_ROOM, 1, enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker, 2, 2, 2);
        }

        public WorldGenMonumentPiece2(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_CORE_ROOM, nbttagcompound);
        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            this.a(generatoraccessseed, structureboundingbox, 1, 8, 0, 14, 8, 14, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_GRAY);
            boolean flag = true;
            IBlockData iblockdata = WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT;

            this.a(generatoraccessseed, structureboundingbox, 0, 7, 0, 0, 7, 15, iblockdata, iblockdata, false);
            this.a(generatoraccessseed, structureboundingbox, 15, 7, 0, 15, 7, 15, iblockdata, iblockdata, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 7, 0, 15, 7, 0, iblockdata, iblockdata, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 7, 15, 14, 7, 15, iblockdata, iblockdata, false);

            int i;

            for (i = 1; i <= 6; ++i) {
                iblockdata = WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT;
                if (i == 2 || i == 6) {
                    iblockdata = WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_GRAY;
                }

                for (int j = 0; j <= 15; j += 15) {
                    this.a(generatoraccessseed, structureboundingbox, j, i, 0, j, i, 1, iblockdata, iblockdata, false);
                    this.a(generatoraccessseed, structureboundingbox, j, i, 6, j, i, 9, iblockdata, iblockdata, false);
                    this.a(generatoraccessseed, structureboundingbox, j, i, 14, j, i, 15, iblockdata, iblockdata, false);
                }

                this.a(generatoraccessseed, structureboundingbox, 1, i, 0, 1, i, 0, iblockdata, iblockdata, false);
                this.a(generatoraccessseed, structureboundingbox, 6, i, 0, 9, i, 0, iblockdata, iblockdata, false);
                this.a(generatoraccessseed, structureboundingbox, 14, i, 0, 14, i, 0, iblockdata, iblockdata, false);
                this.a(generatoraccessseed, structureboundingbox, 1, i, 15, 14, i, 15, iblockdata, iblockdata, false);
            }

            this.a(generatoraccessseed, structureboundingbox, 6, 3, 6, 9, 6, 9, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_BLACK, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_BLACK, false);
            this.a(generatoraccessseed, structureboundingbox, 7, 4, 7, 8, 5, 8, Blocks.GOLD_BLOCK.getBlockData(), Blocks.GOLD_BLOCK.getBlockData(), false);

            for (i = 3; i <= 6; i += 3) {
                for (int k = 6; k <= 9; k += 3) {
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece2.LAMP_BLOCK, k, i, 6, structureboundingbox);
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece2.LAMP_BLOCK, k, i, 9, structureboundingbox);
                }
            }

            this.a(generatoraccessseed, structureboundingbox, 5, 1, 6, 5, 2, 6, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 5, 1, 9, 5, 2, 9, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 10, 1, 6, 10, 2, 6, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 10, 1, 9, 10, 2, 9, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 6, 1, 5, 6, 2, 5, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 9, 1, 5, 9, 2, 5, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 6, 1, 10, 6, 2, 10, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 9, 1, 10, 9, 2, 10, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 5, 2, 5, 5, 6, 5, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 5, 2, 10, 5, 6, 10, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 10, 2, 5, 10, 6, 5, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 10, 2, 10, 10, 6, 10, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 5, 7, 1, 5, 7, 6, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 10, 7, 1, 10, 7, 6, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 5, 7, 9, 5, 7, 14, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 10, 7, 9, 10, 7, 14, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 7, 5, 6, 7, 5, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 7, 10, 6, 7, 10, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 9, 7, 5, 14, 7, 5, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 9, 7, 10, 14, 7, 10, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 2, 1, 2, 2, 1, 3, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 3, 1, 2, 3, 1, 2, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 13, 1, 2, 13, 1, 3, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 12, 1, 2, 12, 1, 2, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 2, 1, 12, 2, 1, 13, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 3, 1, 13, 3, 1, 13, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 13, 1, 12, 13, 1, 13, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 12, 1, 13, 12, 1, 13, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece2.BASE_LIGHT, false);
            return true;
        }
    }

    public static class WorldGenMonumentPiece6 extends WorldGenMonumentPieces.WorldGenMonumentPiece {

        public WorldGenMonumentPiece6(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_DOUBLE_YZ_ROOM, 1, enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker, 1, 2, 2);
        }

        public WorldGenMonumentPiece6(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_DOUBLE_YZ_ROOM, nbttagcompound);
        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker = this.roomDefinition.connections[EnumDirection.NORTH.b()];
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker1 = this.roomDefinition;
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker2 = worldgenmonumentpieces_worldgenmonumentstatetracker.connections[EnumDirection.UP.b()];
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker3 = worldgenmonumentpieces_worldgenmonumentstatetracker1.connections[EnumDirection.UP.b()];

            if (this.roomDefinition.index / 25 > 0) {
                this.a(generatoraccessseed, structureboundingbox, 0, 8, worldgenmonumentpieces_worldgenmonumentstatetracker.hasOpening[EnumDirection.DOWN.b()]);
                this.a(generatoraccessseed, structureboundingbox, 0, 0, worldgenmonumentpieces_worldgenmonumentstatetracker1.hasOpening[EnumDirection.DOWN.b()]);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker3.connections[EnumDirection.UP.b()] == null) {
                this.a(generatoraccessseed, structureboundingbox, 1, 8, 1, 6, 8, 7, WorldGenMonumentPieces.WorldGenMonumentPiece6.BASE_GRAY);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker2.connections[EnumDirection.UP.b()] == null) {
                this.a(generatoraccessseed, structureboundingbox, 1, 8, 8, 6, 8, 14, WorldGenMonumentPieces.WorldGenMonumentPiece6.BASE_GRAY);
            }

            IBlockData iblockdata;
            int i;

            for (i = 1; i <= 7; ++i) {
                iblockdata = WorldGenMonumentPieces.WorldGenMonumentPiece6.BASE_LIGHT;
                if (i == 2 || i == 6) {
                    iblockdata = WorldGenMonumentPieces.WorldGenMonumentPiece6.BASE_GRAY;
                }

                this.a(generatoraccessseed, structureboundingbox, 0, i, 0, 0, i, 15, iblockdata, iblockdata, false);
                this.a(generatoraccessseed, structureboundingbox, 7, i, 0, 7, i, 15, iblockdata, iblockdata, false);
                this.a(generatoraccessseed, structureboundingbox, 1, i, 0, 6, i, 0, iblockdata, iblockdata, false);
                this.a(generatoraccessseed, structureboundingbox, 1, i, 15, 6, i, 15, iblockdata, iblockdata, false);
            }

            for (i = 1; i <= 7; ++i) {
                iblockdata = WorldGenMonumentPieces.WorldGenMonumentPiece6.BASE_BLACK;
                if (i == 2 || i == 6) {
                    iblockdata = WorldGenMonumentPieces.WorldGenMonumentPiece6.LAMP_BLOCK;
                }

                this.a(generatoraccessseed, structureboundingbox, 3, i, 7, 4, i, 8, iblockdata, iblockdata, false);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker1.hasOpening[EnumDirection.SOUTH.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 3, 1, 0, 4, 2, 0);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker1.hasOpening[EnumDirection.EAST.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 7, 1, 3, 7, 2, 4);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker1.hasOpening[EnumDirection.WEST.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 0, 1, 3, 0, 2, 4);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker.hasOpening[EnumDirection.NORTH.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 3, 1, 15, 4, 2, 15);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker.hasOpening[EnumDirection.WEST.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 0, 1, 11, 0, 2, 12);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker.hasOpening[EnumDirection.EAST.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 7, 1, 11, 7, 2, 12);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker3.hasOpening[EnumDirection.SOUTH.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 3, 5, 0, 4, 6, 0);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker3.hasOpening[EnumDirection.EAST.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 7, 5, 3, 7, 6, 4);
                this.a(generatoraccessseed, structureboundingbox, 5, 4, 2, 6, 4, 5, WorldGenMonumentPieces.WorldGenMonumentPiece6.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece6.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 6, 1, 2, 6, 3, 2, WorldGenMonumentPieces.WorldGenMonumentPiece6.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece6.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 6, 1, 5, 6, 3, 5, WorldGenMonumentPieces.WorldGenMonumentPiece6.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece6.BASE_LIGHT, false);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker3.hasOpening[EnumDirection.WEST.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 0, 5, 3, 0, 6, 4);
                this.a(generatoraccessseed, structureboundingbox, 1, 4, 2, 2, 4, 5, WorldGenMonumentPieces.WorldGenMonumentPiece6.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece6.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 1, 1, 2, 1, 3, 2, WorldGenMonumentPieces.WorldGenMonumentPiece6.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece6.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 1, 1, 5, 1, 3, 5, WorldGenMonumentPieces.WorldGenMonumentPiece6.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece6.BASE_LIGHT, false);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker2.hasOpening[EnumDirection.NORTH.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 3, 5, 15, 4, 6, 15);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker2.hasOpening[EnumDirection.WEST.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 0, 5, 11, 0, 6, 12);
                this.a(generatoraccessseed, structureboundingbox, 1, 4, 10, 2, 4, 13, WorldGenMonumentPieces.WorldGenMonumentPiece6.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece6.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 1, 1, 10, 1, 3, 10, WorldGenMonumentPieces.WorldGenMonumentPiece6.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece6.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 1, 1, 13, 1, 3, 13, WorldGenMonumentPieces.WorldGenMonumentPiece6.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece6.BASE_LIGHT, false);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker2.hasOpening[EnumDirection.EAST.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 7, 5, 11, 7, 6, 12);
                this.a(generatoraccessseed, structureboundingbox, 5, 4, 10, 6, 4, 13, WorldGenMonumentPieces.WorldGenMonumentPiece6.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece6.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 6, 1, 10, 6, 3, 10, WorldGenMonumentPieces.WorldGenMonumentPiece6.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece6.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 6, 1, 13, 6, 3, 13, WorldGenMonumentPieces.WorldGenMonumentPiece6.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece6.BASE_LIGHT, false);
            }

            return true;
        }
    }

    public static class WorldGenMonumentPiece4 extends WorldGenMonumentPieces.WorldGenMonumentPiece {

        public WorldGenMonumentPiece4(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_DOUBLE_XY_ROOM, 1, enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker, 2, 2, 1);
        }

        public WorldGenMonumentPiece4(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_DOUBLE_XY_ROOM, nbttagcompound);
        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker = this.roomDefinition.connections[EnumDirection.EAST.b()];
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker1 = this.roomDefinition;
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker2 = worldgenmonumentpieces_worldgenmonumentstatetracker1.connections[EnumDirection.UP.b()];
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker3 = worldgenmonumentpieces_worldgenmonumentstatetracker.connections[EnumDirection.UP.b()];

            if (this.roomDefinition.index / 25 > 0) {
                this.a(generatoraccessseed, structureboundingbox, 8, 0, worldgenmonumentpieces_worldgenmonumentstatetracker.hasOpening[EnumDirection.DOWN.b()]);
                this.a(generatoraccessseed, structureboundingbox, 0, 0, worldgenmonumentpieces_worldgenmonumentstatetracker1.hasOpening[EnumDirection.DOWN.b()]);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker2.connections[EnumDirection.UP.b()] == null) {
                this.a(generatoraccessseed, structureboundingbox, 1, 8, 1, 7, 8, 6, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_GRAY);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker3.connections[EnumDirection.UP.b()] == null) {
                this.a(generatoraccessseed, structureboundingbox, 8, 8, 1, 14, 8, 6, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_GRAY);
            }

            for (int i = 1; i <= 7; ++i) {
                IBlockData iblockdata = WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT;

                if (i == 2 || i == 6) {
                    iblockdata = WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_GRAY;
                }

                this.a(generatoraccessseed, structureboundingbox, 0, i, 0, 0, i, 7, iblockdata, iblockdata, false);
                this.a(generatoraccessseed, structureboundingbox, 15, i, 0, 15, i, 7, iblockdata, iblockdata, false);
                this.a(generatoraccessseed, structureboundingbox, 1, i, 0, 15, i, 0, iblockdata, iblockdata, false);
                this.a(generatoraccessseed, structureboundingbox, 1, i, 7, 14, i, 7, iblockdata, iblockdata, false);
            }

            this.a(generatoraccessseed, structureboundingbox, 2, 1, 3, 2, 7, 4, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 3, 1, 2, 4, 7, 2, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 3, 1, 5, 4, 7, 5, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 13, 1, 3, 13, 7, 4, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 11, 1, 2, 12, 7, 2, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 11, 1, 5, 12, 7, 5, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 5, 1, 3, 5, 3, 4, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 10, 1, 3, 10, 3, 4, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 5, 7, 2, 10, 7, 5, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 5, 5, 2, 5, 7, 2, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 10, 5, 2, 10, 7, 2, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 5, 5, 5, 5, 7, 5, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 10, 5, 5, 10, 7, 5, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, false);
            this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, 6, 6, 2, structureboundingbox);
            this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, 9, 6, 2, structureboundingbox);
            this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, 6, 6, 5, structureboundingbox);
            this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, 9, 6, 5, structureboundingbox);
            this.a(generatoraccessseed, structureboundingbox, 5, 4, 3, 6, 4, 4, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 9, 4, 3, 10, 4, 4, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece4.BASE_LIGHT, false);
            this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece4.LAMP_BLOCK, 5, 4, 2, structureboundingbox);
            this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece4.LAMP_BLOCK, 5, 4, 5, structureboundingbox);
            this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece4.LAMP_BLOCK, 10, 4, 2, structureboundingbox);
            this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece4.LAMP_BLOCK, 10, 4, 5, structureboundingbox);
            if (worldgenmonumentpieces_worldgenmonumentstatetracker1.hasOpening[EnumDirection.SOUTH.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 3, 1, 0, 4, 2, 0);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker1.hasOpening[EnumDirection.NORTH.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 3, 1, 7, 4, 2, 7);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker1.hasOpening[EnumDirection.WEST.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 0, 1, 3, 0, 2, 4);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker.hasOpening[EnumDirection.SOUTH.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 11, 1, 0, 12, 2, 0);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker.hasOpening[EnumDirection.NORTH.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 11, 1, 7, 12, 2, 7);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker.hasOpening[EnumDirection.EAST.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 15, 1, 3, 15, 2, 4);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker2.hasOpening[EnumDirection.SOUTH.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 3, 5, 0, 4, 6, 0);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker2.hasOpening[EnumDirection.NORTH.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 3, 5, 7, 4, 6, 7);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker2.hasOpening[EnumDirection.WEST.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 0, 5, 3, 0, 6, 4);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker3.hasOpening[EnumDirection.SOUTH.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 11, 5, 0, 12, 6, 0);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker3.hasOpening[EnumDirection.NORTH.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 11, 5, 7, 12, 6, 7);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker3.hasOpening[EnumDirection.EAST.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 15, 5, 3, 15, 6, 4);
            }

            return true;
        }
    }

    public static class WorldGenMonumentPiece7 extends WorldGenMonumentPieces.WorldGenMonumentPiece {

        public WorldGenMonumentPiece7(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_DOUBLE_Z_ROOM, 1, enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker, 1, 1, 2);
        }

        public WorldGenMonumentPiece7(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_DOUBLE_Z_ROOM, nbttagcompound);
        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker = this.roomDefinition.connections[EnumDirection.NORTH.b()];
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker1 = this.roomDefinition;

            if (this.roomDefinition.index / 25 > 0) {
                this.a(generatoraccessseed, structureboundingbox, 0, 8, worldgenmonumentpieces_worldgenmonumentstatetracker.hasOpening[EnumDirection.DOWN.b()]);
                this.a(generatoraccessseed, structureboundingbox, 0, 0, worldgenmonumentpieces_worldgenmonumentstatetracker1.hasOpening[EnumDirection.DOWN.b()]);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker1.connections[EnumDirection.UP.b()] == null) {
                this.a(generatoraccessseed, structureboundingbox, 1, 4, 1, 6, 4, 7, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_GRAY);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker.connections[EnumDirection.UP.b()] == null) {
                this.a(generatoraccessseed, structureboundingbox, 1, 4, 8, 6, 4, 14, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_GRAY);
            }

            this.a(generatoraccessseed, structureboundingbox, 0, 3, 0, 0, 3, 15, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 7, 3, 0, 7, 3, 15, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 3, 0, 7, 3, 0, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 3, 15, 6, 3, 15, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 0, 2, 0, 0, 2, 15, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_GRAY, false);
            this.a(generatoraccessseed, structureboundingbox, 7, 2, 0, 7, 2, 15, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_GRAY, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 2, 0, 7, 2, 0, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_GRAY, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 2, 15, 6, 2, 15, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_GRAY, false);
            this.a(generatoraccessseed, structureboundingbox, 0, 1, 0, 0, 1, 15, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 7, 1, 0, 7, 1, 15, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 1, 0, 7, 1, 0, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 1, 15, 6, 1, 15, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 1, 1, 1, 1, 2, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 6, 1, 1, 6, 1, 2, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 3, 1, 1, 3, 2, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 6, 3, 1, 6, 3, 2, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 1, 13, 1, 1, 14, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 6, 1, 13, 6, 1, 14, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 3, 13, 1, 3, 14, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 6, 3, 13, 6, 3, 14, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 2, 1, 6, 2, 3, 6, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 5, 1, 6, 5, 3, 6, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 2, 1, 9, 2, 3, 9, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 5, 1, 9, 5, 3, 9, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 3, 2, 6, 4, 2, 6, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 3, 2, 9, 4, 2, 9, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 2, 2, 7, 2, 2, 8, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 5, 2, 7, 5, 2, 8, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, false);
            this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece7.LAMP_BLOCK, 2, 2, 5, structureboundingbox);
            this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece7.LAMP_BLOCK, 5, 2, 5, structureboundingbox);
            this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece7.LAMP_BLOCK, 2, 2, 10, structureboundingbox);
            this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece7.LAMP_BLOCK, 5, 2, 10, structureboundingbox);
            this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, 2, 3, 5, structureboundingbox);
            this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, 5, 3, 5, structureboundingbox);
            this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, 2, 3, 10, structureboundingbox);
            this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece7.BASE_LIGHT, 5, 3, 10, structureboundingbox);
            if (worldgenmonumentpieces_worldgenmonumentstatetracker1.hasOpening[EnumDirection.SOUTH.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 3, 1, 0, 4, 2, 0);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker1.hasOpening[EnumDirection.EAST.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 7, 1, 3, 7, 2, 4);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker1.hasOpening[EnumDirection.WEST.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 0, 1, 3, 0, 2, 4);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker.hasOpening[EnumDirection.NORTH.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 3, 1, 15, 4, 2, 15);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker.hasOpening[EnumDirection.WEST.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 0, 1, 11, 0, 2, 12);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker.hasOpening[EnumDirection.EAST.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 7, 1, 11, 7, 2, 12);
            }

            return true;
        }
    }

    public static class WorldGenMonumentPiece3 extends WorldGenMonumentPieces.WorldGenMonumentPiece {

        public WorldGenMonumentPiece3(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_DOUBLE_X_ROOM, 1, enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker, 2, 1, 1);
        }

        public WorldGenMonumentPiece3(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_DOUBLE_X_ROOM, nbttagcompound);
        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker = this.roomDefinition.connections[EnumDirection.EAST.b()];
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker1 = this.roomDefinition;

            if (this.roomDefinition.index / 25 > 0) {
                this.a(generatoraccessseed, structureboundingbox, 8, 0, worldgenmonumentpieces_worldgenmonumentstatetracker.hasOpening[EnumDirection.DOWN.b()]);
                this.a(generatoraccessseed, structureboundingbox, 0, 0, worldgenmonumentpieces_worldgenmonumentstatetracker1.hasOpening[EnumDirection.DOWN.b()]);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker1.connections[EnumDirection.UP.b()] == null) {
                this.a(generatoraccessseed, structureboundingbox, 1, 4, 1, 7, 4, 6, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_GRAY);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker.connections[EnumDirection.UP.b()] == null) {
                this.a(generatoraccessseed, structureboundingbox, 8, 4, 1, 14, 4, 6, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_GRAY);
            }

            this.a(generatoraccessseed, structureboundingbox, 0, 3, 0, 0, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 15, 3, 0, 15, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 3, 0, 15, 3, 0, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 3, 7, 14, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 0, 2, 0, 0, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_GRAY, false);
            this.a(generatoraccessseed, structureboundingbox, 15, 2, 0, 15, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_GRAY, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 2, 0, 15, 2, 0, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_GRAY, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 2, 7, 14, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_GRAY, false);
            this.a(generatoraccessseed, structureboundingbox, 0, 1, 0, 0, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 15, 1, 0, 15, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 1, 0, 15, 1, 0, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 1, 7, 14, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 5, 1, 0, 10, 1, 4, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 6, 2, 0, 9, 2, 3, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_GRAY, false);
            this.a(generatoraccessseed, structureboundingbox, 5, 3, 0, 10, 3, 4, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece3.BASE_LIGHT, false);
            this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece3.LAMP_BLOCK, 6, 2, 3, structureboundingbox);
            this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece3.LAMP_BLOCK, 9, 2, 3, structureboundingbox);
            if (worldgenmonumentpieces_worldgenmonumentstatetracker1.hasOpening[EnumDirection.SOUTH.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 3, 1, 0, 4, 2, 0);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker1.hasOpening[EnumDirection.NORTH.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 3, 1, 7, 4, 2, 7);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker1.hasOpening[EnumDirection.WEST.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 0, 1, 3, 0, 2, 4);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker.hasOpening[EnumDirection.SOUTH.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 11, 1, 0, 12, 2, 0);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker.hasOpening[EnumDirection.NORTH.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 11, 1, 7, 12, 2, 7);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker.hasOpening[EnumDirection.EAST.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 15, 1, 3, 15, 2, 4);
            }

            return true;
        }
    }

    public static class WorldGenMonumentPiece5 extends WorldGenMonumentPieces.WorldGenMonumentPiece {

        public WorldGenMonumentPiece5(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_ROOM, 1, enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker, 1, 2, 1);
        }

        public WorldGenMonumentPiece5(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_ROOM, nbttagcompound);
        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            if (this.roomDefinition.index / 25 > 0) {
                this.a(generatoraccessseed, structureboundingbox, 0, 0, this.roomDefinition.hasOpening[EnumDirection.DOWN.b()]);
            }

            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker = this.roomDefinition.connections[EnumDirection.UP.b()];

            if (worldgenmonumentpieces_worldgenmonumentstatetracker.connections[EnumDirection.UP.b()] == null) {
                this.a(generatoraccessseed, structureboundingbox, 1, 8, 1, 6, 8, 6, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_GRAY);
            }

            this.a(generatoraccessseed, structureboundingbox, 0, 4, 0, 0, 4, 7, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 7, 4, 0, 7, 4, 7, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 4, 0, 6, 4, 0, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 4, 7, 6, 4, 7, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 2, 4, 1, 2, 4, 2, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 4, 2, 1, 4, 2, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 5, 4, 1, 5, 4, 2, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 6, 4, 2, 6, 4, 2, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 2, 4, 5, 2, 4, 6, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 4, 5, 1, 4, 5, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 5, 4, 5, 5, 4, 6, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 6, 4, 5, 6, 4, 5, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, false);
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker1 = this.roomDefinition;

            for (int i = 1; i <= 5; i += 4) {
                byte b0 = 0;

                if (worldgenmonumentpieces_worldgenmonumentstatetracker1.hasOpening[EnumDirection.SOUTH.b()]) {
                    this.a(generatoraccessseed, structureboundingbox, 2, i, b0, 2, i + 2, b0, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, 5, i, b0, 5, i + 2, b0, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, 3, i + 2, b0, 4, i + 2, b0, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, false);
                } else {
                    this.a(generatoraccessseed, structureboundingbox, 0, i, b0, 7, i + 2, b0, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, 0, i + 1, b0, 7, i + 1, b0, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_GRAY, false);
                }

                b0 = 7;
                if (worldgenmonumentpieces_worldgenmonumentstatetracker1.hasOpening[EnumDirection.NORTH.b()]) {
                    this.a(generatoraccessseed, structureboundingbox, 2, i, b0, 2, i + 2, b0, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, 5, i, b0, 5, i + 2, b0, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, 3, i + 2, b0, 4, i + 2, b0, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, false);
                } else {
                    this.a(generatoraccessseed, structureboundingbox, 0, i, b0, 7, i + 2, b0, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, 0, i + 1, b0, 7, i + 1, b0, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_GRAY, false);
                }

                byte b1 = 0;

                if (worldgenmonumentpieces_worldgenmonumentstatetracker1.hasOpening[EnumDirection.WEST.b()]) {
                    this.a(generatoraccessseed, structureboundingbox, b1, i, 2, b1, i + 2, 2, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, b1, i, 5, b1, i + 2, 5, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, b1, i + 2, 3, b1, i + 2, 4, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, false);
                } else {
                    this.a(generatoraccessseed, structureboundingbox, b1, i, 0, b1, i + 2, 7, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, b1, i + 1, 0, b1, i + 1, 7, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_GRAY, false);
                }

                b1 = 7;
                if (worldgenmonumentpieces_worldgenmonumentstatetracker1.hasOpening[EnumDirection.EAST.b()]) {
                    this.a(generatoraccessseed, structureboundingbox, b1, i, 2, b1, i + 2, 2, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, b1, i, 5, b1, i + 2, 5, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, b1, i + 2, 3, b1, i + 2, 4, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, false);
                } else {
                    this.a(generatoraccessseed, structureboundingbox, b1, i, 0, b1, i + 2, 7, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, b1, i + 1, 0, b1, i + 1, 7, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece5.BASE_GRAY, false);
                }

                worldgenmonumentpieces_worldgenmonumentstatetracker1 = worldgenmonumentpieces_worldgenmonumentstatetracker;
            }

            return true;
        }
    }

    public static class WorldGenMonumentPieceSimpleT extends WorldGenMonumentPieces.WorldGenMonumentPiece {

        public WorldGenMonumentPieceSimpleT(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_SIMPLE_TOP_ROOM, 1, enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker, 1, 1, 1);
        }

        public WorldGenMonumentPieceSimpleT(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_SIMPLE_TOP_ROOM, nbttagcompound);
        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            if (this.roomDefinition.index / 25 > 0) {
                this.a(generatoraccessseed, structureboundingbox, 0, 0, this.roomDefinition.hasOpening[EnumDirection.DOWN.b()]);
            }

            if (this.roomDefinition.connections[EnumDirection.UP.b()] == null) {
                this.a(generatoraccessseed, structureboundingbox, 1, 4, 1, 6, 4, 6, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_GRAY);
            }

            for (int i = 1; i <= 6; ++i) {
                for (int j = 1; j <= 6; ++j) {
                    if (random.nextInt(3) != 0) {
                        int k = 2 + (random.nextInt(4) == 0 ? 0 : 1);
                        IBlockData iblockdata = Blocks.WET_SPONGE.getBlockData();

                        this.a(generatoraccessseed, structureboundingbox, i, k, j, i, 3, j, iblockdata, iblockdata, false);
                    }
                }
            }

            this.a(generatoraccessseed, structureboundingbox, 0, 1, 0, 0, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 7, 1, 0, 7, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 1, 0, 6, 1, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 1, 7, 6, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 0, 2, 0, 0, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_BLACK, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_BLACK, false);
            this.a(generatoraccessseed, structureboundingbox, 7, 2, 0, 7, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_BLACK, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_BLACK, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 2, 0, 6, 2, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_BLACK, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_BLACK, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 2, 7, 6, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_BLACK, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_BLACK, false);
            this.a(generatoraccessseed, structureboundingbox, 0, 3, 0, 0, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 7, 3, 0, 7, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 3, 0, 6, 3, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 3, 7, 6, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 0, 1, 3, 0, 2, 4, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_BLACK, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_BLACK, false);
            this.a(generatoraccessseed, structureboundingbox, 7, 1, 3, 7, 2, 4, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_BLACK, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_BLACK, false);
            this.a(generatoraccessseed, structureboundingbox, 3, 1, 0, 4, 2, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_BLACK, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_BLACK, false);
            this.a(generatoraccessseed, structureboundingbox, 3, 1, 7, 4, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_BLACK, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.BASE_BLACK, false);
            if (this.roomDefinition.hasOpening[EnumDirection.SOUTH.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 3, 1, 0, 4, 2, 0);
            }

            return true;
        }
    }

    public static class WorldGenMonumentPieceSimple extends WorldGenMonumentPieces.WorldGenMonumentPiece {

        private int mainDesign;

        public WorldGenMonumentPieceSimple(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker, Random random) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_SIMPLE_ROOM, 1, enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker, 1, 1, 1);
            this.mainDesign = random.nextInt(3);
        }

        public WorldGenMonumentPieceSimple(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_SIMPLE_ROOM, nbttagcompound);
        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            if (this.roomDefinition.index / 25 > 0) {
                this.a(generatoraccessseed, structureboundingbox, 0, 0, this.roomDefinition.hasOpening[EnumDirection.DOWN.b()]);
            }

            if (this.roomDefinition.connections[EnumDirection.UP.b()] == null) {
                this.a(generatoraccessseed, structureboundingbox, 1, 4, 1, 6, 4, 6, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY);
            }

            boolean flag = this.mainDesign != 0 && random.nextBoolean() && !this.roomDefinition.hasOpening[EnumDirection.DOWN.b()] && !this.roomDefinition.hasOpening[EnumDirection.UP.b()] && this.roomDefinition.c() > 1;

            if (this.mainDesign == 0) {
                this.a(generatoraccessseed, structureboundingbox, 0, 1, 0, 2, 1, 2, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 0, 3, 0, 2, 3, 2, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 0, 2, 0, 0, 2, 2, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 1, 2, 0, 2, 2, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, false);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.LAMP_BLOCK, 1, 2, 1, structureboundingbox);
                this.a(generatoraccessseed, structureboundingbox, 5, 1, 0, 7, 1, 2, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 5, 3, 0, 7, 3, 2, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 7, 2, 0, 7, 2, 2, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 5, 2, 0, 6, 2, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, false);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.LAMP_BLOCK, 6, 2, 1, structureboundingbox);
                this.a(generatoraccessseed, structureboundingbox, 0, 1, 5, 2, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 0, 3, 5, 2, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 0, 2, 5, 0, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 1, 2, 7, 2, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, false);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.LAMP_BLOCK, 1, 2, 6, structureboundingbox);
                this.a(generatoraccessseed, structureboundingbox, 5, 1, 5, 7, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 5, 3, 5, 7, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 7, 2, 5, 7, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 5, 2, 7, 6, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, false);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.LAMP_BLOCK, 6, 2, 6, structureboundingbox);
                if (this.roomDefinition.hasOpening[EnumDirection.SOUTH.b()]) {
                    this.a(generatoraccessseed, structureboundingbox, 3, 3, 0, 4, 3, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                } else {
                    this.a(generatoraccessseed, structureboundingbox, 3, 3, 0, 4, 3, 1, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, 3, 2, 0, 4, 2, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, false);
                    this.a(generatoraccessseed, structureboundingbox, 3, 1, 0, 4, 1, 1, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                }

                if (this.roomDefinition.hasOpening[EnumDirection.NORTH.b()]) {
                    this.a(generatoraccessseed, structureboundingbox, 3, 3, 7, 4, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                } else {
                    this.a(generatoraccessseed, structureboundingbox, 3, 3, 6, 4, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, 3, 2, 7, 4, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, false);
                    this.a(generatoraccessseed, structureboundingbox, 3, 1, 6, 4, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                }

                if (this.roomDefinition.hasOpening[EnumDirection.WEST.b()]) {
                    this.a(generatoraccessseed, structureboundingbox, 0, 3, 3, 0, 3, 4, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                } else {
                    this.a(generatoraccessseed, structureboundingbox, 0, 3, 3, 1, 3, 4, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, 0, 2, 3, 0, 2, 4, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, false);
                    this.a(generatoraccessseed, structureboundingbox, 0, 1, 3, 1, 1, 4, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                }

                if (this.roomDefinition.hasOpening[EnumDirection.EAST.b()]) {
                    this.a(generatoraccessseed, structureboundingbox, 7, 3, 3, 7, 3, 4, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                } else {
                    this.a(generatoraccessseed, structureboundingbox, 6, 3, 3, 7, 3, 4, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, 7, 2, 3, 7, 2, 4, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, false);
                    this.a(generatoraccessseed, structureboundingbox, 6, 1, 3, 7, 1, 4, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                }
            } else if (this.mainDesign == 1) {
                this.a(generatoraccessseed, structureboundingbox, 2, 1, 2, 2, 3, 2, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 2, 1, 5, 2, 3, 5, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 5, 1, 5, 5, 3, 5, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 5, 1, 2, 5, 3, 2, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.LAMP_BLOCK, 2, 2, 2, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.LAMP_BLOCK, 2, 2, 5, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.LAMP_BLOCK, 5, 2, 5, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.LAMP_BLOCK, 5, 2, 2, structureboundingbox);
                this.a(generatoraccessseed, structureboundingbox, 0, 1, 0, 1, 3, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 0, 1, 1, 0, 3, 1, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 0, 1, 7, 1, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 0, 1, 6, 0, 3, 6, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 6, 1, 7, 7, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 7, 1, 6, 7, 3, 6, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 6, 1, 0, 7, 3, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 7, 1, 1, 7, 3, 1, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, 1, 2, 0, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, 0, 2, 1, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, 1, 2, 7, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, 0, 2, 6, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, 6, 2, 7, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, 7, 2, 6, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, 6, 2, 0, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, 7, 2, 1, structureboundingbox);
                if (!this.roomDefinition.hasOpening[EnumDirection.SOUTH.b()]) {
                    this.a(generatoraccessseed, structureboundingbox, 1, 3, 0, 6, 3, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, 1, 2, 0, 6, 2, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, false);
                    this.a(generatoraccessseed, structureboundingbox, 1, 1, 0, 6, 1, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                }

                if (!this.roomDefinition.hasOpening[EnumDirection.NORTH.b()]) {
                    this.a(generatoraccessseed, structureboundingbox, 1, 3, 7, 6, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, 1, 2, 7, 6, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, false);
                    this.a(generatoraccessseed, structureboundingbox, 1, 1, 7, 6, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                }

                if (!this.roomDefinition.hasOpening[EnumDirection.WEST.b()]) {
                    this.a(generatoraccessseed, structureboundingbox, 0, 3, 1, 0, 3, 6, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, 0, 2, 1, 0, 2, 6, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, false);
                    this.a(generatoraccessseed, structureboundingbox, 0, 1, 1, 0, 1, 6, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                }

                if (!this.roomDefinition.hasOpening[EnumDirection.EAST.b()]) {
                    this.a(generatoraccessseed, structureboundingbox, 7, 3, 1, 7, 3, 6, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, 7, 2, 1, 7, 2, 6, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, false);
                    this.a(generatoraccessseed, structureboundingbox, 7, 1, 1, 7, 1, 6, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                }
            } else if (this.mainDesign == 2) {
                this.a(generatoraccessseed, structureboundingbox, 0, 1, 0, 0, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 7, 1, 0, 7, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 1, 1, 0, 6, 1, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 1, 1, 7, 6, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 0, 2, 0, 0, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_BLACK, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_BLACK, false);
                this.a(generatoraccessseed, structureboundingbox, 7, 2, 0, 7, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_BLACK, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_BLACK, false);
                this.a(generatoraccessseed, structureboundingbox, 1, 2, 0, 6, 2, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_BLACK, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_BLACK, false);
                this.a(generatoraccessseed, structureboundingbox, 1, 2, 7, 6, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_BLACK, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_BLACK, false);
                this.a(generatoraccessseed, structureboundingbox, 0, 3, 0, 0, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 7, 3, 0, 7, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 1, 3, 0, 6, 3, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 1, 3, 7, 6, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 0, 1, 3, 0, 2, 4, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_BLACK, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_BLACK, false);
                this.a(generatoraccessseed, structureboundingbox, 7, 1, 3, 7, 2, 4, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_BLACK, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_BLACK, false);
                this.a(generatoraccessseed, structureboundingbox, 3, 1, 0, 4, 2, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_BLACK, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_BLACK, false);
                this.a(generatoraccessseed, structureboundingbox, 3, 1, 7, 4, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_BLACK, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_BLACK, false);
                if (this.roomDefinition.hasOpening[EnumDirection.SOUTH.b()]) {
                    this.a(generatoraccessseed, structureboundingbox, 3, 1, 0, 4, 2, 0);
                }

                if (this.roomDefinition.hasOpening[EnumDirection.NORTH.b()]) {
                    this.a(generatoraccessseed, structureboundingbox, 3, 1, 7, 4, 2, 7);
                }

                if (this.roomDefinition.hasOpening[EnumDirection.WEST.b()]) {
                    this.a(generatoraccessseed, structureboundingbox, 0, 1, 3, 0, 2, 4);
                }

                if (this.roomDefinition.hasOpening[EnumDirection.EAST.b()]) {
                    this.a(generatoraccessseed, structureboundingbox, 7, 1, 3, 7, 2, 4);
                }
            }

            if (flag) {
                this.a(generatoraccessseed, structureboundingbox, 3, 1, 3, 4, 1, 4, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 3, 2, 3, 4, 2, 4, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 3, 3, 3, 4, 3, 4, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.BASE_LIGHT, false);
            }

            return true;
        }
    }

    public static class WorldGenMonumentPieceEntry extends WorldGenMonumentPieces.WorldGenMonumentPiece {

        public WorldGenMonumentPieceEntry(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_ENTRY_ROOM, 1, enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker, 1, 1, 1);
        }

        public WorldGenMonumentPieceEntry(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_ENTRY_ROOM, nbttagcompound);
        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            this.a(generatoraccessseed, structureboundingbox, 0, 3, 0, 2, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 5, 3, 0, 7, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 0, 2, 0, 1, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 6, 2, 0, 7, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 0, 1, 0, 0, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 7, 1, 0, 7, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 0, 1, 7, 7, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 1, 0, 2, 3, 0, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.BASE_LIGHT, false);
            this.a(generatoraccessseed, structureboundingbox, 5, 1, 0, 6, 3, 0, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.BASE_LIGHT, false);
            if (this.roomDefinition.hasOpening[EnumDirection.NORTH.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 3, 1, 7, 4, 2, 7);
            }

            if (this.roomDefinition.hasOpening[EnumDirection.WEST.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 0, 1, 3, 1, 2, 4);
            }

            if (this.roomDefinition.hasOpening[EnumDirection.EAST.b()]) {
                this.a(generatoraccessseed, structureboundingbox, 6, 1, 3, 7, 2, 4);
            }

            return true;
        }
    }

    public static class WorldGenMonumentPiece1 extends WorldGenMonumentPieces.WorldGenMonumentPiece {

        private static final int WIDTH = 58;
        private static final int HEIGHT = 22;
        private static final int DEPTH = 58;
        public static final int BIOME_RANGE_CHECK = 29;
        private static final int TOP_POSITION = 61;
        private WorldGenMonumentPieces.WorldGenMonumentStateTracker sourceRoom;
        private WorldGenMonumentPieces.WorldGenMonumentStateTracker coreRoom;
        private final List<WorldGenMonumentPieces.WorldGenMonumentPiece> childPieces = Lists.newArrayList();

        public WorldGenMonumentPiece1(Random random, int i, int j, EnumDirection enumdirection) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_BUILDING, enumdirection, 0, a(i, 39, j, enumdirection, 58, 23, 58));
            this.a(enumdirection);
            List<WorldGenMonumentPieces.WorldGenMonumentStateTracker> list = this.a(random);

            this.sourceRoom.claimed = true;
            this.childPieces.add(new WorldGenMonumentPieces.WorldGenMonumentPieceEntry(enumdirection, this.sourceRoom));
            this.childPieces.add(new WorldGenMonumentPieces.WorldGenMonumentPiece2(enumdirection, this.coreRoom));
            List<WorldGenMonumentPieces.IWorldGenMonumentPieceSelector> list1 = Lists.newArrayList();

            list1.add(new WorldGenMonumentPieces.WorldGenMonumentPieceSelector6());
            list1.add(new WorldGenMonumentPieces.WorldGenMonumentPieceSelector4());
            list1.add(new WorldGenMonumentPieces.WorldGenMonumentPieceSelector3());
            list1.add(new WorldGenMonumentPieces.WorldGenMonumentPieceSelector7());
            list1.add(new WorldGenMonumentPieces.WorldGenMonumentPieceSelector5());
            list1.add(new WorldGenMonumentPieces.WorldGenMonumentPieceSelector1());
            list1.add(new WorldGenMonumentPieces.WorldGenMonumentPieceSelector2());
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker = (WorldGenMonumentPieces.WorldGenMonumentStateTracker) iterator.next();

                if (!worldgenmonumentpieces_worldgenmonumentstatetracker.claimed && !worldgenmonumentpieces_worldgenmonumentstatetracker.b()) {
                    Iterator iterator1 = list1.iterator();

                    while (iterator1.hasNext()) {
                        WorldGenMonumentPieces.IWorldGenMonumentPieceSelector worldgenmonumentpieces_iworldgenmonumentpieceselector = (WorldGenMonumentPieces.IWorldGenMonumentPieceSelector) iterator1.next();

                        if (worldgenmonumentpieces_iworldgenmonumentpieceselector.a(worldgenmonumentpieces_worldgenmonumentstatetracker)) {
                            this.childPieces.add(worldgenmonumentpieces_iworldgenmonumentpieceselector.a(enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker, random));
                            break;
                        }
                    }
                }
            }

            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.c(9, 0, 22);
            Iterator iterator2 = this.childPieces.iterator();

            while (iterator2.hasNext()) {
                WorldGenMonumentPieces.WorldGenMonumentPiece worldgenmonumentpieces_worldgenmonumentpiece = (WorldGenMonumentPieces.WorldGenMonumentPiece) iterator2.next();

                worldgenmonumentpieces_worldgenmonumentpiece.f().a((BaseBlockPosition) blockposition_mutableblockposition);
            }

            StructureBoundingBox structureboundingbox = StructureBoundingBox.a(this.c(1, 1, 1), this.c(23, 8, 21));
            StructureBoundingBox structureboundingbox1 = StructureBoundingBox.a(this.c(34, 1, 1), this.c(56, 8, 21));
            StructureBoundingBox structureboundingbox2 = StructureBoundingBox.a(this.c(22, 13, 22), this.c(35, 17, 35));
            int k = random.nextInt();

            this.childPieces.add(new WorldGenMonumentPieces.WorldGenMonumentPiece8(enumdirection, structureboundingbox, k++));
            this.childPieces.add(new WorldGenMonumentPieces.WorldGenMonumentPiece8(enumdirection, structureboundingbox1, k++));
            this.childPieces.add(new WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse(enumdirection, structureboundingbox2));
        }

        public WorldGenMonumentPiece1(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_BUILDING, nbttagcompound);
        }

        private List<WorldGenMonumentPieces.WorldGenMonumentStateTracker> a(Random random) {
            WorldGenMonumentPieces.WorldGenMonumentStateTracker[] aworldgenmonumentpieces_worldgenmonumentstatetracker = new WorldGenMonumentPieces.WorldGenMonumentStateTracker[75];

            boolean flag;
            int i;
            int j;
            int k;

            for (j = 0; j < 5; ++j) {
                for (k = 0; k < 4; ++k) {
                    flag = false;
                    i = b(j, 0, k);
                    aworldgenmonumentpieces_worldgenmonumentstatetracker[i] = new WorldGenMonumentPieces.WorldGenMonumentStateTracker(i);
                }
            }

            for (j = 0; j < 5; ++j) {
                for (k = 0; k < 4; ++k) {
                    flag = true;
                    i = b(j, 1, k);
                    aworldgenmonumentpieces_worldgenmonumentstatetracker[i] = new WorldGenMonumentPieces.WorldGenMonumentStateTracker(i);
                }
            }

            for (j = 1; j < 4; ++j) {
                for (k = 0; k < 2; ++k) {
                    flag = true;
                    i = b(j, 2, k);
                    aworldgenmonumentpieces_worldgenmonumentstatetracker[i] = new WorldGenMonumentPieces.WorldGenMonumentStateTracker(i);
                }
            }

            this.sourceRoom = aworldgenmonumentpieces_worldgenmonumentstatetracker[WorldGenMonumentPieces.WorldGenMonumentPiece1.GRIDROOM_SOURCE_INDEX];

            int l;
            int i1;
            int j1;
            int k1;
            int l1;

            for (j = 0; j < 5; ++j) {
                for (k = 0; k < 5; ++k) {
                    for (int i2 = 0; i2 < 3; ++i2) {
                        i = b(j, i2, k);
                        if (aworldgenmonumentpieces_worldgenmonumentstatetracker[i] != null) {
                            EnumDirection[] aenumdirection = EnumDirection.values();

                            l = aenumdirection.length;

                            for (i1 = 0; i1 < l; ++i1) {
                                EnumDirection enumdirection = aenumdirection[i1];

                                j1 = j + enumdirection.getAdjacentX();
                                k1 = i2 + enumdirection.getAdjacentY();
                                l1 = k + enumdirection.getAdjacentZ();
                                if (j1 >= 0 && j1 < 5 && l1 >= 0 && l1 < 5 && k1 >= 0 && k1 < 3) {
                                    int j2 = b(j1, k1, l1);

                                    if (aworldgenmonumentpieces_worldgenmonumentstatetracker[j2] != null) {
                                        if (l1 == k) {
                                            aworldgenmonumentpieces_worldgenmonumentstatetracker[i].a(enumdirection, aworldgenmonumentpieces_worldgenmonumentstatetracker[j2]);
                                        } else {
                                            aworldgenmonumentpieces_worldgenmonumentstatetracker[i].a(enumdirection.opposite(), aworldgenmonumentpieces_worldgenmonumentstatetracker[j2]);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker = new WorldGenMonumentPieces.WorldGenMonumentStateTracker(1003);
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker1 = new WorldGenMonumentPieces.WorldGenMonumentStateTracker(1001);
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker2 = new WorldGenMonumentPieces.WorldGenMonumentStateTracker(1002);

            aworldgenmonumentpieces_worldgenmonumentstatetracker[WorldGenMonumentPieces.WorldGenMonumentPiece1.GRIDROOM_TOP_CONNECT_INDEX].a(EnumDirection.UP, worldgenmonumentpieces_worldgenmonumentstatetracker);
            aworldgenmonumentpieces_worldgenmonumentstatetracker[WorldGenMonumentPieces.WorldGenMonumentPiece1.GRIDROOM_LEFTWING_CONNECT_INDEX].a(EnumDirection.SOUTH, worldgenmonumentpieces_worldgenmonumentstatetracker1);
            aworldgenmonumentpieces_worldgenmonumentstatetracker[WorldGenMonumentPieces.WorldGenMonumentPiece1.GRIDROOM_RIGHTWING_CONNECT_INDEX].a(EnumDirection.SOUTH, worldgenmonumentpieces_worldgenmonumentstatetracker2);
            worldgenmonumentpieces_worldgenmonumentstatetracker.claimed = true;
            worldgenmonumentpieces_worldgenmonumentstatetracker1.claimed = true;
            worldgenmonumentpieces_worldgenmonumentstatetracker2.claimed = true;
            this.sourceRoom.isSource = true;
            this.coreRoom = aworldgenmonumentpieces_worldgenmonumentstatetracker[b(random.nextInt(4), 0, 2)];
            this.coreRoom.claimed = true;
            this.coreRoom.connections[EnumDirection.EAST.b()].claimed = true;
            this.coreRoom.connections[EnumDirection.NORTH.b()].claimed = true;
            this.coreRoom.connections[EnumDirection.EAST.b()].connections[EnumDirection.NORTH.b()].claimed = true;
            this.coreRoom.connections[EnumDirection.UP.b()].claimed = true;
            this.coreRoom.connections[EnumDirection.EAST.b()].connections[EnumDirection.UP.b()].claimed = true;
            this.coreRoom.connections[EnumDirection.NORTH.b()].connections[EnumDirection.UP.b()].claimed = true;
            this.coreRoom.connections[EnumDirection.EAST.b()].connections[EnumDirection.NORTH.b()].connections[EnumDirection.UP.b()].claimed = true;
            List<WorldGenMonumentPieces.WorldGenMonumentStateTracker> list = Lists.newArrayList();
            WorldGenMonumentPieces.WorldGenMonumentStateTracker[] aworldgenmonumentpieces_worldgenmonumentstatetracker1 = aworldgenmonumentpieces_worldgenmonumentstatetracker;

            l = aworldgenmonumentpieces_worldgenmonumentstatetracker.length;

            for (i1 = 0; i1 < l; ++i1) {
                WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker3 = aworldgenmonumentpieces_worldgenmonumentstatetracker1[i1];

                if (worldgenmonumentpieces_worldgenmonumentstatetracker3 != null) {
                    worldgenmonumentpieces_worldgenmonumentstatetracker3.a();
                    list.add(worldgenmonumentpieces_worldgenmonumentstatetracker3);
                }
            }

            worldgenmonumentpieces_worldgenmonumentstatetracker.a();
            Collections.shuffle(list, random);
            int k2 = 1;
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker4 = (WorldGenMonumentPieces.WorldGenMonumentStateTracker) iterator.next();
                int l2 = 0;

                j1 = 0;

                while (l2 < 2 && j1 < 5) {
                    ++j1;
                    k1 = random.nextInt(6);
                    if (worldgenmonumentpieces_worldgenmonumentstatetracker4.hasOpening[k1]) {
                        l1 = EnumDirection.fromType1(k1).opposite().b();
                        worldgenmonumentpieces_worldgenmonumentstatetracker4.hasOpening[k1] = false;
                        worldgenmonumentpieces_worldgenmonumentstatetracker4.connections[k1].hasOpening[l1] = false;
                        if (worldgenmonumentpieces_worldgenmonumentstatetracker4.a(k2++) && worldgenmonumentpieces_worldgenmonumentstatetracker4.connections[k1].a(k2++)) {
                            ++l2;
                        } else {
                            worldgenmonumentpieces_worldgenmonumentstatetracker4.hasOpening[k1] = true;
                            worldgenmonumentpieces_worldgenmonumentstatetracker4.connections[k1].hasOpening[l1] = true;
                        }
                    }
                }
            }

            list.add(worldgenmonumentpieces_worldgenmonumentstatetracker);
            list.add(worldgenmonumentpieces_worldgenmonumentstatetracker1);
            list.add(worldgenmonumentpieces_worldgenmonumentstatetracker2);
            return list;
        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            int i = Math.max(generatoraccessseed.getSeaLevel(), 64) - this.boundingBox.h();

            this.a(generatoraccessseed, structureboundingbox, 0, 0, 0, 58, i, 58);
            this.a(false, 0, generatoraccessseed, random, structureboundingbox);
            this.a(true, 33, generatoraccessseed, random, structureboundingbox);
            this.a(generatoraccessseed, random, structureboundingbox);
            this.b(generatoraccessseed, random, structureboundingbox);
            this.c(generatoraccessseed, random, structureboundingbox);
            this.d(generatoraccessseed, random, structureboundingbox);
            this.e(generatoraccessseed, random, structureboundingbox);
            this.f(generatoraccessseed, random, structureboundingbox);

            int j;

            for (j = 0; j < 7; ++j) {
                int k = 0;

                while (k < 7) {
                    if (k == 0 && j == 3) {
                        k = 6;
                    }

                    int l = j * 9;
                    int i1 = k * 9;

                    for (int j1 = 0; j1 < 4; ++j1) {
                        for (int k1 = 0; k1 < 4; ++k1) {
                            this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, l + j1, 0, i1 + k1, structureboundingbox);
                            this.a(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, l + j1, -1, i1 + k1, structureboundingbox);
                        }
                    }

                    if (j != 0 && j != 6) {
                        k += 6;
                    } else {
                        ++k;
                    }
                }
            }

            for (j = 0; j < 5; ++j) {
                this.a(generatoraccessseed, structureboundingbox, -1 - j, 0 + j * 2, -1 - j, -1 - j, 23, 58 + j);
                this.a(generatoraccessseed, structureboundingbox, 58 + j, 0 + j * 2, -1 - j, 58 + j, 23, 58 + j);
                this.a(generatoraccessseed, structureboundingbox, 0 - j, 0 + j * 2, -1 - j, 57 + j, 23, -1 - j);
                this.a(generatoraccessseed, structureboundingbox, 0 - j, 0 + j * 2, 58 + j, 57 + j, 23, 58 + j);
            }

            Iterator iterator = this.childPieces.iterator();

            while (iterator.hasNext()) {
                WorldGenMonumentPieces.WorldGenMonumentPiece worldgenmonumentpieces_worldgenmonumentpiece = (WorldGenMonumentPieces.WorldGenMonumentPiece) iterator.next();

                if (worldgenmonumentpieces_worldgenmonumentpiece.f().a(structureboundingbox)) {
                    worldgenmonumentpieces_worldgenmonumentpiece.a(generatoraccessseed, structuremanager, chunkgenerator, random, structureboundingbox, chunkcoordintpair, blockposition);
                }
            }

            return true;
        }

        private void a(boolean flag, int i, GeneratorAccessSeed generatoraccessseed, Random random, StructureBoundingBox structureboundingbox) {
            boolean flag1 = true;

            if (this.a(structureboundingbox, i, 0, i + 23, 20)) {
                this.a(generatoraccessseed, structureboundingbox, i + 0, 0, 0, i + 24, 0, 20, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, i + 0, 1, 0, i + 24, 10, 20);

                int j;

                for (j = 0; j < 4; ++j) {
                    this.a(generatoraccessseed, structureboundingbox, i + j, j + 1, j, i + j, j + 1, 20, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, i + j + 7, j + 5, j + 7, i + j + 7, j + 5, 20, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, i + 17 - j, j + 5, j + 7, i + 17 - j, j + 5, 20, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, i + 24 - j, j + 1, j, i + 24 - j, j + 1, 20, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, i + j + 1, j + 1, j, i + 23 - j, j + 1, j, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, i + j + 8, j + 5, j + 7, i + 16 - j, j + 5, j + 7, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, false);
                }

                this.a(generatoraccessseed, structureboundingbox, i + 4, 4, 4, i + 6, 4, 20, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, i + 7, 4, 4, i + 17, 4, 6, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, i + 18, 4, 4, i + 20, 4, 20, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, i + 11, 8, 11, i + 13, 8, 20, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.DOT_DECO_DATA, i + 12, 9, 12, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.DOT_DECO_DATA, i + 12, 9, 15, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.DOT_DECO_DATA, i + 12, 9, 18, structureboundingbox);
                j = i + (flag ? 19 : 5);
                int k = i + (flag ? 5 : 19);

                int l;

                for (l = 20; l >= 5; l -= 3) {
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.DOT_DECO_DATA, j, 5, l, structureboundingbox);
                }

                for (l = 19; l >= 7; l -= 3) {
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.DOT_DECO_DATA, k, 5, l, structureboundingbox);
                }

                for (l = 0; l < 4; ++l) {
                    int i1 = flag ? i + 24 - (17 - l * 3) : i + 17 - l * 3;

                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.DOT_DECO_DATA, i1, 5, 5, structureboundingbox);
                }

                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.DOT_DECO_DATA, k, 5, 5, structureboundingbox);
                this.a(generatoraccessseed, structureboundingbox, i + 11, 1, 12, i + 13, 7, 12, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, i + 12, 1, 11, i + 12, 7, 13, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
            }

        }

        private void a(GeneratorAccessSeed generatoraccessseed, Random random, StructureBoundingBox structureboundingbox) {
            if (this.a(structureboundingbox, 22, 5, 35, 17)) {
                this.a(generatoraccessseed, structureboundingbox, 25, 0, 0, 32, 8, 20);

                for (int i = 0; i < 4; ++i) {
                    this.a(generatoraccessseed, structureboundingbox, 24, 2, 5 + i * 4, 24, 4, 5 + i * 4, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, 22, 4, 5 + i * 4, 23, 4, 5 + i * 4, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, false);
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, 25, 5, 5 + i * 4, structureboundingbox);
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, 26, 6, 5 + i * 4, structureboundingbox);
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.LAMP_BLOCK, 26, 5, 5 + i * 4, structureboundingbox);
                    this.a(generatoraccessseed, structureboundingbox, 33, 2, 5 + i * 4, 33, 4, 5 + i * 4, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, 34, 4, 5 + i * 4, 35, 4, 5 + i * 4, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, false);
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, 32, 5, 5 + i * 4, structureboundingbox);
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, 31, 6, 5 + i * 4, structureboundingbox);
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.LAMP_BLOCK, 31, 5, 5 + i * 4, structureboundingbox);
                    this.a(generatoraccessseed, structureboundingbox, 27, 6, 5 + i * 4, 30, 6, 5 + i * 4, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                }
            }

        }

        private void b(GeneratorAccessSeed generatoraccessseed, Random random, StructureBoundingBox structureboundingbox) {
            if (this.a(structureboundingbox, 15, 20, 42, 21)) {
                this.a(generatoraccessseed, structureboundingbox, 15, 0, 21, 42, 0, 21, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 26, 1, 21, 31, 3, 21);
                this.a(generatoraccessseed, structureboundingbox, 21, 12, 21, 36, 12, 21, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 17, 11, 21, 40, 11, 21, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 16, 10, 21, 41, 10, 21, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 15, 7, 21, 42, 9, 21, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 16, 6, 21, 41, 6, 21, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 17, 5, 21, 40, 5, 21, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 21, 4, 21, 36, 4, 21, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 22, 3, 21, 26, 3, 21, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 31, 3, 21, 35, 3, 21, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 23, 2, 21, 25, 2, 21, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 32, 2, 21, 34, 2, 21, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 28, 4, 20, 29, 4, 21, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, false);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, 27, 3, 21, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, 30, 3, 21, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, 26, 2, 21, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, 31, 2, 21, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, 25, 1, 21, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, 32, 1, 21, structureboundingbox);

                int i;

                for (i = 0; i < 7; ++i) {
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_BLACK, 28 - i, 6 + i, 21, structureboundingbox);
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_BLACK, 29 + i, 6 + i, 21, structureboundingbox);
                }

                for (i = 0; i < 4; ++i) {
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_BLACK, 28 - i, 9 + i, 21, structureboundingbox);
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_BLACK, 29 + i, 9 + i, 21, structureboundingbox);
                }

                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_BLACK, 28, 12, 21, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_BLACK, 29, 12, 21, structureboundingbox);

                for (i = 0; i < 3; ++i) {
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_BLACK, 22 - i * 2, 8, 21, structureboundingbox);
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_BLACK, 22 - i * 2, 9, 21, structureboundingbox);
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_BLACK, 35 + i * 2, 8, 21, structureboundingbox);
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_BLACK, 35 + i * 2, 9, 21, structureboundingbox);
                }

                this.a(generatoraccessseed, structureboundingbox, 15, 13, 21, 42, 15, 21);
                this.a(generatoraccessseed, structureboundingbox, 15, 1, 21, 15, 6, 21);
                this.a(generatoraccessseed, structureboundingbox, 16, 1, 21, 16, 5, 21);
                this.a(generatoraccessseed, structureboundingbox, 17, 1, 21, 20, 4, 21);
                this.a(generatoraccessseed, structureboundingbox, 21, 1, 21, 21, 3, 21);
                this.a(generatoraccessseed, structureboundingbox, 22, 1, 21, 22, 2, 21);
                this.a(generatoraccessseed, structureboundingbox, 23, 1, 21, 24, 1, 21);
                this.a(generatoraccessseed, structureboundingbox, 42, 1, 21, 42, 6, 21);
                this.a(generatoraccessseed, structureboundingbox, 41, 1, 21, 41, 5, 21);
                this.a(generatoraccessseed, structureboundingbox, 37, 1, 21, 40, 4, 21);
                this.a(generatoraccessseed, structureboundingbox, 36, 1, 21, 36, 3, 21);
                this.a(generatoraccessseed, structureboundingbox, 33, 1, 21, 34, 1, 21);
                this.a(generatoraccessseed, structureboundingbox, 35, 1, 21, 35, 2, 21);
            }

        }

        private void c(GeneratorAccessSeed generatoraccessseed, Random random, StructureBoundingBox structureboundingbox) {
            if (this.a(structureboundingbox, 21, 21, 36, 36)) {
                this.a(generatoraccessseed, structureboundingbox, 21, 0, 22, 36, 0, 36, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 21, 1, 22, 36, 23, 36);

                for (int i = 0; i < 4; ++i) {
                    this.a(generatoraccessseed, structureboundingbox, 21 + i, 13 + i, 21 + i, 36 - i, 13 + i, 21 + i, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, 21 + i, 13 + i, 36 - i, 36 - i, 13 + i, 36 - i, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, 21 + i, 13 + i, 22 + i, 21 + i, 13 + i, 35 - i, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, false);
                    this.a(generatoraccessseed, structureboundingbox, 36 - i, 13 + i, 22 + i, 36 - i, 13 + i, 35 - i, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, false);
                }

                this.a(generatoraccessseed, structureboundingbox, 25, 16, 25, 32, 16, 32, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 25, 17, 25, 25, 19, 25, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 32, 17, 25, 32, 19, 25, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 25, 17, 32, 25, 19, 32, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 32, 17, 32, 32, 19, 32, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, false);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, 26, 20, 26, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, 27, 21, 27, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.LAMP_BLOCK, 27, 20, 27, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, 26, 20, 31, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, 27, 21, 30, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.LAMP_BLOCK, 27, 20, 30, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, 31, 20, 31, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, 30, 21, 30, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.LAMP_BLOCK, 30, 20, 30, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, 31, 20, 26, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, 30, 21, 27, structureboundingbox);
                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.LAMP_BLOCK, 30, 20, 27, structureboundingbox);
                this.a(generatoraccessseed, structureboundingbox, 28, 21, 27, 29, 21, 27, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 27, 21, 28, 27, 21, 29, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 28, 21, 30, 29, 21, 30, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 30, 21, 28, 30, 21, 29, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
            }

        }

        private void d(GeneratorAccessSeed generatoraccessseed, Random random, StructureBoundingBox structureboundingbox) {
            int i;

            if (this.a(structureboundingbox, 0, 21, 6, 58)) {
                this.a(generatoraccessseed, structureboundingbox, 0, 0, 21, 6, 0, 57, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 0, 1, 21, 6, 7, 57);
                this.a(generatoraccessseed, structureboundingbox, 4, 4, 21, 6, 4, 53, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);

                for (i = 0; i < 4; ++i) {
                    this.a(generatoraccessseed, structureboundingbox, i, i + 1, 21, i, i + 1, 57 - i, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, false);
                }

                for (i = 23; i < 53; i += 3) {
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.DOT_DECO_DATA, 5, 5, i, structureboundingbox);
                }

                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.DOT_DECO_DATA, 5, 5, 52, structureboundingbox);

                for (i = 0; i < 4; ++i) {
                    this.a(generatoraccessseed, structureboundingbox, i, i + 1, 21, i, i + 1, 57 - i, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, false);
                }

                this.a(generatoraccessseed, structureboundingbox, 4, 1, 52, 6, 3, 52, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 5, 1, 51, 5, 3, 53, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
            }

            if (this.a(structureboundingbox, 51, 21, 58, 58)) {
                this.a(generatoraccessseed, structureboundingbox, 51, 0, 21, 57, 0, 57, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 51, 1, 21, 57, 7, 57);
                this.a(generatoraccessseed, structureboundingbox, 51, 4, 21, 53, 4, 53, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);

                for (i = 0; i < 4; ++i) {
                    this.a(generatoraccessseed, structureboundingbox, 57 - i, i + 1, 21, 57 - i, i + 1, 57 - i, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, false);
                }

                for (i = 23; i < 53; i += 3) {
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.DOT_DECO_DATA, 52, 5, i, structureboundingbox);
                }

                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.DOT_DECO_DATA, 52, 5, 52, structureboundingbox);
                this.a(generatoraccessseed, structureboundingbox, 51, 1, 52, 53, 3, 52, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 52, 1, 51, 52, 3, 53, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
            }

            if (this.a(structureboundingbox, 0, 51, 57, 57)) {
                this.a(generatoraccessseed, structureboundingbox, 7, 0, 51, 50, 0, 57, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 7, 1, 51, 50, 10, 57);

                for (i = 0; i < 4; ++i) {
                    this.a(generatoraccessseed, structureboundingbox, i + 1, i + 1, 57 - i, 56 - i, i + 1, 57 - i, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, false);
                }
            }

        }

        private void e(GeneratorAccessSeed generatoraccessseed, Random random, StructureBoundingBox structureboundingbox) {
            int i;

            if (this.a(structureboundingbox, 7, 21, 13, 50)) {
                this.a(generatoraccessseed, structureboundingbox, 7, 0, 21, 13, 0, 50, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 7, 1, 21, 13, 10, 50);
                this.a(generatoraccessseed, structureboundingbox, 11, 8, 21, 13, 8, 53, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);

                for (i = 0; i < 4; ++i) {
                    this.a(generatoraccessseed, structureboundingbox, i + 7, i + 5, 21, i + 7, i + 5, 54, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, false);
                }

                for (i = 21; i <= 45; i += 3) {
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.DOT_DECO_DATA, 12, 9, i, structureboundingbox);
                }
            }

            if (this.a(structureboundingbox, 44, 21, 50, 54)) {
                this.a(generatoraccessseed, structureboundingbox, 44, 0, 21, 50, 0, 50, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 44, 1, 21, 50, 10, 50);
                this.a(generatoraccessseed, structureboundingbox, 44, 8, 21, 46, 8, 53, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);

                for (i = 0; i < 4; ++i) {
                    this.a(generatoraccessseed, structureboundingbox, 50 - i, i + 5, 21, 50 - i, i + 5, 54, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, false);
                }

                for (i = 21; i <= 45; i += 3) {
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.DOT_DECO_DATA, 45, 9, i, structureboundingbox);
                }
            }

            if (this.a(structureboundingbox, 8, 44, 49, 54)) {
                this.a(generatoraccessseed, structureboundingbox, 14, 0, 44, 43, 0, 50, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 14, 1, 44, 43, 10, 50);

                for (i = 12; i <= 45; i += 3) {
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.DOT_DECO_DATA, i, 9, 45, structureboundingbox);
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.DOT_DECO_DATA, i, 9, 52, structureboundingbox);
                    if (i == 12 || i == 18 || i == 24 || i == 33 || i == 39 || i == 45) {
                        this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.DOT_DECO_DATA, i, 9, 47, structureboundingbox);
                        this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.DOT_DECO_DATA, i, 9, 50, structureboundingbox);
                        this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.DOT_DECO_DATA, i, 10, 45, structureboundingbox);
                        this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.DOT_DECO_DATA, i, 10, 46, structureboundingbox);
                        this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.DOT_DECO_DATA, i, 10, 51, structureboundingbox);
                        this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.DOT_DECO_DATA, i, 10, 52, structureboundingbox);
                        this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.DOT_DECO_DATA, i, 11, 47, structureboundingbox);
                        this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.DOT_DECO_DATA, i, 11, 50, structureboundingbox);
                        this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.DOT_DECO_DATA, i, 12, 48, structureboundingbox);
                        this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.DOT_DECO_DATA, i, 12, 49, structureboundingbox);
                    }
                }

                for (i = 0; i < 3; ++i) {
                    this.a(generatoraccessseed, structureboundingbox, 8 + i, 5 + i, 54, 49 - i, 5 + i, 54, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                }

                this.a(generatoraccessseed, structureboundingbox, 11, 8, 54, 46, 8, 54, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, 14, 8, 44, 43, 8, 53, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
            }

        }

        private void f(GeneratorAccessSeed generatoraccessseed, Random random, StructureBoundingBox structureboundingbox) {
            int i;

            if (this.a(structureboundingbox, 14, 21, 20, 43)) {
                this.a(generatoraccessseed, structureboundingbox, 14, 0, 21, 20, 0, 43, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 14, 1, 22, 20, 14, 43);
                this.a(generatoraccessseed, structureboundingbox, 18, 12, 22, 20, 12, 39, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 18, 12, 21, 20, 12, 21, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, false);

                for (i = 0; i < 4; ++i) {
                    this.a(generatoraccessseed, structureboundingbox, i + 14, i + 9, 21, i + 14, i + 9, 43 - i, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, false);
                }

                for (i = 23; i <= 39; i += 3) {
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.DOT_DECO_DATA, 19, 13, i, structureboundingbox);
                }
            }

            if (this.a(structureboundingbox, 37, 21, 43, 43)) {
                this.a(generatoraccessseed, structureboundingbox, 37, 0, 21, 43, 0, 43, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 37, 1, 22, 43, 14, 43);
                this.a(generatoraccessseed, structureboundingbox, 37, 12, 22, 39, 12, 39, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 37, 12, 21, 39, 12, 21, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, false);

                for (i = 0; i < 4; ++i) {
                    this.a(generatoraccessseed, structureboundingbox, 43 - i, i + 9, 21, 43 - i, i + 9, 43 - i, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, false);
                }

                for (i = 23; i <= 39; i += 3) {
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.DOT_DECO_DATA, 38, 13, i, structureboundingbox);
                }
            }

            if (this.a(structureboundingbox, 15, 37, 42, 43)) {
                this.a(generatoraccessseed, structureboundingbox, 21, 0, 37, 36, 0, 43, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, 21, 1, 37, 36, 14, 43);
                this.a(generatoraccessseed, structureboundingbox, 21, 12, 37, 36, 12, 39, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_GRAY, false);

                for (i = 0; i < 4; ++i) {
                    this.a(generatoraccessseed, structureboundingbox, 15 + i, i + 9, 43 - i, 42 - i, i + 9, 43 - i, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece1.BASE_LIGHT, false);
                }

                for (i = 21; i <= 36; i += 3) {
                    this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece1.DOT_DECO_DATA, i, 13, 38, structureboundingbox);
                }
            }

        }
    }

    protected abstract static class WorldGenMonumentPiece extends StructurePiece {

        protected static final IBlockData BASE_GRAY = Blocks.PRISMARINE.getBlockData();
        protected static final IBlockData BASE_LIGHT = Blocks.PRISMARINE_BRICKS.getBlockData();
        protected static final IBlockData BASE_BLACK = Blocks.DARK_PRISMARINE.getBlockData();
        protected static final IBlockData DOT_DECO_DATA = WorldGenMonumentPieces.WorldGenMonumentPiece.BASE_LIGHT;
        protected static final IBlockData LAMP_BLOCK = Blocks.SEA_LANTERN.getBlockData();
        protected static final boolean DO_FILL = true;
        protected static final IBlockData FILL_BLOCK = Blocks.WATER.getBlockData();
        protected static final Set<Block> FILL_KEEP = ImmutableSet.builder().add(Blocks.ICE).add(Blocks.PACKED_ICE).add(Blocks.BLUE_ICE).add(WorldGenMonumentPieces.WorldGenMonumentPiece.FILL_BLOCK.getBlock()).build();
        protected static final int GRIDROOM_WIDTH = 8;
        protected static final int GRIDROOM_DEPTH = 8;
        protected static final int GRIDROOM_HEIGHT = 4;
        protected static final int GRID_WIDTH = 5;
        protected static final int GRID_DEPTH = 5;
        protected static final int GRID_HEIGHT = 3;
        protected static final int GRID_FLOOR_COUNT = 25;
        protected static final int GRID_SIZE = 75;
        protected static final int GRIDROOM_SOURCE_INDEX = b(2, 0, 0);
        protected static final int GRIDROOM_TOP_CONNECT_INDEX = b(2, 2, 0);
        protected static final int GRIDROOM_LEFTWING_CONNECT_INDEX = b(0, 1, 0);
        protected static final int GRIDROOM_RIGHTWING_CONNECT_INDEX = b(4, 1, 0);
        protected static final int LEFTWING_INDEX = 1001;
        protected static final int RIGHTWING_INDEX = 1002;
        protected static final int PENTHOUSE_INDEX = 1003;
        protected WorldGenMonumentPieces.WorldGenMonumentStateTracker roomDefinition;

        protected static int b(int i, int j, int k) {
            return j * 25 + k * 5 + i;
        }

        public WorldGenMonumentPiece(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, EnumDirection enumdirection, int i, StructureBoundingBox structureboundingbox) {
            super(worldgenfeaturestructurepiecetype, i, structureboundingbox);
            this.a(enumdirection);
        }

        protected WorldGenMonumentPiece(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, int i, EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker, int j, int k, int l) {
            super(worldgenfeaturestructurepiecetype, i, a(enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker, j, k, l));
            this.a(enumdirection);
            this.roomDefinition = worldgenmonumentpieces_worldgenmonumentstatetracker;
        }

        private static StructureBoundingBox a(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker, int i, int j, int k) {
            int l = worldgenmonumentpieces_worldgenmonumentstatetracker.index;
            int i1 = l % 5;
            int j1 = l / 5 % 5;
            int k1 = l / 25;
            StructureBoundingBox structureboundingbox = a(0, 0, 0, enumdirection, i * 8, j * 4, k * 8);

            switch (enumdirection) {
                case NORTH:
                    structureboundingbox.a(i1 * 8, k1 * 4, -(j1 + k) * 8 + 1);
                    break;
                case SOUTH:
                    structureboundingbox.a(i1 * 8, k1 * 4, j1 * 8);
                    break;
                case WEST:
                    structureboundingbox.a(-(j1 + k) * 8 + 1, k1 * 4, i1 * 8);
                    break;
                case EAST:
                default:
                    structureboundingbox.a(j1 * 8, k1 * 4, i1 * 8);
            }

            return structureboundingbox;
        }

        public WorldGenMonumentPiece(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, NBTTagCompound nbttagcompound) {
            super(worldgenfeaturestructurepiecetype, nbttagcompound);
        }

        @Override
        protected void a(WorldServer worldserver, NBTTagCompound nbttagcompound) {}

        protected void a(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, int i, int j, int k, int l, int i1, int j1) {
            for (int k1 = j; k1 <= i1; ++k1) {
                for (int l1 = i; l1 <= l; ++l1) {
                    for (int i2 = k; i2 <= j1; ++i2) {
                        IBlockData iblockdata = this.a((IBlockAccess) generatoraccessseed, l1, k1, i2, structureboundingbox);

                        if (!WorldGenMonumentPieces.WorldGenMonumentPiece.FILL_KEEP.contains(iblockdata.getBlock())) {
                            if (this.a(k1) >= generatoraccessseed.getSeaLevel() && iblockdata != WorldGenMonumentPieces.WorldGenMonumentPiece.FILL_BLOCK) {
                                this.c(generatoraccessseed, Blocks.AIR.getBlockData(), l1, k1, i2, structureboundingbox);
                            } else {
                                this.c(generatoraccessseed, WorldGenMonumentPieces.WorldGenMonumentPiece.FILL_BLOCK, l1, k1, i2, structureboundingbox);
                            }
                        }
                    }
                }
            }

        }

        protected void a(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, int i, int j, boolean flag) {
            if (flag) {
                this.a(generatoraccessseed, structureboundingbox, i + 0, 0, j + 0, i + 2, 0, j + 8 - 1, WorldGenMonumentPieces.WorldGenMonumentPiece.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, i + 5, 0, j + 0, i + 8 - 1, 0, j + 8 - 1, WorldGenMonumentPieces.WorldGenMonumentPiece.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, i + 3, 0, j + 0, i + 4, 0, j + 2, WorldGenMonumentPieces.WorldGenMonumentPiece.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, i + 3, 0, j + 5, i + 4, 0, j + 8 - 1, WorldGenMonumentPieces.WorldGenMonumentPiece.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece.BASE_GRAY, false);
                this.a(generatoraccessseed, structureboundingbox, i + 3, 0, j + 2, i + 4, 0, j + 2, WorldGenMonumentPieces.WorldGenMonumentPiece.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, i + 3, 0, j + 5, i + 4, 0, j + 5, WorldGenMonumentPieces.WorldGenMonumentPiece.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, i + 2, 0, j + 3, i + 2, 0, j + 4, WorldGenMonumentPieces.WorldGenMonumentPiece.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece.BASE_LIGHT, false);
                this.a(generatoraccessseed, structureboundingbox, i + 5, 0, j + 3, i + 5, 0, j + 4, WorldGenMonumentPieces.WorldGenMonumentPiece.BASE_LIGHT, WorldGenMonumentPieces.WorldGenMonumentPiece.BASE_LIGHT, false);
            } else {
                this.a(generatoraccessseed, structureboundingbox, i + 0, 0, j + 0, i + 8 - 1, 0, j + 8 - 1, WorldGenMonumentPieces.WorldGenMonumentPiece.BASE_GRAY, WorldGenMonumentPieces.WorldGenMonumentPiece.BASE_GRAY, false);
            }

        }

        protected void a(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, int i, int j, int k, int l, int i1, int j1, IBlockData iblockdata) {
            for (int k1 = j; k1 <= i1; ++k1) {
                for (int l1 = i; l1 <= l; ++l1) {
                    for (int i2 = k; i2 <= j1; ++i2) {
                        if (this.a((IBlockAccess) generatoraccessseed, l1, k1, i2, structureboundingbox) == WorldGenMonumentPieces.WorldGenMonumentPiece.FILL_BLOCK) {
                            this.c(generatoraccessseed, iblockdata, l1, k1, i2, structureboundingbox);
                        }
                    }
                }
            }

        }

        protected boolean a(StructureBoundingBox structureboundingbox, int i, int j, int k, int l) {
            int i1 = this.a(i, j);
            int j1 = this.b(i, j);
            int k1 = this.a(k, l);
            int l1 = this.b(k, l);

            return structureboundingbox.a(Math.min(i1, k1), Math.min(j1, l1), Math.max(i1, k1), Math.max(j1, l1));
        }

        protected boolean a(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, int i, int j, int k) {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.c(i, j, k);

            if (structureboundingbox.b((BaseBlockPosition) blockposition_mutableblockposition)) {
                EntityGuardianElder entityguardianelder = (EntityGuardianElder) EntityTypes.ELDER_GUARDIAN.a((World) generatoraccessseed.getLevel());

                entityguardianelder.heal(entityguardianelder.getMaxHealth());
                entityguardianelder.setPositionRotation((double) blockposition_mutableblockposition.getX() + 0.5D, (double) blockposition_mutableblockposition.getY(), (double) blockposition_mutableblockposition.getZ() + 0.5D, 0.0F, 0.0F);
                entityguardianelder.prepare(generatoraccessseed, generatoraccessseed.getDamageScaler(entityguardianelder.getChunkCoordinates()), EnumMobSpawn.STRUCTURE, (GroupDataEntity) null, (NBTTagCompound) null);
                generatoraccessseed.addAllEntities(entityguardianelder);
                return true;
            } else {
                return false;
            }
        }
    }
}
