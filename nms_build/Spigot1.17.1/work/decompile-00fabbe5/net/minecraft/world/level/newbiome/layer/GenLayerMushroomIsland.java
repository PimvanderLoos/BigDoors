package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.WorldGenContext;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer4;

public enum GenLayerMushroomIsland implements AreaTransformer4 {

    INSTANCE;

    private GenLayerMushroomIsland() {}

    @Override
    public int a(WorldGenContext worldgencontext, int i, int j, int k, int l, int i1) {
        return GenLayers.b(i1) && GenLayers.b(l) && GenLayers.b(i) && GenLayers.b(k) && GenLayers.b(j) && worldgencontext.a(100) == 0 ? 14 : i1;
    }
}
