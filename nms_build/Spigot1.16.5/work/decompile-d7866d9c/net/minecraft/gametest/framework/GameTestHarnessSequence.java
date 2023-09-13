package net.minecraft.gametest.framework;

import java.util.Iterator;
import java.util.List;

public class GameTestHarnessSequence {

    private final GameTestHarnessInfo a;
    private final List<GameTestHarnessEvent> b;
    private long c;

    public void a(long i) {
        try {
            this.c(i);
        } catch (Exception exception) {
            ;
        }

    }

    public void b(long i) {
        try {
            this.c(i);
        } catch (Exception exception) {
            this.a.a((Throwable) exception);
        }

    }

    private void c(long i) {
        Iterator iterator = this.b.iterator();

        while (iterator.hasNext()) {
            GameTestHarnessEvent gametestharnessevent = (GameTestHarnessEvent) iterator.next();

            gametestharnessevent.b.run();
            iterator.remove();
            long j = i - this.c;
            long k = this.c;

            this.c = i;
            if (gametestharnessevent.a != null && gametestharnessevent.a != j) {
                this.a.a((Throwable) (new GameTestHarnessAssertion("Succeeded in invalid tick: expected " + (k + gametestharnessevent.a) + ", but current tick is " + i)));
                break;
            }
        }

    }
}
