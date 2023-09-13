package net.minecraft.server;

public enum GenLayerZoom implements AreaTransformer2 {

    NORMAL, FUZZY {
        protected int a(AreaContextTransformed<?> areacontexttransformed, int i, int j, int k, int l) {
            return areacontexttransformed.a(i, j, k, l);
        }
    };

    private GenLayerZoom() {}

    public AreaDimension a(AreaDimension areadimension) {
        int i = areadimension.a() >> 1;
        int j = areadimension.b() >> 1;
        int k = (areadimension.c() >> 1) + 3;
        int l = (areadimension.d() >> 1) + 3;

        return new AreaDimension(i, j, k, l);
    }

    public int a(AreaContextTransformed<?> areacontexttransformed, AreaDimension areadimension, Area area, int i, int j) {
        int k = areadimension.a() >> 1;
        int l = areadimension.b() >> 1;
        int i1 = i + areadimension.a();
        int j1 = j + areadimension.b();
        int k1 = (i1 >> 1) - k;
        int l1 = k1 + 1;
        int i2 = (j1 >> 1) - l;
        int j2 = i2 + 1;
        int k2 = area.a(k1, i2);

        areacontexttransformed.a((long) (i1 >> 1 << 1), (long) (j1 >> 1 << 1));
        int l2 = i1 & 1;
        int i3 = j1 & 1;

        if (l2 == 0 && i3 == 0) {
            return k2;
        } else {
            int j3 = area.a(k1, j2);
            int k3 = areacontexttransformed.a(k2, j3);

            if (l2 == 0 && i3 == 1) {
                return k3;
            } else {
                int l3 = area.a(l1, i2);
                int i4 = areacontexttransformed.a(k2, l3);

                if (l2 == 1 && i3 == 0) {
                    return i4;
                } else {
                    int j4 = area.a(l1, j2);

                    return this.a(areacontexttransformed, k2, l3, j3, j4);
                }
            }
        }
    }

    protected int a(AreaContextTransformed<?> areacontexttransformed, int i, int j, int k, int l) {
        return j == k && k == l ? j : (i == j && i == k ? i : (i == j && i == l ? i : (i == k && i == l ? i : (i == j && k != l ? i : (i == k && j != l ? i : (i == l && j != k ? i : (j == k && i != l ? j : (j == l && i != k ? j : (k == l && i != j ? k : areacontexttransformed.a(i, j, k, l))))))))));
    }
}
