package net.minecraft.world.level.levelgen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.IChunkAccess;

public class WorldGenSurfaceMesaBryce extends WorldGenSurfaceMesa {

    private static final IBlockData WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.getBlockData();
    private static final IBlockData ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.getBlockData();
    private static final IBlockData TERRACOTTA = Blocks.TERRACOTTA.getBlockData();

    public WorldGenSurfaceMesaBryce(Codec<WorldGenSurfaceConfigurationBase> codec) {
        super(codec);
    }

    @Override
    public void a(Random random, IChunkAccess ichunkaccess, BiomeBase biomebase, int i, int j, int k, double d0, IBlockData iblockdata, IBlockData iblockdata1, int l, int i1, long j1, WorldGenSurfaceConfigurationBase worldgensurfaceconfigurationbase) {
        double d1 = 0.0D;
        double d2 = Math.min(Math.abs(d0), this.pillarNoise.a((double) i * 0.25D, (double) j * 0.25D, false) * 15.0D);

        if (d2 > 0.0D) {
            double d3 = 0.001953125D;
            double d4 = Math.abs(this.pillarRoofNoise.a((double) i * 0.001953125D, (double) j * 0.001953125D, false));

            d1 = d2 * d2 * 2.5D;
            double d5 = Math.ceil(d4 * 50.0D) + 14.0D;

            if (d1 > d5) {
                d1 = d5;
            }

            d1 += 64.0D;
        }

        int k1 = i & 15;
        int l1 = j & 15;
        IBlockData iblockdata2 = WorldGenSurfaceMesaBryce.WHITE_TERRACOTTA;
        WorldGenSurfaceConfiguration worldgensurfaceconfiguration = biomebase.e().e();
        IBlockData iblockdata3 = worldgensurfaceconfiguration.b();
        IBlockData iblockdata4 = worldgensurfaceconfiguration.a();
        IBlockData iblockdata5 = iblockdata3;
        int i2 = (int) (d0 / 3.0D + 3.0D + random.nextDouble() * 0.25D);
        boolean flag = Math.cos(d0 / 3.0D * 3.141592653589793D) > 0.0D;
        int j2 = -1;
        boolean flag1 = false;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int k2 = Math.max(k, (int) d1 + 1); k2 >= i1; --k2) {
            blockposition_mutableblockposition.d(k1, k2, l1);
            if (ichunkaccess.getType(blockposition_mutableblockposition).isAir() && k2 < (int) d1) {
                ichunkaccess.setType(blockposition_mutableblockposition, iblockdata, false);
            }

            IBlockData iblockdata6 = ichunkaccess.getType(blockposition_mutableblockposition);

            if (iblockdata6.isAir()) {
                j2 = -1;
            } else if (iblockdata6.a(iblockdata.getBlock())) {
                if (j2 == -1) {
                    flag1 = false;
                    if (i2 <= 0) {
                        iblockdata2 = Blocks.AIR.getBlockData();
                        iblockdata5 = iblockdata;
                    } else if (k2 >= l - 4 && k2 <= l + 1) {
                        iblockdata2 = WorldGenSurfaceMesaBryce.WHITE_TERRACOTTA;
                        iblockdata5 = iblockdata3;
                    }

                    if (k2 < l && (iblockdata2 == null || iblockdata2.isAir())) {
                        iblockdata2 = iblockdata1;
                    }

                    j2 = i2 + Math.max(0, k2 - l);
                    if (k2 >= l - 1) {
                        if (k2 > l + 3 + i2) {
                            IBlockData iblockdata7;

                            if (k2 >= 64 && k2 <= 127) {
                                if (flag) {
                                    iblockdata7 = WorldGenSurfaceMesaBryce.TERRACOTTA;
                                } else {
                                    iblockdata7 = this.a(i, k2, j);
                                }
                            } else {
                                iblockdata7 = WorldGenSurfaceMesaBryce.ORANGE_TERRACOTTA;
                            }

                            ichunkaccess.setType(blockposition_mutableblockposition, iblockdata7, false);
                        } else {
                            ichunkaccess.setType(blockposition_mutableblockposition, iblockdata4, false);
                            flag1 = true;
                        }
                    } else {
                        ichunkaccess.setType(blockposition_mutableblockposition, iblockdata5, false);
                        if (iblockdata5.a(Blocks.WHITE_TERRACOTTA) || iblockdata5.a(Blocks.ORANGE_TERRACOTTA) || iblockdata5.a(Blocks.MAGENTA_TERRACOTTA) || iblockdata5.a(Blocks.LIGHT_BLUE_TERRACOTTA) || iblockdata5.a(Blocks.YELLOW_TERRACOTTA) || iblockdata5.a(Blocks.LIME_TERRACOTTA) || iblockdata5.a(Blocks.PINK_TERRACOTTA) || iblockdata5.a(Blocks.GRAY_TERRACOTTA) || iblockdata5.a(Blocks.LIGHT_GRAY_TERRACOTTA) || iblockdata5.a(Blocks.CYAN_TERRACOTTA) || iblockdata5.a(Blocks.PURPLE_TERRACOTTA) || iblockdata5.a(Blocks.BLUE_TERRACOTTA) || iblockdata5.a(Blocks.BROWN_TERRACOTTA) || iblockdata5.a(Blocks.GREEN_TERRACOTTA) || iblockdata5.a(Blocks.RED_TERRACOTTA) || iblockdata5.a(Blocks.BLACK_TERRACOTTA)) {
                            ichunkaccess.setType(blockposition_mutableblockposition, WorldGenSurfaceMesaBryce.ORANGE_TERRACOTTA, false);
                        }
                    }
                } else if (j2 > 0) {
                    --j2;
                    if (flag1) {
                        ichunkaccess.setType(blockposition_mutableblockposition, WorldGenSurfaceMesaBryce.ORANGE_TERRACOTTA, false);
                    } else {
                        ichunkaccess.setType(blockposition_mutableblockposition, this.a(i, k2, j), false);
                    }
                }
            }
        }

    }
}
