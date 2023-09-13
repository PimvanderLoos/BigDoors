package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public class BehaviorLookInteract {

    public BehaviorLookInteract() {}

    public static BehaviorControl<EntityLiving> create(EntityTypes<?> entitytypes, int i) {
        int j = i * i;

        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.registered(MemoryModuleType.LOOK_TARGET), behaviorbuilder_b.absent(MemoryModuleType.INTERACTION_TARGET), behaviorbuilder_b.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1, memoryaccessor2) -> {
                return (worldserver, entityliving, k) -> {
                    Optional<EntityLiving> optional = ((NearestVisibleLivingEntities) behaviorbuilder_b.get(memoryaccessor2)).findClosest((entityliving1) -> {
                        return entityliving1.distanceToSqr((Entity) entityliving) <= (double) j && entitytypes.equals(entityliving1.getType());
                    });

                    if (optional.isEmpty()) {
                        return false;
                    } else {
                        EntityLiving entityliving1 = (EntityLiving) optional.get();

                        memoryaccessor1.set(entityliving1);
                        memoryaccessor.set(new BehaviorPositionEntity(entityliving1, true));
                        return true;
                    }
                };
            });
        });
    }
}
