package net.minecraft.util.thread;

import com.mojang.datafixers.util.Either;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Mailbox<Msg> extends AutoCloseable {

    String name();

    void tell(Msg msg);

    default void close() {}

    default <Source> CompletableFuture<Source> ask(Function<? super Mailbox<Source>, ? extends Msg> function) {
        CompletableFuture<Source> completablefuture = new CompletableFuture();

        Objects.requireNonNull(completablefuture);
        Msg msg = function.apply(of("ask future procesor handle", completablefuture::complete));

        this.tell(msg);
        return completablefuture;
    }

    default <Source> CompletableFuture<Source> askEither(Function<? super Mailbox<Either<Source, Exception>>, ? extends Msg> function) {
        CompletableFuture<Source> completablefuture = new CompletableFuture();
        Msg msg = function.apply(of("ask future procesor handle", (either) -> {
            Objects.requireNonNull(completablefuture);
            either.ifLeft(completablefuture::complete);
            Objects.requireNonNull(completablefuture);
            either.ifRight(completablefuture::completeExceptionally);
        }));

        this.tell(msg);
        return completablefuture;
    }

    static <Msg> Mailbox<Msg> of(final String s, final Consumer<Msg> consumer) {
        return new Mailbox<Msg>() {
            @Override
            public String name() {
                return s;
            }

            @Override
            public void tell(Msg msg) {
                consumer.accept(msg);
            }

            public String toString() {
                return s;
            }
        };
    }
}
