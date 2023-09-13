package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.monster.EntityGuardianElder;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.WorldGenFeatureStructurePieceType;

public class OceanMonumentPieces {

    private OceanMonumentPieces() {}

    private static class d implements OceanMonumentPieces.i {

        d() {}

        @Override
        public boolean fits(OceanMonumentPieces.v oceanmonumentpieces_v) {
            if (oceanmonumentpieces_v.hasOpening[EnumDirection.NORTH.get3DDataValue()] && !oceanmonumentpieces_v.connections[EnumDirection.NORTH.get3DDataValue()].claimed && oceanmonumentpieces_v.hasOpening[EnumDirection.UP.get3DDataValue()] && !oceanmonumentpieces_v.connections[EnumDirection.UP.get3DDataValue()].claimed) {
                OceanMonumentPieces.v oceanmonumentpieces_v1 = oceanmonumentpieces_v.connections[EnumDirection.NORTH.get3DDataValue()];

                return oceanmonumentpieces_v1.hasOpening[EnumDirection.UP.get3DDataValue()] && !oceanmonumentpieces_v1.connections[EnumDirection.UP.get3DDataValue()].claimed;
            } else {
                return false;
            }
        }

        @Override
        public OceanMonumentPieces.r create(EnumDirection enumdirection, OceanMonumentPieces.v oceanmonumentpieces_v, RandomSource randomsource) {
            oceanmonumentpieces_v.claimed = true;
            oceanmonumentpieces_v.connections[EnumDirection.NORTH.get3DDataValue()].claimed = true;
            oceanmonumentpieces_v.connections[EnumDirection.UP.get3DDataValue()].claimed = true;
            oceanmonumentpieces_v.connections[EnumDirection.NORTH.get3DDataValue()].connections[EnumDirection.UP.get3DDataValue()].claimed = true;
            return new OceanMonumentPieces.n(enumdirection, oceanmonumentpieces_v);
        }
    }

    private static class b implements OceanMonumentPieces.i {

        b() {}

        @Override
        public boolean fits(OceanMonumentPieces.v oceanmonumentpieces_v) {
            if (oceanmonumentpieces_v.hasOpening[EnumDirection.EAST.get3DDataValue()] && !oceanmonumentpieces_v.connections[EnumDirection.EAST.get3DDataValue()].claimed && oceanmonumentpieces_v.hasOpening[EnumDirection.UP.get3DDataValue()] && !oceanmonumentpieces_v.connections[EnumDirection.UP.get3DDataValue()].claimed) {
                OceanMonumentPieces.v oceanmonumentpieces_v1 = oceanmonumentpieces_v.connections[EnumDirection.EAST.get3DDataValue()];

                return oceanmonumentpieces_v1.hasOpening[EnumDirection.UP.get3DDataValue()] && !oceanmonumentpieces_v1.connections[EnumDirection.UP.get3DDataValue()].claimed;
            } else {
                return false;
            }
        }

        @Override
        public OceanMonumentPieces.r create(EnumDirection enumdirection, OceanMonumentPieces.v oceanmonumentpieces_v, RandomSource randomsource) {
            oceanmonumentpieces_v.claimed = true;
            oceanmonumentpieces_v.connections[EnumDirection.EAST.get3DDataValue()].claimed = true;
            oceanmonumentpieces_v.connections[EnumDirection.UP.get3DDataValue()].claimed = true;
            oceanmonumentpieces_v.connections[EnumDirection.EAST.get3DDataValue()].connections[EnumDirection.UP.get3DDataValue()].claimed = true;
            return new OceanMonumentPieces.l(enumdirection, oceanmonumentpieces_v);
        }
    }

    private static class e implements OceanMonumentPieces.i {

        e() {}

        @Override
        public boolean fits(OceanMonumentPieces.v oceanmonumentpieces_v) {
            return oceanmonumentpieces_v.hasOpening[EnumDirection.NORTH.get3DDataValue()] && !oceanmonumentpieces_v.connections[EnumDirection.NORTH.get3DDataValue()].claimed;
        }

        @Override
        public OceanMonumentPieces.r create(EnumDirection enumdirection, OceanMonumentPieces.v oceanmonumentpieces_v, RandomSource randomsource) {
            OceanMonumentPieces.v oceanmonumentpieces_v1 = oceanmonumentpieces_v;

            if (!oceanmonumentpieces_v.hasOpening[EnumDirection.NORTH.get3DDataValue()] || oceanmonumentpieces_v.connections[EnumDirection.NORTH.get3DDataValue()].claimed) {
                oceanmonumentpieces_v1 = oceanmonumentpieces_v.connections[EnumDirection.SOUTH.get3DDataValue()];
            }

            oceanmonumentpieces_v1.claimed = true;
            oceanmonumentpieces_v1.connections[EnumDirection.NORTH.get3DDataValue()].claimed = true;
            return new OceanMonumentPieces.o(enumdirection, oceanmonumentpieces_v1);
        }
    }

    private static class a implements OceanMonumentPieces.i {

        a() {}

        @Override
        public boolean fits(OceanMonumentPieces.v oceanmonumentpieces_v) {
            return oceanmonumentpieces_v.hasOpening[EnumDirection.EAST.get3DDataValue()] && !oceanmonumentpieces_v.connections[EnumDirection.EAST.get3DDataValue()].claimed;
        }

        @Override
        public OceanMonumentPieces.r create(EnumDirection enumdirection, OceanMonumentPieces.v oceanmonumentpieces_v, RandomSource randomsource) {
            oceanmonumentpieces_v.claimed = true;
            oceanmonumentpieces_v.connections[EnumDirection.EAST.get3DDataValue()].claimed = true;
            return new OceanMonumentPieces.k(enumdirection, oceanmonumentpieces_v);
        }
    }

    private static class c implements OceanMonumentPieces.i {

        c() {}

        @Override
        public boolean fits(OceanMonumentPieces.v oceanmonumentpieces_v) {
            return oceanmonumentpieces_v.hasOpening[EnumDirection.UP.get3DDataValue()] && !oceanmonumentpieces_v.connections[EnumDirection.UP.get3DDataValue()].claimed;
        }

        @Override
        public OceanMonumentPieces.r create(EnumDirection enumdirection, OceanMonumentPieces.v oceanmonumentpieces_v, RandomSource randomsource) {
            oceanmonumentpieces_v.claimed = true;
            oceanmonumentpieces_v.connections[EnumDirection.UP.get3DDataValue()].claimed = true;
            return new OceanMonumentPieces.m(enumdirection, oceanmonumentpieces_v);
        }
    }

    private static class g implements OceanMonumentPieces.i {

        g() {}

        @Override
        public boolean fits(OceanMonumentPieces.v oceanmonumentpieces_v) {
            return !oceanmonumentpieces_v.hasOpening[EnumDirection.WEST.get3DDataValue()] && !oceanmonumentpieces_v.hasOpening[EnumDirection.EAST.get3DDataValue()] && !oceanmonumentpieces_v.hasOpening[EnumDirection.NORTH.get3DDataValue()] && !oceanmonumentpieces_v.hasOpening[EnumDirection.SOUTH.get3DDataValue()] && !oceanmonumentpieces_v.hasOpening[EnumDirection.UP.get3DDataValue()];
        }

        @Override
        public OceanMonumentPieces.r create(EnumDirection enumdirection, OceanMonumentPieces.v oceanmonumentpieces_v, RandomSource randomsource) {
            oceanmonumentpieces_v.claimed = true;
            return new OceanMonumentPieces.t(enumdirection, oceanmonumentpieces_v);
        }
    }

    private static class f implements OceanMonumentPieces.i {

        f() {}

        @Override
        public boolean fits(OceanMonumentPieces.v oceanmonumentpieces_v) {
            return true;
        }

        @Override
        public OceanMonumentPieces.r create(EnumDirection enumdirection, OceanMonumentPieces.v oceanmonumentpieces_v, RandomSource randomsource) {
            oceanmonumentpieces_v.claimed = true;
            return new OceanMonumentPieces.s(enumdirection, oceanmonumentpieces_v, randomsource);
        }
    }

    private interface i {

        boolean fits(OceanMonumentPieces.v oceanmonumentpieces_v);

        OceanMonumentPieces.r create(EnumDirection enumdirection, OceanMonumentPieces.v oceanmonumentpieces_v, RandomSource randomsource);
    }

    private static class v {

        final int index;
        final OceanMonumentPieces.v[] connections = new OceanMonumentPieces.v[6];
        final boolean[] hasOpening = new boolean[6];
        boolean claimed;
        boolean isSource;
        private int scanIndex;

        public v(int i) {
            this.index = i;
        }

        public void setConnection(EnumDirection enumdirection, OceanMonumentPieces.v oceanmonumentpieces_v) {
            this.connections[enumdirection.get3DDataValue()] = oceanmonumentpieces_v;
            oceanmonumentpieces_v.connections[enumdirection.getOpposite().get3DDataValue()] = this;
        }

        public void updateOpenings() {
            for (int i = 0; i < 6; ++i) {
                this.hasOpening[i] = this.connections[i] != null;
            }

        }

        public boolean findSource(int i) {
            if (this.isSource) {
                return true;
            } else {
                this.scanIndex = i;

                for (int j = 0; j < 6; ++j) {
                    if (this.connections[j] != null && this.hasOpening[j] && this.connections[j].scanIndex != i && this.connections[j].findSource(i)) {
                        return true;
                    }
                }

                return false;
            }
        }

        public boolean isSpecial() {
            return this.index >= 75;
        }

        public int countOpenings() {
            int i = 0;

            for (int j = 0; j < 6; ++j) {
                if (this.hasOpening[j]) {
                    ++i;
                }
            }

            return i;
        }
    }

    public static class q extends OceanMonumentPieces.r {

        public q(EnumDirection enumdirection, StructureBoundingBox structureboundingbox) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_PENTHOUSE, enumdirection, 1, structureboundingbox);
        }

        public q(NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_PENTHOUSE, nbttagcompound);
        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            this.generateBox(generatoraccessseed, structureboundingbox, 2, -1, 2, 11, -1, 11, OceanMonumentPieces.q.BASE_LIGHT, OceanMonumentPieces.q.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, -1, 0, 1, -1, 11, OceanMonumentPieces.q.BASE_GRAY, OceanMonumentPieces.q.BASE_GRAY, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 12, -1, 0, 13, -1, 11, OceanMonumentPieces.q.BASE_GRAY, OceanMonumentPieces.q.BASE_GRAY, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, -1, 0, 11, -1, 1, OceanMonumentPieces.q.BASE_GRAY, OceanMonumentPieces.q.BASE_GRAY, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, -1, 12, 11, -1, 13, OceanMonumentPieces.q.BASE_GRAY, OceanMonumentPieces.q.BASE_GRAY, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 0, 0, 0, 0, 13, OceanMonumentPieces.q.BASE_LIGHT, OceanMonumentPieces.q.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 13, 0, 0, 13, 0, 13, OceanMonumentPieces.q.BASE_LIGHT, OceanMonumentPieces.q.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 0, 0, 12, 0, 0, OceanMonumentPieces.q.BASE_LIGHT, OceanMonumentPieces.q.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 0, 13, 12, 0, 13, OceanMonumentPieces.q.BASE_LIGHT, OceanMonumentPieces.q.BASE_LIGHT, false);

            for (int i = 2; i <= 11; i += 3) {
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.q.LAMP_BLOCK, 0, 0, i, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.q.LAMP_BLOCK, 13, 0, i, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.q.LAMP_BLOCK, i, 0, 0, structureboundingbox);
            }

            this.generateBox(generatoraccessseed, structureboundingbox, 2, 0, 3, 4, 0, 9, OceanMonumentPieces.q.BASE_LIGHT, OceanMonumentPieces.q.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 9, 0, 3, 11, 0, 9, OceanMonumentPieces.q.BASE_LIGHT, OceanMonumentPieces.q.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 4, 0, 9, 9, 0, 11, OceanMonumentPieces.q.BASE_LIGHT, OceanMonumentPieces.q.BASE_LIGHT, false);
            this.placeBlock(generatoraccessseed, OceanMonumentPieces.q.BASE_LIGHT, 5, 0, 8, structureboundingbox);
            this.placeBlock(generatoraccessseed, OceanMonumentPieces.q.BASE_LIGHT, 8, 0, 8, structureboundingbox);
            this.placeBlock(generatoraccessseed, OceanMonumentPieces.q.BASE_LIGHT, 10, 0, 10, structureboundingbox);
            this.placeBlock(generatoraccessseed, OceanMonumentPieces.q.BASE_LIGHT, 3, 0, 10, structureboundingbox);
            this.generateBox(generatoraccessseed, structureboundingbox, 3, 0, 3, 3, 0, 7, OceanMonumentPieces.q.BASE_BLACK, OceanMonumentPieces.q.BASE_BLACK, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 10, 0, 3, 10, 0, 7, OceanMonumentPieces.q.BASE_BLACK, OceanMonumentPieces.q.BASE_BLACK, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 6, 0, 10, 7, 0, 10, OceanMonumentPieces.q.BASE_BLACK, OceanMonumentPieces.q.BASE_BLACK, false);
            byte b0 = 3;

            for (int j = 0; j < 2; ++j) {
                for (int k = 2; k <= 8; k += 3) {
                    this.generateBox(generatoraccessseed, structureboundingbox, b0, 0, k, b0, 2, k, OceanMonumentPieces.q.BASE_LIGHT, OceanMonumentPieces.q.BASE_LIGHT, false);
                }

                b0 = 10;
            }

            this.generateBox(generatoraccessseed, structureboundingbox, 5, 0, 10, 5, 2, 10, OceanMonumentPieces.q.BASE_LIGHT, OceanMonumentPieces.q.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 8, 0, 10, 8, 2, 10, OceanMonumentPieces.q.BASE_LIGHT, OceanMonumentPieces.q.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 6, -1, 7, 7, -1, 8, OceanMonumentPieces.q.BASE_BLACK, OceanMonumentPieces.q.BASE_BLACK, false);
            this.generateWaterBox(generatoraccessseed, structureboundingbox, 6, -1, 3, 7, -1, 4);
            this.spawnElder(generatoraccessseed, structureboundingbox, 6, 1, 6);
        }
    }

    public static class u extends OceanMonumentPieces.r {

        private int mainDesign;

        public u(EnumDirection enumdirection, StructureBoundingBox structureboundingbox, int i) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_WING_ROOM, enumdirection, 1, structureboundingbox);
            this.mainDesign = i & 1;
        }

        public u(NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_WING_ROOM, nbttagcompound);
        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            if (this.mainDesign == 0) {
                int i;

                for (i = 0; i < 4; ++i) {
                    this.generateBox(generatoraccessseed, structureboundingbox, 10 - i, 3 - i, 20 - i, 12 + i, 3 - i, 20, OceanMonumentPieces.u.BASE_LIGHT, OceanMonumentPieces.u.BASE_LIGHT, false);
                }

                this.generateBox(generatoraccessseed, structureboundingbox, 7, 0, 6, 15, 0, 16, OceanMonumentPieces.u.BASE_LIGHT, OceanMonumentPieces.u.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 6, 0, 6, 6, 3, 20, OceanMonumentPieces.u.BASE_LIGHT, OceanMonumentPieces.u.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 16, 0, 6, 16, 3, 20, OceanMonumentPieces.u.BASE_LIGHT, OceanMonumentPieces.u.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 7, 1, 7, 7, 1, 20, OceanMonumentPieces.u.BASE_LIGHT, OceanMonumentPieces.u.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 15, 1, 7, 15, 1, 20, OceanMonumentPieces.u.BASE_LIGHT, OceanMonumentPieces.u.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 7, 1, 6, 9, 3, 6, OceanMonumentPieces.u.BASE_LIGHT, OceanMonumentPieces.u.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 13, 1, 6, 15, 3, 6, OceanMonumentPieces.u.BASE_LIGHT, OceanMonumentPieces.u.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 8, 1, 7, 9, 1, 7, OceanMonumentPieces.u.BASE_LIGHT, OceanMonumentPieces.u.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 13, 1, 7, 14, 1, 7, OceanMonumentPieces.u.BASE_LIGHT, OceanMonumentPieces.u.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 9, 0, 5, 13, 0, 5, OceanMonumentPieces.u.BASE_LIGHT, OceanMonumentPieces.u.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 10, 0, 7, 12, 0, 7, OceanMonumentPieces.u.BASE_BLACK, OceanMonumentPieces.u.BASE_BLACK, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 8, 0, 10, 8, 0, 12, OceanMonumentPieces.u.BASE_BLACK, OceanMonumentPieces.u.BASE_BLACK, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 14, 0, 10, 14, 0, 12, OceanMonumentPieces.u.BASE_BLACK, OceanMonumentPieces.u.BASE_BLACK, false);

                for (i = 18; i >= 7; i -= 3) {
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.u.LAMP_BLOCK, 6, 3, i, structureboundingbox);
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.u.LAMP_BLOCK, 16, 3, i, structureboundingbox);
                }

                this.placeBlock(generatoraccessseed, OceanMonumentPieces.u.LAMP_BLOCK, 10, 0, 10, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.u.LAMP_BLOCK, 12, 0, 10, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.u.LAMP_BLOCK, 10, 0, 12, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.u.LAMP_BLOCK, 12, 0, 12, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.u.LAMP_BLOCK, 8, 3, 6, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.u.LAMP_BLOCK, 14, 3, 6, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.u.BASE_LIGHT, 4, 2, 4, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.u.LAMP_BLOCK, 4, 1, 4, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.u.BASE_LIGHT, 4, 0, 4, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.u.BASE_LIGHT, 18, 2, 4, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.u.LAMP_BLOCK, 18, 1, 4, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.u.BASE_LIGHT, 18, 0, 4, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.u.BASE_LIGHT, 4, 2, 18, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.u.LAMP_BLOCK, 4, 1, 18, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.u.BASE_LIGHT, 4, 0, 18, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.u.BASE_LIGHT, 18, 2, 18, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.u.LAMP_BLOCK, 18, 1, 18, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.u.BASE_LIGHT, 18, 0, 18, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.u.BASE_LIGHT, 9, 7, 20, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.u.BASE_LIGHT, 13, 7, 20, structureboundingbox);
                this.generateBox(generatoraccessseed, structureboundingbox, 6, 0, 21, 7, 4, 21, OceanMonumentPieces.u.BASE_LIGHT, OceanMonumentPieces.u.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 15, 0, 21, 16, 4, 21, OceanMonumentPieces.u.BASE_LIGHT, OceanMonumentPieces.u.BASE_LIGHT, false);
                this.spawnElder(generatoraccessseed, structureboundingbox, 11, 2, 16);
            } else if (this.mainDesign == 1) {
                this.generateBox(generatoraccessseed, structureboundingbox, 9, 3, 18, 13, 3, 20, OceanMonumentPieces.u.BASE_LIGHT, OceanMonumentPieces.u.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 9, 0, 18, 9, 2, 18, OceanMonumentPieces.u.BASE_LIGHT, OceanMonumentPieces.u.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 13, 0, 18, 13, 2, 18, OceanMonumentPieces.u.BASE_LIGHT, OceanMonumentPieces.u.BASE_LIGHT, false);
                byte b0 = 9;
                boolean flag = true;
                boolean flag1 = true;

                int j;

                for (j = 0; j < 2; ++j) {
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.u.BASE_LIGHT, b0, 6, 20, structureboundingbox);
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.u.LAMP_BLOCK, b0, 5, 20, structureboundingbox);
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.u.BASE_LIGHT, b0, 4, 20, structureboundingbox);
                    b0 = 13;
                }

                this.generateBox(generatoraccessseed, structureboundingbox, 7, 3, 7, 15, 3, 14, OceanMonumentPieces.u.BASE_LIGHT, OceanMonumentPieces.u.BASE_LIGHT, false);
                b0 = 10;

                for (j = 0; j < 2; ++j) {
                    this.generateBox(generatoraccessseed, structureboundingbox, b0, 0, 10, b0, 6, 10, OceanMonumentPieces.u.BASE_LIGHT, OceanMonumentPieces.u.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, b0, 0, 12, b0, 6, 12, OceanMonumentPieces.u.BASE_LIGHT, OceanMonumentPieces.u.BASE_LIGHT, false);
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.u.LAMP_BLOCK, b0, 0, 10, structureboundingbox);
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.u.LAMP_BLOCK, b0, 0, 12, structureboundingbox);
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.u.LAMP_BLOCK, b0, 4, 10, structureboundingbox);
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.u.LAMP_BLOCK, b0, 4, 12, structureboundingbox);
                    b0 = 12;
                }

                b0 = 8;

                for (j = 0; j < 2; ++j) {
                    this.generateBox(generatoraccessseed, structureboundingbox, b0, 0, 7, b0, 2, 7, OceanMonumentPieces.u.BASE_LIGHT, OceanMonumentPieces.u.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, b0, 0, 14, b0, 2, 14, OceanMonumentPieces.u.BASE_LIGHT, OceanMonumentPieces.u.BASE_LIGHT, false);
                    b0 = 14;
                }

                this.generateBox(generatoraccessseed, structureboundingbox, 8, 3, 8, 8, 3, 13, OceanMonumentPieces.u.BASE_BLACK, OceanMonumentPieces.u.BASE_BLACK, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 14, 3, 8, 14, 3, 13, OceanMonumentPieces.u.BASE_BLACK, OceanMonumentPieces.u.BASE_BLACK, false);
                this.spawnElder(generatoraccessseed, structureboundingbox, 11, 5, 13);
            }

        }
    }

    public static class j extends OceanMonumentPieces.r {

        public j(EnumDirection enumdirection, OceanMonumentPieces.v oceanmonumentpieces_v) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_CORE_ROOM, 1, enumdirection, oceanmonumentpieces_v, 2, 2, 2);
        }

        public j(NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_CORE_ROOM, nbttagcompound);
        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            this.generateBoxOnFillOnly(generatoraccessseed, structureboundingbox, 1, 8, 0, 14, 8, 14, OceanMonumentPieces.j.BASE_GRAY);
            boolean flag = true;
            IBlockData iblockdata = OceanMonumentPieces.j.BASE_LIGHT;

            this.generateBox(generatoraccessseed, structureboundingbox, 0, 7, 0, 0, 7, 15, iblockdata, iblockdata, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 15, 7, 0, 15, 7, 15, iblockdata, iblockdata, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 7, 0, 15, 7, 0, iblockdata, iblockdata, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 7, 15, 14, 7, 15, iblockdata, iblockdata, false);

            int i;

            for (i = 1; i <= 6; ++i) {
                iblockdata = OceanMonumentPieces.j.BASE_LIGHT;
                if (i == 2 || i == 6) {
                    iblockdata = OceanMonumentPieces.j.BASE_GRAY;
                }

                for (int j = 0; j <= 15; j += 15) {
                    this.generateBox(generatoraccessseed, structureboundingbox, j, i, 0, j, i, 1, iblockdata, iblockdata, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, j, i, 6, j, i, 9, iblockdata, iblockdata, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, j, i, 14, j, i, 15, iblockdata, iblockdata, false);
                }

                this.generateBox(generatoraccessseed, structureboundingbox, 1, i, 0, 1, i, 0, iblockdata, iblockdata, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 6, i, 0, 9, i, 0, iblockdata, iblockdata, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 14, i, 0, 14, i, 0, iblockdata, iblockdata, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 1, i, 15, 14, i, 15, iblockdata, iblockdata, false);
            }

            this.generateBox(generatoraccessseed, structureboundingbox, 6, 3, 6, 9, 6, 9, OceanMonumentPieces.j.BASE_BLACK, OceanMonumentPieces.j.BASE_BLACK, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 7, 4, 7, 8, 5, 8, Blocks.GOLD_BLOCK.defaultBlockState(), Blocks.GOLD_BLOCK.defaultBlockState(), false);

            for (i = 3; i <= 6; i += 3) {
                for (int k = 6; k <= 9; k += 3) {
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.j.LAMP_BLOCK, k, i, 6, structureboundingbox);
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.j.LAMP_BLOCK, k, i, 9, structureboundingbox);
                }
            }

            this.generateBox(generatoraccessseed, structureboundingbox, 5, 1, 6, 5, 2, 6, OceanMonumentPieces.j.BASE_LIGHT, OceanMonumentPieces.j.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 1, 9, 5, 2, 9, OceanMonumentPieces.j.BASE_LIGHT, OceanMonumentPieces.j.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 10, 1, 6, 10, 2, 6, OceanMonumentPieces.j.BASE_LIGHT, OceanMonumentPieces.j.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 10, 1, 9, 10, 2, 9, OceanMonumentPieces.j.BASE_LIGHT, OceanMonumentPieces.j.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 6, 1, 5, 6, 2, 5, OceanMonumentPieces.j.BASE_LIGHT, OceanMonumentPieces.j.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 9, 1, 5, 9, 2, 5, OceanMonumentPieces.j.BASE_LIGHT, OceanMonumentPieces.j.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 6, 1, 10, 6, 2, 10, OceanMonumentPieces.j.BASE_LIGHT, OceanMonumentPieces.j.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 9, 1, 10, 9, 2, 10, OceanMonumentPieces.j.BASE_LIGHT, OceanMonumentPieces.j.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 2, 5, 5, 6, 5, OceanMonumentPieces.j.BASE_LIGHT, OceanMonumentPieces.j.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 2, 10, 5, 6, 10, OceanMonumentPieces.j.BASE_LIGHT, OceanMonumentPieces.j.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 10, 2, 5, 10, 6, 5, OceanMonumentPieces.j.BASE_LIGHT, OceanMonumentPieces.j.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 10, 2, 10, 10, 6, 10, OceanMonumentPieces.j.BASE_LIGHT, OceanMonumentPieces.j.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 7, 1, 5, 7, 6, OceanMonumentPieces.j.BASE_LIGHT, OceanMonumentPieces.j.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 10, 7, 1, 10, 7, 6, OceanMonumentPieces.j.BASE_LIGHT, OceanMonumentPieces.j.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 7, 9, 5, 7, 14, OceanMonumentPieces.j.BASE_LIGHT, OceanMonumentPieces.j.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 10, 7, 9, 10, 7, 14, OceanMonumentPieces.j.BASE_LIGHT, OceanMonumentPieces.j.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 7, 5, 6, 7, 5, OceanMonumentPieces.j.BASE_LIGHT, OceanMonumentPieces.j.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 7, 10, 6, 7, 10, OceanMonumentPieces.j.BASE_LIGHT, OceanMonumentPieces.j.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 9, 7, 5, 14, 7, 5, OceanMonumentPieces.j.BASE_LIGHT, OceanMonumentPieces.j.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 9, 7, 10, 14, 7, 10, OceanMonumentPieces.j.BASE_LIGHT, OceanMonumentPieces.j.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 1, 2, 2, 1, 3, OceanMonumentPieces.j.BASE_LIGHT, OceanMonumentPieces.j.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 3, 1, 2, 3, 1, 2, OceanMonumentPieces.j.BASE_LIGHT, OceanMonumentPieces.j.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 13, 1, 2, 13, 1, 3, OceanMonumentPieces.j.BASE_LIGHT, OceanMonumentPieces.j.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 12, 1, 2, 12, 1, 2, OceanMonumentPieces.j.BASE_LIGHT, OceanMonumentPieces.j.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 1, 12, 2, 1, 13, OceanMonumentPieces.j.BASE_LIGHT, OceanMonumentPieces.j.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 3, 1, 13, 3, 1, 13, OceanMonumentPieces.j.BASE_LIGHT, OceanMonumentPieces.j.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 13, 1, 12, 13, 1, 13, OceanMonumentPieces.j.BASE_LIGHT, OceanMonumentPieces.j.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 12, 1, 13, 12, 1, 13, OceanMonumentPieces.j.BASE_LIGHT, OceanMonumentPieces.j.BASE_LIGHT, false);
        }
    }

    public static class n extends OceanMonumentPieces.r {

        public n(EnumDirection enumdirection, OceanMonumentPieces.v oceanmonumentpieces_v) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_DOUBLE_YZ_ROOM, 1, enumdirection, oceanmonumentpieces_v, 1, 2, 2);
        }

        public n(NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_DOUBLE_YZ_ROOM, nbttagcompound);
        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            OceanMonumentPieces.v oceanmonumentpieces_v = this.roomDefinition.connections[EnumDirection.NORTH.get3DDataValue()];
            OceanMonumentPieces.v oceanmonumentpieces_v1 = this.roomDefinition;
            OceanMonumentPieces.v oceanmonumentpieces_v2 = oceanmonumentpieces_v.connections[EnumDirection.UP.get3DDataValue()];
            OceanMonumentPieces.v oceanmonumentpieces_v3 = oceanmonumentpieces_v1.connections[EnumDirection.UP.get3DDataValue()];

            if (this.roomDefinition.index / 25 > 0) {
                this.generateDefaultFloor(generatoraccessseed, structureboundingbox, 0, 8, oceanmonumentpieces_v.hasOpening[EnumDirection.DOWN.get3DDataValue()]);
                this.generateDefaultFloor(generatoraccessseed, structureboundingbox, 0, 0, oceanmonumentpieces_v1.hasOpening[EnumDirection.DOWN.get3DDataValue()]);
            }

            if (oceanmonumentpieces_v3.connections[EnumDirection.UP.get3DDataValue()] == null) {
                this.generateBoxOnFillOnly(generatoraccessseed, structureboundingbox, 1, 8, 1, 6, 8, 7, OceanMonumentPieces.n.BASE_GRAY);
            }

            if (oceanmonumentpieces_v2.connections[EnumDirection.UP.get3DDataValue()] == null) {
                this.generateBoxOnFillOnly(generatoraccessseed, structureboundingbox, 1, 8, 8, 6, 8, 14, OceanMonumentPieces.n.BASE_GRAY);
            }

            IBlockData iblockdata;
            int i;

            for (i = 1; i <= 7; ++i) {
                iblockdata = OceanMonumentPieces.n.BASE_LIGHT;
                if (i == 2 || i == 6) {
                    iblockdata = OceanMonumentPieces.n.BASE_GRAY;
                }

                this.generateBox(generatoraccessseed, structureboundingbox, 0, i, 0, 0, i, 15, iblockdata, iblockdata, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 7, i, 0, 7, i, 15, iblockdata, iblockdata, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 1, i, 0, 6, i, 0, iblockdata, iblockdata, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 1, i, 15, 6, i, 15, iblockdata, iblockdata, false);
            }

            for (i = 1; i <= 7; ++i) {
                iblockdata = OceanMonumentPieces.n.BASE_BLACK;
                if (i == 2 || i == 6) {
                    iblockdata = OceanMonumentPieces.n.LAMP_BLOCK;
                }

                this.generateBox(generatoraccessseed, structureboundingbox, 3, i, 7, 4, i, 8, iblockdata, iblockdata, false);
            }

            if (oceanmonumentpieces_v1.hasOpening[EnumDirection.SOUTH.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 3, 1, 0, 4, 2, 0);
            }

            if (oceanmonumentpieces_v1.hasOpening[EnumDirection.EAST.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 7, 1, 3, 7, 2, 4);
            }

            if (oceanmonumentpieces_v1.hasOpening[EnumDirection.WEST.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 0, 1, 3, 0, 2, 4);
            }

            if (oceanmonumentpieces_v.hasOpening[EnumDirection.NORTH.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 3, 1, 15, 4, 2, 15);
            }

            if (oceanmonumentpieces_v.hasOpening[EnumDirection.WEST.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 0, 1, 11, 0, 2, 12);
            }

            if (oceanmonumentpieces_v.hasOpening[EnumDirection.EAST.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 7, 1, 11, 7, 2, 12);
            }

            if (oceanmonumentpieces_v3.hasOpening[EnumDirection.SOUTH.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 3, 5, 0, 4, 6, 0);
            }

            if (oceanmonumentpieces_v3.hasOpening[EnumDirection.EAST.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 7, 5, 3, 7, 6, 4);
                this.generateBox(generatoraccessseed, structureboundingbox, 5, 4, 2, 6, 4, 5, OceanMonumentPieces.n.BASE_LIGHT, OceanMonumentPieces.n.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 6, 1, 2, 6, 3, 2, OceanMonumentPieces.n.BASE_LIGHT, OceanMonumentPieces.n.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 6, 1, 5, 6, 3, 5, OceanMonumentPieces.n.BASE_LIGHT, OceanMonumentPieces.n.BASE_LIGHT, false);
            }

            if (oceanmonumentpieces_v3.hasOpening[EnumDirection.WEST.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 0, 5, 3, 0, 6, 4);
                this.generateBox(generatoraccessseed, structureboundingbox, 1, 4, 2, 2, 4, 5, OceanMonumentPieces.n.BASE_LIGHT, OceanMonumentPieces.n.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 1, 1, 2, 1, 3, 2, OceanMonumentPieces.n.BASE_LIGHT, OceanMonumentPieces.n.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 1, 1, 5, 1, 3, 5, OceanMonumentPieces.n.BASE_LIGHT, OceanMonumentPieces.n.BASE_LIGHT, false);
            }

            if (oceanmonumentpieces_v2.hasOpening[EnumDirection.NORTH.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 3, 5, 15, 4, 6, 15);
            }

            if (oceanmonumentpieces_v2.hasOpening[EnumDirection.WEST.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 0, 5, 11, 0, 6, 12);
                this.generateBox(generatoraccessseed, structureboundingbox, 1, 4, 10, 2, 4, 13, OceanMonumentPieces.n.BASE_LIGHT, OceanMonumentPieces.n.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 1, 1, 10, 1, 3, 10, OceanMonumentPieces.n.BASE_LIGHT, OceanMonumentPieces.n.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 1, 1, 13, 1, 3, 13, OceanMonumentPieces.n.BASE_LIGHT, OceanMonumentPieces.n.BASE_LIGHT, false);
            }

            if (oceanmonumentpieces_v2.hasOpening[EnumDirection.EAST.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 7, 5, 11, 7, 6, 12);
                this.generateBox(generatoraccessseed, structureboundingbox, 5, 4, 10, 6, 4, 13, OceanMonumentPieces.n.BASE_LIGHT, OceanMonumentPieces.n.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 6, 1, 10, 6, 3, 10, OceanMonumentPieces.n.BASE_LIGHT, OceanMonumentPieces.n.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 6, 1, 13, 6, 3, 13, OceanMonumentPieces.n.BASE_LIGHT, OceanMonumentPieces.n.BASE_LIGHT, false);
            }

        }
    }

    public static class l extends OceanMonumentPieces.r {

        public l(EnumDirection enumdirection, OceanMonumentPieces.v oceanmonumentpieces_v) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_DOUBLE_XY_ROOM, 1, enumdirection, oceanmonumentpieces_v, 2, 2, 1);
        }

        public l(NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_DOUBLE_XY_ROOM, nbttagcompound);
        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            OceanMonumentPieces.v oceanmonumentpieces_v = this.roomDefinition.connections[EnumDirection.EAST.get3DDataValue()];
            OceanMonumentPieces.v oceanmonumentpieces_v1 = this.roomDefinition;
            OceanMonumentPieces.v oceanmonumentpieces_v2 = oceanmonumentpieces_v1.connections[EnumDirection.UP.get3DDataValue()];
            OceanMonumentPieces.v oceanmonumentpieces_v3 = oceanmonumentpieces_v.connections[EnumDirection.UP.get3DDataValue()];

            if (this.roomDefinition.index / 25 > 0) {
                this.generateDefaultFloor(generatoraccessseed, structureboundingbox, 8, 0, oceanmonumentpieces_v.hasOpening[EnumDirection.DOWN.get3DDataValue()]);
                this.generateDefaultFloor(generatoraccessseed, structureboundingbox, 0, 0, oceanmonumentpieces_v1.hasOpening[EnumDirection.DOWN.get3DDataValue()]);
            }

            if (oceanmonumentpieces_v2.connections[EnumDirection.UP.get3DDataValue()] == null) {
                this.generateBoxOnFillOnly(generatoraccessseed, structureboundingbox, 1, 8, 1, 7, 8, 6, OceanMonumentPieces.l.BASE_GRAY);
            }

            if (oceanmonumentpieces_v3.connections[EnumDirection.UP.get3DDataValue()] == null) {
                this.generateBoxOnFillOnly(generatoraccessseed, structureboundingbox, 8, 8, 1, 14, 8, 6, OceanMonumentPieces.l.BASE_GRAY);
            }

            for (int i = 1; i <= 7; ++i) {
                IBlockData iblockdata = OceanMonumentPieces.l.BASE_LIGHT;

                if (i == 2 || i == 6) {
                    iblockdata = OceanMonumentPieces.l.BASE_GRAY;
                }

                this.generateBox(generatoraccessseed, structureboundingbox, 0, i, 0, 0, i, 7, iblockdata, iblockdata, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 15, i, 0, 15, i, 7, iblockdata, iblockdata, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 1, i, 0, 15, i, 0, iblockdata, iblockdata, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 1, i, 7, 14, i, 7, iblockdata, iblockdata, false);
            }

            this.generateBox(generatoraccessseed, structureboundingbox, 2, 1, 3, 2, 7, 4, OceanMonumentPieces.l.BASE_LIGHT, OceanMonumentPieces.l.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 3, 1, 2, 4, 7, 2, OceanMonumentPieces.l.BASE_LIGHT, OceanMonumentPieces.l.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 3, 1, 5, 4, 7, 5, OceanMonumentPieces.l.BASE_LIGHT, OceanMonumentPieces.l.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 13, 1, 3, 13, 7, 4, OceanMonumentPieces.l.BASE_LIGHT, OceanMonumentPieces.l.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 11, 1, 2, 12, 7, 2, OceanMonumentPieces.l.BASE_LIGHT, OceanMonumentPieces.l.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 11, 1, 5, 12, 7, 5, OceanMonumentPieces.l.BASE_LIGHT, OceanMonumentPieces.l.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 1, 3, 5, 3, 4, OceanMonumentPieces.l.BASE_LIGHT, OceanMonumentPieces.l.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 10, 1, 3, 10, 3, 4, OceanMonumentPieces.l.BASE_LIGHT, OceanMonumentPieces.l.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 7, 2, 10, 7, 5, OceanMonumentPieces.l.BASE_LIGHT, OceanMonumentPieces.l.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 5, 2, 5, 7, 2, OceanMonumentPieces.l.BASE_LIGHT, OceanMonumentPieces.l.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 10, 5, 2, 10, 7, 2, OceanMonumentPieces.l.BASE_LIGHT, OceanMonumentPieces.l.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 5, 5, 5, 7, 5, OceanMonumentPieces.l.BASE_LIGHT, OceanMonumentPieces.l.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 10, 5, 5, 10, 7, 5, OceanMonumentPieces.l.BASE_LIGHT, OceanMonumentPieces.l.BASE_LIGHT, false);
            this.placeBlock(generatoraccessseed, OceanMonumentPieces.l.BASE_LIGHT, 6, 6, 2, structureboundingbox);
            this.placeBlock(generatoraccessseed, OceanMonumentPieces.l.BASE_LIGHT, 9, 6, 2, structureboundingbox);
            this.placeBlock(generatoraccessseed, OceanMonumentPieces.l.BASE_LIGHT, 6, 6, 5, structureboundingbox);
            this.placeBlock(generatoraccessseed, OceanMonumentPieces.l.BASE_LIGHT, 9, 6, 5, structureboundingbox);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 4, 3, 6, 4, 4, OceanMonumentPieces.l.BASE_LIGHT, OceanMonumentPieces.l.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 9, 4, 3, 10, 4, 4, OceanMonumentPieces.l.BASE_LIGHT, OceanMonumentPieces.l.BASE_LIGHT, false);
            this.placeBlock(generatoraccessseed, OceanMonumentPieces.l.LAMP_BLOCK, 5, 4, 2, structureboundingbox);
            this.placeBlock(generatoraccessseed, OceanMonumentPieces.l.LAMP_BLOCK, 5, 4, 5, structureboundingbox);
            this.placeBlock(generatoraccessseed, OceanMonumentPieces.l.LAMP_BLOCK, 10, 4, 2, structureboundingbox);
            this.placeBlock(generatoraccessseed, OceanMonumentPieces.l.LAMP_BLOCK, 10, 4, 5, structureboundingbox);
            if (oceanmonumentpieces_v1.hasOpening[EnumDirection.SOUTH.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 3, 1, 0, 4, 2, 0);
            }

            if (oceanmonumentpieces_v1.hasOpening[EnumDirection.NORTH.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 3, 1, 7, 4, 2, 7);
            }

            if (oceanmonumentpieces_v1.hasOpening[EnumDirection.WEST.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 0, 1, 3, 0, 2, 4);
            }

            if (oceanmonumentpieces_v.hasOpening[EnumDirection.SOUTH.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 11, 1, 0, 12, 2, 0);
            }

            if (oceanmonumentpieces_v.hasOpening[EnumDirection.NORTH.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 11, 1, 7, 12, 2, 7);
            }

            if (oceanmonumentpieces_v.hasOpening[EnumDirection.EAST.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 15, 1, 3, 15, 2, 4);
            }

            if (oceanmonumentpieces_v2.hasOpening[EnumDirection.SOUTH.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 3, 5, 0, 4, 6, 0);
            }

            if (oceanmonumentpieces_v2.hasOpening[EnumDirection.NORTH.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 3, 5, 7, 4, 6, 7);
            }

            if (oceanmonumentpieces_v2.hasOpening[EnumDirection.WEST.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 0, 5, 3, 0, 6, 4);
            }

            if (oceanmonumentpieces_v3.hasOpening[EnumDirection.SOUTH.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 11, 5, 0, 12, 6, 0);
            }

            if (oceanmonumentpieces_v3.hasOpening[EnumDirection.NORTH.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 11, 5, 7, 12, 6, 7);
            }

            if (oceanmonumentpieces_v3.hasOpening[EnumDirection.EAST.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 15, 5, 3, 15, 6, 4);
            }

        }
    }

    public static class o extends OceanMonumentPieces.r {

        public o(EnumDirection enumdirection, OceanMonumentPieces.v oceanmonumentpieces_v) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_DOUBLE_Z_ROOM, 1, enumdirection, oceanmonumentpieces_v, 1, 1, 2);
        }

        public o(NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_DOUBLE_Z_ROOM, nbttagcompound);
        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            OceanMonumentPieces.v oceanmonumentpieces_v = this.roomDefinition.connections[EnumDirection.NORTH.get3DDataValue()];
            OceanMonumentPieces.v oceanmonumentpieces_v1 = this.roomDefinition;

            if (this.roomDefinition.index / 25 > 0) {
                this.generateDefaultFloor(generatoraccessseed, structureboundingbox, 0, 8, oceanmonumentpieces_v.hasOpening[EnumDirection.DOWN.get3DDataValue()]);
                this.generateDefaultFloor(generatoraccessseed, structureboundingbox, 0, 0, oceanmonumentpieces_v1.hasOpening[EnumDirection.DOWN.get3DDataValue()]);
            }

            if (oceanmonumentpieces_v1.connections[EnumDirection.UP.get3DDataValue()] == null) {
                this.generateBoxOnFillOnly(generatoraccessseed, structureboundingbox, 1, 4, 1, 6, 4, 7, OceanMonumentPieces.o.BASE_GRAY);
            }

            if (oceanmonumentpieces_v.connections[EnumDirection.UP.get3DDataValue()] == null) {
                this.generateBoxOnFillOnly(generatoraccessseed, structureboundingbox, 1, 4, 8, 6, 4, 14, OceanMonumentPieces.o.BASE_GRAY);
            }

            this.generateBox(generatoraccessseed, structureboundingbox, 0, 3, 0, 0, 3, 15, OceanMonumentPieces.o.BASE_LIGHT, OceanMonumentPieces.o.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 7, 3, 0, 7, 3, 15, OceanMonumentPieces.o.BASE_LIGHT, OceanMonumentPieces.o.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 3, 0, 7, 3, 0, OceanMonumentPieces.o.BASE_LIGHT, OceanMonumentPieces.o.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 3, 15, 6, 3, 15, OceanMonumentPieces.o.BASE_LIGHT, OceanMonumentPieces.o.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 0, 0, 2, 15, OceanMonumentPieces.o.BASE_GRAY, OceanMonumentPieces.o.BASE_GRAY, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 7, 2, 0, 7, 2, 15, OceanMonumentPieces.o.BASE_GRAY, OceanMonumentPieces.o.BASE_GRAY, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 2, 0, 7, 2, 0, OceanMonumentPieces.o.BASE_GRAY, OceanMonumentPieces.o.BASE_GRAY, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 2, 15, 6, 2, 15, OceanMonumentPieces.o.BASE_GRAY, OceanMonumentPieces.o.BASE_GRAY, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 1, 0, 0, 1, 15, OceanMonumentPieces.o.BASE_LIGHT, OceanMonumentPieces.o.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 7, 1, 0, 7, 1, 15, OceanMonumentPieces.o.BASE_LIGHT, OceanMonumentPieces.o.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 1, 0, 7, 1, 0, OceanMonumentPieces.o.BASE_LIGHT, OceanMonumentPieces.o.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 1, 15, 6, 1, 15, OceanMonumentPieces.o.BASE_LIGHT, OceanMonumentPieces.o.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 1, 1, 1, 1, 2, OceanMonumentPieces.o.BASE_LIGHT, OceanMonumentPieces.o.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 6, 1, 1, 6, 1, 2, OceanMonumentPieces.o.BASE_LIGHT, OceanMonumentPieces.o.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 3, 1, 1, 3, 2, OceanMonumentPieces.o.BASE_LIGHT, OceanMonumentPieces.o.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 6, 3, 1, 6, 3, 2, OceanMonumentPieces.o.BASE_LIGHT, OceanMonumentPieces.o.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 1, 13, 1, 1, 14, OceanMonumentPieces.o.BASE_LIGHT, OceanMonumentPieces.o.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 6, 1, 13, 6, 1, 14, OceanMonumentPieces.o.BASE_LIGHT, OceanMonumentPieces.o.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 3, 13, 1, 3, 14, OceanMonumentPieces.o.BASE_LIGHT, OceanMonumentPieces.o.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 6, 3, 13, 6, 3, 14, OceanMonumentPieces.o.BASE_LIGHT, OceanMonumentPieces.o.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 1, 6, 2, 3, 6, OceanMonumentPieces.o.BASE_LIGHT, OceanMonumentPieces.o.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 1, 6, 5, 3, 6, OceanMonumentPieces.o.BASE_LIGHT, OceanMonumentPieces.o.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 1, 9, 2, 3, 9, OceanMonumentPieces.o.BASE_LIGHT, OceanMonumentPieces.o.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 1, 9, 5, 3, 9, OceanMonumentPieces.o.BASE_LIGHT, OceanMonumentPieces.o.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 3, 2, 6, 4, 2, 6, OceanMonumentPieces.o.BASE_LIGHT, OceanMonumentPieces.o.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 3, 2, 9, 4, 2, 9, OceanMonumentPieces.o.BASE_LIGHT, OceanMonumentPieces.o.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 2, 7, 2, 2, 8, OceanMonumentPieces.o.BASE_LIGHT, OceanMonumentPieces.o.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 2, 7, 5, 2, 8, OceanMonumentPieces.o.BASE_LIGHT, OceanMonumentPieces.o.BASE_LIGHT, false);
            this.placeBlock(generatoraccessseed, OceanMonumentPieces.o.LAMP_BLOCK, 2, 2, 5, structureboundingbox);
            this.placeBlock(generatoraccessseed, OceanMonumentPieces.o.LAMP_BLOCK, 5, 2, 5, structureboundingbox);
            this.placeBlock(generatoraccessseed, OceanMonumentPieces.o.LAMP_BLOCK, 2, 2, 10, structureboundingbox);
            this.placeBlock(generatoraccessseed, OceanMonumentPieces.o.LAMP_BLOCK, 5, 2, 10, structureboundingbox);
            this.placeBlock(generatoraccessseed, OceanMonumentPieces.o.BASE_LIGHT, 2, 3, 5, structureboundingbox);
            this.placeBlock(generatoraccessseed, OceanMonumentPieces.o.BASE_LIGHT, 5, 3, 5, structureboundingbox);
            this.placeBlock(generatoraccessseed, OceanMonumentPieces.o.BASE_LIGHT, 2, 3, 10, structureboundingbox);
            this.placeBlock(generatoraccessseed, OceanMonumentPieces.o.BASE_LIGHT, 5, 3, 10, structureboundingbox);
            if (oceanmonumentpieces_v1.hasOpening[EnumDirection.SOUTH.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 3, 1, 0, 4, 2, 0);
            }

            if (oceanmonumentpieces_v1.hasOpening[EnumDirection.EAST.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 7, 1, 3, 7, 2, 4);
            }

            if (oceanmonumentpieces_v1.hasOpening[EnumDirection.WEST.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 0, 1, 3, 0, 2, 4);
            }

            if (oceanmonumentpieces_v.hasOpening[EnumDirection.NORTH.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 3, 1, 15, 4, 2, 15);
            }

            if (oceanmonumentpieces_v.hasOpening[EnumDirection.WEST.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 0, 1, 11, 0, 2, 12);
            }

            if (oceanmonumentpieces_v.hasOpening[EnumDirection.EAST.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 7, 1, 11, 7, 2, 12);
            }

        }
    }

    public static class k extends OceanMonumentPieces.r {

        public k(EnumDirection enumdirection, OceanMonumentPieces.v oceanmonumentpieces_v) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_DOUBLE_X_ROOM, 1, enumdirection, oceanmonumentpieces_v, 2, 1, 1);
        }

        public k(NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_DOUBLE_X_ROOM, nbttagcompound);
        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            OceanMonumentPieces.v oceanmonumentpieces_v = this.roomDefinition.connections[EnumDirection.EAST.get3DDataValue()];
            OceanMonumentPieces.v oceanmonumentpieces_v1 = this.roomDefinition;

            if (this.roomDefinition.index / 25 > 0) {
                this.generateDefaultFloor(generatoraccessseed, structureboundingbox, 8, 0, oceanmonumentpieces_v.hasOpening[EnumDirection.DOWN.get3DDataValue()]);
                this.generateDefaultFloor(generatoraccessseed, structureboundingbox, 0, 0, oceanmonumentpieces_v1.hasOpening[EnumDirection.DOWN.get3DDataValue()]);
            }

            if (oceanmonumentpieces_v1.connections[EnumDirection.UP.get3DDataValue()] == null) {
                this.generateBoxOnFillOnly(generatoraccessseed, structureboundingbox, 1, 4, 1, 7, 4, 6, OceanMonumentPieces.k.BASE_GRAY);
            }

            if (oceanmonumentpieces_v.connections[EnumDirection.UP.get3DDataValue()] == null) {
                this.generateBoxOnFillOnly(generatoraccessseed, structureboundingbox, 8, 4, 1, 14, 4, 6, OceanMonumentPieces.k.BASE_GRAY);
            }

            this.generateBox(generatoraccessseed, structureboundingbox, 0, 3, 0, 0, 3, 7, OceanMonumentPieces.k.BASE_LIGHT, OceanMonumentPieces.k.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 15, 3, 0, 15, 3, 7, OceanMonumentPieces.k.BASE_LIGHT, OceanMonumentPieces.k.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 3, 0, 15, 3, 0, OceanMonumentPieces.k.BASE_LIGHT, OceanMonumentPieces.k.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 3, 7, 14, 3, 7, OceanMonumentPieces.k.BASE_LIGHT, OceanMonumentPieces.k.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 0, 0, 2, 7, OceanMonumentPieces.k.BASE_GRAY, OceanMonumentPieces.k.BASE_GRAY, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 15, 2, 0, 15, 2, 7, OceanMonumentPieces.k.BASE_GRAY, OceanMonumentPieces.k.BASE_GRAY, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 2, 0, 15, 2, 0, OceanMonumentPieces.k.BASE_GRAY, OceanMonumentPieces.k.BASE_GRAY, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 2, 7, 14, 2, 7, OceanMonumentPieces.k.BASE_GRAY, OceanMonumentPieces.k.BASE_GRAY, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 1, 0, 0, 1, 7, OceanMonumentPieces.k.BASE_LIGHT, OceanMonumentPieces.k.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 15, 1, 0, 15, 1, 7, OceanMonumentPieces.k.BASE_LIGHT, OceanMonumentPieces.k.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 1, 0, 15, 1, 0, OceanMonumentPieces.k.BASE_LIGHT, OceanMonumentPieces.k.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 1, 7, 14, 1, 7, OceanMonumentPieces.k.BASE_LIGHT, OceanMonumentPieces.k.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 1, 0, 10, 1, 4, OceanMonumentPieces.k.BASE_LIGHT, OceanMonumentPieces.k.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 6, 2, 0, 9, 2, 3, OceanMonumentPieces.k.BASE_GRAY, OceanMonumentPieces.k.BASE_GRAY, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 3, 0, 10, 3, 4, OceanMonumentPieces.k.BASE_LIGHT, OceanMonumentPieces.k.BASE_LIGHT, false);
            this.placeBlock(generatoraccessseed, OceanMonumentPieces.k.LAMP_BLOCK, 6, 2, 3, structureboundingbox);
            this.placeBlock(generatoraccessseed, OceanMonumentPieces.k.LAMP_BLOCK, 9, 2, 3, structureboundingbox);
            if (oceanmonumentpieces_v1.hasOpening[EnumDirection.SOUTH.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 3, 1, 0, 4, 2, 0);
            }

            if (oceanmonumentpieces_v1.hasOpening[EnumDirection.NORTH.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 3, 1, 7, 4, 2, 7);
            }

            if (oceanmonumentpieces_v1.hasOpening[EnumDirection.WEST.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 0, 1, 3, 0, 2, 4);
            }

            if (oceanmonumentpieces_v.hasOpening[EnumDirection.SOUTH.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 11, 1, 0, 12, 2, 0);
            }

            if (oceanmonumentpieces_v.hasOpening[EnumDirection.NORTH.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 11, 1, 7, 12, 2, 7);
            }

            if (oceanmonumentpieces_v.hasOpening[EnumDirection.EAST.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 15, 1, 3, 15, 2, 4);
            }

        }
    }

    public static class m extends OceanMonumentPieces.r {

        public m(EnumDirection enumdirection, OceanMonumentPieces.v oceanmonumentpieces_v) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_ROOM, 1, enumdirection, oceanmonumentpieces_v, 1, 2, 1);
        }

        public m(NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_ROOM, nbttagcompound);
        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            if (this.roomDefinition.index / 25 > 0) {
                this.generateDefaultFloor(generatoraccessseed, structureboundingbox, 0, 0, this.roomDefinition.hasOpening[EnumDirection.DOWN.get3DDataValue()]);
            }

            OceanMonumentPieces.v oceanmonumentpieces_v = this.roomDefinition.connections[EnumDirection.UP.get3DDataValue()];

            if (oceanmonumentpieces_v.connections[EnumDirection.UP.get3DDataValue()] == null) {
                this.generateBoxOnFillOnly(generatoraccessseed, structureboundingbox, 1, 8, 1, 6, 8, 6, OceanMonumentPieces.m.BASE_GRAY);
            }

            this.generateBox(generatoraccessseed, structureboundingbox, 0, 4, 0, 0, 4, 7, OceanMonumentPieces.m.BASE_LIGHT, OceanMonumentPieces.m.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 7, 4, 0, 7, 4, 7, OceanMonumentPieces.m.BASE_LIGHT, OceanMonumentPieces.m.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 4, 0, 6, 4, 0, OceanMonumentPieces.m.BASE_LIGHT, OceanMonumentPieces.m.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 4, 7, 6, 4, 7, OceanMonumentPieces.m.BASE_LIGHT, OceanMonumentPieces.m.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 4, 1, 2, 4, 2, OceanMonumentPieces.m.BASE_LIGHT, OceanMonumentPieces.m.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 4, 2, 1, 4, 2, OceanMonumentPieces.m.BASE_LIGHT, OceanMonumentPieces.m.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 4, 1, 5, 4, 2, OceanMonumentPieces.m.BASE_LIGHT, OceanMonumentPieces.m.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 6, 4, 2, 6, 4, 2, OceanMonumentPieces.m.BASE_LIGHT, OceanMonumentPieces.m.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 4, 5, 2, 4, 6, OceanMonumentPieces.m.BASE_LIGHT, OceanMonumentPieces.m.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 4, 5, 1, 4, 5, OceanMonumentPieces.m.BASE_LIGHT, OceanMonumentPieces.m.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 4, 5, 5, 4, 6, OceanMonumentPieces.m.BASE_LIGHT, OceanMonumentPieces.m.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 6, 4, 5, 6, 4, 5, OceanMonumentPieces.m.BASE_LIGHT, OceanMonumentPieces.m.BASE_LIGHT, false);
            OceanMonumentPieces.v oceanmonumentpieces_v1 = this.roomDefinition;

            for (int i = 1; i <= 5; i += 4) {
                byte b0 = 0;

                if (oceanmonumentpieces_v1.hasOpening[EnumDirection.SOUTH.get3DDataValue()]) {
                    this.generateBox(generatoraccessseed, structureboundingbox, 2, i, b0, 2, i + 2, b0, OceanMonumentPieces.m.BASE_LIGHT, OceanMonumentPieces.m.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, 5, i, b0, 5, i + 2, b0, OceanMonumentPieces.m.BASE_LIGHT, OceanMonumentPieces.m.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, 3, i + 2, b0, 4, i + 2, b0, OceanMonumentPieces.m.BASE_LIGHT, OceanMonumentPieces.m.BASE_LIGHT, false);
                } else {
                    this.generateBox(generatoraccessseed, structureboundingbox, 0, i, b0, 7, i + 2, b0, OceanMonumentPieces.m.BASE_LIGHT, OceanMonumentPieces.m.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, 0, i + 1, b0, 7, i + 1, b0, OceanMonumentPieces.m.BASE_GRAY, OceanMonumentPieces.m.BASE_GRAY, false);
                }

                b0 = 7;
                if (oceanmonumentpieces_v1.hasOpening[EnumDirection.NORTH.get3DDataValue()]) {
                    this.generateBox(generatoraccessseed, structureboundingbox, 2, i, b0, 2, i + 2, b0, OceanMonumentPieces.m.BASE_LIGHT, OceanMonumentPieces.m.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, 5, i, b0, 5, i + 2, b0, OceanMonumentPieces.m.BASE_LIGHT, OceanMonumentPieces.m.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, 3, i + 2, b0, 4, i + 2, b0, OceanMonumentPieces.m.BASE_LIGHT, OceanMonumentPieces.m.BASE_LIGHT, false);
                } else {
                    this.generateBox(generatoraccessseed, structureboundingbox, 0, i, b0, 7, i + 2, b0, OceanMonumentPieces.m.BASE_LIGHT, OceanMonumentPieces.m.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, 0, i + 1, b0, 7, i + 1, b0, OceanMonumentPieces.m.BASE_GRAY, OceanMonumentPieces.m.BASE_GRAY, false);
                }

                byte b1 = 0;

                if (oceanmonumentpieces_v1.hasOpening[EnumDirection.WEST.get3DDataValue()]) {
                    this.generateBox(generatoraccessseed, structureboundingbox, b1, i, 2, b1, i + 2, 2, OceanMonumentPieces.m.BASE_LIGHT, OceanMonumentPieces.m.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, b1, i, 5, b1, i + 2, 5, OceanMonumentPieces.m.BASE_LIGHT, OceanMonumentPieces.m.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, b1, i + 2, 3, b1, i + 2, 4, OceanMonumentPieces.m.BASE_LIGHT, OceanMonumentPieces.m.BASE_LIGHT, false);
                } else {
                    this.generateBox(generatoraccessseed, structureboundingbox, b1, i, 0, b1, i + 2, 7, OceanMonumentPieces.m.BASE_LIGHT, OceanMonumentPieces.m.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, b1, i + 1, 0, b1, i + 1, 7, OceanMonumentPieces.m.BASE_GRAY, OceanMonumentPieces.m.BASE_GRAY, false);
                }

                b1 = 7;
                if (oceanmonumentpieces_v1.hasOpening[EnumDirection.EAST.get3DDataValue()]) {
                    this.generateBox(generatoraccessseed, structureboundingbox, b1, i, 2, b1, i + 2, 2, OceanMonumentPieces.m.BASE_LIGHT, OceanMonumentPieces.m.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, b1, i, 5, b1, i + 2, 5, OceanMonumentPieces.m.BASE_LIGHT, OceanMonumentPieces.m.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, b1, i + 2, 3, b1, i + 2, 4, OceanMonumentPieces.m.BASE_LIGHT, OceanMonumentPieces.m.BASE_LIGHT, false);
                } else {
                    this.generateBox(generatoraccessseed, structureboundingbox, b1, i, 0, b1, i + 2, 7, OceanMonumentPieces.m.BASE_LIGHT, OceanMonumentPieces.m.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, b1, i + 1, 0, b1, i + 1, 7, OceanMonumentPieces.m.BASE_GRAY, OceanMonumentPieces.m.BASE_GRAY, false);
                }

                oceanmonumentpieces_v1 = oceanmonumentpieces_v;
            }

        }
    }

    public static class t extends OceanMonumentPieces.r {

        public t(EnumDirection enumdirection, OceanMonumentPieces.v oceanmonumentpieces_v) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_SIMPLE_TOP_ROOM, 1, enumdirection, oceanmonumentpieces_v, 1, 1, 1);
        }

        public t(NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_SIMPLE_TOP_ROOM, nbttagcompound);
        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            if (this.roomDefinition.index / 25 > 0) {
                this.generateDefaultFloor(generatoraccessseed, structureboundingbox, 0, 0, this.roomDefinition.hasOpening[EnumDirection.DOWN.get3DDataValue()]);
            }

            if (this.roomDefinition.connections[EnumDirection.UP.get3DDataValue()] == null) {
                this.generateBoxOnFillOnly(generatoraccessseed, structureboundingbox, 1, 4, 1, 6, 4, 6, OceanMonumentPieces.t.BASE_GRAY);
            }

            for (int i = 1; i <= 6; ++i) {
                for (int j = 1; j <= 6; ++j) {
                    if (randomsource.nextInt(3) != 0) {
                        int k = 2 + (randomsource.nextInt(4) == 0 ? 0 : 1);
                        IBlockData iblockdata = Blocks.WET_SPONGE.defaultBlockState();

                        this.generateBox(generatoraccessseed, structureboundingbox, i, k, j, i, 3, j, iblockdata, iblockdata, false);
                    }
                }
            }

            this.generateBox(generatoraccessseed, structureboundingbox, 0, 1, 0, 0, 1, 7, OceanMonumentPieces.t.BASE_LIGHT, OceanMonumentPieces.t.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 7, 1, 0, 7, 1, 7, OceanMonumentPieces.t.BASE_LIGHT, OceanMonumentPieces.t.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 1, 0, 6, 1, 0, OceanMonumentPieces.t.BASE_LIGHT, OceanMonumentPieces.t.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 1, 7, 6, 1, 7, OceanMonumentPieces.t.BASE_LIGHT, OceanMonumentPieces.t.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 0, 0, 2, 7, OceanMonumentPieces.t.BASE_BLACK, OceanMonumentPieces.t.BASE_BLACK, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 7, 2, 0, 7, 2, 7, OceanMonumentPieces.t.BASE_BLACK, OceanMonumentPieces.t.BASE_BLACK, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 2, 0, 6, 2, 0, OceanMonumentPieces.t.BASE_BLACK, OceanMonumentPieces.t.BASE_BLACK, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 2, 7, 6, 2, 7, OceanMonumentPieces.t.BASE_BLACK, OceanMonumentPieces.t.BASE_BLACK, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 3, 0, 0, 3, 7, OceanMonumentPieces.t.BASE_LIGHT, OceanMonumentPieces.t.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 7, 3, 0, 7, 3, 7, OceanMonumentPieces.t.BASE_LIGHT, OceanMonumentPieces.t.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 3, 0, 6, 3, 0, OceanMonumentPieces.t.BASE_LIGHT, OceanMonumentPieces.t.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 3, 7, 6, 3, 7, OceanMonumentPieces.t.BASE_LIGHT, OceanMonumentPieces.t.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 1, 3, 0, 2, 4, OceanMonumentPieces.t.BASE_BLACK, OceanMonumentPieces.t.BASE_BLACK, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 7, 1, 3, 7, 2, 4, OceanMonumentPieces.t.BASE_BLACK, OceanMonumentPieces.t.BASE_BLACK, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 3, 1, 0, 4, 2, 0, OceanMonumentPieces.t.BASE_BLACK, OceanMonumentPieces.t.BASE_BLACK, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 3, 1, 7, 4, 2, 7, OceanMonumentPieces.t.BASE_BLACK, OceanMonumentPieces.t.BASE_BLACK, false);
            if (this.roomDefinition.hasOpening[EnumDirection.SOUTH.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 3, 1, 0, 4, 2, 0);
            }

        }
    }

    public static class s extends OceanMonumentPieces.r {

        private int mainDesign;

        public s(EnumDirection enumdirection, OceanMonumentPieces.v oceanmonumentpieces_v, RandomSource randomsource) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_SIMPLE_ROOM, 1, enumdirection, oceanmonumentpieces_v, 1, 1, 1);
            this.mainDesign = randomsource.nextInt(3);
        }

        public s(NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_SIMPLE_ROOM, nbttagcompound);
        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            if (this.roomDefinition.index / 25 > 0) {
                this.generateDefaultFloor(generatoraccessseed, structureboundingbox, 0, 0, this.roomDefinition.hasOpening[EnumDirection.DOWN.get3DDataValue()]);
            }

            if (this.roomDefinition.connections[EnumDirection.UP.get3DDataValue()] == null) {
                this.generateBoxOnFillOnly(generatoraccessseed, structureboundingbox, 1, 4, 1, 6, 4, 6, OceanMonumentPieces.s.BASE_GRAY);
            }

            boolean flag = this.mainDesign != 0 && randomsource.nextBoolean() && !this.roomDefinition.hasOpening[EnumDirection.DOWN.get3DDataValue()] && !this.roomDefinition.hasOpening[EnumDirection.UP.get3DDataValue()] && this.roomDefinition.countOpenings() > 1;

            if (this.mainDesign == 0) {
                this.generateBox(generatoraccessseed, structureboundingbox, 0, 1, 0, 2, 1, 2, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 0, 3, 0, 2, 3, 2, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 0, 0, 2, 2, OceanMonumentPieces.s.BASE_GRAY, OceanMonumentPieces.s.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 1, 2, 0, 2, 2, 0, OceanMonumentPieces.s.BASE_GRAY, OceanMonumentPieces.s.BASE_GRAY, false);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.s.LAMP_BLOCK, 1, 2, 1, structureboundingbox);
                this.generateBox(generatoraccessseed, structureboundingbox, 5, 1, 0, 7, 1, 2, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 5, 3, 0, 7, 3, 2, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 7, 2, 0, 7, 2, 2, OceanMonumentPieces.s.BASE_GRAY, OceanMonumentPieces.s.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 5, 2, 0, 6, 2, 0, OceanMonumentPieces.s.BASE_GRAY, OceanMonumentPieces.s.BASE_GRAY, false);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.s.LAMP_BLOCK, 6, 2, 1, structureboundingbox);
                this.generateBox(generatoraccessseed, structureboundingbox, 0, 1, 5, 2, 1, 7, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 0, 3, 5, 2, 3, 7, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 5, 0, 2, 7, OceanMonumentPieces.s.BASE_GRAY, OceanMonumentPieces.s.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 1, 2, 7, 2, 2, 7, OceanMonumentPieces.s.BASE_GRAY, OceanMonumentPieces.s.BASE_GRAY, false);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.s.LAMP_BLOCK, 1, 2, 6, structureboundingbox);
                this.generateBox(generatoraccessseed, structureboundingbox, 5, 1, 5, 7, 1, 7, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 5, 3, 5, 7, 3, 7, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 7, 2, 5, 7, 2, 7, OceanMonumentPieces.s.BASE_GRAY, OceanMonumentPieces.s.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 5, 2, 7, 6, 2, 7, OceanMonumentPieces.s.BASE_GRAY, OceanMonumentPieces.s.BASE_GRAY, false);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.s.LAMP_BLOCK, 6, 2, 6, structureboundingbox);
                if (this.roomDefinition.hasOpening[EnumDirection.SOUTH.get3DDataValue()]) {
                    this.generateBox(generatoraccessseed, structureboundingbox, 3, 3, 0, 4, 3, 0, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                } else {
                    this.generateBox(generatoraccessseed, structureboundingbox, 3, 3, 0, 4, 3, 1, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, 3, 2, 0, 4, 2, 0, OceanMonumentPieces.s.BASE_GRAY, OceanMonumentPieces.s.BASE_GRAY, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, 3, 1, 0, 4, 1, 1, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                }

                if (this.roomDefinition.hasOpening[EnumDirection.NORTH.get3DDataValue()]) {
                    this.generateBox(generatoraccessseed, structureboundingbox, 3, 3, 7, 4, 3, 7, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                } else {
                    this.generateBox(generatoraccessseed, structureboundingbox, 3, 3, 6, 4, 3, 7, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, 3, 2, 7, 4, 2, 7, OceanMonumentPieces.s.BASE_GRAY, OceanMonumentPieces.s.BASE_GRAY, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, 3, 1, 6, 4, 1, 7, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                }

                if (this.roomDefinition.hasOpening[EnumDirection.WEST.get3DDataValue()]) {
                    this.generateBox(generatoraccessseed, structureboundingbox, 0, 3, 3, 0, 3, 4, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                } else {
                    this.generateBox(generatoraccessseed, structureboundingbox, 0, 3, 3, 1, 3, 4, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 3, 0, 2, 4, OceanMonumentPieces.s.BASE_GRAY, OceanMonumentPieces.s.BASE_GRAY, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, 0, 1, 3, 1, 1, 4, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                }

                if (this.roomDefinition.hasOpening[EnumDirection.EAST.get3DDataValue()]) {
                    this.generateBox(generatoraccessseed, structureboundingbox, 7, 3, 3, 7, 3, 4, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                } else {
                    this.generateBox(generatoraccessseed, structureboundingbox, 6, 3, 3, 7, 3, 4, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, 7, 2, 3, 7, 2, 4, OceanMonumentPieces.s.BASE_GRAY, OceanMonumentPieces.s.BASE_GRAY, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, 6, 1, 3, 7, 1, 4, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                }
            } else if (this.mainDesign == 1) {
                this.generateBox(generatoraccessseed, structureboundingbox, 2, 1, 2, 2, 3, 2, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 2, 1, 5, 2, 3, 5, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 5, 1, 5, 5, 3, 5, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 5, 1, 2, 5, 3, 2, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.s.LAMP_BLOCK, 2, 2, 2, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.s.LAMP_BLOCK, 2, 2, 5, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.s.LAMP_BLOCK, 5, 2, 5, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.s.LAMP_BLOCK, 5, 2, 2, structureboundingbox);
                this.generateBox(generatoraccessseed, structureboundingbox, 0, 1, 0, 1, 3, 0, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 0, 1, 1, 0, 3, 1, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 0, 1, 7, 1, 3, 7, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 0, 1, 6, 0, 3, 6, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 6, 1, 7, 7, 3, 7, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 7, 1, 6, 7, 3, 6, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 6, 1, 0, 7, 3, 0, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 7, 1, 1, 7, 3, 1, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.s.BASE_GRAY, 1, 2, 0, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.s.BASE_GRAY, 0, 2, 1, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.s.BASE_GRAY, 1, 2, 7, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.s.BASE_GRAY, 0, 2, 6, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.s.BASE_GRAY, 6, 2, 7, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.s.BASE_GRAY, 7, 2, 6, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.s.BASE_GRAY, 6, 2, 0, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.s.BASE_GRAY, 7, 2, 1, structureboundingbox);
                if (!this.roomDefinition.hasOpening[EnumDirection.SOUTH.get3DDataValue()]) {
                    this.generateBox(generatoraccessseed, structureboundingbox, 1, 3, 0, 6, 3, 0, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, 1, 2, 0, 6, 2, 0, OceanMonumentPieces.s.BASE_GRAY, OceanMonumentPieces.s.BASE_GRAY, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, 1, 1, 0, 6, 1, 0, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                }

                if (!this.roomDefinition.hasOpening[EnumDirection.NORTH.get3DDataValue()]) {
                    this.generateBox(generatoraccessseed, structureboundingbox, 1, 3, 7, 6, 3, 7, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, 1, 2, 7, 6, 2, 7, OceanMonumentPieces.s.BASE_GRAY, OceanMonumentPieces.s.BASE_GRAY, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, 1, 1, 7, 6, 1, 7, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                }

                if (!this.roomDefinition.hasOpening[EnumDirection.WEST.get3DDataValue()]) {
                    this.generateBox(generatoraccessseed, structureboundingbox, 0, 3, 1, 0, 3, 6, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 1, 0, 2, 6, OceanMonumentPieces.s.BASE_GRAY, OceanMonumentPieces.s.BASE_GRAY, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, 0, 1, 1, 0, 1, 6, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                }

                if (!this.roomDefinition.hasOpening[EnumDirection.EAST.get3DDataValue()]) {
                    this.generateBox(generatoraccessseed, structureboundingbox, 7, 3, 1, 7, 3, 6, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, 7, 2, 1, 7, 2, 6, OceanMonumentPieces.s.BASE_GRAY, OceanMonumentPieces.s.BASE_GRAY, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, 7, 1, 1, 7, 1, 6, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                }
            } else if (this.mainDesign == 2) {
                this.generateBox(generatoraccessseed, structureboundingbox, 0, 1, 0, 0, 1, 7, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 7, 1, 0, 7, 1, 7, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 1, 1, 0, 6, 1, 0, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 1, 1, 7, 6, 1, 7, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 0, 0, 2, 7, OceanMonumentPieces.s.BASE_BLACK, OceanMonumentPieces.s.BASE_BLACK, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 7, 2, 0, 7, 2, 7, OceanMonumentPieces.s.BASE_BLACK, OceanMonumentPieces.s.BASE_BLACK, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 1, 2, 0, 6, 2, 0, OceanMonumentPieces.s.BASE_BLACK, OceanMonumentPieces.s.BASE_BLACK, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 1, 2, 7, 6, 2, 7, OceanMonumentPieces.s.BASE_BLACK, OceanMonumentPieces.s.BASE_BLACK, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 0, 3, 0, 0, 3, 7, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 7, 3, 0, 7, 3, 7, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 1, 3, 0, 6, 3, 0, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 1, 3, 7, 6, 3, 7, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 0, 1, 3, 0, 2, 4, OceanMonumentPieces.s.BASE_BLACK, OceanMonumentPieces.s.BASE_BLACK, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 7, 1, 3, 7, 2, 4, OceanMonumentPieces.s.BASE_BLACK, OceanMonumentPieces.s.BASE_BLACK, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 3, 1, 0, 4, 2, 0, OceanMonumentPieces.s.BASE_BLACK, OceanMonumentPieces.s.BASE_BLACK, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 3, 1, 7, 4, 2, 7, OceanMonumentPieces.s.BASE_BLACK, OceanMonumentPieces.s.BASE_BLACK, false);
                if (this.roomDefinition.hasOpening[EnumDirection.SOUTH.get3DDataValue()]) {
                    this.generateWaterBox(generatoraccessseed, structureboundingbox, 3, 1, 0, 4, 2, 0);
                }

                if (this.roomDefinition.hasOpening[EnumDirection.NORTH.get3DDataValue()]) {
                    this.generateWaterBox(generatoraccessseed, structureboundingbox, 3, 1, 7, 4, 2, 7);
                }

                if (this.roomDefinition.hasOpening[EnumDirection.WEST.get3DDataValue()]) {
                    this.generateWaterBox(generatoraccessseed, structureboundingbox, 0, 1, 3, 0, 2, 4);
                }

                if (this.roomDefinition.hasOpening[EnumDirection.EAST.get3DDataValue()]) {
                    this.generateWaterBox(generatoraccessseed, structureboundingbox, 7, 1, 3, 7, 2, 4);
                }
            }

            if (flag) {
                this.generateBox(generatoraccessseed, structureboundingbox, 3, 1, 3, 4, 1, 4, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 3, 2, 3, 4, 2, 4, OceanMonumentPieces.s.BASE_GRAY, OceanMonumentPieces.s.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 3, 3, 3, 4, 3, 4, OceanMonumentPieces.s.BASE_LIGHT, OceanMonumentPieces.s.BASE_LIGHT, false);
            }

        }
    }

    public static class p extends OceanMonumentPieces.r {

        public p(EnumDirection enumdirection, OceanMonumentPieces.v oceanmonumentpieces_v) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_ENTRY_ROOM, 1, enumdirection, oceanmonumentpieces_v, 1, 1, 1);
        }

        public p(NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_ENTRY_ROOM, nbttagcompound);
        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 3, 0, 2, 3, 7, OceanMonumentPieces.p.BASE_LIGHT, OceanMonumentPieces.p.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 3, 0, 7, 3, 7, OceanMonumentPieces.p.BASE_LIGHT, OceanMonumentPieces.p.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 0, 1, 2, 7, OceanMonumentPieces.p.BASE_LIGHT, OceanMonumentPieces.p.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 6, 2, 0, 7, 2, 7, OceanMonumentPieces.p.BASE_LIGHT, OceanMonumentPieces.p.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 1, 0, 0, 1, 7, OceanMonumentPieces.p.BASE_LIGHT, OceanMonumentPieces.p.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 7, 1, 0, 7, 1, 7, OceanMonumentPieces.p.BASE_LIGHT, OceanMonumentPieces.p.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 1, 7, 7, 3, 7, OceanMonumentPieces.p.BASE_LIGHT, OceanMonumentPieces.p.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 1, 0, 2, 3, 0, OceanMonumentPieces.p.BASE_LIGHT, OceanMonumentPieces.p.BASE_LIGHT, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 1, 0, 6, 3, 0, OceanMonumentPieces.p.BASE_LIGHT, OceanMonumentPieces.p.BASE_LIGHT, false);
            if (this.roomDefinition.hasOpening[EnumDirection.NORTH.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 3, 1, 7, 4, 2, 7);
            }

            if (this.roomDefinition.hasOpening[EnumDirection.WEST.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 0, 1, 3, 1, 2, 4);
            }

            if (this.roomDefinition.hasOpening[EnumDirection.EAST.get3DDataValue()]) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 6, 1, 3, 7, 2, 4);
            }

        }
    }

    public static class h extends OceanMonumentPieces.r {

        private static final int WIDTH = 58;
        private static final int HEIGHT = 22;
        private static final int DEPTH = 58;
        public static final int BIOME_RANGE_CHECK = 29;
        private static final int TOP_POSITION = 61;
        private OceanMonumentPieces.v sourceRoom;
        private OceanMonumentPieces.v coreRoom;
        private final List<OceanMonumentPieces.r> childPieces = Lists.newArrayList();

        public h(RandomSource randomsource, int i, int j, EnumDirection enumdirection) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_BUILDING, enumdirection, 0, makeBoundingBox(i, 39, j, enumdirection, 58, 23, 58));
            this.setOrientation(enumdirection);
            List<OceanMonumentPieces.v> list = this.generateRoomGraph(randomsource);

            this.sourceRoom.claimed = true;
            this.childPieces.add(new OceanMonumentPieces.p(enumdirection, this.sourceRoom));
            this.childPieces.add(new OceanMonumentPieces.j(enumdirection, this.coreRoom));
            List<OceanMonumentPieces.i> list1 = Lists.newArrayList();

            list1.add(new OceanMonumentPieces.b());
            list1.add(new OceanMonumentPieces.d());
            list1.add(new OceanMonumentPieces.e());
            list1.add(new OceanMonumentPieces.a());
            list1.add(new OceanMonumentPieces.c());
            list1.add(new OceanMonumentPieces.g());
            list1.add(new OceanMonumentPieces.f());
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                OceanMonumentPieces.v oceanmonumentpieces_v = (OceanMonumentPieces.v) iterator.next();

                if (!oceanmonumentpieces_v.claimed && !oceanmonumentpieces_v.isSpecial()) {
                    Iterator iterator1 = list1.iterator();

                    while (iterator1.hasNext()) {
                        OceanMonumentPieces.i oceanmonumentpieces_i = (OceanMonumentPieces.i) iterator1.next();

                        if (oceanmonumentpieces_i.fits(oceanmonumentpieces_v)) {
                            this.childPieces.add(oceanmonumentpieces_i.create(enumdirection, oceanmonumentpieces_v, randomsource));
                            break;
                        }
                    }
                }
            }

            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.getWorldPos(9, 0, 22);
            Iterator iterator2 = this.childPieces.iterator();

            while (iterator2.hasNext()) {
                OceanMonumentPieces.r oceanmonumentpieces_r = (OceanMonumentPieces.r) iterator2.next();

                oceanmonumentpieces_r.getBoundingBox().move(blockposition_mutableblockposition);
            }

            StructureBoundingBox structureboundingbox = StructureBoundingBox.fromCorners(this.getWorldPos(1, 1, 1), this.getWorldPos(23, 8, 21));
            StructureBoundingBox structureboundingbox1 = StructureBoundingBox.fromCorners(this.getWorldPos(34, 1, 1), this.getWorldPos(56, 8, 21));
            StructureBoundingBox structureboundingbox2 = StructureBoundingBox.fromCorners(this.getWorldPos(22, 13, 22), this.getWorldPos(35, 17, 35));
            int k = randomsource.nextInt();

            this.childPieces.add(new OceanMonumentPieces.u(enumdirection, structureboundingbox, k++));
            this.childPieces.add(new OceanMonumentPieces.u(enumdirection, structureboundingbox1, k++));
            this.childPieces.add(new OceanMonumentPieces.q(enumdirection, structureboundingbox2));
        }

        public h(NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.OCEAN_MONUMENT_BUILDING, nbttagcompound);
        }

        private List<OceanMonumentPieces.v> generateRoomGraph(RandomSource randomsource) {
            OceanMonumentPieces.v[] aoceanmonumentpieces_v = new OceanMonumentPieces.v[75];

            boolean flag;
            int i;
            int j;
            int k;

            for (j = 0; j < 5; ++j) {
                for (k = 0; k < 4; ++k) {
                    flag = false;
                    i = getRoomIndex(j, 0, k);
                    aoceanmonumentpieces_v[i] = new OceanMonumentPieces.v(i);
                }
            }

            for (j = 0; j < 5; ++j) {
                for (k = 0; k < 4; ++k) {
                    flag = true;
                    i = getRoomIndex(j, 1, k);
                    aoceanmonumentpieces_v[i] = new OceanMonumentPieces.v(i);
                }
            }

            for (j = 1; j < 4; ++j) {
                for (k = 0; k < 2; ++k) {
                    flag = true;
                    i = getRoomIndex(j, 2, k);
                    aoceanmonumentpieces_v[i] = new OceanMonumentPieces.v(i);
                }
            }

            this.sourceRoom = aoceanmonumentpieces_v[OceanMonumentPieces.h.GRIDROOM_SOURCE_INDEX];

            int l;
            int i1;
            int j1;
            int k1;
            int l1;

            for (j = 0; j < 5; ++j) {
                for (k = 0; k < 5; ++k) {
                    for (int i2 = 0; i2 < 3; ++i2) {
                        i = getRoomIndex(j, i2, k);
                        if (aoceanmonumentpieces_v[i] != null) {
                            EnumDirection[] aenumdirection = EnumDirection.values();

                            l = aenumdirection.length;

                            for (i1 = 0; i1 < l; ++i1) {
                                EnumDirection enumdirection = aenumdirection[i1];

                                j1 = j + enumdirection.getStepX();
                                k1 = i2 + enumdirection.getStepY();
                                l1 = k + enumdirection.getStepZ();
                                if (j1 >= 0 && j1 < 5 && l1 >= 0 && l1 < 5 && k1 >= 0 && k1 < 3) {
                                    int j2 = getRoomIndex(j1, k1, l1);

                                    if (aoceanmonumentpieces_v[j2] != null) {
                                        if (l1 == k) {
                                            aoceanmonumentpieces_v[i].setConnection(enumdirection, aoceanmonumentpieces_v[j2]);
                                        } else {
                                            aoceanmonumentpieces_v[i].setConnection(enumdirection.getOpposite(), aoceanmonumentpieces_v[j2]);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            OceanMonumentPieces.v oceanmonumentpieces_v = new OceanMonumentPieces.v(1003);
            OceanMonumentPieces.v oceanmonumentpieces_v1 = new OceanMonumentPieces.v(1001);
            OceanMonumentPieces.v oceanmonumentpieces_v2 = new OceanMonumentPieces.v(1002);

            aoceanmonumentpieces_v[OceanMonumentPieces.h.GRIDROOM_TOP_CONNECT_INDEX].setConnection(EnumDirection.UP, oceanmonumentpieces_v);
            aoceanmonumentpieces_v[OceanMonumentPieces.h.GRIDROOM_LEFTWING_CONNECT_INDEX].setConnection(EnumDirection.SOUTH, oceanmonumentpieces_v1);
            aoceanmonumentpieces_v[OceanMonumentPieces.h.GRIDROOM_RIGHTWING_CONNECT_INDEX].setConnection(EnumDirection.SOUTH, oceanmonumentpieces_v2);
            oceanmonumentpieces_v.claimed = true;
            oceanmonumentpieces_v1.claimed = true;
            oceanmonumentpieces_v2.claimed = true;
            this.sourceRoom.isSource = true;
            this.coreRoom = aoceanmonumentpieces_v[getRoomIndex(randomsource.nextInt(4), 0, 2)];
            this.coreRoom.claimed = true;
            this.coreRoom.connections[EnumDirection.EAST.get3DDataValue()].claimed = true;
            this.coreRoom.connections[EnumDirection.NORTH.get3DDataValue()].claimed = true;
            this.coreRoom.connections[EnumDirection.EAST.get3DDataValue()].connections[EnumDirection.NORTH.get3DDataValue()].claimed = true;
            this.coreRoom.connections[EnumDirection.UP.get3DDataValue()].claimed = true;
            this.coreRoom.connections[EnumDirection.EAST.get3DDataValue()].connections[EnumDirection.UP.get3DDataValue()].claimed = true;
            this.coreRoom.connections[EnumDirection.NORTH.get3DDataValue()].connections[EnumDirection.UP.get3DDataValue()].claimed = true;
            this.coreRoom.connections[EnumDirection.EAST.get3DDataValue()].connections[EnumDirection.NORTH.get3DDataValue()].connections[EnumDirection.UP.get3DDataValue()].claimed = true;
            ObjectArrayList<OceanMonumentPieces.v> objectarraylist = new ObjectArrayList();
            OceanMonumentPieces.v[] aoceanmonumentpieces_v1 = aoceanmonumentpieces_v;

            l = aoceanmonumentpieces_v.length;

            for (i1 = 0; i1 < l; ++i1) {
                OceanMonumentPieces.v oceanmonumentpieces_v3 = aoceanmonumentpieces_v1[i1];

                if (oceanmonumentpieces_v3 != null) {
                    oceanmonumentpieces_v3.updateOpenings();
                    objectarraylist.add(oceanmonumentpieces_v3);
                }
            }

            oceanmonumentpieces_v.updateOpenings();
            SystemUtils.shuffle(objectarraylist, randomsource);
            int k2 = 1;
            ObjectListIterator objectlistiterator = objectarraylist.iterator();

            while (objectlistiterator.hasNext()) {
                OceanMonumentPieces.v oceanmonumentpieces_v4 = (OceanMonumentPieces.v) objectlistiterator.next();
                int l2 = 0;

                j1 = 0;

                while (l2 < 2 && j1 < 5) {
                    ++j1;
                    k1 = randomsource.nextInt(6);
                    if (oceanmonumentpieces_v4.hasOpening[k1]) {
                        l1 = EnumDirection.from3DDataValue(k1).getOpposite().get3DDataValue();
                        oceanmonumentpieces_v4.hasOpening[k1] = false;
                        oceanmonumentpieces_v4.connections[k1].hasOpening[l1] = false;
                        if (oceanmonumentpieces_v4.findSource(k2++) && oceanmonumentpieces_v4.connections[k1].findSource(k2++)) {
                            ++l2;
                        } else {
                            oceanmonumentpieces_v4.hasOpening[k1] = true;
                            oceanmonumentpieces_v4.connections[k1].hasOpening[l1] = true;
                        }
                    }
                }
            }

            objectarraylist.add(oceanmonumentpieces_v);
            objectarraylist.add(oceanmonumentpieces_v1);
            objectarraylist.add(oceanmonumentpieces_v2);
            return objectarraylist;
        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            int i = Math.max(generatoraccessseed.getSeaLevel(), 64) - this.boundingBox.minY();

            this.generateWaterBox(generatoraccessseed, structureboundingbox, 0, 0, 0, 58, i, 58);
            this.generateWing(false, 0, generatoraccessseed, randomsource, structureboundingbox);
            this.generateWing(true, 33, generatoraccessseed, randomsource, structureboundingbox);
            this.generateEntranceArchs(generatoraccessseed, randomsource, structureboundingbox);
            this.generateEntranceWall(generatoraccessseed, randomsource, structureboundingbox);
            this.generateRoofPiece(generatoraccessseed, randomsource, structureboundingbox);
            this.generateLowerWall(generatoraccessseed, randomsource, structureboundingbox);
            this.generateMiddleWall(generatoraccessseed, randomsource, structureboundingbox);
            this.generateUpperWall(generatoraccessseed, randomsource, structureboundingbox);

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
                            this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.BASE_LIGHT, l + j1, 0, i1 + k1, structureboundingbox);
                            this.fillColumnDown(generatoraccessseed, OceanMonumentPieces.h.BASE_LIGHT, l + j1, -1, i1 + k1, structureboundingbox);
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
                this.generateWaterBox(generatoraccessseed, structureboundingbox, -1 - j, 0 + j * 2, -1 - j, -1 - j, 23, 58 + j);
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 58 + j, 0 + j * 2, -1 - j, 58 + j, 23, 58 + j);
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 0 - j, 0 + j * 2, -1 - j, 57 + j, 23, -1 - j);
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 0 - j, 0 + j * 2, 58 + j, 57 + j, 23, 58 + j);
            }

            Iterator iterator = this.childPieces.iterator();

            while (iterator.hasNext()) {
                OceanMonumentPieces.r oceanmonumentpieces_r = (OceanMonumentPieces.r) iterator.next();

                if (oceanmonumentpieces_r.getBoundingBox().intersects(structureboundingbox)) {
                    oceanmonumentpieces_r.postProcess(generatoraccessseed, structuremanager, chunkgenerator, randomsource, structureboundingbox, chunkcoordintpair, blockposition);
                }
            }

        }

        private void generateWing(boolean flag, int i, GeneratorAccessSeed generatoraccessseed, RandomSource randomsource, StructureBoundingBox structureboundingbox) {
            boolean flag1 = true;

            if (this.chunkIntersects(structureboundingbox, i, 0, i + 23, 20)) {
                this.generateBox(generatoraccessseed, structureboundingbox, i + 0, 0, 0, i + 24, 0, 20, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateWaterBox(generatoraccessseed, structureboundingbox, i + 0, 1, 0, i + 24, 10, 20);

                int j;

                for (j = 0; j < 4; ++j) {
                    this.generateBox(generatoraccessseed, structureboundingbox, i + j, j + 1, j, i + j, j + 1, 20, OceanMonumentPieces.h.BASE_LIGHT, OceanMonumentPieces.h.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, i + j + 7, j + 5, j + 7, i + j + 7, j + 5, 20, OceanMonumentPieces.h.BASE_LIGHT, OceanMonumentPieces.h.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, i + 17 - j, j + 5, j + 7, i + 17 - j, j + 5, 20, OceanMonumentPieces.h.BASE_LIGHT, OceanMonumentPieces.h.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, i + 24 - j, j + 1, j, i + 24 - j, j + 1, 20, OceanMonumentPieces.h.BASE_LIGHT, OceanMonumentPieces.h.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, i + j + 1, j + 1, j, i + 23 - j, j + 1, j, OceanMonumentPieces.h.BASE_LIGHT, OceanMonumentPieces.h.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, i + j + 8, j + 5, j + 7, i + 16 - j, j + 5, j + 7, OceanMonumentPieces.h.BASE_LIGHT, OceanMonumentPieces.h.BASE_LIGHT, false);
                }

                this.generateBox(generatoraccessseed, structureboundingbox, i + 4, 4, 4, i + 6, 4, 20, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, i + 7, 4, 4, i + 17, 4, 6, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, i + 18, 4, 4, i + 20, 4, 20, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, i + 11, 8, 11, i + 13, 8, 20, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.DOT_DECO_DATA, i + 12, 9, 12, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.DOT_DECO_DATA, i + 12, 9, 15, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.DOT_DECO_DATA, i + 12, 9, 18, structureboundingbox);
                j = i + (flag ? 19 : 5);
                int k = i + (flag ? 5 : 19);

                int l;

                for (l = 20; l >= 5; l -= 3) {
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.DOT_DECO_DATA, j, 5, l, structureboundingbox);
                }

                for (l = 19; l >= 7; l -= 3) {
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.DOT_DECO_DATA, k, 5, l, structureboundingbox);
                }

                for (l = 0; l < 4; ++l) {
                    int i1 = flag ? i + 24 - (17 - l * 3) : i + 17 - l * 3;

                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.DOT_DECO_DATA, i1, 5, 5, structureboundingbox);
                }

                this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.DOT_DECO_DATA, k, 5, 5, structureboundingbox);
                this.generateBox(generatoraccessseed, structureboundingbox, i + 11, 1, 12, i + 13, 7, 12, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, i + 12, 1, 11, i + 12, 7, 13, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
            }

        }

        private void generateEntranceArchs(GeneratorAccessSeed generatoraccessseed, RandomSource randomsource, StructureBoundingBox structureboundingbox) {
            if (this.chunkIntersects(structureboundingbox, 22, 5, 35, 17)) {
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 25, 0, 0, 32, 8, 20);

                for (int i = 0; i < 4; ++i) {
                    this.generateBox(generatoraccessseed, structureboundingbox, 24, 2, 5 + i * 4, 24, 4, 5 + i * 4, OceanMonumentPieces.h.BASE_LIGHT, OceanMonumentPieces.h.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, 22, 4, 5 + i * 4, 23, 4, 5 + i * 4, OceanMonumentPieces.h.BASE_LIGHT, OceanMonumentPieces.h.BASE_LIGHT, false);
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.BASE_LIGHT, 25, 5, 5 + i * 4, structureboundingbox);
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.BASE_LIGHT, 26, 6, 5 + i * 4, structureboundingbox);
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.LAMP_BLOCK, 26, 5, 5 + i * 4, structureboundingbox);
                    this.generateBox(generatoraccessseed, structureboundingbox, 33, 2, 5 + i * 4, 33, 4, 5 + i * 4, OceanMonumentPieces.h.BASE_LIGHT, OceanMonumentPieces.h.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, 34, 4, 5 + i * 4, 35, 4, 5 + i * 4, OceanMonumentPieces.h.BASE_LIGHT, OceanMonumentPieces.h.BASE_LIGHT, false);
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.BASE_LIGHT, 32, 5, 5 + i * 4, structureboundingbox);
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.BASE_LIGHT, 31, 6, 5 + i * 4, structureboundingbox);
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.LAMP_BLOCK, 31, 5, 5 + i * 4, structureboundingbox);
                    this.generateBox(generatoraccessseed, structureboundingbox, 27, 6, 5 + i * 4, 30, 6, 5 + i * 4, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                }
            }

        }

        private void generateEntranceWall(GeneratorAccessSeed generatoraccessseed, RandomSource randomsource, StructureBoundingBox structureboundingbox) {
            if (this.chunkIntersects(structureboundingbox, 15, 20, 42, 21)) {
                this.generateBox(generatoraccessseed, structureboundingbox, 15, 0, 21, 42, 0, 21, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 26, 1, 21, 31, 3, 21);
                this.generateBox(generatoraccessseed, structureboundingbox, 21, 12, 21, 36, 12, 21, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 17, 11, 21, 40, 11, 21, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 16, 10, 21, 41, 10, 21, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 15, 7, 21, 42, 9, 21, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 16, 6, 21, 41, 6, 21, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 17, 5, 21, 40, 5, 21, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 21, 4, 21, 36, 4, 21, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 22, 3, 21, 26, 3, 21, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 31, 3, 21, 35, 3, 21, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 23, 2, 21, 25, 2, 21, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 32, 2, 21, 34, 2, 21, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 28, 4, 20, 29, 4, 21, OceanMonumentPieces.h.BASE_LIGHT, OceanMonumentPieces.h.BASE_LIGHT, false);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.BASE_LIGHT, 27, 3, 21, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.BASE_LIGHT, 30, 3, 21, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.BASE_LIGHT, 26, 2, 21, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.BASE_LIGHT, 31, 2, 21, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.BASE_LIGHT, 25, 1, 21, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.BASE_LIGHT, 32, 1, 21, structureboundingbox);

                int i;

                for (i = 0; i < 7; ++i) {
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.BASE_BLACK, 28 - i, 6 + i, 21, structureboundingbox);
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.BASE_BLACK, 29 + i, 6 + i, 21, structureboundingbox);
                }

                for (i = 0; i < 4; ++i) {
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.BASE_BLACK, 28 - i, 9 + i, 21, structureboundingbox);
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.BASE_BLACK, 29 + i, 9 + i, 21, structureboundingbox);
                }

                this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.BASE_BLACK, 28, 12, 21, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.BASE_BLACK, 29, 12, 21, structureboundingbox);

                for (i = 0; i < 3; ++i) {
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.BASE_BLACK, 22 - i * 2, 8, 21, structureboundingbox);
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.BASE_BLACK, 22 - i * 2, 9, 21, structureboundingbox);
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.BASE_BLACK, 35 + i * 2, 8, 21, structureboundingbox);
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.BASE_BLACK, 35 + i * 2, 9, 21, structureboundingbox);
                }

                this.generateWaterBox(generatoraccessseed, structureboundingbox, 15, 13, 21, 42, 15, 21);
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 15, 1, 21, 15, 6, 21);
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 16, 1, 21, 16, 5, 21);
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 17, 1, 21, 20, 4, 21);
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 21, 1, 21, 21, 3, 21);
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 22, 1, 21, 22, 2, 21);
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 23, 1, 21, 24, 1, 21);
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 42, 1, 21, 42, 6, 21);
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 41, 1, 21, 41, 5, 21);
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 37, 1, 21, 40, 4, 21);
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 36, 1, 21, 36, 3, 21);
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 33, 1, 21, 34, 1, 21);
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 35, 1, 21, 35, 2, 21);
            }

        }

        private void generateRoofPiece(GeneratorAccessSeed generatoraccessseed, RandomSource randomsource, StructureBoundingBox structureboundingbox) {
            if (this.chunkIntersects(structureboundingbox, 21, 21, 36, 36)) {
                this.generateBox(generatoraccessseed, structureboundingbox, 21, 0, 22, 36, 0, 36, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 21, 1, 22, 36, 23, 36);

                for (int i = 0; i < 4; ++i) {
                    this.generateBox(generatoraccessseed, structureboundingbox, 21 + i, 13 + i, 21 + i, 36 - i, 13 + i, 21 + i, OceanMonumentPieces.h.BASE_LIGHT, OceanMonumentPieces.h.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, 21 + i, 13 + i, 36 - i, 36 - i, 13 + i, 36 - i, OceanMonumentPieces.h.BASE_LIGHT, OceanMonumentPieces.h.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, 21 + i, 13 + i, 22 + i, 21 + i, 13 + i, 35 - i, OceanMonumentPieces.h.BASE_LIGHT, OceanMonumentPieces.h.BASE_LIGHT, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, 36 - i, 13 + i, 22 + i, 36 - i, 13 + i, 35 - i, OceanMonumentPieces.h.BASE_LIGHT, OceanMonumentPieces.h.BASE_LIGHT, false);
                }

                this.generateBox(generatoraccessseed, structureboundingbox, 25, 16, 25, 32, 16, 32, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 25, 17, 25, 25, 19, 25, OceanMonumentPieces.h.BASE_LIGHT, OceanMonumentPieces.h.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 32, 17, 25, 32, 19, 25, OceanMonumentPieces.h.BASE_LIGHT, OceanMonumentPieces.h.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 25, 17, 32, 25, 19, 32, OceanMonumentPieces.h.BASE_LIGHT, OceanMonumentPieces.h.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 32, 17, 32, 32, 19, 32, OceanMonumentPieces.h.BASE_LIGHT, OceanMonumentPieces.h.BASE_LIGHT, false);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.BASE_LIGHT, 26, 20, 26, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.BASE_LIGHT, 27, 21, 27, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.LAMP_BLOCK, 27, 20, 27, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.BASE_LIGHT, 26, 20, 31, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.BASE_LIGHT, 27, 21, 30, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.LAMP_BLOCK, 27, 20, 30, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.BASE_LIGHT, 31, 20, 31, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.BASE_LIGHT, 30, 21, 30, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.LAMP_BLOCK, 30, 20, 30, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.BASE_LIGHT, 31, 20, 26, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.BASE_LIGHT, 30, 21, 27, structureboundingbox);
                this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.LAMP_BLOCK, 30, 20, 27, structureboundingbox);
                this.generateBox(generatoraccessseed, structureboundingbox, 28, 21, 27, 29, 21, 27, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 27, 21, 28, 27, 21, 29, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 28, 21, 30, 29, 21, 30, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 30, 21, 28, 30, 21, 29, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
            }

        }

        private void generateLowerWall(GeneratorAccessSeed generatoraccessseed, RandomSource randomsource, StructureBoundingBox structureboundingbox) {
            int i;

            if (this.chunkIntersects(structureboundingbox, 0, 21, 6, 58)) {
                this.generateBox(generatoraccessseed, structureboundingbox, 0, 0, 21, 6, 0, 57, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 0, 1, 21, 6, 7, 57);
                this.generateBox(generatoraccessseed, structureboundingbox, 4, 4, 21, 6, 4, 53, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);

                for (i = 0; i < 4; ++i) {
                    this.generateBox(generatoraccessseed, structureboundingbox, i, i + 1, 21, i, i + 1, 57 - i, OceanMonumentPieces.h.BASE_LIGHT, OceanMonumentPieces.h.BASE_LIGHT, false);
                }

                for (i = 23; i < 53; i += 3) {
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.DOT_DECO_DATA, 5, 5, i, structureboundingbox);
                }

                this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.DOT_DECO_DATA, 5, 5, 52, structureboundingbox);

                for (i = 0; i < 4; ++i) {
                    this.generateBox(generatoraccessseed, structureboundingbox, i, i + 1, 21, i, i + 1, 57 - i, OceanMonumentPieces.h.BASE_LIGHT, OceanMonumentPieces.h.BASE_LIGHT, false);
                }

                this.generateBox(generatoraccessseed, structureboundingbox, 4, 1, 52, 6, 3, 52, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 5, 1, 51, 5, 3, 53, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
            }

            if (this.chunkIntersects(structureboundingbox, 51, 21, 58, 58)) {
                this.generateBox(generatoraccessseed, structureboundingbox, 51, 0, 21, 57, 0, 57, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 51, 1, 21, 57, 7, 57);
                this.generateBox(generatoraccessseed, structureboundingbox, 51, 4, 21, 53, 4, 53, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);

                for (i = 0; i < 4; ++i) {
                    this.generateBox(generatoraccessseed, structureboundingbox, 57 - i, i + 1, 21, 57 - i, i + 1, 57 - i, OceanMonumentPieces.h.BASE_LIGHT, OceanMonumentPieces.h.BASE_LIGHT, false);
                }

                for (i = 23; i < 53; i += 3) {
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.DOT_DECO_DATA, 52, 5, i, structureboundingbox);
                }

                this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.DOT_DECO_DATA, 52, 5, 52, structureboundingbox);
                this.generateBox(generatoraccessseed, structureboundingbox, 51, 1, 52, 53, 3, 52, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 52, 1, 51, 52, 3, 53, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
            }

            if (this.chunkIntersects(structureboundingbox, 0, 51, 57, 57)) {
                this.generateBox(generatoraccessseed, structureboundingbox, 7, 0, 51, 50, 0, 57, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 7, 1, 51, 50, 10, 57);

                for (i = 0; i < 4; ++i) {
                    this.generateBox(generatoraccessseed, structureboundingbox, i + 1, i + 1, 57 - i, 56 - i, i + 1, 57 - i, OceanMonumentPieces.h.BASE_LIGHT, OceanMonumentPieces.h.BASE_LIGHT, false);
                }
            }

        }

        private void generateMiddleWall(GeneratorAccessSeed generatoraccessseed, RandomSource randomsource, StructureBoundingBox structureboundingbox) {
            int i;

            if (this.chunkIntersects(structureboundingbox, 7, 21, 13, 50)) {
                this.generateBox(generatoraccessseed, structureboundingbox, 7, 0, 21, 13, 0, 50, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 7, 1, 21, 13, 10, 50);
                this.generateBox(generatoraccessseed, structureboundingbox, 11, 8, 21, 13, 8, 53, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);

                for (i = 0; i < 4; ++i) {
                    this.generateBox(generatoraccessseed, structureboundingbox, i + 7, i + 5, 21, i + 7, i + 5, 54, OceanMonumentPieces.h.BASE_LIGHT, OceanMonumentPieces.h.BASE_LIGHT, false);
                }

                for (i = 21; i <= 45; i += 3) {
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.DOT_DECO_DATA, 12, 9, i, structureboundingbox);
                }
            }

            if (this.chunkIntersects(structureboundingbox, 44, 21, 50, 54)) {
                this.generateBox(generatoraccessseed, structureboundingbox, 44, 0, 21, 50, 0, 50, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 44, 1, 21, 50, 10, 50);
                this.generateBox(generatoraccessseed, structureboundingbox, 44, 8, 21, 46, 8, 53, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);

                for (i = 0; i < 4; ++i) {
                    this.generateBox(generatoraccessseed, structureboundingbox, 50 - i, i + 5, 21, 50 - i, i + 5, 54, OceanMonumentPieces.h.BASE_LIGHT, OceanMonumentPieces.h.BASE_LIGHT, false);
                }

                for (i = 21; i <= 45; i += 3) {
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.DOT_DECO_DATA, 45, 9, i, structureboundingbox);
                }
            }

            if (this.chunkIntersects(structureboundingbox, 8, 44, 49, 54)) {
                this.generateBox(generatoraccessseed, structureboundingbox, 14, 0, 44, 43, 0, 50, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 14, 1, 44, 43, 10, 50);

                for (i = 12; i <= 45; i += 3) {
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.DOT_DECO_DATA, i, 9, 45, structureboundingbox);
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.DOT_DECO_DATA, i, 9, 52, structureboundingbox);
                    if (i == 12 || i == 18 || i == 24 || i == 33 || i == 39 || i == 45) {
                        this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.DOT_DECO_DATA, i, 9, 47, structureboundingbox);
                        this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.DOT_DECO_DATA, i, 9, 50, structureboundingbox);
                        this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.DOT_DECO_DATA, i, 10, 45, structureboundingbox);
                        this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.DOT_DECO_DATA, i, 10, 46, structureboundingbox);
                        this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.DOT_DECO_DATA, i, 10, 51, structureboundingbox);
                        this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.DOT_DECO_DATA, i, 10, 52, structureboundingbox);
                        this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.DOT_DECO_DATA, i, 11, 47, structureboundingbox);
                        this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.DOT_DECO_DATA, i, 11, 50, structureboundingbox);
                        this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.DOT_DECO_DATA, i, 12, 48, structureboundingbox);
                        this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.DOT_DECO_DATA, i, 12, 49, structureboundingbox);
                    }
                }

                for (i = 0; i < 3; ++i) {
                    this.generateBox(generatoraccessseed, structureboundingbox, 8 + i, 5 + i, 54, 49 - i, 5 + i, 54, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                }

                this.generateBox(generatoraccessseed, structureboundingbox, 11, 8, 54, 46, 8, 54, OceanMonumentPieces.h.BASE_LIGHT, OceanMonumentPieces.h.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 14, 8, 44, 43, 8, 53, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
            }

        }

        private void generateUpperWall(GeneratorAccessSeed generatoraccessseed, RandomSource randomsource, StructureBoundingBox structureboundingbox) {
            int i;

            if (this.chunkIntersects(structureboundingbox, 14, 21, 20, 43)) {
                this.generateBox(generatoraccessseed, structureboundingbox, 14, 0, 21, 20, 0, 43, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 14, 1, 22, 20, 14, 43);
                this.generateBox(generatoraccessseed, structureboundingbox, 18, 12, 22, 20, 12, 39, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 18, 12, 21, 20, 12, 21, OceanMonumentPieces.h.BASE_LIGHT, OceanMonumentPieces.h.BASE_LIGHT, false);

                for (i = 0; i < 4; ++i) {
                    this.generateBox(generatoraccessseed, structureboundingbox, i + 14, i + 9, 21, i + 14, i + 9, 43 - i, OceanMonumentPieces.h.BASE_LIGHT, OceanMonumentPieces.h.BASE_LIGHT, false);
                }

                for (i = 23; i <= 39; i += 3) {
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.DOT_DECO_DATA, 19, 13, i, structureboundingbox);
                }
            }

            if (this.chunkIntersects(structureboundingbox, 37, 21, 43, 43)) {
                this.generateBox(generatoraccessseed, structureboundingbox, 37, 0, 21, 43, 0, 43, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 37, 1, 22, 43, 14, 43);
                this.generateBox(generatoraccessseed, structureboundingbox, 37, 12, 22, 39, 12, 39, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 37, 12, 21, 39, 12, 21, OceanMonumentPieces.h.BASE_LIGHT, OceanMonumentPieces.h.BASE_LIGHT, false);

                for (i = 0; i < 4; ++i) {
                    this.generateBox(generatoraccessseed, structureboundingbox, 43 - i, i + 9, 21, 43 - i, i + 9, 43 - i, OceanMonumentPieces.h.BASE_LIGHT, OceanMonumentPieces.h.BASE_LIGHT, false);
                }

                for (i = 23; i <= 39; i += 3) {
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.DOT_DECO_DATA, 38, 13, i, structureboundingbox);
                }
            }

            if (this.chunkIntersects(structureboundingbox, 15, 37, 42, 43)) {
                this.generateBox(generatoraccessseed, structureboundingbox, 21, 0, 37, 36, 0, 43, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);
                this.generateWaterBox(generatoraccessseed, structureboundingbox, 21, 1, 37, 36, 14, 43);
                this.generateBox(generatoraccessseed, structureboundingbox, 21, 12, 37, 36, 12, 39, OceanMonumentPieces.h.BASE_GRAY, OceanMonumentPieces.h.BASE_GRAY, false);

                for (i = 0; i < 4; ++i) {
                    this.generateBox(generatoraccessseed, structureboundingbox, 15 + i, i + 9, 43 - i, 42 - i, i + 9, 43 - i, OceanMonumentPieces.h.BASE_LIGHT, OceanMonumentPieces.h.BASE_LIGHT, false);
                }

                for (i = 21; i <= 36; i += 3) {
                    this.placeBlock(generatoraccessseed, OceanMonumentPieces.h.DOT_DECO_DATA, i, 13, 38, structureboundingbox);
                }
            }

        }
    }

    protected abstract static class r extends StructurePiece {

        protected static final IBlockData BASE_GRAY = Blocks.PRISMARINE.defaultBlockState();
        protected static final IBlockData BASE_LIGHT = Blocks.PRISMARINE_BRICKS.defaultBlockState();
        protected static final IBlockData BASE_BLACK = Blocks.DARK_PRISMARINE.defaultBlockState();
        protected static final IBlockData DOT_DECO_DATA = OceanMonumentPieces.r.BASE_LIGHT;
        protected static final IBlockData LAMP_BLOCK = Blocks.SEA_LANTERN.defaultBlockState();
        protected static final boolean DO_FILL = true;
        protected static final IBlockData FILL_BLOCK = Blocks.WATER.defaultBlockState();
        protected static final Set<Block> FILL_KEEP = ImmutableSet.builder().add(Blocks.ICE).add(Blocks.PACKED_ICE).add(Blocks.BLUE_ICE).add(OceanMonumentPieces.r.FILL_BLOCK.getBlock()).build();
        protected static final int GRIDROOM_WIDTH = 8;
        protected static final int GRIDROOM_DEPTH = 8;
        protected static final int GRIDROOM_HEIGHT = 4;
        protected static final int GRID_WIDTH = 5;
        protected static final int GRID_DEPTH = 5;
        protected static final int GRID_HEIGHT = 3;
        protected static final int GRID_FLOOR_COUNT = 25;
        protected static final int GRID_SIZE = 75;
        protected static final int GRIDROOM_SOURCE_INDEX = getRoomIndex(2, 0, 0);
        protected static final int GRIDROOM_TOP_CONNECT_INDEX = getRoomIndex(2, 2, 0);
        protected static final int GRIDROOM_LEFTWING_CONNECT_INDEX = getRoomIndex(0, 1, 0);
        protected static final int GRIDROOM_RIGHTWING_CONNECT_INDEX = getRoomIndex(4, 1, 0);
        protected static final int LEFTWING_INDEX = 1001;
        protected static final int RIGHTWING_INDEX = 1002;
        protected static final int PENTHOUSE_INDEX = 1003;
        protected OceanMonumentPieces.v roomDefinition;

        protected static int getRoomIndex(int i, int j, int k) {
            return j * 25 + k * 5 + i;
        }

        public r(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, EnumDirection enumdirection, int i, StructureBoundingBox structureboundingbox) {
            super(worldgenfeaturestructurepiecetype, i, structureboundingbox);
            this.setOrientation(enumdirection);
        }

        protected r(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, int i, EnumDirection enumdirection, OceanMonumentPieces.v oceanmonumentpieces_v, int j, int k, int l) {
            super(worldgenfeaturestructurepiecetype, i, makeBoundingBox(enumdirection, oceanmonumentpieces_v, j, k, l));
            this.setOrientation(enumdirection);
            this.roomDefinition = oceanmonumentpieces_v;
        }

        private static StructureBoundingBox makeBoundingBox(EnumDirection enumdirection, OceanMonumentPieces.v oceanmonumentpieces_v, int i, int j, int k) {
            int l = oceanmonumentpieces_v.index;
            int i1 = l % 5;
            int j1 = l / 5 % 5;
            int k1 = l / 25;
            StructureBoundingBox structureboundingbox = makeBoundingBox(0, 0, 0, enumdirection, i * 8, j * 4, k * 8);

            switch (enumdirection) {
                case NORTH:
                    structureboundingbox.move(i1 * 8, k1 * 4, -(j1 + k) * 8 + 1);
                    break;
                case SOUTH:
                    structureboundingbox.move(i1 * 8, k1 * 4, j1 * 8);
                    break;
                case WEST:
                    structureboundingbox.move(-(j1 + k) * 8 + 1, k1 * 4, i1 * 8);
                    break;
                case EAST:
                default:
                    structureboundingbox.move(j1 * 8, k1 * 4, i1 * 8);
            }

            return structureboundingbox;
        }

        public r(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, NBTTagCompound nbttagcompound) {
            super(worldgenfeaturestructurepiecetype, nbttagcompound);
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound) {}

        protected void generateWaterBox(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, int i, int j, int k, int l, int i1, int j1) {
            for (int k1 = j; k1 <= i1; ++k1) {
                for (int l1 = i; l1 <= l; ++l1) {
                    for (int i2 = k; i2 <= j1; ++i2) {
                        IBlockData iblockdata = this.getBlock(generatoraccessseed, l1, k1, i2, structureboundingbox);

                        if (!OceanMonumentPieces.r.FILL_KEEP.contains(iblockdata.getBlock())) {
                            if (this.getWorldY(k1) >= generatoraccessseed.getSeaLevel() && iblockdata != OceanMonumentPieces.r.FILL_BLOCK) {
                                this.placeBlock(generatoraccessseed, Blocks.AIR.defaultBlockState(), l1, k1, i2, structureboundingbox);
                            } else {
                                this.placeBlock(generatoraccessseed, OceanMonumentPieces.r.FILL_BLOCK, l1, k1, i2, structureboundingbox);
                            }
                        }
                    }
                }
            }

        }

        protected void generateDefaultFloor(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, int i, int j, boolean flag) {
            if (flag) {
                this.generateBox(generatoraccessseed, structureboundingbox, i + 0, 0, j + 0, i + 2, 0, j + 8 - 1, OceanMonumentPieces.r.BASE_GRAY, OceanMonumentPieces.r.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, i + 5, 0, j + 0, i + 8 - 1, 0, j + 8 - 1, OceanMonumentPieces.r.BASE_GRAY, OceanMonumentPieces.r.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, i + 3, 0, j + 0, i + 4, 0, j + 2, OceanMonumentPieces.r.BASE_GRAY, OceanMonumentPieces.r.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, i + 3, 0, j + 5, i + 4, 0, j + 8 - 1, OceanMonumentPieces.r.BASE_GRAY, OceanMonumentPieces.r.BASE_GRAY, false);
                this.generateBox(generatoraccessseed, structureboundingbox, i + 3, 0, j + 2, i + 4, 0, j + 2, OceanMonumentPieces.r.BASE_LIGHT, OceanMonumentPieces.r.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, i + 3, 0, j + 5, i + 4, 0, j + 5, OceanMonumentPieces.r.BASE_LIGHT, OceanMonumentPieces.r.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, i + 2, 0, j + 3, i + 2, 0, j + 4, OceanMonumentPieces.r.BASE_LIGHT, OceanMonumentPieces.r.BASE_LIGHT, false);
                this.generateBox(generatoraccessseed, structureboundingbox, i + 5, 0, j + 3, i + 5, 0, j + 4, OceanMonumentPieces.r.BASE_LIGHT, OceanMonumentPieces.r.BASE_LIGHT, false);
            } else {
                this.generateBox(generatoraccessseed, structureboundingbox, i + 0, 0, j + 0, i + 8 - 1, 0, j + 8 - 1, OceanMonumentPieces.r.BASE_GRAY, OceanMonumentPieces.r.BASE_GRAY, false);
            }

        }

        protected void generateBoxOnFillOnly(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, int i, int j, int k, int l, int i1, int j1, IBlockData iblockdata) {
            for (int k1 = j; k1 <= i1; ++k1) {
                for (int l1 = i; l1 <= l; ++l1) {
                    for (int i2 = k; i2 <= j1; ++i2) {
                        if (this.getBlock(generatoraccessseed, l1, k1, i2, structureboundingbox) == OceanMonumentPieces.r.FILL_BLOCK) {
                            this.placeBlock(generatoraccessseed, iblockdata, l1, k1, i2, structureboundingbox);
                        }
                    }
                }
            }

        }

        protected boolean chunkIntersects(StructureBoundingBox structureboundingbox, int i, int j, int k, int l) {
            int i1 = this.getWorldX(i, j);
            int j1 = this.getWorldZ(i, j);
            int k1 = this.getWorldX(k, l);
            int l1 = this.getWorldZ(k, l);

            return structureboundingbox.intersects(Math.min(i1, k1), Math.min(j1, l1), Math.max(i1, k1), Math.max(j1, l1));
        }

        protected void spawnElder(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, int i, int j, int k) {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.getWorldPos(i, j, k);

            if (structureboundingbox.isInside(blockposition_mutableblockposition)) {
                EntityGuardianElder entityguardianelder = (EntityGuardianElder) EntityTypes.ELDER_GUARDIAN.create(generatoraccessseed.getLevel());

                if (entityguardianelder != null) {
                    entityguardianelder.heal(entityguardianelder.getMaxHealth());
                    entityguardianelder.moveTo((double) blockposition_mutableblockposition.getX() + 0.5D, (double) blockposition_mutableblockposition.getY(), (double) blockposition_mutableblockposition.getZ() + 0.5D, 0.0F, 0.0F);
                    entityguardianelder.finalizeSpawn(generatoraccessseed, generatoraccessseed.getCurrentDifficultyAt(entityguardianelder.blockPosition()), EnumMobSpawn.STRUCTURE, (GroupDataEntity) null, (NBTTagCompound) null);
                    generatoraccessseed.addFreshEntityWithPassengers(entityguardianelder);
                }
            }

        }
    }
}
