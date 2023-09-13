package net.minecraft.server;

public enum GenLayerRiverMix implements AreaTransformer3, AreaTransformerIdentity {

    INSTANCE;

    private GenLayerRiverMix() {}

    @Override
    public int a(WorldGenContext worldgencontext, Area area, Area area1, int i, int j) {
        int k = area.a(this.a(i), this.b(j));
        int l = area1.a(this.a(i), this.b(j));

        return GenLayers.a(k) ? k : (l == 7 ? (k == 12 ? 11 : (k != 14 && k != 15 ? l & 255 : 15)) : k);
    }
}
