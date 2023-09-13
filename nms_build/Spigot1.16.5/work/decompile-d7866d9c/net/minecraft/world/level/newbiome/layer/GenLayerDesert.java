package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.WorldGenContext;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer7;

public enum GenLayerDesert implements AreaTransformer7 {

    INSTANCE;

    private GenLayerDesert() {}

    @Override
    public int a(WorldGenContext worldgencontext, int i, int j, int k, int l, int i1) {
        int[] aint = new int[1];

        if (!this.a(aint, i1) && !this.a(aint, i, j, k, l, i1, 38, 37) && !this.a(aint, i, j, k, l, i1, 39, 37) && !this.a(aint, i, j, k, l, i1, 32, 5)) {
            if (i1 == 2 && (i == 12 || j == 12 || l == 12 || k == 12)) {
                return 34;
            } else {
                if (i1 == 6) {
                    if (i == 2 || j == 2 || l == 2 || k == 2 || i == 30 || j == 30 || l == 30 || k == 30 || i == 12 || j == 12 || l == 12 || k == 12) {
                        return 1;
                    }

                    if (i == 21 || k == 21 || j == 21 || l == 21 || i == 168 || k == 168 || j == 168 || l == 168) {
                        return 23;
                    }
                }

                return i1;
            }
        } else {
            return aint[0];
        }
    }

    private boolean a(int[] aint, int i) {
        if (!GenLayers.a(i, 3)) {
            return false;
        } else {
            aint[0] = i;
            return true;
        }
    }

    private boolean a(int[] aint, int i, int j, int k, int l, int i1, int j1, int k1) {
        if (i1 != j1) {
            return false;
        } else {
            if (GenLayers.a(i, j1) && GenLayers.a(j, j1) && GenLayers.a(l, j1) && GenLayers.a(k, j1)) {
                aint[0] = i1;
            } else {
                aint[0] = k1;
            }

            return true;
        }
    }
}
