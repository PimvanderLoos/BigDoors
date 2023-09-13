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

    protected static final IBlockData PACKED_ICE = Blocks.PACKED_ICE.getBlockData();
    protected static final IBlockData SNOW_BLOCK = Blocks.SNOW_BLOCK.getBlockData();
    private static final IBlockData AIR = Blocks.AIR.getBlockData();
    private static final IBlockData GRAVEL = Blocks.GRAVEL.getBlockData();
    private static final IBlockData ICE = Blocks.ICE.getBlockData();
    private NoiseGenerator3 icebergNoise;
    private NoiseGenerator3 icebergRoofNoise;
    private long seed;

    public WorldGenSurfaceFrozenOcean(Codec<WorldGenSurfaceConfigurationBase> codec) {
        super(codec);
    }

    public void a(Random random, IChunkAccess ichunkaccess, BiomeBase biomebase, int i, int j, int k, double d0, IBlockData iblockdata, IBlockData iblockdata1, int l, int i1, long j1, WorldGenSurfaceConfigurationBase worldgensurfaceconfigurationbase) {
        double d1 = 0.0D;
        double d2 = 0.0D;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        float f = biomebase.getAdjustedTemperature(blockposition_mutableblockposition.d(i, 63, j));
        double d3 = Math.min(Math.abs(d0), this.icebergNoise.a((double) i * 0.1D, (double) j * 0.1D, false) * 15.0D);

        if (d3 > 1.8D) {
            double d4 = 0.09765625D;
            double d5 = Math.abs(this.icebergRoofNoise.a((double) i * 0.09765625D, (double) j * 0.09765625D, false));

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

        int k1 = i & 15;
        int l1 = j & 15;
        WorldGenSurfaceConfiguration worldgensurfaceconfiguration = biomebase.e().e();
        IBlockData iblockdata2 = worldgensurfaceconfiguration.b();
        IBlockData iblockdata3 = worldgensurfaceconfiguration.a();
        IBlockData iblockdata4 = iblockdata2;
        IBlockData iblockdata5 = iblockdata3;
        int i2 = (int) (d0 / 3.0D + 3.0D + random.nextDouble() * 0.25D);
        int j2 = -1;
        int k2 = 0;
        int l2 = 2 + random.nextInt(4);
        int i3 = l + 18 + random.nextInt(10);

        for (int j3 = Math.max(k, (int) d1 + 1); j3 >= i1; --j3) {
            blockposition_mutableblockposition.d(k1, j3, l1);
            if (ichunkaccess.getType(blockposition_mutableblockposition).isAir() && j3 < (int) d1 && random.nextDouble() > 0.01D) {
                ichunkaccess.setType(blockposition_mutableblockposition, WorldGenSurfaceFrozenOcean.PACKED_ICE, false);
            } else if (ichunkaccess.getType(blockposition_mutableblockposition).getMaterial() == Material.WATER && j3 > (int) d2 && j3 < l && d2 != 0.0D && random.nextDouble() > 0.15D) {
                ichunkaccess.setType(blockposition_mutableblockposition, WorldGenSurfaceFrozenOcean.PACKED_ICE, false);
            }

            IBlockData iblockdata6 = ichunkaccess.getType(blockposition_mutableblockposition);

            if (iblockdata6.isAir()) {
                j2 = -1;
            } else if (iblockdata6.a(iblockdata.getBlock())) {
                if (j2 == -1) {
                    if (i2 <= 0) {
                        iblockdata5 = WorldGenSurfaceFrozenOcean.AIR;
                        iblockdata4 = iblockdata;
                    } else if (j3 >= l - 4 && j3 <= l + 1) {
                        iblockdata5 = iblockdata3;
                        iblockdata4 = iblockdata2;
                    }

                    if (j3 < l && (iblockdata5 == null || iblockdata5.isAir())) {
                        if (biomebase.getAdjustedTemperature(blockposition_mutableblockposition.d(i, j3, j)) < 0.15F) {
                            iblockdata5 = WorldGenSurfaceFrozenOcean.ICE;
                        } else {
                            iblockdata5 = iblockdata1;
                        }
                    }

                    j2 = i2;
                    if (j3 >= l - 1) {
                        ichunkaccess.setType(blockposition_mutableblockposition, iblockdata5, false);
                    } else if (j3 < l - 7 - i2) {
                        iblockdata5 = WorldGenSurfaceFrozenOcean.AIR;
                        iblockdata4 = iblockdata;
                        ichunkaccess.setType(blockposition_mutableblockposition, WorldGenSurfaceFrozenOcean.GRAVEL, false);
                    } else {
                        ichunkaccess.setType(blockposition_mutableblockposition, iblockdata4, false);
                    }
                } else if (j2 > 0) {
                    --j2;
                    ichunkaccess.setType(blockposition_mutableblockposition, iblockdata4, false);
                    if (j2 == 0 && iblockdata4.a(Blocks.SAND) && i2 > 1) {
                        j2 = random.nextInt(4) + Math.max(0, j3 - 63);
                        iblockdata4 = iblockdata4.a(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.getBlockData() : Blocks.SANDSTONE.getBlockData();
                    }
                }
            } else if (iblockdata6.a(Blocks.PACKED_ICE) && k2 <= l2 && j3 > i3) {
                ichunkaccess.setType(blockposition_mutableblockposition, WorldGenSurfaceFrozenOcean.SNOW_BLOCK, false);
                ++k2;
            }
        }

    }

    @Override
    public void a(long i) {
        if (this.seed != i || this.icebergNoise == null || this.icebergRoofNoise == null) {
            SeededRandom seededrandom = new SeededRandom(i);

            this.icebergNoise = new NoiseGenerator3(seededrandom, IntStream.rangeClosed(-3, 0));
            this.icebergRoofNoise = new NoiseGenerator3(seededrandom, ImmutableList.of(0));
        }

        this.seed = i;
    }
}
