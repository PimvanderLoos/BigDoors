package net.minecraft.world;

import javax.annotation.Nullable;

public interface Clearable {

    void clear();

    static void a(@Nullable Object object) {
        if (object instanceof Clearable) {
            ((Clearable) object).clear();
        }

    }
}
