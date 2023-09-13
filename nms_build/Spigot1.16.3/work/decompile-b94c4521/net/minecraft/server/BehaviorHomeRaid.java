package net.minecraft.server;

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
