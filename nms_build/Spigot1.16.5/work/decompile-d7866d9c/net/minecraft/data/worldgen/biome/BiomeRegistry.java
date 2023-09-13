package net.minecraft.data.worldgen.biome;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.data.worldgen.WorldGenSurfaceComposites;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.Biomes;

public abstract class BiomeRegistry {

    private static final Int2ObjectMap<ResourceKey<BiomeBase>> c = new Int2ObjectArrayMap();
    public static final BiomeBase a;
    public static final BiomeBase b;

    private static BiomeBase a(int i, ResourceKey<BiomeBase> resourcekey, BiomeBase biomebase) {
        BiomeRegistry.c.put(i, resourcekey);
        return (BiomeBase) RegistryGeneration.a(RegistryGeneration.WORLDGEN_BIOME, i, resourcekey, biomebase);
    }

    public static ResourceKey<BiomeBase> a(int i) {
        return (ResourceKey) BiomeRegistry.c.get(i);
    }

    static {
        a(0, Biomes.OCEAN, BiomesSettingsDefault.c(false));
        a = a(1, Biomes.PLAINS, BiomesSettingsDefault.a(false));
        a(2, Biomes.DESERT, BiomesSettingsDefault.a(0.125F, 0.05F, true, true, true));
        a(3, Biomes.MOUNTAINS, BiomesSettingsDefault.a(1.0F, 0.5F, WorldGenSurfaceComposites.m, false));
        a(4, Biomes.FOREST, BiomesSettingsDefault.c(0.1F, 0.2F));
        a(5, Biomes.TAIGA, BiomesSettingsDefault.a(0.2F, 0.2F, false, false, true, false));
        a(6, Biomes.SWAMP, BiomesSettingsDefault.d(-0.2F, 0.1F, false));
        a(7, Biomes.RIVER, BiomesSettingsDefault.a(-0.5F, 0.0F, 0.5F, 4159204, false));
        a(8, Biomes.NETHER_WASTES, BiomesSettingsDefault.s());
        a(9, Biomes.THE_END, BiomesSettingsDefault.i());
        a(10, Biomes.FROZEN_OCEAN, BiomesSettingsDefault.e(false));
        a(11, Biomes.FROZEN_RIVER, BiomesSettingsDefault.a(-0.5F, 0.0F, 0.0F, 3750089, true));
        a(12, Biomes.SNOWY_TUNDRA, BiomesSettingsDefault.a(0.125F, 0.05F, false, false));
        a(13, Biomes.SNOWY_MOUNTAINS, BiomesSettingsDefault.a(0.45F, 0.3F, false, true));
        a(14, Biomes.MUSHROOM_FIELDS, BiomesSettingsDefault.a(0.2F, 0.3F));
        a(15, Biomes.MUSHROOM_FIELD_SHORE, BiomesSettingsDefault.a(0.0F, 0.025F));
        a(16, Biomes.BEACH, BiomesSettingsDefault.a(0.0F, 0.025F, 0.8F, 0.4F, 4159204, false, false));
        a(17, Biomes.DESERT_HILLS, BiomesSettingsDefault.a(0.45F, 0.3F, false, true, false));
        a(18, Biomes.WOODED_HILLS, BiomesSettingsDefault.c(0.45F, 0.3F));
        a(19, Biomes.TAIGA_HILLS, BiomesSettingsDefault.a(0.45F, 0.3F, false, false, false, false));
        a(20, Biomes.MOUNTAIN_EDGE, BiomesSettingsDefault.a(0.8F, 0.3F, WorldGenSurfaceComposites.j, true));
        a(21, Biomes.JUNGLE, BiomesSettingsDefault.a());
        a(22, Biomes.JUNGLE_HILLS, BiomesSettingsDefault.e());
        a(23, Biomes.JUNGLE_EDGE, BiomesSettingsDefault.b());
        a(24, Biomes.DEEP_OCEAN, BiomesSettingsDefault.c(true));
        a(25, Biomes.STONE_SHORE, BiomesSettingsDefault.a(0.1F, 0.8F, 0.2F, 0.3F, 4159204, false, true));
        a(26, Biomes.SNOWY_BEACH, BiomesSettingsDefault.a(0.0F, 0.025F, 0.05F, 0.3F, 4020182, true, false));
        a(27, Biomes.BIRCH_FOREST, BiomesSettingsDefault.a(0.1F, 0.2F, false));
        a(28, Biomes.BIRCH_FOREST_HILLS, BiomesSettingsDefault.a(0.45F, 0.3F, false));
        a(29, Biomes.DARK_FOREST, BiomesSettingsDefault.c(0.1F, 0.2F, false));
        a(30, Biomes.SNOWY_TAIGA, BiomesSettingsDefault.a(0.2F, 0.2F, true, false, false, true));
        a(31, Biomes.SNOWY_TAIGA_HILLS, BiomesSettingsDefault.a(0.45F, 0.3F, true, false, false, false));
        a(32, Biomes.GIANT_TREE_TAIGA, BiomesSettingsDefault.a(0.2F, 0.2F, 0.3F, false));
        a(33, Biomes.GIANT_TREE_TAIGA_HILLS, BiomesSettingsDefault.a(0.45F, 0.3F, 0.3F, false));
        a(34, Biomes.WOODED_MOUNTAINS, BiomesSettingsDefault.a(1.0F, 0.5F, WorldGenSurfaceComposites.j, true));
        a(35, Biomes.SAVANNA, BiomesSettingsDefault.a(0.125F, 0.05F, 1.2F, false, false));
        a(36, Biomes.SAVANNA_PLATEAU, BiomesSettingsDefault.m());
        a(37, Biomes.BADLANDS, BiomesSettingsDefault.b(0.1F, 0.2F, false));
        a(38, Biomes.WOODED_BADLANDS_PLATEAU, BiomesSettingsDefault.b(1.5F, 0.025F));
        a(39, Biomes.BADLANDS_PLATEAU, BiomesSettingsDefault.b(1.5F, 0.025F, true));
        a(40, Biomes.SMALL_END_ISLANDS, BiomesSettingsDefault.l());
        a(41, Biomes.END_MIDLANDS, BiomesSettingsDefault.j());
        a(42, Biomes.END_HIGHLANDS, BiomesSettingsDefault.k());
        a(43, Biomes.END_BARRENS, BiomesSettingsDefault.h());
        a(44, Biomes.WARM_OCEAN, BiomesSettingsDefault.o());
        a(45, Biomes.LUKEWARM_OCEAN, BiomesSettingsDefault.d(false));
        a(46, Biomes.COLD_OCEAN, BiomesSettingsDefault.b(false));
        a(47, Biomes.DEEP_WARM_OCEAN, BiomesSettingsDefault.p());
        a(48, Biomes.DEEP_LUKEWARM_OCEAN, BiomesSettingsDefault.d(true));
        a(49, Biomes.DEEP_COLD_OCEAN, BiomesSettingsDefault.b(true));
        a(50, Biomes.DEEP_FROZEN_OCEAN, BiomesSettingsDefault.e(true));
        b = a(127, Biomes.THE_VOID, BiomesSettingsDefault.r());
        a(129, Biomes.SUNFLOWER_PLAINS, BiomesSettingsDefault.a(true));
        a(130, Biomes.DESERT_LAKES, BiomesSettingsDefault.a(0.225F, 0.25F, false, false, false));
        a(131, Biomes.GRAVELLY_MOUNTAINS, BiomesSettingsDefault.a(1.0F, 0.5F, WorldGenSurfaceComposites.k, false));
        a(132, Biomes.FLOWER_FOREST, BiomesSettingsDefault.q());
        a(133, Biomes.TAIGA_MOUNTAINS, BiomesSettingsDefault.a(0.3F, 0.4F, false, true, false, false));
        a(134, Biomes.SWAMP_HILLS, BiomesSettingsDefault.d(-0.1F, 0.3F, true));
        a(140, Biomes.ICE_SPIKES, BiomesSettingsDefault.a(0.425F, 0.45000002F, true, false));
        a(149, Biomes.MODIFIED_JUNGLE, BiomesSettingsDefault.d());
        a(151, Biomes.MODIFIED_JUNGLE_EDGE, BiomesSettingsDefault.c());
        a(155, Biomes.TALL_BIRCH_FOREST, BiomesSettingsDefault.a(0.2F, 0.4F, true));
        a(156, Biomes.TALL_BIRCH_HILLS, BiomesSettingsDefault.a(0.55F, 0.5F, true));
        a(157, Biomes.DARK_FOREST_HILLS, BiomesSettingsDefault.c(0.2F, 0.4F, true));
        a(158, Biomes.SNOWY_TAIGA_MOUNTAINS, BiomesSettingsDefault.a(0.3F, 0.4F, true, true, false, false));
        a(160, Biomes.GIANT_SPRUCE_TAIGA, BiomesSettingsDefault.a(0.2F, 0.2F, 0.25F, true));
        a(161, Biomes.GIANT_SPRUCE_TAIGA_HILLS, BiomesSettingsDefault.a(0.2F, 0.2F, 0.25F, true));
        a(162, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, BiomesSettingsDefault.a(1.0F, 0.5F, WorldGenSurfaceComposites.k, false));
        a(163, Biomes.SHATTERED_SAVANNA, BiomesSettingsDefault.a(0.3625F, 1.225F, 1.1F, true, true));
        a(164, Biomes.SHATTERED_SAVANNA_PLATEAU, BiomesSettingsDefault.a(1.05F, 1.2125001F, 1.0F, true, true));
        a(165, Biomes.ERODED_BADLANDS, BiomesSettingsDefault.n());
        a(166, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, BiomesSettingsDefault.b(0.45F, 0.3F));
        a(167, Biomes.MODIFIED_BADLANDS_PLATEAU, BiomesSettingsDefault.b(0.45F, 0.3F, true));
        a(168, Biomes.BAMBOO_JUNGLE, BiomesSettingsDefault.f());
        a(169, Biomes.BAMBOO_JUNGLE_HILLS, BiomesSettingsDefault.g());
        a(170, Biomes.SOUL_SAND_VALLEY, BiomesSettingsDefault.t());
        a(171, Biomes.CRIMSON_FOREST, BiomesSettingsDefault.v());
        a(172, Biomes.WARPED_FOREST, BiomesSettingsDefault.w());
        a(173, Biomes.BASALT_DELTAS, BiomesSettingsDefault.u());
    }
}
