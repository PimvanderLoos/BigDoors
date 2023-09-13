package net.minecraft.world.entity.monster.piglin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.behavior.BehaviorAttack;
import net.minecraft.world.entity.ai.behavior.BehaviorAttackTargetForget;
import net.minecraft.world.entity.ai.behavior.BehaviorAttackTargetSet;
import net.minecraft.world.entity.ai.behavior.BehaviorForgetAnger;
import net.minecraft.world.entity.ai.behavior.BehaviorGateSingle;
import net.minecraft.world.entity.ai.behavior.BehaviorInteract;
import net.minecraft.world.entity.ai.behavior.BehaviorInteractDoor;
import net.minecraft.world.entity.ai.behavior.BehaviorLook;
import net.minecraft.world.entity.ai.behavior.BehaviorLookInteract;
import net.minecraft.world.entity.ai.behavior.BehaviorLookTarget;
import net.minecraft.world.entity.ai.behavior.BehaviorNop;
import net.minecraft.world.entity.ai.behavior.BehaviorStrollPlace;
import net.minecraft.world.entity.ai.behavior.BehaviorStrollPosition;
import net.minecraft.world.entity.ai.behavior.BehaviorStrollRandomUnconstrained;
import net.minecraft.world.entity.ai.behavior.BehaviorUtil;
import net.minecraft.world.entity.ai.behavior.BehaviorWalkAwayOutOfRange;
import net.minecraft.world.entity.ai.behavior.BehavorMove;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.schedule.Activity;

public class PiglinBruteAI {

    protected static BehaviorController<?> a(EntityPiglinBrute entitypiglinbrute, BehaviorController<EntityPiglinBrute> behaviorcontroller) {
        b(entitypiglinbrute, behaviorcontroller);
        c(entitypiglinbrute, behaviorcontroller);
        d(entitypiglinbrute, behaviorcontroller);
        behaviorcontroller.a((Set) ImmutableSet.of(Activity.CORE));
        behaviorcontroller.b(Activity.IDLE);
        behaviorcontroller.e();
        return behaviorcontroller;
    }

    protected static void a(EntityPiglinBrute entitypiglinbrute) {
        GlobalPos globalpos = GlobalPos.create(entitypiglinbrute.world.getDimensionKey(), entitypiglinbrute.getChunkCoordinates());

        entitypiglinbrute.getBehaviorController().setMemory(MemoryModuleType.HOME, (Object) globalpos);
    }

    private static void b(EntityPiglinBrute entitypiglinbrute, BehaviorController<EntityPiglinBrute> behaviorcontroller) {
        behaviorcontroller.a(Activity.CORE, 0, ImmutableList.of(new BehaviorLook(45, 90), new BehavorMove(), new BehaviorInteractDoor(), new BehaviorForgetAnger<>()));
    }

    private static void c(EntityPiglinBrute entitypiglinbrute, BehaviorController<EntityPiglinBrute> behaviorcontroller) {
        behaviorcontroller.a(Activity.IDLE, 10, ImmutableList.of(new BehaviorAttackTargetSet<>(PiglinBruteAI::a), a(), b(), new BehaviorLookInteract(EntityTypes.PLAYER, 4)));
    }

    private static void d(EntityPiglinBrute entitypiglinbrute, BehaviorController<EntityPiglinBrute> behaviorcontroller) {
        behaviorcontroller.a(Activity.FLIGHT, 10, ImmutableList.of(new BehaviorAttackTargetForget<>((entityliving) -> {
            return !a((EntityPiglinAbstract) entitypiglinbrute, entityliving);
        }), new BehaviorWalkAwayOutOfRange(1.0F), new BehaviorAttack(20)), MemoryModuleType.ATTACK_TARGET);
    }

    private static BehaviorGateSingle<EntityPiglinBrute> a() {
        return new BehaviorGateSingle<>(ImmutableList.of(Pair.of(new BehaviorLookTarget(EntityTypes.PLAYER, 8.0F), 1), Pair.of(new BehaviorLookTarget(EntityTypes.PIGLIN, 8.0F), 1), Pair.of(new BehaviorLookTarget(EntityTypes.PIGLIN_BRUTE, 8.0F), 1), Pair.of(new BehaviorLookTarget(8.0F), 1), Pair.of(new BehaviorNop(30, 60), 1)));
    }

    private static BehaviorGateSingle<EntityPiglinBrute> b() {
        return new BehaviorGateSingle<>(ImmutableList.of(Pair.of(new BehaviorStrollRandomUnconstrained(0.6F), 2), Pair.of(BehaviorInteract.a(EntityTypes.PIGLIN, 8, MemoryModuleType.INTERACTION_TARGET, 0.6F, 2), 2), Pair.of(BehaviorInteract.a(EntityTypes.PIGLIN_BRUTE, 8, MemoryModuleType.INTERACTION_TARGET, 0.6F, 2), 2), Pair.of(new BehaviorStrollPlace(MemoryModuleType.HOME, 0.6F, 2, 100), 2), Pair.of(new BehaviorStrollPosition(MemoryModuleType.HOME, 0.6F, 5), 2), Pair.of(new BehaviorNop(30, 60), 1)));
    }

    protected static void b(EntityPiglinBrute entitypiglinbrute) {
        BehaviorController<EntityPiglinBrute> behaviorcontroller = entitypiglinbrute.getBehaviorController();
        Activity activity = (Activity) behaviorcontroller.f().orElse((Object) null);

        behaviorcontroller.a((List) ImmutableList.of(Activity.FLIGHT, Activity.IDLE));
        Activity activity1 = (Activity) behaviorcontroller.f().orElse((Object) null);

        if (activity != activity1) {
            d(entitypiglinbrute);
        }

        entitypiglinbrute.setAggressive(behaviorcontroller.hasMemory(MemoryModuleType.ATTACK_TARGET));
    }

    private static boolean a(EntityPiglinAbstract entitypiglinabstract, EntityLiving entityliving) {
        return a(entitypiglinabstract).filter((entityliving1) -> {
            return entityliving1 == entityliving;
        }).isPresent();
    }

    private static Optional<? extends EntityLiving> a(EntityPiglinAbstract entitypiglinabstract) {
        Optional<EntityLiving> optional = BehaviorUtil.a((EntityLiving) entitypiglinabstract, MemoryModuleType.ANGRY_AT);

        if (optional.isPresent() && a((EntityLiving) optional.get())) {
            return optional;
        } else {
            Optional<? extends EntityLiving> optional1 = a(entitypiglinabstract, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER);

            return optional1.isPresent() ? optional1 : entitypiglinabstract.getBehaviorController().getMemory(MemoryModuleType.NEAREST_VISIBLE_NEMSIS);
        }
    }

    private static boolean a(EntityLiving entityliving) {
        return IEntitySelector.f.test(entityliving);
    }

    private static Optional<? extends EntityLiving> a(EntityPiglinAbstract entitypiglinabstract, MemoryModuleType<? extends EntityLiving> memorymoduletype) {
        return entitypiglinabstract.getBehaviorController().getMemory(memorymoduletype).filter((entityliving) -> {
            return entityliving.a((Entity) entitypiglinabstract, 12.0D);
        });
    }

    protected static void a(EntityPiglinBrute entitypiglinbrute, EntityLiving entityliving) {
        if (!(entityliving instanceof EntityPiglinAbstract)) {
            PiglinAI.a((EntityPiglinAbstract) entitypiglinbrute, entityliving);
        }
    }

    protected static void c(EntityPiglinBrute entitypiglinbrute) {
        if ((double) entitypiglinbrute.world.random.nextFloat() < 0.0125D) {
            d(entitypiglinbrute);
        }

    }

    private static void d(EntityPiglinBrute entitypiglinbrute) {
        entitypiglinbrute.getBehaviorController().f().ifPresent((activity) -> {
            if (activity == Activity.FLIGHT) {
                entitypiglinbrute.eT();
            }

        });
    }
}
