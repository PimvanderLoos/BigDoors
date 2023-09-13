package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.DataResult;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.vehicle.EntityMinecartChest;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockFalling;
import net.minecraft.world.level.block.BlockFence;
import net.minecraft.world.level.block.BlockMinecartTrack;
import net.minecraft.world.level.block.BlockTorchWall;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityMobSpawner;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockPropertyTrackPosition;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureStructurePieceType;
import net.minecraft.world.level.levelgen.feature.WorldGenMineshaft;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.storage.loot.LootTables;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenMineshaftPieces {

    static final Logger LOGGER = LogManager.getLogger();
    private static final int DEFAULT_SHAFT_WIDTH = 3;
    private static final int DEFAULT_SHAFT_HEIGHT = 3;
    private static final int DEFAULT_SHAFT_LENGTH = 5;
    private static final int MAX_PILLAR_HEIGHT = 20;
    private static final int MAX_CHAIN_HEIGHT = 50;
    private static final int MAX_DEPTH = 8;
    public static final int MAGIC_START_Y = 50;

    public WorldGenMineshaftPieces() {}

    private static WorldGenMineshaftPieces.c createRandomShaftPiece(StructurePieceAccessor structurepieceaccessor, Random random, int i, int j, int k, @Nullable EnumDirection enumdirection, int l, WorldGenMineshaft.Type worldgenmineshaft_type) {
        int i1 = random.nextInt(100);
        StructureBoundingBox structureboundingbox;

        if (i1 >= 80) {
            structureboundingbox = WorldGenMineshaftPieces.WorldGenMineshaftCross.findCrossing(structurepieceaccessor, random, i, j, k, enumdirection);
            if (structureboundingbox != null) {
                return new WorldGenMineshaftPieces.WorldGenMineshaftCross(l, structureboundingbox, enumdirection, worldgenmineshaft_type);
            }
        } else if (i1 >= 70) {
            structureboundingbox = WorldGenMineshaftPieces.WorldGenMineshaftStairs.findStairs(structurepieceaccessor, random, i, j, k, enumdirection);
            if (structureboundingbox != null) {
                return new WorldGenMineshaftPieces.WorldGenMineshaftStairs(l, structureboundingbox, enumdirection, worldgenmineshaft_type);
            }
        } else {
            structureboundingbox = WorldGenMineshaftPieces.WorldGenMineshaftCorridor.findCorridorSize(structurepieceaccessor, random, i, j, k, enumdirection);
            if (structureboundingbox != null) {
                return new WorldGenMineshaftPieces.WorldGenMineshaftCorridor(l, random, structureboundingbox, enumdirection, worldgenmineshaft_type);
            }
        }

        return null;
    }

    static WorldGenMineshaftPieces.c generateAndAddPiece(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, Random random, int i, int j, int k, EnumDirection enumdirection, int l) {
        if (l > 8) {
            return null;
        } else if (Math.abs(i - structurepiece.getBoundingBox().minX()) <= 80 && Math.abs(k - structurepiece.getBoundingBox().minZ()) <= 80) {
            WorldGenMineshaft.Type worldgenmineshaft_type = ((WorldGenMineshaftPieces.c) structurepiece).type;
            WorldGenMineshaftPieces.c worldgenmineshaftpieces_c = createRandomShaftPiece(structurepieceaccessor, random, i, j, k, enumdirection, l + 1, worldgenmineshaft_type);

            if (worldgenmineshaftpieces_c != null) {
                structurepieceaccessor.addPiece(worldgenmineshaftpieces_c);
                worldgenmineshaftpieces_c.addChildren(structurepiece, structurepieceaccessor, random);
            }

            return worldgenmineshaftpieces_c;
        } else {
            return null;
        }
    }

    public static class WorldGenMineshaftCross extends WorldGenMineshaftPieces.c {

        private final EnumDirection direction;
        private final boolean isTwoFloored;

        public WorldGenMineshaftCross(NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.MINE_SHAFT_CROSSING, nbttagcompound);
            this.isTwoFloored = nbttagcompound.getBoolean("tf");
            this.direction = EnumDirection.from2DDataValue(nbttagcompound.getInt("D"));
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound) {
            super.addAdditionalSaveData(structurepieceserializationcontext, nbttagcompound);
            nbttagcompound.putBoolean("tf", this.isTwoFloored);
            nbttagcompound.putInt("D", this.direction.get2DDataValue());
        }

        public WorldGenMineshaftCross(int i, StructureBoundingBox structureboundingbox, @Nullable EnumDirection enumdirection, WorldGenMineshaft.Type worldgenmineshaft_type) {
            super(WorldGenFeatureStructurePieceType.MINE_SHAFT_CROSSING, i, worldgenmineshaft_type, structureboundingbox);
            this.direction = enumdirection;
            this.isTwoFloored = structureboundingbox.getYSpan() > 3;
        }

        @Nullable
        public static StructureBoundingBox findCrossing(StructurePieceAccessor structurepieceaccessor, Random random, int i, int j, int k, EnumDirection enumdirection) {
            byte b0;

            if (random.nextInt(4) == 0) {
                b0 = 6;
            } else {
                b0 = 2;
            }

            StructureBoundingBox structureboundingbox;

            switch (enumdirection) {
                case NORTH:
                default:
                    structureboundingbox = new StructureBoundingBox(-1, 0, -4, 3, b0, 0);
                    break;
                case SOUTH:
                    structureboundingbox = new StructureBoundingBox(-1, 0, 0, 3, b0, 4);
                    break;
                case WEST:
                    structureboundingbox = new StructureBoundingBox(-4, 0, -1, 0, b0, 3);
                    break;
                case EAST:
                    structureboundingbox = new StructureBoundingBox(0, 0, -1, 4, b0, 3);
            }

            structureboundingbox.move(i, j, k);
            return structurepieceaccessor.findCollisionPiece(structureboundingbox) != null ? null : structureboundingbox;
        }

        @Override
        public void addChildren(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, Random random) {
            int i = this.getGenDepth();

            switch (this.direction) {
                case NORTH:
                default:
                    WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.minX() + 1, this.boundingBox.minY(), this.boundingBox.minZ() - 1, EnumDirection.NORTH, i);
                    WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.minX() - 1, this.boundingBox.minY(), this.boundingBox.minZ() + 1, EnumDirection.WEST, i);
                    WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.maxX() + 1, this.boundingBox.minY(), this.boundingBox.minZ() + 1, EnumDirection.EAST, i);
                    break;
                case SOUTH:
                    WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.minX() + 1, this.boundingBox.minY(), this.boundingBox.maxZ() + 1, EnumDirection.SOUTH, i);
                    WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.minX() - 1, this.boundingBox.minY(), this.boundingBox.minZ() + 1, EnumDirection.WEST, i);
                    WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.maxX() + 1, this.boundingBox.minY(), this.boundingBox.minZ() + 1, EnumDirection.EAST, i);
                    break;
                case WEST:
                    WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.minX() + 1, this.boundingBox.minY(), this.boundingBox.minZ() - 1, EnumDirection.NORTH, i);
                    WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.minX() + 1, this.boundingBox.minY(), this.boundingBox.maxZ() + 1, EnumDirection.SOUTH, i);
                    WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.minX() - 1, this.boundingBox.minY(), this.boundingBox.minZ() + 1, EnumDirection.WEST, i);
                    break;
                case EAST:
                    WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.minX() + 1, this.boundingBox.minY(), this.boundingBox.minZ() - 1, EnumDirection.NORTH, i);
                    WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.minX() + 1, this.boundingBox.minY(), this.boundingBox.maxZ() + 1, EnumDirection.SOUTH, i);
                    WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.maxX() + 1, this.boundingBox.minY(), this.boundingBox.minZ() + 1, EnumDirection.EAST, i);
            }

            if (this.isTwoFloored) {
                if (random.nextBoolean()) {
                    WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.minX() + 1, this.boundingBox.minY() + 3 + 1, this.boundingBox.minZ() - 1, EnumDirection.NORTH, i);
                }

                if (random.nextBoolean()) {
                    WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.minX() - 1, this.boundingBox.minY() + 3 + 1, this.boundingBox.minZ() + 1, EnumDirection.WEST, i);
                }

                if (random.nextBoolean()) {
                    WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.maxX() + 1, this.boundingBox.minY() + 3 + 1, this.boundingBox.minZ() + 1, EnumDirection.EAST, i);
                }

                if (random.nextBoolean()) {
                    WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.minX() + 1, this.boundingBox.minY() + 3 + 1, this.boundingBox.maxZ() + 1, EnumDirection.SOUTH, i);
                }
            }

        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            if (!this.edgesLiquid(generatoraccessseed, structureboundingbox)) {
                IBlockData iblockdata = this.type.getPlanksState();

                if (this.isTwoFloored) {
                    this.generateBox(generatoraccessseed, structureboundingbox, this.boundingBox.minX() + 1, this.boundingBox.minY(), this.boundingBox.minZ(), this.boundingBox.maxX() - 1, this.boundingBox.minY() + 3 - 1, this.boundingBox.maxZ(), WorldGenMineshaftPieces.WorldGenMineshaftCross.CAVE_AIR, WorldGenMineshaftPieces.WorldGenMineshaftCross.CAVE_AIR, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, this.boundingBox.minX(), this.boundingBox.minY(), this.boundingBox.minZ() + 1, this.boundingBox.maxX(), this.boundingBox.minY() + 3 - 1, this.boundingBox.maxZ() - 1, WorldGenMineshaftPieces.WorldGenMineshaftCross.CAVE_AIR, WorldGenMineshaftPieces.WorldGenMineshaftCross.CAVE_AIR, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, this.boundingBox.minX() + 1, this.boundingBox.maxY() - 2, this.boundingBox.minZ(), this.boundingBox.maxX() - 1, this.boundingBox.maxY(), this.boundingBox.maxZ(), WorldGenMineshaftPieces.WorldGenMineshaftCross.CAVE_AIR, WorldGenMineshaftPieces.WorldGenMineshaftCross.CAVE_AIR, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, this.boundingBox.minX(), this.boundingBox.maxY() - 2, this.boundingBox.minZ() + 1, this.boundingBox.maxX(), this.boundingBox.maxY(), this.boundingBox.maxZ() - 1, WorldGenMineshaftPieces.WorldGenMineshaftCross.CAVE_AIR, WorldGenMineshaftPieces.WorldGenMineshaftCross.CAVE_AIR, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, this.boundingBox.minX() + 1, this.boundingBox.minY() + 3, this.boundingBox.minZ() + 1, this.boundingBox.maxX() - 1, this.boundingBox.minY() + 3, this.boundingBox.maxZ() - 1, WorldGenMineshaftPieces.WorldGenMineshaftCross.CAVE_AIR, WorldGenMineshaftPieces.WorldGenMineshaftCross.CAVE_AIR, false);
                } else {
                    this.generateBox(generatoraccessseed, structureboundingbox, this.boundingBox.minX() + 1, this.boundingBox.minY(), this.boundingBox.minZ(), this.boundingBox.maxX() - 1, this.boundingBox.maxY(), this.boundingBox.maxZ(), WorldGenMineshaftPieces.WorldGenMineshaftCross.CAVE_AIR, WorldGenMineshaftPieces.WorldGenMineshaftCross.CAVE_AIR, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, this.boundingBox.minX(), this.boundingBox.minY(), this.boundingBox.minZ() + 1, this.boundingBox.maxX(), this.boundingBox.maxY(), this.boundingBox.maxZ() - 1, WorldGenMineshaftPieces.WorldGenMineshaftCross.CAVE_AIR, WorldGenMineshaftPieces.WorldGenMineshaftCross.CAVE_AIR, false);
                }

                this.placeSupportPillar(generatoraccessseed, structureboundingbox, this.boundingBox.minX() + 1, this.boundingBox.minY(), this.boundingBox.minZ() + 1, this.boundingBox.maxY());
                this.placeSupportPillar(generatoraccessseed, structureboundingbox, this.boundingBox.minX() + 1, this.boundingBox.minY(), this.boundingBox.maxZ() - 1, this.boundingBox.maxY());
                this.placeSupportPillar(generatoraccessseed, structureboundingbox, this.boundingBox.maxX() - 1, this.boundingBox.minY(), this.boundingBox.minZ() + 1, this.boundingBox.maxY());
                this.placeSupportPillar(generatoraccessseed, structureboundingbox, this.boundingBox.maxX() - 1, this.boundingBox.minY(), this.boundingBox.maxZ() - 1, this.boundingBox.maxY());
                int i = this.boundingBox.minY() - 1;

                for (int j = this.boundingBox.minX(); j <= this.boundingBox.maxX(); ++j) {
                    for (int k = this.boundingBox.minZ(); k <= this.boundingBox.maxZ(); ++k) {
                        this.setPlanksBlock(generatoraccessseed, structureboundingbox, iblockdata, j, i, k);
                    }
                }

            }
        }

        private void placeSupportPillar(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, int i, int j, int k, int l) {
            if (!this.getBlock(generatoraccessseed, i, l + 1, k, structureboundingbox).isAir()) {
                this.generateBox(generatoraccessseed, structureboundingbox, i, j, k, i, l, k, this.type.getPlanksState(), WorldGenMineshaftPieces.WorldGenMineshaftCross.CAVE_AIR, false);
            }

        }
    }

    public static class WorldGenMineshaftStairs extends WorldGenMineshaftPieces.c {

        public WorldGenMineshaftStairs(int i, StructureBoundingBox structureboundingbox, EnumDirection enumdirection, WorldGenMineshaft.Type worldgenmineshaft_type) {
            super(WorldGenFeatureStructurePieceType.MINE_SHAFT_STAIRS, i, worldgenmineshaft_type, structureboundingbox);
            this.setOrientation(enumdirection);
        }

        public WorldGenMineshaftStairs(NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.MINE_SHAFT_STAIRS, nbttagcompound);
        }

        @Nullable
        public static StructureBoundingBox findStairs(StructurePieceAccessor structurepieceaccessor, Random random, int i, int j, int k, EnumDirection enumdirection) {
            StructureBoundingBox structureboundingbox;

            switch (enumdirection) {
                case NORTH:
                default:
                    structureboundingbox = new StructureBoundingBox(0, -5, -8, 2, 2, 0);
                    break;
                case SOUTH:
                    structureboundingbox = new StructureBoundingBox(0, -5, 0, 2, 2, 8);
                    break;
                case WEST:
                    structureboundingbox = new StructureBoundingBox(-8, -5, 0, 0, 2, 2);
                    break;
                case EAST:
                    structureboundingbox = new StructureBoundingBox(0, -5, 0, 8, 2, 2);
            }

            structureboundingbox.move(i, j, k);
            return structurepieceaccessor.findCollisionPiece(structureboundingbox) != null ? null : structureboundingbox;
        }

        @Override
        public void addChildren(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, Random random) {
            int i = this.getGenDepth();
            EnumDirection enumdirection = this.getOrientation();

            if (enumdirection != null) {
                switch (enumdirection) {
                    case NORTH:
                    default:
                        WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.minX(), this.boundingBox.minY(), this.boundingBox.minZ() - 1, EnumDirection.NORTH, i);
                        break;
                    case SOUTH:
                        WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.minX(), this.boundingBox.minY(), this.boundingBox.maxZ() + 1, EnumDirection.SOUTH, i);
                        break;
                    case WEST:
                        WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.minX() - 1, this.boundingBox.minY(), this.boundingBox.minZ(), EnumDirection.WEST, i);
                        break;
                    case EAST:
                        WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.maxX() + 1, this.boundingBox.minY(), this.boundingBox.minZ(), EnumDirection.EAST, i);
                }
            }

        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            if (!this.edgesLiquid(generatoraccessseed, structureboundingbox)) {
                this.generateBox(generatoraccessseed, structureboundingbox, 0, 5, 0, 2, 7, 1, WorldGenMineshaftPieces.WorldGenMineshaftStairs.CAVE_AIR, WorldGenMineshaftPieces.WorldGenMineshaftStairs.CAVE_AIR, false);
                this.generateBox(generatoraccessseed, structureboundingbox, 0, 0, 7, 2, 2, 8, WorldGenMineshaftPieces.WorldGenMineshaftStairs.CAVE_AIR, WorldGenMineshaftPieces.WorldGenMineshaftStairs.CAVE_AIR, false);

                for (int i = 0; i < 5; ++i) {
                    this.generateBox(generatoraccessseed, structureboundingbox, 0, 5 - i - (i < 4 ? 1 : 0), 2 + i, 2, 7 - i, 2 + i, WorldGenMineshaftPieces.WorldGenMineshaftStairs.CAVE_AIR, WorldGenMineshaftPieces.WorldGenMineshaftStairs.CAVE_AIR, false);
                }

            }
        }
    }

    public static class WorldGenMineshaftCorridor extends WorldGenMineshaftPieces.c {

        private final boolean hasRails;
        private final boolean spiderCorridor;
        private boolean hasPlacedSpider;
        private final int numSections;

        public WorldGenMineshaftCorridor(NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.MINE_SHAFT_CORRIDOR, nbttagcompound);
            this.hasRails = nbttagcompound.getBoolean("hr");
            this.spiderCorridor = nbttagcompound.getBoolean("sc");
            this.hasPlacedSpider = nbttagcompound.getBoolean("hps");
            this.numSections = nbttagcompound.getInt("Num");
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound) {
            super.addAdditionalSaveData(structurepieceserializationcontext, nbttagcompound);
            nbttagcompound.putBoolean("hr", this.hasRails);
            nbttagcompound.putBoolean("sc", this.spiderCorridor);
            nbttagcompound.putBoolean("hps", this.hasPlacedSpider);
            nbttagcompound.putInt("Num", this.numSections);
        }

        public WorldGenMineshaftCorridor(int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection, WorldGenMineshaft.Type worldgenmineshaft_type) {
            super(WorldGenFeatureStructurePieceType.MINE_SHAFT_CORRIDOR, i, worldgenmineshaft_type, structureboundingbox);
            this.setOrientation(enumdirection);
            this.hasRails = random.nextInt(3) == 0;
            this.spiderCorridor = !this.hasRails && random.nextInt(23) == 0;
            if (this.getOrientation().getAxis() == EnumDirection.EnumAxis.Z) {
                this.numSections = structureboundingbox.getZSpan() / 5;
            } else {
                this.numSections = structureboundingbox.getXSpan() / 5;
            }

        }

        @Nullable
        public static StructureBoundingBox findCorridorSize(StructurePieceAccessor structurepieceaccessor, Random random, int i, int j, int k, EnumDirection enumdirection) {
            for (int l = random.nextInt(3) + 2; l > 0; --l) {
                int i1 = l * 5;
                StructureBoundingBox structureboundingbox;

                switch (enumdirection) {
                    case NORTH:
                    default:
                        structureboundingbox = new StructureBoundingBox(0, 0, -(i1 - 1), 2, 2, 0);
                        break;
                    case SOUTH:
                        structureboundingbox = new StructureBoundingBox(0, 0, 0, 2, 2, i1 - 1);
                        break;
                    case WEST:
                        structureboundingbox = new StructureBoundingBox(-(i1 - 1), 0, 0, 0, 2, 2);
                        break;
                    case EAST:
                        structureboundingbox = new StructureBoundingBox(0, 0, 0, i1 - 1, 2, 2);
                }

                structureboundingbox.move(i, j, k);
                if (structurepieceaccessor.findCollisionPiece(structureboundingbox) == null) {
                    return structureboundingbox;
                }
            }

            return null;
        }

        @Override
        public void addChildren(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, Random random) {
            int i = this.getGenDepth();
            int j = random.nextInt(4);
            EnumDirection enumdirection = this.getOrientation();

            if (enumdirection != null) {
                switch (enumdirection) {
                    case NORTH:
                    default:
                        if (j <= 1) {
                            WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.minX(), this.boundingBox.minY() - 1 + random.nextInt(3), this.boundingBox.minZ() - 1, enumdirection, i);
                        } else if (j == 2) {
                            WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.minX() - 1, this.boundingBox.minY() - 1 + random.nextInt(3), this.boundingBox.minZ(), EnumDirection.WEST, i);
                        } else {
                            WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.maxX() + 1, this.boundingBox.minY() - 1 + random.nextInt(3), this.boundingBox.minZ(), EnumDirection.EAST, i);
                        }
                        break;
                    case SOUTH:
                        if (j <= 1) {
                            WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.minX(), this.boundingBox.minY() - 1 + random.nextInt(3), this.boundingBox.maxZ() + 1, enumdirection, i);
                        } else if (j == 2) {
                            WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.minX() - 1, this.boundingBox.minY() - 1 + random.nextInt(3), this.boundingBox.maxZ() - 3, EnumDirection.WEST, i);
                        } else {
                            WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.maxX() + 1, this.boundingBox.minY() - 1 + random.nextInt(3), this.boundingBox.maxZ() - 3, EnumDirection.EAST, i);
                        }
                        break;
                    case WEST:
                        if (j <= 1) {
                            WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.minX() - 1, this.boundingBox.minY() - 1 + random.nextInt(3), this.boundingBox.minZ(), enumdirection, i);
                        } else if (j == 2) {
                            WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.minX(), this.boundingBox.minY() - 1 + random.nextInt(3), this.boundingBox.minZ() - 1, EnumDirection.NORTH, i);
                        } else {
                            WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.minX(), this.boundingBox.minY() - 1 + random.nextInt(3), this.boundingBox.maxZ() + 1, EnumDirection.SOUTH, i);
                        }
                        break;
                    case EAST:
                        if (j <= 1) {
                            WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.maxX() + 1, this.boundingBox.minY() - 1 + random.nextInt(3), this.boundingBox.minZ(), enumdirection, i);
                        } else if (j == 2) {
                            WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.maxX() - 3, this.boundingBox.minY() - 1 + random.nextInt(3), this.boundingBox.minZ() - 1, EnumDirection.NORTH, i);
                        } else {
                            WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.maxX() - 3, this.boundingBox.minY() - 1 + random.nextInt(3), this.boundingBox.maxZ() + 1, EnumDirection.SOUTH, i);
                        }
                }
            }

            if (i < 8) {
                int k;
                int l;

                if (enumdirection != EnumDirection.NORTH && enumdirection != EnumDirection.SOUTH) {
                    for (l = this.boundingBox.minX() + 3; l + 3 <= this.boundingBox.maxX(); l += 5) {
                        k = random.nextInt(5);
                        if (k == 0) {
                            WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, l, this.boundingBox.minY(), this.boundingBox.minZ() - 1, EnumDirection.NORTH, i + 1);
                        } else if (k == 1) {
                            WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, l, this.boundingBox.minY(), this.boundingBox.maxZ() + 1, EnumDirection.SOUTH, i + 1);
                        }
                    }
                } else {
                    for (l = this.boundingBox.minZ() + 3; l + 3 <= this.boundingBox.maxZ(); l += 5) {
                        k = random.nextInt(5);
                        if (k == 0) {
                            WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.minX() - 1, this.boundingBox.minY(), l, EnumDirection.WEST, i + 1);
                        } else if (k == 1) {
                            WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.maxX() + 1, this.boundingBox.minY(), l, EnumDirection.EAST, i + 1);
                        }
                    }
                }
            }

        }

        @Override
        protected boolean createChest(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, Random random, int i, int j, int k, MinecraftKey minecraftkey) {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.getWorldPos(i, j, k);

            if (structureboundingbox.isInside(blockposition_mutableblockposition) && generatoraccessseed.getBlockState(blockposition_mutableblockposition).isAir() && !generatoraccessseed.getBlockState(blockposition_mutableblockposition.below()).isAir()) {
                IBlockData iblockdata = (IBlockData) Blocks.RAIL.defaultBlockState().setValue(BlockMinecartTrack.SHAPE, random.nextBoolean() ? BlockPropertyTrackPosition.NORTH_SOUTH : BlockPropertyTrackPosition.EAST_WEST);

                this.placeBlock(generatoraccessseed, iblockdata, i, j, k, structureboundingbox);
                EntityMinecartChest entityminecartchest = new EntityMinecartChest(generatoraccessseed.getLevel(), (double) blockposition_mutableblockposition.getX() + 0.5D, (double) blockposition_mutableblockposition.getY() + 0.5D, (double) blockposition_mutableblockposition.getZ() + 0.5D);

                entityminecartchest.setLootTable(minecraftkey, random.nextLong());
                generatoraccessseed.addFreshEntity(entityminecartchest);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            if (!this.edgesLiquid(generatoraccessseed, structureboundingbox)) {
                boolean flag = false;
                boolean flag1 = true;
                boolean flag2 = false;
                boolean flag3 = true;
                int i = this.numSections * 5 - 1;
                IBlockData iblockdata = this.type.getPlanksState();

                this.generateBox(generatoraccessseed, structureboundingbox, 0, 0, 0, 2, 1, i, WorldGenMineshaftPieces.WorldGenMineshaftCorridor.CAVE_AIR, WorldGenMineshaftPieces.WorldGenMineshaftCorridor.CAVE_AIR, false);
                this.generateMaybeBox(generatoraccessseed, structureboundingbox, random, 0.8F, 0, 2, 0, 2, 2, i, WorldGenMineshaftPieces.WorldGenMineshaftCorridor.CAVE_AIR, WorldGenMineshaftPieces.WorldGenMineshaftCorridor.CAVE_AIR, false, false);
                if (this.spiderCorridor) {
                    this.generateMaybeBox(generatoraccessseed, structureboundingbox, random, 0.6F, 0, 0, 0, 2, 1, i, Blocks.COBWEB.defaultBlockState(), WorldGenMineshaftPieces.WorldGenMineshaftCorridor.CAVE_AIR, false, true);
                }

                int j;
                int k;

                for (k = 0; k < this.numSections; ++k) {
                    j = 2 + k * 5;
                    this.placeSupport(generatoraccessseed, structureboundingbox, 0, 0, j, 2, 2, random);
                    this.maybePlaceCobWeb(generatoraccessseed, structureboundingbox, random, 0.1F, 0, 2, j - 1);
                    this.maybePlaceCobWeb(generatoraccessseed, structureboundingbox, random, 0.1F, 2, 2, j - 1);
                    this.maybePlaceCobWeb(generatoraccessseed, structureboundingbox, random, 0.1F, 0, 2, j + 1);
                    this.maybePlaceCobWeb(generatoraccessseed, structureboundingbox, random, 0.1F, 2, 2, j + 1);
                    this.maybePlaceCobWeb(generatoraccessseed, structureboundingbox, random, 0.05F, 0, 2, j - 2);
                    this.maybePlaceCobWeb(generatoraccessseed, structureboundingbox, random, 0.05F, 2, 2, j - 2);
                    this.maybePlaceCobWeb(generatoraccessseed, structureboundingbox, random, 0.05F, 0, 2, j + 2);
                    this.maybePlaceCobWeb(generatoraccessseed, structureboundingbox, random, 0.05F, 2, 2, j + 2);
                    if (random.nextInt(100) == 0) {
                        this.createChest(generatoraccessseed, structureboundingbox, random, 2, 0, j - 1, LootTables.ABANDONED_MINESHAFT);
                    }

                    if (random.nextInt(100) == 0) {
                        this.createChest(generatoraccessseed, structureboundingbox, random, 0, 0, j + 1, LootTables.ABANDONED_MINESHAFT);
                    }

                    if (this.spiderCorridor && !this.hasPlacedSpider) {
                        boolean flag4 = true;
                        int l = j - 1 + random.nextInt(3);
                        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.getWorldPos(1, 0, l);

                        if (structureboundingbox.isInside(blockposition_mutableblockposition) && this.isInterior(generatoraccessseed, 1, 0, l, structureboundingbox)) {
                            this.hasPlacedSpider = true;
                            generatoraccessseed.setBlock(blockposition_mutableblockposition, Blocks.SPAWNER.defaultBlockState(), 2);
                            TileEntity tileentity = generatoraccessseed.getBlockEntity(blockposition_mutableblockposition);

                            if (tileentity instanceof TileEntityMobSpawner) {
                                ((TileEntityMobSpawner) tileentity).getSpawner().setEntityId(EntityTypes.CAVE_SPIDER);
                            }
                        }
                    }
                }

                for (k = 0; k <= 2; ++k) {
                    for (j = 0; j <= i; ++j) {
                        this.setPlanksBlock(generatoraccessseed, structureboundingbox, iblockdata, k, -1, j);
                    }
                }

                boolean flag5 = true;

                this.placeDoubleLowerOrUpperSupport(generatoraccessseed, structureboundingbox, 0, -1, 2);
                if (this.numSections > 1) {
                    j = i - 2;
                    this.placeDoubleLowerOrUpperSupport(generatoraccessseed, structureboundingbox, 0, -1, j);
                }

                if (this.hasRails) {
                    IBlockData iblockdata1 = (IBlockData) Blocks.RAIL.defaultBlockState().setValue(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.NORTH_SOUTH);

                    for (int i1 = 0; i1 <= i; ++i1) {
                        IBlockData iblockdata2 = this.getBlock(generatoraccessseed, 1, -1, i1, structureboundingbox);

                        if (!iblockdata2.isAir() && iblockdata2.isSolidRender(generatoraccessseed, this.getWorldPos(1, -1, i1))) {
                            float f = this.isInterior(generatoraccessseed, 1, 0, i1, structureboundingbox) ? 0.7F : 0.9F;

                            this.maybeGenerateBlock(generatoraccessseed, structureboundingbox, random, f, 1, 0, i1, iblockdata1);
                        }
                    }
                }

            }
        }

        private void placeDoubleLowerOrUpperSupport(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, int i, int j, int k) {
            IBlockData iblockdata = this.type.getWoodState();
            IBlockData iblockdata1 = this.type.getPlanksState();

            if (this.getBlock(generatoraccessseed, i, j, k, structureboundingbox).is(iblockdata1.getBlock())) {
                this.fillPillarDownOrChainUp(generatoraccessseed, iblockdata, i, j, k, structureboundingbox);
            }

            if (this.getBlock(generatoraccessseed, i + 2, j, k, structureboundingbox).is(iblockdata1.getBlock())) {
                this.fillPillarDownOrChainUp(generatoraccessseed, iblockdata, i + 2, j, k, structureboundingbox);
            }

        }

        @Override
        protected void fillColumnDown(GeneratorAccessSeed generatoraccessseed, IBlockData iblockdata, int i, int j, int k, StructureBoundingBox structureboundingbox) {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.getWorldPos(i, j, k);

            if (structureboundingbox.isInside(blockposition_mutableblockposition)) {
                int l = blockposition_mutableblockposition.getY();

                while (this.isReplaceableByStructures(generatoraccessseed.getBlockState(blockposition_mutableblockposition)) && blockposition_mutableblockposition.getY() > generatoraccessseed.getMinBuildHeight() + 1) {
                    blockposition_mutableblockposition.move(EnumDirection.DOWN);
                }

                if (this.canPlaceColumnOnTopOf(generatoraccessseed.getBlockState(blockposition_mutableblockposition))) {
                    while (blockposition_mutableblockposition.getY() < l) {
                        blockposition_mutableblockposition.move(EnumDirection.UP);
                        generatoraccessseed.setBlock(blockposition_mutableblockposition, iblockdata, 2);
                    }

                }
            }
        }

        protected void fillPillarDownOrChainUp(GeneratorAccessSeed generatoraccessseed, IBlockData iblockdata, int i, int j, int k, StructureBoundingBox structureboundingbox) {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.getWorldPos(i, j, k);

            if (structureboundingbox.isInside(blockposition_mutableblockposition)) {
                int l = blockposition_mutableblockposition.getY();
                int i1 = 1;
                boolean flag = true;

                for (boolean flag1 = true; flag || flag1; ++i1) {
                    IBlockData iblockdata1;
                    boolean flag2;

                    if (flag) {
                        blockposition_mutableblockposition.setY(l - i1);
                        iblockdata1 = generatoraccessseed.getBlockState(blockposition_mutableblockposition);
                        flag2 = this.isReplaceableByStructures(iblockdata1) && !iblockdata1.is(Blocks.LAVA);
                        if (!flag2 && this.canPlaceColumnOnTopOf(iblockdata1)) {
                            fillColumnBetween(generatoraccessseed, iblockdata, blockposition_mutableblockposition, l - i1 + 1, l);
                            return;
                        }

                        flag = i1 <= 20 && flag2 && blockposition_mutableblockposition.getY() > generatoraccessseed.getMinBuildHeight() + 1;
                    }

                    if (flag1) {
                        blockposition_mutableblockposition.setY(l + i1);
                        iblockdata1 = generatoraccessseed.getBlockState(blockposition_mutableblockposition);
                        flag2 = this.isReplaceableByStructures(iblockdata1);
                        if (!flag2 && this.canHangChainBelow(generatoraccessseed, blockposition_mutableblockposition, iblockdata1)) {
                            generatoraccessseed.setBlock(blockposition_mutableblockposition.setY(l + 1), this.type.getFenceState(), 2);
                            fillColumnBetween(generatoraccessseed, Blocks.CHAIN.defaultBlockState(), blockposition_mutableblockposition, l + 2, l + i1);
                            return;
                        }

                        flag1 = i1 <= 50 && flag2 && blockposition_mutableblockposition.getY() < generatoraccessseed.getMaxBuildHeight() - 1;
                    }
                }

            }
        }

        private static void fillColumnBetween(GeneratorAccessSeed generatoraccessseed, IBlockData iblockdata, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, int i, int j) {
            for (int k = i; k < j; ++k) {
                generatoraccessseed.setBlock(blockposition_mutableblockposition.setY(k), iblockdata, 2);
            }

        }

        private boolean canPlaceColumnOnTopOf(IBlockData iblockdata) {
            return !iblockdata.is(Blocks.RAIL) && !iblockdata.is(Blocks.LAVA);
        }

        private boolean canHangChainBelow(IWorldReader iworldreader, BlockPosition blockposition, IBlockData iblockdata) {
            return Block.canSupportCenter(iworldreader, blockposition, EnumDirection.DOWN) && !(iblockdata.getBlock() instanceof BlockFalling);
        }

        private void placeSupport(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, int i, int j, int k, int l, int i1, Random random) {
            if (this.isSupportingBox(generatoraccessseed, structureboundingbox, i, i1, l, k)) {
                IBlockData iblockdata = this.type.getPlanksState();
                IBlockData iblockdata1 = this.type.getFenceState();

                this.generateBox(generatoraccessseed, structureboundingbox, i, j, k, i, l - 1, k, (IBlockData) iblockdata1.setValue(BlockFence.WEST, true), WorldGenMineshaftPieces.WorldGenMineshaftCorridor.CAVE_AIR, false);
                this.generateBox(generatoraccessseed, structureboundingbox, i1, j, k, i1, l - 1, k, (IBlockData) iblockdata1.setValue(BlockFence.EAST, true), WorldGenMineshaftPieces.WorldGenMineshaftCorridor.CAVE_AIR, false);
                if (random.nextInt(4) == 0) {
                    this.generateBox(generatoraccessseed, structureboundingbox, i, l, k, i, l, k, iblockdata, WorldGenMineshaftPieces.WorldGenMineshaftCorridor.CAVE_AIR, false);
                    this.generateBox(generatoraccessseed, structureboundingbox, i1, l, k, i1, l, k, iblockdata, WorldGenMineshaftPieces.WorldGenMineshaftCorridor.CAVE_AIR, false);
                } else {
                    this.generateBox(generatoraccessseed, structureboundingbox, i, l, k, i1, l, k, iblockdata, WorldGenMineshaftPieces.WorldGenMineshaftCorridor.CAVE_AIR, false);
                    this.maybeGenerateBlock(generatoraccessseed, structureboundingbox, random, 0.05F, i + 1, l, k - 1, (IBlockData) Blocks.WALL_TORCH.defaultBlockState().setValue(BlockTorchWall.FACING, EnumDirection.SOUTH));
                    this.maybeGenerateBlock(generatoraccessseed, structureboundingbox, random, 0.05F, i + 1, l, k + 1, (IBlockData) Blocks.WALL_TORCH.defaultBlockState().setValue(BlockTorchWall.FACING, EnumDirection.NORTH));
                }

            }
        }

        private void maybePlaceCobWeb(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, Random random, float f, int i, int j, int k) {
            if (this.isInterior(generatoraccessseed, i, j, k, structureboundingbox) && random.nextFloat() < f && this.hasSturdyNeighbours(generatoraccessseed, structureboundingbox, i, j, k, 2)) {
                this.placeBlock(generatoraccessseed, Blocks.COBWEB.defaultBlockState(), i, j, k, structureboundingbox);
            }

        }

        private boolean hasSturdyNeighbours(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, int i, int j, int k, int l) {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.getWorldPos(i, j, k);
            int i1 = 0;
            EnumDirection[] aenumdirection = EnumDirection.values();
            int j1 = aenumdirection.length;

            for (int k1 = 0; k1 < j1; ++k1) {
                EnumDirection enumdirection = aenumdirection[k1];

                blockposition_mutableblockposition.move(enumdirection);
                if (structureboundingbox.isInside(blockposition_mutableblockposition) && generatoraccessseed.getBlockState(blockposition_mutableblockposition).isFaceSturdy(generatoraccessseed, blockposition_mutableblockposition, enumdirection.getOpposite())) {
                    ++i1;
                    if (i1 >= l) {
                        return true;
                    }
                }

                blockposition_mutableblockposition.move(enumdirection.getOpposite());
            }

            return false;
        }
    }

    private abstract static class c extends StructurePiece {

        protected WorldGenMineshaft.Type type;

        public c(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, int i, WorldGenMineshaft.Type worldgenmineshaft_type, StructureBoundingBox structureboundingbox) {
            super(worldgenfeaturestructurepiecetype, i, structureboundingbox);
            this.type = worldgenmineshaft_type;
        }

        public c(WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype, NBTTagCompound nbttagcompound) {
            super(worldgenfeaturestructurepiecetype, nbttagcompound);
            this.type = WorldGenMineshaft.Type.byId(nbttagcompound.getInt("MST"));
        }

        @Override
        protected boolean canBeReplaced(IWorldReader iworldreader, int i, int j, int k, StructureBoundingBox structureboundingbox) {
            IBlockData iblockdata = this.getBlock(iworldreader, i, j, k, structureboundingbox);

            return !iblockdata.is(this.type.getPlanksState().getBlock()) && !iblockdata.is(this.type.getWoodState().getBlock()) && !iblockdata.is(this.type.getFenceState().getBlock()) && !iblockdata.is(Blocks.CHAIN);
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound) {
            nbttagcompound.putInt("MST", this.type.ordinal());
        }

        protected boolean isSupportingBox(IBlockAccess iblockaccess, StructureBoundingBox structureboundingbox, int i, int j, int k, int l) {
            for (int i1 = i; i1 <= j; ++i1) {
                if (this.getBlock(iblockaccess, i1, k + 1, l, structureboundingbox).isAir()) {
                    return false;
                }
            }

            return true;
        }

        protected boolean edgesLiquid(IBlockAccess iblockaccess, StructureBoundingBox structureboundingbox) {
            int i = Math.max(this.boundingBox.minX() - 1, structureboundingbox.minX());
            int j = Math.max(this.boundingBox.minY() - 1, structureboundingbox.minY());
            int k = Math.max(this.boundingBox.minZ() - 1, structureboundingbox.minZ());
            int l = Math.min(this.boundingBox.maxX() + 1, structureboundingbox.maxX());
            int i1 = Math.min(this.boundingBox.maxY() + 1, structureboundingbox.maxY());
            int j1 = Math.min(this.boundingBox.maxZ() + 1, structureboundingbox.maxZ());
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            int k1;
            int l1;

            for (k1 = i; k1 <= l; ++k1) {
                for (l1 = k; l1 <= j1; ++l1) {
                    if (iblockaccess.getBlockState(blockposition_mutableblockposition.set(k1, j, l1)).getMaterial().isLiquid()) {
                        return true;
                    }

                    if (iblockaccess.getBlockState(blockposition_mutableblockposition.set(k1, i1, l1)).getMaterial().isLiquid()) {
                        return true;
                    }
                }
            }

            for (k1 = i; k1 <= l; ++k1) {
                for (l1 = j; l1 <= i1; ++l1) {
                    if (iblockaccess.getBlockState(blockposition_mutableblockposition.set(k1, l1, k)).getMaterial().isLiquid()) {
                        return true;
                    }

                    if (iblockaccess.getBlockState(blockposition_mutableblockposition.set(k1, l1, j1)).getMaterial().isLiquid()) {
                        return true;
                    }
                }
            }

            for (k1 = k; k1 <= j1; ++k1) {
                for (l1 = j; l1 <= i1; ++l1) {
                    if (iblockaccess.getBlockState(blockposition_mutableblockposition.set(i, l1, k1)).getMaterial().isLiquid()) {
                        return true;
                    }

                    if (iblockaccess.getBlockState(blockposition_mutableblockposition.set(l, l1, k1)).getMaterial().isLiquid()) {
                        return true;
                    }
                }
            }

            return false;
        }

        protected void setPlanksBlock(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, IBlockData iblockdata, int i, int j, int k) {
            if (this.isInterior(generatoraccessseed, i, j, k, structureboundingbox)) {
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.getWorldPos(i, j, k);
                IBlockData iblockdata1 = generatoraccessseed.getBlockState(blockposition_mutableblockposition);

                if (iblockdata1.isAir() || iblockdata1.is(Blocks.CHAIN)) {
                    generatoraccessseed.setBlock(blockposition_mutableblockposition, iblockdata, 2);
                }

            }
        }
    }

    public static class WorldGenMineshaftRoom extends WorldGenMineshaftPieces.c {

        private final List<StructureBoundingBox> childEntranceBoxes = Lists.newLinkedList();

        public WorldGenMineshaftRoom(int i, Random random, int j, int k, WorldGenMineshaft.Type worldgenmineshaft_type) {
            super(WorldGenFeatureStructurePieceType.MINE_SHAFT_ROOM, i, worldgenmineshaft_type, new StructureBoundingBox(j, 50, k, j + 7 + random.nextInt(6), 54 + random.nextInt(6), k + 7 + random.nextInt(6)));
            this.type = worldgenmineshaft_type;
        }

        public WorldGenMineshaftRoom(NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.MINE_SHAFT_ROOM, nbttagcompound);
            DataResult dataresult = StructureBoundingBox.CODEC.listOf().parse(DynamicOpsNBT.INSTANCE, nbttagcompound.getList("Entrances", 11));
            Logger logger = WorldGenMineshaftPieces.LOGGER;

            Objects.requireNonNull(logger);
            Optional optional = dataresult.resultOrPartial(logger::error);
            List list = this.childEntranceBoxes;

            Objects.requireNonNull(this.childEntranceBoxes);
            optional.ifPresent(list::addAll);
        }

        @Override
        public void addChildren(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, Random random) {
            int i = this.getGenDepth();
            int j = this.boundingBox.getYSpan() - 3 - 1;

            if (j <= 0) {
                j = 1;
            }

            int k;
            WorldGenMineshaftPieces.c worldgenmineshaftpieces_c;
            StructureBoundingBox structureboundingbox;

            for (k = 0; k < this.boundingBox.getXSpan(); k += 4) {
                k += random.nextInt(this.boundingBox.getXSpan());
                if (k + 3 > this.boundingBox.getXSpan()) {
                    break;
                }

                worldgenmineshaftpieces_c = WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.minX() + k, this.boundingBox.minY() + random.nextInt(j) + 1, this.boundingBox.minZ() - 1, EnumDirection.NORTH, i);
                if (worldgenmineshaftpieces_c != null) {
                    structureboundingbox = worldgenmineshaftpieces_c.getBoundingBox();
                    this.childEntranceBoxes.add(new StructureBoundingBox(structureboundingbox.minX(), structureboundingbox.minY(), this.boundingBox.minZ(), structureboundingbox.maxX(), structureboundingbox.maxY(), this.boundingBox.minZ() + 1));
                }
            }

            for (k = 0; k < this.boundingBox.getXSpan(); k += 4) {
                k += random.nextInt(this.boundingBox.getXSpan());
                if (k + 3 > this.boundingBox.getXSpan()) {
                    break;
                }

                worldgenmineshaftpieces_c = WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.minX() + k, this.boundingBox.minY() + random.nextInt(j) + 1, this.boundingBox.maxZ() + 1, EnumDirection.SOUTH, i);
                if (worldgenmineshaftpieces_c != null) {
                    structureboundingbox = worldgenmineshaftpieces_c.getBoundingBox();
                    this.childEntranceBoxes.add(new StructureBoundingBox(structureboundingbox.minX(), structureboundingbox.minY(), this.boundingBox.maxZ() - 1, structureboundingbox.maxX(), structureboundingbox.maxY(), this.boundingBox.maxZ()));
                }
            }

            for (k = 0; k < this.boundingBox.getZSpan(); k += 4) {
                k += random.nextInt(this.boundingBox.getZSpan());
                if (k + 3 > this.boundingBox.getZSpan()) {
                    break;
                }

                worldgenmineshaftpieces_c = WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.minX() - 1, this.boundingBox.minY() + random.nextInt(j) + 1, this.boundingBox.minZ() + k, EnumDirection.WEST, i);
                if (worldgenmineshaftpieces_c != null) {
                    structureboundingbox = worldgenmineshaftpieces_c.getBoundingBox();
                    this.childEntranceBoxes.add(new StructureBoundingBox(this.boundingBox.minX(), structureboundingbox.minY(), structureboundingbox.minZ(), this.boundingBox.minX() + 1, structureboundingbox.maxY(), structureboundingbox.maxZ()));
                }
            }

            for (k = 0; k < this.boundingBox.getZSpan(); k += 4) {
                k += random.nextInt(this.boundingBox.getZSpan());
                if (k + 3 > this.boundingBox.getZSpan()) {
                    break;
                }

                worldgenmineshaftpieces_c = WorldGenMineshaftPieces.generateAndAddPiece(structurepiece, structurepieceaccessor, random, this.boundingBox.maxX() + 1, this.boundingBox.minY() + random.nextInt(j) + 1, this.boundingBox.minZ() + k, EnumDirection.EAST, i);
                if (worldgenmineshaftpieces_c != null) {
                    structureboundingbox = worldgenmineshaftpieces_c.getBoundingBox();
                    this.childEntranceBoxes.add(new StructureBoundingBox(this.boundingBox.maxX() - 1, structureboundingbox.minY(), structureboundingbox.minZ(), this.boundingBox.maxX(), structureboundingbox.maxY(), structureboundingbox.maxZ()));
                }
            }

        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            if (!this.edgesLiquid(generatoraccessseed, structureboundingbox)) {
                this.generateBox(generatoraccessseed, structureboundingbox, this.boundingBox.minX(), this.boundingBox.minY() + 1, this.boundingBox.minZ(), this.boundingBox.maxX(), Math.min(this.boundingBox.minY() + 3, this.boundingBox.maxY()), this.boundingBox.maxZ(), WorldGenMineshaftPieces.WorldGenMineshaftRoom.CAVE_AIR, WorldGenMineshaftPieces.WorldGenMineshaftRoom.CAVE_AIR, false);
                Iterator iterator = this.childEntranceBoxes.iterator();

                while (iterator.hasNext()) {
                    StructureBoundingBox structureboundingbox1 = (StructureBoundingBox) iterator.next();

                    this.generateBox(generatoraccessseed, structureboundingbox, structureboundingbox1.minX(), structureboundingbox1.maxY() - 2, structureboundingbox1.minZ(), structureboundingbox1.maxX(), structureboundingbox1.maxY(), structureboundingbox1.maxZ(), WorldGenMineshaftPieces.WorldGenMineshaftRoom.CAVE_AIR, WorldGenMineshaftPieces.WorldGenMineshaftRoom.CAVE_AIR, false);
                }

                this.generateUpperHalfSphere(generatoraccessseed, structureboundingbox, this.boundingBox.minX(), this.boundingBox.minY() + 4, this.boundingBox.minZ(), this.boundingBox.maxX(), this.boundingBox.maxY(), this.boundingBox.maxZ(), WorldGenMineshaftPieces.WorldGenMineshaftRoom.CAVE_AIR, false);
            }
        }

        @Override
        public void move(int i, int j, int k) {
            super.move(i, j, k);
            Iterator iterator = this.childEntranceBoxes.iterator();

            while (iterator.hasNext()) {
                StructureBoundingBox structureboundingbox = (StructureBoundingBox) iterator.next();

                structureboundingbox.move(i, j, k);
            }

        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound) {
            super.addAdditionalSaveData(structurepieceserializationcontext, nbttagcompound);
            DataResult dataresult = StructureBoundingBox.CODEC.listOf().encodeStart(DynamicOpsNBT.INSTANCE, this.childEntranceBoxes);
            Logger logger = WorldGenMineshaftPieces.LOGGER;

            Objects.requireNonNull(logger);
            dataresult.resultOrPartial(logger::error).ifPresent((nbtbase) -> {
                nbttagcompound.put("Entrances", nbtbase);
            });
        }
    }
}
