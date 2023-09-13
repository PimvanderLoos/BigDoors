package net.minecraft.server;

public enum GenLayerRiver implements AreaTransformer7 {

    INSTANCE;

    public static final int b = IRegistry.BIOME.a((Object) Biomes.RIVER);

    private GenLayerRiver() {}

    public int a(WorldGenContext worldgencontext, int i, int j, int k, int l, int i1) {
        int j1 = a(i1);

        return j1 == a(l) && j1 == a(i) && j1 == a(j) && j1 == a(k) ? -1 : GenLayerRiver.b;
    }

    private static int a(int i) {
        return i >= 2 ? 2 + (i & 1) : i;
    }
}
