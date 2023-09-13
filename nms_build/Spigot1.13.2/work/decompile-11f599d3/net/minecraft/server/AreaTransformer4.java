package net.minecraft.server;

public interface AreaTransformer4 extends AreaTransformer2, AreaTransformerOffset1 {

    int a(WorldGenContext worldgencontext, int i, int j, int k, int l, int i1);

    default int a(AreaContextTransformed<?> areacontexttransformed, AreaDimension areadimension, Area area, int i, int j) {
        return this.a(areacontexttransformed, area.a(i + 0, j + 2), area.a(i + 2, j + 2), area.a(i + 2, j + 0), area.a(i + 0, j + 0), area.a(i + 1, j + 1));
    }
}
