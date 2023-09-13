package net.minecraft.world.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.SystemUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.random.SimpleWeightedRandomList;

public record MobSpawnerData(NBTTagCompound d, Optional<MobSpawnerData.a> e) {

    private final NBTTagCompound entityToSpawn;
    private final Optional<MobSpawnerData.a> customSpawnRules;
    public static final Codec<MobSpawnerData> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(NBTTagCompound.CODEC.fieldOf("entity").forGetter((mobspawnerdata) -> {
            return mobspawnerdata.entityToSpawn;
        }), MobSpawnerData.a.CODEC.optionalFieldOf("custom_spawn_rules").forGetter((mobspawnerdata) -> {
            return mobspawnerdata.customSpawnRules;
        })).apply(instance, MobSpawnerData::new);
    });
    public static final Codec<SimpleWeightedRandomList<MobSpawnerData>> LIST_CODEC = SimpleWeightedRandomList.wrappedCodecAllowingEmpty(MobSpawnerData.CODEC);
    public static final String DEFAULT_TYPE = "minecraft:pig";

    public MobSpawnerData() {
        this((NBTTagCompound) SystemUtils.make(new NBTTagCompound(), (nbttagcompound) -> {
            nbttagcompound.putString("id", "minecraft:pig");
        }), Optional.empty());
    }

    public MobSpawnerData(NBTTagCompound nbttagcompound, Optional<MobSpawnerData.a> optional) {
        MinecraftKey minecraftkey = MinecraftKey.tryParse(nbttagcompound.getString("id"));

        nbttagcompound.putString("id", minecraftkey != null ? minecraftkey.toString() : "minecraft:pig");
        this.entityToSpawn = nbttagcompound;
        this.customSpawnRules = optional;
    }

    public NBTTagCompound getEntityToSpawn() {
        return this.entityToSpawn;
    }

    public Optional<MobSpawnerData.a> getCustomSpawnRules() {
        return this.customSpawnRules;
    }

    public NBTTagCompound entityToSpawn() {
        return this.entityToSpawn;
    }

    public Optional<MobSpawnerData.a> customSpawnRules() {
        return this.customSpawnRules;
    }

    public static record a(InclusiveRange<Integer> b, InclusiveRange<Integer> c) {

        private final InclusiveRange<Integer> blockLightLimit;
        private final InclusiveRange<Integer> skyLightLimit;
        private static final InclusiveRange<Integer> LIGHT_RANGE = new InclusiveRange<>(0, 15);
        public static final Codec<MobSpawnerData.a> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(InclusiveRange.INT.optionalFieldOf("block_light_limit", MobSpawnerData.a.LIGHT_RANGE).flatXmap(MobSpawnerData.a::checkLightBoundaries, MobSpawnerData.a::checkLightBoundaries).forGetter((mobspawnerdata_a) -> {
                return mobspawnerdata_a.blockLightLimit;
            }), InclusiveRange.INT.optionalFieldOf("sky_light_limit", MobSpawnerData.a.LIGHT_RANGE).flatXmap(MobSpawnerData.a::checkLightBoundaries, MobSpawnerData.a::checkLightBoundaries).forGetter((mobspawnerdata_a) -> {
                return mobspawnerdata_a.skyLightLimit;
            })).apply(instance, MobSpawnerData.a::new);
        });

        public a(InclusiveRange<Integer> inclusiverange, InclusiveRange<Integer> inclusiverange1) {
            this.blockLightLimit = inclusiverange;
            this.skyLightLimit = inclusiverange1;
        }

        private static DataResult<InclusiveRange<Integer>> checkLightBoundaries(InclusiveRange<Integer> inclusiverange) {
            return !MobSpawnerData.a.LIGHT_RANGE.contains(inclusiverange) ? DataResult.error("Light values must be withing range " + MobSpawnerData.a.LIGHT_RANGE) : DataResult.success(inclusiverange);
        }

        public InclusiveRange<Integer> blockLightLimit() {
            return this.blockLightLimit;
        }

        public InclusiveRange<Integer> skyLightLimit() {
            return this.skyLightLimit;
        }
    }
}
