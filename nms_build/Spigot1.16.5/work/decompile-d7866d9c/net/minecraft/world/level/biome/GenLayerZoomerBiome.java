package net.minecraft.world.level.biome;

public enum GenLayerZoomerBiome implements GenLayerZoomer {

    INSTANCE;

    private GenLayerZoomerBiome() {}

    @Override
    public BiomeBase a(long i, int j, int k, int l, BiomeManager.Provider biomemanager_provider) {
        return biomemanager_provider.getBiome(j >> 2, k >> 2, l >> 2);
    }
}
