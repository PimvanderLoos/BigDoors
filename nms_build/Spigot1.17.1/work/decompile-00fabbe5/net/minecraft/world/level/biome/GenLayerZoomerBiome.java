package net.minecraft.world.level.biome;

import net.minecraft.core.QuartPos;

public enum GenLayerZoomerBiome implements GenLayerZoomer {

    INSTANCE;

    private GenLayerZoomerBiome() {}

    @Override
    public BiomeBase a(long i, int j, int k, int l, BiomeManager.Provider biomemanager_provider) {
        return biomemanager_provider.getBiome(QuartPos.a(j), QuartPos.a(k), QuartPos.a(l));
    }
}
