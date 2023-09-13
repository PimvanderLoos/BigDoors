package net.minecraft.world.level.levelgen.structure.structures;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.WorldGenFeatureStructurePieceType;
import net.minecraft.world.level.storage.loot.LootTables;

public class BuriedTreasurePieces {

    public BuriedTreasurePieces() {}

    public static class a extends StructurePiece {

        public a(BlockPosition blockposition) {
            super(WorldGenFeatureStructurePieceType.BURIED_TREASURE_PIECE, 0, new StructureBoundingBox(blockposition));
        }

        public a(NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.BURIED_TREASURE_PIECE, nbttagcompound);
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound) {}

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            int i = generatoraccessseed.getHeight(HeightMap.Type.OCEAN_FLOOR_WG, this.boundingBox.minX(), this.boundingBox.minZ());
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(this.boundingBox.minX(), i, this.boundingBox.minZ());

            while (blockposition_mutableblockposition.getY() > generatoraccessseed.getMinBuildHeight()) {
                IBlockData iblockdata = generatoraccessseed.getBlockState(blockposition_mutableblockposition);
                IBlockData iblockdata1 = generatoraccessseed.getBlockState(blockposition_mutableblockposition.below());

                if (iblockdata1 == Blocks.SANDSTONE.defaultBlockState() || iblockdata1 == Blocks.STONE.defaultBlockState() || iblockdata1 == Blocks.ANDESITE.defaultBlockState() || iblockdata1 == Blocks.GRANITE.defaultBlockState() || iblockdata1 == Blocks.DIORITE.defaultBlockState()) {
                    IBlockData iblockdata2 = !iblockdata.isAir() && !this.isLiquid(iblockdata) ? iblockdata : Blocks.SAND.defaultBlockState();
                    EnumDirection[] aenumdirection = EnumDirection.values();
                    int j = aenumdirection.length;

                    for (int k = 0; k < j; ++k) {
                        EnumDirection enumdirection = aenumdirection[k];
                        BlockPosition blockposition1 = blockposition_mutableblockposition.relative(enumdirection);
                        IBlockData iblockdata3 = generatoraccessseed.getBlockState(blockposition1);

                        if (iblockdata3.isAir() || this.isLiquid(iblockdata3)) {
                            BlockPosition blockposition2 = blockposition1.below();
                            IBlockData iblockdata4 = generatoraccessseed.getBlockState(blockposition2);

                            if ((iblockdata4.isAir() || this.isLiquid(iblockdata4)) && enumdirection != EnumDirection.UP) {
                                generatoraccessseed.setBlock(blockposition1, iblockdata1, 3);
                            } else {
                                generatoraccessseed.setBlock(blockposition1, iblockdata2, 3);
                            }
                        }
                    }

                    this.boundingBox = new StructureBoundingBox(blockposition_mutableblockposition);
                    this.createChest(generatoraccessseed, structureboundingbox, randomsource, blockposition_mutableblockposition, LootTables.BURIED_TREASURE, (IBlockData) null);
                    return;
                }

                blockposition_mutableblockposition.move(0, -1, 0);
            }

        }

        private boolean isLiquid(IBlockData iblockdata) {
            return iblockdata == Blocks.WATER.defaultBlockState() || iblockdata == Blocks.LAVA.defaultBlockState();
        }
    }
}
