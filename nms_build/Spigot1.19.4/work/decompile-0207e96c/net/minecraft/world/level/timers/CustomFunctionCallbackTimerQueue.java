package net.minecraft.world.level.timers;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.primitives.UnsignedLong;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.slf4j.Logger;

public class CustomFunctionCallbackTimerQueue<T> {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String CALLBACK_DATA_TAG = "Callback";
    private static final String TIMER_NAME_TAG = "Name";
    private static final String TIMER_TRIGGER_TIME_TAG = "TriggerTime";
    private final CustomFunctionCallbackTimers<T> callbacksRegistry;
    private final Queue<CustomFunctionCallbackTimerQueue.a<T>> queue;
    private UnsignedLong sequentialId;
    private final Table<String, Long, CustomFunctionCallbackTimerQueue.a<T>> events;

    private static <T> Comparator<CustomFunctionCallbackTimerQueue.a<T>> createComparator() {
        return Comparator.comparingLong((customfunctioncallbacktimerqueue_a) -> {
            return customfunctioncallbacktimerqueue_a.triggerTime;
        }).thenComparing((customfunctioncallbacktimerqueue_a) -> {
            return customfunctioncallbacktimerqueue_a.sequentialId;
        });
    }

    public CustomFunctionCallbackTimerQueue(CustomFunctionCallbackTimers<T> customfunctioncallbacktimers, Stream<? extends Dynamic<?>> stream) {
        this(customfunctioncallbacktimers);
        this.queue.clear();
        this.events.clear();
        this.sequentialId = UnsignedLong.ZERO;
        stream.forEach((dynamic) -> {
            NBTBase nbtbase = (NBTBase) dynamic.convert(DynamicOpsNBT.INSTANCE).getValue();

            if (nbtbase instanceof NBTTagCompound) {
                NBTTagCompound nbttagcompound = (NBTTagCompound) nbtbase;

                this.loadEvent(nbttagcompound);
            } else {
                CustomFunctionCallbackTimerQueue.LOGGER.warn("Invalid format of events: {}", nbtbase);
            }

        });
    }

    public CustomFunctionCallbackTimerQueue(CustomFunctionCallbackTimers<T> customfunctioncallbacktimers) {
        this.queue = new PriorityQueue(createComparator());
        this.sequentialId = UnsignedLong.ZERO;
        this.events = HashBasedTable.create();
        this.callbacksRegistry = customfunctioncallbacktimers;
    }

    public void tick(T t0, long i) {
        while (true) {
            CustomFunctionCallbackTimerQueue.a<T> customfunctioncallbacktimerqueue_a = (CustomFunctionCallbackTimerQueue.a) this.queue.peek();

            if (customfunctioncallbacktimerqueue_a == null || customfunctioncallbacktimerqueue_a.triggerTime > i) {
                return;
            }

            this.queue.remove();
            this.events.remove(customfunctioncallbacktimerqueue_a.id, i);
            customfunctioncallbacktimerqueue_a.callback.handle(t0, this, i);
        }
    }

    public void schedule(String s, long i, CustomFunctionCallbackTimer<T> customfunctioncallbacktimer) {
        if (!this.events.contains(s, i)) {
            this.sequentialId = this.sequentialId.plus(UnsignedLong.ONE);
            CustomFunctionCallbackTimerQueue.a<T> customfunctioncallbacktimerqueue_a = new CustomFunctionCallbackTimerQueue.a<>(i, this.sequentialId, s, customfunctioncallbacktimer);

            this.events.put(s, i, customfunctioncallbacktimerqueue_a);
            this.queue.add(customfunctioncallbacktimerqueue_a);
        }
    }

    public int remove(String s) {
        Collection<CustomFunctionCallbackTimerQueue.a<T>> collection = this.events.row(s).values();
        Queue queue = this.queue;

        Objects.requireNonNull(this.queue);
        collection.forEach(queue::remove);
        int i = collection.size();

        collection.clear();
        return i;
    }

    public Set<String> getEventsIds() {
        return Collections.unmodifiableSet(this.events.rowKeySet());
    }

    private void loadEvent(NBTTagCompound nbttagcompound) {
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Callback");
        CustomFunctionCallbackTimer<T> customfunctioncallbacktimer = this.callbacksRegistry.deserialize(nbttagcompound1);

        if (customfunctioncallbacktimer != null) {
            String s = nbttagcompound.getString("Name");
            long i = nbttagcompound.getLong("TriggerTime");

            this.schedule(s, i, customfunctioncallbacktimer);
        }

    }

    private NBTTagCompound storeEvent(CustomFunctionCallbackTimerQueue.a<T> customfunctioncallbacktimerqueue_a) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.putString("Name", customfunctioncallbacktimerqueue_a.id);
        nbttagcompound.putLong("TriggerTime", customfunctioncallbacktimerqueue_a.triggerTime);
        nbttagcompound.put("Callback", this.callbacksRegistry.serialize(customfunctioncallbacktimerqueue_a.callback));
        return nbttagcompound;
    }

    public NBTTagList store() {
        NBTTagList nbttaglist = new NBTTagList();
        Stream stream = this.queue.stream().sorted(createComparator()).map(this::storeEvent);

        Objects.requireNonNull(nbttaglist);
        stream.forEach(nbttaglist::add);
        return nbttaglist;
    }

    public static class a<T> {

        public final long triggerTime;
        public final UnsignedLong sequentialId;
        public final String id;
        public final CustomFunctionCallbackTimer<T> callback;

        a(long i, UnsignedLong unsignedlong, String s, CustomFunctionCallbackTimer<T> customfunctioncallbacktimer) {
            this.triggerTime = i;
            this.sequentialId = unsignedlong;
            this.id = s;
            this.callback = customfunctioncallbacktimer;
        }
    }
}
