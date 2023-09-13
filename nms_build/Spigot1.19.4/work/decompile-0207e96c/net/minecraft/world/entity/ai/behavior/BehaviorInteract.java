package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public class BehaviorInteract {

    public BehaviorInteract() {}

    public static <T extends EntityLiving> BehaviorControl<EntityLiving> of(EntityTypes<? extends T> entitytypes, int i, MemoryModuleType<T> memorymoduletype, float f, int j) {
        return of(entitytypes, i, (entityliving) -> {
            return true;
        }, (entityliving) -> {
            return true;
        }, memorymoduletype, f, j);
    }

    public static <E extends EntityLiving, T extends EntityLiving> BehaviorControl<E> of(EntityTypes<? extends T> entitytypes, int i, Predicate<E> predicate, Predicate<T> predicate1, MemoryModuleType<T> memorymoduletype, float f, int j) {
        int k = i * i;
        Predicate<EntityLiving> predicate2 = (entityliving) -> {
            return entitytypes.equals(entityliving.getType()) && predicate1.test(entityliving);
        };

        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.registered(memorymoduletype), behaviorbuilder_b.registered(MemoryModuleType.LOOK_TARGET), behaviorbuilder_b.absent(MemoryModuleType.WALK_TARGET), behaviorbuilder_b.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1, memoryaccessor2, memoryaccessor3) -> {
                return (worldserver, entityliving, l) -> {
                    NearestVisibleLivingEntities nearestvisiblelivingentities = (NearestVisibleLivingEntities) behaviorbuilder_b.get(memoryaccessor3);

                    if (predicate.test(entityliving) && nearestvisiblelivingentities.contains(predicate2)) {
                        Optional<EntityLiving> optional = nearestvisiblelivingentities.findClosest((entityliving1) -> {
                            return entityliving1.distanceToSqr((Entity) entityliving) <= (double) k && predicate2.test(entityliving1);
                        });

                        optional.ifPresent((entityliving1) -> {
                            memoryaccessor.set(entityliving1);
                            memoryaccessor1.set(new BehaviorPositionEntity(entityliving1, true));
                            memoryaccessor2.set(new MemoryTarget(new BehaviorPositionEntity(entityliving1, false), f, j));
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
