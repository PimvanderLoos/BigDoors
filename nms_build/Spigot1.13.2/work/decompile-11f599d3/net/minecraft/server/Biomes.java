package net.minecraft.server;

public abstract class Biomes {

    public static final BiomeBase OCEAN;
    public static final BiomeBase b;
    public static final BiomeBase PLAINS;
    public static final BiomeBase DESERT;
    public static final BiomeBase MOUNTAINS;
    public static final BiomeBase FOREST;
    public static final BiomeBase TAIGA;
    public static final BiomeBase SWAMP;
    public static final BiomeBase RIVER;
    public static final BiomeBase NETHER;
    public static final BiomeBase THE_END;
    public static final BiomeBase FROZEN_OCEAN;
    public static final BiomeBase FROZEN_RIVER;
    public static final BiomeBase SNOWY_TUNDRA;
    public static final BiomeBase SNOWY_MOUNTAINS;
    public static final BiomeBase MUSHROOM_FIELDS;
    public static final BiomeBase MUSHROOM_FIELD_SHORE;
    public static final BiomeBase BEACH;
    public static final BiomeBase DESERT_HILLS;
    public static final BiomeBase WOODED_HILLS;
    public static final BiomeBase TAIGA_HILLS;
    public static final BiomeBase MOUNTAIN_EDGE;
    public static final BiomeBase JUNGLE;
    public static final BiomeBase JUNGLE_HILLS;
    public static final BiomeBase JUNGLE_EDGE;
    public static final BiomeBase DEEP_OCEAN;
    public static final BiomeBase STONE_SHORE;
    public static final BiomeBase SNOWY_BEACH;
    public static final BiomeBase BIRCH_FOREST;
    public static final BiomeBase BIRCH_FOREST_HILLS;
    public static final BiomeBase DARK_FOREST;
    public static final BiomeBase SNOWY_TAIGA;
    public static final BiomeBase SNOWY_TAIGA_HILLS;
    public static final BiomeBase GIANT_TREE_TAIGA;
    public static final BiomeBase GIANT_TREE_TAIGA_HILLS;
    public static final BiomeBase WOODED_MOUNTAINS;
    public static final BiomeBase SAVANNA;
    public static final BiomeBase SAVANNA_PLATEAU;
    public static final BiomeBase BADLANDS;
    public static final BiomeBase WOODED_BADLANDS_PLATEAU;
    public static final BiomeBase BADLANDS_PLATEAU;
    public static final BiomeBase SMALL_END_ISLANDS;
    public static final BiomeBase END_MIDLANDS;
    public static final BiomeBase END_HIGHLANDS;
    public static final BiomeBase END_BARRENS;
    public static final BiomeBase WARM_OCEAN;
    public static final BiomeBase LUKEWARM_OCEAN;
    public static final BiomeBase COLD_OCEAN;
    public static final BiomeBase DEEP_WARM_OCEAN;
    public static final BiomeBase DEEP_LUKEWARM_OCEAN;
    public static final BiomeBase DEEP_COLD_OCEAN;
    public static final BiomeBase DEEP_FROZEN_OCEAN;
    public static final BiomeBase THE_VOID;
    public static final BiomeBase SUNFLOWER_PLAINS;
    public static final BiomeBase DESERT_LAKES;
    public static final BiomeBase GRAVELLY_MOUNTAINS;
    public static final BiomeBase FLOWER_FOREST;
    public static final BiomeBase TAIGA_MOUNTAINS;
    public static final BiomeBase SWAMP_HILLS;
    public static final BiomeBase ICE_SPIKES;
    public static final BiomeBase MODIFIED_JUNGLE;
    public static final BiomeBase MODIFIED_JUNGLE_EDGE;
    public static final BiomeBase TALL_BIRCH_FOREST;
    public static final BiomeBase TALL_BIRCH_HILLS;
    public static final BiomeBase DARK_FOREST_HILLS;
    public static final BiomeBase SNOWY_TAIGA_MOUNTAINS;
    public static final BiomeBase GIANT_SPRUCE_TAIGA;
    public static final BiomeBase GIANT_SPRUCE_TAIGA_HILLS;
    public static final BiomeBase MODIFIED_GRAVELLY_MOUNTAINS;
    public static final BiomeBase SHATTERED_SAVANNA;
    public static final BiomeBase SHATTERED_SAVANNA_PLATEAU;
    public static final BiomeBase ERODED_BADLANDS;
    public static final BiomeBase MODIFIED_WOODED_BADLANDS_PLATEAU;
    public static final BiomeBase MODIFIED_BADLANDS_PLATEAU;

    private static BiomeBase a(String s) {
        BiomeBase biomebase = (BiomeBase) IRegistry.BIOME.get(new MinecraftKey(s));

        if (biomebase == null) {
            throw new IllegalStateException("Invalid Biome requested: " + s);
        } else {
            return biomebase;
        }
    }

    static {
        if (!DispenserRegistry.a()) {
            throw new RuntimeException("Accessed Biomes before Bootstrap!");
        } else {
            OCEAN = a("ocean");
            b = Biomes.OCEAN;
            PLAINS = a("plains");
            DESERT = a("desert");
            MOUNTAINS = a("mountains");
            FOREST = a("forest");
            TAIGA = a("taiga");
            SWAMP = a("swamp");
            RIVER = a("river");
            NETHER = a("nether");
            THE_END = a("the_end");
            FROZEN_OCEAN = a("frozen_ocean");
            FROZEN_RIVER = a("frozen_river");
            SNOWY_TUNDRA = a("snowy_tundra");
            SNOWY_MOUNTAINS = a("snowy_mountains");
            MUSHROOM_FIELDS = a("mushroom_fields");
            MUSHROOM_FIELD_SHORE = a("mushroom_field_shore");
            BEACH = a("beach");
            DESERT_HILLS = a("desert_hills");
            WOODED_HILLS = a("wooded_hills");
            TAIGA_HILLS = a("taiga_hills");
            MOUNTAIN_EDGE = a("mountain_edge");
            JUNGLE = a("jungle");
            JUNGLE_HILLS = a("jungle_hills");
            JUNGLE_EDGE = a("jungle_edge");
            DEEP_OCEAN = a("deep_ocean");
            STONE_SHORE = a("stone_shore");
            SNOWY_BEACH = a("snowy_beach");
            BIRCH_FOREST = a("birch_forest");
            BIRCH_FOREST_HILLS = a("birch_forest_hills");
            DARK_FOREST = a("dark_forest");
            SNOWY_TAIGA = a("snowy_taiga");
            SNOWY_TAIGA_HILLS = a("snowy_taiga_hills");
            GIANT_TREE_TAIGA = a("giant_tree_taiga");
            GIANT_TREE_TAIGA_HILLS = a("giant_tree_taiga_hills");
            WOODED_MOUNTAINS = a("wooded_mountains");
            SAVANNA = a("savanna");
            SAVANNA_PLATEAU = a("savanna_plateau");
            BADLANDS = a("badlands");
            WOODED_BADLANDS_PLATEAU = a("wooded_badlands_plateau");
            BADLANDS_PLATEAU = a("badlands_plateau");
            SMALL_END_ISLANDS = a("small_end_islands");
            END_MIDLANDS = a("end_midlands");
            END_HIGHLANDS = a("end_highlands");
            END_BARRENS = a("end_barrens");
            WARM_OCEAN = a("warm_ocean");
            LUKEWARM_OCEAN = a("lukewarm_ocean");
            COLD_OCEAN = a("cold_ocean");
            DEEP_WARM_OCEAN = a("deep_warm_ocean");
            DEEP_LUKEWARM_OCEAN = a("deep_lukewarm_ocean");
            DEEP_COLD_OCEAN = a("deep_cold_ocean");
            DEEP_FROZEN_OCEAN = a("deep_frozen_ocean");
            THE_VOID = a("the_void");
            SUNFLOWER_PLAINS = a("sunflower_plains");
            DESERT_LAKES = a("desert_lakes");
            GRAVELLY_MOUNTAINS = a("gravelly_mountains");
            FLOWER_FOREST = a("flower_forest");
            TAIGA_MOUNTAINS = a("taiga_mountains");
            SWAMP_HILLS = a("swamp_hills");
            ICE_SPIKES = a("ice_spikes");
            MODIFIED_JUNGLE = a("modified_jungle");
            MODIFIED_JUNGLE_EDGE = a("modified_jungle_edge");
            TALL_BIRCH_FOREST = a("tall_birch_forest");
            TALL_BIRCH_HILLS = a("tall_birch_hills");
            DARK_FOREST_HILLS = a("dark_forest_hills");
            SNOWY_TAIGA_MOUNTAINS = a("snowy_taiga_mountains");
            GIANT_SPRUCE_TAIGA = a("giant_spruce_taiga");
            GIANT_SPRUCE_TAIGA_HILLS = a("giant_spruce_taiga_hills");
            MODIFIED_GRAVELLY_MOUNTAINS = a("modified_gravelly_mountains");
            SHATTERED_SAVANNA = a("shattered_savanna");
            SHATTERED_SAVANNA_PLATEAU = a("shattered_savanna_plateau");
            ERODED_BADLANDS = a("eroded_badlands");
            MODIFIED_WOODED_BADLANDS_PLATEAU = a("modified_wooded_badlands_plateau");
            MODIFIED_BADLANDS_PLATEAU = a("modified_badlands_plateau");
        }
    }
}
