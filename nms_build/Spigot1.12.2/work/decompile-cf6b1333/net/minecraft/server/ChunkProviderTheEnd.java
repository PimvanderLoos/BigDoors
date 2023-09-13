package net.minecraft.server;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public class ChunkProviderTheEnd implements ChunkGenerator {

    private final Random h;
    protected static final IBlockData a = Blocks.END_STONE.getBlockData();
    protected static final IBlockData b = Blocks.AIR.getBlockData();
    private final NoiseGeneratorOctaves i;
    private final NoiseGeneratorOctaves j;
    private final NoiseGeneratorOctaves k;
    public NoiseGeneratorOctaves c;
    public NoiseGeneratorOctaves d;
    private final World l;
    private final boolean m;
    private final BlockPosition n;
    private final WorldGenEndCity o = new WorldGenEndCity(this);
    private final NoiseGenerator3Handler p;
    private double[] q;
    private BiomeBase[] r;
    double[] e;
    double[] f;
    double[] g;
    private final WorldGenEndIsland s = new WorldGenEndIsland();

    public ChunkProviderTheEnd(World world, boolean flag, long i, BlockPosition blockposition) {
        this.l = world;
        this.m = flag;
        this.n = blockposition;
        this.h = new Random(i);
        this.i = new NoiseGeneratorOctaves(this.h, 16);
        this.j = new NoiseGeneratorOctaves(this.h, 16);
        this.k = new NoiseGeneratorOctaves(this.h, 8);
        this.c = new NoiseGeneratorOctaves(this.h, 10);
        this.d = new NoiseGeneratorOctaves(this.h, 16);
        this.p = new NoiseGenerator3Handler(this.h);
    }

    public void a(int i, int j, ChunkSnapshot chunksnapshot) {
        boolean flag = true;
        boolean flag1 = true;
        boolean flag2 = true;
        boolean flag3 = true;

        this.q = this.a(this.q, i * 2, 0, j * 2, 3, 33, 3);

        for (int k = 0; k < 2; ++k) {
            for (int l = 0; l < 2; ++l) {
                for (int i1 = 0; i1 < 32; ++i1) {
                    double d0 = 0.25D;
                    double d1 = this.q[((k + 0) * 3 + l + 0) * 33 + i1 + 0];
                    double d2 = this.q[((k + 0) * 3 + l + 1) * 33 + i1 + 0];
                    double d3 = this.q[((k + 1) * 3 + l + 0) * 33 + i1 + 0];
                    double d4 = this.q[((k + 1) * 3 + l + 1) * 33 + i1 + 0];
                    double d5 = (this.q[((k + 0) * 3 + l + 0) * 33 + i1 + 1] - d1) * 0.25D;
                    double d6 = (this.q[((k + 0) * 3 + l + 1) * 33 + i1 + 1] - d2) * 0.25D;
                    double d7 = (this.q[((k + 1) * 3 + l + 0) * 33 + i1 + 1] - d3) * 0.25D;
                    double d8 = (this.q[((k + 1) * 3 + l + 1) * 33 + i1 + 1] - d4) * 0.25D;

                    for (int j1 = 0; j1 < 4; ++j1) {
                        double d9 = 0.125D;
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * 0.125D;
                        double d13 = (d4 - d2) * 0.125D;

                        for (int k1 = 0; k1 < 8; ++k1) {
                            double d14 = 0.125D;
                            double d15 = d10;
                            double d16 = (d11 - d10) * 0.125D;

                            for (int l1 = 0; l1 < 8; ++l1) {
                                IBlockData iblockdata = ChunkProviderTheEnd.b;

                                if (d15 > 0.0D) {
                                    iblockdata = ChunkProviderTheEnd.a;
                                }

                                int i2 = k1 + k * 8;
                                int j2 = j1 + i1 * 4;
                                int k2 = l1 + l * 8;

                                chunksnapshot.a(i2, j2, k2, iblockdata);
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

    public void a(ChunkSnapshot chunksnapshot) {
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                boolean flag = true;
                int k = -1;
                IBlockData iblockdata = ChunkProviderTheEnd.a;
                IBlockData iblockdata1 = ChunkProviderTheEnd.a;

                for (int l = 127; l >= 0; --l) {
                    IBlockData iblockdata2 = chunksnapshot.a(i, l, j);

                    if (iblockdata2.getMaterial() == Material.AIR) {
                        k = -1;
                    } else if (iblockdata2.getBlock() == Blocks.STONE) {
                        if (k == -1) {
                            k = 1;
                            if (l >= 0) {
                                chunksnapshot.a(i, l, j, iblockdata);
                            } else {
                                chunksnapshot.a(i, l, j, iblockdata1);
                            }
                        } else if (k > 0) {
                            --k;
                            chunksnapshot.a(i, l, j, iblockdata1);
                        }
                    }
                }
            }
        }

    }

    public Chunk getOrCreateChunk(int i, int j) {
        this.h.setSeed((long) i * 341873128712L + (long) j * 132897987541L);
        ChunkSnapshot chunksnapshot = new ChunkSnapshot();

        this.r = this.l.getWorldChunkManager().getBiomeBlock(this.r, i * 16, j * 16, 16, 16);
        this.a(i, j, chunksnapshot);
        this.a(chunksnapshot);
        if (this.m) {
            this.o.a(this.l, i, j, chunksnapshot);
        }

        Chunk chunk = new Chunk(this.l, chunksnapshot, i, j);
        byte[] abyte = chunk.getBiomeIndex();

        for (int k = 0; k < abyte.length; ++k) {
            abyte[k] = (byte) BiomeBase.a(this.r[k]);
        }

        chunk.initLighting();
        return chunk;
    }

    private float a(int i, int j, int k, int l) {
        float f = (float) (i * 2 + k);
        float f1 = (float) (j * 2 + l);
        float f2 = 100.0F - MathHelper.c(f * f + f1 * f1) * 8.0F;

        if (f2 > 80.0F) {
            f2 = 80.0F;
        }

        if (f2 < -100.0F) {
            f2 = -100.0F;
        }

        for (int i1 = -12; i1 <= 12; ++i1) {
            for (int j1 = -12; j1 <= 12; ++j1) {
                long k1 = (long) (i + i1);
                long l1 = (long) (j + j1);

                if (k1 * k1 + l1 * l1 > 4096L && this.p.a((double) k1, (double) l1) < -0.8999999761581421D) {
                    float f3 = (MathHelper.e((float) k1) * 3439.0F + MathHelper.e((float) l1) * 147.0F) % 13.0F + 9.0F;

                    f = (float) (k - i1 * 2);
                    f1 = (float) (l - j1 * 2);
                    float f4 = 100.0F - MathHelper.c(f * f + f1 * f1) * f3;

                    if (f4 > 80.0F) {
                        f4 = 80.0F;
                    }

                    if (f4 < -100.0F) {
                        f4 = -100.0F;
                    }

                    if (f4 > f2) {
                        f2 = f4;
                    }
                }
            }
        }

        return f2;
    }

    public boolean c(int i, int j) {
        return (long) i * (long) i + (long) j * (long) j > 4096L && this.a(i, j, 1, 1) >= 0.0F;
    }

    private double[] a(double[] adouble, int i, int j, int k, int l, int i1, int j1) {
        if (adouble == null) {
            adouble = new double[l * i1 * j1];
        }

        double d0 = 684.412D;
        double d1 = 684.412D;

        d0 *= 2.0D;
        this.e = this.k.a(this.e, i, j, k, l, i1, j1, d0 / 80.0D, 4.277575000000001D, d0 / 80.0D);
        this.f = this.i.a(this.f, i, j, k, l, i1, j1, d0, 684.412D, d0);
        this.g = this.j.a(this.g, i, j, k, l, i1, j1, d0, 684.412D, d0);
        int k1 = i / 2;
        int l1 = k / 2;
        int i2 = 0;

        for (int j2 = 0; j2 < l; ++j2) {
            for (int k2 = 0; k2 < j1; ++k2) {
                float f = this.a(k1, l1, j2, k2);

                for (int l2 = 0; l2 < i1; ++l2) {
                    double d2 = this.f[i2] / 512.0D;
                    double d3 = this.g[i2] / 512.0D;
                    double d4 = (this.e[i2] / 10.0D + 1.0D) / 2.0D;
                    double d5;

                    if (d4 < 0.0D) {
                        d5 = d2;
                    } else if (d4 > 1.0D) {
                        d5 = d3;
                    } else {
                        d5 = d2 + (d3 - d2) * d4;
                    }

                    d5 -= 8.0D;
                    d5 += (double) f;
                    byte b0 = 2;
                    double d6;

                    if (l2 > i1 / 2 - b0) {
                        d6 = (double) ((float) (l2 - (i1 / 2 - b0)) / 64.0F);
                        d6 = MathHelper.a(d6, 0.0D, 1.0D);
                        d5 = d5 * (1.0D - d6) + -3000.0D * d6;
                    }

                    b0 = 8;
                    if (l2 < b0) {
                        d6 = (double) ((float) (b0 - l2) / ((float) b0 - 1.0F));
                        d5 = d5 * (1.0D - d6) + -30.0D * d6;
                    }

                    adouble[i2] = d5;
                    ++i2;
                }
            }
        }

        return adouble;
    }

    public void recreateStructures(int i, int j) {
        BlockFalling.instaFall = true;
        BlockPosition blockposition = new BlockPosition(i * 16, 0, j * 16);

        if (this.m) {
            this.o.a(this.l, this.h, new ChunkCoordIntPair(i, j));
        }

        this.l.getBiome(blockposition.a(16, 0, 16)).a(this.l, this.l.random, blockposition);
        long k = (long) i * (long) i + (long) j * (long) j;

        if (k > 4096L) {
            float f = this.a(i, j, 1, 1);

            if (f < -20.0F && this.h.nextInt(14) == 0) {
                this.s.generate(this.l, this.h, blockposition.a(this.h.nextInt(16) + 8, 55 + this.h.nextInt(16), this.h.nextInt(16) + 8));
                if (this.h.nextInt(4) == 0) {
                    this.s.generate(this.l, this.h, blockposition.a(this.h.nextInt(16) + 8, 55 + this.h.nextInt(16), this.h.nextInt(16) + 8));
                }
            }

            if (this.a(i, j, 1, 1) > 40.0F) {
                int l = this.h.nextInt(5);

                int i1;
                int j1;
                int k1;
                int l1;

                for (i1 = 0; i1 < l; ++i1) {
                    j1 = this.h.nextInt(16) + 8;
                    k1 = this.h.nextInt(16) + 8;
                    l1 = this.l.getHighestBlockYAt(blockposition.a(j1, 0, k1)).getY();
                    if (l1 > 0) {
                        int i2 = l1 - 1;

                        if (this.l.isEmpty(blockposition.a(j1, i2 + 1, k1)) && this.l.getType(blockposition.a(j1, i2, k1)).getBlock() == Blocks.END_STONE) {
                            BlockChorusFlower.a(this.l, blockposition.a(j1, i2 + 1, k1), this.h, 8);
                        }
                    }
                }

                if (this.h.nextInt(700) == 0) {
                    i1 = this.h.nextInt(16) + 8;
                    j1 = this.h.nextInt(16) + 8;
                    k1 = this.l.getHighestBlockYAt(blockposition.a(i1, 0, j1)).getY();
                    if (k1 > 0) {
                        l1 = k1 + 3 + this.h.nextInt(7);
                        BlockPosition blockposition1 = blockposition.a(i1, l1, j1);

                        (new WorldGenEndGateway()).generate(this.l, this.h, blockposition1);
                        TileEntity tileentity = this.l.getTileEntity(blockposition1);

                        if (tileentity instanceof TileEntityEndGateway) {
                            TileEntityEndGateway tileentityendgateway = (TileEntityEndGateway) tileentity;

                            tileentityendgateway.b(this.n);
                        }
                    }
                }
            }
        }

        BlockFalling.instaFall = false;
    }

    public boolean a(Chunk chunk, int i, int j) {
        return false;
    }

    public List<BiomeBase.BiomeMeta> getMobsFor(EnumCreatureType enumcreaturetype, BlockPosition blockposition) {
        return this.l.getBiome(blockposition).getMobs(enumcreaturetype);
    }

    @Nullable
    public BlockPosition findNearestMapFeature(World world, String s, BlockPosition blockposition, boolean flag) {
        return "EndCity".equals(s) && this.o != null ? this.o.getNearestGeneratedFeature(world, blockposition, flag) : null;
    }

    public boolean a(World world, String s, BlockPosition blockposition) {
        return "EndCity".equals(s) && this.o != null ? this.o.b(blockposition) : false;
    }

    public void recreateStructures(Chunk chunk, int i, int j) {}
}
