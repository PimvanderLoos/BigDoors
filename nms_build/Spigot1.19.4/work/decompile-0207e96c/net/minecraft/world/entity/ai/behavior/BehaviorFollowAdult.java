package net.minecraft.world.entity.ai.behavior;

import java.util.function.Function;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryTarget;

public class BehaviorFollowAdult {

    public BehaviorFollowAdult() {}

    public static OneShot<EntityAgeable> create(UniformInt uniformint, float f) {
        return create(uniformint, (entityliving) -> {
            return f;
        });
    }

    public static OneShot<EntityAgeable> create(UniformInt uniformint, Function<EntityLiving, Float> function) {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.present(MemoryModuleType.NEAREST_VISIBLE_ADULT), behaviorbuilder_b.registered(MemoryModuleType.LOOK_TARGET), behaviorbuilder_b.absent(MemoryModuleType.WALK_TARGET)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1, memoryaccessor2) -> {
                return (worldserver, entityageable, i) -> {
                    if (!entityageable.isBaby()) {
                        return false;
                    } else {
                        EntityAgeable entityageable1 = (EntityAgeable) behaviorbuilder_b.get(memoryaccessor);

                        if (entityageable.closerThan(entityageable1, (double) (uniformint.getMaxValue() + 1)) && !entityageable.closerThan(entityageable1, (double) uniformint.getMinValue())) {
                            MemoryTarget memorytarget = new MemoryTarget(new BehaviorPositionEntity(entityageable1, false), (Float) function.apply(entityageable), uniformint.getMinValue() - 1);

                            memoryaccessor1.set(new BehaviorPositionEntity(entityageable1, true));
                            memoryaccessor2.set(memorytarget);
                            return true;
                        } else {
                            return false;
                        }
                    }
                };
            });
        });
    }
}
