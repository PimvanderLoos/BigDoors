package net.minecraft.world.level.levelgen.surfacebuilders;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.synth.NoiseGenerator3;

public class WorldGenSurfaceMesa extends WorldGenSurface<WorldGenSurfaceConfigurationBase> {

    protected static final int MAX_CLAY_DEPTH = 15;
    private static final IBlockData WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.getBlockData();
    private static final IBlockData ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.getBlockData();
    private static final IBlockData TERRACOTTA = Blocks.TERRACOTTA.getBlockData();
    private static final IBlockData YELLOW_TERRACOTTA = Blocks.YELLOW_TERRACOTTA.getBlockData();
    private static final IBlockData BROWN_TERRACOTTA = Blocks.BROWN_TERRACOTTA.getBlockData();
    private static final IBlockData RED_TERRACOTTA = Blocks.RED_TERRACOTTA.getBlockData();
    private static final IBlockData LIGHT_GRAY_TERRACOTTA = Blocks.LIGHT_GRAY_TERRACOTTA.getBlockData();
    protected IBlockData[] clayBands;
    protected long seed;
    protected NoiseGenerator3 pillarNoise;
    protected NoiseGenerator3 pillarRoofNoise;
    protected NoiseGenerator3 clayBandsOffsetNoise;

    public WorldGenSurfaceMesa(Codec<WorldGenSurfaceConfigurationBase> codec) {
        super(codec);
    }

    public void a(Random random, IChunkAccess ichunkaccess, BiomeBase biomebase, int i, int j, int k, double d0, IBlockData iblockdata, IBlockData iblockdata1, int l, int i1, long j1, WorldGenSurfaceConfigurationBase worldgensurfaceconfigurationbase) {
        int k1 = i & 15;
        int l1 = j & 15;
        IBlockData iblockdata2 = WorldGenSurfaceMesa.WHITE_TERRACOTTA;
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
                            iblockdata2 = WorldGenSurfaceMesa.WHITE_TERRACOTTA;
                            iblockdata5 = iblockdata3;
                        }

                        if (l2 < l && (iblockdata2 == null || iblockdata2.isAir())) {
                            iblockdata2 = iblockdata1;
                        }

                        j2 = i2 + Math.max(0, l2 - l);
                        if (l2 >= l - 1) {
                            if (l2 > l + 3 + i2) {
                                IBlockData iblockdata7;

                                if (l2 >= 64 && l2 <= 127) {
                                    if (flag) {
                                        iblockdata7 = WorldGenSurfaceMesa.TERRACOTTA;
                                    } else {
                                        iblockdata7 = this.a(i, l2, j);
                                    }
                                } else {
                                    iblockdata7 = WorldGenSurfaceMesa.ORANGE_TERRACOTTA;
                                }

                                ichunkaccess.setType(blockposition_mutableblockposition, iblockdata7, false);
                            } else {
                                ichunkaccess.setType(blockposition_mutableblockposition, iblockdata4, false);
                                flag1 = true;
                            }
                        } else {
                            ichunkaccess.setType(blockposition_mutableblockposition, iblockdata5, false);
                            if (iblockdata5.a(Blocks.WHITE_TERRACOTTA) || iblockdata5.a(Blocks.ORANGE_TERRACOTTA) || iblockdata5.a(Blocks.MAGENTA_TERRACOTTA) || iblockdata5.a(Blocks.LIGHT_BLUE_TERRACOTTA) || iblockdata5.a(Blocks.YELLOW_TERRACOTTA) || iblockdata5.a(Blocks.LIME_TERRACOTTA) || iblockdata5.a(Blocks.PINK_TERRACOTTA) || iblockdata5.a(Blocks.GRAY_TERRACOTTA) || iblockdata5.a(Blocks.LIGHT_GRAY_TERRACOTTA) || iblockdata5.a(Blocks.CYAN_TERRACOTTA) || iblockdata5.a(Blocks.PURPLE_TERRACOTTA) || iblockdata5.a(Blocks.BLUE_TERRACOTTA) || iblockdata5.a(Blocks.BROWN_TERRACOTTA) || iblockdata5.a(Blocks.GREEN_TERRACOTTA) || iblockdata5.a(Blocks.RED_TERRACOTTA) || iblockdata5.a(Blocks.BLACK_TERRACOTTA)) {
                                ichunkaccess.setType(blockposition_mutableblockposition, WorldGenSurfaceMesa.ORANGE_TERRACOTTA, false);
                            }
                        }
                    } else if (j2 > 0) {
                        --j2;
                        if (flag1) {
                            ichunkaccess.setType(blockposition_mutableblockposition, WorldGenSurfaceMesa.ORANGE_TERRACOTTA, false);
                        } else {
                            ichunkaccess.setType(blockposition_mutableblockposition, this.a(i, l2, j), false);
                        }
                    }

                    ++k2;
                }
            }
        }

    }

    @Override
    public void a(long i) {
        if (this.seed != i || this.clayBands == null) {
            this.b(i);
        }

        if (this.seed != i || this.pillarNoise == null || this.pillarRoofNoise == null) {
            SeededRandom seededrandom = new SeededRandom(i);

            this.pillarNoise = new NoiseGenerator3(seededrandom, IntStream.rangeClosed(-3, 0));
            this.pillarRoofNoise = new NoiseGenerator3(seededrandom, ImmutableList.of(0));
        }

        this.seed = i;
    }

    protected void b(long i) {
        this.clayBands = new IBlockData[64];
        Arrays.fill(this.clayBands, WorldGenSurfaceMesa.TERRACOTTA);
        SeededRandom seededrandom = new SeededRandom(i);

        this.clayBandsOffsetNoise = new NoiseGenerator3(seededrandom, ImmutableList.of(0));

        int j;

        for (j = 0; j < 64; ++j) {
            j += seededrandom.nextInt(5) + 1;
            if (j < 64) {
                this.clayBands[j] = WorldGenSurfaceMesa.ORANGE_TERRACOTTA;
            }
        }

        j = seededrandom.nextInt(4) + 2;

        int k;
        int l;
        int i1;
        int j1;

        for (j1 = 0; j1 < j; ++j1) {
            k = seededrandom.nextInt(3) + 1;
            l = seededrandom.nextInt(64);

            for (i1 = 0; l + i1 < 64 && i1 < k; ++i1) {
                this.clayBands[l + i1] = WorldGenSurfaceMesa.YELLOW_TERRACOTTA;
            }
        }

        j1 = seededrandom.nextInt(4) + 2;

        int k1;

        for (k = 0; k < j1; ++k) {
            l = seededrandom.nextInt(3) + 2;
            i1 = seededrandom.nextInt(64);

            for (k1 = 0; i1 + k1 < 64 && k1 < l; ++k1) {
                this.clayBands[i1 + k1] = WorldGenSurfaceMesa.BROWN_TERRACOTTA;
            }
        }

        k = seededrandom.nextInt(4) + 2;

        for (l = 0; l < k; ++l) {
            i1 = seededrandom.nextInt(3) + 1;
            k1 = seededrandom.nextInt(64);

            for (int l1 = 0; k1 + l1 < 64 && l1 < i1; ++l1) {
                this.clayBands[k1 + l1] = WorldGenSurfaceMesa.RED_TERRACOTTA;
            }
        }

        l = seededrandom.nextInt(3) + 3;
        i1 = 0;

        for (k1 = 0; k1 < l; ++k1) {
            boolean flag = true;

            i1 += seededrandom.nextInt(16) + 4;

            for (int i2 = 0; i1 + i2 < 64 && i2 < 1; ++i2) {
                this.clayBands[i1 + i2] = WorldGenSurfaceMesa.WHITE_TERRACOTTA;
                if (i1 + i2 > 1 && seededrandom.nextBoolean()) {
                    this.clayBands[i1 + i2 - 1] = WorldGenSurfaceMesa.LIGHT_GRAY_TERRACOTTA;
                }

                if (i1 + i2 < 63 && seededrandom.nextBoolean()) {
                    this.clayBands[i1 + i2 + 1] = WorldGenSurfaceMesa.LIGHT_GRAY_TERRACOTTA;
                }
            }
        }

    }

    protected IBlockData a(int i, int j, int k) {
        int l = (int) Math.round(this.clayBandsOffsetNoise.a((double) i / 512.0D, (double) k / 512.0D, false) * 2.0D);

        return this.clayBands[(j + l + 64) % 64];
    }
}
