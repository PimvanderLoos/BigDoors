package net.minecraft.world.level.biome;

public interface BiomeResolver {

    BiomeBase getNoiseBiome(int i, int j, int k, Climate.Sampler climate_sampler);
}
