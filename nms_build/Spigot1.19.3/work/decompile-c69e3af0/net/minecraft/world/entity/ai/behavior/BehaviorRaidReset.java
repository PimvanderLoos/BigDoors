package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.schedule.Activity;

public class BehaviorRaidReset {

    public BehaviorRaidReset() {}

    public static BehaviorControl<EntityLiving> create() {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.point((worldserver, entityliving, i) -> {
                if (worldserver.random.nextInt(20) != 0) {
                    return false;
                } else {
                    BehaviorController<?> behaviorcontroller = entityliving.getBrain();
                    Raid raid = worldserver.getRaidAt(entityliving.blockPosition());

                    if (raid == null || raid.isStopped() || raid.isLoss()) {
                        behaviorcontroller.setDefaultActivity(Activity.IDLE);
                        behaviorcontroller.updateActivityFromSchedule(worldserver.getDayTime(), worldserver.getGameTime());
                    }

                    return true;
                }
            });
        });
    }
}
