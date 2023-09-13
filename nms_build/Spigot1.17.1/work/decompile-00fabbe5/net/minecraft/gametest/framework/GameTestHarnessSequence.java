package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class GameTestHarnessSequence {

    final GameTestHarnessInfo parent;
    private final List<GameTestHarnessEvent> events = Lists.newArrayList();
    private long lastTick;

    GameTestHarnessSequence(GameTestHarnessInfo gametestharnessinfo) {
        this.parent = gametestharnessinfo;
        this.lastTick = gametestharnessinfo.p();
    }

    public GameTestHarnessSequence a(Runnable runnable) {
        this.events.add(GameTestHarnessEvent.a(runnable));
        return this;
    }

    public GameTestHarnessSequence a(long i, Runnable runnable) {
        this.events.add(GameTestHarnessEvent.a(i, runnable));
        return this;
    }

    public GameTestHarnessSequence a(int i) {
        return this.a(i, () -> {
        });
    }

    public GameTestHarnessSequence b(Runnable runnable) {
        this.events.add(GameTestHarnessEvent.a(() -> {
            this.c(runnable);
        }));
        return this;
    }

    public GameTestHarnessSequence a(int i, Runnable runnable) {
        this.events.add(GameTestHarnessEvent.a(() -> {
            if (this.parent.p() < this.lastTick + (long) i) {
                throw new GameTestHarnessAssertion("Waiting");
            } else {
                this.c(runnable);
            }
        }));
        return this;
    }

    public GameTestHarnessSequence b(int i, Runnable runnable) {
        this.events.add(GameTestHarnessEvent.a(() -> {
            if (this.parent.p() < this.lastTick + (long) i) {
                this.c(runnable);
                throw new GameTestHarnessAssertion("Waiting");
            }
        }));
        return this;
    }

    public void a() {
        List list = this.events;
        GameTestHarnessInfo gametestharnessinfo = this.parent;

        Objects.requireNonNull(this.parent);
        list.add(GameTestHarnessEvent.a(gametestharnessinfo::m));
    }

    public void a(Supplier<Exception> supplier) {
        this.events.add(GameTestHarnessEvent.a(() -> {
            this.parent.a((Throwable) supplier.get());
        }));
    }

    public GameTestHarnessSequence.a b() {
        GameTestHarnessSequence.a gametestharnesssequence_a = new GameTestHarnessSequence.a();

        this.events.add(GameTestHarnessEvent.a(() -> {
            gametestharnesssequence_a.a(this.parent.p());
        }));
        return gametestharnesssequence_a;
    }

    public void a(long i) {
        try {
            this.c(i);
        } catch (GameTestHarnessAssertion gametestharnessassertion) {
            ;
        }

    }

    public void b(long i) {
        try {
            this.c(i);
        } catch (GameTestHarnessAssertion gametestharnessassertion) {
            this.parent.a((Throwable) gametestharnessassertion);
        }

    }

    private void c(Runnable runnable) {
        try {
            runnable.run();
        } catch (GameTestHarnessAssertion gametestharnessassertion) {
            this.parent.a((Throwable) gametestharnessassertion);
        }

    }

    private void c(long i) {
        Iterator iterator = this.events.iterator();

        while (iterator.hasNext()) {
            GameTestHarnessEvent gametestharnessevent = (GameTestHarnessEvent) iterator.next();

            gametestharnessevent.assertion.run();
            iterator.remove();
            long j = i - this.lastTick;
            long k = this.lastTick;

            this.lastTick = i;
            if (gametestharnessevent.expectedDelay != null && gametestharnessevent.expectedDelay != j) {
                GameTestHarnessInfo gametestharnessinfo = this.parent;
                long l = k + gametestharnessevent.expectedDelay;

                gametestharnessinfo.a((Throwable) (new GameTestHarnessAssertion("Succeeded in invalid tick: expected " + l + ", but current tick is " + i)));
                break;
            }
        }

    }

    public class a {

        private static final long NOT_TRIGGERED = -1L;
        private long triggerTime = -1L;

        public a() {}

        void a(long i) {
            if (this.triggerTime != -1L) {
                throw new IllegalStateException("Condition already triggered at " + this.triggerTime);
            } else {
                this.triggerTime = i;
            }
        }

        public void a() {
            long i = GameTestHarnessSequence.this.parent.p();

            if (this.triggerTime != i) {
                if (this.triggerTime == -1L) {
                    throw new GameTestHarnessAssertion("Condition not triggered (t=" + i + ")");
                } else {
                    throw new GameTestHarnessAssertion("Condition triggered at " + this.triggerTime + ", (t=" + i + ")");
                }
            }
        }
    }
}
