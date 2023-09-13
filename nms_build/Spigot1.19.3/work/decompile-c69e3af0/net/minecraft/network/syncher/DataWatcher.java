package net.minecraft.network.syncher;

import com.mojang.logging.LogUtils;
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
import java.util.Locale;
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
import org.slf4j.Logger;

public class DataWatcher {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Object2IntMap<Class<? extends Entity>> ENTITY_ID_POOL = new Object2IntOpenHashMap();
    private static final int MAX_ID_VALUE = 254;
    private final Entity entity;
    private final Int2ObjectMap<DataWatcher.Item<?>> itemsById = new Int2ObjectOpenHashMap();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private boolean isDirty;

    public DataWatcher(Entity entity) {
        this.entity = entity;
    }

    public static <T> DataWatcherObject<T> defineId(Class<? extends Entity> oclass, DataWatcherSerializer<T> datawatcherserializer) {
        if (DataWatcher.LOGGER.isDebugEnabled()) {
            try {
                Class<?> oclass1 = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());

                if (!oclass1.equals(oclass)) {
                    DataWatcher.LOGGER.debug("defineId called for: {} from {}", new Object[]{oclass, oclass1, new RuntimeException()});
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
            return datawatcherserializer.createAccessor(i);
        }
    }

    public <T> void define(DataWatcherObject<T> datawatcherobject, T t0) {
        int i = datawatcherobject.getId();

        if (i > 254) {
            throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is 254)");
        } else if (this.itemsById.containsKey(i)) {
            throw new IllegalArgumentException("Duplicate id value for " + i + "!");
        } else if (DataWatcherRegistry.getSerializedId(datawatcherobject.getSerializer()) < 0) {
            DataWatcherSerializer datawatcherserializer = datawatcherobject.getSerializer();

            throw new IllegalArgumentException("Unregistered serializer " + datawatcherserializer + " for " + i + "!");
        } else {
            this.createDataItem(datawatcherobject, t0);
        }
    }

    private <T> void createDataItem(DataWatcherObject<T> datawatcherobject, T t0) {
        DataWatcher.Item<T> datawatcher_item = new DataWatcher.Item<>(datawatcherobject, t0);

        this.lock.writeLock().lock();
        this.itemsById.put(datawatcherobject.getId(), datawatcher_item);
        this.lock.writeLock().unlock();
    }

    private <T> DataWatcher.Item<T> getItem(DataWatcherObject<T> datawatcherobject) {
        this.lock.readLock().lock();

        DataWatcher.Item datawatcher_item;

        try {
            datawatcher_item = (DataWatcher.Item) this.itemsById.get(datawatcherobject.getId());
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Getting synched entity data");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.addCategory("Synched entity data");

            crashreportsystemdetails.setDetail("Data ID", (Object) datawatcherobject);
            throw new ReportedException(crashreport);
        } finally {
            this.lock.readLock().unlock();
        }

        return datawatcher_item;
    }

    public <T> T get(DataWatcherObject<T> datawatcherobject) {
        return this.getItem(datawatcherobject).getValue();
    }

    public <T> void set(DataWatcherObject<T> datawatcherobject, T t0) {
        DataWatcher.Item<T> datawatcher_item = this.getItem(datawatcherobject);

        if (ObjectUtils.notEqual(t0, datawatcher_item.getValue())) {
            datawatcher_item.setValue(t0);
            this.entity.onSyncedDataUpdated(datawatcherobject);
            datawatcher_item.setDirty(true);
            this.isDirty = true;
        }

    }

    public boolean isDirty() {
        return this.isDirty;
    }

    @Nullable
    public List<DataWatcher.b<?>> packDirty() {
        List<DataWatcher.b<?>> list = null;

        if (this.isDirty) {
            this.lock.readLock().lock();
            ObjectIterator objectiterator = this.itemsById.values().iterator();

            while (objectiterator.hasNext()) {
                DataWatcher.Item<?> datawatcher_item = (DataWatcher.Item) objectiterator.next();

                if (datawatcher_item.isDirty()) {
                    datawatcher_item.setDirty(false);
                    if (list == null) {
                        list = new ArrayList();
                    }

                    list.add(datawatcher_item.value());
                }
            }

            this.lock.readLock().unlock();
        }

        this.isDirty = false;
        return list;
    }

    @Nullable
    public List<DataWatcher.b<?>> getNonDefaultValues() {
        List<DataWatcher.b<?>> list = null;

        this.lock.readLock().lock();
        ObjectIterator objectiterator = this.itemsById.values().iterator();

        while (objectiterator.hasNext()) {
            DataWatcher.Item<?> datawatcher_item = (DataWatcher.Item) objectiterator.next();

            if (!datawatcher_item.isSetToDefault()) {
                if (list == null) {
                    list = new ArrayList();
                }

                list.add(datawatcher_item.value());
            }
        }

        this.lock.readLock().unlock();
        return list;
    }

    public void assignValues(List<DataWatcher.b<?>> list) {
        this.lock.writeLock().lock();

        try {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                DataWatcher.b<?> datawatcher_b = (DataWatcher.b) iterator.next();
                DataWatcher.Item<?> datawatcher_item = (DataWatcher.Item) this.itemsById.get(datawatcher_b.id);

                if (datawatcher_item != null) {
                    this.assignValue(datawatcher_item, datawatcher_b);
                    this.entity.onSyncedDataUpdated(datawatcher_item.getAccessor());
                }
            }
        } finally {
            this.lock.writeLock().unlock();
        }

    }

    private <T> void assignValue(DataWatcher.Item<T> datawatcher_item, DataWatcher.b<?> datawatcher_b) {
        if (!Objects.equals(datawatcher_b.serializer(), datawatcher_item.accessor.getSerializer())) {
            throw new IllegalStateException(String.format(Locale.ROOT, "Invalid entity data item type for field %d on entity %s: old=%s(%s), new=%s(%s)", datawatcher_item.accessor.getId(), this.entity, datawatcher_item.value, datawatcher_item.value.getClass(), datawatcher_b.value, datawatcher_b.value.getClass()));
        } else {
            datawatcher_item.setValue(datawatcher_b.value);
        }
    }

    public boolean isEmpty() {
        return this.itemsById.isEmpty();
    }

    public static class Item<T> {

        final DataWatcherObject<T> accessor;
        T value;
        private final T initialValue;
        private boolean dirty;

        public Item(DataWatcherObject<T> datawatcherobject, T t0) {
            this.accessor = datawatcherobject;
            this.initialValue = t0;
            this.value = t0;
        }

        public DataWatcherObject<T> getAccessor() {
            return this.accessor;
        }

        public void setValue(T t0) {
            this.value = t0;
        }

        public T getValue() {
            return this.value;
        }

        public boolean isDirty() {
            return this.dirty;
        }

        public void setDirty(boolean flag) {
            this.dirty = flag;
        }

        public boolean isSetToDefault() {
            return this.initialValue.equals(this.value);
        }

        public DataWatcher.b<T> value() {
            return DataWatcher.b.create(this.accessor, this.value);
        }
    }

    public static record b<T> (int id, DataWatcherSerializer<T> serializer, T value) {

        public static <T> DataWatcher.b<T> create(DataWatcherObject<T> datawatcherobject, T t0) {
            DataWatcherSerializer<T> datawatcherserializer = datawatcherobject.getSerializer();

            return new DataWatcher.b<>(datawatcherobject.getId(), datawatcherserializer, datawatcherserializer.copy(t0));
        }

        public void write(PacketDataSerializer packetdataserializer) {
            int i = DataWatcherRegistry.getSerializedId(this.serializer);

            if (i < 0) {
                throw new EncoderException("Unknown serializer type " + this.serializer);
            } else {
                packetdataserializer.writeByte(this.id);
                packetdataserializer.writeVarInt(i);
                this.serializer.write(packetdataserializer, this.value);
            }
        }

        public static DataWatcher.b<?> read(PacketDataSerializer packetdataserializer, int i) {
            int j = packetdataserializer.readVarInt();
            DataWatcherSerializer<?> datawatcherserializer = DataWatcherRegistry.getSerializer(j);

            if (datawatcherserializer == null) {
                throw new DecoderException("Unknown serializer type " + j);
            } else {
                return read(packetdataserializer, i, datawatcherserializer);
            }
        }

        private static <T> DataWatcher.b<T> read(PacketDataSerializer packetdataserializer, int i, DataWatcherSerializer<T> datawatcherserializer) {
            return new DataWatcher.b<>(i, datawatcherserializer, datawatcherserializer.read(packetdataserializer));
        }
    }
}
