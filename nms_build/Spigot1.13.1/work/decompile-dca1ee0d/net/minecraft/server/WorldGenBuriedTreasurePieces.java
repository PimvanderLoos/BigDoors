package net.minecraft.server;

import java.util.Random;

public class WorldGenBuriedTreasurePieces {

    public static void a() {
        WorldGenFactory.a(WorldGenBuriedTreasurePieces.a.class, "BTP");
    }

    public static class a extends StructurePiece {

        public a() {}

        public a(BlockPosition blockposition) {
            super(0);
            this.n = new StructureBoundingBox(blockposition.getX(), blockposition.getY(), blockposition.getZ(), blockposition.getX(), blockposition.getY(), blockposition.getZ());
        }

        protected void a(NBTTagCompound nbttagcompound) {}

        protected void a(NBTTagCompound nbttagcompound, DefinedStructureManager definedstructuremanager) {}

        public boolean a(GeneratorAccess generatoraccess, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair) {
            int i = generatoraccess.a(HeightMap.Type.OCEAN_FLOOR_WG, this.n.a, this.n.c);
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(this.n.a, i, this.n.c);

            while (blockposition_mutableblockposition.getY() > 0) {
                IBlockData iblockdata = generatoraccess.getType(blockposition_mutableblockposition);
                IBlockData iblockdata1 = generatoraccess.getType(blockposition_mutableblockposition.down());

                if (iblockdata1 == Blocks.SANDSTONE.getBlockData() || iblockdata1 == Blocks.STONE.getBlockData() || iblockdata1 == Blocks.ANDESITE.getBlockData() || iblockdata1 == Blocks.GRANITE.getBlockData() || iblockdata1 == Blocks.DIORITE.getBlockData()) {
                    IBlockData iblockdata2 = !iblockdata.isAir() && !this.a(iblockdata) ? iblockdata : Blocks.SAND.getBlockData();
                    EnumDirection[] aenumdirection = EnumDirection.values();
                    int j = aenumdirection.length;

                    for (int k = 0; k < j; ++k) {
                        EnumDirection enumdirection = aenumdirection[k];
                        BlockPosition blockposition = blockposition_mutableblockposition.shift(enumdirection);
                        IBlockData iblockdata3 = generatoraccess.getType(blockposition);

                        if (iblockdata3.isAir() || this.a(iblockdata3)) {
                            BlockPosition blockposition1 = blockposition.down();
                            IBlockData iblockdata4 = generatoraccess.getType(blockposition1);

                            if ((iblockdata4.isAir() || this.a(iblockdata4)) && enumdirection != EnumDirection.UP) {
                                generatoraccess.setTypeAndData(blockposition, iblockdata1, 3);
                            } else {
                                generatoraccess.setTypeAndData(blockposition, iblockdata2, 3);
                            }
                        }
                    }

                    return this.a(generatoraccess, structureboundingbox, random, new BlockPosition(this.n.a, blockposition_mutableblockposition.getY(), this.n.c), LootTables.r, (IBlockData) null);
                }

                blockposition_mutableblockposition.d(0, -1, 0);
            }

            return false;
        }

        private boolean a(IBlockData iblockdata) {
            return iblockdata == Blocks.WATER.getBlockData() || iblockdata == Blocks.LAVA.getBlockData();
        }
    }
}
