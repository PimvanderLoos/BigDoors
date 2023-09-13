package net.minecraft.server;

public interface AreaContextTransformed<R extends Area> extends WorldGenContext {

    void a(long i, long j);

    R a(AreaDimension areadimension, AreaTransformer8 areatransformer8);

    default R a(AreaDimension areadimension, AreaTransformer8 areatransformer8, R r0) {
        return this.a(areadimension, areatransformer8);
    }

    default R a(AreaDimension areadimension, AreaTransformer8 areatransformer8, R r0, R r1) {
        return this.a(areadimension, areatransformer8);
    }

    int a(int... aint);
}
