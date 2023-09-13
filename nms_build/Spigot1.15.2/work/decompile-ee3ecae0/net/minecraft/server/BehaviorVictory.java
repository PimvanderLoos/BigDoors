package net.minecraft.server;

public class BehaviorVictory extends BehaviorStrollRandom {

    public BehaviorVictory(float f) {
        super(f);
    }

    protected boolean a(WorldServer worldserver, EntityCreature entitycreature) {
        Raid raid = worldserver.c_(new BlockPosition(entitycreature));

        return raid != null && raid.isVictory() && super.a(worldserver, (EntityLiving) entitycreature);
    }
}
