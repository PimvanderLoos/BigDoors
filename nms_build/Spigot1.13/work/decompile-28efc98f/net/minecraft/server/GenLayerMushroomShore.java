package net.minecraft.server;

public enum GenLayerMushroomShore implements AreaTransformer7 {

    INSTANCE;

    private static final int b = BiomeBase.a(Biomes.r);
    private static final int c = BiomeBase.a(Biomes.B);
    private static final int d = BiomeBase.a(Biomes.d);
    private static final int e = BiomeBase.a(Biomes.e);
    private static final int f = BiomeBase.a(Biomes.J);
    private static final int g = BiomeBase.a(Biomes.f);
    private static final int h = BiomeBase.a(Biomes.w);
    private static final int i = BiomeBase.a(Biomes.y);
    private static final int j = BiomeBase.a(Biomes.x);
    private static final int k = BiomeBase.a(Biomes.M);
    private static final int l = BiomeBase.a(Biomes.N);
    private static final int m = BiomeBase.a(Biomes.O);
    private static final int n = BiomeBase.a(Biomes.at);
    private static final int o = BiomeBase.a(Biomes.au);
    private static final int p = BiomeBase.a(Biomes.av);
    private static final int q = BiomeBase.a(Biomes.p);
    private static final int r = BiomeBase.a(Biomes.q);
    private static final int s = BiomeBase.a(Biomes.i);
    private static final int t = BiomeBase.a(Biomes.v);
    private static final int u = BiomeBase.a(Biomes.A);
    private static final int v = BiomeBase.a(Biomes.h);
    private static final int w = BiomeBase.a(Biomes.g);

    private GenLayerMushroomShore() {}

    public int a(WorldGenContext worldgencontext, int i, int j, int k, int l, int i1) {
        BiomeBase biomebase = BiomeBase.getBiome(i1);

        if (i1 == GenLayerMushroomShore.q) {
            if (GenLayers.b(i) || GenLayers.b(j) || GenLayers.b(k) || GenLayers.b(l)) {
                return GenLayerMushroomShore.r;
            }
        } else if (biomebase != null && biomebase.p() == BiomeBase.Geography.JUNGLE) {
            if (!a(i) || !a(j) || !a(k) || !a(l)) {
                return GenLayerMushroomShore.i;
            }

            if (GenLayers.a(i) || GenLayers.a(j) || GenLayers.a(k) || GenLayers.a(l)) {
                return GenLayerMushroomShore.b;
            }
        } else if (i1 != GenLayerMushroomShore.e && i1 != GenLayerMushroomShore.f && i1 != GenLayerMushroomShore.t) {
            if (biomebase != null && biomebase.c() == BiomeBase.Precipitation.SNOW) {
                if (!GenLayers.a(i1) && (GenLayers.a(i) || GenLayers.a(j) || GenLayers.a(k) || GenLayers.a(l))) {
                    return GenLayerMushroomShore.c;
                }
            } else if (i1 != GenLayerMushroomShore.k && i1 != GenLayerMushroomShore.l) {
                if (!GenLayers.a(i1) && i1 != GenLayerMushroomShore.s && i1 != GenLayerMushroomShore.v && (GenLayers.a(i) || GenLayers.a(j) || GenLayers.a(k) || GenLayers.a(l))) {
                    return GenLayerMushroomShore.b;
                }
            } else if (!GenLayers.a(i) && !GenLayers.a(j) && !GenLayers.a(k) && !GenLayers.a(l) && (!this.b(i) || !this.b(j) || !this.b(k) || !this.b(l))) {
                return GenLayerMushroomShore.d;
            }
        } else if (!GenLayers.a(i1) && (GenLayers.a(i) || GenLayers.a(j) || GenLayers.a(k) || GenLayers.a(l))) {
            return GenLayerMushroomShore.u;
        }

        return i1;
    }

    private static boolean a(int i) {
        return BiomeBase.getBiome(i) != null && BiomeBase.getBiome(i).p() == BiomeBase.Geography.JUNGLE ? true : i == GenLayerMushroomShore.i || i == GenLayerMushroomShore.h || i == GenLayerMushroomShore.j || i == GenLayerMushroomShore.g || i == GenLayerMushroomShore.w || GenLayers.a(i);
    }

    private boolean b(int i) {
        return i == GenLayerMushroomShore.k || i == GenLayerMushroomShore.l || i == GenLayerMushroomShore.m || i == GenLayerMushroomShore.n || i == GenLayerMushroomShore.o || i == GenLayerMushroomShore.p;
    }
}
