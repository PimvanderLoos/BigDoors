package net.minecraft.world.level.levelgen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.IChunkAccess;

public class WorldGenSurfaceMesaForest extends WorldGenSurfaceMesa {

    private static final IBlockData WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.getBlockData();
    private static final IBlockData ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.getBlockData();
    private static final IBlockData TERRACOTTA = Blocks.TERRACOTTA.getBlockData();

    public WorldGenSurfaceMesaForest(Codec<WorldGenSurfaceConfigurationBase> codec) {
        super(codec);
    }

    @Override
    public void a(Random random, IChunkAccess ichunkaccess, BiomeBase biomebase, int i, int j, int k, double d0, IBlockData iblockdata, IBlockData iblockdata1, int l, int i1, long j1, WorldGenSurfaceConfigurationBase worldgensurfaceconfigurationbase) {
        int k1 = i & 15;
        int l1 = j & 15;
        IBlockData iblockdata2 = WorldGenSurfaceMesaForest.WHITE_TERRACOTTA;
        WorldGenSurfaceConfiguration worldgensurfaceconfiguration = biomebase.e().e();
        IBlockData iblockdata3 = worldgensurfaceconfiguration.b();
        IBlockData iblockdata4 = worldgensurfaceconfiguration.a();
        IBlockData iblockdata5 = iblockdata3;
        int i2 = (int) (d0 / 3.0D + 3.0D + random.nextDouble() * 0.25D);
        boolean flag = Math.cos(d0 / 3.0D * 3.141592653589793D) > 0.0D;
        int j2 = -1;
        boolean flag1 = false;
        int k2 = 0;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int l2 = k; l2 >= i1; --l2) {
            if (k2 < 15) {
                blockposition_mutableblockposition.d(k1, l2, l1);
                IBlockData iblockdata6 = ichunkaccess.getType(blockposition_mutableblockposition);

                if (iblockdata6.isAir()) {
                    j2 = -1;
                } else if (iblockdata6.a(iblockdata.getBlock())) {
                    if (j2 == -1) {
                        flag1 = false;
                        if (i2 <= 0) {
                            iblockdata2 = Blocks.AIR.getBlockData();
                            iblockdata5 = iblockdata;
                        } else if (l2 >= l - 4 && l2 <= l + 1) {
                            iblockdata2 = WorldGenSurfaceMesaForest.WHITE_TERRACOTTA;
                            iblockdata5 = iblockdata3;
                        }

                        if (l2 < l && (iblockdata2 == null || iblockdata2.isAir())) {
                            iblockdata2 = iblockdata1;
                        }

                        j2 = i2 + Math.max(0, l2 - l);
                        if (l2 >= l - 1) {
                            if (l2 > 86 + i2 * 2) {
                                if (flag) {
                                    ichunkaccess.setType(blockposition_mutableblockposition, Blocks.COARSE_DIRT.getBlockData(), false);
                                } else {
                                    ichunkaccess.setType(blockposition_mutableblockposition, Blocks.GRASS_BLOCK.getBlockData(), false);
                                }
                            } else if (l2 > l + 3 + i2) {
                                IBlockData iblockdata7;

                                if (l2 >= 64 && l2 <= 127) {
                                    if (flag) {
                                        iblockdata7 = WorldGenSurfaceMesaForest.TERRACOTTA;
                                    } else {
                                        iblockdata7 = this.a(i, l2, j);
                                    }
                                } else {
                                    iblockdata7 = WorldGenSurfaceMesaForest.ORANGE_TERRACOTTA;
                                }

                                ichunkaccess.setType(blockposition_mutableblockposition, iblockdata7, false);
                            } else {
                                ichunkaccess.setType(blockposition_mutableblockposition, iblockdata4, false);
                                flag1 = true;
                            }
                        } else {
                            ichunkaccess.setType(blockposition_mutableblockposition, iblockdata5, false);
                            if (iblockdata5 == WorldGenSurfaceMesaForest.WHITE_TERRACOTTA) {
                                ichunkaccess.setType(blockposition_mutableblockposition, WorldGenSurfaceMesaForest.ORANGE_TERRACOTTA, false);
                            }
                        }
                    } else if (j2 > 0) {
                        --j2;
                        if (flag1) {
                            ichunkaccess.setType(blockposition_mutableblockposition, WorldGenSurfaceMesaForest.ORANGE_TERRACOTTA, false);
                        } else {
                            ichunkaccess.setType(blockposition_mutableblockposition, this.a(i, l2, j), false);
                        }
                    }

                    ++k2;
                }
            }
        }

    }
}
