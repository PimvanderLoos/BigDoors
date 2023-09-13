package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.animal.EntityIronGolem;
import net.minecraft.world.entity.npc.EntityVillager;

public class PathfinderGoalOfferFlower extends PathfinderGoal {

    private static final PathfinderTargetCondition OFFER_TARGER_CONTEXT = PathfinderTargetCondition.b().a(6.0D);
    public static final int OFFER_TICKS = 400;
    private final EntityIronGolem golem;
    private EntityVillager villager;
    private int tick;

    public PathfinderGoalOfferFlower(EntityIronGolem entityirongolem) {
        this.golem = entityirongolem;
        this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
    }

    @Override
    public boolean a() {
        if (!this.golem.level.isDay()) {
            return false;
        } else if (this.golem.getRandom().nextInt(8000) != 0) {
            return false;
        } else {
            this.villager = (EntityVillager) this.golem.level.a(EntityVillager.class, PathfinderGoalOfferFlower.OFFER_TARGER_CONTEXT, this.golem, this.golem.locX(), this.golem.locY(), this.golem.locZ(), this.golem.getBoundingBox().grow(6.0D, 2.0D, 6.0D));
            return this.villager != null;
        }
    }

    @Override
    public boolean b() {
        return this.tick > 0;
    }

    @Override
    public void c() {
        this.tick = 400;
        this.golem.v(true);
    }

    @Override
    public void d() {
        this.golem.v(false);
        this.villager = null;
    }

    @Override
    public void e() {
        this.golem.getControllerLook().a(this.villager, 30.0F, 30.0F);
        --this.tick;
    }
}
