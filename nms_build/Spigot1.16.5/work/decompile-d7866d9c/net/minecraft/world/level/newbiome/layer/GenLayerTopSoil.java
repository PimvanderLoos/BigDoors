package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.WorldGenContext;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer6;

public enum GenLayerTopSoil implements AreaTransformer6 {

    INSTANCE;

    private GenLayerTopSoil() {}

    @Override
    public int a(WorldGenContext worldgencontext, int i) {
        if (GenLayers.b(i)) {
            return i;
        } else {
            int j = worldgencontext.a(6);

            return j == 0 ? 4 : (j == 1 ? 3 : 1);
        }
    }
}
