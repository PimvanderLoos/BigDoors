package net.minecraft.server;

public enum GenLayerPlains implements AreaTransformer6 {

    INSTANCE;

    private static final int b = IRegistry.BIOME.a((Object) Biomes.c);
    private static final int c = IRegistry.BIOME.a((Object) Biomes.ab);

    private GenLayerPlains() {}

    public int a(WorldGenContext worldgencontext, int i) {
        return worldgencontext.a(57) == 0 && i == GenLayerPlains.b ? GenLayerPlains.c : i;
    }
}
