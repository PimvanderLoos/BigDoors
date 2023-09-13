package net.minecraft.world.entity.ai.behavior;

import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.raid.Raid;

public class BehaviorHomeRaid extends BehaviorHome {

    public BehaviorHomeRaid(int i, float f) {
        super(i, f, 1);
    }

    @Override
    protected boolean a(WorldServer worldserver, EntityLiving entityliving) {
        Raid raid = worldserver.b_(entityliving.getChunkCoordinates());

        return super.a(worldserver, entityliving) && raid != null && raid.v() && !raid.isVictory() && !raid.isLoss();
    }
}
