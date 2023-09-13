package net.minecraft.world.level.newbiome.layer;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.SystemUtils;
import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.WorldGenContext;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer3;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformerOffset1;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum GenLayerRegionHills implements AreaTransformer3, AreaTransformerOffset1 {

    INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Int2IntMap c = (Int2IntMap) SystemUtils.a((Object) (new Int2IntOpenHashMap()), (int2intopenhashmap) -> {
        int2intopenhashmap.put(1, 129);
        int2intopenhashmap.put(2, 130);
        int2intopenhashmap.put(3, 131);
        int2intopenhashmap.put(4, 132);
        int2intopenhashmap.put(5, 133);
        int2intopenhashmap.put(6, 134);
        int2intopenhashmap.put(12, 140);
        int2intopenhashmap.put(21, 149);
        int2intopenhashmap.put(23, 151);
        int2intopenhashmap.put(27, 155);
        int2intopenhashmap.put(28, 156);
        int2intopenhashmap.put(29, 157);
        int2intopenhashmap.put(30, 158);
        int2intopenhashmap.put(32, 160);
        int2intopenhashmap.put(33, 161);
        int2intopenhashmap.put(34, 162);
        int2intopenhashmap.put(35, 163);
        int2intopenhashmap.put(36, 164);
        int2intopenhashmap.put(37, 165);
        int2intopenhashmap.put(38, 166);
        int2intopenhashmap.put(39, 167);
    });

    private GenLayerRegionHills() {}

    @Override
    public int a(WorldGenContext worldgencontext, Area area, Area area1, int i, int j) {
        int k = area.a(this.a(i + 1), this.b(j + 1));
        int l = area1.a(this.a(i + 1), this.b(j + 1));

        if (k > 255) {
            GenLayerRegionHills.LOGGER.debug("old! {}", k);
        }

        int i1 = (l - 2) % 29;

        if (!GenLayers.b(k) && l >= 2 && i1 == 1) {
            return GenLayerRegionHills.c.getOrDefault(k, k);
        } else {
            if (worldgencontext.a(3) == 0 || i1 == 0) {
                int j1 = k;

                if (k == 2) {
                    j1 = 17;
                } else if (k == 4) {
                    j1 = 18;
                } else if (k == 27) {
                    j1 = 28;
                } else if (k == 29) {
                    j1 = 1;
                } else if (k == 5) {
                    j1 = 19;
                } else if (k == 32) {
                    j1 = 33;
                } else if (k == 30) {
                    j1 = 31;
                } else if (k == 1) {
                    j1 = worldgencontext.a(3) == 0 ? 18 : 4;
                } else if (k == 12) {
                    j1 = 13;
                } else if (k == 21) {
                    j1 = 22;
                } else if (k == 168) {
                    j1 = 169;
                } else if (k == 0) {
                    j1 = 24;
                } else if (k == 45) {
                    j1 = 48;
                } else if (k == 46) {
                    j1 = 49;
                } else if (k == 10) {
                    j1 = 50;
                } else if (k == 3) {
                    j1 = 34;
                } else if (k == 35) {
                    j1 = 36;
                } else if (GenLayers.a(k, 38)) {
                    j1 = 37;
                } else if ((k == 24 || k == 48 || k == 49 || k == 50) && worldgencontext.a(3) == 0) {
                    j1 = worldgencontext.a(2) == 0 ? 1 : 4;
                }

                if (i1 == 0 && j1 != k) {
                    j1 = GenLayerRegionHills.c.getOrDefault(j1, k);
                }

                if (j1 != k) {
                    int k1 = 0;

                    if (GenLayers.a(area.a(this.a(i + 1), this.b(j + 0)), k)) {
                        ++k1;
                    }

                    if (GenLayers.a(area.a(this.a(i + 2), this.b(j + 1)), k)) {
                        ++k1;
                    }

                    if (GenLayers.a(area.a(this.a(i + 0), this.b(j + 1)), k)) {
                        ++k1;
                    }

                    if (GenLayers.a(area.a(this.a(i + 1), this.b(j + 2)), k)) {
                        ++k1;
                    }

                    if (k1 >= 3) {
                        return j1;
                    }
                }
            }

            return k;
        }
    }
}
