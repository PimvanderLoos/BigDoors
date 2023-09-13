package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.animal.EntityAnimal;
import net.minecraft.world.level.World;

public class PathfinderGoalBreed extends PathfinderGoal {

    private static final PathfinderTargetCondition PARTNER_TARGETING = PathfinderTargetCondition.forNonCombat().range(8.0D).ignoreLineOfSight();
    protected final EntityAnimal animal;
    private final Class<? extends EntityAnimal> partnerClass;
    protected final World level;
    @Nullable
    protected EntityAnimal partner;
    private int loveTime;
    private final double speedModifier;

    public PathfinderGoalBreed(EntityAnimal entityanimal, double d0) {
        this(entityanimal, d0, entityanimal.getClass());
    }

    public PathfinderGoalBreed(EntityAnimal entityanimal, double d0, Class<? extends EntityAnimal> oclass) {
        this.animal = entityanimal;
        this.level = entityanimal.level;
        this.partnerClass = oclass;
        this.speedModifier = d0;
        this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.animal.isInLove()) {
            return false;
        } else {
            this.partner = this.getFreePartner();
            return this.partner != null;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.partner.isAlive() && this.partner.isInLove() && this.loveTime < 60;
    }

    @Override
    public void stop() {
        this.partner = null;
        this.loveTime = 0;
    }

    @Override
    public void tick() {
        this.animal.getLookControl().setLookAt(this.partner, 10.0F, (float) this.animal.getMaxHeadXRot());
        this.animal.getNavigation().moveTo((Entity) this.partner, this.speedModifier);
        ++this.loveTime;
        if (this.loveTime >= this.adjustedTickDelay(60) && this.animal.distanceToSqr((Entity) this.partner) < 9.0D) {
            this.breed();
        }

    }

    @Nullable
    private EntityAnimal getFreePartner() {
        List<? extends EntityAnimal> list = this.level.getNearbyEntities(this.partnerClass, PathfinderGoalBreed.PARTNER_TARGETING, this.animal, this.animal.getBoundingBox().inflate(8.0D));
        double d0 = Double.MAX_VALUE;
        EntityAnimal entityanimal = null;
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            EntityAnimal entityanimal1 = (EntityAnimal) iterator.next();

            if (this.animal.canMate(entityanimal1) && this.animal.distanceToSqr((Entity) entityanimal1) < d0) {
                entityanimal = entityanimal1;
                d0 = this.animal.distanceToSqr((Entity) entityanimal1);
            }
        }

        return entityanimal;
    }

    protected void breed() {
        this.animal.spawnChildFromBreeding((WorldServer) this.level, this.partner);
    }
}
