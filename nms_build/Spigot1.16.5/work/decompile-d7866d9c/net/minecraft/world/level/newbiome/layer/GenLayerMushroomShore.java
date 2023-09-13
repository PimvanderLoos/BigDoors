package net.minecraft.world.level.newbiome.layer;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.world.level.newbiome.context.WorldGenContext;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer7;

public enum GenLayerMushroomShore implements AreaTransformer7 {

    INSTANCE;

    private static final IntSet b = new IntOpenHashSet(new int[]{26, 11, 12, 13, 140, 30, 31, 158, 10});
    private static final IntSet c = new IntOpenHashSet(new int[]{168, 169, 21, 22, 23, 149, 151});

    private GenLayerMushroomShore() {}

    @Override
    public int a(WorldGenContext worldgencontext, int i, int j, int k, int l, int i1) {
        if (i1 == 14) {
            if (GenLayers.b(i) || GenLayers.b(j) || GenLayers.b(k) || GenLayers.b(l)) {
                return 15;
            }
        } else if (GenLayerMushroomShore.c.contains(i1)) {
            if (!c(i) || !c(j) || !c(k) || !c(l)) {
                return 23;
            }

            if (GenLayers.a(i) || GenLayers.a(j) || GenLayers.a(k) || GenLayers.a(l)) {
                return 16;
            }
        } else if (i1 != 3 && i1 != 34 && i1 != 20) {
            if (GenLayerMushroomShore.b.contains(i1)) {
                if (!GenLayers.a(i1) && (GenLayers.a(i) || GenLayers.a(j) || GenLayers.a(k) || GenLayers.a(l))) {
                    return 26;
                }
            } else if (i1 != 37 && i1 != 38) {
                if (!GenLayers.a(i1) && i1 != 7 && i1 != 6 && (GenLayers.a(i) || GenLayers.a(j) || GenLayers.a(k) || GenLayers.a(l))) {
                    return 16;
                }
            } else if (!GenLayers.a(i) && !GenLayers.a(j) && !GenLayers.a(k) && !GenLayers.a(l) && (!this.d(i) || !this.d(j) || !this.d(k) || !this.d(l))) {
                return 2;
            }
        } else if (!GenLayers.a(i1) && (GenLayers.a(i) || GenLayers.a(j) || GenLayers.a(k) || GenLayers.a(l))) {
            return 25;
        }

        return i1;
    }

    private static boolean c(int i) {
        return GenLayerMushroomShore.c.contains(i) || i == 4 || i == 5 || GenLayers.a(i);
    }

    private boolean d(int i) {
        return i == 37 || i == 38 || i == 39 || i == 165 || i == 166 || i == 167;
    }
}
