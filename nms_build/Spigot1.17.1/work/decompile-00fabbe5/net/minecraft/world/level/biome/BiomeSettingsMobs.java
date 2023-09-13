package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.INamable;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumCreatureType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BiomeSettingsMobs {

    public static final Logger LOGGER = LogManager.getLogger();
    private static final float DEFAULT_CREATURE_SPAWN_PROBABILITY = 0.1F;
    public static final WeightedRandomList<BiomeSettingsMobs.c> EMPTY_MOB_LIST = WeightedRandomList.b();
    public static final BiomeSettingsMobs EMPTY = new BiomeSettingsMobs(0.1F, (Map) Stream.of(EnumCreatureType.values()).collect(ImmutableMap.toImmutableMap((enumcreaturetype) -> {
        return enumcreaturetype;
    }, (enumcreaturetype) -> {
        return BiomeSettingsMobs.EMPTY_MOB_LIST;
    })), ImmutableMap.of(), false);
    public static final MapCodec<BiomeSettingsMobs> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        RecordCodecBuilder recordcodecbuilder = Codec.floatRange(0.0F, 0.9999999F).optionalFieldOf("creature_spawn_probability", 0.1F).forGetter((biomesettingsmobs) -> {
            return biomesettingsmobs.creatureGenerationProbability;
        });
        Codec codec = EnumCreatureType.CODEC;
        Codec codec1 = WeightedRandomList.b(BiomeSettingsMobs.c.CODEC);
        Logger logger = BiomeSettingsMobs.LOGGER;

        Objects.requireNonNull(logger);
        return instance.group(recordcodecbuilder, Codec.simpleMap(codec, codec1.promotePartial(SystemUtils.a("Spawn data: ", logger::error)), INamable.a((INamable[]) EnumCreatureType.values())).fieldOf("spawners").forGetter((biomesettingsmobs) -> {
            return biomesettingsmobs.spawners;
        }), Codec.simpleMap(IRegistry.ENTITY_TYPE, BiomeSettingsMobs.b.CODEC, IRegistry.ENTITY_TYPE).fieldOf("spawn_costs").forGetter((biomesettingsmobs) -> {
            return biomesettingsmobs.mobSpawnCosts;
        }), Codec.BOOL.fieldOf("player_spawn_friendly").orElse(false).forGetter(BiomeSettingsMobs::b)).apply(instance, BiomeSettingsMobs::new);
    });
    private final float creatureGenerationProbability;
    private final Map<EnumCreatureType, WeightedRandomList<BiomeSettingsMobs.c>> spawners;
    private final Map<EntityTypes<?>, BiomeSettingsMobs.b> mobSpawnCosts;
    private final boolean playerSpawnFriendly;

    BiomeSettingsMobs(float f, Map<EnumCreatureType, WeightedRandomList<BiomeSettingsMobs.c>> map, Map<EntityTypes<?>, BiomeSettingsMobs.b> map1, boolean flag) {
        this.creatureGenerationProbability = f;
        this.spawners = ImmutableMap.copyOf(map);
        this.mobSpawnCosts = ImmutableMap.copyOf(map1);
        this.playerSpawnFriendly = flag;
    }

    public WeightedRandomList<BiomeSettingsMobs.c> a(EnumCreatureType enumcreaturetype) {
        return (WeightedRandomList) this.spawners.getOrDefault(enumcreaturetype, BiomeSettingsMobs.EMPTY_MOB_LIST);
    }

    @Nullable
    public BiomeSettingsMobs.b a(EntityTypes<?> entitytypes) {
        return (BiomeSettingsMobs.b) this.mobSpawnCosts.get(entitytypes);
    }

    public float a() {
        return this.creatureGenerationProbability;
    }

    public boolean b() {
        return this.playerSpawnFriendly;
    }

    public static class b {

        public static final Codec<BiomeSettingsMobs.b> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(Codec.DOUBLE.fieldOf("energy_budget").forGetter((biomesettingsmobs_b) -> {
                return biomesettingsmobs_b.energyBudget;
            }), Codec.DOUBLE.fieldOf("charge").forGetter((biomesettingsmobs_b) -> {
                return biomesettingsmobs_b.charge;
            })).apply(instance, BiomeSettingsMobs.b::new);
        });
        private final double energyBudget;
        private final double charge;

        b(double d0, double d1) {
            this.energyBudget = d0;
            this.charge = d1;
        }

        public double a() {
            return this.energyBudget;
        }

        public double b() {
            return this.charge;
        }
    }

    public static class c extends WeightedEntry.a {

        public static final Codec<BiomeSettingsMobs.c> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(IRegistry.ENTITY_TYPE.fieldOf("type").forGetter((biomesettingsmobs_c) -> {
                return biomesettingsmobs_c.type;
            }), Weight.CODEC.fieldOf("weight").forGetter(WeightedEntry.a::a), Codec.INT.fieldOf("minCount").forGetter((biomesettingsmobs_c) -> {
                return biomesettingsmobs_c.minCount;
            }), Codec.INT.fieldOf("maxCount").forGetter((biomesettingsmobs_c) -> {
                return biomesettingsmobs_c.maxCount;
            })).apply(instance, BiomeSettingsMobs.c::new);
        });
        public final EntityTypes<?> type;
        public final int minCount;
        public final int maxCount;

        public c(EntityTypes<?> entitytypes, int i, int j, int k) {
            this(entitytypes, Weight.a(i), j, k);
        }

        public c(EntityTypes<?> entitytypes, Weight weight, int i, int j) {
            super(weight);
            this.type = entitytypes.f() == EnumCreatureType.MISC ? EntityTypes.PIG : entitytypes;
            this.minCount = i;
            this.maxCount = j;
        }

        public String toString() {
            MinecraftKey minecraftkey = EntityTypes.getName(this.type);

            return minecraftkey + "*(" + this.minCount + "-" + this.maxCount + "):" + this.a();
        }
    }

    public static class a {

        private final Map<EnumCreatureType, List<BiomeSettingsMobs.c>> spawners = (Map) Stream.of(EnumCreatureType.values()).collect(ImmutableMap.toImmutableMap((enumcreaturetype) -> {
            return enumcreaturetype;
        }, (enumcreaturetype) -> {
            return Lists.newArrayList();
        }));
        private final Map<EntityTypes<?>, BiomeSettingsMobs.b> mobSpawnCosts = Maps.newLinkedHashMap();
        private float creatureGenerationProbability = 0.1F;
        private boolean playerCanSpawn;

        public a() {}

        public BiomeSettingsMobs.a a(EnumCreatureType enumcreaturetype, BiomeSettingsMobs.c biomesettingsmobs_c) {
            ((List) this.spawners.get(enumcreaturetype)).add(biomesettingsmobs_c);
            return this;
        }

        public BiomeSettingsMobs.a a(EntityTypes<?> entitytypes, double d0, double d1) {
            this.mobSpawnCosts.put(entitytypes, new BiomeSettingsMobs.b(d1, d0));
            return this;
        }

        public BiomeSettingsMobs.a a(float f) {
            this.creatureGenerationProbability = f;
            return this;
        }

        public BiomeSettingsMobs.a a() {
            this.playerCanSpawn = true;
            return this;
        }

        public BiomeSettingsMobs b() {
            return new BiomeSettingsMobs(this.creatureGenerationProbability, (Map) this.spawners.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (entry) -> {
                return WeightedRandomList.a((List) entry.getValue());
            })), ImmutableMap.copyOf(this.mobSpawnCosts), this.playerCanSpawn);
        }
    }
}
