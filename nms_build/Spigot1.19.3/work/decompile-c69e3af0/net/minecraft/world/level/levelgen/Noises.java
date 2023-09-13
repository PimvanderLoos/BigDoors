package net.minecraft.world.level.levelgen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;

public class Noises {

    public static final ResourceKey<NoiseGeneratorNormal.a> TEMPERATURE = createKey("temperature");
    public static final ResourceKey<NoiseGeneratorNormal.a> VEGETATION = createKey("vegetation");
    public static final ResourceKey<NoiseGeneratorNormal.a> CONTINENTALNESS = createKey("continentalness");
    public static final ResourceKey<NoiseGeneratorNormal.a> EROSION = createKey("erosion");
    public static final ResourceKey<NoiseGeneratorNormal.a> TEMPERATURE_LARGE = createKey("temperature_large");
    public static final ResourceKey<NoiseGeneratorNormal.a> VEGETATION_LARGE = createKey("vegetation_large");
    public static final ResourceKey<NoiseGeneratorNormal.a> CONTINENTALNESS_LARGE = createKey("continentalness_large");
    public static final ResourceKey<NoiseGeneratorNormal.a> EROSION_LARGE = createKey("erosion_large");
    public static final ResourceKey<NoiseGeneratorNormal.a> RIDGE = createKey("ridge");
    public static final ResourceKey<NoiseGeneratorNormal.a> SHIFT = createKey("offset");
    public static final ResourceKey<NoiseGeneratorNormal.a> AQUIFER_BARRIER = createKey("aquifer_barrier");
    public static final ResourceKey<NoiseGeneratorNormal.a> AQUIFER_FLUID_LEVEL_FLOODEDNESS = createKey("aquifer_fluid_level_floodedness");
    public static final ResourceKey<NoiseGeneratorNormal.a> AQUIFER_LAVA = createKey("aquifer_lava");
    public static final ResourceKey<NoiseGeneratorNormal.a> AQUIFER_FLUID_LEVEL_SPREAD = createKey("aquifer_fluid_level_spread");
    public static final ResourceKey<NoiseGeneratorNormal.a> PILLAR = createKey("pillar");
    public static final ResourceKey<NoiseGeneratorNormal.a> PILLAR_RARENESS = createKey("pillar_rareness");
    public static final ResourceKey<NoiseGeneratorNormal.a> PILLAR_THICKNESS = createKey("pillar_thickness");
    public static final ResourceKey<NoiseGeneratorNormal.a> SPAGHETTI_2D = createKey("spaghetti_2d");
    public static final ResourceKey<NoiseGeneratorNormal.a> SPAGHETTI_2D_ELEVATION = createKey("spaghetti_2d_elevation");
    public static final ResourceKey<NoiseGeneratorNormal.a> SPAGHETTI_2D_MODULATOR = createKey("spaghetti_2d_modulator");
    public static final ResourceKey<NoiseGeneratorNormal.a> SPAGHETTI_2D_THICKNESS = createKey("spaghetti_2d_thickness");
    public static final ResourceKey<NoiseGeneratorNormal.a> SPAGHETTI_3D_1 = createKey("spaghetti_3d_1");
    public static final ResourceKey<NoiseGeneratorNormal.a> SPAGHETTI_3D_2 = createKey("spaghetti_3d_2");
    public static final ResourceKey<NoiseGeneratorNormal.a> SPAGHETTI_3D_RARITY = createKey("spaghetti_3d_rarity");
    public static final ResourceKey<NoiseGeneratorNormal.a> SPAGHETTI_3D_THICKNESS = createKey("spaghetti_3d_thickness");
    public static final ResourceKey<NoiseGeneratorNormal.a> SPAGHETTI_ROUGHNESS = createKey("spaghetti_roughness");
    public static final ResourceKey<NoiseGeneratorNormal.a> SPAGHETTI_ROUGHNESS_MODULATOR = createKey("spaghetti_roughness_modulator");
    public static final ResourceKey<NoiseGeneratorNormal.a> CAVE_ENTRANCE = createKey("cave_entrance");
    public static final ResourceKey<NoiseGeneratorNormal.a> CAVE_LAYER = createKey("cave_layer");
    public static final ResourceKey<NoiseGeneratorNormal.a> CAVE_CHEESE = createKey("cave_cheese");
    public static final ResourceKey<NoiseGeneratorNormal.a> ORE_VEININESS = createKey("ore_veininess");
    public static final ResourceKey<NoiseGeneratorNormal.a> ORE_VEIN_A = createKey("ore_vein_a");
    public static final ResourceKey<NoiseGeneratorNormal.a> ORE_VEIN_B = createKey("ore_vein_b");
    public static final ResourceKey<NoiseGeneratorNormal.a> ORE_GAP = createKey("ore_gap");
    public static final ResourceKey<NoiseGeneratorNormal.a> NOODLE = createKey("noodle");
    public static final ResourceKey<NoiseGeneratorNormal.a> NOODLE_THICKNESS = createKey("noodle_thickness");
    public static final ResourceKey<NoiseGeneratorNormal.a> NOODLE_RIDGE_A = createKey("noodle_ridge_a");
    public static final ResourceKey<NoiseGeneratorNormal.a> NOODLE_RIDGE_B = createKey("noodle_ridge_b");
    public static final ResourceKey<NoiseGeneratorNormal.a> JAGGED = createKey("jagged");
    public static final ResourceKey<NoiseGeneratorNormal.a> SURFACE = createKey("surface");
    public static final ResourceKey<NoiseGeneratorNormal.a> SURFACE_SECONDARY = createKey("surface_secondary");
    public static final ResourceKey<NoiseGeneratorNormal.a> CLAY_BANDS_OFFSET = createKey("clay_bands_offset");
    public static final ResourceKey<NoiseGeneratorNormal.a> BADLANDS_PILLAR = createKey("badlands_pillar");
    public static final ResourceKey<NoiseGeneratorNormal.a> BADLANDS_PILLAR_ROOF = createKey("badlands_pillar_roof");
    public static final ResourceKey<NoiseGeneratorNormal.a> BADLANDS_SURFACE = createKey("badlands_surface");
    public static final ResourceKey<NoiseGeneratorNormal.a> ICEBERG_PILLAR = createKey("iceberg_pillar");
    public static final ResourceKey<NoiseGeneratorNormal.a> ICEBERG_PILLAR_ROOF = createKey("iceberg_pillar_roof");
    public static final ResourceKey<NoiseGeneratorNormal.a> ICEBERG_SURFACE = createKey("iceberg_surface");
    public static final ResourceKey<NoiseGeneratorNormal.a> SWAMP = createKey("surface_swamp");
    public static final ResourceKey<NoiseGeneratorNormal.a> CALCITE = createKey("calcite");
    public static final ResourceKey<NoiseGeneratorNormal.a> GRAVEL = createKey("gravel");
    public static final ResourceKey<NoiseGeneratorNormal.a> POWDER_SNOW = createKey("powder_snow");
    public static final ResourceKey<NoiseGeneratorNormal.a> PACKED_ICE = createKey("packed_ice");
    public static final ResourceKey<NoiseGeneratorNormal.a> ICE = createKey("ice");
    public static final ResourceKey<NoiseGeneratorNormal.a> SOUL_SAND_LAYER = createKey("soul_sand_layer");
    public static final ResourceKey<NoiseGeneratorNormal.a> GRAVEL_LAYER = createKey("gravel_layer");
    public static final ResourceKey<NoiseGeneratorNormal.a> PATCH = createKey("patch");
    public static final ResourceKey<NoiseGeneratorNormal.a> NETHERRACK = createKey("netherrack");
    public static final ResourceKey<NoiseGeneratorNormal.a> NETHER_WART = createKey("nether_wart");
    public static final ResourceKey<NoiseGeneratorNormal.a> NETHER_STATE_SELECTOR = createKey("nether_state_selector");

    public Noises() {}

    private static ResourceKey<NoiseGeneratorNormal.a> createKey(String s) {
        return ResourceKey.create(Registries.NOISE, new MinecraftKey(s));
    }

    public static NoiseGeneratorNormal instantiate(HolderGetter<NoiseGeneratorNormal.a> holdergetter, PositionalRandomFactory positionalrandomfactory, ResourceKey<NoiseGeneratorNormal.a> resourcekey) {
        Holder<NoiseGeneratorNormal.a> holder = holdergetter.getOrThrow(resourcekey);

        return NoiseGeneratorNormal.create(positionalrandomfactory.fromHashOf(((ResourceKey) holder.unwrapKey().orElseThrow()).location()), (NoiseGeneratorNormal.a) holder.value());
    }
}
