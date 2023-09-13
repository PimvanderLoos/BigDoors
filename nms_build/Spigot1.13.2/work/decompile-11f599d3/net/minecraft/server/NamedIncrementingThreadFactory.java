package net.minecraft.server;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NamedIncrementingThreadFactory implements ThreadFactory {

    private static final Logger a = LogManager.getLogger();
    private final ThreadGroup b;
    private final AtomicInteger c = new AtomicInteger(1);
    private final String d;

    public NamedIncrementingThreadFactory(String s) {
        SecurityManager securitymanager = System.getSecurityManager();

        this.b = securitymanager != null ? securitymanager.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.d = s + "-";
    }

    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(this.b, runnable, this.d + this.c.getAndIncrement(), 0L);

        thread.setUncaughtExceptionHandler((thread1, throwable) -> {
            NamedIncrementingThreadFactory.a.error("Caught exception in thread {} from {}", thread1, runnable);
            NamedIncrementingThreadFactory.a.error("", throwable);
        });
        if (thread.getPriority() != 5) {
            thread.setPriority(5);
        }

        return thread;
    }
}
