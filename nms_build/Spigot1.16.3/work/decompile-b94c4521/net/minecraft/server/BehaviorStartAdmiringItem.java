package net.minecraft.server;

import com.google.common.collect.ImmutableMap;

public class BehaviorStartAdmiringItem<E extends EntityPiglin> extends Behavior<E> {

    private final int b;

    public BehaviorStartAdmiringItem(int i) {
        super(ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryStatus.VALUE_PRESENT, MemoryModuleType.ADMIRING_ITEM, MemoryStatus.VALUE_ABSENT, MemoryModuleType.ADMIRING_DISABLED, MemoryStatus.VALUE_ABSENT, MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM, MemoryStatus.VALUE_ABSENT));
        this.b = i;
    }

    protected boolean a(WorldServer worldserver, E e0) {
        EntityItem entityitem = (EntityItem) e0.getBehaviorController().getMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM).get();

        return PiglinAI.a(entityitem.getItemStack().getItem());
    }

    protected void a(WorldServer worldserver, E e0, long i) {
        e0.getBehaviorController().a(MemoryModuleType.ADMIRING_ITEM, true, (long) this.b);
    }
}
