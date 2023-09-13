package net.minecraft.world.level.newbiome.layer.traits;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.area.AreaFactory;
import net.minecraft.world.level.newbiome.context.AreaContextTransformed;

public interface AreaTransformer2 extends AreaTransformer {

    default <R extends Area> AreaFactory<R> a(AreaContextTransformed<R> areacontexttransformed, AreaFactory<R> areafactory) {
        return () -> {
            R r0 = areafactory.make();

            return areacontexttransformed.a((i, j) -> {
                areacontexttransformed.a((long) i, (long) j);
                return this.a(areacontexttransformed, r0, i, j);
            }, r0);
        };
    }

    int a(AreaContextTransformed<?> areacontexttransformed, Area area, int i, int j);
}
