package net.minecraft.server;

public class BehaviorOutsideCelebrate extends BehaviorOutside {

    public BehaviorOutsideCelebrate(float f) {
        super(f);
    }

    @Override
    protected boolean a(WorldServer worldserver, EntityLiving entityliving) {
        Raid raid = worldserver.c_(entityliving.getChunkCoordinates());

        return raid != null && raid.isVictory() && super.a(worldserver, entityliving);
    }
}
