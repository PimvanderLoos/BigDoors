package net.minecraft.world.entity.ai.behavior;

import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;

public class BehaviorNop implements BehaviorControl<EntityLiving> {

    private final int minDuration;
    private final int maxDuration;
    private Behavior.Status status;
    private long endTimestamp;

    public BehaviorNop(int i, int j) {
        this.status = Behavior.Status.STOPPED;
        this.minDuration = i;
        this.maxDuration = j;
    }

    @Override
    public Behavior.Status getStatus() {
        return this.status;
    }

    @Override
    public final boolean tryStart(WorldServer worldserver, EntityLiving entityliving, long i) {
        this.status = Behavior.Status.RUNNING;
        int j = this.minDuration + worldserver.getRandom().nextInt(this.maxDuration + 1 - this.minDuration);

        this.endTimestamp = i + (long) j;
        return true;
    }

    @Override
    public final void tickOrStop(WorldServer worldserver, EntityLiving entityliving, long i) {
        if (i > this.endTimestamp) {
            this.doStop(worldserver, entityliving, i);
        }

    }

    @Override
    public final void doStop(WorldServer worldserver, EntityLiving entityliving, long i) {
        this.status = Behavior.Status.STOPPED;
    }

    @Override
    public String debugString() {
        return this.getClass().getSimpleName();
    }
}
