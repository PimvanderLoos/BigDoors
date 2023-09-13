package net.minecraft.server;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public class ChunkProviderHell implements ChunkGenerator {

    protected static final IBlockData a = Blocks.AIR.getBlockData();
    protected static final IBlockData b = Blocks.NETHERRACK.getBlockData();
    protected static final IBlockData c = Blocks.BEDROCK.getBlockData();
    protected static final IBlockData d = Blocks.LAVA.getBlockData();
    protected static final IBlockData e = Blocks.GRAVEL.getBlockData();
    protected static final IBlockData f = Blocks.SOUL_SAND.getBlockData();
    private final World n;
    private final boolean o;
    private final Random p;
    private double[] q = new double[256];
    private double[] r = new double[256];
    private double[] s = new double[256];
    private double[] t;
    private final NoiseGeneratorOctaves u;
    private final NoiseGeneratorOctaves v;
    private final NoiseGeneratorOctaves w;
    private final NoiseGeneratorOctaves x;
    private final NoiseGeneratorOctaves y;
    public final NoiseGeneratorOctaves g;
    public final NoiseGeneratorOctaves h;
    private final WorldGenFire z = new WorldGenFire();
    private final WorldGenLightStone1 A = new WorldGenLightStone1();
    private final WorldGenLightStone2 B = new WorldGenLightStone2();
    private final WorldGenerator C;
    private final WorldGenerator D;
    private final WorldGenHellLava E;
    private final WorldGenHellLava F;
    private final WorldGenMushrooms G;
    private final WorldGenMushrooms H;
    private final WorldGenNether I;
    private final WorldGenBase J;
    double[] i;
    double[] j;
    double[] k;
    double[] l;
    double[] m;

    public ChunkProviderHell(World world, boolean flag, long i) {
        this.C = new WorldGenMinable(Blocks.QUARTZ_ORE.getBlockData(), 14, BlockPredicate.a(Blocks.NETHERRACK));
        this.D = new WorldGenMinable(Blocks.df.getBlockData(), 33, BlockPredicate.a(Blocks.NETHERRACK));
        this.E = new WorldGenHellLava(Blocks.FLOWING_LAVA, true);
        this.F = new WorldGenHellLava(Blocks.FLOWING_LAVA, false);
        this.G = new WorldGenMushrooms(Blocks.BROWN_MUSHROOM);
        this.H = new WorldGenMushrooms(Blocks.RED_MUSHROOM);
        this.I = new WorldGenNether();
        this.J = new WorldGenCavesHell();
        this.n = world;
        this.o = flag;
        this.p = new Random(i);
        this.u = new NoiseGeneratorOctaves(this.p, 16);
        this.v = new NoiseGeneratorOctaves(this.p, 16);
        this.w = new NoiseGeneratorOctaves(this.p, 8);
        this.x = new NoiseGeneratorOctaves(this.p, 4);
        this.y = new NoiseGeneratorOctaves(this.p, 4);
        this.g = new NoiseGeneratorOctaves(this.p, 10);
        this.h = new NoiseGeneratorOctaves(this.p, 16);
        world.b(63);
    }

    public void a(int i, int j, ChunkSnapshot chunksnapshot) {
        boolean flag = true;
        int k = this.n.getSeaLevel() / 2 + 1;
        boolean flag1 = true;
        boolean flag2 = true;
        boolean flag3 = true;

        this.t = this.a(this.t, i * 4, 0, j * 4, 5, 17, 5);

        for (int l = 0; l < 4; ++l) {
            for (int i1 = 0; i1 < 4; ++i1) {
                for (int j1 = 0; j1 < 16; ++j1) {
                    double d0 = 0.125D;
                    double d1 = this.t[((l + 0) * 5 + i1 + 0) * 17 + j1 + 0];
                    double d2 = this.t[((l + 0) * 5 + i1 + 1) * 17 + j1 + 0];
                    double d3 = this.t[((l + 1) * 5 + i1 + 0) * 17 + j1 + 0];
                    double d4 = this.t[((l + 1) * 5 + i1 + 1) * 17 + j1 + 0];
                    double d5 = (this.t[((l + 0) * 5 + i1 + 0) * 17 + j1 + 1] - d1) * 0.125D;
                    double d6 = (this.t[((l + 0) * 5 + i1 + 1) * 17 + j1 + 1] - d2) * 0.125D;
                    double d7 = (this.t[((l + 1) * 5 + i1 + 0) * 17 + j1 + 1] - d3) * 0.125D;
                    double d8 = (this.t[((l + 1) * 5 + i1 + 1) * 17 + j1 + 1] - d4) * 0.125D;

                    for (int k1 = 0; k1 < 8; ++k1) {
                        double d9 = 0.25D;
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * 0.25D;
                        double d13 = (d4 - d2) * 0.25D;

                        for (int l1 = 0; l1 < 4; ++l1) {
                            double d14 = 0.25D;
                            double d15 = d10;
                            double d16 = (d11 - d10) * 0.25D;

                            for (int i2 = 0; i2 < 4; ++i2) {
                                IBlockData iblockdata = null;

                                if (j1 * 8 + k1 < k) {
                                    iblockdata = ChunkProviderHell.d;
                                }

                                if (d15 > 0.0D) {
                                    iblockdata = ChunkProviderHell.b;
                                }

                                int j2 = l1 + l * 4;
                                int k2 = k1 + j1 * 8;
                                int l2 = i2 + i1 * 4;

                                chunksnapshot.a(j2, k2, l2, iblockdata);
                                d15 += d16;
                            }

                            d10 += d12;
                            d11 += d13;
                        }

                        d1 += d5;
                        d2 += d6;
                        d3 += d7;
                        d4 += d8;
                    }
                }
            }
        }

    }

    public void b(int i, int j, ChunkSnapshot chunksnapshot) {
        int k = this.n.getSeaLevel() + 1;
        double d0 = 0.03125D;

        this.q = this.x.a(this.q, i * 16, j * 16, 0, 16, 16, 1, 0.03125D, 0.03125D, 1.0D);
        this.r = this.x.a(this.r, i * 16, 109, j * 16, 16, 1, 16, 0.03125D, 1.0D, 0.03125D);
        this.s = this.y.a(this.s, i * 16, j * 16, 0, 16, 16, 1, 0.0625D, 0.0625D, 0.0625D);

        for (int l = 0; l < 16; ++l) {
            for (int i1 = 0; i1 < 16; ++i1) {
                boolean flag = this.q[l + i1 * 16] + this.p.nextDouble() * 0.2D > 0.0D;
                boolean flag1 = this.r[l + i1 * 16] + this.p.nextDouble() * 0.2D > 0.0D;
                int j1 = (int) (this.s[l + i1 * 16] / 3.0D + 3.0D + this.p.nextDouble() * 0.25D);
                int k1 = -1;
                IBlockData iblockdata = ChunkProviderHell.b;
                IBlockData iblockdata1 = ChunkProviderHell.b;

                for (int l1 = 127; l1 >= 0; --l1) {
                    if (l1 < 127 - this.p.nextInt(5) && l1 > this.p.nextInt(5)) {
                        IBlockData iblockdata2 = chunksnapshot.a(i1, l1, l);

                        if (iblockdata2.getBlock() != null && iblockdata2.getMaterial() != Material.AIR) {
                            if (iblockdata2.getBlock() == Blocks.NETHERRACK) {
                                if (k1 == -1) {
                                    if (j1 <= 0) {
                                        iblockdata = ChunkProviderHell.a;
                                        iblockdata1 = ChunkProviderHell.b;
                                    } else if (l1 >= k - 4 && l1 <= k + 1) {
                                        iblockdata = ChunkProviderHell.b;
                                        iblockdata1 = ChunkProviderHell.b;
                                        if (flag1) {
                                            iblockdata = ChunkProviderHell.e;
                                            iblockdata1 = ChunkProviderHell.b;
                                        }

                                        if (flag) {
                                            iblockdata = ChunkProviderHell.f;
                                            iblockdata1 = ChunkProviderHell.f;
                                        }
                                    }

                                    if (l1 < k && (iblockdata == null || iblockdata.getMaterial() == Material.AIR)) {
                                        iblockdata = ChunkProviderHell.d;
                                    }

                                    k1 = j1;
                                    if (l1 >= k - 1) {
                                        chunksnapshot.a(i1, l1, l, iblockdata);
                                    } else {
                                        chunksnapshot.a(i1, l1, l, iblockdata1);
                                    }
                                } else if (k1 > 0) {
                                    --k1;
                                    chunksnapshot.a(i1, l1, l, iblockdata1);
                                }
                            }
                        } else {
                            k1 = -1;
                        }
                    } else {
                        chunksnapshot.a(i1, l1, l, ChunkProviderHell.c);
                    }
                }
            }
        }

    }

    public Chunk getOrCreateChunk(int i, int j) {
        this.p.setSeed((long) i * 341873128712L + (long) j * 132897987541L);
        ChunkSnapshot chunksnapshot = new ChunkSnapshot();

        this.a(i, j, chunksnapshot);
        this.b(i, j, chunksnapshot);
        this.J.a(this.n, i, j, chunksnapshot);
        if (this.o) {
            this.I.a(this.n, i, j, chunksnapshot);
        }

        Chunk chunk = new Chunk(this.n, chunksnapshot, i, j);
        BiomeBase[] abiomebase = this.n.getWorldChunkManager().getBiomeBlock((BiomeBase[]) null, i * 16, j * 16, 16, 16);
        byte[] abyte = chunk.getBiomeIndex();

        for (int k = 0; k < abyte.length; ++k) {
            abyte[k] = (byte) BiomeBase.a(abiomebase[k]);
        }

        chunk.m();
        return chunk;
    }

    private double[] a(double[] adouble, int i, int j, int k, int l, int i1, int j1) {
        if (adouble == null) {
            adouble = new double[l * i1 * j1];
        }

        double d0 = 684.412D;
        double d1 = 2053.236D;

        this.l = this.g.a(this.l, i, j, k, l, 1, j1, 1.0D, 0.0D, 1.0D);
        this.m = this.h.a(this.m, i, j, k, l, 1, j1, 100.0D, 0.0D, 100.0D);
        this.i = this.w.a(this.i, i, j, k, l, i1, j1, 8.555150000000001D, 34.2206D, 8.555150000000001D);
        this.j = this.u.a(this.j, i, j, k, l, i1, j1, 684.412D, 2053.236D, 684.412D);
        this.k = this.v.a(this.k, i, j, k, l, i1, j1, 684.412D, 2053.236D, 684.412D);
        int k1 = 0;
        double[] adouble1 = new double[i1];

        int l1;

        for (l1 = 0; l1 < i1; ++l1) {
            adouble1[l1] = Math.cos((double) l1 * 3.141592653589793D * 6.0D / (double) i1) * 2.0D;
            double d2 = (double) l1;

            if (l1 > i1 / 2) {
                d2 = (double) (i1 - 1 - l1);
            }

            if (d2 < 4.0D) {
                d2 = 4.0D - d2;
                adouble1[l1] -= d2 * d2 * d2 * 10.0D;
            }
        }

        for (l1 = 0; l1 < l; ++l1) {
            for (int i2 = 0; i2 < j1; ++i2) {
                double d3 = 0.0D;

                for (int j2 = 0; j2 < i1; ++j2) {
                    double d4 = adouble1[j2];
                    double d5 = this.j[k1] / 512.0D;
                    double d6 = this.k[k1] / 512.0D;
                    double d7 = (this.i[k1] / 10.0D + 1.0D) / 2.0D;
                    double d8;

                    if (d7 < 0.0D) {
                        d8 = d5;
                    } else if (d7 > 1.0D) {
                        d8 = d6;
                    } else {
                        d8 = d5 + (d6 - d5) * d7;
                    }

                    d8 -= d4;
                    double d9;

                    if (j2 > i1 - 4) {
                        d9 = (double) ((float) (j2 - (i1 - 4)) / 3.0F);
                        d8 = d8 * (1.0D - d9) + -10.0D * d9;
                    }

                    if ((double) j2 < 0.0D) {
                        d9 = (0.0D - (double) j2) / 4.0D;
                        d9 = MathHelper.a(d9, 0.0D, 1.0D);
                        d8 = d8 * (1.0D - d9) + -10.0D * d9;
                    }

                    adouble[k1] = d8;
                    ++k1;
                }
            }
        }

        return adouble;
    }

    public void recreateStructures(int i, int j) {
        BlockFalling.instaFall = true;
        int k = i * 16;
        int l = j * 16;
        BlockPosition blockposition = new BlockPosition(k, 0, l);
        BiomeBase biomebase = this.n.getBiome(blockposition.a(16, 0, 16));
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i, j);

        this.I.a(this.n, this.p, chunkcoordintpair);

        int i1;

        for (i1 = 0; i1 < 8; ++i1) {
            this.F.generate(this.n, this.p, blockposition.a(this.p.nextInt(16) + 8, this.p.nextInt(120) + 4, this.p.nextInt(16) + 8));
        }

        for (i1 = 0; i1 < this.p.nextInt(this.p.nextInt(10) + 1) + 1; ++i1) {
            this.z.generate(this.n, this.p, blockposition.a(this.p.nextInt(16) + 8, this.p.nextInt(120) + 4, this.p.nextInt(16) + 8));
        }

        for (i1 = 0; i1 < this.p.nextInt(this.p.nextInt(10) + 1); ++i1) {
            this.A.generate(this.n, this.p, blockposition.a(this.p.nextInt(16) + 8, this.p.nextInt(120) + 4, this.p.nextInt(16) + 8));
        }

        for (i1 = 0; i1 < 10; ++i1) {
            this.B.generate(this.n, this.p, blockposition.a(this.p.nextInt(16) + 8, this.p.nextInt(128), this.p.nextInt(16) + 8));
        }

        if (this.p.nextBoolean()) {
            this.G.generate(this.n, this.p, blockposition.a(this.p.nextInt(16) + 8, this.p.nextInt(128), this.p.nextInt(16) + 8));
        }

        if (this.p.nextBoolean()) {
            this.H.generate(this.n, this.p, blockposition.a(this.p.nextInt(16) + 8, this.p.nextInt(128), this.p.nextInt(16) + 8));
        }

        for (i1 = 0; i1 < 16; ++i1) {
            this.C.generate(this.n, this.p, blockposition.a(this.p.nextInt(16), this.p.nextInt(108) + 10, this.p.nextInt(16)));
        }

        i1 = this.n.getSeaLevel() / 2 + 1;

        int j1;

        for (j1 = 0; j1 < 4; ++j1) {
            this.D.generate(this.n, this.p, blockposition.a(this.p.nextInt(16), i1 - 5 + this.p.nextInt(10), this.p.nextInt(16)));
        }

        for (j1 = 0; j1 < 16; ++j1) {
            this.E.generate(this.n, this.p, blockposition.a(this.p.nextInt(16), this.p.nextInt(108) + 10, this.p.nextInt(16)));
        }

        biomebase.a(this.n, this.p, new BlockPosition(k, 0, l));
        BlockFalling.instaFall = false;
    }

    public boolean a(Chunk chunk, int i, int j) {
        return false;
    }

    public List<BiomeBase.BiomeMeta> getMobsFor(EnumCreatureType enumcreaturetype, BlockPosition blockposition) {
        if (enumcreaturetype == EnumCreatureType.MONSTER) {
            if (this.I.b(blockposition)) {
                return this.I.b();
            }

            if (this.I.a(this.n, blockposition) && this.n.getType(blockposition.down()).getBlock() == Blocks.NETHER_BRICK) {
                return this.I.b();
            }
        }

        BiomeBase biomebase = this.n.getBiome(blockposition);

        return biomebase.getMobs(enumcreaturetype);
    }

    @Nullable
    public BlockPosition findNearestMapFeature(World world, String s, BlockPosition blockposition, boolean flag) {
        return "Fortress".equals(s) && this.I != null ? this.I.getNearestGeneratedFeature(world, blockposition, flag) : null;
    }

    public boolean a(World world, String s, BlockPosition blockposition) {
        return "Fortress".equals(s) && this.I != null ? this.I.b(blockposition) : false;
    }

    public void recreateStructures(Chunk chunk, int i, int j) {
        this.I.a(this.n, i, j, (ChunkSnapshot) null);
    }
}
