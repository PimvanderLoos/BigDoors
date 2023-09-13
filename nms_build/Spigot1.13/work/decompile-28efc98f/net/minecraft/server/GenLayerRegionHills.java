package net.minecraft.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum GenLayerRegionHills implements AreaTransformer3, AreaTransformerOffset1 {

    INSTANCE;

    private static final Logger b = LogManager.getLogger();
    private static final int c = BiomeBase.a(Biomes.C);
    private static final int d = BiomeBase.a(Biomes.D);
    private static final int e = BiomeBase.a(Biomes.d);
    private static final int f = BiomeBase.a(Biomes.s);
    private static final int g = BiomeBase.a(Biomes.e);
    private static final int h = BiomeBase.a(Biomes.J);
    private static final int i = BiomeBase.a(Biomes.f);
    private static final int j = BiomeBase.a(Biomes.t);
    private static final int k = BiomeBase.a(Biomes.n);
    private static final int l = BiomeBase.a(Biomes.o);
    private static final int m = BiomeBase.a(Biomes.w);
    private static final int n = BiomeBase.a(Biomes.x);
    private static final int o = BiomeBase.a(Biomes.M);
    private static final int p = BiomeBase.a(Biomes.N);
    private static final int q = BiomeBase.a(Biomes.c);
    private static final int r = BiomeBase.a(Biomes.H);
    private static final int s = BiomeBase.a(Biomes.I);
    private static final int t = BiomeBase.a(Biomes.E);
    private static final int u = BiomeBase.a(Biomes.K);
    private static final int v = BiomeBase.a(Biomes.L);
    private static final int w = BiomeBase.a(Biomes.g);
    private static final int x = BiomeBase.a(Biomes.F);
    private static final int y = BiomeBase.a(Biomes.G);
    private static final int z = BiomeBase.a(Biomes.u);

    private GenLayerRegionHills() {}

    public int a(WorldGenContext worldgencontext, AreaDimension areadimension, Area area, Area area1, int i, int j) {
        int k = area.a(i + 1, j + 1);
        int l = area1.a(i + 1, j + 1);

        if (k > 255) {
            GenLayerRegionHills.b.debug("old! {}", Integer.valueOf(k));
        }

        int i1 = (l - 2) % 29;
        BiomeBase biomebase;

        if (!GenLayers.b(k) && l >= 2 && i1 == 1) {
            BiomeBase biomebase1 = BiomeBase.a(k);

            if (biomebase1 == null || !biomebase1.b()) {
                biomebase = BiomeBase.b(biomebase1);
                return biomebase == null ? k : BiomeBase.a(biomebase);
            }
        }

        if (worldgencontext.a(3) == 0 || i1 == 0) {
            int j1 = k;

            if (k == GenLayerRegionHills.e) {
                j1 = GenLayerRegionHills.f;
            } else if (k == GenLayerRegionHills.i) {
                j1 = GenLayerRegionHills.j;
            } else if (k == GenLayerRegionHills.c) {
                j1 = GenLayerRegionHills.d;
            } else if (k == GenLayerRegionHills.t) {
                j1 = GenLayerRegionHills.q;
            } else if (k == GenLayerRegionHills.w) {
                j1 = GenLayerRegionHills.z;
            } else if (k == GenLayerRegionHills.r) {
                j1 = GenLayerRegionHills.s;
            } else if (k == GenLayerRegionHills.x) {
                j1 = GenLayerRegionHills.y;
            } else if (k == GenLayerRegionHills.q) {
                j1 = worldgencontext.a(3) == 0 ? GenLayerRegionHills.j : GenLayerRegionHills.i;
            } else if (k == GenLayerRegionHills.k) {
                j1 = GenLayerRegionHills.l;
            } else if (k == GenLayerRegionHills.m) {
                j1 = GenLayerRegionHills.n;
            } else if (k == GenLayers.c) {
                j1 = GenLayers.h;
            } else if (k == GenLayers.b) {
                j1 = GenLayers.g;
            } else if (k == GenLayers.d) {
                j1 = GenLayers.i;
            } else if (k == GenLayers.e) {
                j1 = GenLayers.j;
            } else if (k == GenLayerRegionHills.g) {
                j1 = GenLayerRegionHills.h;
            } else if (k == GenLayerRegionHills.u) {
                j1 = GenLayerRegionHills.v;
            } else if (GenLayers.a(k, GenLayerRegionHills.p)) {
                j1 = GenLayerRegionHills.o;
            } else if ((k == GenLayers.h || k == GenLayers.g || k == GenLayers.i || k == GenLayers.j) && worldgencontext.a(3) == 0) {
                j1 = worldgencontext.a(2) == 0 ? GenLayerRegionHills.q : GenLayerRegionHills.i;
            }

            if (i1 == 0 && j1 != k) {
                biomebase = BiomeBase.b(BiomeBase.a(j1));
                j1 = biomebase == null ? k : BiomeBase.a(biomebase);
            }

            if (j1 != k) {
                int k1 = 0;

                if (GenLayers.a(area.a(i + 1, j + 0), k)) {
                    ++k1;
                }

                if (GenLayers.a(area.a(i + 2, j + 1), k)) {
                    ++k1;
                }

                if (GenLayers.a(area.a(i + 0, j + 1), k)) {
                    ++k1;
                }

                if (GenLayers.a(area.a(i + 1, j + 2), k)) {
                    ++k1;
                }

                if (k1 >= 3) {
                    return j1;
                }
            }
        }

        return k;
    }
}
