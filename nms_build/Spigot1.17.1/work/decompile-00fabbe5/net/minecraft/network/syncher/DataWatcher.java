package net.minecraft.network.syncher;

import com.google.common.collect.Lists;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportSystemDetails;
import net.minecraft.ReportedException;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataWatcher {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Object2IntMap<Class<? extends Entity>> ENTITY_ID_POOL = new Object2IntOpenHashMap();
    private static final int EOF_MARKER = 255;
    private static final int MAX_ID_VALUE = 254;
    private final Entity entity;
    private final Int2ObjectMap<DataWatcher.Item<?>> itemsById = new Int2ObjectOpenHashMap();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private boolean isEmpty = true;
    private boolean isDirty;

    public DataWatcher(Entity entity) {
        this.entity = entity;
    }

    public static <T> DataWatcherObject<T> a(Class<? extends Entity> oclass, DataWatcherSerializer<T> datawatcherserializer) {
        if (DataWatcher.LOGGER.isDebugEnabled()) {
            try {
                Class<?> oclass1 = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());

                if (!oclass1.equals(oclass)) {
                    DataWatcher.LOGGER.debug("defineId called for: {} from {}", oclass, oclass1, new RuntimeException());
                }
            } catch (ClassNotFoundException classnotfoundexception) {
                ;
            }
        }

        int i;

        if (DataWatcher.ENTITY_ID_POOL.containsKey(oclass)) {
            i = DataWatcher.ENTITY_ID_POOL.getInt(oclass) + 1;
        } else {
            int j = 0;
            Class oclass2 = oclass;

            while (oclass2 != Entity.class) {
                oclass2 = oclass2.getSuperclass();
                if (DataWatcher.ENTITY_ID_POOL.containsKey(oclass2)) {
                    j = DataWatcher.ENTITY_ID_POOL.getInt(oclass2) + 1;
                    break;
                }
            }

            i = j;
        }

        if (i > 254) {
            throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is 254)");
        } else {
            DataWatcher.ENTITY_ID_POOL.put(oclass, i);
            return datawatcherserializer.a(i);
        }
    }

    public <T> void register(DataWatcherObject<T> datawatcherobject, T t0) {
        int i = datawatcherobject.a();

        if (i > 254) {
            throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is 254)");
        } else if (this.itemsById.containsKey(i)) {
            throw new IllegalArgumentException("Duplicate id value for " + i + "!");
        } else if (DataWatcherRegistry.b(datawatcherobject.b()) < 0) {
            DataWatcherSerializer datawatcherserializer = datawatcherobject.b();

            throw new IllegalArgumentException("Unregistered serializer " + datawatcherserializer + " for " + i + "!");
        } else {
            this.registerObject(datawatcherobject, t0);
        }
    }

    private <T> void registerObject(DataWatcherObject<T> datawatcherobject, T t0) {
        DataWatcher.Item<T> datawatcher_item = new DataWatcher.Item<>(datawatcherobject, t0);

        this.lock.writeLock().lock();
        this.itemsById.put(datawatcherobject.a(), datawatcher_item);
        this.isEmpty = false;
        this.lock.writeLock().unlock();
    }

    private <T> DataWatcher.Item<T> b(DataWatcherObject<T> datawatcherobject) {
        this.lock.readLock().lock();

        DataWatcher.Item datawatcher_item;

        try {
            datawatcher_item = (DataWatcher.Item) this.itemsById.get(datawatcherobject.a());
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Getting synched entity data");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Synched entity data");

            crashreportsystemdetails.a("Data ID", (Object) datawatcherobject);
            throw new ReportedException(crashreport);
        } finally {
            this.lock.readLock().unlock();
        }

        return datawatcher_item;
    }

    public <T> T get(DataWatcherObject<T> datawatcherobject) {
        return this.b(datawatcherobject).b();
    }

    public <T> void set(DataWatcherObject<T> datawatcherobject, T t0) {
        DataWatcher.Item<T> datawatcher_item = this.b(datawatcherobject);

        if (ObjectUtils.notEqual(t0, datawatcher_item.b())) {
            datawatcher_item.a(t0);
            this.entity.a(datawatcherobject);
            datawatcher_item.a(true);
            this.isDirty = true;
        }

    }

    public boolean a() {
        return this.isDirty;
    }

    public static void a(@Nullable List<DataWatcher.Item<?>> list, PacketDataSerializer packetdataserializer) {
        if (list != null) {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                DataWatcher.Item<?> datawatcher_item = (DataWatcher.Item) iterator.next();

                a(packetdataserializer, datawatcher_item);
            }
        }

        packetdataserializer.writeByte(255);
    }

    @Nullable
    public List<DataWatcher.Item<?>> b() {
        List<DataWatcher.Item<?>> list = null;

        if (this.isDirty) {
            this.lock.readLock().lock();
            ObjectIterator objectiterator = this.itemsById.values().iterator();

            while (objectiterator.hasNext()) {
                DataWatcher.Item<?> datawatcher_item = (DataWatcher.Item) objectiterator.next();

                if (datawatcher_item.c()) {
                    datawatcher_item.a(false);
                    if (list == null) {
                        list = Lists.newArrayList();
                    }

                    list.add(datawatcher_item.d());
                }
            }

            this.lock.readLock().unlock();
        }

        this.isDirty = false;
        return list;
    }

    @Nullable
    public List<DataWatcher.Item<?>> getAll() {
        List<DataWatcher.Item<?>> list = null;

        this.lock.readLock().lock();

        DataWatcher.Item datawatcher_item;

        for (ObjectIterator objectiterator = this.itemsById.values().iterator(); objectiterator.hasNext(); list.add(datawatcher_item.d())) {
            datawatcher_item = (DataWatcher.Item) objectiterator.next();
            if (list == null) {
                list = Lists.newArrayList();
            }
        }

        this.lock.readLock().unlock();
        return list;
    }

    private static <T> void a(PacketDataSerializer packetdataserializer, DataWatcher.Item<T> datawatcher_item) {
        DataWatcherObject<T> datawatcherobject = datawatcher_item.a();
        int i = DataWatcherRegistry.b(datawatcherobject.b());

        if (i < 0) {
            throw new EncoderException("Unknown serializer type " + datawatcherobject.b());
        } else {
            packetdataserializer.writeByte(datawatcherobject.a());
            packetdataserializer.d(i);
            datawatcherobject.b().a(packetdataserializer, datawatcher_item.b());
        }
    }

    @Nullable
    public static List<DataWatcher.Item<?>> a(PacketDataSerializer packetdataserializer) {
        ArrayList arraylist = null;

        short short0;

        while ((short0 = packetdataserializer.readUnsignedByte()) != 255) {
            if (arraylist == null) {
                arraylist = Lists.newArrayList();
            }

            int i = packetdataserializer.j();
            DataWatcherSerializer<?> datawatcherserializer = DataWatcherRegistry.a(i);

            if (datawatcherserializer == null) {
                throw new DecoderException("Unknown serializer type " + i);
            }

            arraylist.add(a(packetdataserializer, short0, datawatcherserializer));
        }

        return arraylist;
    }

    private static <T> DataWatcher.Item<T> a(PacketDataSerializer packetdataserializer, int i, DataWatcherSerializer<T> datawatcherserializer) {
        return new DataWatcher.Item<>(datawatcherserializer.a(i), datawatcherserializer.a(packetdataserializer));
    }

    public void a(List<DataWatcher.Item<?>> list) {
        this.lock.writeLock().lock();

        try {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                DataWatcher.Item<?> datawatcher_item = (DataWatcher.Item) iterator.next();
                DataWatcher.Item<?> datawatcher_item1 = (DataWatcher.Item) this.itemsById.get(datawatcher_item.a().a());

                if (datawatcher_item1 != null) {
                    this.a(datawatcher_item1, datawatcher_item);
                    this.entity.a(datawatcher_item.a());
                }
            }
        } finally {
            this.lock.writeLock().unlock();
        }

        this.isDirty = true;
    }

    private <T> void a(DataWatcher.Item<T> datawatcher_item, DataWatcher.Item<?> datawatcher_item1) {
        if (!Objects.equals(datawatcher_item1.accessor.b(), datawatcher_item.accessor.b())) {
            throw new IllegalStateException(String.format("Invalid entity data item type for field %d on entity %s: old=%s(%s), new=%s(%s)", datawatcher_item.accessor.a(), this.entity, datawatcher_item.value, datawatcher_item.value.getClass(), datawatcher_item1.value, datawatcher_item1.value.getClass()));
        } else {
            datawatcher_item.a(datawatcher_item1.b());
        }
    }

    public boolean d() {
        return this.isEmpty;
    }

    public void e() {
        this.isDirty = false;
        this.lock.readLock().lock();
        ObjectIterator objectiterator = this.itemsById.values().iterator();

        while (objectiterator.hasNext()) {
            DataWatcher.Item<?> datawatcher_item = (DataWatcher.Item) objectiterator.next();

            datawatcher_item.a(false);
        }

        this.lock.readLock().unlock();
    }

    public static class Item<T> {

        final DataWatcherObject<T> accessor;
        T value;
        private boolean dirty;

        public Item(DataWatcherObject<T> datawatcherobject, T t0) {
            this.accessor = datawatcherobject;
            this.value = t0;
            this.dirty = true;
        }

        public DataWatcherObject<T> a() {
            return this.accessor;
        }

        public void a(T t0) {
            this.value = t0;
        }

        public T b() {
            return this.value;
        }

        public boolean c() {
            return this.dirty;
        }

        public void a(boolean flag) {
            this.dirty = flag;
        }

        public DataWatcher.Item<T> d() {
            return new DataWatcher.Item<>(this.accessor, this.accessor.b().a(this.value));
        }
    }
}
