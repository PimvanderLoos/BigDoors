package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.entity.npc.EntityVillagerAbstract;
import net.minecraft.world.entity.player.EntityHuman;

public class PathfinderGoalLookAtTradingPlayer extends PathfinderGoalLookAtPlayer {

    private final EntityVillagerAbstract villager;

    public PathfinderGoalLookAtTradingPlayer(EntityVillagerAbstract entityvillagerabstract) {
        super(entityvillagerabstract, EntityHuman.class, 8.0F);
        this.villager = entityvillagerabstract;
    }

    @Override
    public boolean a() {
        if (this.villager.fx()) {
            this.lookAt = this.villager.getTrader();
            return true;
        } else {
            return false;
        }
    }
}
