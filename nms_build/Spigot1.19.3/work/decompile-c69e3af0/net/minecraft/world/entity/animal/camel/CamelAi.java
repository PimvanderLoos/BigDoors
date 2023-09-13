package net.minecraft.world.entity.animal.camel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.function.Predicate;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorFollowAdult;
import net.minecraft.world.entity.ai.behavior.BehaviorGateSingle;
import net.minecraft.world.entity.ai.behavior.BehaviorLook;
import net.minecraft.world.entity.ai.behavior.BehaviorLookWalk;
import net.minecraft.world.entity.ai.behavior.BehaviorMakeLoveAnimal;
import net.minecraft.world.entity.ai.behavior.BehaviorNop;
import net.minecraft.world.entity.ai.behavior.BehaviorStrollRandomUnconstrained;
import net.minecraft.world.entity.ai.behavior.BehaviorSwim;
import net.minecraft.world.entity.ai.behavior.BehavorMove;
import net.minecraft.world.entity.ai.behavior.CountDownCooldownTicks;
import net.minecraft.world.entity.ai.behavior.FollowTemptation;
import net.minecraft.world.entity.ai.behavior.RandomLookAround;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTargetSometimes;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.crafting.RecipeItemStack;

public class CamelAi {

    private static final float SPEED_MULTIPLIER_WHEN_PANICKING = 4.0F;
    private static final float SPEED_MULTIPLIER_WHEN_IDLING = 2.0F;
    private static final float SPEED_MULTIPLIER_WHEN_TEMPTED = 2.5F;
    private static final float SPEED_MULTIPLIER_WHEN_FOLLOWING_ADULT = 2.5F;
    private static final float SPEED_MULTIPLIER_WHEN_MAKING_LOVE = 1.0F;
    private static final UniformInt ADULT_FOLLOW_RANGE = UniformInt.of(5, 16);
    private static final ImmutableList<SensorType<? extends Sensor<? super Camel>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY, SensorType.CAMEL_TEMPTATIONS, SensorType.NEAREST_ADULT);
    private static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.IS_PANICKING, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.GAZE_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, new MemoryModuleType[]{MemoryModuleType.BREED_TARGET, MemoryModuleType.NEAREST_VISIBLE_ADULT});

    public CamelAi() {}

    protected static void initMemories(Camel camel, RandomSource randomsource) {}

    public static BehaviorController.b<Camel> brainProvider() {
        return BehaviorController.provider(CamelAi.MEMORY_TYPES, CamelAi.SENSOR_TYPES);
    }

    protected static BehaviorController<?> makeBrain(BehaviorController<Camel> behaviorcontroller) {
        initCoreActivity(behaviorcontroller);
        initIdleActivity(behaviorcontroller);
        behaviorcontroller.setCoreActivities(ImmutableSet.of(Activity.CORE));
        behaviorcontroller.setDefaultActivity(Activity.IDLE);
        behaviorcontroller.useDefaultActivity();
        return behaviorcontroller;
    }

    private static void initCoreActivity(BehaviorController<Camel> behaviorcontroller) {
        behaviorcontroller.addActivity(Activity.CORE, 0, ImmutableList.of(new BehaviorSwim(0.8F), new CamelAi.a(4.0F), new BehaviorLook(45, 90), new BehavorMove(), new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS), new CountDownCooldownTicks(MemoryModuleType.GAZE_COOLDOWN_TICKS)));
    }

    private static void initIdleActivity(BehaviorController<Camel> behaviorcontroller) {
        behaviorcontroller.addActivity(Activity.IDLE, ImmutableList.of(Pair.of(0, SetEntityLookTargetSometimes.create(EntityTypes.PLAYER, 6.0F, UniformInt.of(30, 60))), Pair.of(1, new BehaviorMakeLoveAnimal(EntityTypes.CAMEL, 1.0F)), Pair.of(2, new FollowTemptation((entityliving) -> {
            return 2.5F;
        })), Pair.of(3, BehaviorBuilder.triggerIf(Predicate.not(Camel::refuseToMove), BehaviorFollowAdult.create(CamelAi.ADULT_FOLLOW_RANGE, 2.5F))), Pair.of(4, new RandomLookAround(UniformInt.of(150, 250), 30.0F, 0.0F, 0.0F)), Pair.of(5, new BehaviorGateSingle<>(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), ImmutableList.of(Pair.of(BehaviorBuilder.triggerIf(Predicate.not(Camel::refuseToMove), BehaviorStrollRandomUnconstrained.stroll(2.0F)), 1), Pair.of(BehaviorBuilder.triggerIf(Predicate.not(Camel::refuseToMove), BehaviorLookWalk.create(2.0F, 3)), 1), Pair.of(new CamelAi.b(20), 1), Pair.of(new BehaviorNop(30, 60), 1))))));
    }

    public static void updateActivity(Camel camel) {
        camel.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.IDLE));
    }

    public static RecipeItemStack getTemptations() {
        return Camel.TEMPTATION_ITEM;
    }

    public static class a extends AnimalPanic {

        public a(float f) {
            super(f);
        }

        @Override
        protected void start(WorldServer worldserver, EntityCreature entitycreature, long i) {
            if (entitycreature instanceof Camel) {
                Camel camel = (Camel) entitycreature;

                camel.standUpPanic();
            }

            super.start(worldserver, entitycreature, i);
        }
    }

    public static class b extends Behavior<Camel> {

        private final int minimalPoseTicks;

        public b(int i) {
            super(ImmutableMap.of());
            this.minimalPoseTicks = i * 20;
        }

        protected boolean checkExtraStartConditions(WorldServer worldserver, Camel camel) {
            return !camel.isInWater() && camel.getPoseTime() >= (long) this.minimalPoseTicks && !camel.isLeashed() && camel.isOnGround() && !camel.hasControllingPassenger();
        }

        protected void start(WorldServer worldserver, Camel camel, long i) {
            if (camel.isPoseSitting()) {
                camel.standUp();
            } else if (!camel.isPanicking()) {
                camel.sitDown();
            }

        }
    }
}
