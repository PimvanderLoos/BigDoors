package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class BehaviorGate<E extends EntityLiving> extends Behavior<E> {

    private final Set<MemoryModuleType<?>> exitErasedMemories;
    private final BehaviorGate.Order orderPolicy;
    private final BehaviorGate.Execution runningPolicy;
    private final ShufflingList<Behavior<? super E>> behaviors = new ShufflingList<>();

    public BehaviorGate(Map<MemoryModuleType<?>, MemoryStatus> map, Set<MemoryModuleType<?>> set, BehaviorGate.Order behaviorgate_order, BehaviorGate.Execution behaviorgate_execution, List<Pair<Behavior<? super E>, Integer>> list) {
        super(map);
        this.exitErasedMemories = set;
        this.orderPolicy = behaviorgate_order;
        this.runningPolicy = behaviorgate_execution;
        list.forEach((pair) -> {
            this.behaviors.a((Behavior) pair.getFirst(), (Integer) pair.getSecond());
        });
    }

    @Override
    protected boolean b(WorldServer worldserver, E e0, long i) {
        return this.behaviors.b().filter((behavior) -> {
            return behavior.a() == Behavior.Status.RUNNING;
        }).anyMatch((behavior) -> {
            return behavior.b(worldserver, e0, i);
        });
    }

    @Override
    protected boolean a(long i) {
        return false;
    }

    @Override
    protected void a(WorldServer worldserver, E e0, long i) {
        this.orderPolicy.a(this.behaviors);
        this.runningPolicy.a(this.behaviors.b(), worldserver, e0, i);
    }

    @Override
    protected void d(WorldServer worldserver, E e0, long i) {
        this.behaviors.b().filter((behavior) -> {
            return behavior.a() == Behavior.Status.RUNNING;
        }).forEach((behavior) -> {
            behavior.f(worldserver, e0, i);
        });
    }

    @Override
    protected void c(WorldServer worldserver, E e0, long i) {
        this.behaviors.b().filter((behavior) -> {
            return behavior.a() == Behavior.Status.RUNNING;
        }).forEach((behavior) -> {
            behavior.g(worldserver, e0, i);
        });
        Set set = this.exitErasedMemories;
        BehaviorController behaviorcontroller = e0.getBehaviorController();

        Objects.requireNonNull(behaviorcontroller);
        set.forEach(behaviorcontroller::removeMemory);
    }

    @Override
    public String toString() {
        Set<? extends Behavior<? super E>> set = (Set) this.behaviors.b().filter((behavior) -> {
            return behavior.a() == Behavior.Status.RUNNING;
        }).collect(Collectors.toSet());
        String s = this.getClass().getSimpleName();

        return "(" + s + "): " + set;
    }

    public static enum Order {

        ORDERED((shufflinglist) -> {
        }), SHUFFLED(ShufflingList::a);

        private final Consumer<ShufflingList<?>> consumer;

        private Order(Consumer consumer) {
            this.consumer = consumer;
        }

        public void a(ShufflingList<?> shufflinglist) {
            this.consumer.accept(shufflinglist);
        }
    }

    public static enum Execution {

        RUN_ONE {
            @Override
            public <E extends EntityLiving> void a(Stream<Behavior<? super E>> stream, WorldServer worldserver, E e0, long i) {
                stream.filter((behavior) -> {
                    return behavior.a() == Behavior.Status.STOPPED;
                }).filter((behavior) -> {
                    return behavior.e(worldserver, e0, i);
                }).findFirst();
            }
        },
        TRY_ALL {
            @Override
            public <E extends EntityLiving> void a(Stream<Behavior<? super E>> stream, WorldServer worldserver, E e0, long i) {
                stream.filter((behavior) -> {
                    return behavior.a() == Behavior.Status.STOPPED;
                }).forEach((behavior) -> {
                    behavior.e(worldserver, e0, i);
                });
            }
        };

        Execution() {}

        public abstract <E extends EntityLiving> void a(Stream<Behavior<? super E>> stream, WorldServer worldserver, E e0, long i);
    }
}
