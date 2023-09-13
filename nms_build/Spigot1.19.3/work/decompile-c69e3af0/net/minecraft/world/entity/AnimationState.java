package net.minecraft.world.entity;

import java.util.function.Consumer;
import net.minecraft.util.MathHelper;

public class AnimationState {

    private static final long STOPPED = Long.MAX_VALUE;
    private long lastTime = Long.MAX_VALUE;
    private long accumulatedTime;

    public AnimationState() {}

    public void start(int i) {
        this.lastTime = (long) i * 1000L / 20L;
        this.accumulatedTime = 0L;
    }

    public void startIfStopped(int i) {
        if (!this.isStarted()) {
            this.start(i);
        }

    }

    public void animateWhen(boolean flag, int i) {
        if (flag) {
            this.startIfStopped(i);
        } else {
            this.stop();
        }

    }

    public void stop() {
        this.lastTime = Long.MAX_VALUE;
    }

    public void ifStarted(Consumer<AnimationState> consumer) {
        if (this.isStarted()) {
            consumer.accept(this);
        }

    }

    public void updateTime(float f, float f1) {
        if (this.isStarted()) {
            long i = MathHelper.lfloor((double) (f * 1000.0F / 20.0F));

            this.accumulatedTime += (long) ((float) (i - this.lastTime) * f1);
            this.lastTime = i;
        }
    }

    public long getAccumulatedTime() {
        return this.accumulatedTime;
    }

    public boolean isStarted() {
        return this.lastTime != Long.MAX_VALUE;
    }
}
