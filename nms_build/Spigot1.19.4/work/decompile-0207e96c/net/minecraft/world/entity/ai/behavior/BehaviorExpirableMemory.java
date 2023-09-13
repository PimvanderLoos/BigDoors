package net.minecraft.world.entity.ai.behavior;

import java.util.function.Predicate;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class BehaviorExpirableMemory {

    public BehaviorExpirableMemory() {}

    public static <E extends EntityLiving, T> BehaviorControl<E> create(Predicate<E> predicate, MemoryModuleType<? extends T> memorymoduletype, MemoryModuleType<T> memorymoduletype1, UniformInt uniformint) {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.present(memorymoduletype), behaviorbuilder_b.absent(memorymoduletype1)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1) -> {
                return (worldserver, entityliving, i) -> {
                    if (!predicate.test(entityliving)) {
                        return false;
                    } else {
                        memoryaccessor1.setWithExpiry(behaviorbuilder_b.get(memoryaccessor), (long) uniformint.sample(worldserver.random));
                        return true;
                    }
                };
            });
        });
    }
}
