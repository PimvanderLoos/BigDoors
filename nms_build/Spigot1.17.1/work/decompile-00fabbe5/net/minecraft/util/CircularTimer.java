package net.minecraft.util;

public class CircularTimer {

    public static final int LOGGING_LENGTH = 240;
    private final long[] loggedTimes = new long[240];
    private int logStart;
    private int logLength;
    private int logEnd;

    public CircularTimer() {}

    public void a(long i) {
        this.loggedTimes[this.logEnd] = i;
        ++this.logEnd;
        if (this.logEnd == 240) {
            this.logEnd = 0;
        }

        if (this.logLength < 240) {
            this.logStart = 0;
            ++this.logLength;
        } else {
            this.logStart = this.b(this.logEnd + 1);
        }

    }

    public long a(int i) {
        int j = (this.logStart + i) % 240;
        int k = this.logStart;

        long l;

        for (l = 0L; k != j; ++k) {
            l += this.loggedTimes[k];
        }

        return l / (long) i;
    }

    public int a(int i, int j) {
        return this.a(this.a(i), j, 60);
    }

    public int a(long i, int j, int k) {
        double d0 = (double) i / (double) (1000000000L / (long) k);

        return (int) (d0 * (double) j);
    }

    public int a() {
        return this.logStart;
    }

    public int b() {
        return this.logEnd;
    }

    public int b(int i) {
        return i % 240;
    }

    public long[] c() {
        return this.loggedTimes;
    }
}
