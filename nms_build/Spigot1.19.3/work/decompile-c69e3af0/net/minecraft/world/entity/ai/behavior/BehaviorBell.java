package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public class BehaviorBell {

    private static final float SPEED_MODIFIER = 0.3F;

    public BehaviorBell() {}

    public static OneShot<EntityLiving> create() {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.registered(MemoryModuleType.WALK_TARGET), behaviorbuilder_b.registered(MemoryModuleType.LOOK_TARGET), behaviorbuilder_b.present(MemoryModuleType.MEETING_POINT), behaviorbuilder_b.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES), behaviorbuilder_b.absent(MemoryModuleType.INTERACTION_TARGET)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1, memoryaccessor2, memoryaccessor3, memoryaccessor4) -> {
                return (worldserver, entityliving, i) -> {
                    GlobalPos globalpos = (GlobalPos) behaviorbuilder_b.get(memoryaccessor2);
                    NearestVisibleLivingEntities nearestvisiblelivingentities = (NearestVisibleLivingEntities) behaviorbuilder_b.get(memoryaccessor3);

                    if (worldserver.getRandom().nextInt(100) == 0 && worldserver.dimension() == globalpos.dimension() && globalpos.pos().closerToCenterThan(entityliving.position(), 4.0D) && nearestvisiblelivingentities.contains((entityliving1) -> {
                        return EntityTypes.VILLAGER.equals(entityliving1.getType());
                    })) {
                        nearestvisiblelivingentities.findClosest((entityliving1) -> {
                            return EntityTypes.VILLAGER.equals(entityliving1.getType()) && entityliving1.distanceToSqr((Entity) entityliving) <= 32.0D;
                        }).ifPresent((entityliving1) -> {
                            memoryaccessor4.set(entityliving1);
                            memoryaccessor1.set(new BehaviorPositionEntity(entityliving1, true));
                            memoryaccessor.set(new MemoryTarget(new BehaviorPositionEntity(entityliving1, false), 0.3F, 1));
                        });
                        return true;
                    } else {
                        return false;
                    }
                };
            });
        });
    }
}
