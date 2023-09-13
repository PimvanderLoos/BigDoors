package net.minecraft.util.thread;

import com.mojang.datafixers.util.Either;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Mailbox<Msg> extends AutoCloseable {

    String bn();

    void a(Msg msg);

    default void close() {}

    default <Source> CompletableFuture<Source> b(Function<? super Mailbox<Source>, ? extends Msg> function) {
        CompletableFuture<Source> completablefuture = new CompletableFuture();

        Objects.requireNonNull(completablefuture);
        Msg msg = function.apply(a("ask future procesor handle", completablefuture::complete));

        this.a(msg);
        return completablefuture;
    }

    default <Source> CompletableFuture<Source> c(Function<? super Mailbox<Either<Source, Exception>>, ? extends Msg> function) {
        CompletableFuture<Source> completablefuture = new CompletableFuture();
        Msg msg = function.apply(a("ask future procesor handle", (either) -> {
            Objects.requireNonNull(completablefuture);
            either.ifLeft(completablefuture::complete);
            Objects.requireNonNull(completablefuture);
            either.ifRight(completablefuture::completeExceptionally);
        }));

        this.a(msg);
        return completablefuture;
    }

    static <Msg> Mailbox<Msg> a(final String s, final Consumer<Msg> consumer) {
        return new Mailbox<Msg>() {
            @Override
            public String bn() {
                return s;
            }

            @Override
            public void a(Msg msg) {
                consumer.accept(msg);
            }

            public String toString() {
                return s;
            }
        };
    }
}
