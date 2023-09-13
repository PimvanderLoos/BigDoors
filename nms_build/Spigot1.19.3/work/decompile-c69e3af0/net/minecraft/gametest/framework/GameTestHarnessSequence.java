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
        this.lastTick = gametestharnessinfo.getTick();
    }

    public GameTestHarnessSequence thenWaitUntil(Runnable runnable) {
        this.events.add(GameTestHarnessEvent.create(runnable));
        return this;
    }

    public GameTestHarnessSequence thenWaitUntil(long i, Runnable runnable) {
        this.events.add(GameTestHarnessEvent.create(i, runnable));
        return this;
    }

    public GameTestHarnessSequence thenIdle(int i) {
        return this.thenExecuteAfter(i, () -> {
        });
    }

    public GameTestHarnessSequence thenExecute(Runnable runnable) {
        this.events.add(GameTestHarnessEvent.create(() -> {
            this.executeWithoutFail(runnable);
        }));
        return this;
    }

    public GameTestHarnessSequence thenExecuteAfter(int i, Runnable runnable) {
        this.events.add(GameTestHarnessEvent.create(() -> {
            if (this.parent.getTick() < this.lastTick + (long) i) {
                throw new GameTestHarnessAssertion("Waiting");
            } else {
                this.executeWithoutFail(runnable);
            }
        }));
        return this;
    }

    public GameTestHarnessSequence thenExecuteFor(int i, Runnable runnable) {
        this.events.add(GameTestHarnessEvent.create(() -> {
            if (this.parent.getTick() < this.lastTick + (long) i) {
                this.executeWithoutFail(runnable);
                throw new GameTestHarnessAssertion("Waiting");
            }
        }));
        return this;
    }

    public void thenSucceed() {
        List list = this.events;
        GameTestHarnessInfo gametestharnessinfo = this.parent;

        Objects.requireNonNull(this.parent);
        list.add(GameTestHarnessEvent.create(gametestharnessinfo::succeed));
    }

    public void thenFail(Supplier<Exception> supplier) {
        this.events.add(GameTestHarnessEvent.create(() -> {
            this.parent.fail((Throwable) supplier.get());
        }));
    }

    public GameTestHarnessSequence.a thenTrigger() {
        GameTestHarnessSequence.a gametestharnesssequence_a = new GameTestHarnessSequence.a();

        this.events.add(GameTestHarnessEvent.create(() -> {
            gametestharnesssequence_a.trigger(this.parent.getTick());
        }));
        return gametestharnesssequence_a;
    }

    public void tickAndContinue(long i) {
        try {
            this.tick(i);
        } catch (GameTestHarnessAssertion gametestharnessassertion) {
            ;
        }

    }

    public void tickAndFailIfNotComplete(long i) {
        try {
            this.tick(i);
        } catch (GameTestHarnessAssertion gametestharnessassertion) {
            this.parent.fail(gametestharnessassertion);
        }

    }

    private void executeWithoutFail(Runnable runnable) {
        try {
            runnable.run();
        } catch (GameTestHarnessAssertion gametestharnessassertion) {
            this.parent.fail(gametestharnessassertion);
        }

    }

    private void tick(long i) {
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

                gametestharnessinfo.fail(new GameTestHarnessAssertion("Succeeded in invalid tick: expected " + l + ", but current tick is " + i));
                break;
            }
        }

    }

    public class a {

        private static final long NOT_TRIGGERED = -1L;
        private long triggerTime = -1L;

        public a() {}

        void trigger(long i) {
            if (this.triggerTime != -1L) {
                throw new IllegalStateException("Condition already triggered at " + this.triggerTime);
            } else {
                this.triggerTime = i;
            }
        }

        public void assertTriggeredThisTick() {
            long i = GameTestHarnessSequence.this.parent.getTick();

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
