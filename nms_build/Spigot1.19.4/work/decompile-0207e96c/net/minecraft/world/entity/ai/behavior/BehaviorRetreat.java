package net.minecraft.world.entity.ai.behavior;

import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public class BehaviorRetreat {

    public BehaviorRetreat() {}

    public static OneShot<EntityInsentient> create(int i, float f) {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.absent(MemoryModuleType.WALK_TARGET), behaviorbuilder_b.registered(MemoryModuleType.LOOK_TARGET), behaviorbuilder_b.present(MemoryModuleType.ATTACK_TARGET), behaviorbuilder_b.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1, memoryaccessor2, memoryaccessor3) -> {
                return (worldserver, entityinsentient, j) -> {
                    EntityLiving entityliving = (EntityLiving) behaviorbuilder_b.get(memoryaccessor2);

                    if (entityliving.closerThan(entityinsentient, (double) i) && ((NearestVisibleLivingEntities) behaviorbuilder_b.get(memoryaccessor3)).contains(entityliving)) {
                        memoryaccessor1.set(new BehaviorPositionEntity(entityliving, true));
                        entityinsentient.getMoveControl().strafe(-f, 0.0F);
                        entityinsentient.setYRot(MathHelper.rotateIfNecessary(entityinsentient.getYRot(), entityinsentient.yHeadRot, 0.0F));
                        return true;
                    } else {
                        return false;
                    }
                };
            });
        });
    }
}
