package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.entity.npc.EntityVillagerAbstract;
import net.minecraft.world.entity.player.EntityHuman;

public class PathfinderGoalLookAtTradingPlayer extends PathfinderGoalLookAtPlayer {

    private final EntityVillagerAbstract g;

    public PathfinderGoalLookAtTradingPlayer(EntityVillagerAbstract entityvillagerabstract) {
        super(entityvillagerabstract, EntityHuman.class, 8.0F);
        this.g = entityvillagerabstract;
    }

    @Override
    public boolean a() {
        if (this.g.eN()) {
            this.b = this.g.getTrader();
            return true;
        } else {
            return false;
        }
    }
}
