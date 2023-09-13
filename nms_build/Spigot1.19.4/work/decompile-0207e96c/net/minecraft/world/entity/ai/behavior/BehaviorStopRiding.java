package net.minecraft.world.entity.ai.behavior;

import java.util.function.BiPredicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class BehaviorStopRiding {

    public BehaviorStopRiding() {}

    public static <E extends EntityLiving> BehaviorControl<E> create(int i, BiPredicate<E, Entity> bipredicate) {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.registered(MemoryModuleType.RIDE_TARGET)).apply(behaviorbuilder_b, (memoryaccessor) -> {
                return (worldserver, entityliving, j) -> {
                    Entity entity = entityliving.getVehicle();
                    Entity entity1 = (Entity) behaviorbuilder_b.tryGet(memoryaccessor).orElse((Object) null);

                    if (entity == null && entity1 == null) {
                        return false;
                    } else {
                        Entity entity2 = entity == null ? entity1 : entity;

                        if (isVehicleValid(entityliving, entity2, i) && !bipredicate.test(entityliving, entity2)) {
                            return false;
                        } else {
                            entityliving.stopRiding();
                            memoryaccessor.erase();
                            return true;
                        }
                    }
                };
            });
        });
    }

    private static boolean isVehicleValid(EntityLiving entityliving, Entity entity, int i) {
        return entity.isAlive() && entity.closerThan(entityliving, (double) i) && entity.level == entityliving.level;
    }
}
