package net.minecraft.world.entity.ai.behavior;

import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.raid.Raid;

public class BehaviorVictory extends BehaviorStrollRandom {

    public BehaviorVictory(float f) {
        super(f);
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityCreature entitycreature) {
        Raid raid = worldserver.getRaidAt(entitycreature.blockPosition());

        return raid != null && raid.isVictory() && super.checkExtraStartConditions(worldserver, entitycreature);
    }
}
