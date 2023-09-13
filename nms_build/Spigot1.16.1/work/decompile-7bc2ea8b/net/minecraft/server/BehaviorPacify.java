package net.minecraft.server;

import com.google.common.collect.ImmutableMap;

public class BehaviorPacify extends Behavior<EntityLiving> {

    private final int b;

    public BehaviorPacify(MemoryModuleType<?> memorymoduletype, int i) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.PACIFIED, MemoryStatus.VALUE_ABSENT, memorymoduletype, MemoryStatus.VALUE_PRESENT));
        this.b = i;
    }

    @Override
    protected void a(WorldServer worldserver, EntityLiving entityliving, long i) {
        entityliving.getBehaviorController().a(MemoryModuleType.PACIFIED, true, (long) this.b);
        entityliving.getBehaviorController().removeMemory(MemoryModuleType.ATTACK_TARGET);
    }
}
