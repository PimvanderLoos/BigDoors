package net.minecraft.server;

import java.util.Arrays;
import java.util.Random;

public class BiomeMesa extends BiomeBase {

    protected static final IBlockData y = Blocks.DIRT.getBlockData().set(BlockDirt.VARIANT, BlockDirt.EnumDirtVariant.COARSE_DIRT);
    protected static final IBlockData z = Blocks.GRASS.getBlockData();
    protected static final IBlockData A = Blocks.HARDENED_CLAY.getBlockData();
    protected static final IBlockData B = Blocks.STAINED_HARDENED_CLAY.getBlockData();
    protected static final IBlockData C = BiomeMesa.B.set(BlockCloth.COLOR, EnumColor.ORANGE);
    protected static final IBlockData D = Blocks.SAND.getBlockData().set(BlockSand.VARIANT, BlockSand.EnumSandVariant.RED_SAND);
    private IBlockData[] E;
    private long F;
    private NoiseGenerator3 G;
    private NoiseGenerator3 H;
    private NoiseGenerator3 I;
    private final boolean J;
    private final boolean K;

    public BiomeMesa(boolean flag, boolean flag1, BiomeBase.a biomebase_a) {
        super(biomebase_a);
        this.J = flag;
        this.K = flag1;
        this.v.clear();
        this.r = BiomeMesa.D;
        this.s = BiomeMesa.B;
        this.t.z = -999;
        this.t.D = 20;
        this.t.F = 3;
        this.t.G = 5;
        this.t.B = 0;
        this.v.clear();
        if (flag1) {
            this.t.z = 5;
        }

    }

    protected BiomeDecorator a() {
        return new BiomeMesa.a(null);
    }

    public WorldGenTreeAbstract a(Random random) {
        return BiomeMesa.n;
    }

    public void a(World world, Random random, ChunkSnapshot chunksnapshot, int i, int j, double d0) {
        if (this.E == null || this.F != world.getSeed()) {
            this.a(world.getSeed());
        }

        if (this.G == null || this.H == null || this.F != world.getSeed()) {
            Random random1 = new Random(this.F);

            this.G = new NoiseGenerator3(random1, 4);
            this.H = new NoiseGenerator3(random1, 1);
        }

        this.F = world.getSeed();
        double d1 = 0.0D;
        int k;
        int l;

        if (this.J) {
            k = (i & -16) + (j & 15);
            l = (j & -16) + (i & 15);
            double d2 = Math.min(Math.abs(d0), this.G.a((double) k * 0.25D, (double) l * 0.25D));

            if (d2 > 0.0D) {
                double d3 = 0.001953125D;
                double d4 = Math.abs(this.H.a((double) k * 0.001953125D, (double) l * 0.001953125D));

                d1 = d2 * d2 * 2.5D;
                double d5 = Math.ceil(d4 * 50.0D) + 14.0D;

                if (d1 > d5) {
                    d1 = d5;
                }

                d1 += 64.0D;
            }
        }

        k = i & 15;
        l = j & 15;
        int i1 = world.K();
        IBlockData iblockdata = BiomeMesa.B;
        IBlockData iblockdata1 = this.s;
        int j1 = (int) (d0 / 3.0D + 3.0D + random.nextDouble() * 0.25D);
        boolean flag = Math.cos(d0 / 3.0D * 3.141592653589793D) > 0.0D;
        int k1 = -1;
        boolean flag1 = false;
        int l1 = 0;

        for (int i2 = 255; i2 >= 0; --i2) {
            if (chunksnapshot.a(l, i2, k).getMaterial() == Material.AIR && i2 < (int) d1) {
                chunksnapshot.a(l, i2, k, BiomeMesa.a);
            }

            if (i2 <= random.nextInt(5)) {
                chunksnapshot.a(l, i2, k, BiomeMesa.c);
            } else if (l1 < 15 || this.J) {
                IBlockData iblockdata2 = chunksnapshot.a(l, i2, k);

                if (iblockdata2.getMaterial() == Material.AIR) {
                    k1 = -1;
                } else if (iblockdata2.getBlock() == Blocks.STONE) {
                    if (k1 == -1) {
                        flag1 = false;
                        if (j1 <= 0) {
                            iblockdata = BiomeMesa.b;
                            iblockdata1 = BiomeMesa.a;
                        } else if (i2 >= i1 - 4 && i2 <= i1 + 1) {
                            iblockdata = BiomeMesa.B;
                            iblockdata1 = this.s;
                        }

                        if (i2 < i1 && (iblockdata == null || iblockdata.getMaterial() == Material.AIR)) {
                            iblockdata = BiomeMesa.h;
                        }

                        k1 = j1 + Math.max(0, i2 - i1);
                        if (i2 >= i1 - 1) {
                            if (this.K && i2 > 86 + j1 * 2) {
                                if (flag) {
                                    chunksnapshot.a(l, i2, k, BiomeMesa.y);
                                } else {
                                    chunksnapshot.a(l, i2, k, BiomeMesa.z);
                                }
                            } else if (i2 > i1 + 3 + j1) {
                                IBlockData iblockdata3;

                                if (i2 >= 64 && i2 <= 127) {
                                    if (flag) {
                                        iblockdata3 = BiomeMesa.A;
                                    } else {
                                        iblockdata3 = this.a(i, i2, j);
                                    }
                                } else {
                                    iblockdata3 = BiomeMesa.C;
                                }

                                chunksnapshot.a(l, i2, k, iblockdata3);
                            } else {
                                chunksnapshot.a(l, i2, k, this.r);
                                flag1 = true;
                            }
                        } else {
                            chunksnapshot.a(l, i2, k, iblockdata1);
                            if (iblockdata1.getBlock() == Blocks.STAINED_HARDENED_CLAY) {
                                chunksnapshot.a(l, i2, k, BiomeMesa.C);
                            }
                        }
                    } else if (k1 > 0) {
                        --k1;
                        if (flag1) {
                            chunksnapshot.a(l, i2, k, BiomeMesa.C);
                        } else {
                            chunksnapshot.a(l, i2, k, this.a(i, i2, j));
                        }
                    }

                    ++l1;
                }
            }
        }

    }

    private void a(long i) {
        this.E = new IBlockData[64];
        Arrays.fill(this.E, BiomeMesa.A);
        Random random = new Random(i);

        this.I = new NoiseGenerator3(random, 1);

        int j;

        for (j = 0; j < 64; ++j) {
            j += random.nextInt(5) + 1;
            if (j < 64) {
                this.E[j] = BiomeMesa.C;
            }
        }

        j = random.nextInt(4) + 2;

        int k;
        int l;
        int i1;
        int j1;

        for (k = 0; k < j; ++k) {
            l = random.nextInt(3) + 1;
            i1 = random.nextInt(64);

            for (j1 = 0; i1 + j1 < 64 && j1 < l; ++j1) {
                this.E[i1 + j1] = BiomeMesa.B.set(BlockCloth.COLOR, EnumColor.YELLOW);
            }
        }

        k = random.nextInt(4) + 2;

        int k1;

        for (l = 0; l < k; ++l) {
            i1 = random.nextInt(3) + 2;
            j1 = random.nextInt(64);

            for (k1 = 0; j1 + k1 < 64 && k1 < i1; ++k1) {
                this.E[j1 + k1] = BiomeMesa.B.set(BlockCloth.COLOR, EnumColor.BROWN);
            }
        }

        l = random.nextInt(4) + 2;

        for (i1 = 0; i1 < l; ++i1) {
            j1 = random.nextInt(3) + 1;
            k1 = random.nextInt(64);

            for (int l1 = 0; k1 + l1 < 64 && l1 < j1; ++l1) {
                this.E[k1 + l1] = BiomeMesa.B.set(BlockCloth.COLOR, EnumColor.RED);
            }
        }

        i1 = random.nextInt(3) + 3;
        j1 = 0;

        for (k1 = 0; k1 < i1; ++k1) {
            boolean flag = true;

            j1 += random.nextInt(16) + 4;

            for (int i2 = 0; j1 + i2 < 64 && i2 < 1; ++i2) {
                this.E[j1 + i2] = BiomeMesa.B.set(BlockCloth.COLOR, EnumColor.WHITE);
                if (j1 + i2 > 1 && random.nextBoolean()) {
                    this.E[j1 + i2 - 1] = BiomeMesa.B.set(BlockCloth.COLOR, EnumColor.SILVER);
                }

                if (j1 + i2 < 63 && random.nextBoolean()) {
                    this.E[j1 + i2 + 1] = BiomeMesa.B.set(BlockCloth.COLOR, EnumColor.SILVER);
                }
            }
        }

    }

    private IBlockData a(int i, int j, int k) {
        int l = (int) Math.round(this.I.a((double) i / 512.0D, (double) i / 512.0D) * 2.0D);

        return this.E[(j + l + 64) % 64];
    }

    class a extends BiomeDecorator {

        private a() {}

        protected void a(World world, Random random) {
            super.a(world, random);
            this.a(world, random, 20, this.n, 32, 80);
        }

        a(Object object) {
            this();
        }
    }
}
