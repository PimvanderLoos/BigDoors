package net.minecraft.util.profiling;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class MethodProfilerResultsEmpty implements MethodProfilerResults {

    public static final MethodProfilerResultsEmpty EMPTY = new MethodProfilerResultsEmpty();

    private MethodProfilerResultsEmpty() {}

    @Override
    public List<MethodProfilerResultsField> a(String s) {
        return Collections.emptyList();
    }

    @Override
    public boolean a(Path path) {
        return false;
    }

    @Override
    public long a() {
        return 0L;
    }

    @Override
    public int b() {
        return 0;
    }

    @Override
    public long c() {
        return 0L;
    }

    @Override
    public int d() {
        return 0;
    }

    @Override
    public String e() {
        return "";
    }
}
