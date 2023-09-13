package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.WorldGenContext;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer5;

public enum GenLayerCleaner implements AreaTransformer5 {

    INSTANCE;

    private GenLayerCleaner() {}

    @Override
    public int a(WorldGenContext worldgencontext, int i) {
        return GenLayers.b(i) ? i : worldgencontext.a(299999) + 2;
    }
}
