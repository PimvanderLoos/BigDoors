package net.minecraft.world.ticks;

import net.minecraft.core.BlockPosition;

public interface TickList<T> {

    void schedule(NextTickListEntry<T> nextticklistentry);

    boolean hasScheduledTick(BlockPosition blockposition, T t0);

    int count();
}
