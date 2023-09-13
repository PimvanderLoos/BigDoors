package net.minecraft.world.level.entity;

import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.world.phys.AxisAlignedBB;

public interface LevelEntityGetter<T extends EntityAccess> {

    @Nullable
    T a(int i);

    @Nullable
    T a(UUID uuid);

    Iterable<T> a();

    <U extends T> void a(EntityTypeTest<T, U> entitytypetest, Consumer<U> consumer);

    void a(AxisAlignedBB axisalignedbb, Consumer<T> consumer);

    <U extends T> void a(EntityTypeTest<T, U> entitytypetest, AxisAlignedBB axisalignedbb, Consumer<U> consumer);
}
