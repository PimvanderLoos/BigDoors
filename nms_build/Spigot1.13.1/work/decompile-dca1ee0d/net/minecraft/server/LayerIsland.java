package net.minecraft.server;

public enum LayerIsland implements AreaTransformer1 {

    INSTANCE;

    private LayerIsland() {}

    public int a(WorldGenContext worldgencontext, AreaDimension areadimension, int i, int j) {
        return i == -areadimension.a() && j == -areadimension.b() && areadimension.a() > -areadimension.c() && areadimension.a() <= 0 && areadimension.b() > -areadimension.d() && areadimension.b() <= 0 ? 1 : (worldgencontext.a(10) == 0 ? 1 : GenLayers.c);
    }
}
