package net.minecraft.server;

public class GenLayerMushroomShore extends GenLayer {

    public GenLayerMushroomShore(long i, GenLayer genlayer) {
        super(i);
        this.a = genlayer;
    }

    public int[] a(int i, int j, int k, int l) {
        int[] aint = this.a.a(i - 1, j - 1, k + 2, l + 2);
        int[] aint1 = IntCache.a(k * l);

        for (int i1 = 0; i1 < l; ++i1) {
            for (int j1 = 0; j1 < k; ++j1) {
                this.a((long) (j1 + i), (long) (i1 + j));
                int k1 = aint[j1 + 1 + (i1 + 1) * (k + 2)];
                BiomeBase biomebase = BiomeBase.getBiome(k1);
                int l1;
                int i2;
                int j2;
                int k2;

                if (k1 == BiomeBase.a(Biomes.p)) {
                    l1 = aint[j1 + 1 + (i1 + 1 - 1) * (k + 2)];
                    i2 = aint[j1 + 1 + 1 + (i1 + 1) * (k + 2)];
                    j2 = aint[j1 + 1 - 1 + (i1 + 1) * (k + 2)];
                    k2 = aint[j1 + 1 + (i1 + 1 + 1) * (k + 2)];
                    if (l1 != BiomeBase.a(Biomes.a) && i2 != BiomeBase.a(Biomes.a) && j2 != BiomeBase.a(Biomes.a) && k2 != BiomeBase.a(Biomes.a)) {
                        aint1[j1 + i1 * k] = k1;
                    } else {
                        aint1[j1 + i1 * k] = BiomeBase.a(Biomes.q);
                    }
                } else if (biomebase != null && biomebase.g() == BiomeJungle.class) {
                    l1 = aint[j1 + 1 + (i1 + 1 - 1) * (k + 2)];
                    i2 = aint[j1 + 1 + 1 + (i1 + 1) * (k + 2)];
                    j2 = aint[j1 + 1 - 1 + (i1 + 1) * (k + 2)];
                    k2 = aint[j1 + 1 + (i1 + 1 + 1) * (k + 2)];
                    if (this.c(l1) && this.c(i2) && this.c(j2) && this.c(k2)) {
                        if (!b(l1) && !b(i2) && !b(j2) && !b(k2)) {
                            aint1[j1 + i1 * k] = k1;
                        } else {
                            aint1[j1 + i1 * k] = BiomeBase.a(Biomes.r);
                        }
                    } else {
                        aint1[j1 + i1 * k] = BiomeBase.a(Biomes.y);
                    }
                } else if (k1 != BiomeBase.a(Biomes.e) && k1 != BiomeBase.a(Biomes.J) && k1 != BiomeBase.a(Biomes.v)) {
                    if (biomebase != null && biomebase.p()) {
                        this.a(aint, aint1, j1, i1, k, k1, BiomeBase.a(Biomes.B));
                    } else if (k1 != BiomeBase.a(Biomes.M) && k1 != BiomeBase.a(Biomes.N)) {
                        if (k1 != BiomeBase.a(Biomes.a) && k1 != BiomeBase.a(Biomes.z) && k1 != BiomeBase.a(Biomes.i) && k1 != BiomeBase.a(Biomes.h)) {
                            l1 = aint[j1 + 1 + (i1 + 1 - 1) * (k + 2)];
                            i2 = aint[j1 + 1 + 1 + (i1 + 1) * (k + 2)];
                            j2 = aint[j1 + 1 - 1 + (i1 + 1) * (k + 2)];
                            k2 = aint[j1 + 1 + (i1 + 1 + 1) * (k + 2)];
                            if (!b(l1) && !b(i2) && !b(j2) && !b(k2)) {
                                aint1[j1 + i1 * k] = k1;
                            } else {
                                aint1[j1 + i1 * k] = BiomeBase.a(Biomes.r);
                            }
                        } else {
                            aint1[j1 + i1 * k] = k1;
                        }
                    } else {
                        l1 = aint[j1 + 1 + (i1 + 1 - 1) * (k + 2)];
                        i2 = aint[j1 + 1 + 1 + (i1 + 1) * (k + 2)];
                        j2 = aint[j1 + 1 - 1 + (i1 + 1) * (k + 2)];
                        k2 = aint[j1 + 1 + (i1 + 1 + 1) * (k + 2)];
                        if (!b(l1) && !b(i2) && !b(j2) && !b(k2)) {
                            if (this.d(l1) && this.d(i2) && this.d(j2) && this.d(k2)) {
                                aint1[j1 + i1 * k] = k1;
                            } else {
                                aint1[j1 + i1 * k] = BiomeBase.a(Biomes.d);
                            }
                        } else {
                            aint1[j1 + i1 * k] = k1;
                        }
                    }
                } else {
                    this.a(aint, aint1, j1, i1, k, k1, BiomeBase.a(Biomes.A));
                }
            }
        }

        return aint1;
    }

    private void a(int[] aint, int[] aint1, int i, int j, int k, int l, int i1) {
        if (b(l)) {
            aint1[i + j * k] = l;
        } else {
            int j1 = aint[i + 1 + (j + 1 - 1) * (k + 2)];
            int k1 = aint[i + 1 + 1 + (j + 1) * (k + 2)];
            int l1 = aint[i + 1 - 1 + (j + 1) * (k + 2)];
            int i2 = aint[i + 1 + (j + 1 + 1) * (k + 2)];

            if (!b(j1) && !b(k1) && !b(l1) && !b(i2)) {
                aint1[i + j * k] = l;
            } else {
                aint1[i + j * k] = i1;
            }

        }
    }

    private boolean c(int i) {
        return BiomeBase.getBiome(i) != null && BiomeBase.getBiome(i).g() == BiomeJungle.class ? true : i == BiomeBase.a(Biomes.y) || i == BiomeBase.a(Biomes.w) || i == BiomeBase.a(Biomes.x) || i == BiomeBase.a(Biomes.f) || i == BiomeBase.a(Biomes.g) || b(i);
    }

    private boolean d(int i) {
        return BiomeBase.getBiome(i) instanceof BiomeMesa;
    }
}
