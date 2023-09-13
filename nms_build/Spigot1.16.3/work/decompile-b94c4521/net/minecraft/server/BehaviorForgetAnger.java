package net.minecraft.server;

import com.google.common.collect.ImmutableMap;

public class BehaviorForgetAnger<E extends EntityInsentient> extends Behavior<E> {

    public BehaviorForgetAnger() {
        super(ImmutableMap.of(MemoryModuleType.ANGRY_AT, MemoryStatus.VALUE_PRESENT));
    }

    protected void a(WorldServer worldserver, E e0, long i) {
        BehaviorUtil.a((EntityLiving) e0, MemoryModuleType.ANGRY_AT).ifPresent((entityliving) -> {
            if (entityliving.dk() && (entityliving.getEntityType() != EntityTypes.PLAYER || worldserver.getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS))) {
                e0.getBehaviorController().removeMemory(MemoryModuleType.ANGRY_AT);
            }

        });
    }
}
