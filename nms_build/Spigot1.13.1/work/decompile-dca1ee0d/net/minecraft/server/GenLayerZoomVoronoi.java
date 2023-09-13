package net.minecraft.server;

public enum GenLayerZoomVoronoi implements AreaTransformer2 {

    INSTANCE;

    private GenLayerZoomVoronoi() {}

    public int a(AreaContextTransformed<?> areacontexttransformed, AreaDimension areadimension, Area area, int i, int j) {
        int k = i + areadimension.a() - 2;
        int l = j + areadimension.b() - 2;
        int i1 = areadimension.a() >> 2;
        int j1 = areadimension.b() >> 2;
        int k1 = (k >> 2) - i1;
        int l1 = (l >> 2) - j1;

        areacontexttransformed.a((long) (k1 + i1 << 2), (long) (l1 + j1 << 2));
        double d0 = ((double) areacontexttransformed.a(1024) / 1024.0D - 0.5D) * 3.6D;
        double d1 = ((double) areacontexttransformed.a(1024) / 1024.0D - 0.5D) * 3.6D;

        areacontexttransformed.a((long) (k1 + i1 + 1 << 2), (long) (l1 + j1 << 2));
        double d2 = ((double) areacontexttransformed.a(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;
        double d3 = ((double) areacontexttransformed.a(1024) / 1024.0D - 0.5D) * 3.6D;

        areacontexttransformed.a((long) (k1 + i1 << 2), (long) (l1 + j1 + 1 << 2));
        double d4 = ((double) areacontexttransformed.a(1024) / 1024.0D - 0.5D) * 3.6D;
        double d5 = ((double) areacontexttransformed.a(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;

        areacontexttransformed.a((long) (k1 + i1 + 1 << 2), (long) (l1 + j1 + 1 << 2));
        double d6 = ((double) areacontexttransformed.a(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;
        double d7 = ((double) areacontexttransformed.a(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;
        int i2 = k & 3;
        int j2 = l & 3;
        double d8 = ((double) j2 - d1) * ((double) j2 - d1) + ((double) i2 - d0) * ((double) i2 - d0);
        double d9 = ((double) j2 - d3) * ((double) j2 - d3) + ((double) i2 - d2) * ((double) i2 - d2);
        double d10 = ((double) j2 - d5) * ((double) j2 - d5) + ((double) i2 - d4) * ((double) i2 - d4);
        double d11 = ((double) j2 - d7) * ((double) j2 - d7) + ((double) i2 - d6) * ((double) i2 - d6);

        return d8 < d9 && d8 < d10 && d8 < d11 ? area.a(k1 + 0, l1 + 0) : (d9 < d8 && d9 < d10 && d9 < d11 ? area.a(k1 + 1, l1 + 0) & 255 : (d10 < d8 && d10 < d9 && d10 < d11 ? area.a(k1 + 0, l1 + 1) : area.a(k1 + 1, l1 + 1) & 255));
    }

    public AreaDimension a(AreaDimension areadimension) {
        int i = areadimension.a() >> 2;
        int j = areadimension.b() >> 2;
        int k = (areadimension.c() >> 2) + 2;
        int l = (areadimension.d() >> 2) + 2;

        return new AreaDimension(i, j, k, l);
    }
}
