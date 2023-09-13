package net.minecraft.world.entity.ai.behavior;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public abstract class Behavior<E extends EntityLiving> implements BehaviorControl<E> {

    public static final int DEFAULT_DURATION = 60;
    protected final Map<MemoryModuleType<?>, MemoryStatus> entryCondition;
    private Behavior.Status status;
    private long endTimestamp;
    private final int minDuration;
    private final int maxDuration;

    public Behavior(Map<MemoryModuleType<?>, MemoryStatus> map) {
        this(map, 60);
    }

    public Behavior(Map<MemoryModuleType<?>, MemoryStatus> map, int i) {
        this(map, i, i);
    }

    public Behavior(Map<MemoryModuleType<?>, MemoryStatus> map, int i, int j) {
        this.status = Behavior.Status.STOPPED;
        this.minDuration = i;
        this.maxDuration = j;
        this.entryCondition = map;
    }

    @Override
    public Behavior.Status getStatus() {
        return this.status;
    }

    @Override
    public final boolean tryStart(WorldServer worldserver, E e0, long i) {
        if (this.hasRequiredMemories(e0) && this.checkExtraStartConditions(worldserver, e0)) {
            this.status = Behavior.Status.RUNNING;
            int j = this.minDuration + worldserver.getRandom().nextInt(this.maxDuration + 1 - this.minDuration);

            this.endTimestamp = i + (long) j;
            this.start(worldserver, e0, i);
            return true;
        } else {
            return false;
        }
    }

    protected void start(WorldServer worldserver, E e0, long i) {}

    @Override
    public final void tickOrStop(WorldServer worldserver, E e0, long i) {
        if (!this.timedOut(i) && this.canStillUse(worldserver, e0, i)) {
            this.tick(worldserver, e0, i);
        } else {
            this.doStop(worldserver, e0, i);
        }

    }

    protected void tick(WorldServer worldserver, E e0, long i) {}

    @Override
    public final void doStop(WorldServer worldserver, E e0, long i) {
        this.status = Behavior.Status.STOPPED;
        this.stop(worldserver, e0, i);
    }

    protected void stop(WorldServer worldserver, E e0, long i) {}

    protected boolean canStillUse(WorldServer worldserver, E e0, long i) {
        return false;
    }

    protected boolean timedOut(long i) {
        return i > this.endTimestamp;
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, E e0) {
        return true;
    }

    @Override
    public String debugString() {
        return this.getClass().getSimpleName();
    }

    protected boolean hasRequiredMemories(E e0) {
        Iterator iterator = this.entryCondition.entrySet().iterator();

        MemoryModuleType memorymoduletype;
        MemoryStatus memorystatus;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            Entry<MemoryModuleType<?>, MemoryStatus> entry = (Entry) iterator.next();

            memorymoduletype = (MemoryModuleType) entry.getKey();
            memorystatus = (MemoryStatus) entry.getValue();
        } while (e0.getBrain().checkMemory(memorymoduletype, memorystatus));

        return false;
    }

    public static enum Status {

        STOPPED, RUNNING;

        private Status() {}
    }
}
