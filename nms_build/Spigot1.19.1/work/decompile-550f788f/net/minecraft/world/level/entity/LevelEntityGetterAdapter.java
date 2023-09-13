package net.minecraft.world.level.entity;

import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.world.phys.AxisAlignedBB;

public class LevelEntityGetterAdapter<T extends EntityAccess> implements LevelEntityGetter<T> {

    private final EntityLookup<T> visibleEntities;
    private final EntitySectionStorage<T> sectionStorage;

    public LevelEntityGetterAdapter(EntityLookup<T> entitylookup, EntitySectionStorage<T> entitysectionstorage) {
        this.visibleEntities = entitylookup;
        this.sectionStorage = entitysectionstorage;
    }

    @Nullable
    @Override
    public T get(int i) {
        return this.visibleEntities.getEntity(i);
    }

    @Nullable
    @Override
    public T get(UUID uuid) {
        return this.visibleEntities.getEntity(uuid);
    }

    @Override
    public Iterable<T> getAll() {
        return this.visibleEntities.getAllEntities();
    }

    @Override
    public <U extends T> void get(EntityTypeTest<T, U> entitytypetest, Consumer<U> consumer) {
        this.visibleEntities.getEntities(entitytypetest, consumer);
    }

    @Override
    public void get(AxisAlignedBB axisalignedbb, Consumer<T> consumer) {
        this.sectionStorage.getEntities(axisalignedbb, consumer);
    }

    @Override
    public <U extends T> void get(EntityTypeTest<T, U> entitytypetest, AxisAlignedBB axisalignedbb, Consumer<U> consumer) {
        this.sectionStorage.getEntities(entitytypetest, axisalignedbb, consumer);
    }
}
