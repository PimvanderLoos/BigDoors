package net.minecraft.world.level.levelgen;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.data.worldgen.StructureFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.StructureSettingsFeature;
import net.minecraft.world.level.levelgen.feature.configurations.StructureSettingsStronghold;

public class StructureSettings {

    public static final Codec<StructureSettings> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(StructureSettingsStronghold.CODEC.optionalFieldOf("stronghold").forGetter((structuresettings) -> {
            return Optional.ofNullable(structuresettings.stronghold);
        }), Codec.simpleMap(IRegistry.STRUCTURE_FEATURE.byNameCodec(), StructureSettingsFeature.CODEC, IRegistry.STRUCTURE_FEATURE).fieldOf("structures").forGetter((structuresettings) -> {
            return structuresettings.structureConfig;
        })).apply(instance, StructureSettings::new);
    });
    public static final ImmutableMap<StructureGenerator<?>, StructureSettingsFeature> DEFAULTS = ImmutableMap.builder().put(StructureGenerator.VILLAGE, new StructureSettingsFeature(34, 8, 10387312)).put(StructureGenerator.DESERT_PYRAMID, new StructureSettingsFeature(32, 8, 14357617)).put(StructureGenerator.IGLOO, new StructureSettingsFeature(32, 8, 14357618)).put(StructureGenerator.JUNGLE_TEMPLE, new StructureSettingsFeature(32, 8, 14357619)).put(StructureGenerator.SWAMP_HUT, new StructureSettingsFeature(32, 8, 14357620)).put(StructureGenerator.PILLAGER_OUTPOST, new StructureSettingsFeature(32, 8, 165745296)).put(StructureGenerator.STRONGHOLD, new StructureSettingsFeature(1, 0, 0)).put(StructureGenerator.OCEAN_MONUMENT, new StructureSettingsFeature(32, 5, 10387313)).put(StructureGenerator.END_CITY, new StructureSettingsFeature(20, 11, 10387313)).put(StructureGenerator.WOODLAND_MANSION, new StructureSettingsFeature(80, 20, 10387319)).put(StructureGenerator.BURIED_TREASURE, new StructureSettingsFeature(1, 0, 0)).put(StructureGenerator.MINESHAFT, new StructureSettingsFeature(1, 0, 0)).put(StructureGenerator.RUINED_PORTAL, new StructureSettingsFeature(40, 15, 34222645)).put(StructureGenerator.SHIPWRECK, new StructureSettingsFeature(24, 4, 165745295)).put(StructureGenerator.OCEAN_RUIN, new StructureSettingsFeature(20, 8, 14357621)).put(StructureGenerator.BASTION_REMNANT, new StructureSettingsFeature(27, 4, 30084232)).put(StructureGenerator.NETHER_BRIDGE, new StructureSettingsFeature(27, 4, 30084232)).put(StructureGenerator.NETHER_FOSSIL, new StructureSettingsFeature(2, 1, 14357921)).build();
    public static final StructureSettingsStronghold DEFAULT_STRONGHOLD;
    private final Map<StructureGenerator<?>, StructureSettingsFeature> structureConfig;
    private final ImmutableMap<StructureGenerator<?>, ImmutableMultimap<StructureFeature<?, ?>, ResourceKey<BiomeBase>>> configuredStructures;
    @Nullable
    private final StructureSettingsStronghold stronghold;

    private StructureSettings(Map<StructureGenerator<?>, StructureSettingsFeature> map, @Nullable StructureSettingsStronghold structuresettingsstronghold) {
        this.stronghold = structuresettingsstronghold;
        this.structureConfig = map;
        HashMap<StructureGenerator<?>, Builder<StructureFeature<?, ?>, ResourceKey<BiomeBase>>> hashmap = new HashMap();

        StructureFeatures.registerStructures((structurefeature, resourcekey) -> {
            ((Builder) hashmap.computeIfAbsent(structurefeature.feature, (structuregenerator) -> {
                return ImmutableMultimap.builder();
            })).put(structurefeature, resourcekey);
        });
        this.configuredStructures = (ImmutableMap) hashmap.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (entry) -> {
            return ((Builder) entry.getValue()).build();
        }));
    }

    public StructureSettings(Optional<StructureSettingsStronghold> optional, Map<StructureGenerator<?>, StructureSettingsFeature> map) {
        this(map, (StructureSettingsStronghold) optional.orElse((Object) null));
    }

    public StructureSettings(boolean flag) {
        this((Map) Maps.newHashMap(StructureSettings.DEFAULTS), flag ? StructureSettings.DEFAULT_STRONGHOLD : null);
    }

    @VisibleForTesting
    public Map<StructureGenerator<?>, StructureSettingsFeature> structureConfig() {
        return this.structureConfig;
    }

    @Nullable
    public StructureSettingsFeature getConfig(StructureGenerator<?> structuregenerator) {
        return (StructureSettingsFeature) this.structureConfig.get(structuregenerator);
    }

    @Nullable
    public StructureSettingsStronghold stronghold() {
        return this.stronghold;
    }

    public ImmutableMultimap<StructureFeature<?, ?>, ResourceKey<BiomeBase>> structures(StructureGenerator<?> structuregenerator) {
        return (ImmutableMultimap) this.configuredStructures.getOrDefault(structuregenerator, ImmutableMultimap.of());
    }

    static {
        Iterator iterator = IRegistry.STRUCTURE_FEATURE.iterator();

        StructureGenerator structuregenerator;

        do {
            if (!iterator.hasNext()) {
                DEFAULT_STRONGHOLD = new StructureSettingsStronghold(32, 3, 128);
                return;
            }

            structuregenerator = (StructureGenerator) iterator.next();
        } while (StructureSettings.DEFAULTS.containsKey(structuregenerator));

        throw new IllegalStateException("Structure feature without default settings: " + IRegistry.STRUCTURE_FEATURE.getKey(structuregenerator));
    }
}
