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

    public boolean a(PathfinderGoalWrapped pathfindergoalwrapped) {
        return this.C_() && pathfindergoalwrapped.h() < this.h();
    }

    @Override
    public boolean a() {
        return this.goal.a();
    }

    @Override
    public boolean b() {
        return this.goal.b();
    }

    @Override
    public boolean C_() {
        return this.goal.C_();
    }

    @Override
    public void c() {
        if (!this.isRunning) {
            this.isRunning = true;
            this.goal.c();
        }
    }

    @Override
    public void d() {
        if (this.isRunning) {
            this.isRunning = false;
            this.goal.d();
        }
    }

    @Override
    public void e() {
        this.goal.e();
    }

    @Override
    public void a(EnumSet<PathfinderGoal.Type> enumset) {
        this.goal.a(enumset);
    }

    @Override
    public EnumSet<PathfinderGoal.Type> i() {
        return this.goal.i();
    }

    public boolean g() {
        return this.isRunning;
    }

    public int h() {
        return this.priority;
    }

    public PathfinderGoal j() {
        return this.goal;
    }

    public boolean equals(@Nullable Object object) {
        return this == object ? true : (object != null && this.getClass() == object.getClass() ? this.goal.equals(((PathfinderGoalWrapped) object).goal) : false);
    }

    public int hashCode() {
        return this.goal.hashCode();
    }
}
