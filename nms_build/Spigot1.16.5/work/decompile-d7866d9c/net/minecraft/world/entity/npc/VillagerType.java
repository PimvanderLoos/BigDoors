package net.minecraft.world.entity.npc;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import net.minecraft.SystemUtils;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.Biomes;

public final class VillagerType {

    public static final VillagerType DESERT = a("desert");
    public static final VillagerType JUNGLE = a("jungle");
    public static final VillagerType PLAINS = a("plains");
    public static final VillagerType SAVANNA = a("savanna");
    public static final VillagerType SNOW = a("snow");
    public static final VillagerType SWAMP = a("swamp");
    public static final VillagerType TAIGA = a("taiga");
    private final String h;
    private static final Map<ResourceKey<BiomeBase>, VillagerType> i = (Map) SystemUtils.a((Object) Maps.newHashMap(), (hashmap) -> {
        hashmap.put(Biomes.BADLANDS, VillagerType.DESERT);
        hashmap.put(Biomes.BADLANDS_PLATEAU, VillagerType.DESERT);
        hashmap.put(Biomes.DESERT, VillagerType.DESERT);
        hashmap.put(Biomes.DESERT_HILLS, VillagerType.DESERT);
        hashmap.put(Biomes.DESERT_LAKES, VillagerType.DESERT);
        hashmap.put(Biomes.ERODED_BADLANDS, VillagerType.DESERT);
        hashmap.put(Biomes.MODIFIED_BADLANDS_PLATEAU, VillagerType.DESERT);
        hashmap.put(Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, VillagerType.DESERT);
        hashmap.put(Biomes.WOODED_BADLANDS_PLATEAU, VillagerType.DESERT);
        hashmap.put(Biomes.BAMBOO_JUNGLE, VillagerType.JUNGLE);
        hashmap.put(Biomes.BAMBOO_JUNGLE_HILLS, VillagerType.JUNGLE);
        hashmap.put(Biomes.JUNGLE, VillagerType.JUNGLE);
        hashmap.put(Biomes.JUNGLE_EDGE, VillagerType.JUNGLE);
        hashmap.put(Biomes.JUNGLE_HILLS, VillagerType.JUNGLE);
        hashmap.put(Biomes.MODIFIED_JUNGLE, VillagerType.JUNGLE);
        hashmap.put(Biomes.MODIFIED_JUNGLE_EDGE, VillagerType.JUNGLE);
        hashmap.put(Biomes.SAVANNA_PLATEAU, VillagerType.SAVANNA);
        hashmap.put(Biomes.SAVANNA, VillagerType.SAVANNA);
        hashmap.put(Biomes.SHATTERED_SAVANNA, VillagerType.SAVANNA);
        hashmap.put(Biomes.SHATTERED_SAVANNA_PLATEAU, VillagerType.SAVANNA);
        hashmap.put(Biomes.DEEP_FROZEN_OCEAN, VillagerType.SNOW);
        hashmap.put(Biomes.FROZEN_OCEAN, VillagerType.SNOW);
        hashmap.put(Biomes.FROZEN_RIVER, VillagerType.SNOW);
        hashmap.put(Biomes.ICE_SPIKES, VillagerType.SNOW);
        hashmap.put(Biomes.SNOWY_BEACH, VillagerType.SNOW);
        hashmap.put(Biomes.SNOWY_MOUNTAINS, VillagerType.SNOW);
        hashmap.put(Biomes.SNOWY_TAIGA, VillagerType.SNOW);
        hashmap.put(Biomes.SNOWY_TAIGA_HILLS, VillagerType.SNOW);
        hashmap.put(Biomes.SNOWY_TAIGA_MOUNTAINS, VillagerType.SNOW);
        hashmap.put(Biomes.SNOWY_TUNDRA, VillagerType.SNOW);
        hashmap.put(Biomes.SWAMP, VillagerType.SWAMP);
        hashmap.put(Biomes.SWAMP_HILLS, VillagerType.SWAMP);
        hashmap.put(Biomes.GIANT_SPRUCE_TAIGA, VillagerType.TAIGA);
        hashmap.put(Biomes.GIANT_SPRUCE_TAIGA_HILLS, VillagerType.TAIGA);
        hashmap.put(Biomes.GIANT_TREE_TAIGA, VillagerType.TAIGA);
        hashmap.put(Biomes.GIANT_TREE_TAIGA_HILLS, VillagerType.TAIGA);
        hashmap.put(Biomes.GRAVELLY_MOUNTAINS, VillagerType.TAIGA);
        hashmap.put(Biomes.MODIFIED_GRAVELLY_MOUNTAINS, VillagerType.TAIGA);
        hashmap.put(Biomes.MOUNTAIN_EDGE, VillagerType.TAIGA);
        hashmap.put(Biomes.MOUNTAINS, VillagerType.TAIGA);
        hashmap.put(Biomes.TAIGA, VillagerType.TAIGA);
        hashmap.put(Biomes.TAIGA_HILLS, VillagerType.TAIGA);
        hashmap.put(Biomes.TAIGA_MOUNTAINS, VillagerType.TAIGA);
        hashmap.put(Biomes.WOODED_MOUNTAINS, VillagerType.TAIGA);
    });

    private VillagerType(String s) {
        this.h = s;
    }

    public String toString() {
        return this.h;
    }

    private static VillagerType a(String s) {
        return (VillagerType) IRegistry.a((IRegistry) IRegistry.VILLAGER_TYPE, new MinecraftKey(s), (Object) (new VillagerType(s)));
    }

    public static VillagerType a(Optional<ResourceKey<BiomeBase>> optional) {
        return (VillagerType) optional.flatMap((resourcekey) -> {
            return Optional.ofNullable(VillagerType.i.get(resourcekey));
        }).orElse(VillagerType.PLAINS);
    }
}
