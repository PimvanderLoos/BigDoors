package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class BehaviorRunIf<E extends EntityLiving> extends Behavior<E> {

    private final Predicate<E> predicate;
    private final Behavior<? super E> wrappedBehavior;
    private final boolean checkWhileRunningAlso;

    public BehaviorRunIf(Map<MemoryModuleType<?>, MemoryStatus> map, Predicate<E> predicate, Behavior<? super E> behavior, boolean flag) {
        super(mergeMaps(map, behavior.entryCondition));
        this.predicate = predicate;
        this.wrappedBehavior = behavior;
        this.checkWhileRunningAlso = flag;
    }

    private static Map<MemoryModuleType<?>, MemoryStatus> mergeMaps(Map<MemoryModuleType<?>, MemoryStatus> map, Map<MemoryModuleType<?>, MemoryStatus> map1) {
        Map<MemoryModuleType<?>, MemoryStatus> map2 = Maps.newHashMap();

        map2.putAll(map);
        map2.putAll(map1);
        return map2;
    }

    public BehaviorRunIf(Predicate<E> predicate, Behavior<? super E> behavior, boolean flag) {
        this(ImmutableMap.of(), predicate, behavior, flag);
    }

    public BehaviorRunIf(Predicate<E> predicate, Behavior<? super E> behavior) {
        this(ImmutableMap.of(), predicate, behavior, false);
    }

    public BehaviorRunIf(Map<MemoryModuleType<?>, MemoryStatus> map, Behavior<? super E> behavior) {
        this(map, (entityliving) -> {
            return true;
        }, behavior, false);
    }

    @Override
    protected boolean checkExtraStartConditions(WorldServer worldserver, E e0) {
        return this.predicate.test(e0) && this.wrappedBehavior.checkExtraStartConditions(worldserver, e0);
    }

    @Override
    protected boolean canStillUse(WorldServer worldserver, E e0, long i) {
        return this.checkWhileRunningAlso && this.predicate.test(e0) && this.wrappedBehavior.canStillUse(worldserver, e0, i);
    }

    @Override
    protected boolean timedOut(long i) {
        return false;
    }

    @Override
    protected void start(WorldServer worldserver, E e0, long i) {
        this.wrappedBehavior.start(worldserver, e0, i);
    }

    @Override
    protected void tick(WorldServer worldserver, E e0, long i) {
        this.wrappedBehavior.tick(worldserver, e0, i);
    }

    @Override
    protected void stop(WorldServer worldserver, E e0, long i) {
        this.wrappedBehavior.stop(worldserver, e0, i);
    }

    @Override
    public String toString() {
        return "RunIf: " + this.wrappedBehavior;
    }
}
