package net.minecraft.server;

public class GenLayerBiome implements AreaTransformer5 {

    private static final int a = BiomeBase.a(Biomes.C);
    private static final int b = BiomeBase.a(Biomes.d);
    private static final int c = BiomeBase.a(Biomes.e);
    private static final int d = BiomeBase.a(Biomes.f);
    private static final int e = BiomeBase.a(Biomes.n);
    private static final int f = BiomeBase.a(Biomes.w);
    private static final int g = BiomeBase.a(Biomes.O);
    private static final int h = BiomeBase.a(Biomes.N);
    private static final int i = BiomeBase.a(Biomes.p);
    private static final int j = BiomeBase.a(Biomes.c);
    private static final int k = BiomeBase.a(Biomes.H);
    private static final int l = BiomeBase.a(Biomes.E);
    private static final int m = BiomeBase.a(Biomes.K);
    private static final int n = BiomeBase.a(Biomes.h);
    private static final int o = BiomeBase.a(Biomes.g);
    private static final int p = BiomeBase.a(Biomes.F);
    private static final int[] q = new int[] { GenLayerBiome.b, GenLayerBiome.d, GenLayerBiome.c, GenLayerBiome.n, GenLayerBiome.j, GenLayerBiome.o};
    private static final int[] r = new int[] { GenLayerBiome.b, GenLayerBiome.b, GenLayerBiome.b, GenLayerBiome.m, GenLayerBiome.m, GenLayerBiome.j};
    private static final int[] s = new int[] { GenLayerBiome.d, GenLayerBiome.l, GenLayerBiome.c, GenLayerBiome.j, GenLayerBiome.a, GenLayerBiome.n};
    private static final int[] t = new int[] { GenLayerBiome.d, GenLayerBiome.c, GenLayerBiome.o, GenLayerBiome.j};
    private static final int[] u = new int[] { GenLayerBiome.e, GenLayerBiome.e, GenLayerBiome.e, GenLayerBiome.p};
    private final GeneratorSettingsOverworld v;
    private int[] w;

    public GenLayerBiome(WorldType worldtype, GeneratorSettingsOverworld generatorsettingsoverworld) {
        this.w = GenLayerBiome.r;
        if (worldtype == WorldType.NORMAL_1_1) {
            this.w = GenLayerBiome.q;
            this.v = null;
        } else {
            this.v = generatorsettingsoverworld;
        }

    }

    public int a(WorldGenContext worldgencontext, int i) {
        if (this.v != null && this.v.v() >= 0) {
            return this.v.v();
        } else {
            int j = (i & 3840) >> 8;

            i &= -3841;
            if (!GenLayers.a(i) && i != GenLayerBiome.i) {
                switch (i) {
                case 1:
                    if (j > 0) {
                        return worldgencontext.a(3) == 0 ? GenLayerBiome.g : GenLayerBiome.h;
                    }

                    return this.w[worldgencontext.a(this.w.length)];

                case 2:
                    if (j > 0) {
                        return GenLayerBiome.f;
                    }

                    return GenLayerBiome.s[worldgencontext.a(GenLayerBiome.s.length)];

                case 3:
                    if (j > 0) {
                        return GenLayerBiome.k;
                    }

                    return GenLayerBiome.t[worldgencontext.a(GenLayerBiome.t.length)];

                case 4:
                    return GenLayerBiome.u[worldgencontext.a(GenLayerBiome.u.length)];

                default:
                    return GenLayerBiome.i;
                }
            } else {
                return i;
            }
        }
    }
}
