package net.minecraft.world.ticks;

import java.util.function.Function;
import net.minecraft.core.BlockPosition;

public class TickListWorldGen<T> implements LevelTickAccess<T> {

    private final Function<BlockPosition, TickContainerAccess<T>> containerGetter;

    public TickListWorldGen(Function<BlockPosition, TickContainerAccess<T>> function) {
        this.containerGetter = function;
    }

    @Override
    public boolean hasScheduledTick(BlockPosition blockposition, T t0) {
        return ((TickContainerAccess) this.containerGetter.apply(blockposition)).hasScheduledTick(blockposition, t0);
    }

    @Override
    public void schedule(NextTickListEntry<T> nextticklistentry) {
        ((TickContainerAccess) this.containerGetter.apply(nextticklistentry.pos())).schedule(nextticklistentry);
    }

    @Override
    public boolean willTickThisTick(BlockPosition blockposition, T t0) {
        return false;
    }

    @Override
    public int count() {
        return 0;
    }
}
