package net.minecraft.world.level.newbiome.layer.traits;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.AreaContextTransformed;
import net.minecraft.world.level.newbiome.context.WorldGenContext;

public interface AreaTransformer6 extends AreaTransformer2, AreaTransformerOffset1 {

    int a(WorldGenContext worldgencontext, int i);

    @Override
    default int a(AreaContextTransformed<?> areacontexttransformed, Area area, int i, int j) {
        int k = area.a(this.a(i + 1), this.b(j + 1));

        return this.a(areacontexttransformed, k);
    }
}
