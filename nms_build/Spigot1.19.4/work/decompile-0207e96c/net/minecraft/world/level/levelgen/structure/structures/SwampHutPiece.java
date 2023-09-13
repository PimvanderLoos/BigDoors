package net.minecraft.world.level.levelgen.structure.structures;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.animal.EntityCat;
import net.minecraft.world.entity.monster.EntityWitch;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.BlockStairs;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockPropertyStairsShape;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.WorldGenScatteredPiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.WorldGenFeatureStructurePieceType;

public class SwampHutPiece extends WorldGenScatteredPiece {

    private boolean spawnedWitch;
    private boolean spawnedCat;

    public SwampHutPiece(RandomSource randomsource, int i, int j) {
        super(WorldGenFeatureStructurePieceType.SWAMPLAND_HUT, i, 64, j, 7, 7, 9, getRandomHorizontalDirection(randomsource));
    }

    public SwampHutPiece(NBTTagCompound nbttagcompound) {
        super(WorldGenFeatureStructurePieceType.SWAMPLAND_HUT, nbttagcompound);
        this.spawnedWitch = nbttagcompound.getBoolean("Witch");
        this.spawnedCat = nbttagcompound.getBoolean("Cat");
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(structurepieceserializationcontext, nbttagcompound);
        nbttagcompound.putBoolean("Witch", this.spawnedWitch);
        nbttagcompound.putBoolean("Cat", this.spawnedCat);
    }

    @Override
    public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
        if (this.updateAverageGroundHeight(generatoraccessseed, structureboundingbox, 0)) {
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 1, 1, 5, 1, 7, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 4, 2, 5, 4, 7, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 1, 0, 4, 1, 0, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 2, 2, 3, 3, 2, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 2, 3, 1, 3, 6, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 2, 3, 5, 3, 6, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 2, 2, 7, 4, 3, 7, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 0, 2, 1, 3, 2, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 0, 2, 5, 3, 2, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 1, 0, 7, 1, 3, 7, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
            this.generateBox(generatoraccessseed, structureboundingbox, 5, 0, 7, 5, 3, 7, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
            this.placeBlock(generatoraccessseed, Blocks.OAK_FENCE.defaultBlockState(), 2, 3, 2, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.OAK_FENCE.defaultBlockState(), 3, 3, 7, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.AIR.defaultBlockState(), 1, 3, 4, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.AIR.defaultBlockState(), 5, 3, 4, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.AIR.defaultBlockState(), 5, 3, 5, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.POTTED_RED_MUSHROOM.defaultBlockState(), 1, 3, 5, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.CRAFTING_TABLE.defaultBlockState(), 3, 2, 6, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.CAULDRON.defaultBlockState(), 4, 2, 6, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.OAK_FENCE.defaultBlockState(), 1, 2, 1, structureboundingbox);
            this.placeBlock(generatoraccessseed, Blocks.OAK_FENCE.defaultBlockState(), 5, 2, 1, structureboundingbox);
            IBlockData iblockdata = (IBlockData) Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(BlockStairs.FACING, EnumDirection.NORTH);
            IBlockData iblockdata1 = (IBlockData) Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(BlockStairs.FACING, EnumDirection.EAST);
            IBlockData iblockdata2 = (IBlockData) Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(BlockStairs.FACING, EnumDirection.WEST);
            IBlockData iblockdata3 = (IBlockData) Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(BlockStairs.FACING, EnumDirection.SOUTH);

            this.generateBox(generatoraccessseed, structureboundingbox, 0, 4, 1, 6, 4, 1, iblockdata, iblockdata, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 4, 2, 0, 4, 7, iblockdata1, iblockdata1, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 6, 4, 2, 6, 4, 7, iblockdata2, iblockdata2, false);
            this.generateBox(generatoraccessseed, structureboundingbox, 0, 4, 8, 6, 4, 8, iblockdata3, iblockdata3, false);
            this.placeBlock(generatoraccessseed, (IBlockData) iblockdata.setValue(BlockStairs.SHAPE, BlockPropertyStairsShape.OUTER_RIGHT), 0, 4, 1, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) iblockdata.setValue(BlockStairs.SHAPE, BlockPropertyStairsShape.OUTER_LEFT), 6, 4, 1, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) iblockdata3.setValue(BlockStairs.SHAPE, BlockPropertyStairsShape.OUTER_LEFT), 0, 4, 8, structureboundingbox);
            this.placeBlock(generatoraccessseed, (IBlockData) iblockdata3.setValue(BlockStairs.SHAPE, BlockPropertyStairsShape.OUTER_RIGHT), 6, 4, 8, structureboundingbox);

            for (int i = 2; i <= 7; i += 5) {
                for (int j = 1; j <= 5; j += 4) {
                    this.fillColumnDown(generatoraccessseed, Blocks.OAK_LOG.defaultBlockState(), j, -1, i, structureboundingbox);
                }
            }

            if (!this.spawnedWitch) {
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.getWorldPos(2, 2, 5);

                if (structureboundingbox.isInside(blockposition_mutableblockposition)) {
                    this.spawnedWitch = true;
                    EntityWitch entitywitch = (EntityWitch) EntityTypes.WITCH.create(generatoraccessseed.getLevel());

                    if (entitywitch != null) {
                        entitywitch.setPersistenceRequired();
                        entitywitch.moveTo((double) blockposition_mutableblockposition.getX() + 0.5D, (double) blockposition_mutableblockposition.getY(), (double) blockposition_mutableblockposition.getZ() + 0.5D, 0.0F, 0.0F);
                        entitywitch.finalizeSpawn(generatoraccessseed, generatoraccessseed.getCurrentDifficultyAt(blockposition_mutableblockposition), EnumMobSpawn.STRUCTURE, (GroupDataEntity) null, (NBTTagCompound) null);
                        generatoraccessseed.addFreshEntityWithPassengers(entitywitch);
                    }
                }
            }

            this.spawnCat(generatoraccessseed, structureboundingbox);
        }
    }

    private void spawnCat(WorldAccess worldaccess, StructureBoundingBox structureboundingbox) {
        if (!this.spawnedCat) {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = this.getWorldPos(2, 2, 5);

            if (structureboundingbox.isInside(blockposition_mutableblockposition)) {
                this.spawnedCat = true;
                EntityCat entitycat = (EntityCat) EntityTypes.CAT.create(worldaccess.getLevel());

                if (entitycat != null) {
                    entitycat.setPersistenceRequired();
                    entitycat.moveTo((double) blockposition_mutableblockposition.getX() + 0.5D, (double) blockposition_mutableblockposition.getY(), (double) blockposition_mutableblockposition.getZ() + 0.5D, 0.0F, 0.0F);
                    entitycat.finalizeSpawn(worldaccess, worldaccess.getCurrentDifficultyAt(blockposition_mutableblockposition), EnumMobSpawn.STRUCTURE, (GroupDataEntity) null, (NBTTagCompound) null);
                    worldaccess.addFreshEntityWithPassengers(entitycat);
                }
            }
        }

    }
}
