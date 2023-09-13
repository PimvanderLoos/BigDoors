package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.WorldGenContext;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer7;

public enum GenLayerIcePlains implements AreaTransformer7 {

    INSTANCE;

    private GenLayerIcePlains() {}

    @Override
    public int a(WorldGenContext worldgencontext, int i, int j, int k, int l, int i1) {
        return GenLayers.b(i1) && GenLayers.b(i) && GenLayers.b(j) && GenLayers.b(l) && GenLayers.b(k) && worldgencontext.a(2) == 0 ? 1 : i1;
    }
}
