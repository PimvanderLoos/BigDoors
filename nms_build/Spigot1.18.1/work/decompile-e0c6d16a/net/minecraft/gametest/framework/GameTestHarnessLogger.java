package net.minecraft.gametest.framework;

import net.minecraft.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameTestHarnessLogger implements GameTestHarnessITestReporter {

    private static final Logger LOGGER = LogManager.getLogger();

    public GameTestHarnessLogger() {}

    @Override
    public void onTestFailed(GameTestHarnessInfo gametestharnessinfo) {
        if (gametestharnessinfo.isRequired()) {
            GameTestHarnessLogger.LOGGER.error("{} failed! {}", gametestharnessinfo.getTestName(), SystemUtils.describeError(gametestharnessinfo.getError()));
        } else {
            GameTestHarnessLogger.LOGGER.warn("(optional) {} failed. {}", gametestharnessinfo.getTestName(), SystemUtils.describeError(gametestharnessinfo.getError()));
        }

    }

    @Override
    public void onTestSuccess(GameTestHarnessInfo gametestharnessinfo) {}
}
