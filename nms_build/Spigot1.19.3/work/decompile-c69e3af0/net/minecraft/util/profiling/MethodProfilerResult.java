package net.minecraft.util.profiling;

import it.unimi.dsi.fastutil.objects.Object2LongMap;

public interface MethodProfilerResult {

    long getDuration();

    long getMaxDuration();

    long getCount();

    Object2LongMap<String> getCounters();
}
