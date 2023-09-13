package net.minecraft.world.level.levelgen.structure;

import java.util.Random;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.animal.EntityCat;
import net.minecraft.world.entity.monster.EntityWitch;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.BlockStairs;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockPropertyStairsShape;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureStructurePieceType;

public class WorldGenWitchHut extends WorldGenScatteredPiece {

    private boolean spawnedWitch;
    private boolean spawnedCat;

    public WorldGenWitchHut(Random random, int i, int j) {
        super(WorldGenFeatureStructurePieceType.SWAMPLAND_HUT, i, 64, j, 7, 7, 9, b(random));
    }

    public WorldGenWitchHut(WorldServer worldserver, NBTTagCompound nbttagcompound) {
        super(WorldGenFeatureStructurePieceType.SWAMPLAND_HUT, nbttagcompound);
        this.spawnedWitch = nbttagcompound.getBoolean("Witch");
        this.spawnedCat = nbttagcompound.getBoolean("Cat");
    }

    @Override
    protected void a(WorldServer worldserver, NBTTagCompound nbttagcompound) {
        super.a(worldserver, nbttagcompound);
        nbttagcompound.setBoolean("Witch", this.spawnedWitch);
        nbttagcompound.setBoolean("Cat", this.spawnedCat);
    }

    @Override
    public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
        if (!this.a(generatoraccessseed, structureboundingbox, 0)) {
            return false;
        } else {
            this.a(generatoraccessseed, structureboundingbox, 1, 1, 1, 5, 1, 7, Blocks.SPRUCE_PLANKS.getBlockData(), Blocks.SPRUCE_PLANKS.getBlockData(), false);
            this.a(generatoraccessseed, structureboundingbox, 1, 4, 2, 5, 4, 7, Blocks.SPRUCE_PLANKS.getBlockData(), Blocks.SPRUCE_PLANKS.getBlockData(), false);
            this.a(generatoraccessseed, structureboundingbox, 2, 1, 0, 4, 1, 0, Blocks.SPRUCE_PLANKS.getBlockData(), Blocks.SPRUCE_PLANKS.getBlockData(), false);
            this.a(generatoraccessseed, structureboundingbox, 2, 2, 2, 3, 3, 2, Blocks.SPRUCE_PLANKS.getBlockData(), Blocks.SPRUCE_PLANKS.getBlockData(), false);
            this.a(generatoraccessseed, structureboundingbox, 1, 2, 3, 1, 3, 6, Blocks.SPRUCE_PLANKS.getBlockData(), Blocks.SPRUCE_PLANKS.getBlockData(), false);
            this.a(generatoraccessseed, structureboundingbox, 5, 2, 3, 5, 3, 6, Blocks.SPRUCE_PLANKS.getBlockData(), Blocks.SPRUCE_PLANKS.getBlockData(), false);
            this.a(generatoraccessseed, structureboundingbox, 2, 2, 7, 4, 3, 7, Blocks.SPRUCE_PLANKS.getBlockData(), Blocks.SPRUCE_PLANKS.getBlockData(), false);
            this.a(generatoraccessseed, structureboundingbox, 1, 0, 2, 1, 3, 2, Blocks.OAK_LOG.getBlockData(), Blocks.OAK_LOG.getBlockData(), false);
            this.a(generatoraccessseed, structureboundingbox, 5, 0, 2, 5, 3, 2, Blocks.OAK_LOG.getBlockData(), Blocks.OAK_LOG.getBlockData(), false);
            this.a(generatoraccessseed, structureboundingbox, 1, 0, 7, 1, 3, 7, Blocks.OAK_LOG.getBlockData(), Blocks.OAK_LOG.getBlockData(), false);
            this.a(generatoraccessseed, structureboundingbox, 5, 0, 7, 5, 3, 7, Blocks.OAK_LOG.getBlockData(), Blocks.OAK_LOG.getBlockData(), false);
            this.c(generatoraccessseed, Blocks.OAK_FENCE.getBlockData(), 2, 3, 2, structureboundingbox);
            this.c(generatoraccessseed, Blocks.OAK_FENCE.getBlockData(), 3, 3, 7, structureboundingbox);
            this.c(generatoraccessseed, Blocks.AIR.getBlockData(), 1, 3, 4, structureboundingbox);
            this.c(generatoraccessseed, Blocks.AIR.getBlockData(), 5, 3, 4, structureboundingbox);
            this.c(generatoraccessseed, Blocks.AIR.getBlockData(), 5, 3, 5, structureboundingbox);
            this.c(generatoraccessseed, Blocks.POTTED_RED_MUSHROOM.getBlockData(), 1, 3, 5, structureboundingbox);
            this.c(generatoraccessseed, Blocks.CRAFTING_TABLE.getBlockData(), 3, 2, 6, structureboundingbox);
            this.c(generatoraccessseed, Blocks.CAULDRON.getBlockData(), 4, 2, 6, structureboundingbox);
            this.c(generatoraccessseed, Blocks.OAK_FENCE.getBlockData(), 1, 2, 1, structureboundingbox);
            this.c(generatoraccessseed, Blocks.OAK_FENCE.getBlockData(), 5, 2, 1, structureboundingbox);
            IBlockData iblockdata = (IBlockData) Blocks.SPRUCE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.NORTH);
            IBlockData iblockdata1 = (IBlockData) Blocks.SPRUCE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.EAST);
            IBlockData iblockdata2 = (IBlockData) Blocks.SPRUCE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.WEST);
            IBlockData iblockdata3 = (IBlockData) Blocks.SPRUCE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.SOUTH);

            this.a(generatoraccessseed, structureboundingbox, 0, 4, 1, 6, 4, 1, iblockdata, iblockdata, false);
            this.a(generatoraccessseed, structureboundingbox, 0, 4, 2, 0, 4, 7, iblockdata1, iblockdata1, false);
            this.a(generatoraccessseed, structureboundingbox, 6, 4, 2, 6, 4, 7, iblockdata2, iblockdata2, false);
            this.a(generatoraccessseed, structureboundingbox, 0, 4, 8, 6, 4, 8, iblockdata3, iblockdata3, false);
            this.c(generatoraccessseed, (IBlockData) iblockdata.set(BlockStairs.SHAPE, BlockPropertyStairsShape.OUTER_RIGHT), 0, 4, 1, structureboundingbox);
            this.c(generatoraccessseed, (IBlockData) iblockdata.set(BlockStairs.SHAPE, BlockPropertyStairsShape.OUTER_LEFT), 6, 4, 1, structureboundingbox);
            this.c(generatoraccessseed, (IBlockData) iblockdata3.set(BlockStairs.SHAPE, BlockPropertyStairsShape.OUTER_LEFT), 0, 4, 8, structureboundingbox);
            this.c(generatoraccessseed, (IBlockData) iblockdata3.set(BlockStairs.SHAPE, BlockPropertyStairsShape.OUTER_RIGHT), 6, 4, 8, structureboundingbox);

            for (int i = 2; i <= 7; i += 5) {
                for (int j = 1; j <= 5; j += 4) {
                    this.a(generatoraccessseed, Blocks.OAK_LOG.getBlockData(), j, -1, i, structureboundingbox);
                }
            }

            if (!this.spawnedWitch) {
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.c(2, 2, 5);

                if (structureboundingbox.b((BaseBlockPosition) blockposition_mutableblockposition)) {
                    this.spawnedWitch = true;
                    EntityWitch entitywitch = (EntityWitch) EntityTypes.WITCH.a((World) generatoraccessseed.getLevel());

                    entitywitch.setPersistent();
                    entitywitch.setPositionRotation((double) blockposition_mutableblockposition.getX() + 0.5D, (double) blockposition_mutableblockposition.getY(), (double) blockposition_mutableblockposition.getZ() + 0.5D, 0.0F, 0.0F);
                    entitywitch.prepare(generatoraccessseed, generatoraccessseed.getDamageScaler(blockposition_mutableblockposition), EnumMobSpawn.STRUCTURE, (GroupDataEntity) null, (NBTTagCompound) null);
                    generatoraccessseed.addAllEntities(entitywitch);
                }
            }

            this.a((WorldAccess) generatoraccessseed, structureboundingbox);
            return true;
        }
    }

    private void a(WorldAccess worldaccess, StructureBoundingBox structureboundingbox) {
        if (!this.spawnedCat) {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.c(2, 2, 5);

            if (structureboundingbox.b((BaseBlockPosition) blockposition_mutableblockposition)) {
                this.spawnedCat = true;
                EntityCat entitycat = (EntityCat) EntityTypes.CAT.a((World) worldaccess.getLevel());

                entitycat.setPersistent();
                entitycat.setPositionRotation((double) blockposition_mutableblockposition.getX() + 0.5D, (double) blockposition_mutableblockposition.getY(), (double) blockposition_mutableblockposition.getZ() + 0.5D, 0.0F, 0.0F);
                entitycat.prepare(worldaccess, worldaccess.getDamageScaler(blockposition_mutableblockposition), EnumMobSpawn.STRUCTURE, (GroupDataEntity) null, (NBTTagCompound) null);
                worldaccess.addAllEntities(entitycat);
            }
        }

    }
}
