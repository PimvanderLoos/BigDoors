package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.schedule.Activity;

public class BehaviorWake {

    public BehaviorWake() {}

    public static BehaviorControl<EntityLiving> create() {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.point((worldserver, entityliving, i) -> {
                if (!entityliving.getBrain().isActive(Activity.REST) && entityliving.isSleeping()) {
                    entityliving.stopSleeping();
                    return true;
                } else {
                    return false;
                }
            });
        });
    }
}
