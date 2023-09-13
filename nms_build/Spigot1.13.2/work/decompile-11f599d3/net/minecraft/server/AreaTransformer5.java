package net.minecraft.server;

public interface AreaTransformer5 extends AreaTransformer2, AreaTransformerIdentity {

    int a(WorldGenContext worldgencontext, int i);

    default int a(AreaContextTransformed<?> areacontexttransformed, AreaDimension areadimension, Area area, int i, int j) {
        return this.a(areacontexttransformed, area.a(i, j));
    }
}
