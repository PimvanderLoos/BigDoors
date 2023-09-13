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

    private final Predicate<E> b;
    private final Behavior<? super E> c;
    private final boolean d;

    public BehaviorRunIf(Map<MemoryModuleType<?>, MemoryStatus> map, Predicate<E> predicate, Behavior<? super E> behavior, boolean flag) {
        super(a(map, behavior.a));
        this.b = predicate;
        this.c = behavior;
        this.d = flag;
    }

    private static Map<MemoryModuleType<?>, MemoryStatus> a(Map<MemoryModuleType<?>, MemoryStatus> map, Map<MemoryModuleType<?>, MemoryStatus> map1) {
        Map<MemoryModuleType<?>, MemoryStatus> map2 = Maps.newHashMap();

        map2.putAll(map);
        map2.putAll(map1);
        return map2;
    }

    public BehaviorRunIf(Predicate<E> predicate, Behavior<? super E> behavior) {
        this(ImmutableMap.of(), predicate, behavior, false);
    }

    @Override
    protected boolean a(WorldServer worldserver, E e0) {
        return this.b.test(e0) && this.c.a(worldserver, e0);
    }

    @Override
    protected boolean b(WorldServer worldserver, E e0, long i) {
        return this.d && this.b.test(e0) && this.c.b(worldserver, e0, i);
    }

    @Override
    protected boolean a(long i) {
        return false;
    }

    @Override
    protected void a(WorldServer worldserver, E e0, long i) {
        this.c.a(worldserver, e0, i);
    }

    @Override
    protected void d(WorldServer worldserver, E e0, long i) {
        this.c.d(worldserver, e0, i);
    }

    @Override
    protected void c(WorldServer worldserver, E e0, long i) {
        this.c.c(worldserver, e0, i);
    }

    @Override
    public String toString() {
        return "RunIf: " + this.c;
    }
}
