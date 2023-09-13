package net.minecraft.world.level.levelgen.structure;

import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

public interface BuiltinStructures {

    ResourceKey<StructureFeature<?, ?>> PILLAGER_OUTPOST = createKey("pillager_outpost");
    ResourceKey<StructureFeature<?, ?>> MINESHAFT = createKey("mineshaft");
    ResourceKey<StructureFeature<?, ?>> MINESHAFT_MESA = createKey("mineshaft_mesa");
    ResourceKey<StructureFeature<?, ?>> WOODLAND_MANSION = createKey("mansion");
    ResourceKey<StructureFeature<?, ?>> JUNGLE_TEMPLE = createKey("jungle_pyramid");
    ResourceKey<StructureFeature<?, ?>> DESERT_PYRAMID = createKey("desert_pyramid");
    ResourceKey<StructureFeature<?, ?>> IGLOO = createKey("igloo");
    ResourceKey<StructureFeature<?, ?>> SHIPWRECK = createKey("shipwreck");
    ResourceKey<StructureFeature<?, ?>> SHIPWRECK_BEACHED = createKey("shipwreck_beached");
    ResourceKey<StructureFeature<?, ?>> SWAMP_HUT = createKey("swamp_hut");
    ResourceKey<StructureFeature<?, ?>> STRONGHOLD = createKey("stronghold");
    ResourceKey<StructureFeature<?, ?>> OCEAN_MONUMENT = createKey("monument");
    ResourceKey<StructureFeature<?, ?>> OCEAN_RUIN_COLD = createKey("ocean_ruin_cold");
    ResourceKey<StructureFeature<?, ?>> OCEAN_RUIN_WARM = createKey("ocean_ruin_warm");
    ResourceKey<StructureFeature<?, ?>> FORTRESS = createKey("fortress");
    ResourceKey<StructureFeature<?, ?>> NETHER_FOSSIL = createKey("nether_fossil");
    ResourceKey<StructureFeature<?, ?>> END_CITY = createKey("end_city");
    ResourceKey<StructureFeature<?, ?>> BURIED_TREASURE = createKey("buried_treasure");
    ResourceKey<StructureFeature<?, ?>> BASTION_REMNANT = createKey("bastion_remnant");
    ResourceKey<StructureFeature<?, ?>> VILLAGE_PLAINS = createKey("village_plains");
    ResourceKey<StructureFeature<?, ?>> VILLAGE_DESERT = createKey("village_desert");
    ResourceKey<StructureFeature<?, ?>> VILLAGE_SAVANNA = createKey("village_savanna");
    ResourceKey<StructureFeature<?, ?>> VILLAGE_SNOWY = createKey("village_snowy");
    ResourceKey<StructureFeature<?, ?>> VILLAGE_TAIGA = createKey("village_taiga");
    ResourceKey<StructureFeature<?, ?>> RUINED_PORTAL_STANDARD = createKey("ruined_portal");
    ResourceKey<StructureFeature<?, ?>> RUINED_PORTAL_DESERT = createKey("ruined_portal_desert");
    ResourceKey<StructureFeature<?, ?>> RUINED_PORTAL_JUNGLE = createKey("ruined_portal_jungle");
    ResourceKey<StructureFeature<?, ?>> RUINED_PORTAL_SWAMP = createKey("ruined_portal_swamp");
    ResourceKey<StructureFeature<?, ?>> RUINED_PORTAL_MOUNTAIN = createKey("ruined_portal_mountain");
    ResourceKey<StructureFeature<?, ?>> RUINED_PORTAL_OCEAN = createKey("ruined_portal_ocean");
    ResourceKey<StructureFeature<?, ?>> RUINED_PORTAL_NETHER = createKey("ruined_portal_nether");

    private static ResourceKey<StructureFeature<?, ?>> createKey(String s) {
        return ResourceKey.create(IRegistry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, new MinecraftKey(s));
    }
}
