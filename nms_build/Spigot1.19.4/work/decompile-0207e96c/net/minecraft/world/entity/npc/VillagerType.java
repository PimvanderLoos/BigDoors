package net.minecraft.world.entity.npc;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.SystemUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.Biomes;

public final class VillagerType {

    public static final VillagerType DESERT = register("desert");
    public static final VillagerType JUNGLE = register("jungle");
    public static final VillagerType PLAINS = register("plains");
    public static final VillagerType SAVANNA = register("savanna");
    public static final VillagerType SNOW = register("snow");
    public static final VillagerType SWAMP = register("swamp");
    public static final VillagerType TAIGA = register("taiga");
    private final String name;
    private static final Map<ResourceKey<BiomeBase>, VillagerType> BY_BIOME = (Map) SystemUtils.make(Maps.newHashMap(), (hashmap) -> {
        hashmap.put(Biomes.BADLANDS, VillagerType.DESERT);
        hashmap.put(Biomes.DESERT, VillagerType.DESERT);
        hashmap.put(Biomes.ERODED_BADLANDS, VillagerType.DESERT);
        hashmap.put(Biomes.WOODED_BADLANDS, VillagerType.DESERT);
        hashmap.put(Biomes.BAMBOO_JUNGLE, VillagerType.JUNGLE);
        hashmap.put(Biomes.JUNGLE, VillagerType.JUNGLE);
        hashmap.put(Biomes.SPARSE_JUNGLE, VillagerType.JUNGLE);
        hashmap.put(Biomes.SAVANNA_PLATEAU, VillagerType.SAVANNA);
        hashmap.put(Biomes.SAVANNA, VillagerType.SAVANNA);
        hashmap.put(Biomes.WINDSWEPT_SAVANNA, VillagerType.SAVANNA);
        hashmap.put(Biomes.DEEP_FROZEN_OCEAN, VillagerType.SNOW);
        hashmap.put(Biomes.FROZEN_OCEAN, VillagerType.SNOW);
        hashmap.put(Biomes.FROZEN_RIVER, VillagerType.SNOW);
        hashmap.put(Biomes.ICE_SPIKES, VillagerType.SNOW);
        hashmap.put(Biomes.SNOWY_BEACH, VillagerType.SNOW);
        hashmap.put(Biomes.SNOWY_TAIGA, VillagerType.SNOW);
        hashmap.put(Biomes.SNOWY_PLAINS, VillagerType.SNOW);
        hashmap.put(Biomes.GROVE, VillagerType.SNOW);
        hashmap.put(Biomes.SNOWY_SLOPES, VillagerType.SNOW);
        hashmap.put(Biomes.FROZEN_PEAKS, VillagerType.SNOW);
        hashmap.put(Biomes.JAGGED_PEAKS, VillagerType.SNOW);
        hashmap.put(Biomes.SWAMP, VillagerType.SWAMP);
        hashmap.put(Biomes.MANGROVE_SWAMP, VillagerType.SWAMP);
        hashmap.put(Biomes.OLD_GROWTH_SPRUCE_TAIGA, VillagerType.TAIGA);
        hashmap.put(Biomes.OLD_GROWTH_PINE_TAIGA, VillagerType.TAIGA);
        hashmap.put(Biomes.WINDSWEPT_GRAVELLY_HILLS, VillagerType.TAIGA);
        hashmap.put(Biomes.WINDSWEPT_HILLS, VillagerType.TAIGA);
        hashmap.put(Biomes.TAIGA, VillagerType.TAIGA);
        hashmap.put(Biomes.WINDSWEPT_FOREST, VillagerType.TAIGA);
    });

    private VillagerType(String s) {
        this.name = s;
    }

    public String toString() {
        return this.name;
    }

    private static VillagerType register(String s) {
        return (VillagerType) IRegistry.register(BuiltInRegistries.VILLAGER_TYPE, new MinecraftKey(s), new VillagerType(s));
    }

    public static VillagerType byBiome(Holder<BiomeBase> holder) {
        Optional optional = holder.unwrapKey();
        Map map = VillagerType.BY_BIOME;

        Objects.requireNonNull(map);
        return (VillagerType) optional.map(map::get).orElse(VillagerType.PLAINS);
    }
}
