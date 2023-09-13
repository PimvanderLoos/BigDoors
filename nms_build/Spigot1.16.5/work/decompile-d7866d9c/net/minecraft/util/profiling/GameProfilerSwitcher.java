package net.minecraft.util.profiling;

import java.util.function.IntSupplier;
import java.util.function.LongSupplier;

public class GameProfilerSwitcher {

    private final LongSupplier a;
    private final IntSupplier b;
    private GameProfilerFillerActive c;

    public GameProfilerSwitcher(LongSupplier longsupplier, IntSupplier intsupplier) {
        this.c = GameProfilerDisabled.a;
        this.a = longsupplier;
        this.b = intsupplier;
    }

    public boolean a() {
        return this.c != GameProfilerDisabled.a;
    }

    public void b() {
        this.c = GameProfilerDisabled.a;
    }

    public void c() {
        this.c = new MethodProfiler(this.a, this.b, true);
    }

    public GameProfilerFiller d() {
        return this.c;
    }

    public MethodProfilerResults e() {
        return this.c.d();
    }
}
