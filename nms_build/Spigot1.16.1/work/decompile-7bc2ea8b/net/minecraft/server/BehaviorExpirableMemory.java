package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;

public class BehaviorExpirableMemory<E extends EntityInsentient, T> extends Behavior<E> {

    private final Predicate<E> b;
    private final MemoryModuleType<? extends T> c;
    private final MemoryModuleType<T> d;
    private final IntRange e;

    public BehaviorExpirableMemory(Predicate<E> predicate, MemoryModuleType<? extends T> memorymoduletype, MemoryModuleType<T> memorymoduletype1, IntRange intrange) {
        super(ImmutableMap.of(memorymoduletype, MemoryStatus.VALUE_PRESENT, memorymoduletype1, MemoryStatus.VALUE_ABSENT));
        this.b = predicate;
        this.c = memorymoduletype;
        this.d = memorymoduletype1;
        this.e = intrange;
    }

    protected boolean a(WorldServer worldserver, E e0) {
        return this.b.test(e0);
    }

    protected void a(WorldServer worldserver, E e0, long i) {
        BehaviorController<?> behaviorcontroller = e0.getBehaviorController();

        behaviorcontroller.a(this.d, behaviorcontroller.getMemory(this.c).get(), (long) this.e.a(worldserver.random));
    }
}
