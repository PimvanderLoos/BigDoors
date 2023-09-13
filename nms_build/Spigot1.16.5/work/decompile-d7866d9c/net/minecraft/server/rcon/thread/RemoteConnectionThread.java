package net.minecraft.server.rcon.thread;

import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.ThreadNamedUncaughtExceptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class RemoteConnectionThread implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final AtomicInteger e = new AtomicInteger(0);
    protected volatile boolean a;
    protected final String b;
    @Nullable
    protected Thread c;

    protected RemoteConnectionThread(String s) {
        this.b = s;
    }

    public synchronized boolean a() {
        if (this.a) {
            return true;
        } else {
            this.a = true;
            this.c = new Thread(this, this.b + " #" + RemoteConnectionThread.e.incrementAndGet());
            this.c.setUncaughtExceptionHandler(new ThreadNamedUncaughtExceptionHandler(RemoteConnectionThread.LOGGER));
            this.c.start();
            RemoteConnectionThread.LOGGER.info("Thread {} started", this.b);
            return true;
        }
    }

    public synchronized void b() {
        this.a = false;
        if (null != this.c) {
            int i = 0;

            while (this.c.isAlive()) {
                try {
                    this.c.join(1000L);
                    ++i;
                    if (i >= 5) {
                        RemoteConnectionThread.LOGGER.warn("Waited {} seconds attempting force stop!", i);
                    } else if (this.c.isAlive()) {
                        RemoteConnectionThread.LOGGER.warn("Thread {} ({}) failed to exit after {} second(s)", this, this.c.getState(), i, new Exception("Stack:"));
                        this.c.interrupt();
                    }
                } catch (InterruptedException interruptedexception) {
                    ;
                }
            }

            RemoteConnectionThread.LOGGER.info("Thread {} stopped", this.b);
            this.c = null;
        }
    }

    public boolean c() {
        return this.a;
    }
}
