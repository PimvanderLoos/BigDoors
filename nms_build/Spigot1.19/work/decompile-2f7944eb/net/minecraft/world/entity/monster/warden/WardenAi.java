package net.minecraft.world.entity.monster.warden;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.List;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorAttack;
import net.minecraft.world.entity.ai.behavior.BehaviorAttackTargetForget;
import net.minecraft.world.entity.ai.behavior.BehaviorGateSingle;
import net.minecraft.world.entity.ai.behavior.BehaviorLook;
import net.minecraft.world.entity.ai.behavior.BehaviorLookTarget;
import net.minecraft.world.entity.ai.behavior.BehaviorNop;
import net.minecraft.world.entity.ai.behavior.BehaviorStrollRandomUnconstrained;
import net.minecraft.world.entity.ai.behavior.BehaviorSwim;
import net.minecraft.world.entity.ai.behavior.BehaviorTarget;
import net.minecraft.world.entity.ai.behavior.BehaviorWalkAwayOutOfRange;
import net.minecraft.world.entity.ai.behavior.BehavorMove;
import net.minecraft.world.entity.ai.behavior.GoToTargetLocation;
import net.minecraft.world.entity.ai.behavior.warden.Digging;
import net.minecraft.world.entity.ai.behavior.warden.Emerging;
import net.minecraft.world.entity.ai.behavior.warden.ForceUnmount;
import net.minecraft.world.entity.ai.behavior.warden.Roar;
import net.minecraft.world.entity.ai.behavior.warden.SetRoarTarget;
import net.minecraft.world.entity.ai.behavior.warden.SetWardenLookTarget;
import net.minecraft.world.entity.ai.behavior.warden.Sniffing;
import net.minecraft.world.entity.ai.behavior.warden.SonicBoom;
import net.minecraft.world.entity.ai.behavior.warden.TryToSniff;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;

public class WardenAi {

    private static final float SPEED_MULTIPLIER_WHEN_IDLING = 0.5F;
    private static final float SPEED_MULTIPLIER_WHEN_INVESTIGATING = 0.7F;
    private static final float SPEED_MULTIPLIER_WHEN_FIGHTING = 1.2F;
    private static final int MELEE_ATTACK_COOLDOWN = 18;
    private static final int DIGGING_DURATION = MathHelper.ceil(100.0F);
    public static final int EMERGE_DURATION = MathHelper.ceil(133.59999F);
    public static final int ROAR_DURATION = MathHelper.ceil(84.0F);
    private static final int SNIFFING_DURATION = MathHelper.ceil(83.2F);
    public static final int DIGGING_COOLDOWN = 1200;
    private static final int DISTURBANCE_LOCATION_EXPIRY_TIME = 100;
    private static final List<SensorType<? extends Sensor<? super Warden>>> SENSOR_TYPES = List.of(SensorType.NEAREST_PLAYERS, SensorType.WARDEN_ENTITY_SENSOR);
    private static final List<MemoryModuleType<?>> MEMORY_TYPES = List.of(MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleType.ROAR_TARGET, MemoryModuleType.DISTURBANCE_LOCATION, MemoryModuleType.RECENT_PROJECTILE, MemoryModuleType.IS_SNIFFING, MemoryModuleType.IS_EMERGING, MemoryModuleType.ROAR_SOUND_DELAY, MemoryModuleType.DIG_COOLDOWN, MemoryModuleType.ROAR_SOUND_COOLDOWN, MemoryModuleType.SNIFF_COOLDOWN, MemoryModuleType.TOUCH_COOLDOWN, MemoryModuleType.VIBRATION_COOLDOWN, MemoryModuleType.SONIC_BOOM_COOLDOWN, MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN, MemoryModuleType.SONIC_BOOM_SOUND_DELAY);
    private static final Behavior<Warden> DIG_COOLDOWN_SETTER = new Behavior<Warden>(ImmutableMap.of(MemoryModuleType.DIG_COOLDOWN, MemoryStatus.REGISTERED)) {
        protected void start(WorldServer worldserver, Warden warden, long i) {
            WardenAi.setDigCooldown(warden);
        }
    };

    public WardenAi() {}

    public static void updateActivity(Warden warden) {
        warden.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.EMERGE, Activity.DIG, Activity.ROAR, Activity.FIGHT, Activity.INVESTIGATE, Activity.SNIFF, Activity.IDLE));
    }

    protected static BehaviorController<?> makeBrain(Warden warden, Dynamic<?> dynamic) {
        BehaviorController.b<Warden> behaviorcontroller_b = BehaviorController.provider(WardenAi.MEMORY_TYPES, WardenAi.SENSOR_TYPES);
        BehaviorController<Warden> behaviorcontroller = behaviorcontroller_b.makeBrain(dynamic);

        initCoreActivity(behaviorcontroller);
        initEmergeActivity(behaviorcontroller);
        initDiggingActivity(behaviorcontroller);
        initIdleActivity(behaviorcontroller);
        initRoarActivity(behaviorcontroller);
        initFightActivity(warden, behaviorcontroller);
        initInvestigateActivity(behaviorcontroller);
        initSniffingActivity(behaviorcontroller);
        behaviorcontroller.setCoreActivities(ImmutableSet.of(Activity.CORE));
        behaviorcontroller.setDefaultActivity(Activity.IDLE);
        behaviorcontroller.useDefaultActivity();
        return behaviorcontroller;
    }

    private static void initCoreActivity(BehaviorController<Warden> behaviorcontroller) {
        behaviorcontroller.addActivity(Activity.CORE, 0, ImmutableList.of(new BehaviorSwim(0.8F), new SetWardenLookTarget(), new BehaviorLook(45, 90), new BehavorMove()));
    }

    private static void initEmergeActivity(BehaviorController<Warden> behaviorcontroller) {
        behaviorcontroller.addActivityAndRemoveMemoryWhenStopped(Activity.EMERGE, 5, ImmutableList.of(new Emerging<>(WardenAi.EMERGE_DURATION)), MemoryModuleType.IS_EMERGING);
    }

    private static void initDiggingActivity(BehaviorController<Warden> behaviorcontroller) {
        behaviorcontroller.addActivityWithConditions(Activity.DIG, ImmutableList.of(Pair.of(0, new ForceUnmount()), Pair.of(1, new Digging<>(WardenAi.DIGGING_DURATION))), ImmutableSet.of(Pair.of(MemoryModuleType.ROAR_TARGET, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.DIG_COOLDOWN, MemoryStatus.VALUE_ABSENT)));
    }

    private static void initIdleActivity(BehaviorController<Warden> behaviorcontroller) {
        behaviorcontroller.addActivity(Activity.IDLE, 10, ImmutableList.of(new SetRoarTarget<>(Warden::getEntityAngryAt), new TryToSniff(), new BehaviorGateSingle<>(ImmutableMap.of(MemoryModuleType.IS_SNIFFING, MemoryStatus.VALUE_ABSENT), ImmutableList.of(Pair.of(new BehaviorStrollRandomUnconstrained(0.5F), 2), Pair.of(new BehaviorNop(30, 60), 1)))));
    }

    private static void initInvestigateActivity(BehaviorController<Warden> behaviorcontroller) {
        behaviorcontroller.addActivityAndRemoveMemoryWhenStopped(Activity.INVESTIGATE, 5, ImmutableList.of(new SetRoarTarget<>(Warden::getEntityAngryAt), new GoToTargetLocation<>(MemoryModuleType.DISTURBANCE_LOCATION, 2, 0.7F)), MemoryModuleType.DISTURBANCE_LOCATION);
    }

    private static void initSniffingActivity(BehaviorController<Warden> behaviorcontroller) {
        behaviorcontroller.addActivityAndRemoveMemoryWhenStopped(Activity.SNIFF, 5, ImmutableList.of(new SetRoarTarget<>(Warden::getEntityAngryAt), new Sniffing<>(WardenAi.SNIFFING_DURATION)), MemoryModuleType.IS_SNIFFING);
    }

    private static void initRoarActivity(BehaviorController<Warden> behaviorcontroller) {
        behaviorcontroller.addActivityAndRemoveMemoryWhenStopped(Activity.ROAR, 10, ImmutableList.of(new Roar()), MemoryModuleType.ROAR_TARGET);
    }

    private static void initFightActivity(Warden warden, BehaviorController<Warden> behaviorcontroller) {
        behaviorcontroller.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(WardenAi.DIG_COOLDOWN_SETTER, new BehaviorAttackTargetForget<>((entityliving) -> {
            return !warden.getAngerLevel().isAngry() || !warden.canTargetEntity(entityliving);
        }, WardenAi::onTargetInvalid, false), new BehaviorLookTarget((entityliving) -> {
            return isTarget(warden, entityliving);
        }, (float) warden.getAttributeValue(GenericAttributes.FOLLOW_RANGE)), new BehaviorWalkAwayOutOfRange(1.2F), new SonicBoom(), new BehaviorAttack(18)), MemoryModuleType.ATTACK_TARGET);
    }

    private static boolean isTarget(Warden warden, EntityLiving entityliving) {
        return warden.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).filter((entityliving1) -> {
            return entityliving1 == entityliving;
        }).isPresent();
    }

    private static void onTargetInvalid(Warden warden, EntityLiving entityliving) {
        if (!warden.canTargetEntity(entityliving)) {
            warden.clearAnger(entityliving);
        }

        setDigCooldown(warden);
    }

    public static void setDigCooldown(EntityLiving entityliving) {
        if (entityliving.getBrain().hasMemoryValue(MemoryModuleType.DIG_COOLDOWN)) {
            entityliving.getBrain().setMemoryWithExpiry(MemoryModuleType.DIG_COOLDOWN, Unit.INSTANCE, 1200L);
        }

    }

    public static void setDisturbanceLocation(Warden warden, BlockPosition blockposition) {
        if (warden.level.getWorldBorder().isWithinBounds(blockposition) && !warden.getEntityAngryAt().isPresent() && !warden.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).isPresent()) {
            setDigCooldown(warden);
            warden.getBrain().setMemoryWithExpiry(MemoryModuleType.SNIFF_COOLDOWN, Unit.INSTANCE, 100L);
            warden.getBrain().setMemoryWithExpiry(MemoryModuleType.LOOK_TARGET, new BehaviorTarget(blockposition), 100L);
            warden.getBrain().setMemoryWithExpiry(MemoryModuleType.DISTURBANCE_LOCATION, blockposition, 100L);
            warden.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        }
    }
}
