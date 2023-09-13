package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;

public class BehaviorRemoveMemory<E extends EntityLiving> extends Behavior<E> {

    private final Predicate<E> b;
    private final MemoryModuleType<?> c;

    public BehaviorRemoveMemory(Predicate<E> predicate, MemoryModuleType<?> memorymoduletype) {
        super(ImmutableMap.of(memorymoduletype, MemoryStatus.VALUE_PRESENT));
        this.b = predicate;
        this.c = memorymoduletype;
    }

    @Override
    protected boolean a(WorldServer worldserver, E e0) {
        return this.b.test(e0);
    }

    @Override
    protected void a(WorldServer worldserver, E e0, long i) {
        e0.getBehaviorController().removeMemory(this.c);
    }
}
