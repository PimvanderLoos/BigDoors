package net.minecraft.server;

import com.google.common.collect.ImmutableSet;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Random;

public class WorldGenCanyonOcean extends WorldGenCanyon {

    private final float[] g = new float[1024];

    public WorldGenCanyonOcean() {
        this.e = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, new Block[] { Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.SAND, Blocks.GRAVEL, Blocks.WATER, Blocks.LAVA, Blocks.OBSIDIAN, Blocks.AIR, Blocks.CAVE_AIR});
    }

    public boolean a(IBlockAccess iblockaccess, Random random, int i, int j, WorldGenFeatureConfigurationChance worldgenfeatureconfigurationchance) {
        return random.nextFloat() <= worldgenfeatureconfigurationchance.a;
    }

    protected boolean a(GeneratorAccess generatoraccess, long i, int j, int k, double d0, double d1, double d2, double d3, double d4, BitSet bitset) {
        Random random = new Random(i + (long) j + (long) k);
        double d5 = (double) (j * 16 + 8);
        double d6 = (double) (k * 16 + 8);

        if (d0 >= d5 - 16.0D - d3 * 2.0D && d2 >= d6 - 16.0D - d3 * 2.0D && d0 <= d5 + 16.0D + d3 * 2.0D && d2 <= d6 + 16.0D + d3 * 2.0D) {
            int l = Math.max(MathHelper.floor(d0 - d3) - j * 16 - 1, 0);
            int i1 = Math.min(MathHelper.floor(d0 + d3) - j * 16 + 1, 16);
            int j1 = Math.max(MathHelper.floor(d1 - d4) - 1, 1);
            int k1 = Math.min(MathHelper.floor(d1 + d4) + 1, 248);
            int l1 = Math.max(MathHelper.floor(d2 - d3) - k * 16 - 1, 0);
            int i2 = Math.min(MathHelper.floor(d2 + d3) - k * 16 + 1, 16);

            if (l <= i1 && j1 <= k1 && l1 <= i2) {
                boolean flag = false;
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

                for (int j2 = l; j2 < i1; ++j2) {
                    int k2 = j2 + j * 16;
                    double d7 = ((double) k2 + 0.5D - d0) / d3;

                    for (int l2 = l1; l2 < i2; ++l2) {
                        int i3 = l2 + k * 16;
                        double d8 = ((double) i3 + 0.5D - d2) / d3;

                        if (d7 * d7 + d8 * d8 < 1.0D) {
                            for (int j3 = k1; j3 > j1; --j3) {
                                double d9 = ((double) (j3 - 1) + 0.5D - d1) / d4;

                                if ((d7 * d7 + d8 * d8) * (double) this.g[j3 - 1] + d9 * d9 / 6.0D < 1.0D && j3 < generatoraccess.getSeaLevel()) {
                                    int k3 = j2 | l2 << 4 | j3 << 8;

                                    if (!bitset.get(k3)) {
                                        bitset.set(k3);
                                        blockposition_mutableblockposition.c(k2, j3, i3);
                                        IBlockData iblockdata = generatoraccess.getType(blockposition_mutableblockposition);

                                        if (this.a(iblockdata)) {
                                            if (j3 == 10) {
                                                float f = random.nextFloat();

                                                if ((double) f < 0.25D) {
                                                    generatoraccess.setTypeAndData(blockposition_mutableblockposition, Blocks.MAGMA_BLOCK.getBlockData(), 2);
                                                    generatoraccess.getBlockTickList().a(blockposition_mutableblockposition, Blocks.MAGMA_BLOCK, 0);
                                                    flag = true;
                                                } else {
                                                    generatoraccess.setTypeAndData(blockposition_mutableblockposition, Blocks.OBSIDIAN.getBlockData(), 2);
                                                    flag = true;
                                                }
                                            } else if (j3 < 10) {
                                                generatoraccess.setTypeAndData(blockposition_mutableblockposition, Blocks.LAVA.getBlockData(), 2);
                                            } else {
                                                boolean flag1 = false;
                                                Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                                                while (iterator.hasNext()) {
                                                    EnumDirection enumdirection = (EnumDirection) iterator.next();
                                                    IBlockData iblockdata1 = generatoraccess.getType(blockposition_mutableblockposition.c(k2 + enumdirection.getAdjacentX(), j3, i3 + enumdirection.getAdjacentZ()));

                                                    if (iblockdata1.isAir()) {
                                                        generatoraccess.setTypeAndData(blockposition_mutableblockposition, WorldGenCanyonOcean.c.i(), 2);
                                                        generatoraccess.getFluidTickList().a(blockposition_mutableblockposition, WorldGenCanyonOcean.c.c(), 0);
                                                        flag = true;
                                                        flag1 = true;
                                                        break;
                                                    }
                                                }

                                                blockposition_mutableblockposition.c(k2, j3, i3);
                                                if (!flag1) {
                                                    generatoraccess.setTypeAndData(blockposition_mutableblockposition, WorldGenCanyonOcean.c.i(), 2);
                                                    flag = true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                return flag;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
