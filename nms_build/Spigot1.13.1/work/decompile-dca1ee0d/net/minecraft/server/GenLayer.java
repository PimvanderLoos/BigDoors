package net.minecraft.server;

import javax.annotation.Nullable;

public class GenLayer {

    private final AreaFactory<AreaLazy> a;

    public GenLayer(AreaFactory<AreaLazy> areafactory) {
        this.a = areafactory;
    }

    public BiomeBase[] a(int i, int j, int k, int l, @Nullable BiomeBase biomebase) {
        AreaDimension areadimension = new AreaDimension(i, j, k, l);
        AreaLazy arealazy = (AreaLazy) this.a.make(areadimension);
        BiomeBase[] abiomebase = new BiomeBase[k * l];

        for (int i1 = 0; i1 < l; ++i1) {
            for (int j1 = 0; j1 < k; ++j1) {
                abiomebase[j1 + i1 * k] = BiomeBase.getBiome(arealazy.a(j1, i1), biomebase);
            }
        }

        return abiomebase;
    }
}
