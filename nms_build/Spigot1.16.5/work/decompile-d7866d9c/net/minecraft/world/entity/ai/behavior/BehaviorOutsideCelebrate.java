package net.minecraft.world.entity.ai.behavior;

import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.raid.Raid;

public class BehaviorOutsideCelebrate extends BehaviorOutside {

    public BehaviorOutsideCelebrate(float f) {
        super(f);
    }

    @Override
    protected boolean a(WorldServer worldserver, EntityLiving entityliving) {
        Raid raid = worldserver.b_(entityliving.getChunkCoordinates());

        return raid != null && raid.isVictory() && super.a(worldserver, entityliving);
    }
}
