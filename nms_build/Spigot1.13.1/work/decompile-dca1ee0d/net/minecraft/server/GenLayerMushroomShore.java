package net.minecraft.server;

public enum GenLayerMushroomShore implements AreaTransformer7 {

    INSTANCE;

    private static final int b = IRegistry.BIOME.a((Object) Biomes.r);
    private static final int c = IRegistry.BIOME.a((Object) Biomes.B);
    private static final int d = IRegistry.BIOME.a((Object) Biomes.d);
    private static final int e = IRegistry.BIOME.a((Object) Biomes.e);
    private static final int f = IRegistry.BIOME.a((Object) Biomes.J);
    private static final int g = IRegistry.BIOME.a((Object) Biomes.f);
    private static final int h = IRegistry.BIOME.a((Object) Biomes.w);
    private static final int i = IRegistry.BIOME.a((Object) Biomes.y);
    private static final int j = IRegistry.BIOME.a((Object) Biomes.x);
    private static final int k = IRegistry.BIOME.a((Object) Biomes.M);
    private static final int l = IRegistry.BIOME.a((Object) Biomes.N);
    private static final int m = IRegistry.BIOME.a((Object) Biomes.O);
    private static final int n = IRegistry.BIOME.a((Object) Biomes.at);
    private static final int o = IRegistry.BIOME.a((Object) Biomes.au);
    private static final int p = IRegistry.BIOME.a((Object) Biomes.av);
    private static final int q = IRegistry.BIOME.a((Object) Biomes.p);
    private static final int r = IRegistry.BIOME.a((Object) Biomes.q);
    private static final int s = IRegistry.BIOME.a((Object) Biomes.i);
    private static final int t = IRegistry.BIOME.a((Object) Biomes.v);
    private static final int u = IRegistry.BIOME.a((Object) Biomes.A);
    private static final int v = IRegistry.BIOME.a((Object) Biomes.h);
    private static final int w = IRegistry.BIOME.a((Object) Biomes.g);

    private GenLayerMushroomShore() {}

    public int a(WorldGenContext worldgencontext, int i, int j, int k, int l, int i1) {
        BiomeBase biomebase = (BiomeBase) IRegistry.BIOME.fromId(i1);

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
        return IRegistry.BIOME.fromId(i) != null && ((BiomeBase) IRegistry.BIOME.fromId(i)).p() == BiomeBase.Geography.JUNGLE ? true : i == GenLayerMushroomShore.i || i == GenLayerMushroomShore.h || i == GenLayerMushroomShore.j || i == GenLayerMushroomShore.g || i == GenLayerMushroomShore.w || GenLayers.a(i);
    }

    private boolean b(int i) {
        return i == GenLayerMushroomShore.k || i == GenLayerMushroomShore.l || i == GenLayerMushroomShore.m || i == GenLayerMushroomShore.n || i == GenLayerMushroomShore.o || i == GenLayerMushroomShore.p;
    }
}
