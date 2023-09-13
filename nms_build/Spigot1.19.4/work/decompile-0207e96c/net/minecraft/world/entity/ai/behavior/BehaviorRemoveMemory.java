package net.minecraft.world.entity.ai.behavior;

import java.util.function.Predicate;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class BehaviorRemoveMemory {

    public BehaviorRemoveMemory() {}

    public static <E extends EntityLiving> BehaviorControl<E> create(Predicate<E> predicate, MemoryModuleType<?> memorymoduletype) {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.present(memorymoduletype)).apply(behaviorbuilder_b, (memoryaccessor) -> {
                return (worldserver, entityliving, i) -> {
                    if (predicate.test(entityliving)) {
                        memoryaccessor.erase();
                        return true;
                    } else {
                        return false;
                    }
                };
            });
        });
    }
}
