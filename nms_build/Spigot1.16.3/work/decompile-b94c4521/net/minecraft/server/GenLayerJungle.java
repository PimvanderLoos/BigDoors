package net.minecraft.server;

public enum GenLayerJungle implements AreaTransformer6 {

    INSTANCE;

    private GenLayerJungle() {}

    @Override
    public int a(WorldGenContext worldgencontext, int i) {
        return worldgencontext.a(10) == 0 && i == 21 ? 168 : i;
    }
}
