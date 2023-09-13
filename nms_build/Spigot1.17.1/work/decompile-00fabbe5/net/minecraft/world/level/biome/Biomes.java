package net.minecraft.world.level.biome;

import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;

public abstract class Biomes {

    public static final ResourceKey<BiomeBase> OCEAN = a("ocean");
    public static final ResourceKey<BiomeBase> PLAINS = a("plains");
    public static final ResourceKey<BiomeBase> DESERT = a("desert");
    public static final ResourceKey<BiomeBase> MOUNTAINS = a("mountains");
    public static final ResourceKey<BiomeBase> FOREST = a("forest");
    public static final ResourceKey<BiomeBase> TAIGA = a("taiga");
    public static final ResourceKey<BiomeBase> SWAMP = a("swamp");
    public static final ResourceKey<BiomeBase> RIVER = a("river");
    public static final ResourceKey<BiomeBase> NETHER_WASTES = a("nether_wastes");
    public static final ResourceKey<BiomeBase> THE_END = a("the_end");
    public static final ResourceKey<BiomeBase> FROZEN_OCEAN = a("frozen_ocean");
    public static final ResourceKey<BiomeBase> FROZEN_RIVER = a("frozen_river");
    public static final ResourceKey<BiomeBase> SNOWY_TUNDRA = a("snowy_tundra");
    public static final ResourceKey<BiomeBase> SNOWY_MOUNTAINS = a("snowy_mountains");
    public static final ResourceKey<BiomeBase> MUSHROOM_FIELDS = a("mushroom_fields");
    public static final ResourceKey<BiomeBase> MUSHROOM_FIELD_SHORE = a("mushroom_field_shore");
    public static final ResourceKey<BiomeBase> BEACH = a("beach");
    public static final ResourceKey<BiomeBase> DESERT_HILLS = a("desert_hills");
    public static final ResourceKey<BiomeBase> WOODED_HILLS = a("wooded_hills");
    public static final ResourceKey<BiomeBase> TAIGA_HILLS = a("taiga_hills");
    public static final ResourceKey<BiomeBase> MOUNTAIN_EDGE = a("mountain_edge");
    public static final ResourceKey<BiomeBase> JUNGLE = a("jungle");
    public static final ResourceKey<BiomeBase> JUNGLE_HILLS = a("jungle_hills");
    public static final ResourceKey<BiomeBase> JUNGLE_EDGE = a("jungle_edge");
    public static final ResourceKey<BiomeBase> DEEP_OCEAN = a("deep_ocean");
    public static final ResourceKey<BiomeBase> STONE_SHORE = a("stone_shore");
    public static final ResourceKey<BiomeBase> SNOWY_BEACH = a("snowy_beach");
    public static final ResourceKey<BiomeBase> BIRCH_FOREST = a("birch_forest");
    public static final ResourceKey<BiomeBase> BIRCH_FOREST_HILLS = a("birch_forest_hills");
    public static final ResourceKey<BiomeBase> DARK_FOREST = a("dark_forest");
    public static final ResourceKey<BiomeBase> SNOWY_TAIGA = a("snowy_taiga");
    public static final ResourceKey<BiomeBase> SNOWY_TAIGA_HILLS = a("snowy_taiga_hills");
    public static final ResourceKey<BiomeBase> GIANT_TREE_TAIGA = a("giant_tree_taiga");
    public static final ResourceKey<BiomeBase> GIANT_TREE_TAIGA_HILLS = a("giant_tree_taiga_hills");
    public static final ResourceKey<BiomeBase> WOODED_MOUNTAINS = a("wooded_mountains");
    public static final ResourceKey<BiomeBase> SAVANNA = a("savanna");
    public static final ResourceKey<BiomeBase> SAVANNA_PLATEAU = a("savanna_plateau");
    public static final ResourceKey<BiomeBase> BADLANDS = a("badlands");
    public static final ResourceKey<BiomeBase> WOODED_BADLANDS_PLATEAU = a("wooded_badlands_plateau");
    public static final ResourceKey<BiomeBase> BADLANDS_PLATEAU = a("badlands_plateau");
    public static final ResourceKey<BiomeBase> SMALL_END_ISLANDS = a("small_end_islands");
    public static final ResourceKey<BiomeBase> END_MIDLANDS = a("end_midlands");
    public static final ResourceKey<BiomeBase> END_HIGHLANDS = a("end_highlands");
    public static final ResourceKey<BiomeBase> END_BARRENS = a("end_barrens");
    public static final ResourceKey<BiomeBase> WARM_OCEAN = a("warm_ocean");
    public static final ResourceKey<BiomeBase> LUKEWARM_OCEAN = a("lukewarm_ocean");
    public static final ResourceKey<BiomeBase> COLD_OCEAN = a("cold_ocean");
    public static final ResourceKey<BiomeBase> DEEP_WARM_OCEAN = a("deep_warm_ocean");
    public static final ResourceKey<BiomeBase> DEEP_LUKEWARM_OCEAN = a("deep_lukewarm_ocean");
    public static final ResourceKey<BiomeBase> DEEP_COLD_OCEAN = a("deep_cold_ocean");
    public static final ResourceKey<BiomeBase> DEEP_FROZEN_OCEAN = a("deep_frozen_ocean");
    public static final ResourceKey<BiomeBase> THE_VOID = a("the_void");
    public static final ResourceKey<BiomeBase> SUNFLOWER_PLAINS = a("sunflower_plains");
    public static final ResourceKey<BiomeBase> DESERT_LAKES = a("desert_lakes");
    public static final ResourceKey<BiomeBase> GRAVELLY_MOUNTAINS = a("gravelly_mountains");
    public static final ResourceKey<BiomeBase> FLOWER_FOREST = a("flower_forest");
    public static final ResourceKey<BiomeBase> TAIGA_MOUNTAINS = a("taiga_mountains");
    public static final ResourceKey<BiomeBase> SWAMP_HILLS = a("swamp_hills");
    public static final ResourceKey<BiomeBase> ICE_SPIKES = a("ice_spikes");
    public static final ResourceKey<BiomeBase> MODIFIED_JUNGLE = a("modified_jungle");
    public static final ResourceKey<BiomeBase> MODIFIED_JUNGLE_EDGE = a("modified_jungle_edge");
    public static final ResourceKey<BiomeBase> TALL_BIRCH_FOREST = a("tall_birch_forest");
    public static final ResourceKey<BiomeBase> TALL_BIRCH_HILLS = a("tall_birch_hills");
    public static final ResourceKey<BiomeBase> DARK_FOREST_HILLS = a("dark_forest_hills");
    public static final ResourceKey<BiomeBase> SNOWY_TAIGA_MOUNTAINS = a("snowy_taiga_mountains");
    public static final ResourceKey<BiomeBase> GIANT_SPRUCE_TAIGA = a("giant_spruce_taiga");
    public static final ResourceKey<BiomeBase> GIANT_SPRUCE_TAIGA_HILLS = a("giant_spruce_taiga_hills");
    public static final ResourceKey<BiomeBase> MODIFIED_GRAVELLY_MOUNTAINS = a("modified_gravelly_mountains");
    public static final ResourceKey<BiomeBase> SHATTERED_SAVANNA = a("shattered_savanna");
    public static final ResourceKey<BiomeBase> SHATTERED_SAVANNA_PLATEAU = a("shattered_savanna_plateau");
    public static final ResourceKey<BiomeBase> ERODED_BADLANDS = a("eroded_badlands");
    public static final ResourceKey<BiomeBase> MODIFIED_WOODED_BADLANDS_PLATEAU = a("modified_wooded_badlands_plateau");
    public static final ResourceKey<BiomeBase> MODIFIED_BADLANDS_PLATEAU = a("modified_badlands_plateau");
    public static final ResourceKey<BiomeBase> BAMBOO_JUNGLE = a("bamboo_jungle");
    public static final ResourceKey<BiomeBase> BAMBOO_JUNGLE_HILLS = a("bamboo_jungle_hills");
    public static final ResourceKey<BiomeBase> SOUL_SAND_VALLEY = a("soul_sand_valley");
    public static final ResourceKey<BiomeBase> CRIMSON_FOREST = a("crimson_forest");
    public static final ResourceKey<BiomeBase> WARPED_FOREST = a("warped_forest");
    public static final ResourceKey<BiomeBase> BASALT_DELTAS = a("basalt_deltas");
    public static final ResourceKey<BiomeBase> DRIPSTONE_CAVES = a("dripstone_caves");
    public static final ResourceKey<BiomeBase> LUSH_CAVES = a("lush_caves");

    public Biomes() {}

    private static ResourceKey<BiomeBase> a(String s) {
        return ResourceKey.a(IRegistry.BIOME_REGISTRY, new MinecraftKey(s));
    }
}
