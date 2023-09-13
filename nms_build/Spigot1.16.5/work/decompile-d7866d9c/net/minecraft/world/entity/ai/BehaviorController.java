package net.minecraft.world.entity.ai;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.IRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.ExpirableMemory;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BehaviorController<E extends EntityLiving> {

    private static final Logger LOGGER = LogManager.getLogger();
    private final Supplier<Codec<BehaviorController<E>>> b;
    private final Map<MemoryModuleType<?>, Optional<? extends ExpirableMemory<?>>> memories = Maps.newHashMap();
    private final Map<SensorType<? extends Sensor<? super E>>, Sensor<? super E>> sensors = Maps.newLinkedHashMap();
    private final Map<Integer, Map<Activity, Set<Behavior<? super E>>>> e = Maps.newTreeMap();
    private Schedule schedule;
    private final Map<Activity, Set<Pair<MemoryModuleType<?>, MemoryStatus>>> g;
    private final Map<Activity, Set<MemoryModuleType<?>>> h;
    private Set<Activity> i;
    private final Set<Activity> j;
    private Activity k;
    private long l;

    public static <E extends EntityLiving> BehaviorController.b<E> a(Collection<? extends MemoryModuleType<?>> collection, Collection<? extends SensorType<? extends Sensor<? super E>>> collection1) {
        return new BehaviorController.b<>(collection, collection1);
    }

    public static <E extends EntityLiving> Codec<BehaviorController<E>> b(final Collection<? extends MemoryModuleType<?>> collection, final Collection<? extends SensorType<? extends Sensor<? super E>>> collection1) {
        final MutableObject<Codec<BehaviorController<E>>> mutableobject = new MutableObject();

        mutableobject.setValue((new MapCodec<BehaviorController<E>>() {
            public <T> Stream<T> keys(DynamicOps<T> dynamicops) {
                return collection.stream().flatMap((memorymoduletype) -> {
                    return SystemUtils.a(memorymoduletype.getSerializer().map((codec) -> {
                        return IRegistry.MEMORY_MODULE_TYPE.getKey(memorymoduletype);
                    }));
                }).map((minecraftkey) -> {
                    return dynamicops.createString(minecraftkey.toString());
                });
            }

            public <T> DataResult<BehaviorController<E>> decode(DynamicOps<T> dynamicops, MapLike<T> maplike) {
                MutableObject<DataResult<Builder<BehaviorController.a<?>>>> mutableobject1 = new MutableObject(DataResult.success(ImmutableList.builder()));

                maplike.entries().forEach((pair) -> {
                    DataResult<MemoryModuleType<?>> dataresult = IRegistry.MEMORY_MODULE_TYPE.parse(dynamicops, pair.getFirst());
                    DataResult<? extends BehaviorController.a<?>> dataresult1 = dataresult.flatMap((memorymoduletype) -> {
                        return this.a(memorymoduletype, dynamicops, pair.getSecond());
                    });

                    mutableobject1.setValue(((DataResult) mutableobject1.getValue()).apply2(Builder::add, dataresult1));
                });
                DataResult dataresult = (DataResult) mutableobject1.getValue();
                Logger logger = BehaviorController.LOGGER;

                logger.getClass();
                ImmutableList<BehaviorController.a<?>> immutablelist = (ImmutableList) dataresult.resultOrPartial(logger::error).map(Builder::build).orElseGet(ImmutableList::of);
                Collection collection2 = collection;
                Collection collection3 = collection1;
                MutableObject mutableobject2 = mutableobject;

                mutableobject.getClass();
                return DataResult.success(new BehaviorController<>(collection2, collection3, immutablelist, mutableobject2::getValue));
            }

            private <T, U> DataResult<BehaviorController.a<U>> a(MemoryModuleType<U> memorymoduletype, DynamicOps<T> dynamicops, T t0) {
                return ((DataResult) memorymoduletype.getSerializer().map(DataResult::success).orElseGet(() -> {
                    return DataResult.error("No codec for memory: " + memorymoduletype);
                })).flatMap((codec) -> {
                    return codec.parse(dynamicops, t0);
                }).map((expirablememory) -> {
                    return new BehaviorController.a<>(memorymoduletype, Optional.of(expirablememory));
                });
            }

            public <T> RecordBuilder<T> encode(BehaviorController<E> behaviorcontroller, DynamicOps<T> dynamicops, RecordBuilder<T> recordbuilder) {
                behaviorcontroller.j().forEach((behaviorcontroller_a) -> {
                    behaviorcontroller_a.a(dynamicops, recordbuilder);
                });
                return recordbuilder;
            }
        }).fieldOf("memories").codec());
        return (Codec) mutableobject.getValue();
    }

    public BehaviorController(Collection<? extends MemoryModuleType<?>> collection, Collection<? extends SensorType<? extends Sensor<? super E>>> collection1, ImmutableList<BehaviorController.a<?>> immutablelist, Supplier<Codec<BehaviorController<E>>> supplier) {
        this.schedule = Schedule.EMPTY;
        this.g = Maps.newHashMap();
        this.h = Maps.newHashMap();
        this.i = Sets.newHashSet();
        this.j = Sets.newHashSet();
        this.k = Activity.IDLE;
        this.l = -9999L;
        this.b = supplier;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            MemoryModuleType<?> memorymoduletype = (MemoryModuleType) iterator.next();

            this.memories.put(memorymoduletype, Optional.empty());
        }

        iterator = collection1.iterator();

        while (iterator.hasNext()) {
            SensorType<? extends Sensor<? super E>> sensortype = (SensorType) iterator.next();

            this.sensors.put(sensortype, sensortype.a());
        }

        iterator = this.sensors.values().iterator();

        while (iterator.hasNext()) {
            Sensor<? super E> sensor = (Sensor) iterator.next();
            Iterator iterator1 = sensor.a().iterator();

            while (iterator1.hasNext()) {
                MemoryModuleType<?> memorymoduletype1 = (MemoryModuleType) iterator1.next();

                this.memories.put(memorymoduletype1, Optional.empty());
            }
        }

        UnmodifiableIterator unmodifiableiterator = immutablelist.iterator();

        while (unmodifiableiterator.hasNext()) {
            BehaviorController.a<?> behaviorcontroller_a = (BehaviorController.a) unmodifiableiterator.next();

            behaviorcontroller_a.a(this);
        }

    }

    public <T> DataResult<T> a(DynamicOps<T> dynamicops) {
        return ((Codec) this.b.get()).encodeStart(dynamicops, this);
    }

    private Stream<BehaviorController.a<?>> j() {
        return this.memories.entrySet().stream().map((entry) -> {
            return BehaviorController.a.b((MemoryModuleType) entry.getKey(), (Optional) entry.getValue());
        });
    }

    public boolean hasMemory(MemoryModuleType<?> memorymoduletype) {
        return this.a(memorymoduletype, MemoryStatus.VALUE_PRESENT);
    }

    public <U> void removeMemory(MemoryModuleType<U> memorymoduletype) {
        this.setMemory(memorymoduletype, Optional.empty());
    }

    public <U> void setMemory(MemoryModuleType<U> memorymoduletype, @Nullable U u0) {
        this.setMemory(memorymoduletype, Optional.ofNullable(u0));
    }

    public <U> void a(MemoryModuleType<U> memorymoduletype, U u0, long i) {
        this.b(memorymoduletype, Optional.of(ExpirableMemory.a(u0, i)));
    }

    public <U> void setMemory(MemoryModuleType<U> memorymoduletype, Optional<? extends U> optional) {
        this.b(memorymoduletype, optional.map(ExpirableMemory::a));
    }

    private <U> void b(MemoryModuleType<U> memorymoduletype, Optional<? extends ExpirableMemory<?>> optional) {
        if (this.memories.containsKey(memorymoduletype)) {
            if (optional.isPresent() && this.a(((ExpirableMemory) optional.get()).c())) {
                this.removeMemory(memorymoduletype);
            } else {
                this.memories.put(memorymoduletype, optional);
            }
        }

    }

    public <U> Optional<U> getMemory(MemoryModuleType<U> memorymoduletype) {
        return ((Optional) this.memories.get(memorymoduletype)).map(ExpirableMemory::c);
    }

    public <U> boolean b(MemoryModuleType<U> memorymoduletype, U u0) {
        return !this.hasMemory(memorymoduletype) ? false : this.getMemory(memorymoduletype).filter((object) -> {
            return object.equals(u0);
        }).isPresent();
    }

    public boolean a(MemoryModuleType<?> memorymoduletype, MemoryStatus memorystatus) {
        Optional<? extends ExpirableMemory<?>> optional = (Optional) this.memories.get(memorymoduletype);

        return optional == null ? false : memorystatus == MemoryStatus.REGISTERED || memorystatus == MemoryStatus.VALUE_PRESENT && optional.isPresent() || memorystatus == MemoryStatus.VALUE_ABSENT && !optional.isPresent();
    }

    public Schedule getSchedule() {
        return this.schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public void a(Set<Activity> set) {
        this.i = set;
    }

    @Deprecated
    public List<Behavior<? super E>> d() {
        List<Behavior<? super E>> list = new ObjectArrayList();
        Iterator iterator = this.e.values().iterator();

        while (iterator.hasNext()) {
            Map<Activity, Set<Behavior<? super E>>> map = (Map) iterator.next();
            Iterator iterator1 = map.values().iterator();

            while (iterator1.hasNext()) {
                Set<Behavior<? super E>> set = (Set) iterator1.next();
                Iterator iterator2 = set.iterator();

                while (iterator2.hasNext()) {
                    Behavior<? super E> behavior = (Behavior) iterator2.next();

                    if (behavior.a() == Behavior.Status.RUNNING) {
                        list.add(behavior);
                    }
                }
            }
        }

        return list;
    }

    public void e() {
        this.d(this.k);
    }

    public Optional<Activity> f() {
        Iterator iterator = this.j.iterator();

        Activity activity;

        do {
            if (!iterator.hasNext()) {
                return Optional.empty();
            }

            activity = (Activity) iterator.next();
        } while (this.i.contains(activity));

        return Optional.of(activity);
    }

    public void a(Activity activity) {
        if (this.f(activity)) {
            this.d(activity);
        } else {
            this.e();
        }

    }

    private void d(Activity activity) {
        if (!this.c(activity)) {
            this.e(activity);
            this.j.clear();
            this.j.addAll(this.i);
            this.j.add(activity);
        }
    }

    private void e(Activity activity) {
        Iterator iterator = this.j.iterator();

        while (iterator.hasNext()) {
            Activity activity1 = (Activity) iterator.next();

            if (activity1 != activity) {
                Set<MemoryModuleType<?>> set = (Set) this.h.get(activity1);

                if (set != null) {
                    Iterator iterator1 = set.iterator();

                    while (iterator1.hasNext()) {
                        MemoryModuleType<?> memorymoduletype = (MemoryModuleType) iterator1.next();

                        this.removeMemory(memorymoduletype);
                    }
                }
            }
        }

    }

    public void a(long i, long j) {
        if (j - this.l > 20L) {
            this.l = j;
            Activity activity = this.getSchedule().a((int) (i % 24000L));

            if (!this.j.contains(activity)) {
                this.a(activity);
            }
        }

    }

    public void a(List<Activity> list) {
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Activity activity = (Activity) iterator.next();

            if (this.f(activity)) {
                this.d(activity);
                break;
            }
        }

    }

    public void b(Activity activity) {
        this.k = activity;
    }

    public void a(Activity activity, int i, ImmutableList<? extends Behavior<? super E>> immutablelist) {
        this.a(activity, this.a(i, immutablelist));
    }

    public void a(Activity activity, int i, ImmutableList<? extends Behavior<? super E>> immutablelist, MemoryModuleType<?> memorymoduletype) {
        Set<Pair<MemoryModuleType<?>, MemoryStatus>> set = ImmutableSet.of(Pair.of(memorymoduletype, MemoryStatus.VALUE_PRESENT));
        Set<MemoryModuleType<?>> set1 = ImmutableSet.of(memorymoduletype);

        this.a(activity, this.a(i, immutablelist), set, set1);
    }

    public void a(Activity activity, ImmutableList<? extends Pair<Integer, ? extends Behavior<? super E>>> immutablelist) {
        this.a(activity, immutablelist, ImmutableSet.of(), Sets.newHashSet());
    }

    public void a(Activity activity, ImmutableList<? extends Pair<Integer, ? extends Behavior<? super E>>> immutablelist, Set<Pair<MemoryModuleType<?>, MemoryStatus>> set) {
        this.a(activity, immutablelist, set, Sets.newHashSet());
    }

    private void a(Activity activity, ImmutableList<? extends Pair<Integer, ? extends Behavior<? super E>>> immutablelist, Set<Pair<MemoryModuleType<?>, MemoryStatus>> set, Set<MemoryModuleType<?>> set1) {
        this.g.put(activity, set);
        if (!set1.isEmpty()) {
            this.h.put(activity, set1);
        }

        UnmodifiableIterator unmodifiableiterator = immutablelist.iterator();

        while (unmodifiableiterator.hasNext()) {
            Pair<Integer, ? extends Behavior<? super E>> pair = (Pair) unmodifiableiterator.next();

            ((Set) ((Map) this.e.computeIfAbsent(pair.getFirst(), (integer) -> {
                return Maps.newHashMap();
            })).computeIfAbsent(activity, (activity1) -> {
                return Sets.newLinkedHashSet();
            })).add(pair.getSecond());
        }

    }

    public boolean c(Activity activity) {
        return this.j.contains(activity);
    }

    public BehaviorController<E> h() {
        BehaviorController<E> behaviorcontroller = new BehaviorController<>(this.memories.keySet(), this.sensors.keySet(), ImmutableList.of(), this.b);
        Iterator iterator = this.memories.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<MemoryModuleType<?>, Optional<? extends ExpirableMemory<?>>> entry = (Entry) iterator.next();
            MemoryModuleType<?> memorymoduletype = (MemoryModuleType) entry.getKey();

            if (((Optional) entry.getValue()).isPresent()) {
                behaviorcontroller.memories.put(memorymoduletype, entry.getValue());
            }
        }

        return behaviorcontroller;
    }

    public void a(WorldServer worldserver, E e0) {
        this.k();
        this.c(worldserver, e0);
        this.d(worldserver, e0);
        this.e(worldserver, e0);
    }

    private void c(WorldServer worldserver, E e0) {
        Iterator iterator = this.sensors.values().iterator();

        while (iterator.hasNext()) {
            Sensor<? super E> sensor = (Sensor) iterator.next();

            sensor.b(worldserver, e0);
        }

    }

    private void k() {
        Iterator iterator = this.memories.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<MemoryModuleType<?>, Optional<? extends ExpirableMemory<?>>> entry = (Entry) iterator.next();

            if (((Optional) entry.getValue()).isPresent()) {
                ExpirableMemory<?> expirablememory = (ExpirableMemory) ((Optional) entry.getValue()).get();

                expirablememory.a();
                if (expirablememory.d()) {
                    this.removeMemory((MemoryModuleType) entry.getKey());
                }
            }
        }

    }

    public void b(WorldServer worldserver, E e0) {
        long i = e0.world.getTime();
        Iterator iterator = this.d().iterator();

        while (iterator.hasNext()) {
            Behavior<? super E> behavior = (Behavior) iterator.next();

            behavior.g(worldserver, e0, i);
        }

    }

    private void d(WorldServer worldserver, E e0) {
        long i = worldserver.getTime();
        Iterator iterator = this.e.values().iterator();

        while (iterator.hasNext()) {
            Map<Activity, Set<Behavior<? super E>>> map = (Map) iterator.next();
            Iterator iterator1 = map.entrySet().iterator();

            while (iterator1.hasNext()) {
                Entry<Activity, Set<Behavior<? super E>>> entry = (Entry) iterator1.next();
                Activity activity = (Activity) entry.getKey();

                if (this.j.contains(activity)) {
                    Set<Behavior<? super E>> set = (Set) entry.getValue();
                    Iterator iterator2 = set.iterator();

                    while (iterator2.hasNext()) {
                        Behavior<? super E> behavior = (Behavior) iterator2.next();

                        if (behavior.a() == Behavior.Status.STOPPED) {
                            behavior.e(worldserver, e0, i);
                        }
                    }
                }
            }
        }

    }

    private void e(WorldServer worldserver, E e0) {
        long i = worldserver.getTime();
        Iterator iterator = this.d().iterator();

        while (iterator.hasNext()) {
            Behavior<? super E> behavior = (Behavior) iterator.next();

            behavior.f(worldserver, e0, i);
        }

    }

    private boolean f(Activity activity) {
        if (!this.g.containsKey(activity)) {
            return false;
        } else {
            Iterator iterator = ((Set) this.g.get(activity)).iterator();

            MemoryModuleType memorymoduletype;
            MemoryStatus memorystatus;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                Pair<MemoryModuleType<?>, MemoryStatus> pair = (Pair) iterator.next();

                memorymoduletype = (MemoryModuleType) pair.getFirst();
                memorystatus = (MemoryStatus) pair.getSecond();
            } while (this.a(memorymoduletype, memorystatus));

            return false;
        }
    }

    private boolean a(Object object) {
        return object instanceof Collection && ((Collection) object).isEmpty();
    }

    ImmutableList<? extends Pair<Integer, ? extends Behavior<? super E>>> a(int i, ImmutableList<? extends Behavior<? super E>> immutablelist) {
        int j = i;
        Builder<Pair<Integer, ? extends Behavior<? super E>>> builder = ImmutableList.builder();
        UnmodifiableIterator unmodifiableiterator = immutablelist.iterator();

        while (unmodifiableiterator.hasNext()) {
            Behavior<? super E> behavior = (Behavior) unmodifiableiterator.next();

            builder.add(Pair.of(j++, behavior));
        }

        return builder.build();
    }

    static final class a<U> {

        private final MemoryModuleType<U> a;
        private final Optional<? extends ExpirableMemory<U>> b;

        private static <U> BehaviorController.a<U> b(MemoryModuleType<U> memorymoduletype, Optional<? extends ExpirableMemory<?>> optional) {
            return new BehaviorController.a<>(memorymoduletype, optional);
        }

        private a(MemoryModuleType<U> memorymoduletype, Optional<? extends ExpirableMemory<U>> optional) {
            this.a = memorymoduletype;
            this.b = optional;
        }

        private void a(BehaviorController<?> behaviorcontroller) {
            behaviorcontroller.b(this.a, this.b);
        }

        public <T> void a(DynamicOps<T> dynamicops, RecordBuilder<T> recordbuilder) {
            this.a.getSerializer().ifPresent((codec) -> {
                this.b.ifPresent((expirablememory) -> {
                    recordbuilder.add(IRegistry.MEMORY_MODULE_TYPE.encodeStart(dynamicops, this.a), codec.encodeStart(dynamicops, expirablememory));
                });
            });
        }
    }

    public static final class b<E extends EntityLiving> {

        private final Collection<? extends MemoryModuleType<?>> a;
        private final Collection<? extends SensorType<? extends Sensor<? super E>>> b;
        private final Codec<BehaviorController<E>> c;

        private b(Collection<? extends MemoryModuleType<?>> collection, Collection<? extends SensorType<? extends Sensor<? super E>>> collection1) {
            this.a = collection;
            this.b = collection1;
            this.c = BehaviorController.b(collection, collection1);
        }

        public BehaviorController<E> a(Dynamic<?> dynamic) {
            DataResult dataresult = this.c.parse(dynamic);
            Logger logger = BehaviorController.LOGGER;

            logger.getClass();
            return (BehaviorController) dataresult.resultOrPartial(logger::error).orElseGet(() -> {
                return new BehaviorController<>(this.a, this.b, ImmutableList.of(), () -> {
                    return this.c;
                });
            });
        }
    }
}
