package net.minecraft.world.entity.ai.behavior;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public abstract class Behavior<E extends EntityLiving> {

    protected final Map<MemoryModuleType<?>, MemoryStatus> a;
    private Behavior.Status b;
    private long c;
    private final int d;
    private final int e;

    public Behavior(Map<MemoryModuleType<?>, MemoryStatus> map) {
        this(map, 60);
    }

    public Behavior(Map<MemoryModuleType<?>, MemoryStatus> map, int i) {
        this(map, i, i);
    }

    public Behavior(Map<MemoryModuleType<?>, MemoryStatus> map, int i, int j) {
        this.b = Behavior.Status.STOPPED;
        this.d = i;
        this.e = j;
        this.a = map;
    }

    public Behavior.Status a() {
        return this.b;
    }

    public final boolean e(WorldServer worldserver, E e0, long i) {
        if (this.a(e0) && this.a(worldserver, e0)) {
            this.b = Behavior.Status.RUNNING;
            int j = this.d + worldserver.getRandom().nextInt(this.e + 1 - this.d);

            this.c = i + (long) j;
            this.a(worldserver, e0, i);
            return true;
        } else {
            return false;
        }
    }

    protected void a(WorldServer worldserver, E e0, long i) {}

    public final void f(WorldServer worldserver, E e0, long i) {
        if (!this.a(i) && this.b(worldserver, e0, i)) {
            this.d(worldserver, e0, i);
        } else {
            this.g(worldserver, e0, i);
        }

    }

    protected void d(WorldServer worldserver, E e0, long i) {}

    public final void g(WorldServer worldserver, E e0, long i) {
        this.b = Behavior.Status.STOPPED;
        this.c(worldserver, e0, i);
    }

    protected void c(WorldServer worldserver, E e0, long i) {}

    protected boolean b(WorldServer worldserver, E e0, long i) {
        return false;
    }

    protected boolean a(long i) {
        return i > this.c;
    }

    protected boolean a(WorldServer worldserver, E e0) {
        return true;
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }

    private boolean a(E e0) {
        Iterator iterator = this.a.entrySet().iterator();

        MemoryModuleType memorymoduletype;
        MemoryStatus memorystatus;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            Entry<MemoryModuleType<?>, MemoryStatus> entry = (Entry) iterator.next();

            memorymoduletype = (MemoryModuleType) entry.getKey();
            memorystatus = (MemoryStatus) entry.getValue();
        } while (e0.getBehaviorController().a(memorymoduletype, memorystatus));

        return false;
    }

    public static enum Status {

        STOPPED, RUNNING;

        private Status() {}
    }
}
