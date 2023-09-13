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
    protected boolean a(WorldServer worldserver, EntityLiving entityliving) {
        return worldserver.random.nextInt(20) == 0;
    }

    @Override
    protected void a(WorldServer worldserver, EntityLiving entityliving, long i) {
        BehaviorController<?> behaviorcontroller = entityliving.getBehaviorController();
        Raid raid = worldserver.b_(entityliving.getChunkCoordinates());

        if (raid != null) {
            if (raid.c() && !raid.b()) {
                behaviorcontroller.b(Activity.RAID);
                behaviorcontroller.a(Activity.RAID);
            } else {
                behaviorcontroller.b(Activity.PRE_RAID);
                behaviorcontroller.a(Activity.PRE_RAID);
            }
        }

    }
}
