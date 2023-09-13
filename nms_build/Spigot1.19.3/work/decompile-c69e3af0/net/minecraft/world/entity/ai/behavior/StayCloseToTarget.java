package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryTarget;

public class StayCloseToTarget {

    public StayCloseToTarget() {}

    public static BehaviorControl<EntityLiving> create(Function<EntityLiving, Optional<BehaviorPosition>> function, int i, int j, float f) {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.registered(MemoryModuleType.LOOK_TARGET), behaviorbuilder_b.absent(MemoryModuleType.WALK_TARGET)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1) -> {
                return (worldserver, entityliving, k) -> {
                    Optional<BehaviorPosition> optional = (Optional) function.apply(entityliving);

                    if (optional.isEmpty()) {
                        return false;
                    } else {
                        BehaviorPosition behaviorposition = (BehaviorPosition) optional.get();

                        if (entityliving.position().closerThan(behaviorposition.currentPosition(), (double) j)) {
                            return false;
                        } else {
                            BehaviorPosition behaviorposition1 = (BehaviorPosition) optional.get();

                            memoryaccessor.set(behaviorposition1);
                            memoryaccessor1.set(new MemoryTarget(behaviorposition1, f, i));
                            return true;
                        }
                    }
                };
            });
        });
    }
}
