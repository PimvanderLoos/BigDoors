package net.minecraft.world.level.entity;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.EntitySlice;
import net.minecraft.util.VisibleForDebug;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntitySection<T> {

    protected static final Logger LOGGER = LogManager.getLogger();
    private final EntitySlice<T> storage;
    private Visibility chunkStatus;

    public EntitySection(Class<T> oclass, Visibility visibility) {
        this.chunkStatus = visibility;
        this.storage = new EntitySlice<>(oclass);
    }

    public void a(T t0) {
        this.storage.add(t0);
    }

    public boolean b(T t0) {
        return this.storage.remove(t0);
    }

    public void a(Predicate<? super T> predicate, Consumer<T> consumer) {
        Iterator iterator = this.storage.iterator();

        while (iterator.hasNext()) {
            T t0 = iterator.next();

            if (predicate.test(t0)) {
                consumer.accept(t0);
            }
        }

    }

    public <U extends T> void a(EntityTypeTest<T, U> entitytypetest, Predicate<? super U> predicate, Consumer<? super U> consumer) {
        Iterator iterator = this.storage.a(entitytypetest.a()).iterator();

        while (iterator.hasNext()) {
            T t0 = iterator.next();
            U u0 = entitytypetest.a(t0);

            if (u0 != null && predicate.test(u0)) {
                consumer.accept(u0);
            }
        }

    }

    public boolean a() {
        return this.storage.isEmpty();
    }

    public Stream<T> b() {
        return this.storage.stream();
    }

    public Visibility c() {
        return this.chunkStatus;
    }

    public Visibility a(Visibility visibility) {
        Visibility visibility1 = this.chunkStatus;

        this.chunkStatus = visibility;
        return visibility1;
    }

    @VisibleForDebug
    public int d() {
        return this.storage.size();
    }
}
