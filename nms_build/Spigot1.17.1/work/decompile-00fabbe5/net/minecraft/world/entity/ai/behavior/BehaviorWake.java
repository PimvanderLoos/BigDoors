package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.schedule.Activity;

public class BehaviorWake extends Behavior<EntityLiving> {

    public BehaviorWake() {
        super(ImmutableMap.of());
    }

    @Override
    protected boolean a(WorldServer worldserver, EntityLiving entityliving) {
        return !entityliving.getBehaviorController().c(Activity.REST) && entityliving.isSleeping();
    }

    @Override
    protected void a(WorldServer worldserver, EntityLiving entityliving, long i) {
        entityliving.entityWakeup();
    }
}
