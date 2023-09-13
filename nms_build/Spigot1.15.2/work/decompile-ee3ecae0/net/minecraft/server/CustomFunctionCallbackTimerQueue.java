package net.minecraft.server;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.primitives.UnsignedLong;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CustomFunctionCallbackTimerQueue<T> {

    private static final Logger LOGGER = LogManager.getLogger();
    private final CustomFunctionCallbackTimers<T> b;
    private final Queue<CustomFunctionCallbackTimerQueue.a<T>> c = new PriorityQueue(c());
    private UnsignedLong d;
    private final Table<String, Long, CustomFunctionCallbackTimerQueue.a<T>> e;

    private static <T> Comparator<CustomFunctionCallbackTimerQueue.a<T>> c() {
        return Comparator.comparingLong((customfunctioncallbacktimerqueue_a) -> {
            return customfunctioncallbacktimerqueue_a.a;
        }).thenComparing((customfunctioncallbacktimerqueue_a) -> {
            return customfunctioncallbacktimerqueue_a.b;
        });
    }

    public CustomFunctionCallbackTimerQueue(CustomFunctionCallbackTimers<T> customfunctioncallbacktimers) {
        this.d = UnsignedLong.ZERO;
        this.e = HashBasedTable.create();
        this.b = customfunctioncallbacktimers;
    }

    public void a(T t0, long i) {
        while (true) {
            CustomFunctionCallbackTimerQueue.a<T> customfunctioncallbacktimerqueue_a = (CustomFunctionCallbackTimerQueue.a) this.c.peek();

            if (customfunctioncallbacktimerqueue_a == null || customfunctioncallbacktimerqueue_a.a > i) {
                return;
            }

            this.c.remove();
            this.e.remove(customfunctioncallbacktimerqueue_a.c, i);
            customfunctioncallbacktimerqueue_a.d.a(t0, this, i);
        }
    }

    public void a(String s, long i, CustomFunctionCallbackTimer<T> customfunctioncallbacktimer) {
        if (!this.e.contains(s, i)) {
            this.d = this.d.plus(UnsignedLong.ONE);
            CustomFunctionCallbackTimerQueue.a<T> customfunctioncallbacktimerqueue_a = new CustomFunctionCallbackTimerQueue.a<>(i, this.d, s, customfunctioncallbacktimer);

            this.e.put(s, i, customfunctioncallbacktimerqueue_a);
            this.c.add(customfunctioncallbacktimerqueue_a);
        }
    }

    public int a(String s) {
        Collection<CustomFunctionCallbackTimerQueue.a<T>> collection = this.e.row(s).values();
        Queue queue = this.c;

        this.c.getClass();
        collection.forEach(queue::remove);
        int i = collection.size();

        collection.clear();
        return i;
    }

    public Set<String> a() {
        return Collections.unmodifiableSet(this.e.rowKeySet());
    }

    private void a(NBTTagCompound nbttagcompound) {
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Callback");
        CustomFunctionCallbackTimer<T> customfunctioncallbacktimer = this.b.a(nbttagcompound1);

        if (customfunctioncallbacktimer != null) {
            String s = nbttagcompound.getString("Name");
            long i = nbttagcompound.getLong("TriggerTime");

            this.a(s, i, customfunctioncallbacktimer);
        }

    }

    public void a(NBTTagList nbttaglist) {
        this.c.clear();
        this.e.clear();
        this.d = UnsignedLong.ZERO;
        if (!nbttaglist.isEmpty()) {
            if (nbttaglist.a_() != 10) {
                CustomFunctionCallbackTimerQueue.LOGGER.warn("Invalid format of events: " + nbttaglist);
            } else {
                Iterator iterator = nbttaglist.iterator();

                while (iterator.hasNext()) {
                    NBTBase nbtbase = (NBTBase) iterator.next();

                    this.a((NBTTagCompound) nbtbase);
                }

            }
        }
    }

    private NBTTagCompound a(CustomFunctionCallbackTimerQueue.a<T> customfunctioncallbacktimerqueue_a) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.setString("Name", customfunctioncallbacktimerqueue_a.c);
        nbttagcompound.setLong("TriggerTime", customfunctioncallbacktimerqueue_a.a);
        nbttagcompound.set("Callback", this.b.a(customfunctioncallbacktimerqueue_a.d));
        return nbttagcompound;
    }

    public NBTTagList b() {
        NBTTagList nbttaglist = new NBTTagList();

        this.c.stream().sorted(c()).map(this::a).forEach(nbttaglist::add);
        return nbttaglist;
    }

    public static class a<T> {

        public final long a;
        public final UnsignedLong b;
        public final String c;
        public final CustomFunctionCallbackTimer<T> d;

        private a(long i, UnsignedLong unsignedlong, String s, CustomFunctionCallbackTimer<T> customfunctioncallbacktimer) {
            this.a = i;
            this.b = unsignedlong;
            this.c = s;
            this.d = customfunctioncallbacktimer;
        }
    }
}
