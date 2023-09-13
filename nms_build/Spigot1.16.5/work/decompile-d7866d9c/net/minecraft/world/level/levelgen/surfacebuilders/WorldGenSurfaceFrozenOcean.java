package net.minecraft.world.level.levelgen.surfacebuilders;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.synth.NoiseGenerator3;
import net.minecraft.world.level.material.Material;

public class WorldGenSurfaceFrozenOcean extends WorldGenSurface<WorldGenSurfaceConfigurationBase> {

    protected static final IBlockData a = Blocks.PACKED_ICE.getBlockData();
    protected static final IBlockData b = Blocks.SNOW_BLOCK.getBlockData();
    private static final IBlockData c = Blocks.AIR.getBlockData();
    private static final IBlockData d = Blocks.GRAVEL.getBlockData();
    private static final IBlockData e = Blocks.ICE.getBlockData();
    private NoiseGenerator3 K;
    private NoiseGenerator3 L;
    private long M;

    public WorldGenSurfaceFrozenOcean(Codec<WorldGenSurfaceConfigurationBase> codec) {
        super(codec);
    }

    public void a(Random random, IChunkAccess ichunkaccess, BiomeBase biomebase, int i, int j, int k, double d0, IBlockData iblockdata, IBlockData iblockdata1, int l, long i1, WorldGenSurfaceConfigurationBase worldgensurfaceconfigurationbase) {
        double d1 = 0.0D;
        double d2 = 0.0D;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        float f = biomebase.getAdjustedTemperature(blockposition_mutableblockposition.d(i, 63, j));
        double d3 = Math.min(Math.abs(d0), this.K.a((double) i * 0.1D, (double) j * 0.1D, false) * 15.0D);

        if (d3 > 1.8D) {
            double d4 = 0.09765625D;
            double d5 = Math.abs(this.L.a((double) i * 0.09765625D, (double) j * 0.09765625D, false));

            d1 = d3 * d3 * 1.2D;
            double d6 = Math.ceil(d5 * 40.0D) + 14.0D;

            if (d1 > d6) {
                d1 = d6;
            }

            if (f > 0.1F) {
                d1 -= 2.0D;
            }

            if (d1 > 2.0D) {
                d2 = (double) l - d1 - 7.0D;
                d1 += (double) l;
            } else {
                d1 = 0.0D;
            }
        }

        int j1 = i & 15;
        int k1 = j & 15;
        WorldGenSurfaceConfiguration worldgensurfaceconfiguration = biomebase.e().e();
        IBlockData iblockdata2 = worldgensurfaceconfiguration.b();
        IBlockData iblockdata3 = worldgensurfaceconfiguration.a();
        IBlockData iblockdata4 = iblockdata2;
        IBlockData iblockdata5 = iblockdata3;
        int l1 = (int) (d0 / 3.0D + 3.0D + random.nextDouble() * 0.25D);
        int i2 = -1;
        int j2 = 0;
        int k2 = 2 + random.nextInt(4);
        int l2 = l + 18 + random.nextInt(10);

        for (int i3 = Math.max(k, (int) d1 + 1); i3 >= 0; --i3) {
            blockposition_mutableblockposition.d(j1, i3, k1);
            if (ichunkaccess.getType(blockposition_mutableblockposition).isAir() && i3 < (int) d1 && random.nextDouble() > 0.01D) {
                ichunkaccess.setType(blockposition_mutableblockposition, WorldGenSurfaceFrozenOcean.a, false);
            } else if (ichunkaccess.getType(blockposition_mutableblockposition).getMaterial() == Material.WATER && i3 > (int) d2 && i3 < l && d2 != 0.0D && random.nextDouble() > 0.15D) {
                ichunkaccess.setType(blockposition_mutableblockposition, WorldGenSurfaceFrozenOcean.a, false);
            }

            IBlockData iblockdata6 = ichunkaccess.getType(blockposition_mutableblockposition);

            if (iblockdata6.isAir()) {
                i2 = -1;
            } else if (iblockdata6.a(iblockdata.getBlock())) {
                if (i2 == -1) {
                    if (l1 <= 0) {
                        iblockdata5 = WorldGenSurfaceFrozenOcean.c;
                        iblockdata4 = iblockdata;
                    } else if (i3 >= l - 4 && i3 <= l + 1) {
                        iblockdata5 = iblockdata3;
                        iblockdata4 = iblockdata2;
                    }

                    if (i3 < l && (iblockdata5 == null || iblockdata5.isAir())) {
                        if (biomebase.getAdjustedTemperature(blockposition_mutableblockposition.d(i, i3, j)) < 0.15F) {
                            iblockdata5 = WorldGenSurfaceFrozenOcean.e;
                        } else {
                            iblockdata5 = iblockdata1;
                        }
                    }

                    i2 = l1;
                    if (i3 >= l - 1) {
                        ichunkaccess.setType(blockposition_mutableblockposition, iblockdata5, false);
                    } else if (i3 < l - 7 - l1) {
                        iblockdata5 = WorldGenSurfaceFrozenOcean.c;
                        iblockdata4 = iblockdata;
                        ichunkaccess.setType(blockposition_mutableblockposition, WorldGenSurfaceFrozenOcean.d, false);
                    } else {
                        ichunkaccess.setType(blockposition_mutableblockposition, iblockdata4, false);
                    }
                } else if (i2 > 0) {
                    --i2;
                    ichunkaccess.setType(blockposition_mutableblockposition, iblockdata4, false);
                    if (i2 == 0 && iblockdata4.a(Blocks.SAND) && l1 > 1) {
                        i2 = random.nextInt(4) + Math.max(0, i3 - 63);
                        iblockdata4 = iblockdata4.a(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.getBlockData() : Blocks.SANDSTONE.getBlockData();
                    }
                }
            } else if (iblockdata6.a(Blocks.PACKED_ICE) && j2 <= k2 && i3 > l2) {
                ichunkaccess.setType(blockposition_mutableblockposition, WorldGenSurfaceFrozenOcean.b, false);
                ++j2;
            }
        }

    }

    @Override
    public void a(long i) {
        if (this.M != i || this.K == null || this.L == null) {
            SeededRandom seededrandom = new SeededRandom(i);

            this.K = new NoiseGenerator3(seededrandom, IntStream.rangeClosed(-3, 0));
            this.L = new NoiseGenerator3(seededrandom, ImmutableList.of(0));
        }

        this.M = i;
    }
}
