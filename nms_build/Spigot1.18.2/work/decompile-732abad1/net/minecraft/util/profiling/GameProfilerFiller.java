package net.minecraft.util.profiling;

import java.util.function.Supplier;
import net.minecraft.util.profiling.metrics.MetricCategory;

public interface GameProfilerFiller {

    String ROOT = "root";

    void startTick();

    void endTick();

    void push(String s);

    void push(Supplier<String> supplier);

    void pop();

    void popPush(String s);

    void popPush(Supplier<String> supplier);

    void markForCharting(MetricCategory metriccategory);

    default void incrementCounter(String s) {
        this.incrementCounter(s, 1);
    }

    void incrementCounter(String s, int i);

    default void incrementCounter(Supplier<String> supplier) {
        this.incrementCounter(supplier, 1);
    }

    void incrementCounter(Supplier<String> supplier, int i);

    static GameProfilerFiller tee(final GameProfilerFiller gameprofilerfiller, final GameProfilerFiller gameprofilerfiller1) {
        return gameprofilerfiller == GameProfilerDisabled.INSTANCE ? gameprofilerfiller1 : (gameprofilerfiller1 == GameProfilerDisabled.INSTANCE ? gameprofilerfiller : new GameProfilerFiller() {
            @Override
            public void startTick() {
                gameprofilerfiller.startTick();
                gameprofilerfiller1.startTick();
            }

            @Override
            public void endTick() {
                gameprofilerfiller.endTick();
                gameprofilerfiller1.endTick();
            }

            @Override
            public void push(String s) {
                gameprofilerfiller.push(s);
                gameprofilerfiller1.push(s);
            }

            @Override
            public void push(Supplier<String> supplier) {
                gameprofilerfiller.push(supplier);
                gameprofilerfiller1.push(supplier);
            }

            @Override
            public void markForCharting(MetricCategory metriccategory) {
                gameprofilerfiller.markForCharting(metriccategory);
                gameprofilerfiller1.markForCharting(metriccategory);
            }

            @Override
            public void pop() {
                gameprofilerfiller.pop();
                gameprofilerfiller1.pop();
            }

            @Override
            public void popPush(String s) {
                gameprofilerfiller.popPush(s);
                gameprofilerfiller1.popPush(s);
            }

            @Override
            public void popPush(Supplier<String> supplier) {
                gameprofilerfiller.popPush(supplier);
                gameprofilerfiller1.popPush(supplier);
            }

            @Override
            public void incrementCounter(String s, int i) {
                gameprofilerfiller.incrementCounter(s, i);
                gameprofilerfiller1.incrementCounter(s, i);
            }

            @Override
            public void incrementCounter(Supplier<String> supplier, int i) {
                gameprofilerfiller.incrementCounter(supplier, i);
                gameprofilerfiller1.incrementCounter(supplier, i);
            }
        });
    }
}
