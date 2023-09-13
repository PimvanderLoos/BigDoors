package net.minecraft.world.level.entity;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.util.AbortableIterationConsumer;
import org.slf4j.Logger;

public class EntityLookup<T extends EntityAccess> {

    private static final Logger LOGGER = LogUtils.getLogger();
    private final Int2ObjectMap<T> byId = new Int2ObjectLinkedOpenHashMap();
    private final Map<UUID, T> byUuid = Maps.newHashMap();

    public EntityLookup() {}

    public <U extends T> void getEntities(EntityTypeTest<T, U> entitytypetest, AbortableIterationConsumer<U> abortableiterationconsumer) {
        ObjectIterator objectiterator = this.byId.values().iterator();

        EntityAccess entityaccess;

        do {
            if (!objectiterator.hasNext()) {
                return;
            }

            T t0 = (EntityAccess) objectiterator.next();

            entityaccess = (EntityAccess) entitytypetest.tryCast(t0);
        } while (entityaccess == null || !abortableiterationconsumer.accept(entityaccess).shouldAbort());

    }

    public Iterable<T> getAllEntities() {
        return Iterables.unmodifiableIterable(this.byId.values());
    }

    public void add(T t0) {
        UUID uuid = t0.getUUID();

        if (this.byUuid.containsKey(uuid)) {
            EntityLookup.LOGGER.warn("Duplicate entity UUID {}: {}", uuid, t0);
        } else {
            this.byUuid.put(uuid, t0);
            this.byId.put(t0.getId(), t0);
        }
    }

    public void remove(T t0) {
        this.byUuid.remove(t0.getUUID());
        this.byId.remove(t0.getId());
    }

    @Nullable
    public T getEntity(int i) {
        return (EntityAccess) this.byId.get(i);
    }

    @Nullable
    public T getEntity(UUID uuid) {
        return (EntityAccess) this.byUuid.get(uuid);
    }

    public int count() {
        return this.byUuid.size();
    }
}
