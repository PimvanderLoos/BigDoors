package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.INamable;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumCreatureType;
import org.slf4j.Logger;

public class BiomeSettingsMobs {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final float DEFAULT_CREATURE_SPAWN_PROBABILITY = 0.1F;
    public static final WeightedRandomList<BiomeSettingsMobs.c> EMPTY_MOB_LIST = WeightedRandomList.create();
    public static final BiomeSettingsMobs EMPTY = (new BiomeSettingsMobs.a()).build();
    public static final MapCodec<BiomeSettingsMobs> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        RecordCodecBuilder recordcodecbuilder = Codec.floatRange(0.0F, 0.9999999F).optionalFieldOf("creature_spawn_probability", 0.1F).forGetter((biomesettingsmobs) -> {
            return biomesettingsmobs.creatureGenerationProbability;
        });
        Codec codec = EnumCreatureType.CODEC;
        Codec codec1 = WeightedRandomList.codec(BiomeSettingsMobs.c.CODEC);
        Logger logger = BiomeSettingsMobs.LOGGER;

        Objects.requireNonNull(logger);
        return instance.group(recordcodecbuilder, Codec.simpleMap(codec, codec1.promotePartial(SystemUtils.prefix("Spawn data: ", logger::error)), INamable.keys(EnumCreatureType.values())).fieldOf("spawners").forGetter((biomesettingsmobs) -> {
            return biomesettingsmobs.spawners;
        }), Codec.simpleMap(BuiltInRegistries.ENTITY_TYPE.byNameCodec(), BiomeSettingsMobs.b.CODEC, BuiltInRegistries.ENTITY_TYPE).fieldOf("spawn_costs").forGetter((biomesettingsmobs) -> {
            return biomesettingsmobs.mobSpawnCosts;
        })).apply(instance, BiomeSettingsMobs::new);
    });
    private final float creatureGenerationProbability;
    private final Map<EnumCreatureType, WeightedRandomList<BiomeSettingsMobs.c>> spawners;
    private final Map<EntityTypes<?>, BiomeSettingsMobs.b> mobSpawnCosts;

    BiomeSettingsMobs(float f, Map<EnumCreatureType, WeightedRandomList<BiomeSettingsMobs.c>> map, Map<EntityTypes<?>, BiomeSettingsMobs.b> map1) {
        this.creatureGenerationProbability = f;
        this.spawners = ImmutableMap.copyOf(map);
        this.mobSpawnCosts = ImmutableMap.copyOf(map1);
    }

    public WeightedRandomList<BiomeSettingsMobs.c> getMobs(EnumCreatureType enumcreaturetype) {
        return (WeightedRandomList) this.spawners.getOrDefault(enumcreaturetype, BiomeSettingsMobs.EMPTY_MOB_LIST);
    }

    @Nullable
    public BiomeSettingsMobs.b getMobSpawnCost(EntityTypes<?> entitytypes) {
        return (BiomeSettingsMobs.b) this.mobSpawnCosts.get(entitytypes);
    }

    public float getCreatureProbability() {
        return this.creatureGenerationProbability;
    }

    public static record b(double energyBudget, double charge) {

        public static final Codec<BiomeSettingsMobs.b> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(Codec.DOUBLE.fieldOf("energy_budget").forGetter((biomesettingsmobs_b) -> {
                return biomesettingsmobs_b.energyBudget;
            }), Codec.DOUBLE.fieldOf("charge").forGetter((biomesettingsmobs_b) -> {
                return biomesettingsmobs_b.charge;
            })).apply(instance, BiomeSettingsMobs.b::new);
        });
    }

    public static class c extends WeightedEntry.a {

        public static final Codec<BiomeSettingsMobs.c> CODEC = ExtraCodecs.validate(RecordCodecBuilder.create((instance) -> {
            return instance.group(BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter((biomesettingsmobs_c) -> {
                return biomesettingsmobs_c.type;
            }), Weight.CODEC.fieldOf("weight").forGetter(WeightedEntry.a::getWeight), ExtraCodecs.POSITIVE_INT.fieldOf("minCount").forGetter((biomesettingsmobs_c) -> {
                return biomesettingsmobs_c.minCount;
            }), ExtraCodecs.POSITIVE_INT.fieldOf("maxCount").forGetter((biomesettingsmobs_c) -> {
                return biomesettingsmobs_c.maxCount;
            })).apply(instance, BiomeSettingsMobs.c::new);
        }), (biomesettingsmobs_c) -> {
            return biomesettingsmobs_c.minCount > biomesettingsmobs_c.maxCount ? DataResult.error(() -> {
                return "minCount needs to be smaller or equal to maxCount";
            }) : DataResult.success(biomesettingsmobs_c);
        });
        public final EntityTypes<?> type;
        public final int minCount;
        public final int maxCount;

        public c(EntityTypes<?> entitytypes, int i, int j, int k) {
            this(entitytypes, Weight.of(i), j, k);
        }

        public c(EntityTypes<?> entitytypes, Weight weight, int i, int j) {
            super(weight);
            this.type = entitytypes.getCategory() == EnumCreatureType.MISC ? EntityTypes.PIG : entitytypes;
            this.minCount = i;
            this.maxCount = j;
        }

        public String toString() {
            MinecraftKey minecraftkey = EntityTypes.getKey(this.type);

            return minecraftkey + "*(" + this.minCount + "-" + this.maxCount + "):" + this.getWeight();
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

        public a() {}

        public BiomeSettingsMobs.a addSpawn(EnumCreatureType enumcreaturetype, BiomeSettingsMobs.c biomesettingsmobs_c) {
            ((List) this.spawners.get(enumcreaturetype)).add(biomesettingsmobs_c);
            return this;
        }

        public BiomeSettingsMobs.a addMobCharge(EntityTypes<?> entitytypes, double d0, double d1) {
            this.mobSpawnCosts.put(entitytypes, new BiomeSettingsMobs.b(d1, d0));
            return this;
        }

        public BiomeSettingsMobs.a creatureGenerationProbability(float f) {
            this.creatureGenerationProbability = f;
            return this;
        }

        public BiomeSettingsMobs build() {
            return new BiomeSettingsMobs(this.creatureGenerationProbability, (Map) this.spawners.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (entry) -> {
                return WeightedRandomList.create((List) entry.getValue());
            })), ImmutableMap.copyOf(this.mobSpawnCosts));
        }
    }
}
