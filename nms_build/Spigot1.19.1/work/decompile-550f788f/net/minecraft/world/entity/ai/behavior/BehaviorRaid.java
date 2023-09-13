package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.schedule.Activity;

public class BehaviorRaid extends Behavior<EntityLiving> {

    public BehaviorRaid() {
        super(ImmutableMap.of());
    }

    @Override
    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityLiving entityliving) {
        return worldserver.random.nextInt(20) == 0;
    }

    @Override
    protected void start(WorldServer worldserver, EntityLiving entityliving, long i) {
        BehaviorController<?> behaviorcontroller = entityliving.getBrain();
        Raid raid = worldserver.getRaidAt(entityliving.blockPosition());

        if (raid != null) {
            if (raid.hasFirstWaveSpawned() && !raid.isBetweenWaves()) {
                behaviorcontroller.setDefaultActivity(Activity.RAID);
                behaviorcontroller.setActiveActivityIfPossible(Activity.RAID);
            } else {
                behaviorcontroller.setDefaultActivity(Activity.PRE_RAID);
                behaviorcontroller.setActiveActivityIfPossible(Activity.PRE_RAID);
            }
        }

    }
}
