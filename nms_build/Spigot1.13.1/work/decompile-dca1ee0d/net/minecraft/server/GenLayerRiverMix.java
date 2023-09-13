package net.minecraft.server;

public enum GenLayerRiverMix implements AreaTransformer3, AreaTransformerIdentity {

    INSTANCE;

    private static final int b = IRegistry.BIOME.a((Object) Biomes.m);
    private static final int c = IRegistry.BIOME.a((Object) Biomes.n);
    private static final int d = IRegistry.BIOME.a((Object) Biomes.p);
    private static final int e = IRegistry.BIOME.a((Object) Biomes.q);
    private static final int f = IRegistry.BIOME.a((Object) Biomes.i);

    private GenLayerRiverMix() {}

    public int a(WorldGenContext worldgencontext, AreaDimension areadimension, Area area, Area area1, int i, int j) {
        int k = area.a(i, j);
        int l = area1.a(i, j);

        return GenLayers.a(k) ? k : (l == GenLayerRiverMix.f ? (k == GenLayerRiverMix.c ? GenLayerRiverMix.b : (k != GenLayerRiverMix.d && k != GenLayerRiverMix.e ? l & 255 : GenLayerRiverMix.e)) : k);
    }
}
