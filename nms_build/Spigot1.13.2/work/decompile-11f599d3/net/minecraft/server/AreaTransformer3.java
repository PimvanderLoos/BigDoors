package net.minecraft.server;

public interface AreaTransformer3 extends AreaTransformer {

    default <R extends Area> AreaFactory<R> a(AreaContextTransformed<R> areacontexttransformed, AreaFactory<R> areafactory, AreaFactory<R> areafactory1) {
        return (areadimension) -> {
            R r0 = areafactory.make(this.a(areadimension));
            R r1 = areafactory1.make(this.a(areadimension));

            return areacontexttransformed.a(areadimension, (i, j) -> {
                areacontexttransformed.a((long) (i + areadimension.a()), (long) (j + areadimension.b()));
                return this.a((WorldGenContext) areacontexttransformed, areadimension, r0, r1, i, j);
            }, r0, r1);
        };
    }

    int a(WorldGenContext worldgencontext, AreaDimension areadimension, Area area, Area area1, int i, int j);
}
