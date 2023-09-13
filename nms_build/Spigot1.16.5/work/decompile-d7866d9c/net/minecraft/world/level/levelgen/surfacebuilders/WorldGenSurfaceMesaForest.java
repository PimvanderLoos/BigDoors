package net.minecraft.world.level.levelgen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.IChunkAccess;

public class WorldGenSurfaceMesaForest extends WorldGenSurfaceMesa {

    private static final IBlockData K = Blocks.WHITE_TERRACOTTA.getBlockData();
    private static final IBlockData L = Blocks.ORANGE_TERRACOTTA.getBlockData();
    private static final IBlockData M = Blocks.TERRACOTTA.getBlockData();

    public WorldGenSurfaceMesaForest(Codec<WorldGenSurfaceConfigurationBase> codec) {
        super(codec);
    }

    @Override
    public void a(Random random, IChunkAccess ichunkaccess, BiomeBase biomebase, int i, int j, int k, double d0, IBlockData iblockdata, IBlockData iblockdata1, int l, long i1, WorldGenSurfaceConfigurationBase worldgensurfaceconfigurationbase) {
        int j1 = i & 15;
        int k1 = j & 15;
        IBlockData iblockdata2 = WorldGenSurfaceMesaForest.K;
        WorldGenSurfaceConfiguration worldgensurfaceconfiguration = biomebase.e().e();
        IBlockData iblockdata3 = worldgensurfaceconfiguration.b();
        IBlockData iblockdata4 = worldgensurfaceconfiguration.a();
        IBlockData iblockdata5 = iblockdata3;
        int l1 = (int) (d0 / 3.0D + 3.0D + random.nextDouble() * 0.25D);
        boolean flag = Math.cos(d0 / 3.0D * 3.141592653589793D) > 0.0D;
        int i2 = -1;
        boolean flag1 = false;
        int j2 = 0;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int k2 = k; k2 >= 0; --k2) {
            if (j2 < 15) {
                blockposition_mutableblockposition.d(j1, k2, k1);
                IBlockData iblockdata6 = ichunkaccess.getType(blockposition_mutableblockposition);

                if (iblockdata6.isAir()) {
                    i2 = -1;
                } else if (iblockdata6.a(iblockdata.getBlock())) {
                    if (i2 == -1) {
                        flag1 = false;
                        if (l1 <= 0) {
                            iblockdata2 = Blocks.AIR.getBlockData();
                            iblockdata5 = iblockdata;
                        } else if (k2 >= l - 4 && k2 <= l + 1) {
                            iblockdata2 = WorldGenSurfaceMesaForest.K;
                            iblockdata5 = iblockdata3;
                        }

                        if (k2 < l && (iblockdata2 == null || iblockdata2.isAir())) {
                            iblockdata2 = iblockdata1;
                        }

                        i2 = l1 + Math.max(0, k2 - l);
                        if (k2 >= l - 1) {
                            if (k2 > 86 + l1 * 2) {
                                if (flag) {
                                    ichunkaccess.setType(blockposition_mutableblockposition, Blocks.COARSE_DIRT.getBlockData(), false);
                                } else {
                                    ichunkaccess.setType(blockposition_mutableblockposition, Blocks.GRASS_BLOCK.getBlockData(), false);
                                }
                            } else if (k2 > l + 3 + l1) {
                                IBlockData iblockdata7;

                                if (k2 >= 64 && k2 <= 127) {
                                    if (flag) {
                                        iblockdata7 = WorldGenSurfaceMesaForest.M;
                                    } else {
                                        iblockdata7 = this.a(i, k2, j);
                                    }
                                } else {
                                    iblockdata7 = WorldGenSurfaceMesaForest.L;
                                }

                                ichunkaccess.setType(blockposition_mutableblockposition, iblockdata7, false);
                            } else {
                                ichunkaccess.setType(blockposition_mutableblockposition, iblockdata4, false);
                                flag1 = true;
                            }
                        } else {
                            ichunkaccess.setType(blockposition_mutableblockposition, iblockdata5, false);
                            if (iblockdata5 == WorldGenSurfaceMesaForest.K) {
                                ichunkaccess.setType(blockposition_mutableblockposition, WorldGenSurfaceMesaForest.L, false);
                            }
                        }
                    } else if (i2 > 0) {
                        --i2;
                        if (flag1) {
                            ichunkaccess.setType(blockposition_mutableblockposition, WorldGenSurfaceMesaForest.L, false);
                        } else {
                            ichunkaccess.setType(blockposition_mutableblockposition, this.a(i, k2, j), false);
                        }
                    }

                    ++j2;
                }
            }
        }

    }
}
