package net.minecraft.world.ticks;

import net.minecraft.core.BlockPosition;

public interface LevelTickAccess<T> extends TickList<T> {

    boolean willTickThisTick(BlockPosition blockposition, T t0);
}
