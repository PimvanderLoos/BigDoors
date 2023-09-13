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
    public T a(int i) {
        return this.visibleEntities.a(i);
    }

    @Nullable
    @Override
    public T a(UUID uuid) {
        return this.visibleEntities.a(uuid);
    }

    @Override
    public Iterable<T> a() {
        return this.visibleEntities.a();
    }

    @Override
    public <U extends T> void a(EntityTypeTest<T, U> entitytypetest, Consumer<U> consumer) {
        this.visibleEntities.a(entitytypetest, consumer);
    }

    @Override
    public void a(AxisAlignedBB axisalignedbb, Consumer<T> consumer) {
        this.sectionStorage.b(axisalignedbb, consumer);
    }

    @Override
    public <U extends T> void a(EntityTypeTest<T, U> entitytypetest, AxisAlignedBB axisalignedbb, Consumer<U> consumer) {
        this.sectionStorage.a(entitytypetest, axisalignedbb, consumer);
    }
}
