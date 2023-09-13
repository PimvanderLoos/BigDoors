package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class BehaviorExpirableMemory<E extends EntityInsentient, T> extends Behavior<E> {

    private final Predicate<E> predicate;
    private final MemoryModuleType<? extends T> sourceMemory;
    private final MemoryModuleType<T> targetMemory;
    private final UniformInt durationOfCopy;

    public BehaviorExpirableMemory(Predicate<E> predicate, MemoryModuleType<? extends T> memorymoduletype, MemoryModuleType<T> memorymoduletype1, UniformInt uniformint) {
        super(ImmutableMap.of(memorymoduletype, MemoryStatus.VALUE_PRESENT, memorymoduletype1, MemoryStatus.VALUE_ABSENT));
        this.predicate = predicate;
        this.sourceMemory = memorymoduletype;
        this.targetMemory = memorymoduletype1;
        this.durationOfCopy = uniformint;
    }

    protected boolean a(WorldServer worldserver, E e0) {
        return this.predicate.test(e0);
    }

    protected void a(WorldServer worldserver, E e0, long i) {
        BehaviorController<?> behaviorcontroller = e0.getBehaviorController();

        behaviorcontroller.a(this.targetMemory, behaviorcontroller.getMemory(this.sourceMemory).get(), (long) this.durationOfCopy.a(worldserver.random));
    }
}
