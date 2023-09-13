package net.minecraft.server;

public enum GenLayerOceanEdge implements AreaTransformer1 {

    INSTANCE;

    private GenLayerOceanEdge() {}

    public int a(WorldGenContext worldgencontext, AreaDimension areadimension, int i, int j) {
        NoiseGeneratorPerlin noisegeneratorperlin = worldgencontext.a();
        double d0 = noisegeneratorperlin.a((double) (i + areadimension.a()) / 8.0D, (double) (j + areadimension.b()) / 8.0D);

        return d0 > 0.4D ? GenLayers.a : (d0 > 0.2D ? GenLayers.b : (d0 < -0.4D ? GenLayers.e : (d0 < -0.2D ? GenLayers.d : GenLayers.c)));
    }
}
