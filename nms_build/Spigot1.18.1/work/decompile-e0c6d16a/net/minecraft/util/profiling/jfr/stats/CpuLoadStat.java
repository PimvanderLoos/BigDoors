package net.minecraft.util.profiling.jfr.stats;

import jdk.jfr.consumer.RecordedEvent;

public record CpuLoadStat(double a, double b, double c) {

    private final double jvm;
    private final double userJvm;
    private final double system;

    public CpuLoadStat(double d0, double d1, double d2) {
        this.jvm = d0;
        this.userJvm = d1;
        this.system = d2;
    }

    public static CpuLoadStat from(RecordedEvent recordedevent) {
        return new CpuLoadStat((double) recordedevent.getFloat("jvmSystem"), (double) recordedevent.getFloat("jvmUser"), (double) recordedevent.getFloat("machineTotal"));
    }

    public double jvm() {
        return this.jvm;
    }

    public double userJvm() {
        return this.userJvm;
    }

    public double system() {
        return this.system;
    }
}
