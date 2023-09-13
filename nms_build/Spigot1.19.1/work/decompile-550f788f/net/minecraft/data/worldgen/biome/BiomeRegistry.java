package net.minecraft.data.worldgen.biome;

import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.Biomes;

public abstract class BiomeRegistry {

    public BiomeRegistry() {}

    public static Holder<BiomeBase> bootstrap(IRegistry<BiomeBase> iregistry) {
        RegistryGeneration.register(iregistry, Biomes.THE_VOID, OverworldBiomes.theVoid());
        RegistryGeneration.register(iregistry, Biomes.PLAINS, OverworldBiomes.plains(false, false, false));
        RegistryGeneration.register(iregistry, Biomes.SUNFLOWER_PLAINS, OverworldBiomes.plains(true, false, false));
        RegistryGeneration.register(iregistry, Biomes.SNOWY_PLAINS, OverworldBiomes.plains(false, true, false));
        RegistryGeneration.register(iregistry, Biomes.ICE_SPIKES, OverworldBiomes.plains(false, true, true));
        RegistryGeneration.register(iregistry, Biomes.DESERT, OverworldBiomes.desert());
        RegistryGeneration.register(iregistry, Biomes.SWAMP, OverworldBiomes.swamp());
        RegistryGeneration.register(iregistry, Biomes.MANGROVE_SWAMP, OverworldBiomes.mangroveSwamp());
        RegistryGeneration.register(iregistry, Biomes.FOREST, OverworldBiomes.forest(false, false, false));
        RegistryGeneration.register(iregistry, Biomes.FLOWER_FOREST, OverworldBiomes.forest(false, false, true));
        RegistryGeneration.register(iregistry, Biomes.BIRCH_FOREST, OverworldBiomes.forest(true, false, false));
        RegistryGeneration.register(iregistry, Biomes.DARK_FOREST, OverworldBiomes.darkForest());
        RegistryGeneration.register(iregistry, Biomes.OLD_GROWTH_BIRCH_FOREST, OverworldBiomes.forest(true, true, false));
        RegistryGeneration.register(iregistry, Biomes.OLD_GROWTH_PINE_TAIGA, OverworldBiomes.oldGrowthTaiga(false));
        RegistryGeneration.register(iregistry, Biomes.OLD_GROWTH_SPRUCE_TAIGA, OverworldBiomes.oldGrowthTaiga(true));
        RegistryGeneration.register(iregistry, Biomes.TAIGA, OverworldBiomes.taiga(false));
        RegistryGeneration.register(iregistry, Biomes.SNOWY_TAIGA, OverworldBiomes.taiga(true));
        RegistryGeneration.register(iregistry, Biomes.SAVANNA, OverworldBiomes.savanna(false, false));
        RegistryGeneration.register(iregistry, Biomes.SAVANNA_PLATEAU, OverworldBiomes.savanna(false, true));
        RegistryGeneration.register(iregistry, Biomes.WINDSWEPT_HILLS, OverworldBiomes.windsweptHills(false));
        RegistryGeneration.register(iregistry, Biomes.WINDSWEPT_GRAVELLY_HILLS, OverworldBiomes.windsweptHills(false));
        RegistryGeneration.register(iregistry, Biomes.WINDSWEPT_FOREST, OverworldBiomes.windsweptHills(true));
        RegistryGeneration.register(iregistry, Biomes.WINDSWEPT_SAVANNA, OverworldBiomes.savanna(true, false));
        RegistryGeneration.register(iregistry, Biomes.JUNGLE, OverworldBiomes.jungle());
        RegistryGeneration.register(iregistry, Biomes.SPARSE_JUNGLE, OverworldBiomes.sparseJungle());
        RegistryGeneration.register(iregistry, Biomes.BAMBOO_JUNGLE, OverworldBiomes.bambooJungle());
        RegistryGeneration.register(iregistry, Biomes.BADLANDS, OverworldBiomes.badlands(false));
        RegistryGeneration.register(iregistry, Biomes.ERODED_BADLANDS, OverworldBiomes.badlands(false));
        RegistryGeneration.register(iregistry, Biomes.WOODED_BADLANDS, OverworldBiomes.badlands(true));
        RegistryGeneration.register(iregistry, Biomes.MEADOW, OverworldBiomes.meadow());
        RegistryGeneration.register(iregistry, Biomes.GROVE, OverworldBiomes.grove());
        RegistryGeneration.register(iregistry, Biomes.SNOWY_SLOPES, OverworldBiomes.snowySlopes());
        RegistryGeneration.register(iregistry, Biomes.FROZEN_PEAKS, OverworldBiomes.frozenPeaks());
        RegistryGeneration.register(iregistry, Biomes.JAGGED_PEAKS, OverworldBiomes.jaggedPeaks());
        RegistryGeneration.register(iregistry, Biomes.STONY_PEAKS, OverworldBiomes.stonyPeaks());
        RegistryGeneration.register(iregistry, Biomes.RIVER, OverworldBiomes.river(false));
        RegistryGeneration.register(iregistry, Biomes.FROZEN_RIVER, OverworldBiomes.river(true));
        RegistryGeneration.register(iregistry, Biomes.BEACH, OverworldBiomes.beach(false, false));
        RegistryGeneration.register(iregistry, Biomes.SNOWY_BEACH, OverworldBiomes.beach(true, false));
        RegistryGeneration.register(iregistry, Biomes.STONY_SHORE, OverworldBiomes.beach(false, true));
        RegistryGeneration.register(iregistry, Biomes.WARM_OCEAN, OverworldBiomes.warmOcean());
        RegistryGeneration.register(iregistry, Biomes.LUKEWARM_OCEAN, OverworldBiomes.lukeWarmOcean(false));
        RegistryGeneration.register(iregistry, Biomes.DEEP_LUKEWARM_OCEAN, OverworldBiomes.lukeWarmOcean(true));
        RegistryGeneration.register(iregistry, Biomes.OCEAN, OverworldBiomes.ocean(false));
        RegistryGeneration.register(iregistry, Biomes.DEEP_OCEAN, OverworldBiomes.ocean(true));
        RegistryGeneration.register(iregistry, Biomes.COLD_OCEAN, OverworldBiomes.coldOcean(false));
        RegistryGeneration.register(iregistry, Biomes.DEEP_COLD_OCEAN, OverworldBiomes.coldOcean(true));
        RegistryGeneration.register(iregistry, Biomes.FROZEN_OCEAN, OverworldBiomes.frozenOcean(false));
        RegistryGeneration.register(iregistry, Biomes.DEEP_FROZEN_OCEAN, OverworldBiomes.frozenOcean(true));
        RegistryGeneration.register(iregistry, Biomes.MUSHROOM_FIELDS, OverworldBiomes.mushroomFields());
        RegistryGeneration.register(iregistry, Biomes.DRIPSTONE_CAVES, OverworldBiomes.dripstoneCaves());
        RegistryGeneration.register(iregistry, Biomes.LUSH_CAVES, OverworldBiomes.lushCaves());
        RegistryGeneration.register(iregistry, Biomes.DEEP_DARK, OverworldBiomes.deepDark());
        RegistryGeneration.register(iregistry, Biomes.NETHER_WASTES, NetherBiomes.netherWastes());
        RegistryGeneration.register(iregistry, Biomes.WARPED_FOREST, NetherBiomes.warpedForest());
        RegistryGeneration.register(iregistry, Biomes.CRIMSON_FOREST, NetherBiomes.crimsonForest());
        RegistryGeneration.register(iregistry, Biomes.SOUL_SAND_VALLEY, NetherBiomes.soulSandValley());
        RegistryGeneration.register(iregistry, Biomes.BASALT_DELTAS, NetherBiomes.basaltDeltas());
        RegistryGeneration.register(iregistry, Biomes.THE_END, EndBiomes.theEnd());
        RegistryGeneration.register(iregistry, Biomes.END_HIGHLANDS, EndBiomes.endHighlands());
        RegistryGeneration.register(iregistry, Biomes.END_MIDLANDS, EndBiomes.endMidlands());
        RegistryGeneration.register(iregistry, Biomes.SMALL_END_ISLANDS, EndBiomes.smallEndIslands());
        return RegistryGeneration.register(iregistry, Biomes.END_BARRENS, EndBiomes.endBarrens());
    }
}
