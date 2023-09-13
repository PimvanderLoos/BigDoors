package net.minecraft.world.entity.ai.behavior.warden;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.Behavior;

public class ForceUnmount extends Behavior<EntityLiving> {

    public ForceUnmount() {
        super(ImmutableMap.of());
    }

    @Override
    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityLiving entityliving) {
        return entityliving.isPassenger();
    }

    @Override
    protected void start(WorldServer worldserver, EntityLiving entityliving, long i) {
        entityliving.unRide();
    }
}
