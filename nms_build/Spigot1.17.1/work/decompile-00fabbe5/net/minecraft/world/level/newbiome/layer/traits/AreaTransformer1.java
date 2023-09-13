package net.minecraft.world.level.newbiome.layer.traits;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.area.AreaFactory;
import net.minecraft.world.level.newbiome.context.AreaContextTransformed;
import net.minecraft.world.level.newbiome.context.WorldGenContext;

public interface AreaTransformer1 {

    default <R extends Area> AreaFactory<R> a(AreaContextTransformed<R> areacontexttransformed) {
        return () -> {
            return areacontexttransformed.a((i, j) -> {
                areacontexttransformed.a((long) i, (long) j);
                return this.a((WorldGenContext) areacontexttransformed, i, j);
            });
        };
    }

    int a(WorldGenContext worldgencontext, int i, int j);
}
