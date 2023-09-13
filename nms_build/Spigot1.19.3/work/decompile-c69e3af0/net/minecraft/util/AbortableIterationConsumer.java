package net.minecraft.util;

import java.util.function.Consumer;

@FunctionalInterface
public interface AbortableIterationConsumer<T> {

    AbortableIterationConsumer.a accept(T t0);

    static <T> AbortableIterationConsumer<T> forConsumer(Consumer<T> consumer) {
        return (object) -> {
            consumer.accept(object);
            return AbortableIterationConsumer.a.CONTINUE;
        };
    }

    public static enum a {

        CONTINUE, ABORT;

        private a() {}

        public boolean shouldAbort() {
            return this == AbortableIterationConsumer.a.ABORT;
        }
    }
}
