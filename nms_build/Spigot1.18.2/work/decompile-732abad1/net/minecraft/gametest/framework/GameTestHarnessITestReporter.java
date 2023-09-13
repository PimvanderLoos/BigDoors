package net.minecraft.gametest.framework;

public interface GameTestHarnessITestReporter {

    void onTestFailed(GameTestHarnessInfo gametestharnessinfo);

    void onTestSuccess(GameTestHarnessInfo gametestharnessinfo);

    default void finish() {}
}
