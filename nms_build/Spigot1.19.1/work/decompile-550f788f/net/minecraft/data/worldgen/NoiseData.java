package net.minecraft.data.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;

public class NoiseData {

    public NoiseData() {}

    public static Holder<NoiseGeneratorNormal.a> bootstrap(IRegistry<NoiseGeneratorNormal.a> iregistry) {
        registerBiomeNoises(iregistry, 0, Noises.TEMPERATURE, Noises.VEGETATION, Noises.CONTINENTALNESS, Noises.EROSION);
        registerBiomeNoises(iregistry, -2, Noises.TEMPERATURE_LARGE, Noises.VEGETATION_LARGE, Noises.CONTINENTALNESS_LARGE, Noises.EROSION_LARGE);
        register(iregistry, Noises.RIDGE, -7, 1.0D, 2.0D, 1.0D, 0.0D, 0.0D, 0.0D);
        register(iregistry, Noises.SHIFT, -3, 1.0D, 1.0D, 1.0D, 0.0D);
        register(iregistry, Noises.AQUIFER_BARRIER, -3, 1.0D);
        register(iregistry, Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS, -7, 1.0D);
        register(iregistry, Noises.AQUIFER_LAVA, -1, 1.0D);
        register(iregistry, Noises.AQUIFER_FLUID_LEVEL_SPREAD, -5, 1.0D);
        register(iregistry, Noises.PILLAR, -7, 1.0D, 1.0D);
        register(iregistry, Noises.PILLAR_RARENESS, -8, 1.0D);
        register(iregistry, Noises.PILLAR_THICKNESS, -8, 1.0D);
        register(iregistry, Noises.SPAGHETTI_2D, -7, 1.0D);
        register(iregistry, Noises.SPAGHETTI_2D_ELEVATION, -8, 1.0D);
        register(iregistry, Noises.SPAGHETTI_2D_MODULATOR, -11, 1.0D);
        register(iregistry, Noises.SPAGHETTI_2D_THICKNESS, -11, 1.0D);
        register(iregistry, Noises.SPAGHETTI_3D_1, -7, 1.0D);
        register(iregistry, Noises.SPAGHETTI_3D_2, -7, 1.0D);
        register(iregistry, Noises.SPAGHETTI_3D_RARITY, -11, 1.0D);
        register(iregistry, Noises.SPAGHETTI_3D_THICKNESS, -8, 1.0D);
        register(iregistry, Noises.SPAGHETTI_ROUGHNESS, -5, 1.0D);
        register(iregistry, Noises.SPAGHETTI_ROUGHNESS_MODULATOR, -8, 1.0D);
        register(iregistry, Noises.CAVE_ENTRANCE, -7, 0.4D, 0.5D, 1.0D);
        register(iregistry, Noises.CAVE_LAYER, -8, 1.0D);
        register(iregistry, Noises.CAVE_CHEESE, -8, 0.5D, 1.0D, 2.0D, 1.0D, 2.0D, 1.0D, 0.0D, 2.0D, 0.0D);
        register(iregistry, Noises.ORE_VEININESS, -8, 1.0D);
        register(iregistry, Noises.ORE_VEIN_A, -7, 1.0D);
        register(iregistry, Noises.ORE_VEIN_B, -7, 1.0D);
        register(iregistry, Noises.ORE_GAP, -5, 1.0D);
        register(iregistry, Noises.NOODLE, -8, 1.0D);
        register(iregistry, Noises.NOODLE_THICKNESS, -8, 1.0D);
        register(iregistry, Noises.NOODLE_RIDGE_A, -7, 1.0D);
        register(iregistry, Noises.NOODLE_RIDGE_B, -7, 1.0D);
        register(iregistry, Noises.JAGGED, -16, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D);
        register(iregistry, Noises.SURFACE, -6, 1.0D, 1.0D, 1.0D);
        register(iregistry, Noises.SURFACE_SECONDARY, -6, 1.0D, 1.0D, 0.0D, 1.0D);
        register(iregistry, Noises.CLAY_BANDS_OFFSET, -8, 1.0D);
        register(iregistry, Noises.BADLANDS_PILLAR, -2, 1.0D, 1.0D, 1.0D, 1.0D);
        register(iregistry, Noises.BADLANDS_PILLAR_ROOF, -8, 1.0D);
        register(iregistry, Noises.BADLANDS_SURFACE, -6, 1.0D, 1.0D, 1.0D);
        register(iregistry, Noises.ICEBERG_PILLAR, -6, 1.0D, 1.0D, 1.0D, 1.0D);
        register(iregistry, Noises.ICEBERG_PILLAR_ROOF, -3, 1.0D);
        register(iregistry, Noises.ICEBERG_SURFACE, -6, 1.0D, 1.0D, 1.0D);
        register(iregistry, Noises.SWAMP, -2, 1.0D);
        register(iregistry, Noises.CALCITE, -9, 1.0D, 1.0D, 1.0D, 1.0D);
        register(iregistry, Noises.GRAVEL, -8, 1.0D, 1.0D, 1.0D, 1.0D);
        register(iregistry, Noises.POWDER_SNOW, -6, 1.0D, 1.0D, 1.0D, 1.0D);
        register(iregistry, Noises.PACKED_ICE, -7, 1.0D, 1.0D, 1.0D, 1.0D);
        register(iregistry, Noises.ICE, -4, 1.0D, 1.0D, 1.0D, 1.0D);
        register(iregistry, Noises.SOUL_SAND_LAYER, -8, 1.0D, 1.0D, 1.0D, 1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.013333333333333334D);
        register(iregistry, Noises.GRAVEL_LAYER, -8, 1.0D, 1.0D, 1.0D, 1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.013333333333333334D);
        register(iregistry, Noises.PATCH, -5, 1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.013333333333333334D);
        register(iregistry, Noises.NETHERRACK, -3, 1.0D, 0.0D, 0.0D, 0.35D);
        register(iregistry, Noises.NETHER_WART, -3, 1.0D, 0.0D, 0.0D, 0.9D);
        return register(iregistry, Noises.NETHER_STATE_SELECTOR, -4, 1.0D);
    }

    private static void registerBiomeNoises(IRegistry<NoiseGeneratorNormal.a> iregistry, int i, ResourceKey<NoiseGeneratorNormal.a> resourcekey, ResourceKey<NoiseGeneratorNormal.a> resourcekey1, ResourceKey<NoiseGeneratorNormal.a> resourcekey2, ResourceKey<NoiseGeneratorNormal.a> resourcekey3) {
        register(iregistry, resourcekey, -10 + i, 1.5D, 0.0D, 1.0D, 0.0D, 0.0D, 0.0D);
        register(iregistry, resourcekey1, -8 + i, 1.0D, 1.0D, 0.0D, 0.0D, 0.0D, 0.0D);
        register(iregistry, resourcekey2, -9 + i, 1.0D, 1.0D, 2.0D, 2.0D, 2.0D, 1.0D, 1.0D, 1.0D, 1.0D);
        register(iregistry, resourcekey3, -9 + i, 1.0D, 1.0D, 0.0D, 1.0D, 1.0D);
    }

    private static Holder<NoiseGeneratorNormal.a> register(IRegistry<NoiseGeneratorNormal.a> iregistry, ResourceKey<NoiseGeneratorNormal.a> resourcekey, int i, double d0, double... adouble) {
        return RegistryGeneration.register(iregistry, resourcekey, new NoiseGeneratorNormal.a(i, d0, adouble));
    }
}
