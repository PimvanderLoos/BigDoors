package net.minecraft.world.entity.animal.axolotl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPosition;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsItem;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.behavior.BehaviorAttack;
import net.minecraft.world.entity.ai.behavior.BehaviorAttackTargetForget;
import net.minecraft.world.entity.ai.behavior.BehaviorAttackTargetSet;
import net.minecraft.world.entity.ai.behavior.BehaviorFollowAdult;
import net.minecraft.world.entity.ai.behavior.BehaviorGate;
import net.minecraft.world.entity.ai.behavior.BehaviorGateSingle;
import net.minecraft.world.entity.ai.behavior.BehaviorLook;
import net.minecraft.world.entity.ai.behavior.BehaviorLookTarget;
import net.minecraft.world.entity.ai.behavior.BehaviorLookWalk;
import net.minecraft.world.entity.ai.behavior.BehaviorMakeLoveAnimal;
import net.minecraft.world.entity.ai.behavior.BehaviorNop;
import net.minecraft.world.entity.ai.behavior.BehaviorPosition;
import net.minecraft.world.entity.ai.behavior.BehaviorRemoveMemory;
import net.minecraft.world.entity.ai.behavior.BehaviorRunIf;
import net.minecraft.world.entity.ai.behavior.BehaviorRunSometimes;
import net.minecraft.world.entity.ai.behavior.BehaviorStrollRandomUnconstrained;
import net.minecraft.world.entity.ai.behavior.BehaviorWalkAwayOutOfRange;
import net.minecraft.world.entity.ai.behavior.BehavorMove;
import net.minecraft.world.entity.ai.behavior.CountDownCooldownTicks;
import net.minecraft.world.entity.ai.behavior.FollowTemptation;
import net.minecraft.world.entity.ai.behavior.RandomSwim;
import net.minecraft.world.entity.ai.behavior.TryFindWater;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.level.World;

public class AxolotlAi {

    private static final UniformInt ADULT_FOLLOW_RANGE = UniformInt.a(5, 16);
    private static final float SPEED_MULTIPLIER_WHEN_MAKING_LOVE = 0.2F;
    private static final float SPEED_MULTIPLIER_ON_LAND = 0.15F;
    private static final float SPEED_MULTIPLIER_WHEN_IDLING_IN_WATER = 0.5F;
    private static final float SPEED_MULTIPLIER_WHEN_CHASING_IN_WATER = 0.6F;
    private static final float SPEED_MULTIPLIER_WHEN_FOLLOWING_ADULT_IN_WATER = 0.6F;

    public AxolotlAi() {}

    protected static BehaviorController<?> a(BehaviorController<Axolotl> behaviorcontroller) {
        d(behaviorcontroller);
        e(behaviorcontroller);
        c(behaviorcontroller);
        b(behaviorcontroller);
        behaviorcontroller.a((Set) ImmutableSet.of(Activity.CORE));
        behaviorcontroller.b(Activity.IDLE);
        behaviorcontroller.e();
        return behaviorcontroller;
    }

    private static void b(BehaviorController<Axolotl> behaviorcontroller) {
        behaviorcontroller.a(Activity.PLAY_DEAD, ImmutableList.of(Pair.of(0, new PlayDead()), Pair.of(1, new BehaviorRemoveMemory<>(AxolotlAi::c, MemoryModuleType.PLAY_DEAD_TICKS))), ImmutableSet.of(Pair.of(MemoryModuleType.PLAY_DEAD_TICKS, MemoryStatus.VALUE_PRESENT)), ImmutableSet.of(MemoryModuleType.PLAY_DEAD_TICKS));
    }

    private static void c(BehaviorController<Axolotl> behaviorcontroller) {
        behaviorcontroller.a(Activity.FIGHT, 0, ImmutableList.of(new BehaviorAttackTargetForget<>(Axolotl::a), new BehaviorWalkAwayOutOfRange(AxolotlAi::b), new BehaviorAttack(20), new BehaviorRemoveMemory<>(AxolotlAi::c, MemoryModuleType.ATTACK_TARGET)), MemoryModuleType.ATTACK_TARGET);
    }

    private static void d(BehaviorController<Axolotl> behaviorcontroller) {
        behaviorcontroller.a(Activity.CORE, 0, ImmutableList.of(new BehaviorLook(45, 90), new BehavorMove(), new ValidatePlayDead(), new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS)));
    }

    private static void e(BehaviorController<Axolotl> behaviorcontroller) {
        behaviorcontroller.a(Activity.IDLE, ImmutableList.of(Pair.of(0, new BehaviorRunSometimes<>(new BehaviorLookTarget(EntityTypes.PLAYER, 6.0F), UniformInt.a(30, 60))), Pair.of(1, new BehaviorMakeLoveAnimal(EntityTypes.AXOLOTL, 0.2F)), Pair.of(2, new BehaviorGateSingle<>(ImmutableList.of(Pair.of(new FollowTemptation(AxolotlAi::d), 1), Pair.of(new BehaviorFollowAdult<>(AxolotlAi.ADULT_FOLLOW_RANGE, AxolotlAi::c), 1)))), Pair.of(3, new BehaviorAttackTargetSet<>(AxolotlAi::b)), Pair.of(3, new TryFindWater(6, 0.15F)), Pair.of(4, new BehaviorGate<>(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), ImmutableSet.of(), BehaviorGate.Order.ORDERED, BehaviorGate.Execution.TRY_ALL, ImmutableList.of(Pair.of(new RandomSwim(0.5F), 2), Pair.of(new BehaviorStrollRandomUnconstrained(0.15F, false), 2), Pair.of(new BehaviorLookWalk(AxolotlAi::a, AxolotlAi::d, 3), 3), Pair.of(new BehaviorRunIf<>(Entity::aO, new BehaviorNop(30, 60)), 5), Pair.of(new BehaviorRunIf<>(Entity::isOnGround, new BehaviorNop(200, 400)), 5))))));
    }

    private static boolean a(EntityLiving entityliving) {
        World world = entityliving.level;
        Optional<BehaviorPosition> optional = entityliving.getBehaviorController().getMemory(MemoryModuleType.LOOK_TARGET);

        if (optional.isPresent()) {
            BlockPosition blockposition = ((BehaviorPosition) optional.get()).b();

            return world.B(blockposition) == entityliving.aO();
        } else {
            return false;
        }
    }

    public static void a(Axolotl axolotl) {
        BehaviorController<Axolotl> behaviorcontroller = axolotl.getBehaviorController();
        Activity activity = (Activity) behaviorcontroller.f().orElse((Object) null);

        if (activity != Activity.PLAY_DEAD) {
            behaviorcontroller.a((List) ImmutableList.of(Activity.PLAY_DEAD, Activity.FIGHT, Activity.IDLE));
            if (activity == Activity.FIGHT && behaviorcontroller.f().orElse((Object) null) != Activity.FIGHT) {
                behaviorcontroller.a(MemoryModuleType.HAS_HUNTING_COOLDOWN, true, 2400L);
            }
        }

    }

    private static float b(EntityLiving entityliving) {
        return entityliving.aO() ? 0.6F : 0.15F;
    }

    private static float c(EntityLiving entityliving) {
        return entityliving.aO() ? 0.6F : 0.15F;
    }

    private static float d(EntityLiving entityliving) {
        return entityliving.aO() ? 0.5F : 0.15F;
    }

    private static Optional<? extends EntityLiving> b(Axolotl axolotl) {
        return c(axolotl) ? Optional.empty() : axolotl.getBehaviorController().getMemory(MemoryModuleType.NEAREST_ATTACKABLE);
    }

    private static boolean c(Axolotl axolotl) {
        return axolotl.getBehaviorController().hasMemory(MemoryModuleType.BREED_TARGET);
    }

    public static RecipeItemStack a() {
        return RecipeItemStack.a((Tag) TagsItem.AXOLOTL_TEMPT_ITEMS);
    }
}
