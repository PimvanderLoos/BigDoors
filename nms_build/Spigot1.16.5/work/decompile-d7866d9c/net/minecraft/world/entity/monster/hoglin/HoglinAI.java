package net.minecraft.world.entity.monster.hoglin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.IntRange;
import net.minecraft.util.TimeRange;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.IEntitySelector;
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
import net.minecraft.world.entity.schedule.Activity;

public class HoglinAI {

    private static final IntRange a = TimeRange.a(5, 20);
    private static final IntRange b = IntRange.a(5, 16);

    protected static BehaviorController<?> a(BehaviorController<EntityHoglin> behaviorcontroller) {
        b(behaviorcontroller);
        c(behaviorcontroller);
        d(behaviorcontroller);
        e(behaviorcontroller);
        behaviorcontroller.a((Set) ImmutableSet.of(Activity.CORE));
        behaviorcontroller.b(Activity.IDLE);
        behaviorcontroller.e();
        return behaviorcontroller;
    }

    private static void b(BehaviorController<EntityHoglin> behaviorcontroller) {
        behaviorcontroller.a(Activity.CORE, 0, ImmutableList.of(new BehaviorLook(45, 90), new BehavorMove()));
    }

    private static void c(BehaviorController<EntityHoglin> behaviorcontroller) {
        behaviorcontroller.a(Activity.IDLE, 10, ImmutableList.of(new BehaviorPacify(MemoryModuleType.NEAREST_REPELLENT, 200), new BehaviorMakeLoveAnimal(EntityTypes.HOGLIN, 0.6F), BehaviorWalkAway.a(MemoryModuleType.NEAREST_REPELLENT, 1.0F, 8, true), new BehaviorAttackTargetSet<>(HoglinAI::d), new BehaviorRunIf<>(EntityHoglin::eL, BehaviorWalkAway.b(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, 0.4F, 8, false)), new BehaviorRunSometimes<>(new BehaviorLookTarget(8.0F), IntRange.a(30, 60)), new BehaviorFollowAdult<>(HoglinAI.b, 0.6F), a()));
    }

    private static void d(BehaviorController<EntityHoglin> behaviorcontroller) {
        behaviorcontroller.a(Activity.FLIGHT, 10, ImmutableList.of(new BehaviorPacify(MemoryModuleType.NEAREST_REPELLENT, 200), new BehaviorMakeLoveAnimal(EntityTypes.HOGLIN, 0.6F), new BehaviorWalkAwayOutOfRange(1.0F), new BehaviorRunIf<>(EntityHoglin::eL, new BehaviorAttack(40)), new BehaviorRunIf<>(EntityAgeable::isBaby, new BehaviorAttack(15)), new BehaviorAttackTargetForget<>(), new BehaviorRemoveMemory<>(HoglinAI::i, MemoryModuleType.ATTACK_TARGET)), MemoryModuleType.ATTACK_TARGET);
    }

    private static void e(BehaviorController<EntityHoglin> behaviorcontroller) {
        behaviorcontroller.a(Activity.AVOID, 10, ImmutableList.of(BehaviorWalkAway.b(MemoryModuleType.AVOID_TARGET, 1.3F, 15, false), a(), new BehaviorRunSometimes<>(new BehaviorLookTarget(8.0F), IntRange.a(30, 60)), new BehaviorRemoveMemory<>(HoglinAI::e, MemoryModuleType.AVOID_TARGET)), MemoryModuleType.AVOID_TARGET);
    }

    private static BehaviorGateSingle<EntityHoglin> a() {
        return new BehaviorGateSingle<>(ImmutableList.of(Pair.of(new BehaviorStrollRandomUnconstrained(0.4F), 2), Pair.of(new BehaviorLookWalk(0.4F, 3), 2), Pair.of(new BehaviorNop(30, 60), 1)));
    }

    protected static void a(EntityHoglin entityhoglin) {
        BehaviorController<EntityHoglin> behaviorcontroller = entityhoglin.getBehaviorController();
        Activity activity = (Activity) behaviorcontroller.f().orElse((Object) null);

        behaviorcontroller.a((List) ImmutableList.of(Activity.FLIGHT, Activity.AVOID, Activity.IDLE));
        Activity activity1 = (Activity) behaviorcontroller.f().orElse((Object) null);

        if (activity != activity1) {
            b(entityhoglin).ifPresent(entityhoglin::a);
        }

        entityhoglin.setAggressive(behaviorcontroller.hasMemory(MemoryModuleType.ATTACK_TARGET));
    }

    protected static void a(EntityHoglin entityhoglin, EntityLiving entityliving) {
        if (!entityhoglin.isBaby()) {
            if (entityliving.getEntityType() == EntityTypes.PIGLIN && f(entityhoglin)) {
                e(entityhoglin, entityliving);
                c(entityhoglin, entityliving);
            } else {
                h(entityhoglin, entityliving);
            }
        }
    }

    private static void c(EntityHoglin entityhoglin, EntityLiving entityliving) {
        g(entityhoglin).forEach((entityhoglin1) -> {
            d(entityhoglin1, entityliving);
        });
    }

    private static void d(EntityHoglin entityhoglin, EntityLiving entityliving) {
        BehaviorController<EntityHoglin> behaviorcontroller = entityhoglin.getBehaviorController();
        EntityLiving entityliving1 = BehaviorUtil.a((EntityLiving) entityhoglin, behaviorcontroller.getMemory(MemoryModuleType.AVOID_TARGET), entityliving);

        entityliving1 = BehaviorUtil.a((EntityLiving) entityhoglin, behaviorcontroller.getMemory(MemoryModuleType.ATTACK_TARGET), entityliving1);
        e(entityhoglin, entityliving1);
    }

    private static void e(EntityHoglin entityhoglin, EntityLiving entityliving) {
        entityhoglin.getBehaviorController().removeMemory(MemoryModuleType.ATTACK_TARGET);
        entityhoglin.getBehaviorController().removeMemory(MemoryModuleType.WALK_TARGET);
        entityhoglin.getBehaviorController().a(MemoryModuleType.AVOID_TARGET, entityliving, (long) HoglinAI.a.a(entityhoglin.world.random));
    }

    private static Optional<? extends EntityLiving> d(EntityHoglin entityhoglin) {
        return !c(entityhoglin) && !i(entityhoglin) ? entityhoglin.getBehaviorController().getMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER) : Optional.empty();
    }

    static boolean a(EntityHoglin entityhoglin, BlockPosition blockposition) {
        Optional<BlockPosition> optional = entityhoglin.getBehaviorController().getMemory(MemoryModuleType.NEAREST_REPELLENT);

        return optional.isPresent() && ((BlockPosition) optional.get()).a((BaseBlockPosition) blockposition, 8.0D);
    }

    private static boolean e(EntityHoglin entityhoglin) {
        return entityhoglin.eL() && !f(entityhoglin);
    }

    private static boolean f(EntityHoglin entityhoglin) {
        if (entityhoglin.isBaby()) {
            return false;
        } else {
            int i = (Integer) entityhoglin.getBehaviorController().getMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT).orElse(0);
            int j = (Integer) entityhoglin.getBehaviorController().getMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT).orElse(0) + 1;

            return i > j;
        }
    }

    protected static void b(EntityHoglin entityhoglin, EntityLiving entityliving) {
        BehaviorController<EntityHoglin> behaviorcontroller = entityhoglin.getBehaviorController();

        behaviorcontroller.removeMemory(MemoryModuleType.PACIFIED);
        behaviorcontroller.removeMemory(MemoryModuleType.BREED_TARGET);
        if (entityhoglin.isBaby()) {
            d(entityhoglin, entityliving);
        } else {
            f(entityhoglin, entityliving);
        }
    }

    private static void f(EntityHoglin entityhoglin, EntityLiving entityliving) {
        if (!entityhoglin.getBehaviorController().c(Activity.AVOID) || entityliving.getEntityType() != EntityTypes.PIGLIN) {
            if (IEntitySelector.f.test(entityliving)) {
                if (entityliving.getEntityType() != EntityTypes.HOGLIN) {
                    if (!BehaviorUtil.a(entityhoglin, entityliving, 4.0D)) {
                        g(entityhoglin, entityliving);
                        h(entityhoglin, entityliving);
                    }
                }
            }
        }
    }

    private static void g(EntityHoglin entityhoglin, EntityLiving entityliving) {
        BehaviorController<EntityHoglin> behaviorcontroller = entityhoglin.getBehaviorController();

        behaviorcontroller.removeMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        behaviorcontroller.removeMemory(MemoryModuleType.BREED_TARGET);
        behaviorcontroller.a(MemoryModuleType.ATTACK_TARGET, entityliving, 200L);
    }

    private static void h(EntityHoglin entityhoglin, EntityLiving entityliving) {
        g(entityhoglin).forEach((entityhoglin1) -> {
            i(entityhoglin1, entityliving);
        });
    }

    private static void i(EntityHoglin entityhoglin, EntityLiving entityliving) {
        if (!c(entityhoglin)) {
            Optional<EntityLiving> optional = entityhoglin.getBehaviorController().getMemory(MemoryModuleType.ATTACK_TARGET);
            EntityLiving entityliving1 = BehaviorUtil.a((EntityLiving) entityhoglin, optional, entityliving);

            g(entityhoglin, entityliving1);
        }
    }

    public static Optional<SoundEffect> b(EntityHoglin entityhoglin) {
        return entityhoglin.getBehaviorController().f().map((activity) -> {
            return a(entityhoglin, activity);
        });
    }

    private static SoundEffect a(EntityHoglin entityhoglin, Activity activity) {
        return activity != Activity.AVOID && !entityhoglin.isConverting() ? (activity == Activity.FLIGHT ? SoundEffects.ENTITY_HOGLIN_ANGRY : (h(entityhoglin) ? SoundEffects.ENTITY_HOGLIN_RETREAT : SoundEffects.ENTITY_HOGLIN_AMBIENT)) : SoundEffects.ENTITY_HOGLIN_RETREAT;
    }

    private static List<EntityHoglin> g(EntityHoglin entityhoglin) {
        return (List) entityhoglin.getBehaviorController().getMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS).orElse(ImmutableList.of());
    }

    private static boolean h(EntityHoglin entityhoglin) {
        return entityhoglin.getBehaviorController().hasMemory(MemoryModuleType.NEAREST_REPELLENT);
    }

    private static boolean i(EntityHoglin entityhoglin) {
        return entityhoglin.getBehaviorController().hasMemory(MemoryModuleType.BREED_TARGET);
    }

    protected static boolean c(EntityHoglin entityhoglin) {
        return entityhoglin.getBehaviorController().hasMemory(MemoryModuleType.PACIFIED);
    }
}
