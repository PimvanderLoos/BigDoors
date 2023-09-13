package net.minecraft.world.entity.ai.behavior;

import net.minecraft.server.level.WorldServer;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityLiving;

public class BehaviorRunSometimes<E extends EntityLiving> extends Behavior<E> {

    private boolean resetTicks;
    private boolean wasRunning;
    private final UniformInt interval;
    private final Behavior<? super E> wrappedBehavior;
    private int ticksUntilNextStart;

    public BehaviorRunSometimes(Behavior<? super E> behavior, UniformInt uniformint) {
        this(behavior, false, uniformint);
    }

    public BehaviorRunSometimes(Behavior<? super E> behavior, boolean flag, UniformInt uniformint) {
        super(behavior.entryCondition);
        this.wrappedBehavior = behavior;
        this.resetTicks = !flag;
        this.interval = uniformint;
    }

    @Override
    protected boolean checkExtraStartConditions(WorldServer worldserver, E e0) {
        if (!this.wrappedBehavior.checkExtraStartConditions(worldserver, e0)) {
            return false;
        } else {
            if (this.resetTicks) {
                this.resetTicksUntilNextStart(worldserver);
                this.resetTicks = false;
            }

            if (this.ticksUntilNextStart > 0) {
                --this.ticksUntilNextStart;
            }

            return !this.wasRunning && this.ticksUntilNextStart == 0;
        }
    }

    @Override
    protected void start(WorldServer worldserver, E e0, long i) {
        this.wrappedBehavior.start(worldserver, e0, i);
    }

    @Override
    protected boolean canStillUse(WorldServer worldserver, E e0, long i) {
        return this.wrappedBehavior.canStillUse(worldserver, e0, i);
    }

    @Override
    protected void tick(WorldServer worldserver, E e0, long i) {
        this.wrappedBehavior.tick(worldserver, e0, i);
        this.wasRunning = this.wrappedBehavior.getStatus() == Behavior.Status.RUNNING;
    }

    @Override
    protected void stop(WorldServer worldserver, E e0, long i) {
        this.resetTicksUntilNextStart(worldserver);
        this.wrappedBehavior.stop(worldserver, e0, i);
    }

    private void resetTicksUntilNextStart(WorldServer worldserver) {
        this.ticksUntilNextStart = this.interval.sample(worldserver.random);
    }

    @Override
    protected boolean timedOut(long i) {
        return false;
    }

    @Override
    public String toString() {
        return "RunSometimes: " + this.wrappedBehavior;
    }
}
