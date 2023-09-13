package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.WorldGenContext;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer7;

public enum GenLayerSmooth implements AreaTransformer7 {

    INSTANCE;

    private GenLayerSmooth() {}

    @Override
    public int a(WorldGenContext worldgencontext, int i, int j, int k, int l, int i1) {
        boolean flag = j == l;
        boolean flag1 = i == k;

        return flag == flag1 ? (flag ? (worldgencontext.a(2) == 0 ? l : i) : i1) : (flag ? l : i);
    }
}
