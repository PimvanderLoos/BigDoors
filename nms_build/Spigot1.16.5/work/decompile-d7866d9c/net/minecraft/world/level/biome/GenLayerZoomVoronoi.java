package net.minecraft.world.level.biome;

import net.minecraft.util.LinearCongruentialGenerator;

public enum GenLayerZoomVoronoi implements GenLayerZoomer {

    INSTANCE;

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
        double[] adouble = new double[8];

        int k2;
        int l2;
        int i3;

        for (k2 = 0; k2 < 8; ++k2) {
            boolean flag = (k2 & 4) == 0;
            boolean flag1 = (k2 & 2) == 0;
            boolean flag2 = (k2 & 1) == 0;

            l2 = flag ? l1 : l1 + 1;
            i3 = flag1 ? i2 : i2 + 1;
            int j3 = flag2 ? j2 : j2 + 1;
            double d3 = flag ? d0 : d0 - 1.0D;
            double d4 = flag1 ? d1 : d1 - 1.0D;
            double d5 = flag2 ? d2 : d2 - 1.0D;

            adouble[k2] = a(i, l2, i3, j3, d3, d4, d5);
        }

        k2 = 0;
        double d6 = adouble[0];

        int k3;

        for (k3 = 1; k3 < 8; ++k3) {
            if (d6 > adouble[k3]) {
                k2 = k3;
                d6 = adouble[k3];
            }
        }

        k3 = (k2 & 4) == 0 ? l1 : l1 + 1;
        l2 = (k2 & 2) == 0 ? i2 : i2 + 1;
        i3 = (k2 & 1) == 0 ? j2 : j2 + 1;
        return biomemanager_provider.getBiome(k3, l2, i3);
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
        double d0 = (double) ((int) Math.floorMod(i >> 24, 1024L)) / 1024.0D;

        return (d0 - 0.5D) * 0.9D;
    }

    private static double a(double d0) {
        return d0 * d0;
    }
}
