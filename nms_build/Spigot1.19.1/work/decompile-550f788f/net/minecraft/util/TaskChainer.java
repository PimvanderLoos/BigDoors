package net.minecraft.util;

import com.mojang.logging.LogUtils;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import org.slf4j.Logger;

@FunctionalInterface
public interface TaskChainer {

    Logger LOGGER = LogUtils.getLogger();
    TaskChainer IMMEDIATE = (taskchainer_a) -> {
        ((CompletableFuture) taskchainer_a.get()).exceptionally((throwable) -> {
            TaskChainer.LOGGER.error("Task failed", throwable);
            return null;
        });
    };

    void append(TaskChainer.a taskchainer_a);

    public interface a extends Supplier<CompletableFuture<?>> {}
}
