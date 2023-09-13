package net.minecraft.server;

import com.google.common.collect.ImmutableMap;

public class BehaviorStopAdmiring<E extends EntityPiglin> extends Behavior<E> {

    public BehaviorStopAdmiring() {
        super(ImmutableMap.of(MemoryModuleType.ADMIRING_ITEM, MemoryStatus.VALUE_ABSENT));
    }

    protected boolean a(WorldServer worldserver, E e0) {
        return !e0.getItemInOffHand().isEmpty() && e0.getItemInOffHand().getItem() != Items.SHIELD;
    }

    protected void a(WorldServer worldserver, E e0, long i) {
        PiglinAI.a(e0, true);
    }
}
