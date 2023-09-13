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
    protected boolean a(WorldServer worldserver, E e0) {
        if (!this.wrappedBehavior.a(worldserver, e0)) {
            return false;
        } else {
            if (this.resetTicks) {
                this.a(worldserver);
                this.resetTicks = false;
            }

            if (this.ticksUntilNextStart > 0) {
                --this.ticksUntilNextStart;
            }

            return !this.wasRunning && this.ticksUntilNextStart == 0;
        }
    }

    @Override
    protected void a(WorldServer worldserver, E e0, long i) {
        this.wrappedBehavior.a(worldserver, e0, i);
    }

    @Override
    protected boolean b(WorldServer worldserver, E e0, long i) {
        return this.wrappedBehavior.b(worldserver, e0, i);
    }

    @Override
    protected void d(WorldServer worldserver, E e0, long i) {
        this.wrappedBehavior.d(worldserver, e0, i);
        this.wasRunning = this.wrappedBehavior.a() == Behavior.Status.RUNNING;
    }

    @Override
    protected void c(WorldServer worldserver, E e0, long i) {
        this.a(worldserver);
        this.wrappedBehavior.c(worldserver, e0, i);
    }

    private void a(WorldServer worldserver) {
        this.ticksUntilNextStart = this.interval.a(worldserver.random);
    }

    @Override
    protected boolean a(long i) {
        return false;
    }

    @Override
    public String toString() {
        return "RunSometimes: " + this.wrappedBehavior;
    }
}
