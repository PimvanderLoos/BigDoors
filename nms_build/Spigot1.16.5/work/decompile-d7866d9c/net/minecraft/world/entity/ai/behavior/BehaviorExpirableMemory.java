package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.IntRange;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

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
