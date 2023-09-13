package net.minecraft.world.level.biome;

import net.minecraft.util.LinearCongruentialGenerator;

public enum GenLayerZoomVoronoi implements GenLayerZoomer {

    INSTANCE;

    private static final int ZOOM_BITS = 2;
    private static final int ZOOM = 4;
    private static final int ZOOM_MASK = 3;

    private GenLayerZoomVoronoi() {}

    @Override
    public BiomeBase a(long i, int j, int k, int l, BiomeManager.Provider biomemanager_provider) {
        int i1 = j - 2;
        int j1 = k - 2;
        int k1 = l - 2;
        int l1 = i1 >> 2;
        int i2 = j1 >> 2;
        int j2 = k1 >> 2;
        double d0 = (double) (i1 & 3) / 4.0D;
        double d1 = (double) (j1 & 3) / 4.0D;
        double d2 = (double) (k1 & 3) / 4.0D;
        int k2 = 0;
        double d3 = Double.POSITIVE_INFINITY;

        int l2;

        for (l2 = 0; l2 < 8; ++l2) {
            boolean flag = (l2 & 4) == 0;
            boolean flag1 = (l2 & 2) == 0;
            boolean flag2 = (l2 & 1) == 0;
            int i3 = flag ? l1 : l1 + 1;
            int j3 = flag1 ? i2 : i2 + 1;
            int k3 = flag2 ? j2 : j2 + 1;
            double d4 = flag ? d0 : d0 - 1.0D;
            double d5 = flag1 ? d1 : d1 - 1.0D;
            double d6 = flag2 ? d2 : d2 - 1.0D;
            double d7 = a(i, i3, j3, k3, d4, d5, d6);

            if (d3 > d7) {
                k2 = l2;
                d3 = d7;
            }
        }

        l2 = (k2 & 4) == 0 ? l1 : l1 + 1;
        int l3 = (k2 & 2) == 0 ? i2 : i2 + 1;
        int i4 = (k2 & 1) == 0 ? j2 : j2 + 1;

        return biomemanager_provider.getBiome(l2, l3, i4);
    }

    private static double a(long i, int j, int k, int l, double d0, double d1, double d2) {
        long i1 = LinearCongruentialGenerator.a(i, (long) j);

        i1 = LinearCongruentialGenerator.a(i1, (long) k);
        i1 = LinearCongruentialGenerator.a(i1, (long) l);
        i1 = LinearCongruentialGenerator.a(i1, (long) j);
        i1 = LinearCongruentialGenerator.a(i1, (long) k);
        i1 = LinearCongruentialGenerator.a(i1, (long) l);
        double d3 = a(i1);

        i1 = LinearCongruentialGenerator.a(i1, i);
        double d4 = a(i1);

        i1 = LinearCongruentialGenerator.a(i1, i);
        double d5 = a(i1);

        return a(d2 + d5) + a(d1 + d4) + a(d0 + d3);
    }

    private static double a(long i) {
        double d0 = (double) Math.floorMod(i >> 24, 1024) / 1024.0D;

        return (d0 - 0.5D) * 0.9D;
    }

    private static double a(double d0) {
        return d0 * d0;
    }
}
