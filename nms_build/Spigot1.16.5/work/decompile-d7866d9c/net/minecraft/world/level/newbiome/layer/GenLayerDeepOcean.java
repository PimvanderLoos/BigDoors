package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.WorldGenContext;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer7;

public enum GenLayerDeepOcean implements AreaTransformer7 {

    INSTANCE;

    private GenLayerDeepOcean() {}

    @Override
    public int a(WorldGenContext worldgencontext, int i, int j, int k, int l, int i1) {
        if (GenLayers.b(i1)) {
            int j1 = 0;

            if (GenLayers.b(i)) {
                ++j1;
            }

            if (GenLayers.b(j)) {
                ++j1;
            }

            if (GenLayers.b(l)) {
                ++j1;
            }

            if (GenLayers.b(k)) {
                ++j1;
            }

            if (j1 > 3) {
                if (i1 == 44) {
                    return 47;
                }

                if (i1 == 45) {
                    return 48;
                }

                if (i1 == 0) {
                    return 24;
                }

                if (i1 == 46) {
                    return 49;
                }

                if (i1 == 10) {
                    return 50;
                }

                return 24;
            }
        }

        return i1;
    }
}
