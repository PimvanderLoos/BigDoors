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

    protected boolean b(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        return entityinsentient.getBehaviorController().getMemory(MemoryModuleType.LOOK_TARGET).filter((behaviorposition) -> {
            return behaviorposition.a(entityinsentient);
        }).isPresent();
    }

    protected void c(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        entityinsentient.getBehaviorController().removeMemory(MemoryModuleType.LOOK_TARGET);
    }

    protected void d(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        entityinsentient.getBehaviorController().getMemory(MemoryModuleType.LOOK_TARGET).ifPresent((behaviorposition) -> {
            entityinsentient.getControllerLook().a(behaviorposition.a());
        });
    }
}
