package net.minecraft.server;

public class GenLayerDesert extends GenLayer {

    public GenLayerDesert(long i, GenLayer genlayer) {
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

                if (!this.a(aint, aint1, j1, i1, k, k1, BiomeBase.a(Biomes.e), BiomeBase.a(Biomes.v)) && !this.b(aint, aint1, j1, i1, k, k1, BiomeBase.a(Biomes.N), BiomeBase.a(Biomes.M)) && !this.b(aint, aint1, j1, i1, k, k1, BiomeBase.a(Biomes.O), BiomeBase.a(Biomes.M)) && !this.b(aint, aint1, j1, i1, k, k1, BiomeBase.a(Biomes.H), BiomeBase.a(Biomes.g))) {
                    int l1;
                    int i2;
                    int j2;
                    int k2;

                    if (k1 == BiomeBase.a(Biomes.d)) {
                        l1 = aint[j1 + 1 + (i1 + 1 - 1) * (k + 2)];
                        i2 = aint[j1 + 1 + 1 + (i1 + 1) * (k + 2)];
                        j2 = aint[j1 + 1 - 1 + (i1 + 1) * (k + 2)];
                        k2 = aint[j1 + 1 + (i1 + 1 + 1) * (k + 2)];
                        if (l1 != BiomeBase.a(Biomes.n) && i2 != BiomeBase.a(Biomes.n) && j2 != BiomeBase.a(Biomes.n) && k2 != BiomeBase.a(Biomes.n)) {
                            aint1[j1 + i1 * k] = k1;
                        } else {
                            aint1[j1 + i1 * k] = BiomeBase.a(Biomes.J);
                        }
                    } else if (k1 == BiomeBase.a(Biomes.h)) {
                        l1 = aint[j1 + 1 + (i1 + 1 - 1) * (k + 2)];
                        i2 = aint[j1 + 1 + 1 + (i1 + 1) * (k + 2)];
                        j2 = aint[j1 + 1 - 1 + (i1 + 1) * (k + 2)];
                        k2 = aint[j1 + 1 + (i1 + 1 + 1) * (k + 2)];
                        if (l1 != BiomeBase.a(Biomes.d) && i2 != BiomeBase.a(Biomes.d) && j2 != BiomeBase.a(Biomes.d) && k2 != BiomeBase.a(Biomes.d) && l1 != BiomeBase.a(Biomes.F) && i2 != BiomeBase.a(Biomes.F) && j2 != BiomeBase.a(Biomes.F) && k2 != BiomeBase.a(Biomes.F) && l1 != BiomeBase.a(Biomes.n) && i2 != BiomeBase.a(Biomes.n) && j2 != BiomeBase.a(Biomes.n) && k2 != BiomeBase.a(Biomes.n)) {
                            if (l1 != BiomeBase.a(Biomes.w) && k2 != BiomeBase.a(Biomes.w) && i2 != BiomeBase.a(Biomes.w) && j2 != BiomeBase.a(Biomes.w)) {
                                aint1[j1 + i1 * k] = k1;
                            } else {
                                aint1[j1 + i1 * k] = BiomeBase.a(Biomes.y);
                            }
                        } else {
                            aint1[j1 + i1 * k] = BiomeBase.a(Biomes.c);
                        }
                    } else {
                        aint1[j1 + i1 * k] = k1;
                    }
                }
            }
        }

        return aint1;
    }

    private boolean a(int[] aint, int[] aint1, int i, int j, int k, int l, int i1, int j1) {
        if (!a(l, i1)) {
            return false;
        } else {
            int k1 = aint[i + 1 + (j + 1 - 1) * (k + 2)];
            int l1 = aint[i + 1 + 1 + (j + 1) * (k + 2)];
            int i2 = aint[i + 1 - 1 + (j + 1) * (k + 2)];
            int j2 = aint[i + 1 + (j + 1 + 1) * (k + 2)];

            if (this.b(k1, i1) && this.b(l1, i1) && this.b(i2, i1) && this.b(j2, i1)) {
                aint1[i + j * k] = l;
            } else {
                aint1[i + j * k] = j1;
            }

            return true;
        }
    }

    private boolean b(int[] aint, int[] aint1, int i, int j, int k, int l, int i1, int j1) {
        if (l != i1) {
            return false;
        } else {
            int k1 = aint[i + 1 + (j + 1 - 1) * (k + 2)];
            int l1 = aint[i + 1 + 1 + (j + 1) * (k + 2)];
            int i2 = aint[i + 1 - 1 + (j + 1) * (k + 2)];
            int j2 = aint[i + 1 + (j + 1 + 1) * (k + 2)];

            if (a(k1, i1) && a(l1, i1) && a(i2, i1) && a(j2, i1)) {
                aint1[i + j * k] = l;
            } else {
                aint1[i + j * k] = j1;
            }

            return true;
        }
    }

    private boolean b(int i, int j) {
        if (a(i, j)) {
            return true;
        } else {
            BiomeBase biomebase = BiomeBase.getBiome(i);
            BiomeBase biomebase1 = BiomeBase.getBiome(j);

            if (biomebase != null && biomebase1 != null) {
                BiomeBase.EnumTemperature biomebase_enumtemperature = biomebase.h();
                BiomeBase.EnumTemperature biomebase_enumtemperature1 = biomebase1.h();

                return biomebase_enumtemperature == biomebase_enumtemperature1 || biomebase_enumtemperature == BiomeBase.EnumTemperature.MEDIUM || biomebase_enumtemperature1 == BiomeBase.EnumTemperature.MEDIUM;
            } else {
                return false;
            }
        }
    }
}
