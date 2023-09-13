package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class BehaviorAttackTargetForget {

    private static final int TIMEOUT_TO_GET_WITHIN_ATTACK_RANGE = 200;

    public BehaviorAttackTargetForget() {}

    public static <E extends EntityInsentient> BehaviorControl<E> create(BiConsumer<E, EntityLiving> biconsumer) {
        return create((entityliving) -> {
            return false;
        }, biconsumer, true);
    }

    public static <E extends EntityInsentient> BehaviorControl<E> create(Predicate<EntityLiving> predicate) {
        return create(predicate, (entityinsentient, entityliving) -> {
        }, true);
    }

    public static <E extends EntityInsentient> BehaviorControl<E> create() {
        return create((entityliving) -> {
            return false;
        }, (entityinsentient, entityliving) -> {
        }, true);
    }

    public static <E extends EntityInsentient> BehaviorControl<E> create(Predicate<EntityLiving> predicate, BiConsumer<E, EntityLiving> biconsumer, boolean flag) {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.present(MemoryModuleType.ATTACK_TARGET), behaviorbuilder_b.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1) -> {
                return (worldserver, entityinsentient, i) -> {
                    EntityLiving entityliving = (EntityLiving) behaviorbuilder_b.get(memoryaccessor);

                    if (entityinsentient.canAttack(entityliving) && (!flag || !isTiredOfTryingToReachTarget(entityinsentient, behaviorbuilder_b.tryGet(memoryaccessor1))) && entityliving.isAlive() && entityliving.level == entityinsentient.level && !predicate.test(entityliving)) {
                        return true;
                    } else {
                        biconsumer.accept(entityinsentient, entityliving);
                        memoryaccessor.erase();
                        return true;
                    }
                };
            });
        });
    }

    private static boolean isTiredOfTryingToReachTarget(EntityLiving entityliving, Optional<Long> optional) {
        return optional.isPresent() && entityliving.level.getGameTime() - (Long) optional.get() > 200L;
    }
}
