package net.minecraft.world.entity.animal.axolotl;

import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class ValidatePlayDead {

    public ValidatePlayDead() {}

    public static BehaviorControl<EntityLiving> create() {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.present(MemoryModuleType.PLAY_DEAD_TICKS), behaviorbuilder_b.registered(MemoryModuleType.HURT_BY_ENTITY)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1) -> {
                return (worldserver, entityliving, i) -> {
                    int j = (Integer) behaviorbuilder_b.get(memoryaccessor);

                    if (j <= 0) {
                        memoryaccessor.erase();
                        memoryaccessor1.erase();
                        entityliving.getBrain().useDefaultActivity();
                    } else {
                        memoryaccessor.set(j - 1);
                    }

                    return true;
                };
            });
        });
    }
}
