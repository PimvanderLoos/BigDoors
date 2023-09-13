package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.WorldGenContext;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer3;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformerIdentity;

public enum GenLayerOcean implements AreaTransformer3, AreaTransformerIdentity {

    INSTANCE;

    private GenLayerOcean() {}

    @Override
    public int a(WorldGenContext worldgencontext, Area area, Area area1, int i, int j) {
        int k = area.a(this.a(i), this.b(j));
        int l = area1.a(this.a(i), this.b(j));

        if (!GenLayers.a(k)) {
            return k;
        } else {
            boolean flag = true;
            boolean flag1 = true;

            for (int i1 = -8; i1 <= 8; i1 += 4) {
                for (int j1 = -8; j1 <= 8; j1 += 4) {
                    int k1 = area.a(this.a(i + i1), this.b(j + j1));

                    if (!GenLayers.a(k1)) {
                        if (l == 44) {
                            return 45;
                        }

                        if (l == 10) {
                            return 46;
                        }
                    }
                }
            }

            if (k == 24) {
                if (l == 45) {
                    return 48;
                }

                if (l == 0) {
                    return 24;
                }

                if (l == 46) {
                    return 49;
                }

                if (l == 10) {
                    return 50;
                }
            }

            return l;
        }
    }
}
