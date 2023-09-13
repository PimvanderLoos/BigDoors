package net.minecraft.server;

import java.util.List;

public class ChunkProviderTheEnd extends ChunkGeneratorAbstract<GeneratorSettingsEnd> {

    protected static final IBlockData f = Blocks.AIR.getBlockData();
    private final NoiseGeneratorOctaves g;
    private final NoiseGeneratorOctaves h;
    private final NoiseGeneratorOctaves i;
    private final NoiseGeneratorOctaves j;
    private final NoiseGeneratorOctaves k;
    private final NoiseGenerator3 l;
    private final BlockPosition m;
    private final GeneratorSettingsEnd n;
    private final IBlockData o;
    private final IBlockData p;

    public ChunkProviderTheEnd(GeneratorAccess generatoraccess, WorldChunkManager worldchunkmanager, GeneratorSettingsEnd generatorsettingsend) {
        super(generatoraccess, worldchunkmanager);
        this.n = generatorsettingsend;
        this.o = this.n.r();
        this.p = this.n.s();
        this.m = generatorsettingsend.t();
        SeededRandom seededrandom = new SeededRandom(this.b);

        this.g = new NoiseGeneratorOctaves(seededrandom, 16);
        this.h = new NoiseGeneratorOctaves(seededrandom, 16);
        this.i = new NoiseGeneratorOctaves(seededrandom, 8);
        this.j = new NoiseGeneratorOctaves(seededrandom, 10);
        this.k = new NoiseGeneratorOctaves(seededrandom, 16);
        seededrandom.a(262);
        this.l = new NoiseGenerator3(new SeededRandom(this.b), 4);
    }

    public void a(int i, int j, IChunkAccess ichunkaccess) {
        boolean flag = true;
        boolean flag1 = true;
        boolean flag2 = true;
        boolean flag3 = true;
        double[] adouble = this.a(i * 2, 0, j * 2, 3, 33, 3);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int k = 0; k < 2; ++k) {
            for (int l = 0; l < 2; ++l) {
                for (int i1 = 0; i1 < 32; ++i1) {
                    double d0 = 0.25D;
                    double d1 = adouble[((k + 0) * 3 + l + 0) * 33 + i1 + 0];
                    double d2 = adouble[((k + 0) * 3 + l + 1) * 33 + i1 + 0];
                    double d3 = adouble[((k + 1) * 3 + l + 0) * 33 + i1 + 0];
                    double d4 = adouble[((k + 1) * 3 + l + 1) * 33 + i1 + 0];
                    double d5 = (adouble[((k + 0) * 3 + l + 0) * 33 + i1 + 1] - d1) * 0.25D;
                    double d6 = (adouble[((k + 0) * 3 + l + 1) * 33 + i1 + 1] - d2) * 0.25D;
                    double d7 = (adouble[((k + 1) * 3 + l + 0) * 33 + i1 + 1] - d3) * 0.25D;
                    double d8 = (adouble[((k + 1) * 3 + l + 1) * 33 + i1 + 1] - d4) * 0.25D;

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
                                IBlockData iblockdata = ChunkProviderTheEnd.f;

                                if (d15 > 0.0D) {
                                    iblockdata = this.o;
                                }

                                int i2 = k1 + k * 8;
                                int j2 = j1 + i1 * 4;
                                int k2 = l1 + l * 8;

                                ichunkaccess.setType(blockposition_mutableblockposition.c(i2, j2, k2), iblockdata, false);
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

    public void createChunk(IChunkAccess ichunkaccess) {
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        int i = chunkcoordintpair.x;
        int j = chunkcoordintpair.z;
        SeededRandom seededrandom = new SeededRandom();

        seededrandom.a(i, j);
        BiomeBase[] abiomebase = this.c.getBiomeBlock(i * 16, j * 16, 16, 16);

        ichunkaccess.a(abiomebase);
        this.a(i, j, ichunkaccess);
        this.a(ichunkaccess, abiomebase, seededrandom, 0);
        ichunkaccess.a(HeightMap.Type.WORLD_SURFACE_WG, HeightMap.Type.OCEAN_FLOOR_WG);
        ichunkaccess.a(ChunkStatus.BASE);
    }

    private double[] a(int i, int j, int k, int l, int i1, int j1) {
        double[] adouble = new double[l * i1 * j1];
        double d0 = 684.412D;
        double d1 = 684.412D;

        d0 *= 2.0D;
        double[] adouble1 = this.i.a(i, j, k, l, i1, j1, d0 / 80.0D, 4.277575000000001D, d0 / 80.0D);
        double[] adouble2 = this.g.a(i, j, k, l, i1, j1, d0, 684.412D, d0);
        double[] adouble3 = this.h.a(i, j, k, l, i1, j1, d0, 684.412D, d0);
        int k1 = i / 2;
        int l1 = k / 2;
        int i2 = 0;

        for (int j2 = 0; j2 < l; ++j2) {
            for (int k2 = 0; k2 < j1; ++k2) {
                float f = this.c.c(k1, l1, j2, k2);

                for (int l2 = 0; l2 < i1; ++l2) {
                    double d2 = adouble2[i2] / 512.0D;
                    double d3 = adouble3[i2] / 512.0D;
                    double d4 = (adouble1[i2] / 10.0D + 1.0D) / 2.0D;
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
                        d5 = d5 * (1.0D - d6) - 3000.0D * d6;
                    }

                    b0 = 8;
                    if (l2 < b0) {
                        d6 = (double) ((float) (b0 - l2) / ((float) b0 - 1.0F));
                        d5 = d5 * (1.0D - d6) - 30.0D * d6;
                    }

                    adouble[i2] = d5;
                    ++i2;
                }
            }
        }

        return adouble;
    }

    public void addMobs(RegionLimitedWorldAccess regionlimitedworldaccess) {}

    public List<BiomeBase.BiomeMeta> getMobsFor(EnumCreatureType enumcreaturetype, BlockPosition blockposition) {
        return this.a.getBiome(blockposition).getMobs(enumcreaturetype);
    }

    public BlockPosition f() {
        return this.m;
    }

    public int a(World world, boolean flag, boolean flag1) {
        return 0;
    }

    public GeneratorSettingsEnd getSettings() {
        return this.n;
    }

    public double[] a(int i, int j) {
        double d0 = 0.03125D;

        return this.l.a((double) (i << 4), (double) (j << 4), 16, 16, 0.0625D, 0.0625D, 1.0D);
    }

    public int getSpawnHeight() {
        return 50;
    }
}
