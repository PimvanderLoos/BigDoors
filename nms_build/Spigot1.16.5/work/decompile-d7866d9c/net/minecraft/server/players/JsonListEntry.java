package net.minecraft.server.players;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public abstract class JsonListEntry<T> {

    @Nullable
    private final T a;

    public JsonListEntry(@Nullable T t0) {
        this.a = t0;
    }

    @Nullable
    public T getKey() {
        return this.a;
    }

    boolean hasExpired() {
        return false;
    }

    protected abstract void a(JsonObject jsonobject);
}
