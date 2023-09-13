package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.animal.EntityIronGolem;
import net.minecraft.world.entity.npc.EntityVillager;

public class PathfinderGoalOfferFlower extends PathfinderGoal {

    private static final PathfinderTargetCondition OFFER_TARGER_CONTEXT = PathfinderTargetCondition.forNonCombat().range(6.0D);
    public static final int OFFER_TICKS = 400;
    private final EntityIronGolem golem;
    private EntityVillager villager;
    private int tick;

    public PathfinderGoalOfferFlower(EntityIronGolem entityirongolem) {
        this.golem = entityirongolem;
        this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.golem.level.isDay()) {
            return false;
        } else if (this.golem.getRandom().nextInt(8000) != 0) {
            return false;
        } else {
            this.villager = (EntityVillager) this.golem.level.getNearestEntity(EntityVillager.class, PathfinderGoalOfferFlower.OFFER_TARGER_CONTEXT, this.golem, this.golem.getX(), this.golem.getY(), this.golem.getZ(), this.golem.getBoundingBox().inflate(6.0D, 2.0D, 6.0D));
            return this.villager != null;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.tick > 0;
    }

    @Override
    public void start() {
        this.tick = this.adjustedTickDelay(400);
        this.golem.offerFlower(true);
    }

    @Override
    public void stop() {
        this.golem.offerFlower(false);
        this.villager = null;
    }

    @Override
    public void tick() {
        this.golem.getLookControl().setLookAt(this.villager, 30.0F, 30.0F);
        --this.tick;
    }
}
