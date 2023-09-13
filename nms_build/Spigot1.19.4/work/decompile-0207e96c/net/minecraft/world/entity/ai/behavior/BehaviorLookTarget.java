package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public class BehaviorLookTarget {

    public BehaviorLookTarget() {}

    public static BehaviorControl<EntityLiving> create(EnumCreatureType enumcreaturetype, float f) {
        return create((entityliving) -> {
            return enumcreaturetype.equals(entityliving.getType().getCategory());
        }, f);
    }

    public static OneShot<EntityLiving> create(EntityTypes<?> entitytypes, float f) {
        return create((entityliving) -> {
            return entitytypes.equals(entityliving.getType());
        }, f);
    }

    public static OneShot<EntityLiving> create(float f) {
        return create((entityliving) -> {
            return true;
        }, f);
    }

    public static OneShot<EntityLiving> create(Predicate<EntityLiving> predicate, float f) {
        float f1 = f * f;

        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.absent(MemoryModuleType.LOOK_TARGET), behaviorbuilder_b.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1) -> {
                return (worldserver, entityliving, i) -> {
                    Optional<EntityLiving> optional = ((NearestVisibleLivingEntities) behaviorbuilder_b.get(memoryaccessor1)).findClosest(predicate.and((entityliving1) -> {
                        return entityliving1.distanceToSqr((Entity) entityliving) <= (double) f1 && !entityliving.hasPassenger((Entity) entityliving1);
                    }));

                    if (optional.isEmpty()) {
                        return false;
                    } else {
                        memoryaccessor.set(new BehaviorPositionEntity((Entity) optional.get(), true));
                        return true;
                    }
                };
            });
        });
    }
}
