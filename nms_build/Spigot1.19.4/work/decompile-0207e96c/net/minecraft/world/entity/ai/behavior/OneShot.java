package net.minecraft.world.entity.ai.behavior;

import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.declarative.Trigger;

public abstract class OneShot<E extends EntityLiving> implements BehaviorControl<E>, Trigger<E> {

    private Behavior.Status status;

    public OneShot() {
        this.status = Behavior.Status.STOPPED;
    }

    @Override
    public final Behavior.Status getStatus() {
        return this.status;
    }

    @Override
    public final boolean tryStart(WorldServer worldserver, E e0, long i) {
        if (this.trigger(worldserver, e0, i)) {
            this.status = Behavior.Status.RUNNING;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public final void tickOrStop(WorldServer worldserver, E e0, long i) {
        this.doStop(worldserver, e0, i);
    }

    @Override
    public final void doStop(WorldServer worldserver, E e0, long i) {
        this.status = Behavior.Status.STOPPED;
    }

    @Override
    public String debugString() {
        return this.getClass().getSimpleName();
    }
}
