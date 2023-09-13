package net.minecraft.util.profiling;

import java.io.File;

public interface MethodProfilerResults {

    boolean a(File file);

    long a();

    int b();

    long c();

    int d();

    default long g() {
        return this.c() - this.a();
    }

    default int f() {
        return this.d() - this.b();
    }

    static String b(String s) {
        return s.replace('\u001e', '.');
    }
}
