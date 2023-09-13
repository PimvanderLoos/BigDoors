package net.minecraft.world.level.biome;

public enum GenLayerZoomVoronoiFixed implements GenLayerZoomer {

    INSTANCE;

    private GenLayerZoomVoronoiFixed() {}

    @Override
    public BiomeBase a(long i, int j, int k, int l, BiomeManager.Provider biomemanager_provider) {
        return GenLayerZoomVoronoi.INSTANCE.a(i, j, 0, l, biomemanager_provider);
    }
}
