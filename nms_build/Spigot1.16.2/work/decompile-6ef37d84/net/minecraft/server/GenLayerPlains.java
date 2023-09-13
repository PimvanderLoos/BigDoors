package net.minecraft.server;

public enum GenLayerPlains implements AreaTransformer6 {

    INSTANCE;

    private GenLayerPlains() {}

    @Override
    public int a(WorldGenContext worldgencontext, int i) {
        return worldgencontext.a(57) == 0 && i == 1 ? 129 : i;
    }
}
