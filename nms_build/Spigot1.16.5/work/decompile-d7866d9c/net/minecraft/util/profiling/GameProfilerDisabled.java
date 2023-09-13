package net.minecraft.util.profiling;

import java.util.function.Supplier;

public class GameProfilerDisabled implements GameProfilerFillerActive {

    public static final GameProfilerDisabled a = new GameProfilerDisabled();

    private GameProfilerDisabled() {}

    @Override
    public void a() {}

    @Override
    public void b() {}

    @Override
    public void enter(String s) {}

    @Override
    public void a(Supplier<String> supplier) {}

    @Override
    public void exit() {}

    @Override
    public void exitEnter(String s) {}

    @Override
    public void c(String s) {}

    @Override
    public void c(Supplier<String> supplier) {}

    @Override
    public MethodProfilerResults d() {
        return MethodProfilerResultsEmpty.a;
    }
}
