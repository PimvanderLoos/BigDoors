package net.minecraft.util;

import javax.annotation.Nullable;

public class ExceptionSuppressor<T extends Throwable> {

    @Nullable
    private T result;

    public ExceptionSuppressor() {}

    public void a(T t0) {
        if (this.result == null) {
            this.result = t0;
        } else {
            this.result.addSuppressed(t0);
        }

    }

    public void a() throws T {
        if (this.result != null) {
            throw this.result;
        }
    }
}
