package net.minecraft.world.entity.ai.memory;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.IRegistry;
import net.minecraft.core.MinecraftSerializableUUID;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.BehaviorPosition;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.monster.hoglin.EntityHoglin;
import net.minecraft.world.entity.monster.piglin.EntityPiglinAbstract;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.pathfinder.PathEntity;

public class MemoryModuleType<U> {

    public static final MemoryModuleType<Void> DUMMY = a("dummy");
    public static final MemoryModuleType<GlobalPos> HOME = a("home", GlobalPos.a);
    public static final MemoryModuleType<GlobalPos> JOB_SITE = a("job_site", GlobalPos.a);
    public static final MemoryModuleType<GlobalPos> POTENTIAL_JOB_SITE = a("potential_job_site", GlobalPos.a);
    public static final MemoryModuleType<GlobalPos> MEETING_POINT = a("meeting_point", GlobalPos.a);
    public static final MemoryModuleType<List<GlobalPos>> SECONDARY_JOB_SITE = a("secondary_job_site");
    public static final MemoryModuleType<List<EntityLiving>> MOBS = a("mobs");
    public static final MemoryModuleType<List<EntityLiving>> VISIBLE_MOBS = a("visible_mobs");
    public static final MemoryModuleType<List<EntityLiving>> VISIBLE_VILLAGER_BABIES = a("visible_villager_babies");
    public static final MemoryModuleType<List<EntityHuman>> NEAREST_PLAYERS = a("nearest_players");
    public static final MemoryModuleType<EntityHuman> NEAREST_VISIBLE_PLAYER = a("nearest_visible_player");
    public static final MemoryModuleType<EntityHuman> NEAREST_VISIBLE_TARGETABLE_PLAYER = a("nearest_visible_targetable_player");
    public static final MemoryModuleType<MemoryTarget> WALK_TARGET = a("walk_target");
    public static final MemoryModuleType<BehaviorPosition> LOOK_TARGET = a("look_target");
    public static final MemoryModuleType<EntityLiving> ATTACK_TARGET = a("attack_target");
    public static final MemoryModuleType<Boolean> ATTACK_COOLING_DOWN = a("attack_cooling_down");
    public static final MemoryModuleType<EntityLiving> INTERACTION_TARGET = a("interaction_target");
    public static final MemoryModuleType<EntityAgeable> BREED_TARGET = a("breed_target");
    public static final MemoryModuleType<Entity> RIDE_TARGET = a("ride_target");
    public static final MemoryModuleType<PathEntity> PATH = a("path");
    public static final MemoryModuleType<List<GlobalPos>> INTERACTABLE_DOORS = a("interactable_doors");
    public static final MemoryModuleType<Set<GlobalPos>> DOORS_TO_CLOSE = a("doors_to_close");
    public static final MemoryModuleType<BlockPosition> NEAREST_BED = a("nearest_bed");
    public static final MemoryModuleType<DamageSource> HURT_BY = a("hurt_by");
    public static final MemoryModuleType<EntityLiving> HURT_BY_ENTITY = a("hurt_by_entity");
    public static final MemoryModuleType<EntityLiving> AVOID_TARGET = a("avoid_target");
    public static final MemoryModuleType<EntityLiving> NEAREST_HOSTILE = a("nearest_hostile");
    public static final MemoryModuleType<GlobalPos> HIDING_PLACE = a("hiding_place");
    public static final MemoryModuleType<Long> HEARD_BELL_TIME = a("heard_bell_time");
    public static final MemoryModuleType<Long> CANT_REACH_WALK_TARGET_SINCE = a("cant_reach_walk_target_since");
    public static final MemoryModuleType<Boolean> GOLEM_DETECTED_RECENTLY = a("golem_detected_recently", Codec.BOOL);
    public static final MemoryModuleType<Long> LAST_SLEPT = a("last_slept", Codec.LONG);
    public static final MemoryModuleType<Long> LAST_WOKEN = a("last_woken", Codec.LONG);
    public static final MemoryModuleType<Long> LAST_WORKED_AT_POI = a("last_worked_at_poi", Codec.LONG);
    public static final MemoryModuleType<EntityAgeable> NEAREST_VISIBLE_ADULY = a("nearest_visible_adult");
    public static final MemoryModuleType<EntityItem> NEAREST_VISIBLE_WANTED_ITEM = a("nearest_visible_wanted_item");
    public static final MemoryModuleType<EntityInsentient> NEAREST_VISIBLE_NEMSIS = a("nearest_visible_nemesis");
    public static final MemoryModuleType<UUID> ANGRY_AT = a("angry_at", MinecraftSerializableUUID.a);
    public static final MemoryModuleType<Boolean> UNIVERSAL_ANGER = a("universal_anger", Codec.BOOL);
    public static final MemoryModuleType<Boolean> ADMIRING_ITEM = a("admiring_item", Codec.BOOL);
    public static final MemoryModuleType<Integer> TIME_TRYING_TO_REACH_ADMIRE_ITEM = a("time_trying_to_reach_admire_item");
    public static final MemoryModuleType<Boolean> DISABLE_WALK_TO_ADMIRE_ITEM = a("disable_walk_to_admire_item");
    public static final MemoryModuleType<Boolean> ADMIRING_DISABLED = a("admiring_disabled", Codec.BOOL);
    public static final MemoryModuleType<Boolean> HUNTED_RECENTLY = a("hunted_recently", Codec.BOOL);
    public static final MemoryModuleType<BlockPosition> CELEBRATE_LOCATION = a("celebrate_location");
    public static final MemoryModuleType<Boolean> DANCING = a("dancing");
    public static final MemoryModuleType<EntityHoglin> NEAREST_VISIBLE_HUNTABLE_HOGLIN = a("nearest_visible_huntable_hoglin");
    public static final MemoryModuleType<EntityHoglin> NEAREST_VISIBLE_BABY_HOGLIN = a("nearest_visible_baby_hoglin");
    public static final MemoryModuleType<EntityHuman> NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD = a("nearest_targetable_player_not_wearing_gold");
    public static final MemoryModuleType<List<EntityPiglinAbstract>> NEARBY_ADULT_PIGLINS = a("nearby_adult_piglins");
    public static final MemoryModuleType<List<EntityPiglinAbstract>> NEAREST_VISIBLE_ADULT_PIGLINS = a("nearest_visible_adult_piglins");
    public static final MemoryModuleType<List<EntityHoglin>> NEAREST_VISIBLE_ADULT_HOGLINS = a("nearest_visible_adult_hoglins");
    public static final MemoryModuleType<EntityPiglinAbstract> NEAREST_VISIBLE_ADULT_PIGLIN = a("nearest_visible_adult_piglin");
    public static final MemoryModuleType<EntityLiving> NEAREST_VISIBLE_ZOMBIFIED = a("nearest_visible_zombified");
    public static final MemoryModuleType<Integer> VISIBLE_ADULT_PIGLIN_COUNT = a("visible_adult_piglin_count");
    public static final MemoryModuleType<Integer> VISIBLE_ADULT_HOGLIN_COUNT = a("visible_adult_hoglin_count");
    public static final MemoryModuleType<EntityHuman> NEAREST_PLAYER_HOLDING_WANTED_ITEM = a("nearest_player_holding_wanted_item");
    public static final MemoryModuleType<Boolean> ATE_RECENTLY = a("ate_recently");
    public static final MemoryModuleType<BlockPosition> NEAREST_REPELLENT = a("nearest_repellent");
    public static final MemoryModuleType<Boolean> PACIFIED = a("pacified");
    private final Optional<Codec<ExpirableMemory<U>>> ai;

    private MemoryModuleType(Optional<Codec<U>> optional) {
        this.ai = optional.map(ExpirableMemory::a);
    }

    public String toString() {
        return IRegistry.MEMORY_MODULE_TYPE.getKey(this).toString();
    }

    public Optional<Codec<ExpirableMemory<U>>> getSerializer() {
        return this.ai;
    }

    private static <U> MemoryModuleType<U> a(String s, Codec<U> codec) {
        return (MemoryModuleType) IRegistry.a((IRegistry) IRegistry.MEMORY_MODULE_TYPE, new MinecraftKey(s), (Object) (new MemoryModuleType<>(Optional.of(codec))));
    }

    private static <U> MemoryModuleType<U> a(String s) {
        return (MemoryModuleType) IRegistry.a((IRegistry) IRegistry.MEMORY_MODULE_TYPE, new MinecraftKey(s), (Object) (new MemoryModuleType<>(Optional.empty())));
    }
}
