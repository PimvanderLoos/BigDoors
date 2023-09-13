package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.DataResult;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
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

    public WorldGenMineshaftPieces() {}

    private static WorldGenMineshaftPieces.c a(StructurePieceAccessor structurepieceaccessor, Random random, int i, int j, int k, @Nullable EnumDirection enumdirection, int l, WorldGenMineshaft.Type worldgenmineshaft_type) {
        int i1 = random.nextInt(100);
        StructureBoundingBox structureboundingbox;

        if (i1 >= 80) {
            structureboundingbox = WorldGenMineshaftPieces.WorldGenMineshaftCross.a(structurepieceaccessor, random, i, j, k, enumdirection);
            if (structureboundingbox != null) {
                return new WorldGenMineshaftPieces.WorldGenMineshaftCross(l, structureboundingbox, enumdirection, worldgenmineshaft_type);
            }
        } else if (i1 >= 70) {
            structureboundingbox = WorldGenMineshaftPieces.WorldGenMineshaftStairs.a(structurepieceaccessor, random, i, j, k, enumdirection);
            if (structureboundingbox != null) {
                return new WorldGenMineshaftPieces.WorldGenMineshaftStairs(l, structureboundingbox, enumdirection, worldgenmineshaft_type);
            }
        } else {
            structureboundingbox = WorldGenMineshaftPieces.WorldGenMineshaftCorridor.a(structurepieceaccessor, random, i, j, k, enumdirection);
            if (structureboundingbox != null) {
                return new WorldGenMineshaftPieces.WorldGenMineshaftCorridor(l, random, structureboundingbox, enumdirection, worldgenmineshaft_type);
            }
        }

        return null;
    }

    static WorldGenMineshaftPieces.c a(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, Random random, int i, int j, int k, EnumDirection enumdirection, int l) {
        if (l > 8) {
            return null;
        } else if (Math.abs(i - structurepiece.f().g()) <= 80 && Math.abs(k - structurepiece.f().i()) <= 80) {
            WorldGenMineshaft.Type worldgenmineshaft_type = ((WorldGenMineshaftPieces.c) structurepiece).type;
            WorldGenMineshaftPieces.c worldgenmineshaftpieces_c = a(structurepieceaccessor, random, i, j, k, enumdirection, l + 1, worldgenmineshaft_type);

            if (worldgenmineshaftpieces_c != null) {
                structurepieceaccessor.a((StructurePiece) worldgenmineshaftpieces_c);
                worldgenmineshaftpieces_c.a(structurepiece, structurepieceaccessor, random);
            }

            return worldgenmineshaftpieces_c;
        } else {
            return null;
        }
    }

    public static class WorldGenMineshaftCross extends WorldGenMineshaftPieces.c {

        private final EnumDirection direction;
        private final boolean isTwoFloored;

        public WorldGenMineshaftCross(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.MINE_SHAFT_CROSSING, nbttagcompound);
            this.isTwoFloored = nbttagcompound.getBoolean("tf");
            this.direction = EnumDirection.fromType2(nbttagcompound.getInt("D"));
        }

        @Override
        protected void a(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super.a(worldserver, nbttagcompound);
            nbttagcompound.setBoolean("tf", this.isTwoFloored);
            nbttagcompound.setInt("D", this.direction.get2DRotationValue());
        }

        public WorldGenMineshaftCross(int i, StructureBoundingBox structureboundingbox, @Nullable EnumDirection enumdirection, WorldGenMineshaft.Type worldgenmineshaft_type) {
            super(WorldGenFeatureStructurePieceType.MINE_SHAFT_CROSSING, i, worldgenmineshaft_type, structureboundingbox);
            this.direction = enumdirection;
            this.isTwoFloored = structureboundingbox.d() > 3;
        }

        @Nullable
        public static StructureBoundingBox a(StructurePieceAccessor structurepieceaccessor, Random random, int i, int j, int k, EnumDirection enumdirection) {
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

            structureboundingbox.a(i, j, k);
            return structurepieceaccessor.a(structureboundingbox) != null ? null : structureboundingbox;
        }

        @Override
        public void a(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, Random random) {
            int i = this.g();

            switch (this.direction) {
                case NORTH:
                default:
                    WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.g() + 1, this.boundingBox.h(), this.boundingBox.i() - 1, EnumDirection.NORTH, i);
                    WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.g() - 1, this.boundingBox.h(), this.boundingBox.i() + 1, EnumDirection.WEST, i);
                    WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.j() + 1, this.boundingBox.h(), this.boundingBox.i() + 1, EnumDirection.EAST, i);
                    break;
                case SOUTH:
                    WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.g() + 1, this.boundingBox.h(), this.boundingBox.l() + 1, EnumDirection.SOUTH, i);
                    WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.g() - 1, this.boundingBox.h(), this.boundingBox.i() + 1, EnumDirection.WEST, i);
                    WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.j() + 1, this.boundingBox.h(), this.boundingBox.i() + 1, EnumDirection.EAST, i);
                    break;
                case WEST:
                    WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.g() + 1, this.boundingBox.h(), this.boundingBox.i() - 1, EnumDirection.NORTH, i);
                    WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.g() + 1, this.boundingBox.h(), this.boundingBox.l() + 1, EnumDirection.SOUTH, i);
                    WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.g() - 1, this.boundingBox.h(), this.boundingBox.i() + 1, EnumDirection.WEST, i);
                    break;
                case EAST:
                    WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.g() + 1, this.boundingBox.h(), this.boundingBox.i() - 1, EnumDirection.NORTH, i);
                    WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.g() + 1, this.boundingBox.h(), this.boundingBox.l() + 1, EnumDirection.SOUTH, i);
                    WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.j() + 1, this.boundingBox.h(), this.boundingBox.i() + 1, EnumDirection.EAST, i);
            }

            if (this.isTwoFloored) {
                if (random.nextBoolean()) {
                    WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.g() + 1, this.boundingBox.h() + 3 + 1, this.boundingBox.i() - 1, EnumDirection.NORTH, i);
                }

                if (random.nextBoolean()) {
                    WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.g() - 1, this.boundingBox.h() + 3 + 1, this.boundingBox.i() + 1, EnumDirection.WEST, i);
                }

                if (random.nextBoolean()) {
                    WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.j() + 1, this.boundingBox.h() + 3 + 1, this.boundingBox.i() + 1, EnumDirection.EAST, i);
                }

                if (random.nextBoolean()) {
                    WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.g() + 1, this.boundingBox.h() + 3 + 1, this.boundingBox.l() + 1, EnumDirection.SOUTH, i);
                }
            }

        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            if (this.a((IBlockAccess) generatoraccessseed, structureboundingbox)) {
                return false;
            } else {
                IBlockData iblockdata = this.type.d();

                if (this.isTwoFloored) {
                    this.a(generatoraccessseed, structureboundingbox, this.boundingBox.g() + 1, this.boundingBox.h(), this.boundingBox.i(), this.boundingBox.j() - 1, this.boundingBox.h() + 3 - 1, this.boundingBox.l(), WorldGenMineshaftPieces.WorldGenMineshaftCross.CAVE_AIR, WorldGenMineshaftPieces.WorldGenMineshaftCross.CAVE_AIR, false);
                    this.a(generatoraccessseed, structureboundingbox, this.boundingBox.g(), this.boundingBox.h(), this.boundingBox.i() + 1, this.boundingBox.j(), this.boundingBox.h() + 3 - 1, this.boundingBox.l() - 1, WorldGenMineshaftPieces.WorldGenMineshaftCross.CAVE_AIR, WorldGenMineshaftPieces.WorldGenMineshaftCross.CAVE_AIR, false);
                    this.a(generatoraccessseed, structureboundingbox, this.boundingBox.g() + 1, this.boundingBox.k() - 2, this.boundingBox.i(), this.boundingBox.j() - 1, this.boundingBox.k(), this.boundingBox.l(), WorldGenMineshaftPieces.WorldGenMineshaftCross.CAVE_AIR, WorldGenMineshaftPieces.WorldGenMineshaftCross.CAVE_AIR, false);
                    this.a(generatoraccessseed, structureboundingbox, this.boundingBox.g(), this.boundingBox.k() - 2, this.boundingBox.i() + 1, this.boundingBox.j(), this.boundingBox.k(), this.boundingBox.l() - 1, WorldGenMineshaftPieces.WorldGenMineshaftCross.CAVE_AIR, WorldGenMineshaftPieces.WorldGenMineshaftCross.CAVE_AIR, false);
                    this.a(generatoraccessseed, structureboundingbox, this.boundingBox.g() + 1, this.boundingBox.h() + 3, this.boundingBox.i() + 1, this.boundingBox.j() - 1, this.boundingBox.h() + 3, this.boundingBox.l() - 1, WorldGenMineshaftPieces.WorldGenMineshaftCross.CAVE_AIR, WorldGenMineshaftPieces.WorldGenMineshaftCross.CAVE_AIR, false);
                } else {
                    this.a(generatoraccessseed, structureboundingbox, this.boundingBox.g() + 1, this.boundingBox.h(), this.boundingBox.i(), this.boundingBox.j() - 1, this.boundingBox.k(), this.boundingBox.l(), WorldGenMineshaftPieces.WorldGenMineshaftCross.CAVE_AIR, WorldGenMineshaftPieces.WorldGenMineshaftCross.CAVE_AIR, false);
                    this.a(generatoraccessseed, structureboundingbox, this.boundingBox.g(), this.boundingBox.h(), this.boundingBox.i() + 1, this.boundingBox.j(), this.boundingBox.k(), this.boundingBox.l() - 1, WorldGenMineshaftPieces.WorldGenMineshaftCross.CAVE_AIR, WorldGenMineshaftPieces.WorldGenMineshaftCross.CAVE_AIR, false);
                }

                this.a(generatoraccessseed, structureboundingbox, this.boundingBox.g() + 1, this.boundingBox.h(), this.boundingBox.i() + 1, this.boundingBox.k());
                this.a(generatoraccessseed, structureboundingbox, this.boundingBox.g() + 1, this.boundingBox.h(), this.boundingBox.l() - 1, this.boundingBox.k());
                this.a(generatoraccessseed, structureboundingbox, this.boundingBox.j() - 1, this.boundingBox.h(), this.boundingBox.i() + 1, this.boundingBox.k());
                this.a(generatoraccessseed, structureboundingbox, this.boundingBox.j() - 1, this.boundingBox.h(), this.boundingBox.l() - 1, this.boundingBox.k());
                int i = this.boundingBox.h() - 1;

                for (int j = this.boundingBox.g(); j <= this.boundingBox.j(); ++j) {
                    for (int k = this.boundingBox.i(); k <= this.boundingBox.l(); ++k) {
                        this.a(generatoraccessseed, structureboundingbox, iblockdata, j, i, k);
                    }
                }

                return true;
            }
        }

        private void a(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, int i, int j, int k, int l) {
            if (!this.a((IBlockAccess) generatoraccessseed, i, l + 1, k, structureboundingbox).isAir()) {
                this.a(generatoraccessseed, structureboundingbox, i, j, k, i, l, k, this.type.d(), WorldGenMineshaftPieces.WorldGenMineshaftCross.CAVE_AIR, false);
            }

        }
    }

    public static class WorldGenMineshaftStairs extends WorldGenMineshaftPieces.c {

        public WorldGenMineshaftStairs(int i, StructureBoundingBox structureboundingbox, EnumDirection enumdirection, WorldGenMineshaft.Type worldgenmineshaft_type) {
            super(WorldGenFeatureStructurePieceType.MINE_SHAFT_STAIRS, i, worldgenmineshaft_type, structureboundingbox);
            this.a(enumdirection);
        }

        public WorldGenMineshaftStairs(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.MINE_SHAFT_STAIRS, nbttagcompound);
        }

        @Nullable
        public static StructureBoundingBox a(StructurePieceAccessor structurepieceaccessor, Random random, int i, int j, int k, EnumDirection enumdirection) {
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

            structureboundingbox.a(i, j, k);
            return structurepieceaccessor.a(structureboundingbox) != null ? null : structureboundingbox;
        }

        @Override
        public void a(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, Random random) {
            int i = this.g();
            EnumDirection enumdirection = this.h();

            if (enumdirection != null) {
                switch (enumdirection) {
                    case NORTH:
                    default:
                        WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.g(), this.boundingBox.h(), this.boundingBox.i() - 1, EnumDirection.NORTH, i);
                        break;
                    case SOUTH:
                        WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.g(), this.boundingBox.h(), this.boundingBox.l() + 1, EnumDirection.SOUTH, i);
                        break;
                    case WEST:
                        WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.g() - 1, this.boundingBox.h(), this.boundingBox.i(), EnumDirection.WEST, i);
                        break;
                    case EAST:
                        WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.j() + 1, this.boundingBox.h(), this.boundingBox.i(), EnumDirection.EAST, i);
                }
            }

        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            if (this.a((IBlockAccess) generatoraccessseed, structureboundingbox)) {
                return false;
            } else {
                this.a(generatoraccessseed, structureboundingbox, 0, 5, 0, 2, 7, 1, WorldGenMineshaftPieces.WorldGenMineshaftStairs.CAVE_AIR, WorldGenMineshaftPieces.WorldGenMineshaftStairs.CAVE_AIR, false);
                this.a(generatoraccessseed, structureboundingbox, 0, 0, 7, 2, 2, 8, WorldGenMineshaftPieces.WorldGenMineshaftStairs.CAVE_AIR, WorldGenMineshaftPieces.WorldGenMineshaftStairs.CAVE_AIR, false);

                for (int i = 0; i < 5; ++i) {
                    this.a(generatoraccessseed, structureboundingbox, 0, 5 - i - (i < 4 ? 1 : 0), 2 + i, 2, 7 - i, 2 + i, WorldGenMineshaftPieces.WorldGenMineshaftStairs.CAVE_AIR, WorldGenMineshaftPieces.WorldGenMineshaftStairs.CAVE_AIR, false);
                }

                return true;
            }
        }
    }

    public static class WorldGenMineshaftCorridor extends WorldGenMineshaftPieces.c {

        private final boolean hasRails;
        private final boolean spiderCorridor;
        private boolean hasPlacedSpider;
        private final int numSections;

        public WorldGenMineshaftCorridor(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.MINE_SHAFT_CORRIDOR, nbttagcompound);
            this.hasRails = nbttagcompound.getBoolean("hr");
            this.spiderCorridor = nbttagcompound.getBoolean("sc");
            this.hasPlacedSpider = nbttagcompound.getBoolean("hps");
            this.numSections = nbttagcompound.getInt("Num");
        }

        @Override
        protected void a(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super.a(worldserver, nbttagcompound);
            nbttagcompound.setBoolean("hr", this.hasRails);
            nbttagcompound.setBoolean("sc", this.spiderCorridor);
            nbttagcompound.setBoolean("hps", this.hasPlacedSpider);
            nbttagcompound.setInt("Num", this.numSections);
        }

        public WorldGenMineshaftCorridor(int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection, WorldGenMineshaft.Type worldgenmineshaft_type) {
            super(WorldGenFeatureStructurePieceType.MINE_SHAFT_CORRIDOR, i, worldgenmineshaft_type, structureboundingbox);
            this.a(enumdirection);
            this.hasRails = random.nextInt(3) == 0;
            this.spiderCorridor = !this.hasRails && random.nextInt(23) == 0;
            if (this.h().n() == EnumDirection.EnumAxis.Z) {
                this.numSections = structureboundingbox.e() / 5;
            } else {
                this.numSections = structureboundingbox.c() / 5;
            }

        }

        @Nullable
        public static StructureBoundingBox a(StructurePieceAccessor structurepieceaccessor, Random random, int i, int j, int k, EnumDirection enumdirection) {
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

                structureboundingbox.a(i, j, k);
                if (structurepieceaccessor.a(structureboundingbox) == null) {
                    return structureboundingbox;
                }
            }

            return null;
        }

        @Override
        public void a(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, Random random) {
            int i = this.g();
            int j = random.nextInt(4);
            EnumDirection enumdirection = this.h();

            if (enumdirection != null) {
                switch (enumdirection) {
                    case NORTH:
                    default:
                        if (j <= 1) {
                            WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.g(), this.boundingBox.h() - 1 + random.nextInt(3), this.boundingBox.i() - 1, enumdirection, i);
                        } else if (j == 2) {
                            WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.g() - 1, this.boundingBox.h() - 1 + random.nextInt(3), this.boundingBox.i(), EnumDirection.WEST, i);
                        } else {
                            WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.j() + 1, this.boundingBox.h() - 1 + random.nextInt(3), this.boundingBox.i(), EnumDirection.EAST, i);
                        }
                        break;
                    case SOUTH:
                        if (j <= 1) {
                            WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.g(), this.boundingBox.h() - 1 + random.nextInt(3), this.boundingBox.l() + 1, enumdirection, i);
                        } else if (j == 2) {
                            WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.g() - 1, this.boundingBox.h() - 1 + random.nextInt(3), this.boundingBox.l() - 3, EnumDirection.WEST, i);
                        } else {
                            WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.j() + 1, this.boundingBox.h() - 1 + random.nextInt(3), this.boundingBox.l() - 3, EnumDirection.EAST, i);
                        }
                        break;
                    case WEST:
                        if (j <= 1) {
                            WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.g() - 1, this.boundingBox.h() - 1 + random.nextInt(3), this.boundingBox.i(), enumdirection, i);
                        } else if (j == 2) {
                            WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.g(), this.boundingBox.h() - 1 + random.nextInt(3), this.boundingBox.i() - 1, EnumDirection.NORTH, i);
                        } else {
                            WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.g(), this.boundingBox.h() - 1 + random.nextInt(3), this.boundingBox.l() + 1, EnumDirection.SOUTH, i);
                        }
                        break;
                    case EAST:
                        if (j <= 1) {
                            WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.j() + 1, this.boundingBox.h() - 1 + random.nextInt(3), this.boundingBox.i(), enumdirection, i);
                        } else if (j == 2) {
                            WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.j() - 3, this.boundingBox.h() - 1 + random.nextInt(3), this.boundingBox.i() - 1, EnumDirection.NORTH, i);
                        } else {
                            WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.j() - 3, this.boundingBox.h() - 1 + random.nextInt(3), this.boundingBox.l() + 1, EnumDirection.SOUTH, i);
                        }
                }
            }

            if (i < 8) {
                int k;
                int l;

                if (enumdirection != EnumDirection.NORTH && enumdirection != EnumDirection.SOUTH) {
                    for (l = this.boundingBox.g() + 3; l + 3 <= this.boundingBox.j(); l += 5) {
                        k = random.nextInt(5);
                        if (k == 0) {
                            WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, l, this.boundingBox.h(), this.boundingBox.i() - 1, EnumDirection.NORTH, i + 1);
                        } else if (k == 1) {
                            WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, l, this.boundingBox.h(), this.boundingBox.l() + 1, EnumDirection.SOUTH, i + 1);
                        }
                    }
                } else {
                    for (l = this.boundingBox.i() + 3; l + 3 <= this.boundingBox.l(); l += 5) {
                        k = random.nextInt(5);
                        if (k == 0) {
                            WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.g() - 1, this.boundingBox.h(), l, EnumDirection.WEST, i + 1);
                        } else if (k == 1) {
                            WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.j() + 1, this.boundingBox.h(), l, EnumDirection.EAST, i + 1);
                        }
                    }
                }
            }

        }

        @Override
        protected boolean a(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, Random random, int i, int j, int k, MinecraftKey minecraftkey) {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.c(i, j, k);

            if (structureboundingbox.b((BaseBlockPosition) blockposition_mutableblockposition) && generatoraccessseed.getType(blockposition_mutableblockposition).isAir() && !generatoraccessseed.getType(blockposition_mutableblockposition.down()).isAir()) {
                IBlockData iblockdata = (IBlockData) Blocks.RAIL.getBlockData().set(BlockMinecartTrack.SHAPE, random.nextBoolean() ? BlockPropertyTrackPosition.NORTH_SOUTH : BlockPropertyTrackPosition.EAST_WEST);

                this.c(generatoraccessseed, iblockdata, i, j, k, structureboundingbox);
                EntityMinecartChest entityminecartchest = new EntityMinecartChest(generatoraccessseed.getLevel(), (double) blockposition_mutableblockposition.getX() + 0.5D, (double) blockposition_mutableblockposition.getY() + 0.5D, (double) blockposition_mutableblockposition.getZ() + 0.5D);

                entityminecartchest.setLootTable(minecraftkey, random.nextLong());
                generatoraccessseed.addEntity(entityminecartchest);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            if (this.a((IBlockAccess) generatoraccessseed, structureboundingbox)) {
                return false;
            } else {
                boolean flag = false;
                boolean flag1 = true;
                boolean flag2 = false;
                boolean flag3 = true;
                int i = this.numSections * 5 - 1;
                IBlockData iblockdata = this.type.d();

                this.a(generatoraccessseed, structureboundingbox, 0, 0, 0, 2, 1, i, WorldGenMineshaftPieces.WorldGenMineshaftCorridor.CAVE_AIR, WorldGenMineshaftPieces.WorldGenMineshaftCorridor.CAVE_AIR, false);
                this.a(generatoraccessseed, structureboundingbox, random, 0.8F, 0, 2, 0, 2, 2, i, WorldGenMineshaftPieces.WorldGenMineshaftCorridor.CAVE_AIR, WorldGenMineshaftPieces.WorldGenMineshaftCorridor.CAVE_AIR, false, false);
                if (this.spiderCorridor) {
                    this.a(generatoraccessseed, structureboundingbox, random, 0.6F, 0, 0, 0, 2, 1, i, Blocks.COBWEB.getBlockData(), WorldGenMineshaftPieces.WorldGenMineshaftCorridor.CAVE_AIR, false, true);
                }

                int j;
                int k;

                for (k = 0; k < this.numSections; ++k) {
                    j = 2 + k * 5;
                    this.a(generatoraccessseed, structureboundingbox, 0, 0, j, 2, 2, random);
                    this.a(generatoraccessseed, structureboundingbox, random, 0.1F, 0, 2, j - 1);
                    this.a(generatoraccessseed, structureboundingbox, random, 0.1F, 2, 2, j - 1);
                    this.a(generatoraccessseed, structureboundingbox, random, 0.1F, 0, 2, j + 1);
                    this.a(generatoraccessseed, structureboundingbox, random, 0.1F, 2, 2, j + 1);
                    this.a(generatoraccessseed, structureboundingbox, random, 0.05F, 0, 2, j - 2);
                    this.a(generatoraccessseed, structureboundingbox, random, 0.05F, 2, 2, j - 2);
                    this.a(generatoraccessseed, structureboundingbox, random, 0.05F, 0, 2, j + 2);
                    this.a(generatoraccessseed, structureboundingbox, random, 0.05F, 2, 2, j + 2);
                    if (random.nextInt(100) == 0) {
                        this.a(generatoraccessseed, structureboundingbox, random, 2, 0, j - 1, LootTables.ABANDONED_MINESHAFT);
                    }

                    if (random.nextInt(100) == 0) {
                        this.a(generatoraccessseed, structureboundingbox, random, 0, 0, j + 1, LootTables.ABANDONED_MINESHAFT);
                    }

                    if (this.spiderCorridor && !this.hasPlacedSpider) {
                        boolean flag4 = true;
                        int l = j - 1 + random.nextInt(3);
                        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.c(1, 0, l);

                        if (structureboundingbox.b((BaseBlockPosition) blockposition_mutableblockposition) && this.b(generatoraccessseed, 1, 0, l, structureboundingbox)) {
                            this.hasPlacedSpider = true;
                            generatoraccessseed.setTypeAndData(blockposition_mutableblockposition, Blocks.SPAWNER.getBlockData(), 2);
                            TileEntity tileentity = generatoraccessseed.getTileEntity(blockposition_mutableblockposition);

                            if (tileentity instanceof TileEntityMobSpawner) {
                                ((TileEntityMobSpawner) tileentity).getSpawner().setMobName(EntityTypes.CAVE_SPIDER);
                            }
                        }
                    }
                }

                for (k = 0; k <= 2; ++k) {
                    for (j = 0; j <= i; ++j) {
                        this.a(generatoraccessseed, structureboundingbox, iblockdata, k, -1, j);
                    }
                }

                boolean flag5 = true;

                this.a(generatoraccessseed, structureboundingbox, 0, -1, 2);
                if (this.numSections > 1) {
                    j = i - 2;
                    this.a(generatoraccessseed, structureboundingbox, 0, -1, j);
                }

                if (this.hasRails) {
                    IBlockData iblockdata1 = (IBlockData) Blocks.RAIL.getBlockData().set(BlockMinecartTrack.SHAPE, BlockPropertyTrackPosition.NORTH_SOUTH);

                    for (int i1 = 0; i1 <= i; ++i1) {
                        IBlockData iblockdata2 = this.a((IBlockAccess) generatoraccessseed, 1, -1, i1, structureboundingbox);

                        if (!iblockdata2.isAir() && iblockdata2.i(generatoraccessseed, this.c(1, -1, i1))) {
                            float f = this.b(generatoraccessseed, 1, 0, i1, structureboundingbox) ? 0.7F : 0.9F;

                            this.a(generatoraccessseed, structureboundingbox, random, f, 1, 0, i1, iblockdata1);
                        }
                    }
                }

                return true;
            }
        }

        private void a(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, int i, int j, int k) {
            IBlockData iblockdata = this.type.b();
            IBlockData iblockdata1 = this.type.d();

            if (this.a((IBlockAccess) generatoraccessseed, i, j, k, structureboundingbox).a(iblockdata1.getBlock())) {
                this.b(generatoraccessseed, iblockdata, i, j, k, structureboundingbox);
            }

            if (this.a((IBlockAccess) generatoraccessseed, i + 2, j, k, structureboundingbox).a(iblockdata1.getBlock())) {
                this.b(generatoraccessseed, iblockdata, i + 2, j, k, structureboundingbox);
            }

        }

        @Override
        protected void a(GeneratorAccessSeed generatoraccessseed, IBlockData iblockdata, int i, int j, int k, StructureBoundingBox structureboundingbox) {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.c(i, j, k);

            if (structureboundingbox.b((BaseBlockPosition) blockposition_mutableblockposition)) {
                int l = blockposition_mutableblockposition.getY();

                while (this.a(generatoraccessseed.getType(blockposition_mutableblockposition)) && blockposition_mutableblockposition.getY() > generatoraccessseed.getMinBuildHeight() + 1) {
                    blockposition_mutableblockposition.c(EnumDirection.DOWN);
                }

                if (this.b(generatoraccessseed.getType(blockposition_mutableblockposition))) {
                    while (blockposition_mutableblockposition.getY() < l) {
                        blockposition_mutableblockposition.c(EnumDirection.UP);
                        generatoraccessseed.setTypeAndData(blockposition_mutableblockposition, iblockdata, 2);
                    }

                }
            }
        }

        protected void b(GeneratorAccessSeed generatoraccessseed, IBlockData iblockdata, int i, int j, int k, StructureBoundingBox structureboundingbox) {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.c(i, j, k);

            if (structureboundingbox.b((BaseBlockPosition) blockposition_mutableblockposition)) {
                int l = blockposition_mutableblockposition.getY();
                int i1 = 1;
                boolean flag = true;

                for (boolean flag1 = true; flag || flag1; ++i1) {
                    IBlockData iblockdata1;
                    boolean flag2;

                    if (flag) {
                        blockposition_mutableblockposition.t(l - i1);
                        iblockdata1 = generatoraccessseed.getType(blockposition_mutableblockposition);
                        flag2 = this.a(iblockdata1) && !iblockdata1.a(Blocks.LAVA);
                        if (!flag2 && this.b(iblockdata1)) {
                            a(generatoraccessseed, iblockdata, blockposition_mutableblockposition, l - i1 + 1, l);
                            return;
                        }

                        flag = i1 <= 20 && flag2 && blockposition_mutableblockposition.getY() > generatoraccessseed.getMinBuildHeight() + 1;
                    }

                    if (flag1) {
                        blockposition_mutableblockposition.t(l + i1);
                        iblockdata1 = generatoraccessseed.getType(blockposition_mutableblockposition);
                        flag2 = this.a(iblockdata1);
                        if (!flag2 && this.a((IWorldReader) generatoraccessseed, (BlockPosition) blockposition_mutableblockposition, iblockdata1)) {
                            generatoraccessseed.setTypeAndData(blockposition_mutableblockposition.t(l + 1), this.type.e(), 2);
                            a(generatoraccessseed, Blocks.CHAIN.getBlockData(), blockposition_mutableblockposition, l + 2, l + i1);
                            return;
                        }

                        flag1 = i1 <= 50 && flag2 && blockposition_mutableblockposition.getY() < generatoraccessseed.getMaxBuildHeight() - 1;
                    }
                }

            }
        }

        private static void a(GeneratorAccessSeed generatoraccessseed, IBlockData iblockdata, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, int i, int j) {
            for (int k = i; k < j; ++k) {
                generatoraccessseed.setTypeAndData(blockposition_mutableblockposition.t(k), iblockdata, 2);
            }

        }

        private boolean b(IBlockData iblockdata) {
            return !iblockdata.a(Blocks.RAIL) && !iblockdata.a(Blocks.LAVA);
        }

        private boolean a(IWorldReader iworldreader, BlockPosition blockposition, IBlockData iblockdata) {
            return Block.a(iworldreader, blockposition, EnumDirection.DOWN) && !(iblockdata.getBlock() instanceof BlockFalling);
        }

        private void a(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, int i, int j, int k, int l, int i1, Random random) {
            if (this.a((IBlockAccess) generatoraccessseed, structureboundingbox, i, i1, l, k)) {
                IBlockData iblockdata = this.type.d();
                IBlockData iblockdata1 = this.type.e();

                this.a(generatoraccessseed, structureboundingbox, i, j, k, i, l - 1, k, (IBlockData) iblockdata1.set(BlockFence.WEST, true), WorldGenMineshaftPieces.WorldGenMineshaftCorridor.CAVE_AIR, false);
                this.a(generatoraccessseed, structureboundingbox, i1, j, k, i1, l - 1, k, (IBlockData) iblockdata1.set(BlockFence.EAST, true), WorldGenMineshaftPieces.WorldGenMineshaftCorridor.CAVE_AIR, false);
                if (random.nextInt(4) == 0) {
                    this.a(generatoraccessseed, structureboundingbox, i, l, k, i, l, k, iblockdata, WorldGenMineshaftPieces.WorldGenMineshaftCorridor.CAVE_AIR, false);
                    this.a(generatoraccessseed, structureboundingbox, i1, l, k, i1, l, k, iblockdata, WorldGenMineshaftPieces.WorldGenMineshaftCorridor.CAVE_AIR, false);
                } else {
                    this.a(generatoraccessseed, structureboundingbox, i, l, k, i1, l, k, iblockdata, WorldGenMineshaftPieces.WorldGenMineshaftCorridor.CAVE_AIR, false);
                    this.a(generatoraccessseed, structureboundingbox, random, 0.05F, i + 1, l, k - 1, (IBlockData) Blocks.WALL_TORCH.getBlockData().set(BlockTorchWall.FACING, EnumDirection.NORTH));
                    this.a(generatoraccessseed, structureboundingbox, random, 0.05F, i + 1, l, k + 1, (IBlockData) Blocks.WALL_TORCH.getBlockData().set(BlockTorchWall.FACING, EnumDirection.SOUTH));
                }

            }
        }

        private void a(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, Random random, float f, int i, int j, int k) {
            if (this.b(generatoraccessseed, i, j, k, structureboundingbox) && random.nextFloat() < f && this.a(generatoraccessseed, structureboundingbox, i, j, k, 2)) {
                this.c(generatoraccessseed, Blocks.COBWEB.getBlockData(), i, j, k, structureboundingbox);
            }

        }

        private boolean a(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, int i, int j, int k, int l) {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.c(i, j, k);
            int i1 = 0;
            EnumDirection[] aenumdirection = EnumDirection.values();
            int j1 = aenumdirection.length;

            for (int k1 = 0; k1 < j1; ++k1) {
                EnumDirection enumdirection = aenumdirection[k1];

                blockposition_mutableblockposition.c(enumdirection);
                if (structureboundingbox.b((BaseBlockPosition) blockposition_mutableblockposition) && generatoraccessseed.getType(blockposition_mutableblockposition).d(generatoraccessseed, blockposition_mutableblockposition, enumdirection.opposite())) {
                    ++i1;
                    if (i1 >= l) {
                        return true;
                    }
                }

                blockposition_mutableblockposition.c(enumdirection.opposite());
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
            this.type = WorldGenMineshaft.Type.a(nbttagcompound.getInt("MST"));
        }

        @Override
        protected boolean a(IWorldReader iworldreader, int i, int j, int k, StructureBoundingBox structureboundingbox) {
            IBlockData iblockdata = this.a((IBlockAccess) iworldreader, i, j, k, structureboundingbox);

            return !iblockdata.a(this.type.d().getBlock()) && !iblockdata.a(this.type.b().getBlock()) && !iblockdata.a(this.type.e().getBlock()) && !iblockdata.a(Blocks.CHAIN);
        }

        @Override
        protected void a(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            nbttagcompound.setInt("MST", this.type.ordinal());
        }

        protected boolean a(IBlockAccess iblockaccess, StructureBoundingBox structureboundingbox, int i, int j, int k, int l) {
            for (int i1 = i; i1 <= j; ++i1) {
                if (this.a(iblockaccess, i1, k + 1, l, structureboundingbox).isAir()) {
                    return false;
                }
            }

            return true;
        }

        protected boolean a(IBlockAccess iblockaccess, StructureBoundingBox structureboundingbox) {
            int i = Math.max(this.boundingBox.g() - 1, structureboundingbox.g());
            int j = Math.max(this.boundingBox.h() - 1, structureboundingbox.h());
            int k = Math.max(this.boundingBox.i() - 1, structureboundingbox.i());
            int l = Math.min(this.boundingBox.j() + 1, structureboundingbox.j());
            int i1 = Math.min(this.boundingBox.k() + 1, structureboundingbox.k());
            int j1 = Math.min(this.boundingBox.l() + 1, structureboundingbox.l());
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            int k1;
            int l1;

            for (k1 = i; k1 <= l; ++k1) {
                for (l1 = k; l1 <= j1; ++l1) {
                    if (iblockaccess.getType(blockposition_mutableblockposition.d(k1, j, l1)).getMaterial().isLiquid()) {
                        return true;
                    }

                    if (iblockaccess.getType(blockposition_mutableblockposition.d(k1, i1, l1)).getMaterial().isLiquid()) {
                        return true;
                    }
                }
            }

            for (k1 = i; k1 <= l; ++k1) {
                for (l1 = j; l1 <= i1; ++l1) {
                    if (iblockaccess.getType(blockposition_mutableblockposition.d(k1, l1, k)).getMaterial().isLiquid()) {
                        return true;
                    }

                    if (iblockaccess.getType(blockposition_mutableblockposition.d(k1, l1, j1)).getMaterial().isLiquid()) {
                        return true;
                    }
                }
            }

            for (k1 = k; k1 <= j1; ++k1) {
                for (l1 = j; l1 <= i1; ++l1) {
                    if (iblockaccess.getType(blockposition_mutableblockposition.d(i, l1, k1)).getMaterial().isLiquid()) {
                        return true;
                    }

                    if (iblockaccess.getType(blockposition_mutableblockposition.d(l, l1, k1)).getMaterial().isLiquid()) {
                        return true;
                    }
                }
            }

            return false;
        }

        protected void a(GeneratorAccessSeed generatoraccessseed, StructureBoundingBox structureboundingbox, IBlockData iblockdata, int i, int j, int k) {
            if (this.b(generatoraccessseed, i, j, k, structureboundingbox)) {
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.c(i, j, k);
                IBlockData iblockdata1 = generatoraccessseed.getType(blockposition_mutableblockposition);

                if (iblockdata1.isAir() || iblockdata1.a(Blocks.CHAIN)) {
                    generatoraccessseed.setTypeAndData(blockposition_mutableblockposition, iblockdata, 2);
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

        public WorldGenMineshaftRoom(WorldServer worldserver, NBTTagCompound nbttagcompound) {
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
        public void a(StructurePiece structurepiece, StructurePieceAccessor structurepieceaccessor, Random random) {
            int i = this.g();
            int j = this.boundingBox.d() - 3 - 1;

            if (j <= 0) {
                j = 1;
            }

            int k;
            WorldGenMineshaftPieces.c worldgenmineshaftpieces_c;
            StructureBoundingBox structureboundingbox;

            for (k = 0; k < this.boundingBox.c(); k += 4) {
                k += random.nextInt(this.boundingBox.c());
                if (k + 3 > this.boundingBox.c()) {
                    break;
                }

                worldgenmineshaftpieces_c = WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.g() + k, this.boundingBox.h() + random.nextInt(j) + 1, this.boundingBox.i() - 1, EnumDirection.NORTH, i);
                if (worldgenmineshaftpieces_c != null) {
                    structureboundingbox = worldgenmineshaftpieces_c.f();
                    this.childEntranceBoxes.add(new StructureBoundingBox(structureboundingbox.g(), structureboundingbox.h(), this.boundingBox.i(), structureboundingbox.j(), structureboundingbox.k(), this.boundingBox.i() + 1));
                }
            }

            for (k = 0; k < this.boundingBox.c(); k += 4) {
                k += random.nextInt(this.boundingBox.c());
                if (k + 3 > this.boundingBox.c()) {
                    break;
                }

                worldgenmineshaftpieces_c = WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.g() + k, this.boundingBox.h() + random.nextInt(j) + 1, this.boundingBox.l() + 1, EnumDirection.SOUTH, i);
                if (worldgenmineshaftpieces_c != null) {
                    structureboundingbox = worldgenmineshaftpieces_c.f();
                    this.childEntranceBoxes.add(new StructureBoundingBox(structureboundingbox.g(), structureboundingbox.h(), this.boundingBox.l() - 1, structureboundingbox.j(), structureboundingbox.k(), this.boundingBox.l()));
                }
            }

            for (k = 0; k < this.boundingBox.e(); k += 4) {
                k += random.nextInt(this.boundingBox.e());
                if (k + 3 > this.boundingBox.e()) {
                    break;
                }

                worldgenmineshaftpieces_c = WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.g() - 1, this.boundingBox.h() + random.nextInt(j) + 1, this.boundingBox.i() + k, EnumDirection.WEST, i);
                if (worldgenmineshaftpieces_c != null) {
                    structureboundingbox = worldgenmineshaftpieces_c.f();
                    this.childEntranceBoxes.add(new StructureBoundingBox(this.boundingBox.g(), structureboundingbox.h(), structureboundingbox.i(), this.boundingBox.g() + 1, structureboundingbox.k(), structureboundingbox.l()));
                }
            }

            for (k = 0; k < this.boundingBox.e(); k += 4) {
                k += random.nextInt(this.boundingBox.e());
                if (k + 3 > this.boundingBox.e()) {
                    break;
                }

                worldgenmineshaftpieces_c = WorldGenMineshaftPieces.a(structurepiece, structurepieceaccessor, random, this.boundingBox.j() + 1, this.boundingBox.h() + random.nextInt(j) + 1, this.boundingBox.i() + k, EnumDirection.EAST, i);
                if (worldgenmineshaftpieces_c != null) {
                    structureboundingbox = worldgenmineshaftpieces_c.f();
                    this.childEntranceBoxes.add(new StructureBoundingBox(this.boundingBox.j() - 1, structureboundingbox.h(), structureboundingbox.i(), this.boundingBox.j(), structureboundingbox.k(), structureboundingbox.l()));
                }
            }

        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            if (this.a((IBlockAccess) generatoraccessseed, structureboundingbox)) {
                return false;
            } else {
                this.a(generatoraccessseed, structureboundingbox, this.boundingBox.g(), this.boundingBox.h(), this.boundingBox.i(), this.boundingBox.j(), this.boundingBox.h(), this.boundingBox.l(), Blocks.DIRT.getBlockData(), WorldGenMineshaftPieces.WorldGenMineshaftRoom.CAVE_AIR, true);
                this.a(generatoraccessseed, structureboundingbox, this.boundingBox.g(), this.boundingBox.h() + 1, this.boundingBox.i(), this.boundingBox.j(), Math.min(this.boundingBox.h() + 3, this.boundingBox.k()), this.boundingBox.l(), WorldGenMineshaftPieces.WorldGenMineshaftRoom.CAVE_AIR, WorldGenMineshaftPieces.WorldGenMineshaftRoom.CAVE_AIR, false);
                Iterator iterator = this.childEntranceBoxes.iterator();

                while (iterator.hasNext()) {
                    StructureBoundingBox structureboundingbox1 = (StructureBoundingBox) iterator.next();

                    this.a(generatoraccessseed, structureboundingbox, structureboundingbox1.g(), structureboundingbox1.k() - 2, structureboundingbox1.i(), structureboundingbox1.j(), structureboundingbox1.k(), structureboundingbox1.l(), WorldGenMineshaftPieces.WorldGenMineshaftRoom.CAVE_AIR, WorldGenMineshaftPieces.WorldGenMineshaftRoom.CAVE_AIR, false);
                }

                this.a(generatoraccessseed, structureboundingbox, this.boundingBox.g(), this.boundingBox.h() + 4, this.boundingBox.i(), this.boundingBox.j(), this.boundingBox.k(), this.boundingBox.l(), WorldGenMineshaftPieces.WorldGenMineshaftRoom.CAVE_AIR, false);
                return true;
            }
        }

        @Override
        public void a(int i, int j, int k) {
            super.a(i, j, k);
            Iterator iterator = this.childEntranceBoxes.iterator();

            while (iterator.hasNext()) {
                StructureBoundingBox structureboundingbox = (StructureBoundingBox) iterator.next();

                structureboundingbox.a(i, j, k);
            }

        }

        @Override
        protected void a(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super.a(worldserver, nbttagcompound);
            DataResult dataresult = StructureBoundingBox.CODEC.listOf().encodeStart(DynamicOpsNBT.INSTANCE, this.childEntranceBoxes);
            Logger logger = WorldGenMineshaftPieces.LOGGER;

            Objects.requireNonNull(logger);
            dataresult.resultOrPartial(logger::error).ifPresent((nbtbase) -> {
                nbttagcompound.set("Entrances", nbtbase);
            });
        }
    }
}
