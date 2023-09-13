package net.minecraft.server;

public interface AreaTransformer1 {

    default <R extends Area> AreaFactory<R> a(AreaContextTransformed<R> areacontexttransformed) {
        return (areadimension) -> {
            return areacontexttransformed.a(areadimension, (i, j) -> {
                areacontexttransformed.a((long) (i + areadimension.a()), (long) (j + areadimension.b()));
                return this.a((WorldGenContext) areacontexttransformed, areadimension, i, j);
            });
        };
    }

    int a(WorldGenContext worldgencontext, AreaDimension areadimension, int i, int j);
}
