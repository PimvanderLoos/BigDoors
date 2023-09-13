package net.minecraft.util.profiling;

import java.nio.file.Path;
import java.util.List;

public interface MethodProfilerResults {

    char PATH_SEPARATOR = '\u001e';

    List<MethodProfilerResultsField> a(String s);

    boolean a(Path path);

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

    String e();

    static String b(String s) {
        return s.replace('\u001e', '.');
    }
}
