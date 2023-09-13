package net.minecraft.world.level.entity;

import javax.annotation.Nullable;

public interface EntityTypeTest<B, T extends B> {

    static <B, T extends B> EntityTypeTest<B, T> a(final Class<T> oclass) {
        return new EntityTypeTest<B, T>() {
            @Nullable
            @Override
            public T a(B b0) {
                return oclass.isInstance(b0) ? b0 : null;
            }

            @Override
            public Class<? extends B> a() {
                return oclass;
            }
        };
    }

    @Nullable
    T a(B b0);

    Class<? extends B> a();
}
