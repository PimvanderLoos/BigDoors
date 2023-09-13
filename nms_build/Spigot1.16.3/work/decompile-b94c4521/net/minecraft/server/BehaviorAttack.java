package net.minecraft.server;

import com.google.common.collect.ImmutableMap;

public class BehaviorAttack extends Behavior<EntityInsentient> {

    private final int b;

    public BehaviorAttack(int i) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryStatus.VALUE_ABSENT));
        this.b = i;
    }

    protected boolean a(WorldServer worldserver, EntityInsentient entityinsentient) {
        EntityLiving entityliving = this.b(entityinsentient);

        return !this.a(entityinsentient) && BehaviorUtil.c(entityinsentient, entityliving) && BehaviorUtil.b((EntityLiving) entityinsentient, entityliving);
    }

    private boolean a(EntityInsentient entityinsentient) {
        return entityinsentient.a((item) -> {
            return item instanceof ItemProjectileWeapon && entityinsentient.a((ItemProjectileWeapon) item);
        });
    }

    protected void a(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        EntityLiving entityliving = this.b(entityinsentient);

        BehaviorUtil.a((EntityLiving) entityinsentient, entityliving);
        entityinsentient.swingHand(EnumHand.MAIN_HAND);
        entityinsentient.attackEntity(entityliving);
        entityinsentient.getBehaviorController().a(MemoryModuleType.ATTACK_COOLING_DOWN, true, (long) this.b);
    }

    private EntityLiving b(EntityInsentient entityinsentient) {
        return (EntityLiving) entityinsentient.getBehaviorController().getMemory(MemoryModuleType.ATTACK_TARGET).get();
    }
}
