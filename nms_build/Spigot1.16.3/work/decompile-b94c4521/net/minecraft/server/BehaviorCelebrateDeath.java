package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import java.util.function.BiPredicate;

public class BehaviorCelebrateDeath extends Behavior<EntityLiving> {

    private final int b;
    private final BiPredicate<EntityLiving, EntityLiving> c;

    public BehaviorCelebrateDeath(int i, BiPredicate<EntityLiving, EntityLiving> bipredicate) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.ANGRY_AT, MemoryStatus.REGISTERED, MemoryModuleType.CELEBRATE_LOCATION, MemoryStatus.VALUE_ABSENT, MemoryModuleType.DANCING, MemoryStatus.REGISTERED));
        this.b = i;
        this.c = bipredicate;
    }

    @Override
    protected boolean a(WorldServer worldserver, EntityLiving entityliving) {
        return this.a(entityliving).dk();
    }

    @Override
    protected void a(WorldServer worldserver, EntityLiving entityliving, long i) {
        EntityLiving entityliving1 = this.a(entityliving);

        if (this.c.test(entityliving, entityliving1)) {
            entityliving.getBehaviorController().a(MemoryModuleType.DANCING, true, (long) this.b);
        }

        entityliving.getBehaviorController().a(MemoryModuleType.CELEBRATE_LOCATION, entityliving1.getChunkCoordinates(), (long) this.b);
        if (entityliving1.getEntityType() != EntityTypes.PLAYER || worldserver.getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS)) {
            entityliving.getBehaviorController().removeMemory(MemoryModuleType.ATTACK_TARGET);
            entityliving.getBehaviorController().removeMemory(MemoryModuleType.ANGRY_AT);
        }

    }

    private EntityLiving a(EntityLiving entityliving) {
        return (EntityLiving) entityliving.getBehaviorController().getMemory(MemoryModuleType.ATTACK_TARGET).get();
    }
}
