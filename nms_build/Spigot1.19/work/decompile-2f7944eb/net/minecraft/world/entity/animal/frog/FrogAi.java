package net.minecraft.world.entity.animal.frog;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import net.minecraft.world.entity.ai.behavior.BehaviorAttackTargetForget;
import net.minecraft.world.entity.ai.behavior.BehaviorAttackTargetSet;
import net.minecraft.world.entity.ai.behavior.BehaviorGate;
import net.minecraft.world.entity.ai.behavior.BehaviorGateSingle;
import net.minecraft.world.entity.ai.behavior.BehaviorLook;
import net.minecraft.world.entity.ai.behavior.BehaviorLookTarget;
import net.minecraft.world.entity.ai.behavior.BehaviorLookWalk;
import net.minecraft.world.entity.ai.behavior.BehaviorMakeLoveAnimal;
import net.minecraft.world.entity.ai.behavior.BehaviorNop;
import net.minecraft.world.entity.ai.behavior.BehaviorRunIf;
import net.minecraft.world.entity.ai.behavior.BehaviorRunSometimes;
import net.minecraft.world.entity.ai.behavior.BehaviorStrollRandomUnconstrained;
import net.minecraft.world.entity.ai.behavior.BehaviorUtil;
import net.minecraft.world.entity.ai.behavior.BehavorMove;
import net.minecraft.world.entity.ai.behavior.CountDownCooldownTicks;
import net.minecraft.world.entity.ai.behavior.Croak;
import net.minecraft.world.entity.ai.behavior.FollowTemptation;
import net.minecraft.world.entity.ai.behavior.LongJumpMidJump;
import net.minecraft.world.entity.ai.behavior.LongJumpToPreferredBlock;
import net.minecraft.world.entity.ai.behavior.RandomSwim;
import net.minecraft.world.entity.ai.behavior.TryFindLand;
import net.minecraft.world.entity.ai.behavior.TryFindLandNearWater;
import net.minecraft.world.entity.ai.behavior.TryLaySpawnOnWaterNearLand;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.level.block.Blocks;

public class FrogAi {

    private static final float SPEED_MULTIPLIER_WHEN_PANICKING = 2.0F;
    private static final float SPEED_MULTIPLIER_WHEN_MAKING_LOVE = 1.0F;
    private static final float SPEED_MULTIPLIER_WHEN_IDLING = 1.0F;
    private static final float SPEED_MULTIPLIER_ON_LAND = 1.0F;
    private static final float SPEED_MULTIPLIER_IN_WATER = 0.75F;
    private static final UniformInt TIME_BETWEEN_LONG_JUMPS = UniformInt.of(100, 140);
    private static final int MAX_LONG_JUMP_HEIGHT = 2;
    private static final int MAX_LONG_JUMP_WIDTH = 4;
    private static final float MAX_JUMP_VELOCITY = 1.5F;
    private static final float SPEED_MULTIPLIER_WHEN_TEMPTED = 1.25F;

    public FrogAi() {}

    protected static void initMemories(Frog frog, RandomSource randomsource) {
        frog.getBrain().setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, (Object) FrogAi.TIME_BETWEEN_LONG_JUMPS.sample(randomsource));
    }

    protected static BehaviorController<?> makeBrain(BehaviorController<Frog> behaviorcontroller) {
        initCoreActivity(behaviorcontroller);
        initIdleActivity(behaviorcontroller);
        initSwimActivity(behaviorcontroller);
        initLaySpawnActivity(behaviorcontroller);
        initTongueActivity(behaviorcontroller);
        initJumpActivity(behaviorcontroller);
        behaviorcontroller.setCoreActivities(ImmutableSet.of(Activity.CORE));
        behaviorcontroller.setDefaultActivity(Activity.IDLE);
        behaviorcontroller.useDefaultActivity();
        return behaviorcontroller;
    }

    private static void initCoreActivity(BehaviorController<Frog> behaviorcontroller) {
        behaviorcontroller.addActivity(Activity.CORE, 0, ImmutableList.of(new AnimalPanic(2.0F), new BehaviorLook(45, 90), new BehavorMove(), new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS), new CountDownCooldownTicks(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS)));
    }

    private static void initIdleActivity(BehaviorController<Frog> behaviorcontroller) {
        behaviorcontroller.addActivityWithConditions(Activity.IDLE, ImmutableList.of(Pair.of(0, new BehaviorRunSometimes<>(new BehaviorLookTarget(EntityTypes.PLAYER, 6.0F), UniformInt.of(30, 60))), Pair.of(0, new BehaviorMakeLoveAnimal(EntityTypes.FROG, 1.0F)), Pair.of(1, new FollowTemptation((entityliving) -> {
            return 1.25F;
        })), Pair.of(2, new BehaviorAttackTargetSet<>(FrogAi::canAttack, (frog) -> {
            return frog.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE);
        })), Pair.of(3, new TryFindLand(6, 1.0F)), Pair.of(4, new BehaviorGateSingle<>(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), ImmutableList.of(Pair.of(new BehaviorStrollRandomUnconstrained(1.0F), 1), Pair.of(new BehaviorLookWalk(1.0F, 3), 1), Pair.of(new Croak(), 3), Pair.of(new BehaviorRunIf<>(Entity::isOnGround, new BehaviorNop(5, 20)), 2))))), ImmutableSet.of(Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.IS_IN_WATER, MemoryStatus.VALUE_ABSENT)));
    }

    private static void initSwimActivity(BehaviorController<Frog> behaviorcontroller) {
        behaviorcontroller.addActivityWithConditions(Activity.SWIM, ImmutableList.of(Pair.of(0, new BehaviorRunSometimes<>(new BehaviorLookTarget(EntityTypes.PLAYER, 6.0F), UniformInt.of(30, 60))), Pair.of(1, new FollowTemptation((entityliving) -> {
            return 1.25F;
        })), Pair.of(2, new BehaviorAttackTargetSet<>(FrogAi::canAttack, (frog) -> {
            return frog.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE);
        })), Pair.of(3, new TryFindLand(8, 1.5F)), Pair.of(5, new BehaviorGate<>(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), ImmutableSet.of(), BehaviorGate.Order.ORDERED, BehaviorGate.Execution.TRY_ALL, ImmutableList.of(Pair.of(new RandomSwim(0.75F), 1), Pair.of(new BehaviorStrollRandomUnconstrained(1.0F, true), 1), Pair.of(new BehaviorLookWalk(1.0F, 3), 1), Pair.of(new BehaviorRunIf<>(Entity::isInWaterOrBubble, new BehaviorNop(30, 60)), 5))))), ImmutableSet.of(Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.IS_IN_WATER, MemoryStatus.VALUE_PRESENT)));
    }

    private static void initLaySpawnActivity(BehaviorController<Frog> behaviorcontroller) {
        behaviorcontroller.addActivityWithConditions(Activity.LAY_SPAWN, ImmutableList.of(Pair.of(0, new BehaviorRunSometimes<>(new BehaviorLookTarget(EntityTypes.PLAYER, 6.0F), UniformInt.of(30, 60))), Pair.of(1, new BehaviorAttackTargetSet<>(FrogAi::canAttack, (frog) -> {
            return frog.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE);
        })), Pair.of(2, new TryFindLandNearWater(8, 1.0F)), Pair.of(3, new TryLaySpawnOnWaterNearLand(Blocks.FROGSPAWN, MemoryModuleType.IS_PREGNANT)), Pair.of(4, new BehaviorGateSingle<>(ImmutableList.of(Pair.of(new BehaviorStrollRandomUnconstrained(1.0F), 2), Pair.of(new BehaviorLookWalk(1.0F, 3), 1), Pair.of(new Croak(), 2), Pair.of(new BehaviorRunIf<>(Entity::isOnGround, new BehaviorNop(5, 20)), 1))))), ImmutableSet.of(Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.IS_PREGNANT, MemoryStatus.VALUE_PRESENT)));
    }

    private static void initJumpActivity(BehaviorController<Frog> behaviorcontroller) {
        behaviorcontroller.addActivityWithConditions(Activity.LONG_JUMP, ImmutableList.of(Pair.of(0, new LongJumpMidJump(FrogAi.TIME_BETWEEN_LONG_JUMPS, SoundEffects.FROG_STEP)), Pair.of(1, new LongJumpToPreferredBlock<>(FrogAi.TIME_BETWEEN_LONG_JUMPS, 2, 4, 1.5F, (frog) -> {
            return SoundEffects.FROG_LONG_JUMP;
        }, TagsBlock.FROG_PREFER_JUMP_TO, 0.5F, (iblockdata) -> {
            return iblockdata.is(Blocks.LILY_PAD);
        }))), ImmutableSet.of(Pair.of(MemoryModuleType.TEMPTING_PLAYER, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.BREED_TARGET, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.IS_IN_WATER, MemoryStatus.VALUE_ABSENT)));
    }

    private static void initTongueActivity(BehaviorController<Frog> behaviorcontroller) {
        behaviorcontroller.addActivityAndRemoveMemoryWhenStopped(Activity.TONGUE, 0, ImmutableList.of(new BehaviorAttackTargetForget<>(), new ShootTongue(SoundEffects.FROG_TONGUE, SoundEffects.FROG_EAT)), MemoryModuleType.ATTACK_TARGET);
    }

    private static boolean canAttack(Frog frog) {
        return !BehaviorUtil.isBreeding(frog);
    }

    public static void updateActivity(Frog frog) {
        frog.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.TONGUE, Activity.LAY_SPAWN, Activity.LONG_JUMP, Activity.SWIM, Activity.IDLE));
    }

    public static RecipeItemStack getTemptations() {
        return Frog.TEMPTATION_ITEM;
    }
}
