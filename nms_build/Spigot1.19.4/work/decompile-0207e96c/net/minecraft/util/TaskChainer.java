package net.minecraft.util;

import com.mojang.logging.LogUtils;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.slf4j.Logger;

@FunctionalInterface
public interface TaskChainer {

    Logger LOGGER = LogUtils.getLogger();

    static TaskChainer immediate(Executor executor) {
        return (taskchainer_a) -> {
            taskchainer_a.submit(executor).exceptionally((throwable) -> {
                TaskChainer.LOGGER.error("Task failed", throwable);
                return null;
            });
        };
    }

    void append(TaskChainer.a taskchainer_a);

    public interface a {

        CompletableFuture<?> submit(Executor executor);
    }
}
