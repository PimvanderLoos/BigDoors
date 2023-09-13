package net.minecraft.world.level.entity;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.util.EntitySlice;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.phys.AxisAlignedBB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntitySection<T extends EntityAccess> {

    protected static final Logger LOGGER = LogManager.getLogger();
    private final EntitySlice<T> storage;
    private Visibility chunkStatus;

    public EntitySection(Class<T> oclass, Visibility visibility) {
        this.chunkStatus = visibility;
        this.storage = new EntitySlice<>(oclass);
    }

    public void add(T t0) {
        this.storage.add(t0);
    }

    public boolean remove(T t0) {
        return this.storage.remove(t0);
    }

    public void getEntities(AxisAlignedBB axisalignedbb, Consumer<T> consumer) {
        Iterator iterator = this.storage.iterator();

        while (iterator.hasNext()) {
            T t0 = (EntityAccess) iterator.next();

            if (t0.getBoundingBox().intersects(axisalignedbb)) {
                consumer.accept(t0);
            }
        }

    }

    public <U extends T> void getEntities(EntityTypeTest<T, U> entitytypetest, AxisAlignedBB axisalignedbb, Consumer<? super U> consumer) {
        Collection<? extends T> collection = this.storage.find(entitytypetest.getBaseClass());

        if (!collection.isEmpty()) {
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                T t0 = (EntityAccess) iterator.next();
                U u0 = (EntityAccess) entitytypetest.tryCast(t0);

                if (u0 != null && t0.getBoundingBox().intersects(axisalignedbb)) {
                    consumer.accept(u0);
                }
            }

        }
    }

    public boolean isEmpty() {
        return this.storage.isEmpty();
    }

    public Stream<T> getEntities() {
        return this.storage.stream();
    }

    public Visibility getStatus() {
        return this.chunkStatus;
    }

    public Visibility updateChunkStatus(Visibility visibility) {
        Visibility visibility1 = this.chunkStatus;

        this.chunkStatus = visibility;
        return visibility1;
    }

    @VisibleForDebug
    public int size() {
        return this.storage.size();
    }
}
