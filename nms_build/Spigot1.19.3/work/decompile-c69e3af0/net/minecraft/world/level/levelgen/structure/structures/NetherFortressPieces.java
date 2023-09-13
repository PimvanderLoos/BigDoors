package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.BlockFence;
import net.minecraft.world.level.block.BlockStairs;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityMobSpawner;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.WorldGenFeatureStructurePieceType;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.level.storage.loot.LootTables;

public class NetherFortressPieces {

    private static final int MAX_DEPTH = 30;
    private static final int LOWEST_Y_POSITION = 10;
    public static final int MAGIC_START_Y = 64;
    static final NetherFortressPieces.n[] BRIDGE_PIECE_WEIGHTS = new NetherFortressPieces.n[]{new NetherFortressPieces.n(NetherFortressPieces.c.class, 30, 0, true), new NetherFortressPieces.n(NetherFortressPieces.a.class, 10, 4), new NetherFortressPieces.n(NetherFortressPieces.o.class, 10, 4), new NetherFortressPieces.n(NetherFortressPieces.p.class, 10, 3), new NetherFortressPieces.n(NetherFortressPieces.l.class, 5, 2), new NetherFortressPieces.n(NetherFortressPieces.f.class, 5, 1)};
    static final NetherFortressPieces.n[] CASTLE_PIECE_WEIGHTS = new NetherFortressPieces.n[]{new NetherFortressPieces.n(NetherFortressPieces.i.class, 25, 0, true), new NetherFortressPieces.n(NetherFortressPieces.g.class, 15, 5), new NetherFortressPieces.n(NetherFortressPieces.j.class, 5, 10), new NetherFortressPieces.n(NetherFortressPieces.h.class, 5, 10), new NetherFortressPieces.n(NetherFortressPieces.d.class, 10, 3, true), new NetherFortressPieces.n(NetherFortressPieces.e.class, 7, 2), new NetherFortressPieces.n(NetherFortressPieces.k.class, 5, 2)};

    public NetherFortressPieces() {}

    static NetherFortressPieces.m findAndCreateBridgePieceFactory(NetherFortressPieces.n netherfortresspieces_n, StructurePieceAccessor structurepieceaccessor, RandomSource randomsource, int i, int j, int k, EnumDirection enumdirection, int l) {
        Class<? extends NetherFortressPieces.m> oclass = netherfortresspieces_n.pieceClass;
        Object object = null;

        if (oclass == NetherFortressPieces.c.class) {
            object = NetherFortressPieces.c.createPiece(structurepieceaccessor, randomsource, i, j, k, enumdirection, l);
        } else if (oclass == NetherFortressPieces.a.class) {
            object = NetherFortressPieces.a.createPiece(structurepieceaccessor, i, j, k, enumdirection, l);
        } else if (oclass == NetherFortressPieces.o.class) {
            object = NetherFortressPieces.o.createPiece(structurepieceaccessor, i, j, k, enumdirection, l);
        } else if (oclass == NetherFortressPieces.p.class) {
            object = NetherFortressPieces.p.createPiece(structurepieceaccessor, i, j, k, l, enumdirection);
        } else if (oclass == NetherFortressPieces.l.class) {
            object = NetherFortressPieces.l.createPiece(structurepieceaccessor, i, j, k, l, enumdirection);
        } else if (oclass == NetherFortressPieces.f.class) {
            object = NetherFortressPieces.f.createPiece(structurepieceaccessor, randomsource, i, j, k, enumdirection, l);
        } else if (oclass == NetherFortressPieces.i.class) {
            object = NetherFortressPieces.i.createPiece(structurepieceaccessor, i, j, k, enumdirection, l);
        } else if (oclass == NetherFortressPieces.j.class) {
            object = NetherFortressPieces.j.createPiece(structurepieceaccessor, randomsource, i, j, k, enumdirection, l);
        } else if (oclass == NetherFortressPieces.h.class) {
            object = NetherFortressPieces.h.createPiece(structurepieceaccessor, randomsource, i, j, k, enumdirection, l);
        } else if (oclass == NetherFortressPieces.d.class) {
            object = NetherFortressPieces.d.createPiece(structurepieceaccessor, i, j, k, enumdirection, l);
        } else if (oclass == NetherFortressPieces.e.class) {
            object = NetherFortressPieces.e.createPiece(structurepieceaccessor, i, j, k, enumdirection, l);
        } else if (oclass == NetherFortressPieces.g.class) {
            object = NetherFortressPieces.g.createPiece(structurepieceaccessor, i, j, k, enumdirection, l);
        } else if (oclass == NetherFortressPieces.k.class) {
            object = NetherFortressPieces.k.createPiece(structurepieceaccessor, i, j, k, enumdirection, l);
        }

        return (NetherFortressPieces.m) object;
    }

    private static class n {

        public final Class<? extends NetherFortressPieces.m> pieceClass;
        public final int weight;
        public int placeCount;
        public final int maxPlaceCount;
        public final boolean allowInRow;

        public n(Class<? extends NetherFortressPieces.m> oclass, int i, int j, boolean flag) {
            this.pieceClass = oclass;
            this.weight = i;
            this.maxPlaceCount = j;
            this.allowInRow = flag;
        }

        public n(Class<? extends NetherFortressPieces.m> oclass, int i, int j) {
            this(oclass, i, j, false);
        }

        public boolean doPlace(int i) {
            return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
        }

        public boolean isValid() {
            return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
        }
    }

    public static class c extends NetherFortressPieces.m {

        private static final int WIDTH = 5;
        private static final int HEIGHT = 10;
        private static final int DEPTH = 19;

        public c(int i, RandomSource randomsource, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(WorldGenFeatureStructurePieceType.NETHER_FORTRESS_BRIDGE_STRAIGHT, i, structureboundingbox);
            this.setOrientation(enumdirection);
        }

        public c(NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.NETHER_FORTRESS_BRIDGE_STRAIGHT, nbttagcompound);
        }

        @Override
        public void addChildren(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, RandomSource randomsource) {
            this.generateChildForward((NetherFortressPieces.q) structurepiece, structurepieceaccessor, randomsource, 1, 3, false);
        }

        public static NetherFortressPieces.c createPiece(StructurePieceAccessor structurepieceaccessor, RandomSource randomsource, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.orientBox(i, j, k, -1, -3, 0, 5, 10, 19, enumdirection);

            return isOkBox(structureboundingbox) && structurepieceaccessor.findCollisionPiece(structureboundingbox) == null ? new NetherFortressPieces.c(l, randomsource, structureboundingbox, enumdirection) : null;
        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 3, 0, 4, 4, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 5, 0, 3, 7, 18, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 5, 0, 0, 5, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 4, 5, 0, 4, 5, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 0, 4, 2, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 13, 4, 2, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 0, 0, 4, 1, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 0, 15, 4, 1, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

            for (int i = 0; i <= 4; ++i) {
                for (int j = 0; j <= 2; ++j) {
                    this.fillColumnDown(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, structureboundingbox);
                    this.fillColumnDown(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, 18 - j, structureboundingbox);
                }
            }

            IBlockData iblockdata = (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.NORTH, true)).setValue(BlockFence.SOUTH, true);
            IBlockData iblockdata1 = (IBlockData) iblockdata.setValue(BlockFence.EAST, true);
            IBlockData iblockdata2 = (IBlockData) iblockdata.setValue(BlockFence.WEST, true);

            this.generateBox(generatoraccessseed, structureboundingbox, 0, 1, 1, 0, 4, 1, iblockdata1, iblockdata1, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 3, 4, 0, 4, 4, iblockdata1, iblockdata1, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 3, 14, 0, 4, 14, iblockdata1, iblockdata1, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 1, 17, 0, 4, 17, iblockdata1, iblockdata1, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 4, 1, 1, 4, 4, 1, iblockdata2, iblockdata2, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 4, 3, 4, 4, 4, 4, iblockdata2, iblockdata2, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 4, 3, 14, 4, 4, 14, iblockdata2, iblockdata2, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 4, 1, 17, 4, 4, 17, iblockdata2, iblockdata2, false);
        }
    }

    public static class a extends NetherFortressPieces.m {

        private static final int WIDTH = 19;
        private static final int HEIGHT = 10;
        private static final int DEPTH = 19;

        public a(int i, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(WorldGenFeatureStructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, i, structureboundingbox);
            this.setOrientation(enumdirection);
        }

        protected a(int i, int j, EnumDirection enumdirection) {
            super(WorldGenFeatureStructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, 0, StructurePiece.makeBoundingBox(i, 64, j, enumdirection, 19, 10, 19));
            this.setOrientation(enumdirection);
        }

        protected a(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, NBTTagCompound nbttagcompound) {
            super(worldgenfeaturestructurepiecetype, nbttagcompound);
        }

        public a(NBTTagCompound nbttagcompound) {
            this(WorldGenFeatureStructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, nbttagcompound);
        }

        @Override
        public void addChildren(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, RandomSource randomsource) {
            this.generateChildForward((NetherFortressPieces.q) structurepiece, structurepieceaccessor, randomsource, 8, 3, false);
            this.generateChildLeft((NetherFortressPieces.q) structurepiece, structurepieceaccessor, randomsource, 3, 8, false);
            this.generateChildRight((NetherFortressPieces.q) structurepiece, structurepieceaccessor, randomsource, 3, 8, false);
        }

        public static NetherFortressPieces.a createPiece(StructurePieceAccessor structurepieceaccessor, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.orientBox(i, j, k, -8, -3, 0, 19, 10, 19, enumdirection);

            return isOkBox(structureboundingbox) && structurepieceaccessor.findCollisionPiece(structureboundingbox) == null ? new NetherFortressPieces.a(l, structureboundingbox, enumdirection) : null;
        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            this.generateBox(generatoraccessseed, structureboundingbox, 7, 3, 0, 11, 4, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 3, 7, 18, 4, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 8, 5, 0, 10, 7, 18, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 5, 8, 18, 7, 10, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 7, 5, 0, 7, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 7, 5, 11, 7, 5, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 11, 5, 0, 11, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 11, 5, 11, 11, 5, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 5, 7, 7, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 11, 5, 7, 18, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 5, 11, 7, 5, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 11, 5, 11, 18, 5, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 7, 2, 0, 11, 2, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 7, 2, 13, 11, 2, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 7, 0, 0, 11, 1, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 7, 0, 15, 11, 1, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

            int i;
            int j;

            for (i = 7; i <= 11; ++i) {
                for (j = 0; j <= 2; ++j) {
                    this.fillColumnDown(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, structureboundingbox);
                    this.fillColumnDown(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, 18 - j, structureboundingbox);
                }
            }

            this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 7, 5, 2, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 13, 2, 7, 18, 2, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 0, 7, 3, 1, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 15, 0, 7, 18, 1, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

            for (i = 0; i <= 2; ++i) {
                for (j = 7; j <= 11; ++j) {
                    this.fillColumnDown(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, structureboundingbox);
                    this.fillColumnDown(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), 18 - i, -1, j, structureboundingbox);
                }
            }

        }
    }

    public static class o extends NetherFortressPieces.m {

        private static final int WIDTH = 7;
        private static final int HEIGHT = 9;
        private static final int DEPTH = 7;

        public o(int i, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(WorldGenFeatureStructurePieceType.NETHER_FORTRESS_ROOM_CROSSING, i, structureboundingbox);
            this.setOrientation(enumdirection);
        }

        public o(NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.NETHER_FORTRESS_ROOM_CROSSING, nbttagcompound);
        }

        @Override
        public void addChildren(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, RandomSource randomsource) {
            this.generateChildForward((NetherFortressPieces.q) structurepiece, structurepieceaccessor, randomsource, 2, 0, false);
            this.generateChildLeft((NetherFortressPieces.q) structurepiece, structurepieceaccessor, randomsource, 0, 2, false);
            this.generateChildRight((NetherFortressPieces.q) structurepiece, structurepieceaccessor, randomsource, 0, 2, false);
        }

        public static NetherFortressPieces.o createPiece(StructurePieceAccessor structurepieceaccessor, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.orientBox(i, j, k, -2, 0, 0, 7, 9, 7, enumdirection);

            return isOkBox(structureboundingbox) && structurepieceaccessor.findCollisionPiece(structureboundingbox) == null ? new NetherFortressPieces.o(l, structureboundingbox, enumdirection) : null;
        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 0, 0, 6, 1, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 0, 6, 7, 6, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 0, 1, 6, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 6, 1, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 2, 0, 6, 6, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 2, 6, 6, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 0, 0, 6, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 5, 0, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 6, 2, 0, 6, 6, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 6, 2, 5, 6, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            IBlockData iblockdata = (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.WEST, true)).setValue(BlockFence.EAST, true);
            IBlockData iblockdata1 = (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.NORTH, true)).setValue(BlockFence.SOUTH, true);

            this.generateBox(generatoraccessseed, structureboundingbox, 2, 6, 0, 4, 6, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 5, 0, 4, 5, 0, iblockdata, iblockdata, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 6, 6, 4, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 5, 6, 4, 5, 6, iblockdata, iblockdata, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 6, 2, 0, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 5, 2, 0, 5, 4, iblockdata1, iblockdata1, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 6, 6, 2, 6, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 6, 5, 2, 6, 5, 4, iblockdata1, iblockdata1, false);

            for (int i = 0; i <= 6; ++i) {
                for (int j = 0; j <= 6; ++j) {
                    this.fillColumnDown(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, structureboundingbox);
                }
            }

        }
    }

    public static class p extends NetherFortressPieces.m {

        private static final int WIDTH = 7;
        private static final int HEIGHT = 11;
        private static final int DEPTH = 7;

        public p(int i, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(WorldGenFeatureStructurePieceType.NETHER_FORTRESS_STAIRS_ROOM, i, structureboundingbox);
            this.setOrientation(enumdirection);
        }

        public p(NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.NETHER_FORTRESS_STAIRS_ROOM, nbttagcompound);
        }

        @Override
        public void addChildren(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, RandomSource randomsource) {
            this.generateChildRight((NetherFortressPieces.q) structurepiece, structurepieceaccessor, randomsource, 6, 2, false);
        }

        public static NetherFortressPieces.p createPiece(StructurePieceAccessor structurepieceaccessor, int i, int j, int k, int l, EnumDirection enumdirection) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.orientBox(i, j, k, -2, 0, 0, 7, 11, 7, enumdirection);

            return isOkBox(structureboundingbox) && structurepieceaccessor.findCollisionPiece(structureboundingbox) == null ? new NetherFortressPieces.p(l, structureboundingbox, enumdirection) : null;
        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 0, 0, 6, 1, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 0, 6, 10, 6, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 0, 1, 8, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 2, 0, 6, 8, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 1, 0, 8, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 6, 2, 1, 6, 8, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 2, 6, 5, 8, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            IBlockData iblockdata = (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.WEST, true)).setValue(BlockFence.EAST, true);
            IBlockData iblockdata1 = (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.NORTH, true)).setValue(BlockFence.SOUTH, true);

            this.generateBox(generatoraccessseed, structureboundingbox, 0, 3, 2, 0, 5, 4, iblockdata1, iblockdata1, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 6, 3, 2, 6, 5, 2, iblockdata1, iblockdata1, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 6, 3, 4, 6, 5, 4, iblockdata1, iblockdata1, false);
            this.placeBlock(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), 5, 2, 5, structureboundingbox);
            this.generateBox(generatoraccessseed, structureboundingbox, 4, 2, 5, 4, 3, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 3, 2, 5, 3, 4, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 2, 5, 2, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 2, 5, 1, 6, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 7, 1, 5, 7, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 6, 8, 2, 6, 8, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 6, 0, 4, 8, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 5, 0, 4, 5, 0, iblockdata, iblockdata, false);

            for (int i = 0; i <= 6; ++i) {
                for (int j = 0; j <= 6; ++j) {
                    this.fillColumnDown(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, structureboundingbox);
                }
            }

        }
    }

    public static class l extends NetherFortressPieces.m {

        private static final int WIDTH = 7;
        private static final int HEIGHT = 8;
        private static final int DEPTH = 9;
        private boolean hasPlacedSpawner;

        public l(int i, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(WorldGenFeatureStructurePieceType.NETHER_FORTRESS_MONSTER_THRONE, i, structureboundingbox);
            this.setOrientation(enumdirection);
        }

        public l(NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.NETHER_FORTRESS_MONSTER_THRONE, nbttagcompound);
            this.hasPlacedSpawner = nbttagcompound.getBoolean("Mob");
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound) {
            super.addAdditionalSaveData(structurepieceserializationcontext, nbttagcompound);
            nbttagcompound.putBoolean("Mob", this.hasPlacedSpawner);
        }

        public static NetherFortressPieces.l createPiece(StructurePieceAccessor structurepieceaccessor, int i, int j, int k, int l, EnumDirection enumdirection) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.orientBox(i, j, k, -2, 0, 0, 7, 8, 9, enumdirection);

            return isOkBox(structureboundingbox) && structurepieceaccessor.findCollisionPiece(structureboundingbox) == null ? new NetherFortressPieces.l(l, structureboundingbox, enumdirection) : null;
        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 0, 6, 7, 7, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 0, 0, 5, 1, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 2, 1, 5, 2, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 3, 2, 5, 3, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 4, 3, 5, 4, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 2, 0, 1, 4, 2, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 2, 0, 5, 4, 2, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 5, 2, 1, 5, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 5, 2, 5, 5, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 5, 3, 0, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 6, 5, 3, 6, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 5, 8, 5, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            IBlockData iblockdata = (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.WEST, true)).setValue(BlockFence.EAST, true);
            IBlockData iblockdata1 = (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.NORTH, true)).setValue(BlockFence.SOUTH, true);

            this.placeBlock(generatoraccessseed, (IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.WEST, true), 1, 6, 3, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.EAST, true), 5, 6, 3, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.EAST, true)).setValue(BlockFence.NORTH, true), 0, 6, 3, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.WEST, true)).setValue(BlockFence.NORTH, true), 6, 6, 3, structureboundingbox);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 6, 4, 0, 6, 7, iblockdata1, iblockdata1, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 6, 6, 4, 6, 6, 7, iblockdata1, iblockdata1, false);
            this.placeBlock(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.EAST, true)).setValue(BlockFence.SOUTH, true), 0, 6, 8, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.WEST, true)).setValue(BlockFence.SOUTH, true), 6, 6, 8, structureboundingbox);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 6, 8, 5, 6, 8, iblockdata, iblockdata, false);
            this.placeBlock(generatoraccessseed, (IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.EAST, true), 1, 7, 8, structureboundingbox);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 7, 8, 4, 7, 8, iblockdata, iblockdata, false);
            this.placeBlock(generatoraccessseed, (IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.WEST, true), 5, 7, 8, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.EAST, true), 2, 8, 8, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata, 3, 8, 8, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.WEST, true), 4, 8, 8, structureboundingbox);
            if (!this.hasPlacedSpawner) {
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.getWorldPos(3, 5, 5);

                if (structureboundingbox.isInside(blockposition_mutableblockposition)) {
                    this.hasPlacedSpawner = true;
                    generatoraccessseed.setBlock(blockposition_mutableblockposition, Blocks.SPAWNER.defaultBlockState(), 2);
                    TileEntity tileentity = generatoraccessseed.getBlockEntity(blockposition_mutableblockposition);

                    if (tileentity instanceof TileEntityMobSpawner) {
                        TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner) tileentity;

                        tileentitymobspawner.setEntityId(EntityTypes.BLAZE, randomsource);
                    }
                }
            }

            for (int i = 0; i <= 6; ++i) {
                for (int j = 0; j <= 6; ++j) {
                    this.fillColumnDown(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, structureboundingbox);
                }
            }

        }
    }

    public static class f extends NetherFortressPieces.m {

        private static final int WIDTH = 13;
        private static final int HEIGHT = 14;
        private static final int DEPTH = 13;

        public f(int i, RandomSource randomsource, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(WorldGenFeatureStructurePieceType.NETHER_FORTRESS_CASTLE_ENTRANCE, i, structureboundingbox);
            this.setOrientation(enumdirection);
        }

        public f(NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.NETHER_FORTRESS_CASTLE_ENTRANCE, nbttagcompound);
        }

        @Override
        public void addChildren(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, RandomSource randomsource) {
            this.generateChildForward((NetherFortressPieces.q) structurepiece, structurepieceaccessor, randomsource, 5, 3, true);
        }

        public static NetherFortressPieces.f createPiece(StructurePieceAccessor structurepieceaccessor, RandomSource randomsource, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.orientBox(i, j, k, -5, -3, 0, 13, 14, 13, enumdirection);

            return isOkBox(structureboundingbox) && structurepieceaccessor.findCollisionPiece(structureboundingbox) == null ? new NetherFortressPieces.f(l, randomsource, structureboundingbox, enumdirection) : null;
        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 3, 0, 12, 4, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 5, 0, 12, 13, 12, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 5, 0, 1, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 11, 5, 0, 12, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 5, 11, 4, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 8, 5, 11, 10, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 9, 11, 7, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 5, 0, 4, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 8, 5, 0, 10, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 9, 0, 7, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 11, 2, 10, 12, 10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 8, 0, 7, 8, 0, Blocks.NETHER_BRICK_FENCE.defaultBlockState(), Blocks.NETHER_BRICK_FENCE.defaultBlockState(), false);
            IBlockData iblockdata = (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.WEST, true)).setValue(BlockFence.EAST, true);
            IBlockData iblockdata1 = (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.NORTH, true)).setValue(BlockFence.SOUTH, true);

            int i;

            for (i = 1; i <= 11; i += 2) {
                this.generateBox(generatoraccessseed, structureboundingbox, i, 10, 0, i, 11, 0, iblockdata, iblockdata, false);
                this.generateBox(generatoraccessseed, structureboundingbox, i, 10, 12, i, 11, 12, iblockdata, iblockdata, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 0, 10, i, 0, 11, i, iblockdata1, iblockdata1, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 12, 10, i, 12, 11, i, iblockdata1, iblockdata1, false);
                this.placeBlock(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), i, 13, 0, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), i, 13, 12, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), 0, 13, i, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), 12, 13, i, structureboundingbox);
                if (i != 11) {
                    this.placeBlock(generatoraccessseed, iblockdata, i + 1, 13, 0, structureboundingbox);
                    this.placeBlock(generatoraccessseed, iblockdata, i + 1, 13, 12, structureboundingbox);
                    this.placeBlock(generatoraccessseed, iblockdata1, 0, 13, i + 1, structureboundingbox);
                    this.placeBlock(generatoraccessseed, iblockdata1, 12, 13, i + 1, structureboundingbox);
                }
            }

            this.placeBlock(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.NORTH, true)).setValue(BlockFence.EAST, true), 0, 13, 0, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.SOUTH, true)).setValue(BlockFence.EAST, true), 0, 13, 12, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.SOUTH, true)).setValue(BlockFence.WEST, true), 12, 13, 12, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.NORTH, true)).setValue(BlockFence.WEST, true), 12, 13, 0, structureboundingbox);

            for (i = 3; i <= 9; i += 2) {
                this.generateBox(generatoraccessseed, structureboundingbox, 1, 7, i, 1, 8, i, (IBlockData) iblockdata1.setValue(BlockFence.WEST, true), (IBlockData) iblockdata1.setValue(BlockFence.WEST, true), false);
                this.generateBox(generatoraccessseed, structureboundingbox, 11, 7, i, 11, 8, i, (IBlockData) iblockdata1.setValue(BlockFence.EAST, true), (IBlockData) iblockdata1.setValue(BlockFence.EAST, true), false);
            }

            this.generateBox(generatoraccessseed, structureboundingbox, 4, 2, 0, 8, 2, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 4, 12, 2, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 4, 0, 0, 8, 1, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 4, 0, 9, 8, 1, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 0, 4, 3, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 9, 0, 4, 12, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

            int j;

            for (i = 4; i <= 8; ++i) {
                for (j = 0; j <= 2; ++j) {
                    this.fillColumnDown(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, structureboundingbox);
                    this.fillColumnDown(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, 12 - j, structureboundingbox);
                }
            }

            for (i = 0; i <= 2; ++i) {
                for (j = 4; j <= 8; ++j) {
                    this.fillColumnDown(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, structureboundingbox);
                    this.fillColumnDown(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), 12 - i, -1, j, structureboundingbox);
                }
            }

            this.generateBox(generatoraccessseed, structureboundingbox, 5, 5, 5, 7, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 6, 1, 6, 6, 4, 6, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.placeBlock(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), 6, 0, 6, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.LAVA.defaultBlockState(), 6, 5, 6, structureboundingbox);
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.getWorldPos(6, 5, 6);

            if (structureboundingbox.isInside(blockposition_mutableblockposition)) {
                generatoraccessseed.scheduleTick(blockposition_mutableblockposition, (FluidType) FluidTypes.LAVA, 0);
            }

        }
    }

    public static class i extends NetherFortressPieces.m {

        private static final int WIDTH = 5;
        private static final int HEIGHT = 7;
        private static final int DEPTH = 5;

        public i(int i, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(WorldGenFeatureStructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR, i, structureboundingbox);
            this.setOrientation(enumdirection);
        }

        public i(NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR, nbttagcompound);
        }

        @Override
        public void addChildren(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, RandomSource randomsource) {
            this.generateChildForward((NetherFortressPieces.q) structurepiece, structurepieceaccessor, randomsource, 1, 0, true);
        }

        public static NetherFortressPieces.i createPiece(StructurePieceAccessor structurepieceaccessor, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.orientBox(i, j, k, -1, 0, 0, 5, 7, 5, enumdirection);

            return isOkBox(structureboundingbox) && structurepieceaccessor.findCollisionPiece(structureboundingbox) == null ? new NetherFortressPieces.i(l, structureboundingbox, enumdirection) : null;
        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            IBlockData iblockdata = (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.NORTH, true)).setValue(BlockFence.SOUTH, true);

            this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 0, 0, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 4, 2, 0, 4, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 3, 1, 0, 4, 1, iblockdata, iblockdata, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 3, 3, 0, 4, 3, iblockdata, iblockdata, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 4, 3, 1, 4, 4, 1, iblockdata, iblockdata, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 4, 3, 3, 4, 4, 3, iblockdata, iblockdata, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

            for (int i = 0; i <= 4; ++i) {
                for (int j = 0; j <= 4; ++j) {
                    this.fillColumnDown(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, structureboundingbox);
                }
            }

        }
    }

    public static class j extends NetherFortressPieces.m {

        private static final int WIDTH = 5;
        private static final int HEIGHT = 7;
        private static final int DEPTH = 5;
        private boolean isNeedingChest;

        public j(int i, RandomSource randomsource, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(WorldGenFeatureStructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_RIGHT_TURN, i, structureboundingbox);
            this.setOrientation(enumdirection);
            this.isNeedingChest = randomsource.nextInt(3) == 0;
        }

        public j(NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_RIGHT_TURN, nbttagcompound);
            this.isNeedingChest = nbttagcompound.getBoolean("Chest");
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound) {
            super.addAdditionalSaveData(structurepieceserializationcontext, nbttagcompound);
            nbttagcompound.putBoolean("Chest", this.isNeedingChest);
        }

        @Override
        public void addChildren(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, RandomSource randomsource) {
            this.generateChildRight((NetherFortressPieces.q) structurepiece, structurepieceaccessor, randomsource, 0, 1, true);
        }

        public static NetherFortressPieces.j createPiece(StructurePieceAccessor structurepieceaccessor, RandomSource randomsource, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.orientBox(i, j, k, -1, 0, 0, 5, 7, 5, enumdirection);

            return isOkBox(structureboundingbox) && structurepieceaccessor.findCollisionPiece(structureboundingbox) == null ? new NetherFortressPieces.j(l, randomsource, structureboundingbox, enumdirection) : null;
        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            IBlockData iblockdata = (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.WEST, true)).setValue(BlockFence.EAST, true);
            IBlockData iblockdata1 = (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.NORTH, true)).setValue(BlockFence.SOUTH, true);

            this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 0, 0, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 3, 1, 0, 4, 1, iblockdata1, iblockdata1, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 3, 3, 0, 4, 3, iblockdata1, iblockdata1, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 4, 2, 0, 4, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 2, 4, 4, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 3, 4, 1, 4, 4, iblockdata, iblockdata, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 3, 3, 4, 3, 4, 4, iblockdata, iblockdata, false);
            if (this.isNeedingChest && structureboundingbox.isInside(this.getWorldPos(1, 2, 3))) {
                this.isNeedingChest = false;
                this.createChest(generatoraccessseed, structureboundingbox, randomsource, 1, 2, 3, LootTables.NETHER_BRIDGE);
            }

            this.generateBox(generatoraccessseed, structureboundingbox, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

            for (int i = 0; i <= 4; ++i) {
                for (int j = 0; j <= 4; ++j) {
                    this.fillColumnDown(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, structureboundingbox);
                }
            }

        }
    }

    public static class h extends NetherFortressPieces.m {

        private static final int WIDTH = 5;
        private static final int HEIGHT = 7;
        private static final int DEPTH = 5;
        private boolean isNeedingChest;

        public h(int i, RandomSource randomsource, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(WorldGenFeatureStructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_LEFT_TURN, i, structureboundingbox);
            this.setOrientation(enumdirection);
            this.isNeedingChest = randomsource.nextInt(3) == 0;
        }

        public h(NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_LEFT_TURN, nbttagcompound);
            this.isNeedingChest = nbttagcompound.getBoolean("Chest");
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound) {
            super.addAdditionalSaveData(structurepieceserializationcontext, nbttagcompound);
            nbttagcompound.putBoolean("Chest", this.isNeedingChest);
        }

        @Override
        public void addChildren(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, RandomSource randomsource) {
            this.generateChildLeft((NetherFortressPieces.q) structurepiece, structurepieceaccessor, randomsource, 0, 1, true);
        }

        public static NetherFortressPieces.h createPiece(StructurePieceAccessor structurepieceaccessor, RandomSource randomsource, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.orientBox(i, j, k, -1, 0, 0, 5, 7, 5, enumdirection);

            return isOkBox(structureboundingbox) && structurepieceaccessor.findCollisionPiece(structureboundingbox) == null ? new NetherFortressPieces.h(l, randomsource, structureboundingbox, enumdirection) : null;
        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            IBlockData iblockdata = (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.WEST, true)).setValue(BlockFence.EAST, true);
            IBlockData iblockdata1 = (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.NORTH, true)).setValue(BlockFence.SOUTH, true);

            this.generateBox(generatoraccessseed, structureboundingbox, 4, 2, 0, 4, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 4, 3, 1, 4, 4, 1, iblockdata1, iblockdata1, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 4, 3, 3, 4, 4, 3, iblockdata1, iblockdata1, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 0, 0, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 4, 3, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 3, 4, 1, 4, 4, iblockdata, iblockdata, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 3, 3, 4, 3, 4, 4, iblockdata, iblockdata, false);
            if (this.isNeedingChest && structureboundingbox.isInside(this.getWorldPos(3, 2, 3))) {
                this.isNeedingChest = false;
                this.createChest(generatoraccessseed, structureboundingbox, randomsource, 3, 2, 3, LootTables.NETHER_BRIDGE);
            }

            this.generateBox(generatoraccessseed, structureboundingbox, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

            for (int i = 0; i <= 4; ++i) {
                for (int j = 0; j <= 4; ++j) {
                    this.fillColumnDown(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, structureboundingbox);
                }
            }

        }
    }

    public static class d extends NetherFortressPieces.m {

        private static final int WIDTH = 5;
        private static final int HEIGHT = 14;
        private static final int DEPTH = 10;

        public d(int i, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(WorldGenFeatureStructurePieceType.NETHER_FORTRESS_CASTLE_CORRIDOR_STAIRS, i, structureboundingbox);
            this.setOrientation(enumdirection);
        }

        public d(NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.NETHER_FORTRESS_CASTLE_CORRIDOR_STAIRS, nbttagcompound);
        }

        @Override
        public void addChildren(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, RandomSource randomsource) {
            this.generateChildForward((NetherFortressPieces.q) structurepiece, structurepieceaccessor, randomsource, 1, 0, true);
        }

        public static NetherFortressPieces.d createPiece(StructurePieceAccessor structurepieceaccessor, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.orientBox(i, j, k, -1, -7, 0, 5, 14, 10, enumdirection);

            return isOkBox(structureboundingbox) && structurepieceaccessor.findCollisionPiece(structureboundingbox) == null ? new NetherFortressPieces.d(l, structureboundingbox, enumdirection) : null;
        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            IBlockData iblockdata = (IBlockData) Blocks.NETHER_BRICK_STAIRS.defaultBlockState().setValue(BlockStairs.FACING, EnumDirection.SOUTH);
            IBlockData iblockdata1 = (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.NORTH, true)).setValue(BlockFence.SOUTH, true);

            for (int i = 0; i <= 9; ++i) {
                int j = Math.max(1, 7 - i);
                int k = Math.min(Math.max(j + 5, 14 - i), 13);
                int l = i;

                this.generateBox(generatoraccessseed, structureboundingbox, 0, 0, i, 4, j, i, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
                this.generateBox(generatoraccessseed, structureboundingbox, 1, j + 1, i, 3, k - 1, i, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
                if (i <= 6) {
                    this.placeBlock(generatoraccessseed, iblockdata, 1, j + 1, i, structureboundingbox);
                    this.placeBlock(generatoraccessseed, iblockdata, 2, j + 1, i, structureboundingbox);
                    this.placeBlock(generatoraccessseed, iblockdata, 3, j + 1, i, structureboundingbox);
                }

                this.generateBox(generatoraccessseed, structureboundingbox, 0, k, i, 4, k, i, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
                this.generateBox(generatoraccessseed, structureboundingbox, 0, j + 1, i, 0, k - 1, i, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
                this.generateBox(generatoraccessseed, structureboundingbox, 4, j + 1, i, 4, k - 1, i, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
                if ((i & 1) == 0) {
                    this.generateBox(generatoraccessseed, structureboundingbox, 0, j + 2, i, 0, j + 3, i, iblockdata1, iblockdata1, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, 4, j + 2, i, 4, j + 3, i, iblockdata1, iblockdata1, false);
                }

                for (int i1 = 0; i1 <= 4; ++i1) {
                    this.fillColumnDown(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), i1, -1, l, structureboundingbox);
                }
            }

        }
    }

    public static class e extends NetherFortressPieces.m {

        private static final int WIDTH = 9;
        private static final int HEIGHT = 7;
        private static final int DEPTH = 9;

        public e(int i, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(WorldGenFeatureStructurePieceType.NETHER_FORTRESS_CASTLE_CORRIDOR_T_BALCONY, i, structureboundingbox);
            this.setOrientation(enumdirection);
        }

        public e(NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.NETHER_FORTRESS_CASTLE_CORRIDOR_T_BALCONY, nbttagcompound);
        }

        @Override
        public void addChildren(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, RandomSource randomsource) {
            byte b0 = 1;
            EnumDirection enumdirection = this.getOrientation();

            if (enumdirection == EnumDirection.WEST || enumdirection == EnumDirection.NORTH) {
                b0 = 5;
            }

            this.generateChildLeft((NetherFortressPieces.q) structurepiece, structurepieceaccessor, randomsource, 0, b0, randomsource.nextInt(8) > 0);
            this.generateChildRight((NetherFortressPieces.q) structurepiece, structurepieceaccessor, randomsource, 0, b0, randomsource.nextInt(8) > 0);
        }

        public static NetherFortressPieces.e createPiece(StructurePieceAccessor structurepieceaccessor, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.orientBox(i, j, k, -3, 0, 0, 9, 7, 9, enumdirection);

            return isOkBox(structureboundingbox) && structurepieceaccessor.findCollisionPiece(structureboundingbox) == null ? new NetherFortressPieces.e(l, structureboundingbox, enumdirection) : null;
        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            IBlockData iblockdata = (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.NORTH, true)).setValue(BlockFence.SOUTH, true);
            IBlockData iblockdata1 = (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.WEST, true)).setValue(BlockFence.EAST, true);

            this.generateBox(generatoraccessseed, structureboundingbox, 0, 0, 0, 8, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 0, 8, 5, 8, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 6, 0, 8, 6, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 0, 2, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 6, 2, 0, 8, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 3, 0, 1, 4, 0, iblockdata1, iblockdata1, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 7, 3, 0, 7, 4, 0, iblockdata1, iblockdata1, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 4, 8, 2, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 1, 4, 2, 2, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 6, 1, 4, 7, 2, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 3, 8, 7, 3, 8, iblockdata1, iblockdata1, false);
            this.placeBlock(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.EAST, true)).setValue(BlockFence.SOUTH, true), 0, 3, 8, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.WEST, true)).setValue(BlockFence.SOUTH, true), 8, 3, 8, structureboundingbox);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 3, 6, 0, 3, 7, iblockdata, iblockdata, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 8, 3, 6, 8, 3, 7, iblockdata, iblockdata, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 3, 4, 0, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 8, 3, 4, 8, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 3, 5, 2, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 6, 3, 5, 7, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 4, 5, 1, 5, 5, iblockdata1, iblockdata1, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 7, 4, 5, 7, 5, 5, iblockdata1, iblockdata1, false);

            for (int i = 0; i <= 5; ++i) {
                for (int j = 0; j <= 8; ++j) {
                    this.fillColumnDown(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), j, -1, i, structureboundingbox);
                }
            }

        }
    }

    public static class g extends NetherFortressPieces.m {

        private static final int WIDTH = 5;
        private static final int HEIGHT = 7;
        private static final int DEPTH = 5;

        public g(int i, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(WorldGenFeatureStructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_CROSSING, i, structureboundingbox);
            this.setOrientation(enumdirection);
        }

        public g(NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_CROSSING, nbttagcompound);
        }

        @Override
        public void addChildren(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, RandomSource randomsource) {
            this.generateChildForward((NetherFortressPieces.q) structurepiece, structurepieceaccessor, randomsource, 1, 0, true);
            this.generateChildLeft((NetherFortressPieces.q) structurepiece, structurepieceaccessor, randomsource, 0, 1, true);
            this.generateChildRight((NetherFortressPieces.q) structurepiece, structurepieceaccessor, randomsource, 0, 1, true);
        }

        public static NetherFortressPieces.g createPiece(StructurePieceAccessor structurepieceaccessor, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.orientBox(i, j, k, -1, 0, 0, 5, 7, 5, enumdirection);

            return isOkBox(structureboundingbox) && structurepieceaccessor.findCollisionPiece(structureboundingbox) == null ? new NetherFortressPieces.g(l, structureboundingbox, enumdirection) : null;
        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 0, 0, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 4, 2, 0, 4, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 4, 0, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 4, 2, 4, 4, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

            for (int i = 0; i <= 4; ++i) {
                for (int j = 0; j <= 4; ++j) {
                    this.fillColumnDown(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, structureboundingbox);
                }
            }

        }
    }

    public static class k extends NetherFortressPieces.m {

        private static final int WIDTH = 13;
        private static final int HEIGHT = 14;
        private static final int DEPTH = 13;

        public k(int i, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(WorldGenFeatureStructurePieceType.NETHER_FORTRESS_CASTLE_STALK_ROOM, i, structureboundingbox);
            this.setOrientation(enumdirection);
        }

        public k(NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.NETHER_FORTRESS_CASTLE_STALK_ROOM, nbttagcompound);
        }

        @Override
        public void addChildren(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, RandomSource randomsource) {
            this.generateChildForward((NetherFortressPieces.q) structurepiece, structurepieceaccessor, randomsource, 5, 3, true);
            this.generateChildForward((NetherFortressPieces.q) structurepiece, structurepieceaccessor, randomsource, 5, 11, true);
        }

        public static NetherFortressPieces.k createPiece(StructurePieceAccessor structurepieceaccessor, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.orientBox(i, j, k, -5, -3, 0, 13, 14, 13, enumdirection);

            return isOkBox(structureboundingbox) && structurepieceaccessor.findCollisionPiece(structureboundingbox) == null ? new NetherFortressPieces.k(l, structureboundingbox, enumdirection) : null;
        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 3, 0, 12, 4, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 5, 0, 12, 13, 12, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 5, 0, 1, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 11, 5, 0, 12, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 5, 11, 4, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 8, 5, 11, 10, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 9, 11, 7, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 5, 0, 4, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 8, 5, 0, 10, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 9, 0, 7, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 11, 2, 10, 12, 10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            IBlockData iblockdata = (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.WEST, true)).setValue(BlockFence.EAST, true);
            IBlockData iblockdata1 = (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.NORTH, true)).setValue(BlockFence.SOUTH, true);
            IBlockData iblockdata2 = (IBlockData) iblockdata1.setValue(BlockFence.WEST, true);
            IBlockData iblockdata3 = (IBlockData) iblockdata1.setValue(BlockFence.EAST, true);

            int i;

            for (i = 1; i <= 11; i += 2) {
                this.generateBox(generatoraccessseed, structureboundingbox, i, 10, 0, i, 11, 0, iblockdata, iblockdata, false);
                this.generateBox(generatoraccessseed, structureboundingbox, i, 10, 12, i, 11, 12, iblockdata, iblockdata, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 0, 10, i, 0, 11, i, iblockdata1, iblockdata1, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 12, 10, i, 12, 11, i, iblockdata1, iblockdata1, false);
                this.placeBlock(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), i, 13, 0, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), i, 13, 12, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), 0, 13, i, structureboundingbox);
                this.placeBlock(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), 12, 13, i, structureboundingbox);
                if (i != 11) {
                    this.placeBlock(generatoraccessseed, iblockdata, i + 1, 13, 0, structureboundingbox);
                    this.placeBlock(generatoraccessseed, iblockdata, i + 1, 13, 12, structureboundingbox);
                    this.placeBlock(generatoraccessseed, iblockdata1, 0, 13, i + 1, structureboundingbox);
                    this.placeBlock(generatoraccessseed, iblockdata1, 12, 13, i + 1, structureboundingbox);
                }
            }

            this.placeBlock(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.NORTH, true)).setValue(BlockFence.EAST, true), 0, 13, 0, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.SOUTH, true)).setValue(BlockFence.EAST, true), 0, 13, 12, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.SOUTH, true)).setValue(BlockFence.WEST, true), 12, 13, 12, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(BlockFence.NORTH, true)).setValue(BlockFence.WEST, true), 12, 13, 0, structureboundingbox);

            for (i = 3; i <= 9; i += 2) {
                this.generateBox(generatoraccessseed, structureboundingbox, 1, 7, i, 1, 8, i, iblockdata2, iblockdata2, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 11, 7, i, 11, 8, i, iblockdata3, iblockdata3, false);
            }

            IBlockData iblockdata4 = (IBlockData) Blocks.NETHER_BRICK_STAIRS.defaultBlockState().setValue(BlockStairs.FACING, EnumDirection.NORTH);

            int j;
            int k;

            for (j = 0; j <= 6; ++j) {
                int l = j + 4;

                for (k = 5; k <= 7; ++k) {
                    this.placeBlock(generatoraccessseed, iblockdata4, k, 5 + j, l, structureboundingbox);
                }

                if (l >= 5 && l <= 8) {
                    this.generateBox(generatoraccessseed, structureboundingbox, 5, 5, l, 7, j + 4, l, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
                } else if (l >= 9 && l <= 10) {
                    this.generateBox(generatoraccessseed, structureboundingbox, 5, 8, l, 7, j + 4, l, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
                }

                if (j >= 1) {
                    this.generateBox(generatoraccessseed, structureboundingbox, 5, 6 + j, l, 7, 9 + j, l, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
                }
            }

            for (j = 5; j <= 7; ++j) {
                this.placeBlock(generatoraccessseed, iblockdata4, j, 12, 11, structureboundingbox);
            }

            this.generateBox(generatoraccessseed, structureboundingbox, 5, 6, 7, 5, 7, 7, iblockdata3, iblockdata3, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 7, 6, 7, 7, 7, 7, iblockdata2, iblockdata2, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 13, 12, 7, 13, 12, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 5, 2, 3, 5, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 5, 9, 3, 5, 10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 5, 4, 2, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 9, 5, 2, 10, 5, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 9, 5, 9, 10, 5, 10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 10, 5, 4, 10, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            IBlockData iblockdata5 = (IBlockData) iblockdata4.setValue(BlockStairs.FACING, EnumDirection.EAST);
            IBlockData iblockdata6 = (IBlockData) iblockdata4.setValue(BlockStairs.FACING, EnumDirection.WEST);

            this.placeBlock(generatoraccessseed, iblockdata6, 4, 5, 2, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata6, 4, 5, 3, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata6, 4, 5, 9, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata6, 4, 5, 10, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata5, 8, 5, 2, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata5, 8, 5, 3, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata5, 8, 5, 9, structureboundingbox);
            this.placeBlock(generatoraccessseed, iblockdata5, 8, 5, 10, structureboundingbox);
            this.generateBox(generatoraccessseed, structureboundingbox, 3, 4, 4, 4, 4, 8, Blocks.SOUL_SAND.defaultBlockState(), Blocks.SOUL_SAND.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 8, 4, 4, 9, 4, 8, Blocks.SOUL_SAND.defaultBlockState(), Blocks.SOUL_SAND.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 3, 5, 4, 4, 5, 8, Blocks.NETHER_WART.defaultBlockState(), Blocks.NETHER_WART.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 8, 5, 4, 9, 5, 8, Blocks.NETHER_WART.defaultBlockState(), Blocks.NETHER_WART.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 4, 2, 0, 8, 2, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 2, 4, 12, 2, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 4, 0, 0, 8, 1, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 4, 0, 9, 8, 1, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 0, 4, 3, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 9, 0, 4, 12, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

            int i1;

            for (k = 4; k <= 8; ++k) {
                for (i1 = 0; i1 <= 2; ++i1) {
                    this.fillColumnDown(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), k, -1, i1, structureboundingbox);
                    this.fillColumnDown(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), k, -1, 12 - i1, structureboundingbox);
                }
            }

            for (k = 0; k <= 2; ++k) {
                for (i1 = 4; i1 <= 8; ++i1) {
                    this.fillColumnDown(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), k, -1, i1, structureboundingbox);
                    this.fillColumnDown(generatoraccessseed, Blocks.NETHER_BRICKS.defaultBlockState(), 12 - k, -1, i1, structureboundingbox);
                }
            }

        }
    }

    public static class b extends NetherFortressPieces.m {

        private static final int WIDTH = 5;
        private static final int HEIGHT = 10;
        private static final int DEPTH = 8;
        private final int selfSeed;

        public b(int i, RandomSource randomsource, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(WorldGenFeatureStructurePieceType.NETHER_FORTRESS_BRIDGE_END_FILLER, i, structureboundingbox);
            this.setOrientation(enumdirection);
            this.selfSeed = randomsource.nextInt();
        }

        public b(NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.NETHER_FORTRESS_BRIDGE_END_FILLER, nbttagcompound);
            this.selfSeed = nbttagcompound.getInt("Seed");
        }

        public static NetherFortressPieces.b createPiece(StructurePieceAccessor structurepieceaccessor, RandomSource randomsource, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.orientBox(i, j, k, -1, -3, 0, 5, 10, 8, enumdirection);

            return isOkBox(structureboundingbox) && structurepieceaccessor.findCollisionPiece(structureboundingbox) == null ? new NetherFortressPieces.b(l, randomsource, structureboundingbox, enumdirection) : null;
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound) {
            super.addAdditionalSaveData(structurepieceserializationcontext, nbttagcompound);
            nbttagcompound.putInt("Seed", this.selfSeed);
        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            RandomSource randomsource1 = RandomSource.create((long) this.selfSeed);

            int i;
            int j;
            int k;

            for (j = 0; j <= 4; ++j) {
                for (k = 3; k <= 4; ++k) {
                    i = randomsource1.nextInt(8);
                    this.generateBox(generatoraccessseed, structureboundingbox, j, k, 0, j, k, i, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
                }
            }

            j = randomsource1.nextInt(8);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 5, 0, 0, 5, j, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            j = randomsource1.nextInt(8);
            this.generateBox(generatoraccessseed, structureboundingbox, 4, 5, 0, 4, 5, j, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

            for (j = 0; j <= 4; ++j) {
                k = randomsource1.nextInt(5);
                this.generateBox(generatoraccessseed, structureboundingbox, j, 2, 0, j, 2, k, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            }

            for (j = 0; j <= 4; ++j) {
                for (k = 0; k <= 1; ++k) {
                    i = randomsource1.nextInt(3);
                    this.generateBox(generatoraccessseed, structureboundingbox, j, k, 0, j, k, i, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
                }
            }

        }
    }

    public static class q extends NetherFortressPieces.a {

        public NetherFortressPieces.n previousPiece;
        public List<NetherFortressPieces.n> availableBridgePieces;
        public List<NetherFortressPieces.n> availableCastlePieces;
        public final List<StructurePiece> pendingChildren = Lists.newArrayList();

        public q(RandomSource randomsource, int i, int j) {
            super(i, j, getRandomHorizontalDirection(randomsource));
            this.availableBridgePieces = Lists.newArrayList();
            NetherFortressPieces.n[] anetherfortresspieces_n = NetherFortressPieces.BRIDGE_PIECE_WEIGHTS;
            int k = anetherfortresspieces_n.length;

            NetherFortressPieces.n netherfortresspieces_n;
            int l;

            for (l = 0; l < k; ++l) {
                netherfortresspieces_n = anetherfortresspieces_n[l];
                netherfortresspieces_n.placeCount = 0;
                this.availableBridgePieces.add(netherfortresspieces_n);
            }

            this.availableCastlePieces = Lists.newArrayList();
            anetherfortresspieces_n = NetherFortressPieces.CASTLE_PIECE_WEIGHTS;
            k = anetherfortresspieces_n.length;

            for (l = 0; l < k; ++l) {
                netherfortresspieces_n = anetherfortresspieces_n[l];
                netherfortresspieces_n.placeCount = 0;
                this.availableCastlePieces.add(netherfortresspieces_n);
            }

        }

        public q(NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.NETHER_FORTRESS_START, nbttagcompound);
        }
    }

    private abstract static class m extends StructurePiece {

        protected m(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, int i, StructureBoundingBox structureboundingbox) {
            super(worldgenfeaturestructurepiecetype, i, structureboundingbox);
        }

        public m(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, NBTTagCompound nbttagcompound) {
            super(worldgenfeaturestructurepiecetype, nbttagcompound);
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound) {}

        private int updatePieceWeight(List<NetherFortressPieces.n> list) {
            boolean flag = false;
            int i = 0;

            NetherFortressPieces.n netherfortresspieces_n;

            for (Iterator iterator = list.iterator(); iterator.hasNext(); i += netherfortresspieces_n.weight) {
                netherfortresspieces_n = (NetherFortressPieces.n) iterator.next();
                if (netherfortresspieces_n.maxPlaceCount > 0 && netherfortresspieces_n.placeCount < netherfortresspieces_n.maxPlaceCount) {
                    flag = true;
                }
            }

            return flag ? i : -1;
        }

        private NetherFortressPieces.m generatePiece(NetherFortressPieces.q netherfortresspieces_q, List<NetherFortressPieces.n> list, StructurePieceAccessor structurepieceaccessor, RandomSource randomsource, int i, int j, int k, EnumDirection enumdirection, int l) {
            int i1 = this.updatePieceWeight(list);
            boolean flag = i1 > 0 && l <= 30;
            int j1 = 0;

            while (j1 < 5 && flag) {
                ++j1;
                int k1 = randomsource.nextInt(i1);
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    NetherFortressPieces.n netherfortresspieces_n = (NetherFortressPieces.n) iterator.next();

                    k1 -= netherfortresspieces_n.weight;
                    if (k1 < 0) {
                        if (!netherfortresspieces_n.doPlace(l) || netherfortresspieces_n == netherfortresspieces_q.previousPiece && !netherfortresspieces_n.allowInRow) {
                            break;
                        }

                        NetherFortressPieces.m netherfortresspieces_m = NetherFortressPieces.findAndCreateBridgePieceFactory(netherfortresspieces_n, structurepieceaccessor, randomsource, i, j, k, enumdirection, l);

                        if (netherfortresspieces_m != null) {
                            ++netherfortresspieces_n.placeCount;
                            netherfortresspieces_q.previousPiece = netherfortresspieces_n;
                            if (!netherfortresspieces_n.isValid()) {
                                list.remove(netherfortresspieces_n);
                            }

                            return netherfortresspieces_m;
                        }
                    }
                }
            }

            return NetherFortressPieces.b.createPiece(structurepieceaccessor, randomsource, i, j, k, enumdirection, l);
        }

        private StructurePiece generateAndAddPiece(NetherFortressPieces.q netherfortresspieces_q, StructurePieceAccessor structurepieceaccessor, RandomSource randomsource, int i, int j, int k, @Nullable EnumDirection enumdirection, int l, boolean flag) {
            if (Math.abs(i - netherfortresspieces_q.getBoundingBox().minX()) <= 112 && Math.abs(k - netherfortresspieces_q.getBoundingBox().minZ()) <= 112) {
                List<NetherFortressPieces.n> list = netherfortresspieces_q.availableBridgePieces;

                if (flag) {
                    list = netherfortresspieces_q.availableCastlePieces;
                }

                NetherFortressPieces.m netherfortresspieces_m = this.generatePiece(netherfortresspieces_q, list, structurepieceaccessor, randomsource, i, j, k, enumdirection, l + 1);

                if (netherfortresspieces_m != null) {
                    structurepieceaccessor.addPiece(netherfortresspieces_m);
                    netherfortresspieces_q.pendingChildren.add(netherfortresspieces_m);
                }

                return netherfortresspieces_m;
            } else {
                return NetherFortressPieces.b.createPiece(structurepieceaccessor, randomsource, i, j, k, enumdirection, l);
            }
        }

        @Nullable
        protected StructurePiece generateChildForward(NetherFortressPieces.q netherfortresspieces_q, StructurePieceAccessor structurepieceaccessor, RandomSource randomsource, int i, int j, boolean flag) {
            EnumDirection enumdirection = this.getOrientation();

            if (enumdirection != null) {
                switch (enumdirection) {
                    case NORTH:
                        return this.generateAndAddPiece(netherfortresspieces_q, structurepieceaccessor, randomsource, this.boundingBox.minX() + i, this.boundingBox.minY() + j, this.boundingBox.minZ() - 1, enumdirection, this.getGenDepth(), flag);
                    case SOUTH:
                        return this.generateAndAddPiece(netherfortresspieces_q, structurepieceaccessor, randomsource, this.boundingBox.minX() + i, this.boundingBox.minY() + j, this.boundingBox.maxZ() + 1, enumdirection, this.getGenDepth(), flag);
                    case WEST:
                        return this.generateAndAddPiece(netherfortresspieces_q, structurepieceaccessor, randomsource, this.boundingBox.minX() - 1, this.boundingBox.minY() + j, this.boundingBox.minZ() + i, enumdirection, this.getGenDepth(), flag);
                    case EAST:
                        return this.generateAndAddPiece(netherfortresspieces_q, structurepieceaccessor, randomsource, this.boundingBox.maxX() + 1, this.boundingBox.minY() + j, this.boundingBox.minZ() + i, enumdirection, this.getGenDepth(), flag);
                }
            }

            return null;
        }

        @Nullable
        protected StructurePiece generateChildLeft(NetherFortressPieces.q netherfortresspieces_q, StructurePieceAccessor structurepieceaccessor, RandomSource randomsource, int i, int j, boolean flag) {
            EnumDirection enumdirection = this.getOrientation();

            if (enumdirection != null) {
                switch (enumdirection) {
                    case NORTH:
                        return this.generateAndAddPiece(netherfortresspieces_q, structurepieceaccessor, randomsource, this.boundingBox.minX() - 1, this.boundingBox.minY() + i, this.boundingBox.minZ() + j, EnumDirection.WEST, this.getGenDepth(), flag);
                    case SOUTH:
                        return this.generateAndAddPiece(netherfortresspieces_q, structurepieceaccessor, randomsource, this.boundingBox.minX() - 1, this.boundingBox.minY() + i, this.boundingBox.minZ() + j, EnumDirection.WEST, this.getGenDepth(), flag);
                    case WEST:
                        return this.generateAndAddPiece(netherfortresspieces_q, structurepieceaccessor, randomsource, this.boundingBox.minX() + j, this.boundingBox.minY() + i, this.boundingBox.minZ() - 1, EnumDirection.NORTH, this.getGenDepth(), flag);
                    case EAST:
                        return this.generateAndAddPiece(netherfortresspieces_q, structurepieceaccessor, randomsource, this.boundingBox.minX() + j, this.boundingBox.minY() + i, this.boundingBox.minZ() - 1, EnumDirection.NORTH, this.getGenDepth(), flag);
                }
            }

            return null;
        }

        @Nullable
        protected StructurePiece generateChildRight(NetherFortressPieces.q netherfortresspieces_q, StructurePieceAccessor structurepieceaccessor, RandomSource randomsource, int i, int j, boolean flag) {
            EnumDirection enumdirection = this.getOrientation();

            if (enumdirection != null) {
                switch (enumdirection) {
                    case NORTH:
                        return this.generateAndAddPiece(netherfortresspieces_q, structurepieceaccessor, randomsource, this.boundingBox.maxX() + 1, this.boundingBox.minY() + i, this.boundingBox.minZ() + j, EnumDirection.EAST, this.getGenDepth(), flag);
                    case SOUTH:
                        return this.generateAndAddPiece(netherfortresspieces_q, structurepieceaccessor, randomsource, this.boundingBox.maxX() + 1, this.boundingBox.minY() + i, this.boundingBox.minZ() + j, EnumDirection.EAST, this.getGenDepth(), flag);
                    case WEST:
                        return this.generateAndAddPiece(netherfortresspieces_q, structurepieceaccessor, randomsource, this.boundingBox.minX() + j, this.boundingBox.minY() + i, this.boundingBox.maxZ() + 1, EnumDirection.SOUTH, this.getGenDepth(), flag);
                    case EAST:
                        return this.generateAndAddPiece(netherfortresspieces_q, structurepieceaccessor, randomsource, this.boundingBox.minX() + j, this.boundingBox.minY() + i, this.boundingBox.maxZ() + 1, EnumDirection.SOUTH, this.getGenDepth(), flag);
                }
            }

            return null;
        }

        protected static boolean isOkBox(StructureBoundingBox structureboundingbox) {
            return structureboundingbox != null && structureboundingbox.minY() > 10;
        }
    }
}
