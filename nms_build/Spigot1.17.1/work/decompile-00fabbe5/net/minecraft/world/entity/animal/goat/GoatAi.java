package net.minecraft.world.entity.animal.goat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Set;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import net.minecraft.world.entity.ai.behavior.BehaviorFollowAdult;
import net.minecraft.world.entity.ai.behavior.BehaviorGateSingle;
import net.minecraft.world.entity.ai.behavior.BehaviorLook;
import net.minecraft.world.entity.ai.behavior.BehaviorLookTarget;
import net.minecraft.world.entity.ai.behavior.BehaviorLookWalk;
import net.minecraft.world.entity.ai.behavior.BehaviorMakeLoveAnimal;
import net.minecraft.world.entity.ai.behavior.BehaviorNop;
import net.minecraft.world.entity.ai.behavior.BehaviorRunSometimes;
import net.minecraft.world.entity.ai.behavior.BehaviorStrollRandomUnconstrained;
import net.minecraft.world.entity.ai.behavior.BehaviorSwim;
import net.minecraft.world.entity.ai.behavior.BehavorMove;
import net.minecraft.world.entity.ai.behavior.CountDownCooldownTicks;
import net.minecraft.world.entity.ai.behavior.FollowTemptation;
import net.minecraft.world.entity.ai.behavior.LongJumpMidJump;
import net.minecraft.world.entity.ai.behavior.LongJumpToRandomPos;
import net.minecraft.world.entity.ai.behavior.PrepareRamNearestTarget;
import net.minecraft.world.entity.ai.behavior.RamTarget;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeItemStack;

public class GoatAi {

    public static final int RAM_PREPARE_TIME = 20;
    public static final int RAM_MAX_DISTANCE = 7;
    private static final UniformInt ADULT_FOLLOW_RANGE = UniformInt.a(5, 16);
    private static final float SPEED_MULTIPLIER_WHEN_MAKING_LOVE = 1.0F;
    private static final float SPEED_MULTIPLIER_WHEN_IDLING = 1.0F;
    private static final float SPEED_MULTIPLIER_WHEN_FOLLOWING_ADULT = 1.25F;
    private static final float SPEED_MULTIPLIER_WHEN_TEMPTED = 1.25F;
    private static final float SPEED_MULTIPLIER_WHEN_PANICKING = 2.0F;
    private static final float SPEED_MULTIPLIER_WHEN_PREPARING_TO_RAM = 1.25F;
    private static final UniformInt TIME_BETWEEN_LONG_JUMPS = UniformInt.a(600, 1200);
    public static final int MAX_LONG_JUMP_HEIGHT = 5;
    public static final int MAX_LONG_JUMP_WIDTH = 5;
    public static final float MAX_JUMP_VELOCITY = 1.5F;
    private static final UniformInt TIME_BETWEEN_RAMS = UniformInt.a(600, 6000);
    private static final UniformInt TIME_BETWEEN_RAMS_SCREAMER = UniformInt.a(100, 300);
    private static final PathfinderTargetCondition RAM_TARGET_CONDITIONS = PathfinderTargetCondition.a().a((entityliving) -> {
        return !entityliving.getEntityType().equals(EntityTypes.GOAT) && entityliving.level.getWorldBorder().a(entityliving.getBoundingBox());
    });
    private static final float SPEED_MULTIPLIER_WHEN_RAMMING = 3.0F;
    public static final int RAM_MIN_DISTANCE = 4;
    public static final float ADULT_RAM_KNOCKBACK_FORCE = 2.5F;
    public static final float BABY_RAM_KNOCKBACK_FORCE = 1.0F;

    public GoatAi() {}

    protected static void a(Goat goat) {
        goat.getBehaviorController().setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, (Object) GoatAi.TIME_BETWEEN_LONG_JUMPS.a(goat.level.random));
        goat.getBehaviorController().setMemory(MemoryModuleType.RAM_COOLDOWN_TICKS, (Object) GoatAi.TIME_BETWEEN_RAMS.a(goat.level.random));
    }

    protected static BehaviorController<?> a(BehaviorController<Goat> behaviorcontroller) {
        b(behaviorcontroller);
        c(behaviorcontroller);
        d(behaviorcontroller);
        e(behaviorcontroller);
        behaviorcontroller.a((Set) ImmutableSet.of(Activity.CORE));
        behaviorcontroller.b(Activity.IDLE);
        behaviorcontroller.e();
        return behaviorcontroller;
    }

    private static void b(BehaviorController<Goat> behaviorcontroller) {
        behaviorcontroller.a(Activity.CORE, 0, ImmutableList.of(new BehaviorSwim(0.8F), new AnimalPanic(2.0F), new BehaviorLook(45, 90), new BehavorMove(), new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS), new CountDownCooldownTicks(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS), new CountDownCooldownTicks(MemoryModuleType.RAM_COOLDOWN_TICKS)));
    }

    private static void c(BehaviorController<Goat> behaviorcontroller) {
        behaviorcontroller.a(Activity.IDLE, ImmutableList.of(Pair.of(0, new BehaviorRunSometimes<>(new BehaviorLookTarget(EntityTypes.PLAYER, 6.0F), UniformInt.a(30, 60))), Pair.of(0, new BehaviorMakeLoveAnimal(EntityTypes.GOAT, 1.0F)), Pair.of(1, new FollowTemptation((entityliving) -> {
            return 1.25F;
        })), Pair.of(2, new BehaviorFollowAdult<>(GoatAi.ADULT_FOLLOW_RANGE, 1.25F)), Pair.of(3, new BehaviorGateSingle<>(ImmutableList.of(Pair.of(new BehaviorStrollRandomUnconstrained(1.0F), 2), Pair.of(new BehaviorLookWalk(1.0F, 3), 2), Pair.of(new BehaviorNop(30, 60), 1))))), ImmutableSet.of(Pair.of(MemoryModuleType.RAM_TARGET, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryStatus.VALUE_ABSENT)));
    }

    private static void d(BehaviorController<Goat> behaviorcontroller) {
        behaviorcontroller.a(Activity.LONG_JUMP, ImmutableList.of(Pair.of(0, new LongJumpMidJump(GoatAi.TIME_BETWEEN_LONG_JUMPS, SoundEffects.GOAT_STEP)), Pair.of(1, new LongJumpToRandomPos<>(GoatAi.TIME_BETWEEN_LONG_JUMPS, 5, 5, 1.5F, (goat) -> {
            return goat.isScreamingGoat() ? SoundEffects.GOAT_SCREAMING_LONG_JUMP : SoundEffects.GOAT_LONG_JUMP;
        }))), ImmutableSet.of(Pair.of(MemoryModuleType.TEMPTING_PLAYER, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.BREED_TARGET, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT)));
    }

    private static void e(BehaviorController<Goat> behaviorcontroller) {
        behaviorcontroller.a(Activity.RAM, ImmutableList.of(Pair.of(0, new RamTarget<>((goat) -> {
            return goat.isScreamingGoat() ? GoatAi.TIME_BETWEEN_RAMS_SCREAMER : GoatAi.TIME_BETWEEN_RAMS;
        }, GoatAi.RAM_TARGET_CONDITIONS, 3.0F, (goat) -> {
            return goat.isBaby() ? 1.0D : 2.5D;
        }, (goat) -> {
            return goat.isScreamingGoat() ? SoundEffects.GOAT_SCREAMING_RAM_IMPACT : SoundEffects.GOAT_RAM_IMPACT;
        })), Pair.of(1, new PrepareRamNearestTarget<>((goat) -> {
            return goat.isScreamingGoat() ? GoatAi.TIME_BETWEEN_RAMS_SCREAMER.a() : GoatAi.TIME_BETWEEN_RAMS.a();
        }, 4, 7, 1.25F, GoatAi.RAM_TARGET_CONDITIONS, 20, (goat) -> {
            return goat.isScreamingGoat() ? SoundEffects.GOAT_SCREAMING_PREPARE_RAM : SoundEffects.GOAT_PREPARE_RAM;
        }))), ImmutableSet.of(Pair.of(MemoryModuleType.TEMPTING_PLAYER, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.BREED_TARGET, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT)));
    }

    public static void b(Goat goat) {
        goat.getBehaviorController().a((List) ImmutableList.of(Activity.RAM, Activity.LONG_JUMP, Activity.IDLE));
    }

    public static RecipeItemStack a() {
        return RecipeItemStack.a(Items.WHEAT);
    }
}
