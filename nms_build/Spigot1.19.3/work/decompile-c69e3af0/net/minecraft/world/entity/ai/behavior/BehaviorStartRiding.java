package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryTarget;

public class BehaviorStartRiding {

    private static final int CLOSE_ENOUGH_TO_START_RIDING_DIST = 1;

    public BehaviorStartRiding() {}

    public static BehaviorControl<EntityLiving> create(float f) {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.registered(MemoryModuleType.LOOK_TARGET), behaviorbuilder_b.absent(MemoryModuleType.WALK_TARGET), behaviorbuilder_b.present(MemoryModuleType.RIDE_TARGET)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1, memoryaccessor2) -> {
                return (worldserver, entityliving, i) -> {
                    if (entityliving.isPassenger()) {
                        return false;
                    } else {
                        Entity entity = (Entity) behaviorbuilder_b.get(memoryaccessor2);

                        if (entity.closerThan(entityliving, 1.0D)) {
                            entityliving.startRiding(entity);
                        } else {
                            memoryaccessor.set(new BehaviorPositionEntity(entity, true));
                            memoryaccessor1.set(new MemoryTarget(new BehaviorPositionEntity(entity, false), f, 1));
                        }

                        return true;
                    }
                };
            });
        });
    }
}
