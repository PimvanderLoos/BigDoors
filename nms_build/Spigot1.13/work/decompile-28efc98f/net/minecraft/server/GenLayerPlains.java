package net.minecraft.server;

public enum GenLayerPlains implements AreaTransformer6 {

    INSTANCE;

    private static final int b = BiomeBase.a(Biomes.c);
    private static final int c = BiomeBase.a(Biomes.ab);

    private GenLayerPlains() {}

    public int a(WorldGenContext worldgencontext, int i) {
        return worldgencontext.a(57) == 0 && i == GenLayerPlains.b ? GenLayerPlains.c : i;
    }
}
