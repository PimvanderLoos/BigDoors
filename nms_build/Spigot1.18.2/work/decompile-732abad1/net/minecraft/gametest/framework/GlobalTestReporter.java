package net.minecraft.gametest.framework;

public class GlobalTestReporter {

    private static GameTestHarnessITestReporter DELEGATE = new GameTestHarnessLogger();

    public GlobalTestReporter() {}

    public static void replaceWith(GameTestHarnessITestReporter gametestharnessitestreporter) {
        GlobalTestReporter.DELEGATE = gametestharnessitestreporter;
    }

    public static void onTestFailed(GameTestHarnessInfo gametestharnessinfo) {
        GlobalTestReporter.DELEGATE.onTestFailed(gametestharnessinfo);
    }

    public static void onTestSuccess(GameTestHarnessInfo gametestharnessinfo) {
        GlobalTestReporter.DELEGATE.onTestSuccess(gametestharnessinfo);
    }

    public static void finish() {
        GlobalTestReporter.DELEGATE.finish();
    }
}
