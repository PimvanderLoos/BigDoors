package net.minecraft.world.entity.monster.hoglin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.TimeRange;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.behavior.BehaviorAttack;
import net.minecraft.world.entity.ai.behavior.BehaviorAttackTargetForget;
import net.minecraft.world.entity.ai.behavior.BehaviorAttackTargetSet;
import net.minecraft.world.entity.ai.behavior.BehaviorFollowAdult;
import net.minecraft.world.entity.ai.behavior.BehaviorGateSingle;
import net.minecraft.world.entity.ai.behavior.BehaviorLook;
import net.minecraft.world.entity.ai.behavior.BehaviorLookTarget;
import net.minecraft.world.entity.ai.behavior.BehaviorLookWalk;
import net.minecraft.world.entity.ai.behavior.BehaviorMakeLoveAnimal;
import net.minecraft.world.entity.ai.behavior.BehaviorNop;
import net.minecraft.world.entity.ai.behavior.BehaviorPacify;
import net.minecraft.world.entity.ai.behavior.BehaviorRemoveMemory;
import net.minecraft.world.entity.ai.behavior.BehaviorRunIf;
import net.minecraft.world.entity.ai.behavior.BehaviorRunSometimes;
import net.minecraft.world.entity.ai.behavior.BehaviorStrollRandomUnconstrained;
import net.minecraft.world.entity.ai.behavior.BehaviorUtil;
import net.minecraft.world.entity.ai.behavior.BehaviorWalkAway;
import net.minecraft.world.entity.ai.behavior.BehaviorWalkAwayOutOfRange;
import net.minecraft.world.entity.ai.behavior.BehavorMove;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.schedule.Activity;

public class HoglinAI {

    public static final int REPELLENT_DETECTION_RANGE_HORIZONTAL = 8;
    public static final int REPELLENT_DETECTION_RANGE_VERTICAL = 4;
    private static final UniformInt RETREAT_DURATION = TimeRange.rangeOfSeconds(5, 20);
    private static final int ATTACK_DURATION = 200;
    private static final int DESIRED_DISTANCE_FROM_PIGLIN_WHEN_IDLING = 8;
    private static final int DESIRED_DISTANCE_FROM_PIGLIN_WHEN_RETREATING = 15;
    private static final int ATTACK_INTERVAL = 40;
    private static final int BABY_ATTACK_INTERVAL = 15;
    private static final int REPELLENT_PACIFY_TIME = 200;
    private static final UniformInt ADULT_FOLLOW_RANGE = UniformInt.of(5, 16);
    private static final float SPEED_MULTIPLIER_WHEN_AVOIDING_REPELLENT = 1.0F;
    private static final float SPEED_MULTIPLIER_WHEN_RETREATING = 1.3F;
    private static final float SPEED_MULTIPLIER_WHEN_MAKING_LOVE = 0.6F;
    private static final float SPEED_MULTIPLIER_WHEN_IDLING = 0.4F;
    private static final float SPEED_MULTIPLIER_WHEN_FOLLOWING_ADULT = 0.6F;

    public HoglinAI() {}

    protected static BehaviorController<?> makeBrain(BehaviorController<EntityHoglin> behaviorcontroller) {
        initCoreActivity(behaviorcontroller);
        initIdleActivity(behaviorcontroller);
        initFightActivity(behaviorcontroller);
        initRetreatActivity(behaviorcontroller);
        behaviorcontroller.setCoreActivities(ImmutableSet.of(Activity.CORE));
        behaviorcontroller.setDefaultActivity(Activity.IDLE);
        behaviorcontroller.useDefaultActivity();
        return behaviorcontroller;
    }

    private static void initCoreActivity(BehaviorController<EntityHoglin> behaviorcontroller) {
        behaviorcontroller.addActivity(Activity.CORE, 0, ImmutableList.of(new BehaviorLook(45, 90), new BehavorMove()));
    }

    private static void initIdleActivity(BehaviorController<EntityHoglin> behaviorcontroller) {
        behaviorcontroller.addActivity(Activity.IDLE, 10, ImmutableList.of(new BehaviorPacify(MemoryModuleType.NEAREST_REPELLENT, 200), new BehaviorMakeLoveAnimal(EntityTypes.HOGLIN, 0.6F), BehaviorWalkAway.pos(MemoryModuleType.NEAREST_REPELLENT, 1.0F, 8, true), new BehaviorAttackTargetSet<>(HoglinAI::findNearestValidAttackTarget), new BehaviorRunIf<>(EntityHoglin::isAdult, BehaviorWalkAway.entity(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, 0.4F, 8, false)), new BehaviorRunSometimes<>(new BehaviorLookTarget(8.0F), UniformInt.of(30, 60)), new BehaviorFollowAdult<>(HoglinAI.ADULT_FOLLOW_RANGE, 0.6F), createIdleMovementBehaviors()));
    }

    private static void initFightActivity(BehaviorController<EntityHoglin> behaviorcontroller) {
        behaviorcontroller.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(new BehaviorPacify(MemoryModuleType.NEAREST_REPELLENT, 200), new BehaviorMakeLoveAnimal(EntityTypes.HOGLIN, 0.6F), new BehaviorWalkAwayOutOfRange(1.0F), new BehaviorRunIf<>(EntityHoglin::isAdult, new BehaviorAttack(40)), new BehaviorRunIf<>(EntityAgeable::isBaby, new BehaviorAttack(15)), new BehaviorAttackTargetForget<>(), new BehaviorRemoveMemory<>(HoglinAI::isBreeding, MemoryModuleType.ATTACK_TARGET)), MemoryModuleType.ATTACK_TARGET);
    }

    private static void initRetreatActivity(BehaviorController<EntityHoglin> behaviorcontroller) {
        behaviorcontroller.addActivityAndRemoveMemoryWhenStopped(Activity.AVOID, 10, ImmutableList.of(BehaviorWalkAway.entity(MemoryModuleType.AVOID_TARGET, 1.3F, 15, false), createIdleMovementBehaviors(), new BehaviorRunSometimes<>(new BehaviorLookTarget(8.0F), UniformInt.of(30, 60)), new BehaviorRemoveMemory<>(HoglinAI::wantsToStopFleeing, MemoryModuleType.AVOID_TARGET)), MemoryModuleType.AVOID_TARGET);
    }

    private static BehaviorGateSingle<EntityHoglin> createIdleMovementBehaviors() {
        return new BehaviorGateSingle<>(ImmutableList.of(Pair.of(new BehaviorStrollRandomUnconstrained(0.4F), 2), Pair.of(new BehaviorLookWalk(0.4F, 3), 2), Pair.of(new BehaviorNop(30, 60), 1)));
    }

    protected static void updateActivity(EntityHoglin entityhoglin) {
        BehaviorController<EntityHoglin> behaviorcontroller = entityhoglin.getBrain();
        Activity activity = (Activity) behaviorcontroller.getActiveNonCoreActivity().orElse((Object) null);

        behaviorcontroller.setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.AVOID, Activity.IDLE));
        Activity activity1 = (Activity) behaviorcontroller.getActiveNonCoreActivity().orElse((Object) null);

        if (activity != activity1) {
            Optional optional = getSoundForCurrentActivity(entityhoglin);

            Objects.requireNonNull(entityhoglin);
            optional.ifPresent(entityhoglin::playSound);
        }

        entityhoglin.setAggressive(behaviorcontroller.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
    }

    protected static void onHitTarget(EntityHoglin entityhoglin, EntityLiving entityliving) {
        if (!entityhoglin.isBaby()) {
            if (entityliving.getType() == EntityTypes.PIGLIN && piglinsOutnumberHoglins(entityhoglin)) {
                setAvoidTarget(entityhoglin, entityliving);
                broadcastRetreat(entityhoglin, entityliving);
            } else {
                broadcastAttackTarget(entityhoglin, entityliving);
            }
        }
    }

    private static void broadcastRetreat(EntityHoglin entityhoglin, EntityLiving entityliving) {
        getVisibleAdultHoglins(entityhoglin).forEach((entityhoglin1) -> {
            retreatFromNearestTarget(entityhoglin1, entityliving);
        });
    }

    private static void retreatFromNearestTarget(EntityHoglin entityhoglin, EntityLiving entityliving) {
        BehaviorController<EntityHoglin> behaviorcontroller = entityhoglin.getBrain();
        EntityLiving entityliving1 = BehaviorUtil.getNearestTarget(entityhoglin, behaviorcontroller.getMemory(MemoryModuleType.AVOID_TARGET), entityliving);

        entityliving1 = BehaviorUtil.getNearestTarget(entityhoglin, behaviorcontroller.getMemory(MemoryModuleType.ATTACK_TARGET), entityliving1);
        setAvoidTarget(entityhoglin, entityliving1);
    }

    private static void setAvoidTarget(EntityHoglin entityhoglin, EntityLiving entityliving) {
        entityhoglin.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
        entityhoglin.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        entityhoglin.getBrain().setMemoryWithExpiry(MemoryModuleType.AVOID_TARGET, entityliving, (long) HoglinAI.RETREAT_DURATION.sample(entityhoglin.level.random));
    }

    private static Optional<? extends EntityLiving> findNearestValidAttackTarget(EntityHoglin entityhoglin) {
        return !isPacified(entityhoglin) && !isBreeding(entityhoglin) ? entityhoglin.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER) : Optional.empty();
    }

    static boolean isPosNearNearestRepellent(EntityHoglin entityhoglin, BlockPosition blockposition) {
        Optional<BlockPosition> optional = entityhoglin.getBrain().getMemory(MemoryModuleType.NEAREST_REPELLENT);

        return optional.isPresent() && ((BlockPosition) optional.get()).closerThan((BaseBlockPosition) blockposition, 8.0D);
    }

    private static boolean wantsToStopFleeing(EntityHoglin entityhoglin) {
        return entityhoglin.isAdult() && !piglinsOutnumberHoglins(entityhoglin);
    }

    private static boolean piglinsOutnumberHoglins(EntityHoglin entityhoglin) {
        if (entityhoglin.isBaby()) {
            return false;
        } else {
            int i = (Integer) entityhoglin.getBrain().getMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT).orElse(0);
            int j = (Integer) entityhoglin.getBrain().getMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT).orElse(0) + 1;

            return i > j;
        }
    }

    protected static void wasHurtBy(EntityHoglin entityhoglin, EntityLiving entityliving) {
        BehaviorController<EntityHoglin> behaviorcontroller = entityhoglin.getBrain();

        behaviorcontroller.eraseMemory(MemoryModuleType.PACIFIED);
        behaviorcontroller.eraseMemory(MemoryModuleType.BREED_TARGET);
        if (entityhoglin.isBaby()) {
            retreatFromNearestTarget(entityhoglin, entityliving);
        } else {
            maybeRetaliate(entityhoglin, entityliving);
        }
    }

    private static void maybeRetaliate(EntityHoglin entityhoglin, EntityLiving entityliving) {
        if (!entityhoglin.getBrain().isActive(Activity.AVOID) || entityliving.getType() != EntityTypes.PIGLIN) {
            if (entityliving.getType() != EntityTypes.HOGLIN) {
                if (!BehaviorUtil.isOtherTargetMuchFurtherAwayThanCurrentAttackTarget(entityhoglin, entityliving, 4.0D)) {
                    if (Sensor.isEntityAttackable(entityhoglin, entityliving)) {
                        setAttackTarget(entityhoglin, entityliving);
                        broadcastAttackTarget(entityhoglin, entityliving);
                    }
                }
            }
        }
    }

    private static void setAttackTarget(EntityHoglin entityhoglin, EntityLiving entityliving) {
        BehaviorController<EntityHoglin> behaviorcontroller = entityhoglin.getBrain();

        behaviorcontroller.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        behaviorcontroller.eraseMemory(MemoryModuleType.BREED_TARGET);
        behaviorcontroller.setMemoryWithExpiry(MemoryModuleType.ATTACK_TARGET, entityliving, 200L);
    }

    private static void broadcastAttackTarget(EntityHoglin entityhoglin, EntityLiving entityliving) {
        getVisibleAdultHoglins(entityhoglin).forEach((entityhoglin1) -> {
            setAttackTargetIfCloserThanCurrent(entityhoglin1, entityliving);
        });
    }

    private static void setAttackTargetIfCloserThanCurrent(EntityHoglin entityhoglin, EntityLiving entityliving) {
        if (!isPacified(entityhoglin)) {
            Optional<EntityLiving> optional = entityhoglin.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
            EntityLiving entityliving1 = BehaviorUtil.getNearestTarget(entityhoglin, optional, entityliving);

            setAttackTarget(entityhoglin, entityliving1);
        }
    }

    public static Optional<SoundEffect> getSoundForCurrentActivity(EntityHoglin entityhoglin) {
        return entityhoglin.getBrain().getActiveNonCoreActivity().map((activity) -> {
            return getSoundForActivity(entityhoglin, activity);
        });
    }

    private static SoundEffect getSoundForActivity(EntityHoglin entityhoglin, Activity activity) {
        return activity != Activity.AVOID && !entityhoglin.isConverting() ? (activity == Activity.FIGHT ? SoundEffects.HOGLIN_ANGRY : (isNearRepellent(entityhoglin) ? SoundEffects.HOGLIN_RETREAT : SoundEffects.HOGLIN_AMBIENT)) : SoundEffects.HOGLIN_RETREAT;
    }

    private static List<EntityHoglin> getVisibleAdultHoglins(EntityHoglin entityhoglin) {
        return (List) entityhoglin.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS).orElse(ImmutableList.of());
    }

    private static boolean isNearRepellent(EntityHoglin entityhoglin) {
        return entityhoglin.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_REPELLENT);
    }

    private static boolean isBreeding(EntityHoglin entityhoglin) {
        return entityhoglin.getBrain().hasMemoryValue(MemoryModuleType.BREED_TARGET);
    }

    protected static boolean isPacified(EntityHoglin entityhoglin) {
        return entityhoglin.getBrain().hasMemoryValue(MemoryModuleType.PACIFIED);
    }
}
