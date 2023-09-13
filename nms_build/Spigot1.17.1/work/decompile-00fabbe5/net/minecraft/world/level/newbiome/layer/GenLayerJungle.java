package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.WorldGenContext;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer6;

public enum GenLayerJungle implements AreaTransformer6 {

    INSTANCE;

    private GenLayerJungle() {}

    @Override
    public int a(WorldGenContext worldgencontext, int i) {
        return worldgencontext.a(10) == 0 && i == 21 ? 168 : i;
    }
}
