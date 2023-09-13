package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.GameRules;

public class BehaviorForgetAnger<E extends EntityInsentient> extends Behavior<E> {

    public BehaviorForgetAnger() {
        super(ImmutableMap.of(MemoryModuleType.ANGRY_AT, MemoryStatus.VALUE_PRESENT));
    }

    protected void start(WorldServer worldserver, E e0, long i) {
        BehaviorUtil.getLivingEntityFromUUIDMemory(e0, MemoryModuleType.ANGRY_AT).ifPresent((entityliving) -> {
            if (entityliving.isDeadOrDying() && (entityliving.getType() != EntityTypes.PLAYER || worldserver.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS))) {
                e0.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
            }

        });
    }
}
