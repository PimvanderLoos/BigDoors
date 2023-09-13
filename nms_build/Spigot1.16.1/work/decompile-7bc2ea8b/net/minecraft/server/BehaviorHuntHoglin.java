package net.minecraft.server;

import com.google.common.collect.ImmutableMap;

public class BehaviorHuntHoglin<E extends EntityPiglin> extends Behavior<E> {

    public BehaviorHuntHoglin() {
        super(ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, MemoryStatus.VALUE_PRESENT, MemoryModuleType.ANGRY_AT, MemoryStatus.VALUE_ABSENT, MemoryModuleType.HUNTED_RECENTLY, MemoryStatus.VALUE_ABSENT, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryStatus.REGISTERED));
    }

    protected boolean a(WorldServer worldserver, EntityPiglin entitypiglin) {
        return !entitypiglin.isBaby() && !PiglinAI.e(entitypiglin);
    }

    protected void a(WorldServer worldserver, E e0, long i) {
        EntityHoglin entityhoglin = (EntityHoglin) e0.getBehaviorController().getMemory(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN).get();

        PiglinAI.c(e0, (EntityLiving) entityhoglin);
        PiglinAI.j(e0);
        PiglinAI.b(e0, (EntityLiving) entityhoglin);
        PiglinAI.g(e0);
    }
}
