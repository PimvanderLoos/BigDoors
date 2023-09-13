package net.minecraft.server;

public interface AreaTransformer6 extends AreaTransformer2, AreaTransformerOffset1 {

    int a(WorldGenContext worldgencontext, int i);

    default int a(AreaContextTransformed<?> areacontexttransformed, AreaDimension areadimension, Area area, int i, int j) {
        int k = area.a(i + 1, j + 1);

        return this.a(areacontexttransformed, k);
    }
}
