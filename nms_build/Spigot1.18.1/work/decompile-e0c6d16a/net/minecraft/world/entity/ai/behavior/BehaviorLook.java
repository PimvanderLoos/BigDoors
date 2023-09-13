package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class BehaviorLook extends Behavior<EntityInsentient> {

    public BehaviorLook(int i, int j) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_PRESENT), i, j);
    }

    protected boolean canStillUse(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        return entityinsentient.getBrain().getMemory(MemoryModuleType.LOOK_TARGET).filter((behaviorposition) -> {
            return behaviorposition.isVisibleBy(entityinsentient);
        }).isPresent();
    }

    protected void stop(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        entityinsentient.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
    }

    protected void tick(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        entityinsentient.getBrain().getMemory(MemoryModuleType.LOOK_TARGET).ifPresent((behaviorposition) -> {
            entityinsentient.getLookControl().setLookAt(behaviorposition.currentPosition());
        });
    }
}
