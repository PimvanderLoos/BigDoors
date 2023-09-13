package net.minecraft.world.level.biome;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;

public abstract class Biomes {

    public static final ResourceKey<BiomeBase> THE_VOID = register("the_void");
    public static final ResourceKey<BiomeBase> PLAINS = register("plains");
    public static final ResourceKey<BiomeBase> SUNFLOWER_PLAINS = register("sunflower_plains");
    public static final ResourceKey<BiomeBase> SNOWY_PLAINS = register("snowy_plains");
    public static final ResourceKey<BiomeBase> ICE_SPIKES = register("ice_spikes");
    public static final ResourceKey<BiomeBase> DESERT = register("desert");
    public static final ResourceKey<BiomeBase> SWAMP = register("swamp");
    public static final ResourceKey<BiomeBase> MANGROVE_SWAMP = register("mangrove_swamp");
    public static final ResourceKey<BiomeBase> FOREST = register("forest");
    public static final ResourceKey<BiomeBase> FLOWER_FOREST = register("flower_forest");
    public static final ResourceKey<BiomeBase> BIRCH_FOREST = register("birch_forest");
    public static final ResourceKey<BiomeBase> DARK_FOREST = register("dark_forest");
    public static final ResourceKey<BiomeBase> OLD_GROWTH_BIRCH_FOREST = register("old_growth_birch_forest");
    public static final ResourceKey<BiomeBase> OLD_GROWTH_PINE_TAIGA = register("old_growth_pine_taiga");
    public static final ResourceKey<BiomeBase> OLD_GROWTH_SPRUCE_TAIGA = register("old_growth_spruce_taiga");
    public static final ResourceKey<BiomeBase> TAIGA = register("taiga");
    public static final ResourceKey<BiomeBase> SNOWY_TAIGA = register("snowy_taiga");
    public static final ResourceKey<BiomeBase> SAVANNA = register("savanna");
    public static final ResourceKey<BiomeBase> SAVANNA_PLATEAU = register("savanna_plateau");
    public static final ResourceKey<BiomeBase> WINDSWEPT_HILLS = register("windswept_hills");
    public static final ResourceKey<BiomeBase> WINDSWEPT_GRAVELLY_HILLS = register("windswept_gravelly_hills");
    public static final ResourceKey<BiomeBase> WINDSWEPT_FOREST = register("windswept_forest");
    public static final ResourceKey<BiomeBase> WINDSWEPT_SAVANNA = register("windswept_savanna");
    public static final ResourceKey<BiomeBase> JUNGLE = register("jungle");
    public static final ResourceKey<BiomeBase> SPARSE_JUNGLE = register("sparse_jungle");
    public static final ResourceKey<BiomeBase> BAMBOO_JUNGLE = register("bamboo_jungle");
    public static final ResourceKey<BiomeBase> BADLANDS = register("badlands");
    public static final ResourceKey<BiomeBase> ERODED_BADLANDS = register("eroded_badlands");
    public static final ResourceKey<BiomeBase> WOODED_BADLANDS = register("wooded_badlands");
    public static final ResourceKey<BiomeBase> MEADOW = register("meadow");
    public static final ResourceKey<BiomeBase> CHERRY_GROVE = register("cherry_grove");
    public static final ResourceKey<BiomeBase> GROVE = register("grove");
    public static final ResourceKey<BiomeBase> SNOWY_SLOPES = register("snowy_slopes");
    public static final ResourceKey<BiomeBase> FROZEN_PEAKS = register("frozen_peaks");
    public static final ResourceKey<BiomeBase> JAGGED_PEAKS = register("jagged_peaks");
    public static final ResourceKey<BiomeBase> STONY_PEAKS = register("stony_peaks");
    public static final ResourceKey<BiomeBase> RIVER = register("river");
    public static final ResourceKey<BiomeBase> FROZEN_RIVER = register("frozen_river");
    public static final ResourceKey<BiomeBase> BEACH = register("beach");
    public static final ResourceKey<BiomeBase> SNOWY_BEACH = register("snowy_beach");
    public static final ResourceKey<BiomeBase> STONY_SHORE = register("stony_shore");
    public static final ResourceKey<BiomeBase> WARM_OCEAN = register("warm_ocean");
    public static final ResourceKey<BiomeBase> LUKEWARM_OCEAN = register("lukewarm_ocean");
    public static final ResourceKey<BiomeBase> DEEP_LUKEWARM_OCEAN = register("deep_lukewarm_ocean");
    public static final ResourceKey<BiomeBase> OCEAN = register("ocean");
    public static final ResourceKey<BiomeBase> DEEP_OCEAN = register("deep_ocean");
    public static final ResourceKey<BiomeBase> COLD_OCEAN = register("cold_ocean");
    public static final ResourceKey<BiomeBase> DEEP_COLD_OCEAN = register("deep_cold_ocean");
    public static final ResourceKey<BiomeBase> FROZEN_OCEAN = register("frozen_ocean");
    public static final ResourceKey<BiomeBase> DEEP_FROZEN_OCEAN = register("deep_frozen_ocean");
    public static final ResourceKey<BiomeBase> MUSHROOM_FIELDS = register("mushroom_fields");
    public static final ResourceKey<BiomeBase> DRIPSTONE_CAVES = register("dripstone_caves");
    public static final ResourceKey<BiomeBase> LUSH_CAVES = register("lush_caves");
    public static final ResourceKey<BiomeBase> DEEP_DARK = register("deep_dark");
    public static final ResourceKey<BiomeBase> NETHER_WASTES = register("nether_wastes");
    public static final ResourceKey<BiomeBase> WARPED_FOREST = register("warped_forest");
    public static final ResourceKey<BiomeBase> CRIMSON_FOREST = register("crimson_forest");
    public static final ResourceKey<BiomeBase> SOUL_SAND_VALLEY = register("soul_sand_valley");
    public static final ResourceKey<BiomeBase> BASALT_DELTAS = register("basalt_deltas");
    public static final ResourceKey<BiomeBase> THE_END = register("the_end");
    public static final ResourceKey<BiomeBase> END_HIGHLANDS = register("end_highlands");
    public static final ResourceKey<BiomeBase> END_MIDLANDS = register("end_midlands");
    public static final ResourceKey<BiomeBase> SMALL_END_ISLANDS = register("small_end_islands");
    public static final ResourceKey<BiomeBase> END_BARRENS = register("end_barrens");

    public Biomes() {}

    private static ResourceKey<BiomeBase> register(String s) {
        return ResourceKey.create(Registries.BIOME, new MinecraftKey(s));
    }
}
