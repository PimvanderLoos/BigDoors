package net.minecraft.world.entity.ai.goal;

import com.mojang.datafixers.DataFixUtils;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.entity.animal.EntityFishSchool;

public class PathfinderGoalFishSchool extends PathfinderGoal {

    private static final int INTERVAL_TICKS = 200;
    private final EntityFishSchool mob;
    private int timeToRecalcPath;
    private int nextStartTick;

    public PathfinderGoalFishSchool(EntityFishSchool entityfishschool) {
        this.mob = entityfishschool;
        this.nextStartTick = this.nextStartTick(entityfishschool);
    }

    protected int nextStartTick(EntityFishSchool entityfishschool) {
        return reducedTickDelay(200 + entityfishschool.getRandom().nextInt(200) % 20);
    }

    @Override
    public boolean canUse() {
        if (this.mob.hasFollowers()) {
            return false;
        } else if (this.mob.isFollower()) {
            return true;
        } else if (this.nextStartTick > 0) {
            --this.nextStartTick;
            return false;
        } else {
            this.nextStartTick = this.nextStartTick(this.mob);
            Predicate<EntityFishSchool> predicate = (entityfishschool) -> {
                return entityfishschool.canBeFollowed() || !entityfishschool.isFollower();
            };
            List<? extends EntityFishSchool> list = this.mob.level.getEntitiesOfClass(this.mob.getClass(), this.mob.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), predicate);
            EntityFishSchool entityfishschool = (EntityFishSchool) DataFixUtils.orElse(list.stream().filter(EntityFishSchool::canBeFollowed).findAny(), this.mob);

            entityfishschool.addFollowers(list.stream().filter((entityfishschool1) -> {
                return !entityfishschool1.isFollower();
            }));
            return this.mob.isFollower();
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.isFollower() && this.mob.inRangeOfLeader();
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
    }

    @Override
    public void stop() {
        this.mob.stopFollowing();
    }

    @Override
    public void tick() {
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            this.mob.pathToLeader();
        }
    }
}
