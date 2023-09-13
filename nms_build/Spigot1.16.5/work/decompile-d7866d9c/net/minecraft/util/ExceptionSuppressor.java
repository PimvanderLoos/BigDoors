package net.minecraft.util;

import javax.annotation.Nullable;

public class ExceptionSuppressor<T extends Throwable> {

    @Nullable
    private T a;

    public ExceptionSuppressor() {}

    public void a(T t0) {
        if (this.a == null) {
            this.a = t0;
        } else {
            this.a.addSuppressed(t0);
        }

    }

    public void a() throws T {
        if (this.a != null) {
            throw this.a;
        }
    }
}
