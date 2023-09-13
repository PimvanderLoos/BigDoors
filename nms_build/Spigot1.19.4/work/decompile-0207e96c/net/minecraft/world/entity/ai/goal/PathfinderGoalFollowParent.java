package net.minecraft.world.entity.ai.goal;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.EntityAnimal;

public class PathfinderGoalFollowParent extends PathfinderGoal {

    public static final int HORIZONTAL_SCAN_RANGE = 8;
    public static final int VERTICAL_SCAN_RANGE = 4;
    public static final int DONT_FOLLOW_IF_CLOSER_THAN = 3;
    private final EntityAnimal animal;
    @Nullable
    private EntityAnimal parent;
    private final double speedModifier;
    private int timeToRecalcPath;

    public PathfinderGoalFollowParent(EntityAnimal entityanimal, double d0) {
        this.animal = entityanimal;
        this.speedModifier = d0;
    }

    @Override
    public boolean canUse() {
        if (this.animal.getAge() >= 0) {
            return false;
        } else {
            List<? extends EntityAnimal> list = this.animal.level.getEntitiesOfClass(this.animal.getClass(), this.animal.getBoundingBox().inflate(8.0D, 4.0D, 8.0D));
            EntityAnimal entityanimal = null;
            double d0 = Double.MAX_VALUE;
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityAnimal entityanimal1 = (EntityAnimal) iterator.next();

                if (entityanimal1.getAge() >= 0) {
                    double d1 = this.animal.distanceToSqr((Entity) entityanimal1);

                    if (d1 <= d0) {
                        d0 = d1;
                        entityanimal = entityanimal1;
                    }
                }
            }

            if (entityanimal == null) {
                return false;
            } else if (d0 < 9.0D) {
                return false;
            } else {
                this.parent = entityanimal;
                return true;
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (this.animal.getAge() >= 0) {
            return false;
        } else if (!this.parent.isAlive()) {
            return false;
        } else {
            double d0 = this.animal.distanceToSqr((Entity) this.parent);

            return d0 >= 9.0D && d0 <= 256.0D;
        }
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
    }

    @Override
    public void stop() {
        this.parent = null;
    }

    @Override
    public void tick() {
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            this.animal.getNavigation().moveTo((Entity) this.parent, this.speedModifier);
        }
    }
}
