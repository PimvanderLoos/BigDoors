package net.minecraft.server;

import java.util.Random;

public class WorldGenSurfaceMesaBryce extends WorldGenSurfaceMesa {

    private static final IBlockData f = Blocks.WHITE_TERRACOTTA.getBlockData();
    private static final IBlockData g = Blocks.ORANGE_TERRACOTTA.getBlockData();
    private static final IBlockData h = Blocks.TERRACOTTA.getBlockData();

    public WorldGenSurfaceMesaBryce() {}

    public void a(Random random, IChunkAccess ichunkaccess, BiomeBase biomebase, int i, int j, int k, double d0, IBlockData iblockdata, IBlockData iblockdata1, int l, long i1, WorldGenSurfaceConfigurationBase worldgensurfaceconfigurationbase) {
        double d1 = 0.0D;
        double d2 = Math.min(Math.abs(d0), this.c.a((double) i * 0.25D, (double) j * 0.25D));

        if (d2 > 0.0D) {
            double d3 = 0.001953125D;
            double d4 = Math.abs(this.d.a((double) i * 0.001953125D, (double) j * 0.001953125D));

            d1 = d2 * d2 * 2.5D;
            double d5 = Math.ceil(d4 * 50.0D) + 14.0D;

            if (d1 > d5) {
                d1 = d5;
            }

            d1 += 64.0D;
        }

        int j1 = i & 15;
        int k1 = j & 15;
        IBlockData iblockdata2 = WorldGenSurfaceMesaBryce.f;
        IBlockData iblockdata3 = biomebase.r().b();
        int l1 = (int) (d0 / 3.0D + 3.0D + random.nextDouble() * 0.25D);
        boolean flag = Math.cos(d0 / 3.0D * 3.141592653589793D) > 0.0D;
        int i2 = -1;
        boolean flag1 = false;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int j2 = Math.max(k, (int) d1 + 1); j2 >= 0; --j2) {
            blockposition_mutableblockposition.c(j1, j2, k1);
            if (ichunkaccess.getType(blockposition_mutableblockposition).isAir() && j2 < (int) d1) {
                ichunkaccess.setType(blockposition_mutableblockposition, iblockdata, false);
            }

            IBlockData iblockdata4 = ichunkaccess.getType(blockposition_mutableblockposition);

            if (iblockdata4.isAir()) {
                i2 = -1;
            } else if (iblockdata4.getBlock() == iblockdata.getBlock()) {
                if (i2 == -1) {
                    flag1 = false;
                    if (l1 <= 0) {
                        iblockdata2 = Blocks.AIR.getBlockData();
                        iblockdata3 = iblockdata;
                    } else if (j2 >= l - 4 && j2 <= l + 1) {
                        iblockdata2 = WorldGenSurfaceMesaBryce.f;
                        iblockdata3 = biomebase.r().b();
                    }

                    if (j2 < l && (iblockdata2 == null || iblockdata2.isAir())) {
                        iblockdata2 = iblockdata1;
                    }

                    i2 = l1 + Math.max(0, j2 - l);
                    if (j2 >= l - 1) {
                        if (j2 > l + 3 + l1) {
                            IBlockData iblockdata5;

                            if (j2 >= 64 && j2 <= 127) {
                                if (flag) {
                                    iblockdata5 = WorldGenSurfaceMesaBryce.h;
                                } else {
                                    iblockdata5 = this.a(i, j2, j);
                                }
                            } else {
                                iblockdata5 = WorldGenSurfaceMesaBryce.g;
                            }

                            ichunkaccess.setType(blockposition_mutableblockposition, iblockdata5, false);
                        } else {
                            ichunkaccess.setType(blockposition_mutableblockposition, biomebase.r().a(), false);
                            flag1 = true;
                        }
                    } else {
                        ichunkaccess.setType(blockposition_mutableblockposition, iblockdata3, false);
                        Block block = iblockdata3.getBlock();

                        if (block == Blocks.WHITE_TERRACOTTA || block == Blocks.ORANGE_TERRACOTTA || block == Blocks.MAGENTA_TERRACOTTA || block == Blocks.LIGHT_BLUE_TERRACOTTA || block == Blocks.YELLOW_TERRACOTTA || block == Blocks.LIME_TERRACOTTA || block == Blocks.PINK_TERRACOTTA || block == Blocks.GRAY_TERRACOTTA || block == Blocks.LIGHT_GRAY_TERRACOTTA || block == Blocks.CYAN_TERRACOTTA || block == Blocks.PURPLE_TERRACOTTA || block == Blocks.BLUE_TERRACOTTA || block == Blocks.BROWN_TERRACOTTA || block == Blocks.GREEN_TERRACOTTA || block == Blocks.RED_TERRACOTTA || block == Blocks.BLACK_TERRACOTTA) {
                            ichunkaccess.setType(blockposition_mutableblockposition, WorldGenSurfaceMesaBryce.g, false);
                        }
                    }
                } else if (i2 > 0) {
                    --i2;
                    if (flag1) {
                        ichunkaccess.setType(blockposition_mutableblockposition, WorldGenSurfaceMesaBryce.g, false);
                    } else {
                        ichunkaccess.setType(blockposition_mutableblockposition, this.a(i, j2, j), false);
                    }
                }
            }
        }

    }
}
