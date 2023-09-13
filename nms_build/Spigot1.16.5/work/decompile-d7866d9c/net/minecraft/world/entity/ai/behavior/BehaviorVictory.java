package net.minecraft.world.entity.ai.behavior;

import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.raid.Raid;

public class BehaviorVictory extends BehaviorStrollRandom {

    public BehaviorVictory(float f) {
        super(f);
    }

    protected boolean a(WorldServer worldserver, EntityCreature entitycreature) {
        Raid raid = worldserver.b_(entitycreature.getChunkCoordinates());

        return raid != null && raid.isVictory() && super.a(worldserver, (EntityLiving) entitycreature);
    }
}
