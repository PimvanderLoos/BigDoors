package net.minecraft.util;

import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportSystemDetails;
import net.minecraft.ReportedException;

public class ThreadingDetector {

    public ThreadingDetector() {}

    public static void a(Semaphore semaphore, @Nullable DebugBuffer<Pair<Thread, StackTraceElement[]>> debugbuffer, String s) {
        boolean flag = semaphore.tryAcquire();

        if (!flag) {
            throw a(s, debugbuffer);
        }
    }

    public static ReportedException a(String s, @Nullable DebugBuffer<Pair<Thread, StackTraceElement[]>> debugbuffer) {
        String s1 = (String) Thread.getAllStackTraces().keySet().stream().filter(Objects::nonNull).map((thread) -> {
            String s2 = thread.getName();

            return s2 + ": \n\tat " + (String) Arrays.stream(thread.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n\tat "));
        }).collect(Collectors.joining("\n"));
        CrashReport crashreport = new CrashReport("Accessing " + s + " from multiple threads", new IllegalStateException());
        CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Thread dumps");

        crashreportsystemdetails.a("Thread dumps", (Object) s1);
        if (debugbuffer != null) {
            StringBuilder stringbuilder = new StringBuilder();
            List<Pair<Thread, StackTraceElement[]>> list = debugbuffer.a();
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                Pair<Thread, StackTraceElement[]> pair = (Pair) iterator.next();

                stringbuilder.append("Thread ").append(((Thread) pair.getFirst()).getName()).append(": \n\tat ").append((String) Arrays.stream((StackTraceElement[]) pair.getSecond()).map(Object::toString).collect(Collectors.joining("\n\tat "))).append("\n");
            }

            crashreportsystemdetails.a("Last threads", (Object) stringbuilder.toString());
        }

        return new ReportedException(crashreport);
    }
}
