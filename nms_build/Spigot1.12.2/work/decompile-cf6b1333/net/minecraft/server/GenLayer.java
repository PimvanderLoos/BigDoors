package net.minecraft.server;

public abstract class GenLayer {

    private long c;
    protected GenLayer a;
    private long d;
    protected long b;

    public static GenLayer[] a(long i, WorldType worldtype, CustomWorldSettingsFinal customworldsettingsfinal) {
        LayerIsland layerisland = new LayerIsland(1L);
        GenLayerZoomFuzzy genlayerzoomfuzzy = new GenLayerZoomFuzzy(2000L, layerisland);
        GenLayerIsland genlayerisland = new GenLayerIsland(1L, genlayerzoomfuzzy);
        GenLayerZoom genlayerzoom = new GenLayerZoom(2001L, genlayerisland);

        genlayerisland = new GenLayerIsland(2L, genlayerzoom);
        genlayerisland = new GenLayerIsland(50L, genlayerisland);
        genlayerisland = new GenLayerIsland(70L, genlayerisland);
        GenLayerIcePlains genlayericeplains = new GenLayerIcePlains(2L, genlayerisland);
        GenLayerTopSoil genlayertopsoil = new GenLayerTopSoil(2L, genlayericeplains);

        genlayerisland = new GenLayerIsland(3L, genlayertopsoil);
        GenLayerSpecial genlayerspecial = new GenLayerSpecial(2L, genlayerisland, GenLayerSpecial.EnumGenLayerSpecial.COOL_WARM);

        genlayerspecial = new GenLayerSpecial(2L, genlayerspecial, GenLayerSpecial.EnumGenLayerSpecial.HEAT_ICE);
        genlayerspecial = new GenLayerSpecial(3L, genlayerspecial, GenLayerSpecial.EnumGenLayerSpecial.SPECIAL);
        genlayerzoom = new GenLayerZoom(2002L, genlayerspecial);
        genlayerzoom = new GenLayerZoom(2003L, genlayerzoom);
        genlayerisland = new GenLayerIsland(4L, genlayerzoom);
        GenLayerMushroomIsland genlayermushroomisland = new GenLayerMushroomIsland(5L, genlayerisland);
        GenLayerDeepOcean genlayerdeepocean = new GenLayerDeepOcean(4L, genlayermushroomisland);
        GenLayer genlayer = GenLayerZoom.b(1000L, genlayerdeepocean, 0);
        int j = 4;
        int k = j;

        if (customworldsettingsfinal != null) {
            j = customworldsettingsfinal.H;
            k = customworldsettingsfinal.I;
        }

        if (worldtype == WorldType.LARGE_BIOMES) {
            j = 6;
        }

        GenLayer genlayer1 = GenLayerZoom.b(1000L, genlayer, 0);
        GenLayerCleaner genlayercleaner = new GenLayerCleaner(100L, genlayer1);
        GenLayerBiome genlayerbiome = new GenLayerBiome(200L, genlayer, worldtype, customworldsettingsfinal);
        GenLayer genlayer2 = GenLayerZoom.b(1000L, genlayerbiome, 2);
        GenLayerDesert genlayerdesert = new GenLayerDesert(1000L, genlayer2);
        GenLayer genlayer3 = GenLayerZoom.b(1000L, genlayercleaner, 2);
        GenLayerRegionHills genlayerregionhills = new GenLayerRegionHills(1000L, genlayerdesert, genlayer3);

        genlayer1 = GenLayerZoom.b(1000L, genlayercleaner, 2);
        genlayer1 = GenLayerZoom.b(1000L, genlayer1, k);
        GenLayerRiver genlayerriver = new GenLayerRiver(1L, genlayer1);
        GenLayerSmooth genlayersmooth = new GenLayerSmooth(1000L, genlayerriver);
        Object object = new GenLayerPlains(1001L, genlayerregionhills);

        for (int l = 0; l < j; ++l) {
            object = new GenLayerZoom((long) (1000 + l), (GenLayer) object);
            if (l == 0) {
                object = new GenLayerIsland(3L, (GenLayer) object);
            }

            if (l == 1 || j == 1) {
                object = new GenLayerMushroomShore(1000L, (GenLayer) object);
            }
        }

        GenLayerSmooth genlayersmooth1 = new GenLayerSmooth(1000L, (GenLayer) object);
        GenLayerRiverMix genlayerrivermix = new GenLayerRiverMix(100L, genlayersmooth1, genlayersmooth);
        GenLayerZoomVoronoi genlayerzoomvoronoi = new GenLayerZoomVoronoi(10L, genlayerrivermix);

        genlayerrivermix.a(i);
        genlayerzoomvoronoi.a(i);
        return new GenLayer[] { genlayerrivermix, genlayerzoomvoronoi, genlayerrivermix};
    }

    public GenLayer(long i) {
        this.b = i;
        this.b *= this.b * 6364136223846793005L + 1442695040888963407L;
        this.b += i;
        this.b *= this.b * 6364136223846793005L + 1442695040888963407L;
        this.b += i;
        this.b *= this.b * 6364136223846793005L + 1442695040888963407L;
        this.b += i;
    }

    public void a(long i) {
        this.c = i;
        if (this.a != null) {
            this.a.a(i);
        }

        this.c *= this.c * 6364136223846793005L + 1442695040888963407L;
        this.c += this.b;
        this.c *= this.c * 6364136223846793005L + 1442695040888963407L;
        this.c += this.b;
        this.c *= this.c * 6364136223846793005L + 1442695040888963407L;
        this.c += this.b;
    }

    public void a(long i, long j) {
        this.d = this.c;
        this.d *= this.d * 6364136223846793005L + 1442695040888963407L;
        this.d += i;
        this.d *= this.d * 6364136223846793005L + 1442695040888963407L;
        this.d += j;
        this.d *= this.d * 6364136223846793005L + 1442695040888963407L;
        this.d += i;
        this.d *= this.d * 6364136223846793005L + 1442695040888963407L;
        this.d += j;
    }

    protected int a(int i) {
        int j = (int) ((this.d >> 24) % (long) i);

        if (j < 0) {
            j += i;
        }

        this.d *= this.d * 6364136223846793005L + 1442695040888963407L;
        this.d += this.c;
        return j;
    }

    public abstract int[] a(int i, int j, int k, int l);

    protected static boolean a(int i, int j) {
        if (i == j) {
            return true;
        } else {
            BiomeBase biomebase = BiomeBase.getBiome(i);
            BiomeBase biomebase1 = BiomeBase.getBiome(j);

            return biomebase != null && biomebase1 != null ? (biomebase != Biomes.N && biomebase != Biomes.O ? biomebase == biomebase1 || biomebase.g() == biomebase1.g() : biomebase1 == Biomes.N || biomebase1 == Biomes.O) : false;
        }
    }

    protected static boolean b(int i) {
        BiomeBase biomebase = BiomeBase.getBiome(i);

        return biomebase == Biomes.a || biomebase == Biomes.z || biomebase == Biomes.l;
    }

    protected int a(int... aint) {
        return aint[this.a(aint.length)];
    }

    protected int b(int i, int j, int k, int l) {
        return j == k && k == l ? j : (i == j && i == k ? i : (i == j && i == l ? i : (i == k && i == l ? i : (i == j && k != l ? i : (i == k && j != l ? i : (i == l && j != k ? i : (j == k && i != l ? j : (j == l && i != k ? j : (k == l && i != j ? k : this.a(new int[] { i, j, k, l}))))))))));
    }
}
