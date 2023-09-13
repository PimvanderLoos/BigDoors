package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.BlockButtonAbstract;
import net.minecraft.world.level.block.BlockDoor;
import net.minecraft.world.level.block.BlockEnderPortalFrame;
import net.minecraft.world.level.block.BlockFence;
import net.minecraft.world.level.block.BlockIronBars;
import net.minecraft.world.level.block.BlockLadder;
import net.minecraft.world.level.block.BlockStairs;
import net.minecraft.world.level.block.BlockStepAbstract;
import net.minecraft.world.level.block.BlockTorchWall;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityMobSpawner;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockPropertyDoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.BlockPropertySlabType;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.NoiseEffect;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureStructurePieceType;
import net.minecraft.world.level.storage.loot.LootTables;

public class WorldGenStrongholdPieces {

    private static final int SMALL_DOOR_WIDTH = 3;
    private static final int SMALL_DOOR_HEIGHT = 3;
    private static final int MAX_DEPTH = 50;
    private static final int LOWEST_Y_POSITION = 10;
    private static final boolean CHECK_AIR = true;
    private static final WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight[] STRONGHOLD_PIECE_WEIGHTS = new WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight[]{new WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight(WorldGenStrongholdPieces.WorldGenStrongholdStairs.class, 40, 0), new WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight(WorldGenStrongholdPieces.WorldGenStrongholdPrison.class, 5, 5), new WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight(WorldGenStrongholdPieces.WorldGenStrongholdLeftTurn.class, 20, 0), new WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight(WorldGenStrongholdPieces.WorldGenStrongholdRightTurn.class, 20, 0), new WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight(WorldGenStrongholdPieces.WorldGenStrongholdRoomCrossing.class, 10, 6), new WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight(WorldGenStrongholdPieces.WorldGenStrongholdStairsStraight.class, 5, 5), new WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight(WorldGenStrongholdPieces.WorldGenStrongholdStairs2.class, 5, 5), new WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight(WorldGenStrongholdPieces.WorldGenStrongholdCrossing.class, 5, 4), new WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight(WorldGenStrongholdPieces.WorldGenStrongholdChestCorridor.class, 5, 4), new WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight(WorldGenStrongholdPieces.WorldGenStrongholdLibrary.class, 10, 2) {
                @Override
                public boolean a(int i) {
                    return super.a(i) && i > 4;
                }
            }, new WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight(WorldGenStrongholdPieces.WorldGenStrongholdPortalRoom.class, 20, 1) {
                @Override
                public boolean a(int i) {
                    return super.a(i) && i > 5;
                }
            }};
    private static List<WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight> currentPieces;
    static Class<? extends WorldGenStrongholdPieces.WorldGenStrongholdPiece> imposedPiece;
    private static int totalWeight;
    static final WorldGenStrongholdPieces.WorldGenStrongholdStones SMOOTH_STONE_SELECTOR = new WorldGenStrongholdPieces.WorldGenStrongholdStones();

    public WorldGenStrongholdPieces() {}

    public static void a() {
        WorldGenStrongholdPieces.currentPieces = Lists.newArrayList();
        WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight[] aworldgenstrongholdpieces_worldgenstrongholdpieceweight = WorldGenStrongholdPieces.STRONGHOLD_PIECE_WEIGHTS;
        int i = aworldgenstrongholdpieces_worldgenstrongholdpieceweight.length;

        for (int j = 0; j < i; ++j) {
            WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight worldgenstrongholdpieces_worldgenstrongholdpieceweight = aworldgenstrongholdpieces_worldgenstrongholdpieceweight[j];

            worldgenstrongholdpieces_worldgenstrongholdpieceweight.placeCount = 0;
            WorldGenStrongholdPieces.currentPieces.add(worldgenstrongholdpieces_worldgenstrongholdpieceweight);
        }

        WorldGenStrongholdPieces.imposedPiece = null;
    }

    private static boolean b() {
        boolean flag = false;

        WorldGenStrongholdPieces.totalWeight = 0;

        WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight worldgenstrongholdpieces_worldgenstrongholdpieceweight;

        for (Iterator iterator = WorldGenStrongholdPieces.currentPieces.iterator(); iterator.hasNext(); WorldGenStrongholdPieces.totalWeight += worldgenstrongholdpieces_worldgenstrongholdpieceweight.weight) {
            worldgenstrongholdpieces_worldgenstrongholdpieceweight = (WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight) iterator.next();
            if (worldgenstrongholdpieces_worldgenstrongholdpieceweight.maxPlaceCount > 0 && worldgenstrongholdpieces_worldgenstrongholdpieceweight.placeCount < worldgenstrongholdpieces_worldgenstrongholdpieceweight.maxPlaceCount) {
                flag = true;
            }
        }

        return flag;
    }

    private static WorldGenStrongholdPieces.WorldGenStrongholdPiece a(Class<? extends WorldGenStrongholdPieces.WorldGenStrongholdPiece> oclass, StructurePieceAccessor structurepieceaccessor, Random random, int i, int j, int k, @Nullable EnumDirection enumdirection, int l) {
        Object object = null;

        if (oclass == WorldGenStrongholdPieces.WorldGenStrongholdStairs.class) {
            object = WorldGenStrongholdPieces.WorldGenStrongholdStairs.a(structurepieceaccessor, random, i, j, k, enumdirection, l);
        } else if (oclass == WorldGenStrongholdPieces.WorldGenStrongholdPrison.class) {
            object = WorldGenStrongholdPieces.WorldGenStrongholdPrison.a(structurepieceaccessor, random, i, j, k, enumdirection, l);
        } else if (oclass == WorldGenStrongholdPieces.WorldGenStrongholdLeftTurn.class) {
            object = WorldGenStrongholdPieces.WorldGenStrongholdLeftTurn.a(structurepieceaccessor, random, i, j, k, enumdirection, l);
        } else if (oclass == WorldGenStrongholdPieces.WorldGenStrongholdRightTurn.class) {
            object = WorldGenStrongholdPieces.WorldGenStrongholdRightTurn.a(structurepieceaccessor, random, i, j, k, enumdirection, l);
        } else if (oclass == WorldGenStrongholdPieces.WorldGenStrongholdRoomCrossing.class) {
            object = WorldGenStrongholdPieces.WorldGenStrongholdRoomCrossing.a(structurepieceaccessor, random, i, j, k, enumdirection, l);
        } else if (oclass == WorldGenStrongholdPieces.WorldGenStrongholdStairsStraight.class) {
            object = WorldGenStrongholdPieces.WorldGenStrongholdStairsStraight.a(structurepieceaccessor, random, i, j, k, enumdirection, l);
        } else if (oclass == WorldGenStrongholdPieces.WorldGenStrongholdStairs2.class) {
            object = WorldGenStrongholdPieces.WorldGenStrongholdStairs2.a(structurepieceaccessor, random, i, j, k, enumdirection, l);
        } else if (oclass == WorldGenStrongholdPieces.WorldGenStrongholdCrossing.class) {
            object = WorldGenStrongholdPieces.WorldGenStrongholdCrossing.a(structurepieceaccessor, random, i, j, k, enumdirection, l);
        } else if (oclass == WorldGenStrongholdPieces.WorldGenStrongholdChestCorridor.class) {
            object = WorldGenStrongholdPieces.WorldGenStrongholdChestCorridor.a(structurepieceaccessor, random, i, j, k, enumdirection, l);
        } else if (oclass == WorldGenStrongholdPieces.WorldGenStrongholdLibrary.class) {
            object = WorldGenStrongholdPieces.WorldGenStrongholdLibrary.a(structurepieceaccessor, random, i, j, k, enumdirection, l);
        } else if (oclass == WorldGenStrongholdPieces.WorldGenStrongholdPortalRoom.class) {
            object = WorldGenStrongholdPieces.WorldGenStrongholdPortalRoom.a(structurepieceaccessor, i, j, k, enumdirection, l);
        }

        return (WorldGenStrongholdPieces.WorldGenStrongholdPiece) object;
    }

    private static WorldGenStrongholdPieces.WorldGenStrongholdPiece a(WorldGenStrongholdPieces.WorldGenStrongholdStart worldgenstrongholdpieces_worldgenstrongholdstart, StructurePieceAccessor structurepieceaccessor, Random random, int i, int j, int k, EnumDirection enumdirection, int l) {
        if (!b()) {
            return null;
        } else {
            if (WorldGenStrongholdPieces.imposedPiece != null) {
                WorldGenStrongholdPieces.WorldGenStrongholdPiece worldgenstrongholdpieces_worldgenstrongholdpiece = a(WorldGenStrongholdPieces.imposedPiece, structurepieceaccessor, random, i, j, k, enumdirection, l);

                WorldGenStrongholdPieces.imposedPiece = null;
                if (worldgenstrongholdpieces_worldgenstrongholdpiece != null) {
                    return worldgenstrongholdpieces_worldgenstrongholdpiece;
                }
            }

            int i1 = 0;

            while (i1 < 5) {
                ++i1;
                int j1 = random.nextInt(WorldGenStrongholdPieces.totalWeight);
                Iterator iterator = WorldGenStrongholdPieces.currentPieces.iterator();

                while (iterator.hasNext()) {
                    WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight worldgenstrongholdpieces_worldgenstrongholdpieceweight = (WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight) iterator.next();

                    j1 -= worldgenstrongholdpieces_worldgenstrongholdpieceweight.weight;
                    if (j1 < 0) {
                        if (!worldgenstrongholdpieces_worldgenstrongholdpieceweight.a(l) || worldgenstrongholdpieces_worldgenstrongholdpieceweight == worldgenstrongholdpieces_worldgenstrongholdstart.previousPiece) {
                            break;
                        }

                        WorldGenStrongholdPieces.WorldGenStrongholdPiece worldgenstrongholdpieces_worldgenstrongholdpiece1 = a(worldgenstrongholdpieces_worldgenstrongholdpieceweight.pieceClass, structurepieceaccessor, random, i, j, k, enumdirection, l);

                        if (worldgenstrongholdpieces_worldgenstrongholdpiece1 != null) {
                            ++worldgenstrongholdpieces_worldgenstrongholdpieceweight.placeCount;
                            worldgenstrongholdpieces_worldgenstrongholdstart.previousPiece = worldgenstrongholdpieces_worldgenstrongholdpieceweight;
                            if (!worldgenstrongholdpieces_worldgenstrongholdpieceweight.a()) {
                                WorldGenStrongholdPieces.currentPieces.remove(worldgenstrongholdpieces_worldgenstrongholdpieceweight);
                            }

                            return worldgenstrongholdpieces_worldgenstrongholdpiece1;
                        }
                    }
                }
            }

            StructureBoundingBox structureboundingbox = WorldGenStrongholdPieces.WorldGenStrongholdCorridor.a(structurepieceaccessor, random, i, j, k, enumdirection);

            if (structureboundingbox != null && structureboundingbox.h() > 1) {
                return new WorldGenStrongholdPieces.WorldGenStrongholdCorridor(l, structureboundingbox, enumdirection);
            } else {
                return null;
            }
        }
    }

    static StructurePiece b(WorldGenStrongholdPieces.WorldGenStrongholdStart worldgenstrongholdpieces_worldgenstrongholdstart, StructurePieceAccessor structurepieceaccessor, Random random, int i, int j, int k, @Nullable EnumDirection enumdirection, int l) {
        if (l > 50) {
            return null;
        } else if (Math.abs(i - worldgenstrongholdpieces_worldgenstrongholdstart.f().g()) <= 112 && Math.abs(k - worldgenstrongholdpieces_worldgenstrongholdstart.f().i()) <= 112) {
            WorldGenStrongholdPieces.WorldGenStrongholdPiece worldgenstrongholdpieces_worldgenstrongholdpiece = a(worldgenstrongholdpieces_worldgenstrongholdstart, structurepieceaccessor, random, i, j, k, enumdirection, l + 1);

            if (worldgenstrongholdpieces_worldgenstrongholdpiece != null) {
                structurepieceaccessor.a((StructurePiece) worldgenstrongholdpieces_worldgenstrongholdpiece);
                worldgenstrongholdpieces_worldgenstrongholdstart.pendingChildren.add(worldgenstrongholdpieces_worldgenstrongholdpiece);
            }

            return worldgenstrongholdpieces_worldgenstrongholdpiece;
        } else {
            return null;
        }
    }

    private static class WorldGenStrongholdPieceWeight {

        public final Class<? extends WorldGenStrongholdPieces.WorldGenStrongholdPiece> pieceClass;
        public final int weight;
        public int placeCount;
        public final int maxPlaceCount;

        public WorldGenStrongholdPieceWeight(Class<? extends WorldGenStrongholdPieces.WorldGenStrongholdPiece> oclass, int i, int j) {
            this.pieceClass = oclass;
            this.weight = i;
            this.maxPlaceCount = j;
        }

        public boolean a(int i) {
            return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
        }

        public boolean a() {
            return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
        }
    }

    public static class WorldGenStrongholdStairs extends WorldGenStrongholdPieces.WorldGenStrongholdPiece {

        private static final int WIDTH = 5;
        private static final int HEIGHT = 5;
        private static final int DEPTH = 7;
        private final boolean leftChild;
        private final boolean rightChild;

        public WorldGenStrongholdStairs(int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(WorldGenFeatureStructurePieceType.STRONGHOLD_STRAIGHT, i, structureboundingbox);
            this.a(enumdirection);
            this.entryDoor = this.a(random);
            this.leftChild = random.nextInt(2) == 0;
            this.rightChild = random.nextInt(2) == 0;
        }

        public WorldGenStrongholdStairs(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.STRONGHOLD_STRAIGHT, nbttagcompound);
            this.leftChild = nbttagcompound.getBoolean("Left");
            this.rightChild = nbttagcompound.getBoolean("Right");
        }

        @Override
        protected void a(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super.a(worldserver, nbttagcompound);
            nbttagcompound.setBoolean("Left", this.leftChild);
            nbttagcompound.setBoolean("Right", this.rightChild);
        }

        @Override
        public void a(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, Random random) {
            this.a((WorldGenStrongholdPieces.WorldGenStrongholdStart) structurepiece, structurepieceaccessor, random, 1, 1);
            if (this.leftChild) {
                this.b((WorldGenStrongholdPieces.WorldGenStrongholdStart) structurepiece, structurepieceaccessor, random, 1, 2);
            }

            if (this.rightChild) {
                this.c((WorldGenStrongholdPieces.WorldGenStrongholdStart) structurepiece, structurepieceaccessor, random, 1, 2);
            }

        }

        public static WorldGenStrongholdPieces.WorldGenStrongholdStairs a(StructurePieceAccessor structurepieceaccessor, Random random, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, -1, -1, 0, 5, 5, 7, enumdirection);

            return a(structureboundingbox) && structurepieceaccessor.a(structureboundingbox) == null ? new WorldGenStrongholdPieces.WorldGenStrongholdStairs(l, random, structureboundingbox, enumdirection) : null;
        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            this.a(generatoraccessseed, structureboundingbox, 0, 0, 0, 4, 4, 6, true, random, WorldGenStrongholdPieces.SMOOTH_STONE_SELECTOR);
            this.a(generatoraccessseed, random, structureboundingbox, this.entryDoor, 1, 1, 0);
            this.a(generatoraccessseed, random, structureboundingbox, WorldGenStrongholdPieces.WorldGenStrongholdPiece.WorldGenStrongholdDoorType.OPENING, 1, 1, 6);
            IBlockData iblockdata = (IBlockData) Blocks.WALL_TORCH.getBlockData().set(BlockTorchWall.FACING, EnumDirection.EAST);
            IBlockData iblockdata1 = (IBlockData) Blocks.WALL_TORCH.getBlockData().set(BlockTorchWall.FACING, EnumDirection.WEST);

            this.a(generatoraccessseed, structureboundingbox, random, 0.1F, 1, 2, 1, iblockdata);
            this.a(generatoraccessseed, structureboundingbox, random, 0.1F, 3, 2, 1, iblockdata1);
            this.a(generatoraccessseed, structureboundingbox, random, 0.1F, 1, 2, 5, iblockdata);
            this.a(generatoraccessseed, structureboundingbox, random, 0.1F, 3, 2, 5, iblockdata1);
            if (this.leftChild) {
                this.a(generatoraccessseed, structureboundingbox, 0, 1, 2, 0, 3, 4, WorldGenStrongholdPieces.WorldGenStrongholdStairs.CAVE_AIR, WorldGenStrongholdPieces.WorldGenStrongholdStairs.CAVE_AIR, false);
            }

            if (this.rightChild) {
                this.a(generatoraccessseed, structureboundingbox, 4, 1, 2, 4, 3, 4, WorldGenStrongholdPieces.WorldGenStrongholdStairs.CAVE_AIR, WorldGenStrongholdPieces.WorldGenStrongholdStairs.CAVE_AIR, false);
            }

            return true;
        }
    }

    public static class WorldGenStrongholdPrison extends WorldGenStrongholdPieces.WorldGenStrongholdPiece {

        protected static final int WIDTH = 9;
        protected static final int HEIGHT = 5;
        protected static final int DEPTH = 11;

        public WorldGenStrongholdPrison(int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(WorldGenFeatureStructurePieceType.STRONGHOLD_PRISON_HALL, i, structureboundingbox);
            this.a(enumdirection);
            this.entryDoor = this.a(random);
        }

        public WorldGenStrongholdPrison(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.STRONGHOLD_PRISON_HALL, nbttagcompound);
        }

        @Override
        public void a(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, Random random) {
            this.a((WorldGenStrongholdPieces.WorldGenStrongholdStart) structurepiece, structurepieceaccessor, random, 1, 1);
        }

        public static WorldGenStrongholdPieces.WorldGenStrongholdPrison a(StructurePieceAccessor structurepieceaccessor, Random random, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, -1, -1, 0, 9, 5, 11, enumdirection);

            return a(structureboundingbox) && structurepieceaccessor.a(structureboundingbox) == null ? new WorldGenStrongholdPieces.WorldGenStrongholdPrison(l, random, structureboundingbox, enumdirection) : null;
        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            this.a(generatoraccessseed, structureboundingbox, 0, 0, 0, 8, 4, 10, true, random, WorldGenStrongholdPieces.SMOOTH_STONE_SELECTOR);
            this.a(generatoraccessseed, random, structureboundingbox, this.entryDoor, 1, 1, 0);
            this.a(generatoraccessseed, structureboundingbox, 1, 1, 10, 3, 3, 10, WorldGenStrongholdPieces.WorldGenStrongholdPrison.CAVE_AIR, WorldGenStrongholdPieces.WorldGenStrongholdPrison.CAVE_AIR, false);
            this.a(generatoraccessseed, structureboundingbox, 4, 1, 1, 4, 3, 1, false, random, WorldGenStrongholdPieces.SMOOTH_STONE_SELECTOR);
            this.a(generatoraccessseed, structureboundingbox, 4, 1, 3, 4, 3, 3, false, random, WorldGenStrongholdPieces.SMOOTH_STONE_SELECTOR);
            this.a(generatoraccessseed, structureboundingbox, 4, 1, 7, 4, 3, 7, false, random, WorldGenStrongholdPieces.SMOOTH_STONE_SELECTOR);
            this.a(generatoraccessseed, structureboundingbox, 4, 1, 9, 4, 3, 9, false, random, WorldGenStrongholdPieces.SMOOTH_STONE_SELECTOR);

            for (int i = 1; i <= 3; ++i) {
                this.c(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.IRON_BARS.getBlockData().set(BlockIronBars.NORTH, true)).set(BlockIronBars.SOUTH, true), 4, i, 4, structureboundingbox);
                this.c(generatoraccessseed, (IBlockData) ((IBlockData) ((IBlockData) Blocks.IRON_BARS.getBlockData().set(BlockIronBars.NORTH, true)).set(BlockIronBars.SOUTH, true)).set(BlockIronBars.EAST, true), 4, i, 5, structureboundingbox);
                this.c(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.IRON_BARS.getBlockData().set(BlockIronBars.NORTH, true)).set(BlockIronBars.SOUTH, true), 4, i, 6, structureboundingbox);
                this.c(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.IRON_BARS.getBlockData().set(BlockIronBars.WEST, true)).set(BlockIronBars.EAST, true), 5, i, 5, structureboundingbox);
                this.c(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.IRON_BARS.getBlockData().set(BlockIronBars.WEST, true)).set(BlockIronBars.EAST, true), 6, i, 5, structureboundingbox);
                this.c(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.IRON_BARS.getBlockData().set(BlockIronBars.WEST, true)).set(BlockIronBars.EAST, true), 7, i, 5, structureboundingbox);
            }

            this.c(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.IRON_BARS.getBlockData().set(BlockIronBars.NORTH, true)).set(BlockIronBars.SOUTH, true), 4, 3, 2, structureboundingbox);
            this.c(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.IRON_BARS.getBlockData().set(BlockIronBars.NORTH, true)).set(BlockIronBars.SOUTH, true), 4, 3, 8, structureboundingbox);
            IBlockData iblockdata = (IBlockData) Blocks.IRON_DOOR.getBlockData().set(BlockDoor.FACING, EnumDirection.WEST);
            IBlockData iblockdata1 = (IBlockData) ((IBlockData) Blocks.IRON_DOOR.getBlockData().set(BlockDoor.FACING, EnumDirection.WEST)).set(BlockDoor.HALF, BlockPropertyDoubleBlockHalf.UPPER);

            this.c(generatoraccessseed, iblockdata, 4, 1, 2, structureboundingbox);
            this.c(generatoraccessseed, iblockdata1, 4, 2, 2, structureboundingbox);
            this.c(generatoraccessseed, iblockdata, 4, 1, 8, structureboundingbox);
            this.c(generatoraccessseed, iblockdata1, 4, 2, 8, structureboundingbox);
            return true;
        }
    }

    public static class WorldGenStrongholdLeftTurn extends WorldGenStrongholdPieces.q {

        public WorldGenStrongholdLeftTurn(int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(WorldGenFeatureStructurePieceType.STRONGHOLD_LEFT_TURN, i, structureboundingbox);
            this.a(enumdirection);
            this.entryDoor = this.a(random);
        }

        public WorldGenStrongholdLeftTurn(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.STRONGHOLD_LEFT_TURN, nbttagcompound);
        }

        @Override
        public void a(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, Random random) {
            EnumDirection enumdirection = this.h();

            if (enumdirection != EnumDirection.NORTH && enumdirection != EnumDirection.EAST) {
                this.c((WorldGenStrongholdPieces.WorldGenStrongholdStart) structurepiece, structurepieceaccessor, random, 1, 1);
            } else {
                this.b((WorldGenStrongholdPieces.WorldGenStrongholdStart) structurepiece, structurepieceaccessor, random, 1, 1);
            }

        }

        public static WorldGenStrongholdPieces.WorldGenStrongholdLeftTurn a(StructurePieceAccessor structurepieceaccessor, Random random, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, -1, -1, 0, 5, 5, 5, enumdirection);

            return a(structureboundingbox) && structurepieceaccessor.a(structureboundingbox) == null ? new WorldGenStrongholdPieces.WorldGenStrongholdLeftTurn(l, random, structureboundingbox, enumdirection) : null;
        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            this.a(generatoraccessseed, structureboundingbox, 0, 0, 0, 4, 4, 4, true, random, WorldGenStrongholdPieces.SMOOTH_STONE_SELECTOR);
            this.a(generatoraccessseed, random, structureboundingbox, this.entryDoor, 1, 1, 0);
            EnumDirection enumdirection = this.h();

            if (enumdirection != EnumDirection.NORTH && enumdirection != EnumDirection.EAST) {
                this.a(generatoraccessseed, structureboundingbox, 4, 1, 1, 4, 3, 3, WorldGenStrongholdPieces.WorldGenStrongholdLeftTurn.CAVE_AIR, WorldGenStrongholdPieces.WorldGenStrongholdLeftTurn.CAVE_AIR, false);
            } else {
                this.a(generatoraccessseed, structureboundingbox, 0, 1, 1, 0, 3, 3, WorldGenStrongholdPieces.WorldGenStrongholdLeftTurn.CAVE_AIR, WorldGenStrongholdPieces.WorldGenStrongholdLeftTurn.CAVE_AIR, false);
            }

            return true;
        }
    }

    public static class WorldGenStrongholdRightTurn extends WorldGenStrongholdPieces.q {

        public WorldGenStrongholdRightTurn(int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(WorldGenFeatureStructurePieceType.STRONGHOLD_RIGHT_TURN, i, structureboundingbox);
            this.a(enumdirection);
            this.entryDoor = this.a(random);
        }

        public WorldGenStrongholdRightTurn(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.STRONGHOLD_RIGHT_TURN, nbttagcompound);
        }

        @Override
        public void a(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, Random random) {
            EnumDirection enumdirection = this.h();

            if (enumdirection != EnumDirection.NORTH && enumdirection != EnumDirection.EAST) {
                this.b((WorldGenStrongholdPieces.WorldGenStrongholdStart) structurepiece, structurepieceaccessor, random, 1, 1);
            } else {
                this.c((WorldGenStrongholdPieces.WorldGenStrongholdStart) structurepiece, structurepieceaccessor, random, 1, 1);
            }

        }

        public static WorldGenStrongholdPieces.WorldGenStrongholdRightTurn a(StructurePieceAccessor structurepieceaccessor, Random random, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, -1, -1, 0, 5, 5, 5, enumdirection);

            return a(structureboundingbox) && structurepieceaccessor.a(structureboundingbox) == null ? new WorldGenStrongholdPieces.WorldGenStrongholdRightTurn(l, random, structureboundingbox, enumdirection) : null;
        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            this.a(generatoraccessseed, structureboundingbox, 0, 0, 0, 4, 4, 4, true, random, WorldGenStrongholdPieces.SMOOTH_STONE_SELECTOR);
            this.a(generatoraccessseed, random, structureboundingbox, this.entryDoor, 1, 1, 0);
            EnumDirection enumdirection = this.h();

            if (enumdirection != EnumDirection.NORTH && enumdirection != EnumDirection.EAST) {
                this.a(generatoraccessseed, structureboundingbox, 0, 1, 1, 0, 3, 3, WorldGenStrongholdPieces.WorldGenStrongholdRightTurn.CAVE_AIR, WorldGenStrongholdPieces.WorldGenStrongholdRightTurn.CAVE_AIR, false);
            } else {
                this.a(generatoraccessseed, structureboundingbox, 4, 1, 1, 4, 3, 3, WorldGenStrongholdPieces.WorldGenStrongholdRightTurn.CAVE_AIR, WorldGenStrongholdPieces.WorldGenStrongholdRightTurn.CAVE_AIR, false);
            }

            return true;
        }
    }

    public static class WorldGenStrongholdRoomCrossing extends WorldGenStrongholdPieces.WorldGenStrongholdPiece {

        protected static final int WIDTH = 11;
        protected static final int HEIGHT = 7;
        protected static final int DEPTH = 11;
        protected final int type;

        public WorldGenStrongholdRoomCrossing(int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(WorldGenFeatureStructurePieceType.STRONGHOLD_ROOM_CROSSING, i, structureboundingbox);
            this.a(enumdirection);
            this.entryDoor = this.a(random);
            this.type = random.nextInt(5);
        }

        public WorldGenStrongholdRoomCrossing(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.STRONGHOLD_ROOM_CROSSING, nbttagcompound);
            this.type = nbttagcompound.getInt("Type");
        }

        @Override
        protected void a(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super.a(worldserver, nbttagcompound);
            nbttagcompound.setInt("Type", this.type);
        }

        @Override
        public void a(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, Random random) {
            this.a((WorldGenStrongholdPieces.WorldGenStrongholdStart) structurepiece, structurepieceaccessor, random, 4, 1);
            this.b((WorldGenStrongholdPieces.WorldGenStrongholdStart) structurepiece, structurepieceaccessor, random, 1, 4);
            this.c((WorldGenStrongholdPieces.WorldGenStrongholdStart) structurepiece, structurepieceaccessor, random, 1, 4);
        }

        public static WorldGenStrongholdPieces.WorldGenStrongholdRoomCrossing a(StructurePieceAccessor structurepieceaccessor, Random random, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, -4, -1, 0, 11, 7, 11, enumdirection);

            return a(structureboundingbox) && structurepieceaccessor.a(structureboundingbox) == null ? new WorldGenStrongholdPieces.WorldGenStrongholdRoomCrossing(l, random, structureboundingbox, enumdirection) : null;
        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            this.a(generatoraccessseed, structureboundingbox, 0, 0, 0, 10, 6, 10, true, random, WorldGenStrongholdPieces.SMOOTH_STONE_SELECTOR);
            this.a(generatoraccessseed, random, structureboundingbox, this.entryDoor, 4, 1, 0);
            this.a(generatoraccessseed, structureboundingbox, 4, 1, 10, 6, 3, 10, WorldGenStrongholdPieces.WorldGenStrongholdRoomCrossing.CAVE_AIR, WorldGenStrongholdPieces.WorldGenStrongholdRoomCrossing.CAVE_AIR, false);
            this.a(generatoraccessseed, structureboundingbox, 0, 1, 4, 0, 3, 6, WorldGenStrongholdPieces.WorldGenStrongholdRoomCrossing.CAVE_AIR, WorldGenStrongholdPieces.WorldGenStrongholdRoomCrossing.CAVE_AIR, false);
            this.a(generatoraccessseed, structureboundingbox, 10, 1, 4, 10, 3, 6, WorldGenStrongholdPieces.WorldGenStrongholdRoomCrossing.CAVE_AIR, WorldGenStrongholdPieces.WorldGenStrongholdRoomCrossing.CAVE_AIR, false);
            int i;

            switch (this.type) {
                case 0:
                    this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 5, 1, 5, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 5, 2, 5, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 5, 3, 5, structureboundingbox);
                    this.c(generatoraccessseed, (IBlockData) Blocks.WALL_TORCH.getBlockData().set(BlockTorchWall.FACING, EnumDirection.WEST), 4, 3, 5, structureboundingbox);
                    this.c(generatoraccessseed, (IBlockData) Blocks.WALL_TORCH.getBlockData().set(BlockTorchWall.FACING, EnumDirection.EAST), 6, 3, 5, structureboundingbox);
                    this.c(generatoraccessseed, (IBlockData) Blocks.WALL_TORCH.getBlockData().set(BlockTorchWall.FACING, EnumDirection.SOUTH), 5, 3, 4, structureboundingbox);
                    this.c(generatoraccessseed, (IBlockData) Blocks.WALL_TORCH.getBlockData().set(BlockTorchWall.FACING, EnumDirection.NORTH), 5, 3, 6, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.SMOOTH_STONE_SLAB.getBlockData(), 4, 1, 4, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.SMOOTH_STONE_SLAB.getBlockData(), 4, 1, 5, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.SMOOTH_STONE_SLAB.getBlockData(), 4, 1, 6, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.SMOOTH_STONE_SLAB.getBlockData(), 6, 1, 4, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.SMOOTH_STONE_SLAB.getBlockData(), 6, 1, 5, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.SMOOTH_STONE_SLAB.getBlockData(), 6, 1, 6, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.SMOOTH_STONE_SLAB.getBlockData(), 5, 1, 4, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.SMOOTH_STONE_SLAB.getBlockData(), 5, 1, 6, structureboundingbox);
                    break;
                case 1:
                    for (i = 0; i < 5; ++i) {
                        this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 3, 1, 3 + i, structureboundingbox);
                        this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 7, 1, 3 + i, structureboundingbox);
                        this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 3 + i, 1, 3, structureboundingbox);
                        this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 3 + i, 1, 7, structureboundingbox);
                    }

                    this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 5, 1, 5, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 5, 2, 5, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 5, 3, 5, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.WATER.getBlockData(), 5, 4, 5, structureboundingbox);
                    break;
                case 2:
                    for (i = 1; i <= 9; ++i) {
                        this.c(generatoraccessseed, Blocks.COBBLESTONE.getBlockData(), 1, 3, i, structureboundingbox);
                        this.c(generatoraccessseed, Blocks.COBBLESTONE.getBlockData(), 9, 3, i, structureboundingbox);
                    }

                    for (i = 1; i <= 9; ++i) {
                        this.c(generatoraccessseed, Blocks.COBBLESTONE.getBlockData(), i, 3, 1, structureboundingbox);
                        this.c(generatoraccessseed, Blocks.COBBLESTONE.getBlockData(), i, 3, 9, structureboundingbox);
                    }

                    this.c(generatoraccessseed, Blocks.COBBLESTONE.getBlockData(), 5, 1, 4, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.COBBLESTONE.getBlockData(), 5, 1, 6, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.COBBLESTONE.getBlockData(), 5, 3, 4, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.COBBLESTONE.getBlockData(), 5, 3, 6, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.COBBLESTONE.getBlockData(), 4, 1, 5, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.COBBLESTONE.getBlockData(), 6, 1, 5, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.COBBLESTONE.getBlockData(), 4, 3, 5, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.COBBLESTONE.getBlockData(), 6, 3, 5, structureboundingbox);

                    for (i = 1; i <= 3; ++i) {
                        this.c(generatoraccessseed, Blocks.COBBLESTONE.getBlockData(), 4, i, 4, structureboundingbox);
                        this.c(generatoraccessseed, Blocks.COBBLESTONE.getBlockData(), 6, i, 4, structureboundingbox);
                        this.c(generatoraccessseed, Blocks.COBBLESTONE.getBlockData(), 4, i, 6, structureboundingbox);
                        this.c(generatoraccessseed, Blocks.COBBLESTONE.getBlockData(), 6, i, 6, structureboundingbox);
                    }

                    this.c(generatoraccessseed, Blocks.TORCH.getBlockData(), 5, 3, 5, structureboundingbox);

                    for (i = 2; i <= 8; ++i) {
                        this.c(generatoraccessseed, Blocks.OAK_PLANKS.getBlockData(), 2, 3, i, structureboundingbox);
                        this.c(generatoraccessseed, Blocks.OAK_PLANKS.getBlockData(), 3, 3, i, structureboundingbox);
                        if (i <= 3 || i >= 7) {
                            this.c(generatoraccessseed, Blocks.OAK_PLANKS.getBlockData(), 4, 3, i, structureboundingbox);
                            this.c(generatoraccessseed, Blocks.OAK_PLANKS.getBlockData(), 5, 3, i, structureboundingbox);
                            this.c(generatoraccessseed, Blocks.OAK_PLANKS.getBlockData(), 6, 3, i, structureboundingbox);
                        }

                        this.c(generatoraccessseed, Blocks.OAK_PLANKS.getBlockData(), 7, 3, i, structureboundingbox);
                        this.c(generatoraccessseed, Blocks.OAK_PLANKS.getBlockData(), 8, 3, i, structureboundingbox);
                    }

                    IBlockData iblockdata = (IBlockData) Blocks.LADDER.getBlockData().set(BlockLadder.FACING, EnumDirection.WEST);

                    this.c(generatoraccessseed, iblockdata, 9, 1, 3, structureboundingbox);
                    this.c(generatoraccessseed, iblockdata, 9, 2, 3, structureboundingbox);
                    this.c(generatoraccessseed, iblockdata, 9, 3, 3, structureboundingbox);
                    this.a(generatoraccessseed, structureboundingbox, random, 3, 4, 8, LootTables.STRONGHOLD_CROSSING);
            }

            return true;
        }
    }

    public static class WorldGenStrongholdStairsStraight extends WorldGenStrongholdPieces.WorldGenStrongholdPiece {

        private static final int WIDTH = 5;
        private static final int HEIGHT = 11;
        private static final int DEPTH = 8;

        public WorldGenStrongholdStairsStraight(int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(WorldGenFeatureStructurePieceType.STRONGHOLD_STRAIGHT_STAIRS_DOWN, i, structureboundingbox);
            this.a(enumdirection);
            this.entryDoor = this.a(random);
        }

        public WorldGenStrongholdStairsStraight(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.STRONGHOLD_STRAIGHT_STAIRS_DOWN, nbttagcompound);
        }

        @Override
        public void a(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, Random random) {
            this.a((WorldGenStrongholdPieces.WorldGenStrongholdStart) structurepiece, structurepieceaccessor, random, 1, 1);
        }

        public static WorldGenStrongholdPieces.WorldGenStrongholdStairsStraight a(StructurePieceAccessor structurepieceaccessor, Random random, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, -1, -7, 0, 5, 11, 8, enumdirection);

            return a(structureboundingbox) && structurepieceaccessor.a(structureboundingbox) == null ? new WorldGenStrongholdPieces.WorldGenStrongholdStairsStraight(l, random, structureboundingbox, enumdirection) : null;
        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            this.a(generatoraccessseed, structureboundingbox, 0, 0, 0, 4, 10, 7, true, random, WorldGenStrongholdPieces.SMOOTH_STONE_SELECTOR);
            this.a(generatoraccessseed, random, structureboundingbox, this.entryDoor, 1, 7, 0);
            this.a(generatoraccessseed, random, structureboundingbox, WorldGenStrongholdPieces.WorldGenStrongholdPiece.WorldGenStrongholdDoorType.OPENING, 1, 1, 7);
            IBlockData iblockdata = (IBlockData) Blocks.COBBLESTONE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.SOUTH);

            for (int i = 0; i < 6; ++i) {
                this.c(generatoraccessseed, iblockdata, 1, 6 - i, 1 + i, structureboundingbox);
                this.c(generatoraccessseed, iblockdata, 2, 6 - i, 1 + i, structureboundingbox);
                this.c(generatoraccessseed, iblockdata, 3, 6 - i, 1 + i, structureboundingbox);
                if (i < 5) {
                    this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 1, 5 - i, 1 + i, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 2, 5 - i, 1 + i, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 3, 5 - i, 1 + i, structureboundingbox);
                }
            }

            return true;
        }
    }

    public static class WorldGenStrongholdStairs2 extends WorldGenStrongholdPieces.WorldGenStrongholdPiece {

        private static final int WIDTH = 5;
        private static final int HEIGHT = 11;
        private static final int DEPTH = 5;
        private final boolean isSource;

        public WorldGenStrongholdStairs2(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, int i, int j, int k, EnumDirection enumdirection) {
            super(worldgenfeaturestructurepiecetype, i, a(j, 64, k, enumdirection, 5, 11, 5));
            this.isSource = true;
            this.a(enumdirection);
            this.entryDoor = WorldGenStrongholdPieces.WorldGenStrongholdPiece.WorldGenStrongholdDoorType.OPENING;
        }

        public WorldGenStrongholdStairs2(int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(WorldGenFeatureStructurePieceType.STRONGHOLD_STAIRS_DOWN, i, structureboundingbox);
            this.isSource = false;
            this.a(enumdirection);
            this.entryDoor = this.a(random);
        }

        public WorldGenStrongholdStairs2(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, NBTTagCompound nbttagcompound) {
            super(worldgenfeaturestructurepiecetype, nbttagcompound);
            this.isSource = nbttagcompound.getBoolean("Source");
        }

        public WorldGenStrongholdStairs2(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            this(WorldGenFeatureStructurePieceType.STRONGHOLD_STAIRS_DOWN, nbttagcompound);
        }

        @Override
        protected void a(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super.a(worldserver, nbttagcompound);
            nbttagcompound.setBoolean("Source", this.isSource);
        }

        @Override
        public void a(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, Random random) {
            if (this.isSource) {
                WorldGenStrongholdPieces.imposedPiece = WorldGenStrongholdPieces.WorldGenStrongholdCrossing.class;
            }

            this.a((WorldGenStrongholdPieces.WorldGenStrongholdStart) structurepiece, structurepieceaccessor, random, 1, 1);
        }

        public static WorldGenStrongholdPieces.WorldGenStrongholdStairs2 a(StructurePieceAccessor structurepieceaccessor, Random random, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, -1, -7, 0, 5, 11, 5, enumdirection);

            return a(structureboundingbox) && structurepieceaccessor.a(structureboundingbox) == null ? new WorldGenStrongholdPieces.WorldGenStrongholdStairs2(l, random, structureboundingbox, enumdirection) : null;
        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            this.a(generatoraccessseed, structureboundingbox, 0, 0, 0, 4, 10, 4, true, random, WorldGenStrongholdPieces.SMOOTH_STONE_SELECTOR);
            this.a(generatoraccessseed, random, structureboundingbox, this.entryDoor, 1, 7, 0);
            this.a(generatoraccessseed, random, structureboundingbox, WorldGenStrongholdPieces.WorldGenStrongholdPiece.WorldGenStrongholdDoorType.OPENING, 1, 1, 4);
            this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 2, 6, 1, structureboundingbox);
            this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 1, 5, 1, structureboundingbox);
            this.c(generatoraccessseed, Blocks.SMOOTH_STONE_SLAB.getBlockData(), 1, 6, 1, structureboundingbox);
            this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 1, 5, 2, structureboundingbox);
            this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 1, 4, 3, structureboundingbox);
            this.c(generatoraccessseed, Blocks.SMOOTH_STONE_SLAB.getBlockData(), 1, 5, 3, structureboundingbox);
            this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 2, 4, 3, structureboundingbox);
            this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 3, 3, 3, structureboundingbox);
            this.c(generatoraccessseed, Blocks.SMOOTH_STONE_SLAB.getBlockData(), 3, 4, 3, structureboundingbox);
            this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 3, 3, 2, structureboundingbox);
            this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 3, 2, 1, structureboundingbox);
            this.c(generatoraccessseed, Blocks.SMOOTH_STONE_SLAB.getBlockData(), 3, 3, 1, structureboundingbox);
            this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 2, 2, 1, structureboundingbox);
            this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 1, 1, 1, structureboundingbox);
            this.c(generatoraccessseed, Blocks.SMOOTH_STONE_SLAB.getBlockData(), 1, 2, 1, structureboundingbox);
            this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 1, 1, 2, structureboundingbox);
            this.c(generatoraccessseed, Blocks.SMOOTH_STONE_SLAB.getBlockData(), 1, 1, 3, structureboundingbox);
            return true;
        }
    }

    public static class WorldGenStrongholdCrossing extends WorldGenStrongholdPieces.WorldGenStrongholdPiece {

        protected static final int WIDTH = 10;
        protected static final int HEIGHT = 9;
        protected static final int DEPTH = 11;
        private final boolean leftLow;
        private final boolean leftHigh;
        private final boolean rightLow;
        private final boolean rightHigh;

        public WorldGenStrongholdCrossing(int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(WorldGenFeatureStructurePieceType.STRONGHOLD_FIVE_CROSSING, i, structureboundingbox);
            this.a(enumdirection);
            this.entryDoor = this.a(random);
            this.leftLow = random.nextBoolean();
            this.leftHigh = random.nextBoolean();
            this.rightLow = random.nextBoolean();
            this.rightHigh = random.nextInt(3) > 0;
        }

        public WorldGenStrongholdCrossing(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.STRONGHOLD_FIVE_CROSSING, nbttagcompound);
            this.leftLow = nbttagcompound.getBoolean("leftLow");
            this.leftHigh = nbttagcompound.getBoolean("leftHigh");
            this.rightLow = nbttagcompound.getBoolean("rightLow");
            this.rightHigh = nbttagcompound.getBoolean("rightHigh");
        }

        @Override
        protected void a(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super.a(worldserver, nbttagcompound);
            nbttagcompound.setBoolean("leftLow", this.leftLow);
            nbttagcompound.setBoolean("leftHigh", this.leftHigh);
            nbttagcompound.setBoolean("rightLow", this.rightLow);
            nbttagcompound.setBoolean("rightHigh", this.rightHigh);
        }

        @Override
        public void a(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, Random random) {
            int i = 3;
            int j = 5;
            EnumDirection enumdirection = this.h();

            if (enumdirection == EnumDirection.WEST || enumdirection == EnumDirection.NORTH) {
                i = 8 - i;
                j = 8 - j;
            }

            this.a((WorldGenStrongholdPieces.WorldGenStrongholdStart) structurepiece, structurepieceaccessor, random, 5, 1);
            if (this.leftLow) {
                this.b((WorldGenStrongholdPieces.WorldGenStrongholdStart) structurepiece, structurepieceaccessor, random, i, 1);
            }

            if (this.leftHigh) {
                this.b((WorldGenStrongholdPieces.WorldGenStrongholdStart) structurepiece, structurepieceaccessor, random, j, 7);
            }

            if (this.rightLow) {
                this.c((WorldGenStrongholdPieces.WorldGenStrongholdStart) structurepiece, structurepieceaccessor, random, i, 1);
            }

            if (this.rightHigh) {
                this.c((WorldGenStrongholdPieces.WorldGenStrongholdStart) structurepiece, structurepieceaccessor, random, j, 7);
            }

        }

        public static WorldGenStrongholdPieces.WorldGenStrongholdCrossing a(StructurePieceAccessor structurepieceaccessor, Random random, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, -4, -3, 0, 10, 9, 11, enumdirection);

            return a(structureboundingbox) && structurepieceaccessor.a(structureboundingbox) == null ? new WorldGenStrongholdPieces.WorldGenStrongholdCrossing(l, random, structureboundingbox, enumdirection) : null;
        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            this.a(generatoraccessseed, structureboundingbox, 0, 0, 0, 9, 8, 10, true, random, WorldGenStrongholdPieces.SMOOTH_STONE_SELECTOR);
            this.a(generatoraccessseed, random, structureboundingbox, this.entryDoor, 4, 3, 0);
            if (this.leftLow) {
                this.a(generatoraccessseed, structureboundingbox, 0, 3, 1, 0, 5, 3, WorldGenStrongholdPieces.WorldGenStrongholdCrossing.CAVE_AIR, WorldGenStrongholdPieces.WorldGenStrongholdCrossing.CAVE_AIR, false);
            }

            if (this.rightLow) {
                this.a(generatoraccessseed, structureboundingbox, 9, 3, 1, 9, 5, 3, WorldGenStrongholdPieces.WorldGenStrongholdCrossing.CAVE_AIR, WorldGenStrongholdPieces.WorldGenStrongholdCrossing.CAVE_AIR, false);
            }

            if (this.leftHigh) {
                this.a(generatoraccessseed, structureboundingbox, 0, 5, 7, 0, 7, 9, WorldGenStrongholdPieces.WorldGenStrongholdCrossing.CAVE_AIR, WorldGenStrongholdPieces.WorldGenStrongholdCrossing.CAVE_AIR, false);
            }

            if (this.rightHigh) {
                this.a(generatoraccessseed, structureboundingbox, 9, 5, 7, 9, 7, 9, WorldGenStrongholdPieces.WorldGenStrongholdCrossing.CAVE_AIR, WorldGenStrongholdPieces.WorldGenStrongholdCrossing.CAVE_AIR, false);
            }

            this.a(generatoraccessseed, structureboundingbox, 5, 1, 10, 7, 3, 10, WorldGenStrongholdPieces.WorldGenStrongholdCrossing.CAVE_AIR, WorldGenStrongholdPieces.WorldGenStrongholdCrossing.CAVE_AIR, false);
            this.a(generatoraccessseed, structureboundingbox, 1, 2, 1, 8, 2, 6, false, random, WorldGenStrongholdPieces.SMOOTH_STONE_SELECTOR);
            this.a(generatoraccessseed, structureboundingbox, 4, 1, 5, 4, 4, 9, false, random, WorldGenStrongholdPieces.SMOOTH_STONE_SELECTOR);
            this.a(generatoraccessseed, structureboundingbox, 8, 1, 5, 8, 4, 9, false, random, WorldGenStrongholdPieces.SMOOTH_STONE_SELECTOR);
            this.a(generatoraccessseed, structureboundingbox, 1, 4, 7, 3, 4, 9, false, random, WorldGenStrongholdPieces.SMOOTH_STONE_SELECTOR);
            this.a(generatoraccessseed, structureboundingbox, 1, 3, 5, 3, 3, 6, false, random, WorldGenStrongholdPieces.SMOOTH_STONE_SELECTOR);
            this.a(generatoraccessseed, structureboundingbox, 1, 3, 4, 3, 3, 4, Blocks.SMOOTH_STONE_SLAB.getBlockData(), Blocks.SMOOTH_STONE_SLAB.getBlockData(), false);
            this.a(generatoraccessseed, structureboundingbox, 1, 4, 6, 3, 4, 6, Blocks.SMOOTH_STONE_SLAB.getBlockData(), Blocks.SMOOTH_STONE_SLAB.getBlockData(), false);
            this.a(generatoraccessseed, structureboundingbox, 5, 1, 7, 7, 1, 8, false, random, WorldGenStrongholdPieces.SMOOTH_STONE_SELECTOR);
            this.a(generatoraccessseed, structureboundingbox, 5, 1, 9, 7, 1, 9, Blocks.SMOOTH_STONE_SLAB.getBlockData(), Blocks.SMOOTH_STONE_SLAB.getBlockData(), false);
            this.a(generatoraccessseed, structureboundingbox, 5, 2, 7, 7, 2, 7, Blocks.SMOOTH_STONE_SLAB.getBlockData(), Blocks.SMOOTH_STONE_SLAB.getBlockData(), false);
            this.a(generatoraccessseed, structureboundingbox, 4, 5, 7, 4, 5, 9, Blocks.SMOOTH_STONE_SLAB.getBlockData(), Blocks.SMOOTH_STONE_SLAB.getBlockData(), false);
            this.a(generatoraccessseed, structureboundingbox, 8, 5, 7, 8, 5, 9, Blocks.SMOOTH_STONE_SLAB.getBlockData(), Blocks.SMOOTH_STONE_SLAB.getBlockData(), false);
            this.a(generatoraccessseed, structureboundingbox, 5, 5, 7, 7, 5, 9, (IBlockData) Blocks.SMOOTH_STONE_SLAB.getBlockData().set(BlockStepAbstract.TYPE, BlockPropertySlabType.DOUBLE), (IBlockData) Blocks.SMOOTH_STONE_SLAB.getBlockData().set(BlockStepAbstract.TYPE, BlockPropertySlabType.DOUBLE), false);
            this.c(generatoraccessseed, (IBlockData) Blocks.WALL_TORCH.getBlockData().set(BlockTorchWall.FACING, EnumDirection.SOUTH), 6, 5, 6, structureboundingbox);
            return true;
        }
    }

    public static class WorldGenStrongholdChestCorridor extends WorldGenStrongholdPieces.WorldGenStrongholdPiece {

        private static final int WIDTH = 5;
        private static final int HEIGHT = 5;
        private static final int DEPTH = 7;
        private boolean hasPlacedChest;

        public WorldGenStrongholdChestCorridor(int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(WorldGenFeatureStructurePieceType.STRONGHOLD_CHEST_CORRIDOR, i, structureboundingbox);
            this.a(enumdirection);
            this.entryDoor = this.a(random);
        }

        public WorldGenStrongholdChestCorridor(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.STRONGHOLD_CHEST_CORRIDOR, nbttagcompound);
            this.hasPlacedChest = nbttagcompound.getBoolean("Chest");
        }

        @Override
        protected void a(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super.a(worldserver, nbttagcompound);
            nbttagcompound.setBoolean("Chest", this.hasPlacedChest);
        }

        @Override
        public void a(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, Random random) {
            this.a((WorldGenStrongholdPieces.WorldGenStrongholdStart) structurepiece, structurepieceaccessor, random, 1, 1);
        }

        public static WorldGenStrongholdPieces.WorldGenStrongholdChestCorridor a(StructurePieceAccessor structurepieceaccessor, Random random, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, -1, -1, 0, 5, 5, 7, enumdirection);

            return a(structureboundingbox) && structurepieceaccessor.a(structureboundingbox) == null ? new WorldGenStrongholdPieces.WorldGenStrongholdChestCorridor(l, random, structureboundingbox, enumdirection) : null;
        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            this.a(generatoraccessseed, structureboundingbox, 0, 0, 0, 4, 4, 6, true, random, WorldGenStrongholdPieces.SMOOTH_STONE_SELECTOR);
            this.a(generatoraccessseed, random, structureboundingbox, this.entryDoor, 1, 1, 0);
            this.a(generatoraccessseed, random, structureboundingbox, WorldGenStrongholdPieces.WorldGenStrongholdPiece.WorldGenStrongholdDoorType.OPENING, 1, 1, 6);
            this.a(generatoraccessseed, structureboundingbox, 3, 1, 2, 3, 1, 4, Blocks.STONE_BRICKS.getBlockData(), Blocks.STONE_BRICKS.getBlockData(), false);
            this.c(generatoraccessseed, Blocks.STONE_BRICK_SLAB.getBlockData(), 3, 1, 1, structureboundingbox);
            this.c(generatoraccessseed, Blocks.STONE_BRICK_SLAB.getBlockData(), 3, 1, 5, structureboundingbox);
            this.c(generatoraccessseed, Blocks.STONE_BRICK_SLAB.getBlockData(), 3, 2, 2, structureboundingbox);
            this.c(generatoraccessseed, Blocks.STONE_BRICK_SLAB.getBlockData(), 3, 2, 4, structureboundingbox);

            for (int i = 2; i <= 4; ++i) {
                this.c(generatoraccessseed, Blocks.STONE_BRICK_SLAB.getBlockData(), 2, 1, i, structureboundingbox);
            }

            if (!this.hasPlacedChest && structureboundingbox.b((BaseBlockPosition) this.c(3, 2, 3))) {
                this.hasPlacedChest = true;
                this.a(generatoraccessseed, structureboundingbox, random, 3, 2, 3, LootTables.STRONGHOLD_CORRIDOR);
            }

            return true;
        }
    }

    public static class WorldGenStrongholdLibrary extends WorldGenStrongholdPieces.WorldGenStrongholdPiece {

        protected static final int WIDTH = 14;
        protected static final int HEIGHT = 6;
        protected static final int TALL_HEIGHT = 11;
        protected static final int DEPTH = 15;
        private final boolean isTall;

        public WorldGenStrongholdLibrary(int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(WorldGenFeatureStructurePieceType.STRONGHOLD_LIBRARY, i, structureboundingbox);
            this.a(enumdirection);
            this.entryDoor = this.a(random);
            this.isTall = structureboundingbox.d() > 6;
        }

        public WorldGenStrongholdLibrary(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.STRONGHOLD_LIBRARY, nbttagcompound);
            this.isTall = nbttagcompound.getBoolean("Tall");
        }

        @Override
        protected void a(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super.a(worldserver, nbttagcompound);
            nbttagcompound.setBoolean("Tall", this.isTall);
        }

        public static WorldGenStrongholdPieces.WorldGenStrongholdLibrary a(StructurePieceAccessor structurepieceaccessor, Random random, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, -4, -1, 0, 14, 11, 15, enumdirection);

            if (!a(structureboundingbox) || structurepieceaccessor.a(structureboundingbox) != null) {
                structureboundingbox = StructureBoundingBox.a(i, j, k, -4, -1, 0, 14, 6, 15, enumdirection);
                if (!a(structureboundingbox) || structurepieceaccessor.a(structureboundingbox) != null) {
                    return null;
                }
            }

            return new WorldGenStrongholdPieces.WorldGenStrongholdLibrary(l, random, structureboundingbox, enumdirection);
        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            byte b0 = 11;

            if (!this.isTall) {
                b0 = 6;
            }

            this.a(generatoraccessseed, structureboundingbox, 0, 0, 0, 13, b0 - 1, 14, true, random, WorldGenStrongholdPieces.SMOOTH_STONE_SELECTOR);
            this.a(generatoraccessseed, random, structureboundingbox, this.entryDoor, 4, 1, 0);
            this.a(generatoraccessseed, structureboundingbox, random, 0.07F, 2, 1, 1, 11, 4, 13, Blocks.COBWEB.getBlockData(), Blocks.COBWEB.getBlockData(), false, false);
            boolean flag = true;
            boolean flag1 = true;

            int i;

            for (i = 1; i <= 13; ++i) {
                if ((i - 1) % 4 == 0) {
                    this.a(generatoraccessseed, structureboundingbox, 1, 1, i, 1, 4, i, Blocks.OAK_PLANKS.getBlockData(), Blocks.OAK_PLANKS.getBlockData(), false);
                    this.a(generatoraccessseed, structureboundingbox, 12, 1, i, 12, 4, i, Blocks.OAK_PLANKS.getBlockData(), Blocks.OAK_PLANKS.getBlockData(), false);
                    this.c(generatoraccessseed, (IBlockData) Blocks.WALL_TORCH.getBlockData().set(BlockTorchWall.FACING, EnumDirection.EAST), 2, 3, i, structureboundingbox);
                    this.c(generatoraccessseed, (IBlockData) Blocks.WALL_TORCH.getBlockData().set(BlockTorchWall.FACING, EnumDirection.WEST), 11, 3, i, structureboundingbox);
                    if (this.isTall) {
                        this.a(generatoraccessseed, structureboundingbox, 1, 6, i, 1, 9, i, Blocks.OAK_PLANKS.getBlockData(), Blocks.OAK_PLANKS.getBlockData(), false);
                        this.a(generatoraccessseed, structureboundingbox, 12, 6, i, 12, 9, i, Blocks.OAK_PLANKS.getBlockData(), Blocks.OAK_PLANKS.getBlockData(), false);
                    }
                } else {
                    this.a(generatoraccessseed, structureboundingbox, 1, 1, i, 1, 4, i, Blocks.BOOKSHELF.getBlockData(), Blocks.BOOKSHELF.getBlockData(), false);
                    this.a(generatoraccessseed, structureboundingbox, 12, 1, i, 12, 4, i, Blocks.BOOKSHELF.getBlockData(), Blocks.BOOKSHELF.getBlockData(), false);
                    if (this.isTall) {
                        this.a(generatoraccessseed, structureboundingbox, 1, 6, i, 1, 9, i, Blocks.BOOKSHELF.getBlockData(), Blocks.BOOKSHELF.getBlockData(), false);
                        this.a(generatoraccessseed, structureboundingbox, 12, 6, i, 12, 9, i, Blocks.BOOKSHELF.getBlockData(), Blocks.BOOKSHELF.getBlockData(), false);
                    }
                }
            }

            for (i = 3; i < 12; i += 2) {
                this.a(generatoraccessseed, structureboundingbox, 3, 1, i, 4, 3, i, Blocks.BOOKSHELF.getBlockData(), Blocks.BOOKSHELF.getBlockData(), false);
                this.a(generatoraccessseed, structureboundingbox, 6, 1, i, 7, 3, i, Blocks.BOOKSHELF.getBlockData(), Blocks.BOOKSHELF.getBlockData(), false);
                this.a(generatoraccessseed, structureboundingbox, 9, 1, i, 10, 3, i, Blocks.BOOKSHELF.getBlockData(), Blocks.BOOKSHELF.getBlockData(), false);
            }

            if (this.isTall) {
                this.a(generatoraccessseed, structureboundingbox, 1, 5, 1, 3, 5, 13, Blocks.OAK_PLANKS.getBlockData(), Blocks.OAK_PLANKS.getBlockData(), false);
                this.a(generatoraccessseed, structureboundingbox, 10, 5, 1, 12, 5, 13, Blocks.OAK_PLANKS.getBlockData(), Blocks.OAK_PLANKS.getBlockData(), false);
                this.a(generatoraccessseed, structureboundingbox, 4, 5, 1, 9, 5, 2, Blocks.OAK_PLANKS.getBlockData(), Blocks.OAK_PLANKS.getBlockData(), false);
                this.a(generatoraccessseed, structureboundingbox, 4, 5, 12, 9, 5, 13, Blocks.OAK_PLANKS.getBlockData(), Blocks.OAK_PLANKS.getBlockData(), false);
                this.c(generatoraccessseed, Blocks.OAK_PLANKS.getBlockData(), 9, 5, 11, structureboundingbox);
                this.c(generatoraccessseed, Blocks.OAK_PLANKS.getBlockData(), 8, 5, 11, structureboundingbox);
                this.c(generatoraccessseed, Blocks.OAK_PLANKS.getBlockData(), 9, 5, 10, structureboundingbox);
                IBlockData iblockdata = (IBlockData) ((IBlockData) Blocks.OAK_FENCE.getBlockData().set(BlockFence.WEST, true)).set(BlockFence.EAST, true);
                IBlockData iblockdata1 = (IBlockData) ((IBlockData) Blocks.OAK_FENCE.getBlockData().set(BlockFence.NORTH, true)).set(BlockFence.SOUTH, true);

                this.a(generatoraccessseed, structureboundingbox, 3, 6, 3, 3, 6, 11, iblockdata1, iblockdata1, false);
                this.a(generatoraccessseed, structureboundingbox, 10, 6, 3, 10, 6, 9, iblockdata1, iblockdata1, false);
                this.a(generatoraccessseed, structureboundingbox, 4, 6, 2, 9, 6, 2, iblockdata, iblockdata, false);
                this.a(generatoraccessseed, structureboundingbox, 4, 6, 12, 7, 6, 12, iblockdata, iblockdata, false);
                this.c(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.OAK_FENCE.getBlockData().set(BlockFence.NORTH, true)).set(BlockFence.EAST, true), 3, 6, 2, structureboundingbox);
                this.c(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.OAK_FENCE.getBlockData().set(BlockFence.SOUTH, true)).set(BlockFence.EAST, true), 3, 6, 12, structureboundingbox);
                this.c(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.OAK_FENCE.getBlockData().set(BlockFence.NORTH, true)).set(BlockFence.WEST, true), 10, 6, 2, structureboundingbox);

                for (int j = 0; j <= 2; ++j) {
                    this.c(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.OAK_FENCE.getBlockData().set(BlockFence.SOUTH, true)).set(BlockFence.WEST, true), 8 + j, 6, 12 - j, structureboundingbox);
                    if (j != 2) {
                        this.c(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.OAK_FENCE.getBlockData().set(BlockFence.NORTH, true)).set(BlockFence.EAST, true), 8 + j, 6, 11 - j, structureboundingbox);
                    }
                }

                IBlockData iblockdata2 = (IBlockData) Blocks.LADDER.getBlockData().set(BlockLadder.FACING, EnumDirection.SOUTH);

                this.c(generatoraccessseed, iblockdata2, 10, 1, 13, structureboundingbox);
                this.c(generatoraccessseed, iblockdata2, 10, 2, 13, structureboundingbox);
                this.c(generatoraccessseed, iblockdata2, 10, 3, 13, structureboundingbox);
                this.c(generatoraccessseed, iblockdata2, 10, 4, 13, structureboundingbox);
                this.c(generatoraccessseed, iblockdata2, 10, 5, 13, structureboundingbox);
                this.c(generatoraccessseed, iblockdata2, 10, 6, 13, structureboundingbox);
                this.c(generatoraccessseed, iblockdata2, 10, 7, 13, structureboundingbox);
                boolean flag2 = true;
                boolean flag3 = true;
                IBlockData iblockdata3 = (IBlockData) Blocks.OAK_FENCE.getBlockData().set(BlockFence.EAST, true);

                this.c(generatoraccessseed, iblockdata3, 6, 9, 7, structureboundingbox);
                IBlockData iblockdata4 = (IBlockData) Blocks.OAK_FENCE.getBlockData().set(BlockFence.WEST, true);

                this.c(generatoraccessseed, iblockdata4, 7, 9, 7, structureboundingbox);
                this.c(generatoraccessseed, iblockdata3, 6, 8, 7, structureboundingbox);
                this.c(generatoraccessseed, iblockdata4, 7, 8, 7, structureboundingbox);
                IBlockData iblockdata5 = (IBlockData) ((IBlockData) iblockdata1.set(BlockFence.WEST, true)).set(BlockFence.EAST, true);

                this.c(generatoraccessseed, iblockdata5, 6, 7, 7, structureboundingbox);
                this.c(generatoraccessseed, iblockdata5, 7, 7, 7, structureboundingbox);
                this.c(generatoraccessseed, iblockdata3, 5, 7, 7, structureboundingbox);
                this.c(generatoraccessseed, iblockdata4, 8, 7, 7, structureboundingbox);
                this.c(generatoraccessseed, (IBlockData) iblockdata3.set(BlockFence.NORTH, true), 6, 7, 6, structureboundingbox);
                this.c(generatoraccessseed, (IBlockData) iblockdata3.set(BlockFence.SOUTH, true), 6, 7, 8, structureboundingbox);
                this.c(generatoraccessseed, (IBlockData) iblockdata4.set(BlockFence.NORTH, true), 7, 7, 6, structureboundingbox);
                this.c(generatoraccessseed, (IBlockData) iblockdata4.set(BlockFence.SOUTH, true), 7, 7, 8, structureboundingbox);
                IBlockData iblockdata6 = Blocks.TORCH.getBlockData();

                this.c(generatoraccessseed, iblockdata6, 5, 8, 7, structureboundingbox);
                this.c(generatoraccessseed, iblockdata6, 8, 8, 7, structureboundingbox);
                this.c(generatoraccessseed, iblockdata6, 6, 8, 6, structureboundingbox);
                this.c(generatoraccessseed, iblockdata6, 6, 8, 8, structureboundingbox);
                this.c(generatoraccessseed, iblockdata6, 7, 8, 6, structureboundingbox);
                this.c(generatoraccessseed, iblockdata6, 7, 8, 8, structureboundingbox);
            }

            this.a(generatoraccessseed, structureboundingbox, random, 3, 3, 5, LootTables.STRONGHOLD_LIBRARY);
            if (this.isTall) {
                this.c(generatoraccessseed, WorldGenStrongholdPieces.WorldGenStrongholdLibrary.CAVE_AIR, 12, 9, 1, structureboundingbox);
                this.a(generatoraccessseed, structureboundingbox, random, 12, 8, 1, LootTables.STRONGHOLD_LIBRARY);
            }

            return true;
        }
    }

    public static class WorldGenStrongholdPortalRoom extends WorldGenStrongholdPieces.WorldGenStrongholdPiece {

        protected static final int WIDTH = 11;
        protected static final int HEIGHT = 8;
        protected static final int DEPTH = 16;
        private boolean hasPlacedSpawner;

        public WorldGenStrongholdPortalRoom(int i, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(WorldGenFeatureStructurePieceType.STRONGHOLD_PORTAL_ROOM, i, structureboundingbox);
            this.a(enumdirection);
        }

        public WorldGenStrongholdPortalRoom(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.STRONGHOLD_PORTAL_ROOM, nbttagcompound);
            this.hasPlacedSpawner = nbttagcompound.getBoolean("Mob");
        }

        @Override
        protected void a(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super.a(worldserver, nbttagcompound);
            nbttagcompound.setBoolean("Mob", this.hasPlacedSpawner);
        }

        @Override
        public void a(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, Random random) {
            if (structurepiece != null) {
                ((WorldGenStrongholdPieces.WorldGenStrongholdStart) structurepiece).portalRoomPiece = this;
            }

        }

        public static WorldGenStrongholdPieces.WorldGenStrongholdPortalRoom a(StructurePieceAccessor structurepieceaccessor, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, -4, -1, 0, 11, 8, 16, enumdirection);

            return a(structureboundingbox) && structurepieceaccessor.a(structureboundingbox) == null ? new WorldGenStrongholdPieces.WorldGenStrongholdPortalRoom(l, structureboundingbox, enumdirection) : null;
        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            this.a(generatoraccessseed, structureboundingbox, 0, 0, 0, 10, 7, 15, false, random, WorldGenStrongholdPieces.SMOOTH_STONE_SELECTOR);
            this.a(generatoraccessseed, random, structureboundingbox, WorldGenStrongholdPieces.WorldGenStrongholdPiece.WorldGenStrongholdDoorType.GRATES, 4, 1, 0);
            byte b0 = 6;

            this.a(generatoraccessseed, structureboundingbox, 1, b0, 1, 1, b0, 14, false, random, WorldGenStrongholdPieces.SMOOTH_STONE_SELECTOR);
            this.a(generatoraccessseed, structureboundingbox, 9, b0, 1, 9, b0, 14, false, random, WorldGenStrongholdPieces.SMOOTH_STONE_SELECTOR);
            this.a(generatoraccessseed, structureboundingbox, 2, b0, 1, 8, b0, 2, false, random, WorldGenStrongholdPieces.SMOOTH_STONE_SELECTOR);
            this.a(generatoraccessseed, structureboundingbox, 2, b0, 14, 8, b0, 14, false, random, WorldGenStrongholdPieces.SMOOTH_STONE_SELECTOR);
            this.a(generatoraccessseed, structureboundingbox, 1, 1, 1, 2, 1, 4, false, random, WorldGenStrongholdPieces.SMOOTH_STONE_SELECTOR);
            this.a(generatoraccessseed, structureboundingbox, 8, 1, 1, 9, 1, 4, false, random, WorldGenStrongholdPieces.SMOOTH_STONE_SELECTOR);
            this.a(generatoraccessseed, structureboundingbox, 1, 1, 1, 1, 1, 3, Blocks.LAVA.getBlockData(), Blocks.LAVA.getBlockData(), false);
            this.a(generatoraccessseed, structureboundingbox, 9, 1, 1, 9, 1, 3, Blocks.LAVA.getBlockData(), Blocks.LAVA.getBlockData(), false);
            this.a(generatoraccessseed, structureboundingbox, 3, 1, 8, 7, 1, 12, false, random, WorldGenStrongholdPieces.SMOOTH_STONE_SELECTOR);
            this.a(generatoraccessseed, structureboundingbox, 4, 1, 9, 6, 1, 11, Blocks.LAVA.getBlockData(), Blocks.LAVA.getBlockData(), false);
            IBlockData iblockdata = (IBlockData) ((IBlockData) Blocks.IRON_BARS.getBlockData().set(BlockIronBars.NORTH, true)).set(BlockIronBars.SOUTH, true);
            IBlockData iblockdata1 = (IBlockData) ((IBlockData) Blocks.IRON_BARS.getBlockData().set(BlockIronBars.WEST, true)).set(BlockIronBars.EAST, true);

            int i;

            for (i = 3; i < 14; i += 2) {
                this.a(generatoraccessseed, structureboundingbox, 0, 3, i, 0, 4, i, iblockdata, iblockdata, false);
                this.a(generatoraccessseed, structureboundingbox, 10, 3, i, 10, 4, i, iblockdata, iblockdata, false);
            }

            for (i = 2; i < 9; i += 2) {
                this.a(generatoraccessseed, structureboundingbox, i, 3, 15, i, 4, 15, iblockdata1, iblockdata1, false);
            }

            IBlockData iblockdata2 = (IBlockData) Blocks.STONE_BRICK_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.NORTH);

            this.a(generatoraccessseed, structureboundingbox, 4, 1, 5, 6, 1, 7, false, random, WorldGenStrongholdPieces.SMOOTH_STONE_SELECTOR);
            this.a(generatoraccessseed, structureboundingbox, 4, 2, 6, 6, 2, 7, false, random, WorldGenStrongholdPieces.SMOOTH_STONE_SELECTOR);
            this.a(generatoraccessseed, structureboundingbox, 4, 3, 7, 6, 3, 7, false, random, WorldGenStrongholdPieces.SMOOTH_STONE_SELECTOR);

            for (int j = 4; j <= 6; ++j) {
                this.c(generatoraccessseed, iblockdata2, j, 1, 4, structureboundingbox);
                this.c(generatoraccessseed, iblockdata2, j, 2, 5, structureboundingbox);
                this.c(generatoraccessseed, iblockdata2, j, 3, 6, structureboundingbox);
            }

            IBlockData iblockdata3 = (IBlockData) Blocks.END_PORTAL_FRAME.getBlockData().set(BlockEnderPortalFrame.FACING, EnumDirection.NORTH);
            IBlockData iblockdata4 = (IBlockData) Blocks.END_PORTAL_FRAME.getBlockData().set(BlockEnderPortalFrame.FACING, EnumDirection.SOUTH);
            IBlockData iblockdata5 = (IBlockData) Blocks.END_PORTAL_FRAME.getBlockData().set(BlockEnderPortalFrame.FACING, EnumDirection.EAST);
            IBlockData iblockdata6 = (IBlockData) Blocks.END_PORTAL_FRAME.getBlockData().set(BlockEnderPortalFrame.FACING, EnumDirection.WEST);
            boolean flag = true;
            boolean[] aboolean = new boolean[12];

            for (int k = 0; k < aboolean.length; ++k) {
                aboolean[k] = random.nextFloat() > 0.9F;
                flag &= aboolean[k];
            }

            this.c(generatoraccessseed, (IBlockData) iblockdata3.set(BlockEnderPortalFrame.HAS_EYE, aboolean[0]), 4, 3, 8, structureboundingbox);
            this.c(generatoraccessseed, (IBlockData) iblockdata3.set(BlockEnderPortalFrame.HAS_EYE, aboolean[1]), 5, 3, 8, structureboundingbox);
            this.c(generatoraccessseed, (IBlockData) iblockdata3.set(BlockEnderPortalFrame.HAS_EYE, aboolean[2]), 6, 3, 8, structureboundingbox);
            this.c(generatoraccessseed, (IBlockData) iblockdata4.set(BlockEnderPortalFrame.HAS_EYE, aboolean[3]), 4, 3, 12, structureboundingbox);
            this.c(generatoraccessseed, (IBlockData) iblockdata4.set(BlockEnderPortalFrame.HAS_EYE, aboolean[4]), 5, 3, 12, structureboundingbox);
            this.c(generatoraccessseed, (IBlockData) iblockdata4.set(BlockEnderPortalFrame.HAS_EYE, aboolean[5]), 6, 3, 12, structureboundingbox);
            this.c(generatoraccessseed, (IBlockData) iblockdata5.set(BlockEnderPortalFrame.HAS_EYE, aboolean[6]), 3, 3, 9, structureboundingbox);
            this.c(generatoraccessseed, (IBlockData) iblockdata5.set(BlockEnderPortalFrame.HAS_EYE, aboolean[7]), 3, 3, 10, structureboundingbox);
            this.c(generatoraccessseed, (IBlockData) iblockdata5.set(BlockEnderPortalFrame.HAS_EYE, aboolean[8]), 3, 3, 11, structureboundingbox);
            this.c(generatoraccessseed, (IBlockData) iblockdata6.set(BlockEnderPortalFrame.HAS_EYE, aboolean[9]), 7, 3, 9, structureboundingbox);
            this.c(generatoraccessseed, (IBlockData) iblockdata6.set(BlockEnderPortalFrame.HAS_EYE, aboolean[10]), 7, 3, 10, structureboundingbox);
            this.c(generatoraccessseed, (IBlockData) iblockdata6.set(BlockEnderPortalFrame.HAS_EYE, aboolean[11]), 7, 3, 11, structureboundingbox);
            if (flag) {
                IBlockData iblockdata7 = Blocks.END_PORTAL.getBlockData();

                this.c(generatoraccessseed, iblockdata7, 4, 3, 9, structureboundingbox);
                this.c(generatoraccessseed, iblockdata7, 5, 3, 9, structureboundingbox);
                this.c(generatoraccessseed, iblockdata7, 6, 3, 9, structureboundingbox);
                this.c(generatoraccessseed, iblockdata7, 4, 3, 10, structureboundingbox);
                this.c(generatoraccessseed, iblockdata7, 5, 3, 10, structureboundingbox);
                this.c(generatoraccessseed, iblockdata7, 6, 3, 10, structureboundingbox);
                this.c(generatoraccessseed, iblockdata7, 4, 3, 11, structureboundingbox);
                this.c(generatoraccessseed, iblockdata7, 5, 3, 11, structureboundingbox);
                this.c(generatoraccessseed, iblockdata7, 6, 3, 11, structureboundingbox);
            }

            if (!this.hasPlacedSpawner) {
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.c(5, 3, 6);

                if (structureboundingbox.b((BaseBlockPosition) blockposition_mutableblockposition)) {
                    this.hasPlacedSpawner = true;
                    generatoraccessseed.setTypeAndData(blockposition_mutableblockposition, Blocks.SPAWNER.getBlockData(), 2);
                    TileEntity tileentity = generatoraccessseed.getTileEntity(blockposition_mutableblockposition);

                    if (tileentity instanceof TileEntityMobSpawner) {
                        ((TileEntityMobSpawner) tileentity).getSpawner().setMobName(EntityTypes.SILVERFISH);
                    }
                }
            }

            return true;
        }
    }

    private abstract static class WorldGenStrongholdPiece extends StructurePiece {

        protected WorldGenStrongholdPieces.WorldGenStrongholdPiece.WorldGenStrongholdDoorType entryDoor;

        protected WorldGenStrongholdPiece(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, int i, StructureBoundingBox structureboundingbox) {
            super(worldgenfeaturestructurepiecetype, i, structureboundingbox);
            this.entryDoor = WorldGenStrongholdPieces.WorldGenStrongholdPiece.WorldGenStrongholdDoorType.OPENING;
        }

        public WorldGenStrongholdPiece(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, NBTTagCompound nbttagcompound) {
            super(worldgenfeaturestructurepiecetype, nbttagcompound);
            this.entryDoor = WorldGenStrongholdPieces.WorldGenStrongholdPiece.WorldGenStrongholdDoorType.OPENING;
            this.entryDoor = WorldGenStrongholdPieces.WorldGenStrongholdPiece.WorldGenStrongholdDoorType.valueOf(nbttagcompound.getString("EntryDoor"));
        }

        @Override
        public NoiseEffect ad_() {
            return NoiseEffect.BURY;
        }

        @Override
        protected void a(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            nbttagcompound.setString("EntryDoor", this.entryDoor.name());
        }

        protected void a(GeneratorAccessSeed generatoraccessseed, Random random, StructureBoundingBox structureboundingbox, WorldGenStrongholdPieces.WorldGenStrongholdPiece.WorldGenStrongholdDoorType worldgenstrongholdpieces_worldgenstrongholdpiece_worldgenstrongholddoortype, int i, int j, int k) {
            switch (worldgenstrongholdpieces_worldgenstrongholdpiece_worldgenstrongholddoortype) {
                case OPENING:
                    this.a(generatoraccessseed, structureboundingbox, i, j, k, i + 3 - 1, j + 3 - 1, k, WorldGenStrongholdPieces.WorldGenStrongholdPiece.CAVE_AIR, WorldGenStrongholdPieces.WorldGenStrongholdPiece.CAVE_AIR, false);
                    break;
                case WOOD_DOOR:
                    this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), i, j, k, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), i, j + 1, k, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), i, j + 2, k, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), i + 1, j + 2, k, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), i + 2, j + 2, k, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), i + 2, j + 1, k, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), i + 2, j, k, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.OAK_DOOR.getBlockData(), i + 1, j, k, structureboundingbox);
                    this.c(generatoraccessseed, (IBlockData) Blocks.OAK_DOOR.getBlockData().set(BlockDoor.HALF, BlockPropertyDoubleBlockHalf.UPPER), i + 1, j + 1, k, structureboundingbox);
                    break;
                case GRATES:
                    this.c(generatoraccessseed, Blocks.CAVE_AIR.getBlockData(), i + 1, j, k, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.CAVE_AIR.getBlockData(), i + 1, j + 1, k, structureboundingbox);
                    this.c(generatoraccessseed, (IBlockData) Blocks.IRON_BARS.getBlockData().set(BlockIronBars.WEST, true), i, j, k, structureboundingbox);
                    this.c(generatoraccessseed, (IBlockData) Blocks.IRON_BARS.getBlockData().set(BlockIronBars.WEST, true), i, j + 1, k, structureboundingbox);
                    this.c(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.IRON_BARS.getBlockData().set(BlockIronBars.EAST, true)).set(BlockIronBars.WEST, true), i, j + 2, k, structureboundingbox);
                    this.c(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.IRON_BARS.getBlockData().set(BlockIronBars.EAST, true)).set(BlockIronBars.WEST, true), i + 1, j + 2, k, structureboundingbox);
                    this.c(generatoraccessseed, (IBlockData) ((IBlockData) Blocks.IRON_BARS.getBlockData().set(BlockIronBars.EAST, true)).set(BlockIronBars.WEST, true), i + 2, j + 2, k, structureboundingbox);
                    this.c(generatoraccessseed, (IBlockData) Blocks.IRON_BARS.getBlockData().set(BlockIronBars.EAST, true), i + 2, j + 1, k, structureboundingbox);
                    this.c(generatoraccessseed, (IBlockData) Blocks.IRON_BARS.getBlockData().set(BlockIronBars.EAST, true), i + 2, j, k, structureboundingbox);
                    break;
                case IRON_DOOR:
                    this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), i, j, k, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), i, j + 1, k, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), i, j + 2, k, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), i + 1, j + 2, k, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), i + 2, j + 2, k, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), i + 2, j + 1, k, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), i + 2, j, k, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.IRON_DOOR.getBlockData(), i + 1, j, k, structureboundingbox);
                    this.c(generatoraccessseed, (IBlockData) Blocks.IRON_DOOR.getBlockData().set(BlockDoor.HALF, BlockPropertyDoubleBlockHalf.UPPER), i + 1, j + 1, k, structureboundingbox);
                    this.c(generatoraccessseed, (IBlockData) Blocks.STONE_BUTTON.getBlockData().set(BlockButtonAbstract.FACING, EnumDirection.NORTH), i + 2, j + 1, k + 1, structureboundingbox);
                    this.c(generatoraccessseed, (IBlockData) Blocks.STONE_BUTTON.getBlockData().set(BlockButtonAbstract.FACING, EnumDirection.SOUTH), i + 2, j + 1, k - 1, structureboundingbox);
            }

        }

        protected WorldGenStrongholdPieces.WorldGenStrongholdPiece.WorldGenStrongholdDoorType a(Random random) {
            int i = random.nextInt(5);

            switch (i) {
                case 0:
                case 1:
                default:
                    return WorldGenStrongholdPieces.WorldGenStrongholdPiece.WorldGenStrongholdDoorType.OPENING;
                case 2:
                    return WorldGenStrongholdPieces.WorldGenStrongholdPiece.WorldGenStrongholdDoorType.WOOD_DOOR;
                case 3:
                    return WorldGenStrongholdPieces.WorldGenStrongholdPiece.WorldGenStrongholdDoorType.GRATES;
                case 4:
                    return WorldGenStrongholdPieces.WorldGenStrongholdPiece.WorldGenStrongholdDoorType.IRON_DOOR;
            }
        }

        @Nullable
        protected StructurePiece a(WorldGenStrongholdPieces.WorldGenStrongholdStart worldgenstrongholdpieces_worldgenstrongholdstart, StructurePieceAccessor structurepieceaccessor, Random random, int i, int j) {
            EnumDirection enumdirection = this.h();

            if (enumdirection != null) {
                switch (enumdirection) {
                    case NORTH:
                        return WorldGenStrongholdPieces.b(worldgenstrongholdpieces_worldgenstrongholdstart, structurepieceaccessor, random, this.boundingBox.g() + i, this.boundingBox.h() + j, this.boundingBox.i() - 1, enumdirection, this.g());
                    case SOUTH:
                        return WorldGenStrongholdPieces.b(worldgenstrongholdpieces_worldgenstrongholdstart, structurepieceaccessor, random, this.boundingBox.g() + i, this.boundingBox.h() + j, this.boundingBox.l() + 1, enumdirection, this.g());
                    case WEST:
                        return WorldGenStrongholdPieces.b(worldgenstrongholdpieces_worldgenstrongholdstart, structurepieceaccessor, random, this.boundingBox.g() - 1, this.boundingBox.h() + j, this.boundingBox.i() + i, enumdirection, this.g());
                    case EAST:
                        return WorldGenStrongholdPieces.b(worldgenstrongholdpieces_worldgenstrongholdstart, structurepieceaccessor, random, this.boundingBox.j() + 1, this.boundingBox.h() + j, this.boundingBox.i() + i, enumdirection, this.g());
                }
            }

            return null;
        }

        @Nullable
        protected StructurePiece b(WorldGenStrongholdPieces.WorldGenStrongholdStart worldgenstrongholdpieces_worldgenstrongholdstart, StructurePieceAccessor structurepieceaccessor, Random random, int i, int j) {
            EnumDirection enumdirection = this.h();

            if (enumdirection != null) {
                switch (enumdirection) {
                    case NORTH:
                        return WorldGenStrongholdPieces.b(worldgenstrongholdpieces_worldgenstrongholdstart, structurepieceaccessor, random, this.boundingBox.g() - 1, this.boundingBox.h() + i, this.boundingBox.i() + j, EnumDirection.WEST, this.g());
                    case SOUTH:
                        return WorldGenStrongholdPieces.b(worldgenstrongholdpieces_worldgenstrongholdstart, structurepieceaccessor, random, this.boundingBox.g() - 1, this.boundingBox.h() + i, this.boundingBox.i() + j, EnumDirection.WEST, this.g());
                    case WEST:
                        return WorldGenStrongholdPieces.b(worldgenstrongholdpieces_worldgenstrongholdstart, structurepieceaccessor, random, this.boundingBox.g() + j, this.boundingBox.h() + i, this.boundingBox.i() - 1, EnumDirection.NORTH, this.g());
                    case EAST:
                        return WorldGenStrongholdPieces.b(worldgenstrongholdpieces_worldgenstrongholdstart, structurepieceaccessor, random, this.boundingBox.g() + j, this.boundingBox.h() + i, this.boundingBox.i() - 1, EnumDirection.NORTH, this.g());
                }
            }

            return null;
        }

        @Nullable
        protected StructurePiece c(WorldGenStrongholdPieces.WorldGenStrongholdStart worldgenstrongholdpieces_worldgenstrongholdstart, StructurePieceAccessor structurepieceaccessor, Random random, int i, int j) {
            EnumDirection enumdirection = this.h();

            if (enumdirection != null) {
                switch (enumdirection) {
                    case NORTH:
                        return WorldGenStrongholdPieces.b(worldgenstrongholdpieces_worldgenstrongholdstart, structurepieceaccessor, random, this.boundingBox.j() + 1, this.boundingBox.h() + i, this.boundingBox.i() + j, EnumDirection.EAST, this.g());
                    case SOUTH:
                        return WorldGenStrongholdPieces.b(worldgenstrongholdpieces_worldgenstrongholdstart, structurepieceaccessor, random, this.boundingBox.j() + 1, this.boundingBox.h() + i, this.boundingBox.i() + j, EnumDirection.EAST, this.g());
                    case WEST:
                        return WorldGenStrongholdPieces.b(worldgenstrongholdpieces_worldgenstrongholdstart, structurepieceaccessor, random, this.boundingBox.g() + j, this.boundingBox.h() + i, this.boundingBox.l() + 1, EnumDirection.SOUTH, this.g());
                    case EAST:
                        return WorldGenStrongholdPieces.b(worldgenstrongholdpieces_worldgenstrongholdstart, structurepieceaccessor, random, this.boundingBox.g() + j, this.boundingBox.h() + i, this.boundingBox.l() + 1, EnumDirection.SOUTH, this.g());
                }
            }

            return null;
        }

        protected static boolean a(StructureBoundingBox structureboundingbox) {
            return structureboundingbox != null && structureboundingbox.h() > 10;
        }

        protected static enum WorldGenStrongholdDoorType {

            OPENING, WOOD_DOOR, GRATES, IRON_DOOR;

            private WorldGenStrongholdDoorType() {}
        }
    }

    public static class WorldGenStrongholdStart extends WorldGenStrongholdPieces.WorldGenStrongholdStairs2 {

        public WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight previousPiece;
        @Nullable
        public WorldGenStrongholdPieces.WorldGenStrongholdPortalRoom portalRoomPiece;
        public final List<StructurePiece> pendingChildren = Lists.newArrayList();

        public WorldGenStrongholdStart(Random random, int i, int j) {
            super(WorldGenFeatureStructurePieceType.STRONGHOLD_START, 0, i, j, b(random));
        }

        public WorldGenStrongholdStart(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.STRONGHOLD_START, nbttagcompound);
        }

        @Override
        public BlockPosition ae_() {
            return this.portalRoomPiece != null ? this.portalRoomPiece.ae_() : super.ae_();
        }
    }

    public static class WorldGenStrongholdCorridor extends WorldGenStrongholdPieces.WorldGenStrongholdPiece {

        private final int steps;

        public WorldGenStrongholdCorridor(int i, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(WorldGenFeatureStructurePieceType.STRONGHOLD_FILLER_CORRIDOR, i, structureboundingbox);
            this.a(enumdirection);
            this.steps = enumdirection != EnumDirection.NORTH && enumdirection != EnumDirection.SOUTH ? structureboundingbox.c() : structureboundingbox.e();
        }

        public WorldGenStrongholdCorridor(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.STRONGHOLD_FILLER_CORRIDOR, nbttagcompound);
            this.steps = nbttagcompound.getInt("Steps");
        }

        @Override
        protected void a(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super.a(worldserver, nbttagcompound);
            nbttagcompound.setInt("Steps", this.steps);
        }

        public static StructureBoundingBox a(StructurePieceAccessor structurepieceaccessor, Random random, int i, int j, int k, EnumDirection enumdirection) {
            boolean flag = true;
            StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, -1, -1, 0, 5, 5, 4, enumdirection);
            StructurePiece structurepiece = structurepieceaccessor.a(structureboundingbox);

            if (structurepiece == null) {
                return null;
            } else {
                if (structurepiece.f().h() == structureboundingbox.h()) {
                    for (int l = 2; l >= 1; --l) {
                        structureboundingbox = StructureBoundingBox.a(i, j, k, -1, -1, 0, 5, 5, l, enumdirection);
                        if (!structurepiece.f().a(structureboundingbox)) {
                            return StructureBoundingBox.a(i, j, k, -1, -1, 0, 5, 5, l + 1, enumdirection);
                        }
                    }
                }

                return null;
            }
        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            for (int i = 0; i < this.steps; ++i) {
                this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 0, 0, i, structureboundingbox);
                this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 1, 0, i, structureboundingbox);
                this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 2, 0, i, structureboundingbox);
                this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 3, 0, i, structureboundingbox);
                this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 4, 0, i, structureboundingbox);

                for (int j = 1; j <= 3; ++j) {
                    this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 0, j, i, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.CAVE_AIR.getBlockData(), 1, j, i, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.CAVE_AIR.getBlockData(), 2, j, i, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.CAVE_AIR.getBlockData(), 3, j, i, structureboundingbox);
                    this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 4, j, i, structureboundingbox);
                }

                this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 0, 4, i, structureboundingbox);
                this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 1, 4, i, structureboundingbox);
                this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 2, 4, i, structureboundingbox);
                this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 3, 4, i, structureboundingbox);
                this.c(generatoraccessseed, Blocks.STONE_BRICKS.getBlockData(), 4, 4, i, structureboundingbox);
            }

            return true;
        }
    }

    private static class WorldGenStrongholdStones extends StructurePiece.StructurePieceBlockSelector {

        WorldGenStrongholdStones() {}

        @Override
        public void a(Random random, int i, int j, int k, boolean flag) {
            if (flag) {
                float f = random.nextFloat();

                if (f < 0.2F) {
                    this.next = Blocks.CRACKED_STONE_BRICKS.getBlockData();
                } else if (f < 0.5F) {
                    this.next = Blocks.MOSSY_STONE_BRICKS.getBlockData();
                } else if (f < 0.55F) {
                    this.next = Blocks.INFESTED_STONE_BRICKS.getBlockData();
                } else {
                    this.next = Blocks.STONE_BRICKS.getBlockData();
                }
            } else {
                this.next = Blocks.CAVE_AIR.getBlockData();
            }

        }
    }

    public abstract static class q extends WorldGenStrongholdPieces.WorldGenStrongholdPiece {

        protected static final int WIDTH = 5;
        protected static final int HEIGHT = 5;
        protected static final int DEPTH = 5;

        protected q(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, int i, StructureBoundingBox structureboundingbox) {
            super(worldgenfeaturestructurepiecetype, i, structureboundingbox);
        }

        public q(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, NBTTagCompound nbttagcompound) {
            super(worldgenfeaturestructurepiecetype, nbttagcompound);
        }
    }
}
