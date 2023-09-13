package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;

public class BehaviorSchedule extends Behavior<EntityLiving> {

    public BehaviorSchedule() {
        super(ImmutableMap.of());
    }

    @Override
    protected void start(WorldServer worldserver, EntityLiving entityliving, long i) {
        entityliving.getBrain().updateActivityFromSchedule(worldserver.getDayTime(), worldserver.getGameTime());
    }
}
