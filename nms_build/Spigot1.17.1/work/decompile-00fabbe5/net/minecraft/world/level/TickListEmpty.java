package net.minecraft.world.level;

import net.minecraft.core.BlockPosition;

public class TickListEmpty<T> implements TickList<T> {

    private static final TickListEmpty<Object> INSTANCE = new TickListEmpty<>();

    public TickListEmpty() {}

    public static <T> TickListEmpty<T> b() {
        return TickListEmpty.INSTANCE;
    }

    @Override
    public boolean a(BlockPosition blockposition, T t0) {
        return false;
    }

    @Override
    public void a(BlockPosition blockposition, T t0, int i) {}

    @Override
    public void a(BlockPosition blockposition, T t0, int i, TickListPriority ticklistpriority) {}

    @Override
    public boolean b(BlockPosition blockposition, T t0) {
        return false;
    }

    @Override
    public int a() {
        return 0;
    }
}
