package net.minecraft.world.entity.ai.behavior.warden;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.warden.Warden;

public class SetRoarTarget<E extends Warden> extends Behavior<E> {

    private final Function<E, Optional<? extends EntityLiving>> targetFinderFunction;

    public SetRoarTarget(Function<E, Optional<? extends EntityLiving>> function) {
        super(ImmutableMap.of(MemoryModuleType.ROAR_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryStatus.REGISTERED));
        this.targetFinderFunction = function;
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, E e0) {
        Optional optional = (Optional) this.targetFinderFunction.apply(e0);

        Objects.requireNonNull(e0);
        return optional.filter(e0::canTargetEntity).isPresent();
    }

    protected void start(WorldServer worldserver, E e0, long i) {
        ((Optional) this.targetFinderFunction.apply(e0)).ifPresent((entityliving) -> {
            e0.getBrain().setMemory(MemoryModuleType.ROAR_TARGET, (Object) entityliving);
            e0.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        });
    }
}
