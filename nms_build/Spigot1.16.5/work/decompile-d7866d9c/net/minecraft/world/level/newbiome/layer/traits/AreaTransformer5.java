package net.minecraft.world.level.newbiome.layer.traits;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.AreaContextTransformed;
import net.minecraft.world.level.newbiome.context.WorldGenContext;

public interface AreaTransformer5 extends AreaTransformer2, AreaTransformerIdentity {

    int a(WorldGenContext worldgencontext, int i);

    @Override
    default int a(AreaContextTransformed<?> areacontexttransformed, Area area, int i, int j) {
        return this.a(areacontexttransformed, area.a(this.a(i), this.b(j)));
    }
}
