package net.minecraft.server;

import com.google.common.collect.ImmutableMap;

public class BehaviorRememberHuntedHoglin<E extends EntityPiglin> extends Behavior<E> {

    public BehaviorRememberHuntedHoglin() {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.HUNTED_RECENTLY, MemoryStatus.REGISTERED));
    }

    protected void a(WorldServer worldserver, E e0, long i) {
        if (this.a(e0)) {
            PiglinAI.j(e0);
        }

    }

    private boolean a(E e0) {
        EntityLiving entityliving = (EntityLiving) e0.getBehaviorController().getMemory(MemoryModuleType.ATTACK_TARGET).get();

        return entityliving.getEntityType() == EntityTypes.HOGLIN && entityliving.dk();
    }
}
