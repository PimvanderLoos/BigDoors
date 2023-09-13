package net.minecraft.gametest.framework;

public class GlobalTestReporter {

    private static GameTestHarnessITestReporter DELEGATE = new GameTestHarnessLogger();

    public GlobalTestReporter() {}

    public static void a(GameTestHarnessITestReporter gametestharnessitestreporter) {
        GlobalTestReporter.DELEGATE = gametestharnessitestreporter;
    }

    public static void a(GameTestHarnessInfo gametestharnessinfo) {
        GlobalTestReporter.DELEGATE.a(gametestharnessinfo);
    }

    public static void b(GameTestHarnessInfo gametestharnessinfo) {
        GlobalTestReporter.DELEGATE.b(gametestharnessinfo);
    }

    public static void a() {
        GlobalTestReporter.DELEGATE.a();
    }
}
