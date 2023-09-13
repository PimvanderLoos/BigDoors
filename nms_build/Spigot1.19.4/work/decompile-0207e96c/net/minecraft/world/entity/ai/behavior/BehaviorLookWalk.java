package net.minecraft.world.entity.ai.behavior;

import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryTarget;

public class BehaviorLookWalk {

    public BehaviorLookWalk() {}

    public static OneShot<EntityLiving> create(float f, int i) {
        return create((entityliving) -> {
            return true;
        }, (entityliving) -> {
            return f;
        }, i);
    }

    public static OneShot<EntityLiving> create(Predicate<EntityLiving> predicate, Function<EntityLiving, Float> function, int i) {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.absent(MemoryModuleType.WALK_TARGET), behaviorbuilder_b.present(MemoryModuleType.LOOK_TARGET)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1) -> {
                return (worldserver, entityliving, j) -> {
                    if (!predicate.test(entityliving)) {
                        return false;
                    } else {
                        memoryaccessor.set(new MemoryTarget((BehaviorPosition) behaviorbuilder_b.get(memoryaccessor1), (Float) function.apply(entityliving), i));
                        return true;
                    }
                };
            });
        });
    }
}
