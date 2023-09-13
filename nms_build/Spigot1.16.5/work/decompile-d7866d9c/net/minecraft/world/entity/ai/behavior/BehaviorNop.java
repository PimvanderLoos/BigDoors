package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;

public class BehaviorNop extends Behavior<EntityLiving> {

    public BehaviorNop(int i, int j) {
        super(ImmutableMap.of(), i, j);
    }

    @Override
    protected boolean b(WorldServer worldserver, EntityLiving entityliving, long i) {
        return true;
    }
}
