package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.GameRules;

public class BehaviorForgetAnger<E extends EntityInsentient> extends Behavior<E> {

    public BehaviorForgetAnger() {
        super(ImmutableMap.of(MemoryModuleType.ANGRY_AT, MemoryStatus.VALUE_PRESENT));
    }

    protected void a(WorldServer worldserver, E e0, long i) {
        BehaviorUtil.a((EntityLiving) e0, MemoryModuleType.ANGRY_AT).ifPresent((entityliving) -> {
            if (entityliving.dl() && (entityliving.getEntityType() != EntityTypes.PLAYER || worldserver.getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS))) {
                e0.getBehaviorController().removeMemory(MemoryModuleType.ANGRY_AT);
            }

        });
    }
}
