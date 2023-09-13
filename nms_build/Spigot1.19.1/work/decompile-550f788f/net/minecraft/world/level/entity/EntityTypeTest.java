package net.minecraft.world.level.entity;

import javax.annotation.Nullable;

public interface EntityTypeTest<B, T extends B> {

    static <B, T extends B> EntityTypeTest<B, T> forClass(final Class<T> oclass) {
        return new EntityTypeTest<B, T>() {
            @Nullable
            @Override
            public T tryCast(B b0) {
                return oclass.isInstance(b0) ? b0 : null;
            }

            @Override
            public Class<? extends B> getBaseClass() {
                return oclass;
            }
        };
    }

    @Nullable
    T tryCast(B b0);

    Class<? extends B> getBaseClass();
}
