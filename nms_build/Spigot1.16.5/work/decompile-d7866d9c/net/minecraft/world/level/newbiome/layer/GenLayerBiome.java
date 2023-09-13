package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.WorldGenContext;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer5;

public class GenLayerBiome implements AreaTransformer5 {

    private static final int[] a = new int[]{2, 4, 3, 6, 1, 5};
    private static final int[] b = new int[]{2, 2, 2, 35, 35, 1};
    private static final int[] c = new int[]{4, 29, 3, 1, 27, 6};
    private static final int[] d = new int[]{4, 3, 5, 1};
    private static final int[] e = new int[]{12, 12, 12, 30};
    private int[] f;

    public GenLayerBiome(boolean flag) {
        this.f = GenLayerBiome.b;
        if (flag) {
            this.f = GenLayerBiome.a;
        }

    }

    @Override
    public int a(WorldGenContext worldgencontext, int i) {
        int j = (i & 3840) >> 8;

        i &= -3841;
        if (!GenLayers.a(i) && i != 14) {
            switch (i) {
                case 1:
                    if (j > 0) {
                        return worldgencontext.a(3) == 0 ? 39 : 38;
                    }

                    return this.f[worldgencontext.a(this.f.length)];
                case 2:
                    if (j > 0) {
                        return 21;
                    }

                    return GenLayerBiome.c[worldgencontext.a(GenLayerBiome.c.length)];
                case 3:
                    if (j > 0) {
                        return 32;
                    }

                    return GenLayerBiome.d[worldgencontext.a(GenLayerBiome.d.length)];
                case 4:
                    return GenLayerBiome.e[worldgencontext.a(GenLayerBiome.e.length)];
                default:
                    return 14;
            }
        } else {
            return i;
        }
    }
}
