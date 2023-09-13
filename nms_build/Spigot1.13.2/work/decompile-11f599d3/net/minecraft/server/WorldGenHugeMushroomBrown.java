package net.minecraft.server;

import java.util.Random;

public class WorldGenHugeMushroomBrown extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenHugeMushroomBrown() {}

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        int i = random.nextInt(3) + 4;

        if (random.nextInt(12) == 0) {
            i *= 2;
        }

        int j = blockposition.getY();

        if (j >= 1 && j + i + 1 < 256) {
            Block block = generatoraccess.getType(blockposition.down()).getBlock();

            if (!Block.d(block) && block != Blocks.GRASS_BLOCK && block != Blocks.MYCELIUM) {
                return false;
            } else {
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

                int k;
                int l;

                for (int i1 = 0; i1 <= 1 + i; ++i1) {
                    int j1 = i1 <= 3 ? 0 : 3;

                    for (k = -j1; k <= j1; ++k) {
                        for (l = -j1; l <= j1; ++l) {
                            IBlockData iblockdata = generatoraccess.getType(blockposition_mutableblockposition.g(blockposition).d(k, i1, l));

                            if (!iblockdata.isAir() && !iblockdata.a(TagsBlock.LEAVES)) {
                                return false;
                            }
                        }
                    }
                }

                IBlockData iblockdata1 = (IBlockData) ((IBlockData) Blocks.BROWN_MUSHROOM_BLOCK.getBlockData().set(BlockHugeMushroom.p, true)).set(BlockHugeMushroom.q, false);
                boolean flag = true;

                for (k = -3; k <= 3; ++k) {
                    for (l = -3; l <= 3; ++l) {
                        boolean flag1 = k == -3;
                        boolean flag2 = k == 3;
                        boolean flag3 = l == -3;
                        boolean flag4 = l == 3;
                        boolean flag5 = flag1 || flag2;
                        boolean flag6 = flag3 || flag4;

                        if (!flag5 || !flag6) {
                            blockposition_mutableblockposition.g(blockposition).d(k, i, l);
                            if (!generatoraccess.getType(blockposition_mutableblockposition).f(generatoraccess, blockposition_mutableblockposition)) {
                                boolean flag7 = flag1 || flag6 && k == -2;
                                boolean flag8 = flag2 || flag6 && k == 2;
                                boolean flag9 = flag3 || flag5 && l == -2;
                                boolean flag10 = flag4 || flag5 && l == 2;

                                this.a(generatoraccess, (BlockPosition) blockposition_mutableblockposition, (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata1.set(BlockHugeMushroom.o, flag7)).set(BlockHugeMushroom.b, flag8)).set(BlockHugeMushroom.a, flag9)).set(BlockHugeMushroom.c, flag10));
                            }
                        }
                    }
                }

                IBlockData iblockdata2 = (IBlockData) ((IBlockData) Blocks.MUSHROOM_STEM.getBlockData().set(BlockHugeMushroom.p, false)).set(BlockHugeMushroom.q, false);

                for (l = 0; l < i; ++l) {
                    blockposition_mutableblockposition.g(blockposition).c(EnumDirection.UP, l);
                    if (!generatoraccess.getType(blockposition_mutableblockposition).f(generatoraccess, blockposition_mutableblockposition)) {
                        this.a(generatoraccess, (BlockPosition) blockposition_mutableblockposition, iblockdata2);
                    }
                }

                return true;
            }
        } else {
            return false;
        }
    }
}
