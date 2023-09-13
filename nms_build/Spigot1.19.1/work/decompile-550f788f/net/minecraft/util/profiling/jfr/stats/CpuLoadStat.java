package net.minecraft.util.profiling.jfr.stats;

import jdk.jfr.consumer.RecordedEvent;

public record CpuLoadStat(double jvm, double userJvm, double system) {

    public static CpuLoadStat from(RecordedEvent recordedevent) {
        return new CpuLoadStat((double) recordedevent.getFloat("jvmSystem"), (double) recordedevent.getFloat("jvmUser"), (double) recordedevent.getFloat("machineTotal"));
    }
}
