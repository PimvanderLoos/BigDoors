package net.minecraft.world.level.entity;

import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.util.EntitySlice;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.phys.AxisAlignedBB;
import org.slf4j.Logger;

public class EntitySection<T extends EntityAccess> {

    private static final Logger LOGGER = LogUtils.getLogger();
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

    public AbortableIterationConsumer.a getEntities(AxisAlignedBB axisalignedbb, AbortableIterationConsumer<T> abortableiterationconsumer) {
        Iterator iterator = this.storage.iterator();

        EntityAccess entityaccess;

        do {
            if (!iterator.hasNext()) {
                return AbortableIterationConsumer.a.CONTINUE;
            }

            entityaccess = (EntityAccess) iterator.next();
        } while (!entityaccess.getBoundingBox().intersects(axisalignedbb) || !abortableiterationconsumer.accept(entityaccess).shouldAbort());

        return AbortableIterationConsumer.a.ABORT;
    }

    public <U extends T> AbortableIterationConsumer.a getEntities(EntityTypeTest<T, U> entitytypetest, AxisAlignedBB axisalignedbb, AbortableIterationConsumer<? super U> abortableiterationconsumer) {
        Collection<? extends T> collection = this.storage.find(entitytypetest.getBaseClass());

        if (collection.isEmpty()) {
            return AbortableIterationConsumer.a.CONTINUE;
        } else {
            Iterator iterator = collection.iterator();

            EntityAccess entityaccess;
            EntityAccess entityaccess1;

            do {
                if (!iterator.hasNext()) {
                    return AbortableIterationConsumer.a.CONTINUE;
                }

                entityaccess = (EntityAccess) iterator.next();
                entityaccess1 = (EntityAccess) entitytypetest.tryCast(entityaccess);
            } while (entityaccess1 == null || !entityaccess.getBoundingBox().intersects(axisalignedbb) || !abortableiterationconsumer.accept(entityaccess1).shouldAbort());

            return AbortableIterationConsumer.a.ABORT;
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
