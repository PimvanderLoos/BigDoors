package net.minecraft.world.level.newbiome.layer.traits;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.AreaContextTransformed;
import net.minecraft.world.level.newbiome.context.WorldGenContext;

public interface AreaTransformer7 extends AreaTransformer2, AreaTransformerOffset1 {

    int a(WorldGenContext worldgencontext, int i, int j, int k, int l, int i1);

    @Override
    default int a(AreaContextTransformed<?> areacontexttransformed, Area area, int i, int j) {
        return this.a(areacontexttransformed, area.a(this.a(i + 1), this.b(j + 0)), area.a(this.a(i + 2), this.b(j + 1)), area.a(this.a(i + 1), this.b(j + 2)), area.a(this.a(i + 0), this.b(j + 1)), area.a(this.a(i + 1), this.b(j + 1)));
    }
}
