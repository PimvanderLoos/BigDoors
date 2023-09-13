package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class BehaviorAttackTargetSet<E extends EntityInsentient> extends Behavior<E> {

    private final Predicate<E> canAttackPredicate;
    private final Function<E, Optional<? extends EntityLiving>> targetFinderFunction;

    public BehaviorAttackTargetSet(Predicate<E> predicate, Function<E, Optional<? extends EntityLiving>> function) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryStatus.REGISTERED));
        this.canAttackPredicate = predicate;
        this.targetFinderFunction = function;
    }

    public BehaviorAttackTargetSet(Function<E, Optional<? extends EntityLiving>> function) {
        this((entityinsentient) -> {
            return true;
        }, function);
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, E e0) {
        if (!this.canAttackPredicate.test(e0)) {
            return false;
        } else {
            Optional<? extends EntityLiving> optional = (Optional) this.targetFinderFunction.apply(e0);

            return optional.isPresent() ? e0.canAttack((EntityLiving) optional.get()) : false;
        }
    }

    protected void start(WorldServer worldserver, E e0, long i) {
        ((Optional) this.targetFinderFunction.apply(e0)).ifPresent((entityliving) -> {
            this.setAttackTarget(e0, entityliving);
        });
    }

    private void setAttackTarget(E e0, EntityLiving entityliving) {
        e0.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, (Object) entityliving);
        e0.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }
}
