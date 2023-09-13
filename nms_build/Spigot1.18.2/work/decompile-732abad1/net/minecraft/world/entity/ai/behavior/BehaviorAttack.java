package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemProjectileWeapon;

public class BehaviorAttack extends Behavior<EntityInsentient> {

    private final int cooldownBetweenAttacks;

    public BehaviorAttack(int i) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryStatus.VALUE_ABSENT));
        this.cooldownBetweenAttacks = i;
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityInsentient entityinsentient) {
        EntityLiving entityliving = this.getAttackTarget(entityinsentient);

        return !this.isHoldingUsableProjectileWeapon(entityinsentient) && BehaviorUtil.canSee(entityinsentient, entityliving) && BehaviorUtil.isWithinMeleeAttackRange(entityinsentient, entityliving);
    }

    private boolean isHoldingUsableProjectileWeapon(EntityInsentient entityinsentient) {
        return entityinsentient.isHolding((itemstack) -> {
            Item item = itemstack.getItem();

            return item instanceof ItemProjectileWeapon && entityinsentient.canFireProjectileWeapon((ItemProjectileWeapon) item);
        });
    }

    protected void start(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        EntityLiving entityliving = this.getAttackTarget(entityinsentient);

        BehaviorUtil.lookAtEntity(entityinsentient, entityliving);
        entityinsentient.swing(EnumHand.MAIN_HAND);
        entityinsentient.doHurtTarget(entityliving);
        entityinsentient.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_COOLING_DOWN, true, (long) this.cooldownBetweenAttacks);
    }

    private EntityLiving getAttackTarget(EntityInsentient entityinsentient) {
        return (EntityLiving) entityinsentient.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
    }
}
