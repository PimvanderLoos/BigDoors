package net.minecraft.server;

public interface TickList<T> {

    boolean a(BlockPosition blockposition, T t0);

    default void a(BlockPosition blockposition, T t0, int i) {
        this.a(blockposition, t0, i, TickListPriority.NORMAL);
    }

    void a(BlockPosition blockposition, T t0, int i, TickListPriority ticklistpriority);

    boolean b(BlockPosition blockposition, T t0);
}
