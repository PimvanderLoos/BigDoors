package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

public class BehaviorGate<E extends EntityLiving> implements BehaviorControl<E> {

    private final Map<MemoryModuleType<?>, MemoryStatus> entryCondition;
    private final Set<MemoryModuleType<?>> exitErasedMemories;
    private final BehaviorGate.Order orderPolicy;
    private final BehaviorGate.Execution runningPolicy;
    private final ShufflingList<BehaviorControl<? super E>> behaviors = new ShufflingList<>();
    private Behavior.Status status;

    public BehaviorGate(Map<MemoryModuleType<?>, MemoryStatus> map, Set<MemoryModuleType<?>> set, BehaviorGate.Order behaviorgate_order, BehaviorGate.Execution behaviorgate_execution, List<Pair<? extends BehaviorControl<? super E>, Integer>> list) {
        this.status = Behavior.Status.STOPPED;
        this.entryCondition = map;
        this.exitErasedMemories = set;
        this.orderPolicy = behaviorgate_order;
        this.runningPolicy = behaviorgate_execution;
        list.forEach((pair) -> {
            this.behaviors.add((BehaviorControl) pair.getFirst(), (Integer) pair.getSecond());
        });
    }

    @Override
    public Behavior.Status getStatus() {
        return this.status;
    }

    private boolean hasRequiredMemories(E e0) {
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

    @Override
    public final boolean tryStart(WorldServer worldserver, E e0, long i) {
        if (this.hasRequiredMemories(e0)) {
            this.status = Behavior.Status.RUNNING;
            this.orderPolicy.apply(this.behaviors);
            this.runningPolicy.apply(this.behaviors.stream(), worldserver, e0, i);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public final void tickOrStop(WorldServer worldserver, E e0, long i) {
        this.behaviors.stream().filter((behaviorcontrol) -> {
            return behaviorcontrol.getStatus() == Behavior.Status.RUNNING;
        }).forEach((behaviorcontrol) -> {
            behaviorcontrol.tickOrStop(worldserver, e0, i);
        });
        if (this.behaviors.stream().noneMatch((behaviorcontrol) -> {
            return behaviorcontrol.getStatus() == Behavior.Status.RUNNING;
        })) {
            this.doStop(worldserver, e0, i);
        }

    }

    @Override
    public final void doStop(WorldServer worldserver, E e0, long i) {
        this.status = Behavior.Status.STOPPED;
        this.behaviors.stream().filter((behaviorcontrol) -> {
            return behaviorcontrol.getStatus() == Behavior.Status.RUNNING;
        }).forEach((behaviorcontrol) -> {
            behaviorcontrol.doStop(worldserver, e0, i);
        });
        Set set = this.exitErasedMemories;
        BehaviorController behaviorcontroller = e0.getBrain();

        Objects.requireNonNull(behaviorcontroller);
        set.forEach(behaviorcontroller::eraseMemory);
    }

    @Override
    public String debugString() {
        return this.getClass().getSimpleName();
    }

    public String toString() {
        Set<? extends BehaviorControl<? super E>> set = (Set) this.behaviors.stream().filter((behaviorcontrol) -> {
            return behaviorcontrol.getStatus() == Behavior.Status.RUNNING;
        }).collect(Collectors.toSet());
        String s = this.getClass().getSimpleName();

        return "(" + s + "): " + set;
    }

    public static enum Order {

        ORDERED((shufflinglist) -> {
        }), SHUFFLED(ShufflingList::shuffle);

        private final Consumer<ShufflingList<?>> consumer;

        private Order(Consumer consumer) {
            this.consumer = consumer;
        }

        public void apply(ShufflingList<?> shufflinglist) {
            this.consumer.accept(shufflinglist);
        }
    }

    public static enum Execution {

        RUN_ONE {
            @Override
            public <E extends EntityLiving> void apply(Stream<BehaviorControl<? super E>> stream, WorldServer worldserver, E e0, long i) {
                stream.filter((behaviorcontrol) -> {
                    return behaviorcontrol.getStatus() == Behavior.Status.STOPPED;
                }).filter((behaviorcontrol) -> {
                    return behaviorcontrol.tryStart(worldserver, e0, i);
                }).findFirst();
            }
        },
        TRY_ALL {
            @Override
            public <E extends EntityLiving> void apply(Stream<BehaviorControl<? super E>> stream, WorldServer worldserver, E e0, long i) {
                stream.filter((behaviorcontrol) -> {
                    return behaviorcontrol.getStatus() == Behavior.Status.STOPPED;
                }).forEach((behaviorcontrol) -> {
                    behaviorcontrol.tryStart(worldserver, e0, i);
                });
            }
        };

        Execution() {}

        public abstract <E extends EntityLiving> void apply(Stream<BehaviorControl<? super E>> stream, WorldServer worldserver, E e0, long i);
    }
}
