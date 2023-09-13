package net.minecraft.server.rcon.thread;

import com.mojang.logging.LogUtils;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.ThreadNamedUncaughtExceptionHandler;
import org.slf4j.Logger;

public abstract class RemoteConnectionThread implements Runnable {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
    private static final int MAX_STOP_WAIT = 5;
    protected volatile boolean running;
    protected final String name;
    @Nullable
    protected Thread thread;

    protected RemoteConnectionThread(String s) {
        this.name = s;
    }

    public synchronized boolean start() {
        if (this.running) {
            return true;
        } else {
            this.running = true;
            this.thread = new Thread(this, this.name + " #" + RemoteConnectionThread.UNIQUE_THREAD_ID.incrementAndGet());
            this.thread.setUncaughtExceptionHandler(new ThreadNamedUncaughtExceptionHandler(RemoteConnectionThread.LOGGER));
            this.thread.start();
            RemoteConnectionThread.LOGGER.info("Thread {} started", this.name);
            return true;
        }
    }

    public synchronized void stop() {
        this.running = false;
        if (null != this.thread) {
            int i = 0;

            while (this.thread.isAlive()) {
                try {
                    this.thread.join(1000L);
                    ++i;
                    if (i >= 5) {
                        RemoteConnectionThread.LOGGER.warn("Waited {} seconds attempting force stop!", i);
                    } else if (this.thread.isAlive()) {
                        RemoteConnectionThread.LOGGER.warn("Thread {} ({}) failed to exit after {} second(s)", new Object[]{this, this.thread.getState(), i, new Exception("Stack:")});
                        this.thread.interrupt();
                    }
                } catch (InterruptedException interruptedexception) {
                    ;
                }
            }

            RemoteConnectionThread.LOGGER.info("Thread {} stopped", this.name);
            this.thread = null;
        }
    }

    public boolean isRunning() {
        return this.running;
    }
}
