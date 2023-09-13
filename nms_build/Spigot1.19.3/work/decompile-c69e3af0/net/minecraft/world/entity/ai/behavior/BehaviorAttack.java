package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemProjectileWeapon;

public class BehaviorAttack {

    public BehaviorAttack() {}

    public static OneShot<EntityInsentient> create(int i) {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.registered(MemoryModuleType.LOOK_TARGET), behaviorbuilder_b.present(MemoryModuleType.ATTACK_TARGET), behaviorbuilder_b.absent(MemoryModuleType.ATTACK_COOLING_DOWN), behaviorbuilder_b.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1, memoryaccessor2, memoryaccessor3) -> {
                return (worldserver, entityinsentient, j) -> {
                    EntityLiving entityliving = (EntityLiving) behaviorbuilder_b.get(memoryaccessor1);

                    if (!isHoldingUsableProjectileWeapon(entityinsentient) && entityinsentient.isWithinMeleeAttackRange(entityliving) && ((NearestVisibleLivingEntities) behaviorbuilder_b.get(memoryaccessor3)).contains(entityliving)) {
                        memoryaccessor.set(new BehaviorPositionEntity(entityliving, true));
                        entityinsentient.swing(EnumHand.MAIN_HAND);
                        entityinsentient.doHurtTarget(entityliving);
                        memoryaccessor2.setWithExpiry(true, (long) i);
                        return true;
                    } else {
                        return false;
                    }
                };
            });
        });
    }

    private static boolean isHoldingUsableProjectileWeapon(EntityInsentient entityinsentient) {
        return entityinsentient.isHolding((itemstack) -> {
            Item item = itemstack.getItem();

            return item instanceof ItemProjectileWeapon && entityinsentient.canFireProjectileWeapon((ItemProjectileWeapon) item);
        });
    }
}
