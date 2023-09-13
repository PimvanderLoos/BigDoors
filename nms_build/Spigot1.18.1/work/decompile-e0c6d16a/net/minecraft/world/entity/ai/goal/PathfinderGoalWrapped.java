package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;

public class PathfinderGoalWrapped extends PathfinderGoal {

    private final PathfinderGoal goal;
    private final int priority;
    private boolean isRunning;

    public PathfinderGoalWrapped(int i, PathfinderGoal pathfindergoal) {
        this.priority = i;
        this.goal = pathfindergoal;
    }

    public boolean canBeReplacedBy(PathfinderGoalWrapped pathfindergoalwrapped) {
        return this.isInterruptable() && pathfindergoalwrapped.getPriority() < this.getPriority();
    }

    @Override
    public boolean canUse() {
        return this.goal.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return this.goal.canContinueToUse();
    }

    @Override
    public boolean isInterruptable() {
        return this.goal.isInterruptable();
    }

    @Override
    public void start() {
        if (!this.isRunning) {
            this.isRunning = true;
            this.goal.start();
        }
    }

    @Override
    public void stop() {
        if (this.isRunning) {
            this.isRunning = false;
            this.goal.stop();
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return this.goal.requiresUpdateEveryTick();
    }

    @Override
    protected int adjustedTickDelay(int i) {
        return this.goal.adjustedTickDelay(i);
    }

    @Override
    public void tick() {
        this.goal.tick();
    }

    @Override
    public void setFlags(EnumSet<PathfinderGoal.Type> enumset) {
        this.goal.setFlags(enumset);
    }

    @Override
    public EnumSet<PathfinderGoal.Type> getFlags() {
        return this.goal.getFlags();
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public int getPriority() {
        return this.priority;
    }

    public PathfinderGoal getGoal() {
        return this.goal;
    }

    public boolean equals(@Nullable Object object) {
        return this == object ? true : (object != null && this.getClass() == object.getClass() ? this.goal.equals(((PathfinderGoalWrapped) object).goal) : false);
    }

    public int hashCode() {
        return this.goal.hashCode();
    }
}
