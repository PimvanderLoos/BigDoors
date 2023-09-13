package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.WorldGenContext;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer5;

public class GenLayerBiome implements AreaTransformer5 {

    private static final int[] LEGACY_WARM_BIOMES = new int[]{2, 4, 3, 6, 1, 5};
    private static final int[] WARM_BIOMES = new int[]{2, 2, 2, 35, 35, 1};
    private static final int[] MEDIUM_BIOMES = new int[]{4, 29, 3, 1, 27, 6};
    private static final int[] COLD_BIOMES = new int[]{4, 3, 5, 1};
    private static final int[] ICE_BIOMES = new int[]{12, 12, 12, 30};
    private int[] warmBiomes;

    public GenLayerBiome(boolean flag) {
        this.warmBiomes = GenLayerBiome.WARM_BIOMES;
        if (flag) {
            this.warmBiomes = GenLayerBiome.LEGACY_WARM_BIOMES;
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

                    return this.warmBiomes[worldgencontext.a(this.warmBiomes.length)];
                case 2:
                    if (j > 0) {
                        return 21;
                    }

                    return GenLayerBiome.MEDIUM_BIOMES[worldgencontext.a(GenLayerBiome.MEDIUM_BIOMES.length)];
                case 3:
                    if (j > 0) {
                        return 32;
                    }

                    return GenLayerBiome.COLD_BIOMES[worldgencontext.a(GenLayerBiome.COLD_BIOMES.length)];
                case 4:
                    return GenLayerBiome.ICE_BIOMES[worldgencontext.a(GenLayerBiome.ICE_BIOMES.length)];
                default:
                    return 14;
            }
        } else {
            return i;
        }
    }
}
