package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class BehaviorAttackTargetSet {

    public BehaviorAttackTargetSet() {}

    public static <E extends EntityInsentient> BehaviorControl<E> create(Function<E, Optional<? extends EntityLiving>> function) {
        return create((entityinsentient) -> {
            return true;
        }, function);
    }

    public static <E extends EntityInsentient> BehaviorControl<E> create(Predicate<E> predicate, Function<E, Optional<? extends EntityLiving>> function) {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.absent(MemoryModuleType.ATTACK_TARGET), behaviorbuilder_b.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1) -> {
                return (worldserver, entityinsentient, i) -> {
                    if (!predicate.test(entityinsentient)) {
                        return false;
                    } else {
                        Optional<? extends EntityLiving> optional = (Optional) function.apply(entityinsentient);

                        if (optional.isEmpty()) {
                            return false;
                        } else {
                            EntityLiving entityliving = (EntityLiving) optional.get();

                            if (!entityinsentient.canAttack(entityliving)) {
                                return false;
                            } else {
                                memoryaccessor.set(entityliving);
                                memoryaccessor1.erase();
                                return true;
                            }
                        }
                    }
                };
            });
        });
    }
}
