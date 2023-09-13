package net.minecraft.world.level.levelgen.structure;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureStructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import net.minecraft.world.level.storage.loot.LootTables;

public class WorldGenBuriedTreasurePieces {
    public static class a extends StructurePiece {

        public a(BlockPosition blockposition) {
            super(WorldGenFeatureStructurePieceType.aa, 0);
            this.n = new StructureBoundingBox(blockposition.getX(), blockposition.getY(), blockposition.getZ(), blockposition.getX(), blockposition.getY(), blockposition.getZ());
        }

        public a(DefinedStructureManager definedstructuremanager, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.aa, nbttagcompound);
        }

        @Override
        protected void a(NBTTagCompound nbttagcompound) {}

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            int i = generatoraccessseed.a(HeightMap.Type.OCEAN_FLOOR_WG, this.n.a, this.n.c);
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(this.n.a, i, this.n.c);

            while (blockposition_mutableblockposition.getY() > 0) {
                IBlockData iblockdata = generatoraccessseed.getType(blockposition_mutableblockposition);
                IBlockData iblockdata1 = generatoraccessseed.getType(blockposition_mutableblockposition.down());

                if (iblockdata1 == Blocks.SANDSTONE.getBlockData() || iblockdata1 == Blocks.STONE.getBlockData() || iblockdata1 == Blocks.ANDESITE.getBlockData() || iblockdata1 == Blocks.GRANITE.getBlockData() || iblockdata1 == Blocks.DIORITE.getBlockData()) {
                    IBlockData iblockdata2 = !iblockdata.isAir() && !this.a(iblockdata) ? iblockdata : Blocks.SAND.getBlockData();
                    EnumDirection[] aenumdirection = EnumDirection.values();
                    int j = aenumdirection.length;

                    for (int k = 0; k < j; ++k) {
                        EnumDirection enumdirection = aenumdirection[k];
                        BlockPosition blockposition1 = blockposition_mutableblockposition.shift(enumdirection);
                        IBlockData iblockdata3 = generatoraccessseed.getType(blockposition1);

                        if (iblockdata3.isAir() || this.a(iblockdata3)) {
                            BlockPosition blockposition2 = blockposition1.down();
                            IBlockData iblockdata4 = generatoraccessseed.getType(blockposition2);

                            if ((iblockdata4.isAir() || this.a(iblockdata4)) && enumdirection != EnumDirection.UP) {
                                generatoraccessseed.setTypeAndData(blockposition1, iblockdata1, 3);
                            } else {
                                generatoraccessseed.setTypeAndData(blockposition1, iblockdata2, 3);
                            }
                        }
                    }

                    this.n = new StructureBoundingBox(blockposition_mutableblockposition.getX(), blockposition_mutableblockposition.getY(), blockposition_mutableblockposition.getZ(), blockposition_mutableblockposition.getX(), blockposition_mutableblockposition.getY(), blockposition_mutableblockposition.getZ());
                    return this.a(generatoraccessseed, structureboundingbox, random, blockposition_mutableblockposition, LootTables.G, (IBlockData) null);
                }

                blockposition_mutableblockposition.e(0, -1, 0);
            }

            return false;
        }

        private boolean a(IBlockData iblockdata) {
            return iblockdata == Blocks.WATER.getBlockData() || iblockdata == Blocks.LAVA.getBlockData();
        }
    }
}
