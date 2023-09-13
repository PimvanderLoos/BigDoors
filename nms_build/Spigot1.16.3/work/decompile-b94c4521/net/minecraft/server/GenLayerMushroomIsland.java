package net.minecraft.server;

public enum GenLayerMushroomIsland implements AreaTransformer4 {

    INSTANCE;

    private GenLayerMushroomIsland() {}

    @Override
    public int a(WorldGenContext worldgencontext, int i, int j, int k, int l, int i1) {
        return GenLayers.b(i1) && GenLayers.b(l) && GenLayers.b(i) && GenLayers.b(k) && GenLayers.b(j) && worldgencontext.a(100) == 0 ? 14 : i1;
    }
}
