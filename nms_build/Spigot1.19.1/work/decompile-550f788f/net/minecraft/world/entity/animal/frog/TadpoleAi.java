package net.minecraft.world.entity.animal.frog;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import net.minecraft.world.entity.ai.behavior.BehaviorGate;
import net.minecraft.world.entity.ai.behavior.BehaviorLook;
import net.minecraft.world.entity.ai.behavior.BehaviorLookTarget;
import net.minecraft.world.entity.ai.behavior.BehaviorLookWalk;
import net.minecraft.world.entity.ai.behavior.BehaviorNop;
import net.minecraft.world.entity.ai.behavior.BehaviorRunIf;
import net.minecraft.world.entity.ai.behavior.BehaviorRunSometimes;
import net.minecraft.world.entity.ai.behavior.BehavorMove;
import net.minecraft.world.entity.ai.behavior.CountDownCooldownTicks;
import net.minecraft.world.entity.ai.behavior.FollowTemptation;
import net.minecraft.world.entity.ai.behavior.RandomSwim;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.schedule.Activity;

public class TadpoleAi {

    private static final float SPEED_MULTIPLIER_WHEN_PANICKING = 2.0F;
    private static final float SPEED_MULTIPLIER_WHEN_IDLING_IN_WATER = 0.5F;
    private static final float SPEED_MULTIPLIER_WHEN_TEMPTED = 1.25F;

    public TadpoleAi() {}

    protected static BehaviorController<?> makeBrain(BehaviorController<Tadpole> behaviorcontroller) {
        initCoreActivity(behaviorcontroller);
        initIdleActivity(behaviorcontroller);
        behaviorcontroller.setCoreActivities(ImmutableSet.of(Activity.CORE));
        behaviorcontroller.setDefaultActivity(Activity.IDLE);
        behaviorcontroller.useDefaultActivity();
        return behaviorcontroller;
    }

    private static void initCoreActivity(BehaviorController<Tadpole> behaviorcontroller) {
        behaviorcontroller.addActivity(Activity.CORE, 0, ImmutableList.of(new AnimalPanic(2.0F), new BehaviorLook(45, 90), new BehavorMove(), new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS)));
    }

    private static void initIdleActivity(BehaviorController<Tadpole> behaviorcontroller) {
        behaviorcontroller.addActivity(Activity.IDLE, ImmutableList.of(Pair.of(0, new BehaviorRunSometimes<>(new BehaviorLookTarget(EntityTypes.PLAYER, 6.0F), UniformInt.of(30, 60))), Pair.of(1, new FollowTemptation((entityliving) -> {
            return 1.25F;
        })), Pair.of(2, new BehaviorGate<>(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), ImmutableSet.of(), BehaviorGate.Order.ORDERED, BehaviorGate.Execution.TRY_ALL, ImmutableList.of(Pair.of(new RandomSwim(0.5F), 2), Pair.of(new BehaviorLookWalk(0.5F, 3), 3), Pair.of(new BehaviorRunIf<>(Entity::isInWaterOrBubble, new BehaviorNop(30, 60)), 5))))));
    }

    public static void updateActivity(Tadpole tadpole) {
        tadpole.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.IDLE));
    }
}
