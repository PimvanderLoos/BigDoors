package net.minecraft;

public class ReportedException extends RuntimeException {

    private final CrashReport report;

    public ReportedException(CrashReport crashreport) {
        this.report = crashreport;
    }

    public CrashReport a() {
        return this.report;
    }

    public Throwable getCause() {
        return this.report.b();
    }

    public String getMessage() {
        return this.report.a();
    }
}
