package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class BehaviorCooldown {

    private static final int SAFE_DISTANCE_FROM_DANGER = 36;

    public BehaviorCooldown() {}

    public static BehaviorControl<EntityLiving> create() {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.registered(MemoryModuleType.HURT_BY), behaviorbuilder_b.registered(MemoryModuleType.HURT_BY_ENTITY), behaviorbuilder_b.registered(MemoryModuleType.NEAREST_HOSTILE)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1, memoryaccessor2) -> {
                return (worldserver, entityliving, i) -> {
                    boolean flag = behaviorbuilder_b.tryGet(memoryaccessor).isPresent() || behaviorbuilder_b.tryGet(memoryaccessor2).isPresent() || behaviorbuilder_b.tryGet(memoryaccessor1).filter((entityliving1) -> {
                        return entityliving1.distanceToSqr((Entity) entityliving) <= 36.0D;
                    }).isPresent();

                    if (!flag) {
                        memoryaccessor.erase();
                        memoryaccessor1.erase();
                        entityliving.getBrain().updateActivityFromSchedule(worldserver.getDayTime(), worldserver.getGameTime());
                    }

                    return true;
                };
            });
        });
    }
}
