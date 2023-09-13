package net.minecraft.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GenLayerRegionHills extends GenLayer {

    private static final Logger c = LogManager.getLogger();
    private final GenLayer d;

    public GenLayerRegionHills(long i, GenLayer genlayer, GenLayer genlayer1) {
        super(i);
        this.a = genlayer;
        this.d = genlayer1;
    }

    public int[] a(int i, int j, int k, int l) {
        int[] aint = this.a.a(i - 1, j - 1, k + 2, l + 2);
        int[] aint1 = this.d.a(i - 1, j - 1, k + 2, l + 2);
        int[] aint2 = IntCache.a(k * l);

        for (int i1 = 0; i1 < l; ++i1) {
            for (int j1 = 0; j1 < k; ++j1) {
                this.a((long) (j1 + i), (long) (i1 + j));
                int k1 = aint[j1 + 1 + (i1 + 1) * (k + 2)];
                int l1 = aint1[j1 + 1 + (i1 + 1) * (k + 2)];
                boolean flag = (l1 - 2) % 29 == 0;

                if (k1 > 255) {
                    GenLayerRegionHills.c.debug("old! {}", Integer.valueOf(k1));
                }

                BiomeBase biomebase = BiomeBase.a(k1);
                boolean flag1 = biomebase != null && biomebase.b();
                BiomeBase biomebase1;

                if (k1 != 0 && l1 >= 2 && (l1 - 2) % 29 == 1 && !flag1) {
                    biomebase1 = BiomeBase.b(biomebase);
                    aint2[j1 + i1 * k] = biomebase1 == null ? k1 : BiomeBase.a(biomebase1);
                } else if (this.a(3) != 0 && !flag) {
                    aint2[j1 + i1 * k] = k1;
                } else {
                    biomebase1 = biomebase;
                    int i2;

                    if (biomebase == Biomes.d) {
                        biomebase1 = Biomes.s;
                    } else if (biomebase == Biomes.f) {
                        biomebase1 = Biomes.t;
                    } else if (biomebase == Biomes.C) {
                        biomebase1 = Biomes.D;
                    } else if (biomebase == Biomes.E) {
                        biomebase1 = Biomes.c;
                    } else if (biomebase == Biomes.g) {
                        biomebase1 = Biomes.u;
                    } else if (biomebase == Biomes.H) {
                        biomebase1 = Biomes.I;
                    } else if (biomebase == Biomes.F) {
                        biomebase1 = Biomes.G;
                    } else if (biomebase == Biomes.c) {
                        if (this.a(3) == 0) {
                            biomebase1 = Biomes.t;
                        } else {
                            biomebase1 = Biomes.f;
                        }
                    } else if (biomebase == Biomes.n) {
                        biomebase1 = Biomes.o;
                    } else if (biomebase == Biomes.w) {
                        biomebase1 = Biomes.x;
                    } else if (biomebase == Biomes.a) {
                        biomebase1 = Biomes.z;
                    } else if (biomebase == Biomes.e) {
                        biomebase1 = Biomes.J;
                    } else if (biomebase == Biomes.K) {
                        biomebase1 = Biomes.L;
                    } else if (a(k1, BiomeBase.a(Biomes.N))) {
                        biomebase1 = Biomes.M;
                    } else if (biomebase == Biomes.z && this.a(3) == 0) {
                        i2 = this.a(2);
                        if (i2 == 0) {
                            biomebase1 = Biomes.c;
                        } else {
                            biomebase1 = Biomes.f;
                        }
                    }

                    i2 = BiomeBase.a(biomebase1);
                    if (flag && i2 != k1) {
                        BiomeBase biomebase2 = BiomeBase.b(biomebase1);

                        i2 = biomebase2 == null ? k1 : BiomeBase.a(biomebase2);
                    }

                    if (i2 == k1) {
                        aint2[j1 + i1 * k] = k1;
                    } else {
                        int j2 = aint[j1 + 1 + (i1 + 0) * (k + 2)];
                        int k2 = aint[j1 + 2 + (i1 + 1) * (k + 2)];
                        int l2 = aint[j1 + 0 + (i1 + 1) * (k + 2)];
                        int i3 = aint[j1 + 1 + (i1 + 2) * (k + 2)];
                        int j3 = 0;

                        if (a(j2, k1)) {
                            ++j3;
                        }

                        if (a(k2, k1)) {
                            ++j3;
                        }

                        if (a(l2, k1)) {
                            ++j3;
                        }

                        if (a(i3, k1)) {
                            ++j3;
                        }

                        if (j3 >= 3) {
                            aint2[j1 + i1 * k] = i2;
                        } else {
                            aint2[j1 + i1 * k] = k1;
                        }
                    }
                }
            }
        }

        return aint2;
    }
}
