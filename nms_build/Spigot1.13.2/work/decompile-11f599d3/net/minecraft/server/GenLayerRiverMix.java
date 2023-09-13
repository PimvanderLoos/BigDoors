package net.minecraft.server;

public enum GenLayerRiverMix implements AreaTransformer3, AreaTransformerIdentity {

    INSTANCE;

    private static final int b = IRegistry.BIOME.a((Object) Biomes.FROZEN_RIVER);
    private static final int c = IRegistry.BIOME.a((Object) Biomes.SNOWY_TUNDRA);
    private static final int d = IRegistry.BIOME.a((Object) Biomes.MUSHROOM_FIELDS);
    private static final int e = IRegistry.BIOME.a((Object) Biomes.MUSHROOM_FIELD_SHORE);
    private static final int f = IRegistry.BIOME.a((Object) Biomes.RIVER);

    private GenLayerRiverMix() {}

    public int a(WorldGenContext worldgencontext, AreaDimension areadimension, Area area, Area area1, int i, int j) {
        int k = area.a(i, j);
        int l = area1.a(i, j);

        return GenLayers.a(k) ? k : (l == GenLayerRiverMix.f ? (k == GenLayerRiverMix.c ? GenLayerRiverMix.b : (k != GenLayerRiverMix.d && k != GenLayerRiverMix.e ? l & 255 : GenLayerRiverMix.e)) : k);
    }
}
