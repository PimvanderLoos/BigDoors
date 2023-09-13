package net.minecraft.util;

import com.mojang.logging.LogUtils;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import org.slf4j.Logger;

public class FutureChain implements TaskChainer, AutoCloseable {

    private static final Logger LOGGER = LogUtils.getLogger();
    private CompletableFuture<?> head = CompletableFuture.completedFuture((Object) null);
    private final Executor checkedExecutor;
    private volatile boolean closed;

    public FutureChain(Executor executor) {
        this.checkedExecutor = (runnable) -> {
            if (!this.closed) {
                executor.execute(runnable);
            }

        };
    }

    @Override
    public void append(TaskChainer.a taskchainer_a) {
        this.head = this.head.thenComposeAsync((object) -> {
            return taskchainer_a.submit(this.checkedExecutor);
        }, this.checkedExecutor).exceptionally((throwable) -> {
            if (throwable instanceof CompletionException) {
                CompletionException completionexception = (CompletionException) throwable;

                throwable = completionexception.getCause();
            }

            if (throwable instanceof CancellationException) {
                CancellationException cancellationexception = (CancellationException) throwable;

                throw cancellationexception;
            } else {
                FutureChain.LOGGER.error("Chain link failed, continuing to next one", throwable);
                return null;
            }
        });
    }

    public void close() {
        this.closed = true;
    }
}
