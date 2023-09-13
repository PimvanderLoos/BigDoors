package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.schedule.Activity;

public class BehaviorBellAlert {

    public BehaviorBellAlert() {}

    public static BehaviorControl<EntityLiving> create() {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.present(MemoryModuleType.HEARD_BELL_TIME)).apply(behaviorbuilder_b, (memoryaccessor) -> {
                return (worldserver, entityliving, i) -> {
                    Raid raid = worldserver.getRaidAt(entityliving.blockPosition());

                    if (raid == null) {
                        entityliving.getBrain().setActiveActivityIfPossible(Activity.HIDE);
                    }

                    return true;
                };
            });
        });
    }
}
