package net.minecraft.server;

public class GenLayerBiome extends GenLayer {

    private BiomeBase[] c;
    private final BiomeBase[] d;
    private final BiomeBase[] e;
    private final BiomeBase[] f;
    private final CustomWorldSettingsFinal g;

    public GenLayerBiome(long i, GenLayer genlayer, WorldType worldtype, CustomWorldSettingsFinal customworldsettingsfinal) {
        super(i);
        this.c = new BiomeBase[] { Biomes.d, Biomes.d, Biomes.d, Biomes.K, Biomes.K, Biomes.c};
        this.d = new BiomeBase[] { Biomes.f, Biomes.E, Biomes.e, Biomes.c, Biomes.C, Biomes.h};
        this.e = new BiomeBase[] { Biomes.f, Biomes.e, Biomes.g, Biomes.c};
        this.f = new BiomeBase[] { Biomes.n, Biomes.n, Biomes.n, Biomes.F};
        this.a = genlayer;
        if (worldtype == WorldType.NORMAL_1_1) {
            this.c = new BiomeBase[] { Biomes.d, Biomes.f, Biomes.e, Biomes.h, Biomes.c, Biomes.g};
            this.g = null;
        } else {
            this.g = customworldsettingsfinal;
        }

    }

    public int[] a(int i, int j, int k, int l) {
        int[] aint = this.a.a(i, j, k, l);
        int[] aint1 = IntCache.a(k * l);

        for (int i1 = 0; i1 < l; ++i1) {
            for (int j1 = 0; j1 < k; ++j1) {
                this.a((long) (j1 + i), (long) (i1 + j));
                int k1 = aint[j1 + i1 * k];
                int l1 = (k1 & 3840) >> 8;

                k1 &= -3841;
                if (this.g != null && this.g.G >= 0) {
                    aint1[j1 + i1 * k] = this.g.G;
                } else if (b(k1)) {
                    aint1[j1 + i1 * k] = k1;
                } else if (k1 == BiomeBase.a(Biomes.p)) {
                    aint1[j1 + i1 * k] = k1;
                } else if (k1 == 1) {
                    if (l1 > 0) {
                        if (this.a(3) == 0) {
                            aint1[j1 + i1 * k] = BiomeBase.a(Biomes.O);
                        } else {
                            aint1[j1 + i1 * k] = BiomeBase.a(Biomes.N);
                        }
                    } else {
                        aint1[j1 + i1 * k] = BiomeBase.a(this.c[this.a(this.c.length)]);
                    }
                } else if (k1 == 2) {
                    if (l1 > 0) {
                        aint1[j1 + i1 * k] = BiomeBase.a(Biomes.w);
                    } else {
                        aint1[j1 + i1 * k] = BiomeBase.a(this.d[this.a(this.d.length)]);
                    }
                } else if (k1 == 3) {
                    if (l1 > 0) {
                        aint1[j1 + i1 * k] = BiomeBase.a(Biomes.H);
                    } else {
                        aint1[j1 + i1 * k] = BiomeBase.a(this.e[this.a(this.e.length)]);
                    }
                } else if (k1 == 4) {
                    aint1[j1 + i1 * k] = BiomeBase.a(this.f[this.a(this.f.length)]);
                } else {
                    aint1[j1 + i1 * k] = BiomeBase.a(Biomes.p);
                }
            }
        }

        return aint1;
    }
}
