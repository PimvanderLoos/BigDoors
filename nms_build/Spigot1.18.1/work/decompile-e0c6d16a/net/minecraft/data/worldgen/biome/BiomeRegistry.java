package net.minecraft.data.worldgen.biome;

import net.minecraft.data.RegistryGeneration;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.Biomes;

public abstract class BiomeRegistry {

    /** @deprecated */
    @Deprecated
    public static final BiomeBase THE_VOID = register(Biomes.THE_VOID, OverworldBiomes.theVoid());
    /** @deprecated */
    @Deprecated
    public static final BiomeBase PLAINS = register(Biomes.PLAINS, OverworldBiomes.plains(false, false, false));

    public BiomeRegistry() {}

    private static BiomeBase register(ResourceKey<BiomeBase> resourcekey, BiomeBase biomebase) {
        return (BiomeBase) RegistryGeneration.registerMapping(RegistryGeneration.BIOME, resourcekey, biomebase);
    }

    static {
        register(Biomes.SUNFLOWER_PLAINS, OverworldBiomes.plains(true, false, false));
        register(Biomes.SNOWY_PLAINS, OverworldBiomes.plains(false, true, false));
        register(Biomes.ICE_SPIKES, OverworldBiomes.plains(false, true, true));
        register(Biomes.DESERT, OverworldBiomes.desert());
        register(Biomes.SWAMP, OverworldBiomes.swamp());
        register(Biomes.FOREST, OverworldBiomes.forest(false, false, false));
        register(Biomes.FLOWER_FOREST, OverworldBiomes.forest(false, false, true));
        register(Biomes.BIRCH_FOREST, OverworldBiomes.forest(true, false, false));
        register(Biomes.DARK_FOREST, OverworldBiomes.darkForest());
        register(Biomes.OLD_GROWTH_BIRCH_FOREST, OverworldBiomes.forest(true, true, false));
        register(Biomes.OLD_GROWTH_PINE_TAIGA, OverworldBiomes.oldGrowthTaiga(false));
        register(Biomes.OLD_GROWTH_SPRUCE_TAIGA, OverworldBiomes.oldGrowthTaiga(true));
        register(Biomes.TAIGA, OverworldBiomes.taiga(false));
        register(Biomes.SNOWY_TAIGA, OverworldBiomes.taiga(true));
        register(Biomes.SAVANNA, OverworldBiomes.savanna(false, false));
        register(Biomes.SAVANNA_PLATEAU, OverworldBiomes.savanna(false, true));
        register(Biomes.WINDSWEPT_HILLS, OverworldBiomes.windsweptHills(false));
        register(Biomes.WINDSWEPT_GRAVELLY_HILLS, OverworldBiomes.windsweptHills(false));
        register(Biomes.WINDSWEPT_FOREST, OverworldBiomes.windsweptHills(true));
        register(Biomes.WINDSWEPT_SAVANNA, OverworldBiomes.savanna(true, false));
        register(Biomes.JUNGLE, OverworldBiomes.jungle());
        register(Biomes.SPARSE_JUNGLE, OverworldBiomes.sparseJungle());
        register(Biomes.BAMBOO_JUNGLE, OverworldBiomes.bambooJungle());
        register(Biomes.BADLANDS, OverworldBiomes.badlands(false));
        register(Biomes.ERODED_BADLANDS, OverworldBiomes.badlands(false));
        register(Biomes.WOODED_BADLANDS, OverworldBiomes.badlands(true));
        register(Biomes.MEADOW, OverworldBiomes.meadow());
        register(Biomes.GROVE, OverworldBiomes.grove());
        register(Biomes.SNOWY_SLOPES, OverworldBiomes.snowySlopes());
        register(Biomes.FROZEN_PEAKS, OverworldBiomes.frozenPeaks());
        register(Biomes.JAGGED_PEAKS, OverworldBiomes.jaggedPeaks());
        register(Biomes.STONY_PEAKS, OverworldBiomes.stonyPeaks());
        register(Biomes.RIVER, OverworldBiomes.river(false));
        register(Biomes.FROZEN_RIVER, OverworldBiomes.river(true));
        register(Biomes.BEACH, OverworldBiomes.beach(false, false));
        register(Biomes.SNOWY_BEACH, OverworldBiomes.beach(true, false));
        register(Biomes.STONY_SHORE, OverworldBiomes.beach(false, true));
        register(Biomes.WARM_OCEAN, OverworldBiomes.warmOcean());
        register(Biomes.LUKEWARM_OCEAN, OverworldBiomes.lukeWarmOcean(false));
        register(Biomes.DEEP_LUKEWARM_OCEAN, OverworldBiomes.lukeWarmOcean(true));
        register(Biomes.OCEAN, OverworldBiomes.ocean(false));
        register(Biomes.DEEP_OCEAN, OverworldBiomes.ocean(true));
        register(Biomes.COLD_OCEAN, OverworldBiomes.coldOcean(false));
        register(Biomes.DEEP_COLD_OCEAN, OverworldBiomes.coldOcean(true));
        register(Biomes.FROZEN_OCEAN, OverworldBiomes.frozenOcean(false));
        register(Biomes.DEEP_FROZEN_OCEAN, OverworldBiomes.frozenOcean(true));
        register(Biomes.MUSHROOM_FIELDS, OverworldBiomes.mushroomFields());
        register(Biomes.DRIPSTONE_CAVES, OverworldBiomes.dripstoneCaves());
        register(Biomes.LUSH_CAVES, OverworldBiomes.lushCaves());
        register(Biomes.NETHER_WASTES, NetherBiomes.netherWastes());
        register(Biomes.WARPED_FOREST, NetherBiomes.warpedForest());
        register(Biomes.CRIMSON_FOREST, NetherBiomes.crimsonForest());
        register(Biomes.SOUL_SAND_VALLEY, NetherBiomes.soulSandValley());
        register(Biomes.BASALT_DELTAS, NetherBiomes.basaltDeltas());
        register(Biomes.THE_END, EndBiomes.theEnd());
        register(Biomes.END_HIGHLANDS, EndBiomes.endHighlands());
        register(Biomes.END_MIDLANDS, EndBiomes.endMidlands());
        register(Biomes.SMALL_END_ISLANDS, EndBiomes.smallEndIslands());
        register(Biomes.END_BARRENS, EndBiomes.endBarrens());
    }
}
