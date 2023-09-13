package net.minecraft.server;

import java.util.function.Function;

public class TickListWorldGen<T> implements TickList<T> {

    private final Function<BlockPosition, TickList<T>> a;

    public TickListWorldGen(Function<BlockPosition, TickList<T>> function) {
        this.a = function;
    }

    public boolean a(BlockPosition blockposition, T t0) {
        return ((TickList) this.a.apply(blockposition)).a(blockposition, t0);
    }

    public void a(BlockPosition blockposition, T t0, int i, TickListPriority ticklistpriority) {
        ((TickList) this.a.apply(blockposition)).a(blockposition, t0, i, ticklistpriority);
    }

    public boolean b(BlockPosition blockposition, T t0) {
        return false;
    }
}
