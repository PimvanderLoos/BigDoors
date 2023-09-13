package net.minecraft.world.level.newbiome.layer.traits;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.area.AreaFactory;
import net.minecraft.world.level.newbiome.context.AreaContextTransformed;
import net.minecraft.world.level.newbiome.context.WorldGenContext;

public interface AreaTransformer3 extends AreaTransformer {

    default <R extends Area> AreaFactory<R> a(AreaContextTransformed<R> areacontexttransformed, AreaFactory<R> areafactory, AreaFactory<R> areafactory1) {
        return () -> {
            R r0 = areafactory.make();
            R r1 = areafactory1.make();

            return areacontexttransformed.a((i, j) -> {
                areacontexttransformed.a((long) i, (long) j);
                return this.a((WorldGenContext) areacontexttransformed, r0, r1, i, j);
            }, r0, r1);
        };
    }

    int a(WorldGenContext worldgencontext, Area area, Area area1, int i, int j);
}
