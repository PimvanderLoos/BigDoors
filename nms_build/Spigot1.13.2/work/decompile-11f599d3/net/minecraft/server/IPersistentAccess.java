package net.minecraft.server;

import java.util.function.Function;
import javax.annotation.Nullable;

public interface IPersistentAccess {

    @Nullable
    PersistentCollection h();

    @Nullable
    default <T extends PersistentBase> T a(DimensionManager dimensionmanager, Function<String, T> function, String s) {
        PersistentCollection persistentcollection = this.h();

        return persistentcollection == null ? null : persistentcollection.get(dimensionmanager, function, s);
    }

    default void a(DimensionManager dimensionmanager, String s, PersistentBase persistentbase) {
        PersistentCollection persistentcollection = this.h();

        if (persistentcollection != null) {
            persistentcollection.a(dimensionmanager, s, persistentbase);
        }

    }

    default int a(DimensionManager dimensionmanager, String s) {
        return this.h().a(dimensionmanager, s);
    }
}
