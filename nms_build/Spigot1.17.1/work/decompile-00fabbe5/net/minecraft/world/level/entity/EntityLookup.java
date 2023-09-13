package net.minecraft.world.level.entity;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityLookup<T extends EntityAccess> {

    private static final Logger LOGGER = LogManager.getLogger();
    private final Int2ObjectMap<T> byId = new Int2ObjectLinkedOpenHashMap();
    private final Map<UUID, T> byUuid = Maps.newHashMap();

    public EntityLookup() {}

    public <U extends T> void a(EntityTypeTest<T, U> entitytypetest, Consumer<U> consumer) {
        ObjectIterator objectiterator = this.byId.values().iterator();

        while (objectiterator.hasNext()) {
            T t0 = (EntityAccess) objectiterator.next();
            U u0 = (EntityAccess) entitytypetest.a((Object) t0);

            if (u0 != null) {
                consumer.accept(u0);
            }
        }

    }

    public Iterable<T> a() {
        return Iterables.unmodifiableIterable(this.byId.values());
    }

    public void a(T t0) {
        UUID uuid = t0.getUniqueID();

        if (this.byUuid.containsKey(uuid)) {
            EntityLookup.LOGGER.warn("Duplicate entity UUID {}: {}", uuid, t0);
        } else {
            this.byUuid.put(uuid, t0);
            this.byId.put(t0.getId(), t0);
        }
    }

    public void b(T t0) {
        this.byUuid.remove(t0.getUniqueID());
        this.byId.remove(t0.getId());
    }

    @Nullable
    public T a(int i) {
        return (EntityAccess) this.byId.get(i);
    }

    @Nullable
    public T a(UUID uuid) {
        return (EntityAccess) this.byUuid.get(uuid);
    }

    public int b() {
        return this.byUuid.size();
    }
}
