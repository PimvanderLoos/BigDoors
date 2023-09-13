package net.minecraft.util.profiling;

public final class MethodProfilerResultsField implements Comparable<MethodProfilerResultsField> {

    public final double percentage;
    public final double globalPercentage;
    public final long count;
    public final String name;

    public MethodProfilerResultsField(String s, double d0, double d1, long i) {
        this.name = s;
        this.percentage = d0;
        this.globalPercentage = d1;
        this.count = i;
    }

    public int compareTo(MethodProfilerResultsField methodprofilerresultsfield) {
        return methodprofilerresultsfield.percentage < this.percentage ? -1 : (methodprofilerresultsfield.percentage > this.percentage ? 1 : methodprofilerresultsfield.name.compareTo(this.name));
    }

    public int getColor() {
        return (this.name.hashCode() & 11184810) + 4473924;
    }
}
