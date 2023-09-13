package net.minecraft.server;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public class ChunkProviderGenerate implements ChunkGenerator {

    protected static final IBlockData a = Blocks.STONE.getBlockData();
    private final Random i;
    private final NoiseGeneratorOctaves j;
    private final NoiseGeneratorOctaves k;
    private final NoiseGeneratorOctaves l;
    private final NoiseGenerator3 m;
    public NoiseGeneratorOctaves b;
    public NoiseGeneratorOctaves c;
    public NoiseGeneratorOctaves d;
    private final World n;
    private final boolean o;
    private final WorldType p;
    private final double[] q;
    private final float[] r;
    private CustomWorldSettingsFinal s;
    private IBlockData t;
    private double[] u;
    private final WorldGenBase v;
    private final WorldGenStronghold w;
    private final WorldGenVillage x;
    private final WorldGenMineshaft y;
    private final WorldGenLargeFeature z;
    private final WorldGenBase A;
    private final WorldGenMonument B;
    private final WorldGenWoodlandMansion C;
    private BiomeBase[] D;
    double[] e;
    double[] f;
    double[] g;
    double[] h;

    public ChunkProviderGenerate(World world, long i, boolean flag, String s) {
        this.t = Blocks.WATER.getBlockData();
        this.u = new double[256];
        this.v = new WorldGenCaves();
        this.w = new WorldGenStronghold();
        this.x = new WorldGenVillage();
        this.y = new WorldGenMineshaft();
        this.z = new WorldGenLargeFeature();
        this.A = new WorldGenCanyon();
        this.B = new WorldGenMonument();
        this.C = new WorldGenWoodlandMansion(this);
        this.n = world;
        this.o = flag;
        this.p = world.getWorldData().getType();
        this.i = new Random(i);
        this.j = new NoiseGeneratorOctaves(this.i, 16);
        this.k = new NoiseGeneratorOctaves(this.i, 16);
        this.l = new NoiseGeneratorOctaves(this.i, 8);
        this.m = new NoiseGenerator3(this.i, 4);
        this.b = new NoiseGeneratorOctaves(this.i, 10);
        this.c = new NoiseGeneratorOctaves(this.i, 16);
        this.d = new NoiseGeneratorOctaves(this.i, 8);
        this.q = new double[825];
        this.r = new float[25];

        for (int j = -2; j <= 2; ++j) {
            for (int k = -2; k <= 2; ++k) {
                float f = 10.0F / MathHelper.c((float) (j * j + k * k) + 0.2F);

                this.r[j + 2 + (k + 2) * 5] = f;
            }
        }

        if (s != null) {
            this.s = CustomWorldSettingsFinal.CustomWorldSettings.a(s).b();
            this.t = this.s.F ? Blocks.LAVA.getBlockData() : Blocks.WATER.getBlockData();
            world.b(this.s.q);
        }

    }

    public void a(int i, int j, ChunkSnapshot chunksnapshot) {
        this.D = this.n.getWorldChunkManager().getBiomes(this.D, i * 4 - 2, j * 4 - 2, 10, 10);
        this.a(i * 4, 0, j * 4);

        for (int k = 0; k < 4; ++k) {
            int l = k * 5;
            int i1 = (k + 1) * 5;

            for (int j1 = 0; j1 < 4; ++j1) {
                int k1 = (l + j1) * 33;
                int l1 = (l + j1 + 1) * 33;
                int i2 = (i1 + j1) * 33;
                int j2 = (i1 + j1 + 1) * 33;

                for (int k2 = 0; k2 < 32; ++k2) {
                    double d0 = 0.125D;
                    double d1 = this.q[k1 + k2];
                    double d2 = this.q[l1 + k2];
                    double d3 = this.q[i2 + k2];
                    double d4 = this.q[j2 + k2];
                    double d5 = (this.q[k1 + k2 + 1] - d1) * 0.125D;
                    double d6 = (this.q[l1 + k2 + 1] - d2) * 0.125D;
                    double d7 = (this.q[i2 + k2 + 1] - d3) * 0.125D;
                    double d8 = (this.q[j2 + k2 + 1] - d4) * 0.125D;

                    for (int l2 = 0; l2 < 8; ++l2) {
                        double d9 = 0.25D;
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * 0.25D;
                        double d13 = (d4 - d2) * 0.25D;

                        for (int i3 = 0; i3 < 4; ++i3) {
                            double d14 = 0.25D;
                            double d15 = (d11 - d10) * 0.25D;
                            double d16 = d10 - d15;

                            for (int j3 = 0; j3 < 4; ++j3) {
                                if ((d16 += d15) > 0.0D) {
                                    chunksnapshot.a(k * 4 + i3, k2 * 8 + l2, j1 * 4 + j3, ChunkProviderGenerate.a);
                                } else if (k2 * 8 + l2 < this.s.q) {
                                    chunksnapshot.a(k * 4 + i3, k2 * 8 + l2, j1 * 4 + j3, this.t);
                                }
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

    public void a(int i, int j, ChunkSnapshot chunksnapshot, BiomeBase[] abiomebase) {
        double d0 = 0.03125D;

        this.u = this.m.a(this.u, (double) (i * 16), (double) (j * 16), 16, 16, 0.0625D, 0.0625D, 1.0D);

        for (int k = 0; k < 16; ++k) {
            for (int l = 0; l < 16; ++l) {
                BiomeBase biomebase = abiomebase[l + k * 16];

                biomebase.a(this.n, this.i, chunksnapshot, i * 16 + k, j * 16 + l, this.u[l + k * 16]);
            }
        }

    }

    public Chunk getOrCreateChunk(int i, int j) {
        this.i.setSeed((long) i * 341873128712L + (long) j * 132897987541L);
        ChunkSnapshot chunksnapshot = new ChunkSnapshot();

        this.a(i, j, chunksnapshot);
        this.D = this.n.getWorldChunkManager().getBiomeBlock(this.D, i * 16, j * 16, 16, 16);
        this.a(i, j, chunksnapshot, this.D);
        if (this.s.r) {
            this.v.a(this.n, i, j, chunksnapshot);
        }

        if (this.s.A) {
            this.A.a(this.n, i, j, chunksnapshot);
        }

        if (this.o) {
            if (this.s.w) {
                this.y.a(this.n, i, j, chunksnapshot);
            }

            if (this.s.v) {
                this.x.a(this.n, i, j, chunksnapshot);
            }

            if (this.s.u) {
                this.w.a(this.n, i, j, chunksnapshot);
            }

            if (this.s.x) {
                this.z.a(this.n, i, j, chunksnapshot);
            }

            if (this.s.y) {
                this.B.a(this.n, i, j, chunksnapshot);
            }

            if (this.s.z) {
                this.C.a(this.n, i, j, chunksnapshot);
            }
        }

        Chunk chunk = new Chunk(this.n, chunksnapshot, i, j);
        byte[] abyte = chunk.getBiomeIndex();

        for (int k = 0; k < abyte.length; ++k) {
            abyte[k] = (byte) BiomeBase.a(this.D[k]);
        }

        chunk.initLighting();
        return chunk;
    }

    private void a(int i, int j, int k) {
        this.h = this.c.a(this.h, i, k, 5, 5, (double) this.s.e, (double) this.s.f, (double) this.s.g);
        float f = this.s.a;
        float f1 = this.s.b;

        this.e = this.l.a(this.e, i, j, k, 5, 33, 5, (double) (f / this.s.h), (double) (f1 / this.s.i), (double) (f / this.s.j));
        this.f = this.j.a(this.f, i, j, k, 5, 33, 5, (double) f, (double) f1, (double) f);
        this.g = this.k.a(this.g, i, j, k, 5, 33, 5, (double) f, (double) f1, (double) f);
        int l = 0;
        int i1 = 0;

        for (int j1 = 0; j1 < 5; ++j1) {
            for (int k1 = 0; k1 < 5; ++k1) {
                float f2 = 0.0F;
                float f3 = 0.0F;
                float f4 = 0.0F;
                boolean flag = true;
                BiomeBase biomebase = this.D[j1 + 2 + (k1 + 2) * 10];

                for (int l1 = -2; l1 <= 2; ++l1) {
                    for (int i2 = -2; i2 <= 2; ++i2) {
                        BiomeBase biomebase1 = this.D[j1 + l1 + 2 + (k1 + i2 + 2) * 10];
                        float f5 = this.s.n + biomebase1.j() * this.s.m;
                        float f6 = this.s.p + biomebase1.m() * this.s.o;

                        if (this.p == WorldType.AMPLIFIED && f5 > 0.0F) {
                            f5 = 1.0F + f5 * 2.0F;
                            f6 = 1.0F + f6 * 4.0F;
                        }

                        float f7 = this.r[l1 + 2 + (i2 + 2) * 5] / (f5 + 2.0F);

                        if (biomebase1.j() > biomebase.j()) {
                            f7 /= 2.0F;
                        }

                        f2 += f6 * f7;
                        f3 += f5 * f7;
                        f4 += f7;
                    }
                }

                f2 /= f4;
                f3 /= f4;
                f2 = f2 * 0.9F + 0.1F;
                f3 = (f3 * 4.0F - 1.0F) / 8.0F;
                double d0 = this.h[i1] / 8000.0D;

                if (d0 < 0.0D) {
                    d0 = -d0 * 0.3D;
                }

                d0 = d0 * 3.0D - 2.0D;
                if (d0 < 0.0D) {
                    d0 /= 2.0D;
                    if (d0 < -1.0D) {
                        d0 = -1.0D;
                    }

                    d0 /= 1.4D;
                    d0 /= 2.0D;
                } else {
                    if (d0 > 1.0D) {
                        d0 = 1.0D;
                    }

                    d0 /= 8.0D;
                }

                ++i1;
                double d1 = (double) f3;
                double d2 = (double) f2;

                d1 += d0 * 0.2D;
                d1 = d1 * (double) this.s.k / 8.0D;
                double d3 = (double) this.s.k + d1 * 4.0D;

                for (int j2 = 0; j2 < 33; ++j2) {
                    double d4 = ((double) j2 - d3) * (double) this.s.l * 128.0D / 256.0D / d2;

                    if (d4 < 0.0D) {
                        d4 *= 4.0D;
                    }

                    double d5 = this.f[l] / (double) this.s.d;
                    double d6 = this.g[l] / (double) this.s.c;
                    double d7 = (this.e[l] / 10.0D + 1.0D) / 2.0D;
                    double d8 = MathHelper.b(d5, d6, d7) - d4;

                    if (j2 > 29) {
                        double d9 = (double) ((float) (j2 - 29) / 3.0F);

                        d8 = d8 * (1.0D - d9) + -10.0D * d9;
                    }

                    this.q[l] = d8;
                    ++l;
                }
            }
        }

    }

    public void recreateStructures(int i, int j) {
        BlockFalling.instaFall = true;
        int k = i * 16;
        int l = j * 16;
        BlockPosition blockposition = new BlockPosition(k, 0, l);
        BiomeBase biomebase = this.n.getBiome(blockposition.a(16, 0, 16));

        this.i.setSeed(this.n.getSeed());
        long i1 = this.i.nextLong() / 2L * 2L + 1L;
        long j1 = this.i.nextLong() / 2L * 2L + 1L;

        this.i.setSeed((long) i * i1 + (long) j * j1 ^ this.n.getSeed());
        boolean flag = false;
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i, j);

        if (this.o) {
            if (this.s.w) {
                this.y.a(this.n, this.i, chunkcoordintpair);
            }

            if (this.s.v) {
                flag = this.x.a(this.n, this.i, chunkcoordintpair);
            }

            if (this.s.u) {
                this.w.a(this.n, this.i, chunkcoordintpair);
            }

            if (this.s.x) {
                this.z.a(this.n, this.i, chunkcoordintpair);
            }

            if (this.s.y) {
                this.B.a(this.n, this.i, chunkcoordintpair);
            }

            if (this.s.z) {
                this.C.a(this.n, this.i, chunkcoordintpair);
            }
        }

        int k1;
        int l1;
        int i2;

        if (biomebase != Biomes.d && biomebase != Biomes.s && this.s.B && !flag && this.i.nextInt(this.s.C) == 0) {
            k1 = this.i.nextInt(16) + 8;
            l1 = this.i.nextInt(256);
            i2 = this.i.nextInt(16) + 8;
            (new WorldGenLakes(Blocks.WATER)).generate(this.n, this.i, blockposition.a(k1, l1, i2));
        }

        if (!flag && this.i.nextInt(this.s.E / 10) == 0 && this.s.D) {
            k1 = this.i.nextInt(16) + 8;
            l1 = this.i.nextInt(this.i.nextInt(248) + 8);
            i2 = this.i.nextInt(16) + 8;
            if (l1 < this.n.getSeaLevel() || this.i.nextInt(this.s.E / 8) == 0) {
                (new WorldGenLakes(Blocks.LAVA)).generate(this.n, this.i, blockposition.a(k1, l1, i2));
            }
        }

        if (this.s.s) {
            for (k1 = 0; k1 < this.s.t; ++k1) {
                l1 = this.i.nextInt(16) + 8;
                i2 = this.i.nextInt(256);
                int j2 = this.i.nextInt(16) + 8;

                (new WorldGenDungeons()).generate(this.n, this.i, blockposition.a(l1, i2, j2));
            }
        }

        biomebase.a(this.n, this.i, new BlockPosition(k, 0, l));
        SpawnerCreature.a(this.n, biomebase, k + 8, l + 8, 16, 16, this.i);
        blockposition = blockposition.a(8, 0, 8);

        for (k1 = 0; k1 < 16; ++k1) {
            for (l1 = 0; l1 < 16; ++l1) {
                BlockPosition blockposition1 = this.n.p(blockposition.a(k1, 0, l1));
                BlockPosition blockposition2 = blockposition1.down();

                if (this.n.u(blockposition2)) {
                    this.n.setTypeAndData(blockposition2, Blocks.ICE.getBlockData(), 2);
                }

                if (this.n.f(blockposition1, true)) {
                    this.n.setTypeAndData(blockposition1, Blocks.SNOW_LAYER.getBlockData(), 2);
                }
            }
        }

        BlockFalling.instaFall = false;
    }

    public boolean a(Chunk chunk, int i, int j) {
        boolean flag = false;

        if (this.s.y && this.o && chunk.x() < 3600L) {
            flag |= this.B.a(this.n, this.i, new ChunkCoordIntPair(i, j));
        }

        return flag;
    }

    public List<BiomeBase.BiomeMeta> getMobsFor(EnumCreatureType enumcreaturetype, BlockPosition blockposition) {
        BiomeBase biomebase = this.n.getBiome(blockposition);

        if (this.o) {
            if (enumcreaturetype == EnumCreatureType.MONSTER && this.z.a(blockposition)) {
                return this.z.b();
            }

            if (enumcreaturetype == EnumCreatureType.MONSTER && this.s.y && this.B.a(this.n, blockposition)) {
                return this.B.b();
            }
        }

        return biomebase.getMobs(enumcreaturetype);
    }

    public boolean a(World world, String s, BlockPosition blockposition) {
        return !this.o ? false : ("Stronghold".equals(s) && this.w != null ? this.w.b(blockposition) : ("Mansion".equals(s) && this.C != null ? this.C.b(blockposition) : ("Monument".equals(s) && this.B != null ? this.B.b(blockposition) : ("Village".equals(s) && this.x != null ? this.x.b(blockposition) : ("Mineshaft".equals(s) && this.y != null ? this.y.b(blockposition) : ("Temple".equals(s) && this.z != null ? this.z.b(blockposition) : false))))));
    }

    @Nullable
    public BlockPosition findNearestMapFeature(World world, String s, BlockPosition blockposition, boolean flag) {
        return !this.o ? null : ("Stronghold".equals(s) && this.w != null ? this.w.getNearestGeneratedFeature(world, blockposition, flag) : ("Mansion".equals(s) && this.C != null ? this.C.getNearestGeneratedFeature(world, blockposition, flag) : ("Monument".equals(s) && this.B != null ? this.B.getNearestGeneratedFeature(world, blockposition, flag) : ("Village".equals(s) && this.x != null ? this.x.getNearestGeneratedFeature(world, blockposition, flag) : ("Mineshaft".equals(s) && this.y != null ? this.y.getNearestGeneratedFeature(world, blockposition, flag) : ("Temple".equals(s) && this.z != null ? this.z.getNearestGeneratedFeature(world, blockposition, flag) : null))))));
    }

    public void recreateStructures(Chunk chunk, int i, int j) {
        if (this.o) {
            if (this.s.w) {
                this.y.a(this.n, i, j, (ChunkSnapshot) null);
            }

            if (this.s.v) {
                this.x.a(this.n, i, j, (ChunkSnapshot) null);
            }

            if (this.s.u) {
                this.w.a(this.n, i, j, (ChunkSnapshot) null);
            }

            if (this.s.x) {
                this.z.a(this.n, i, j, (ChunkSnapshot) null);
            }

            if (this.s.y) {
                this.B.a(this.n, i, j, (ChunkSnapshot) null);
            }

            if (this.s.z) {
                this.C.a(this.n, i, j, (ChunkSnapshot) null);
            }
        }

    }
}
