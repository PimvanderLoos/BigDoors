package net.minecraft.gametest.framework;

import com.mojang.logging.LogUtils;
import net.minecraft.SystemUtils;
import org.slf4j.Logger;

public class GameTestHarnessLogger implements GameTestHarnessITestReporter {

    private static final Logger LOGGER = LogUtils.getLogger();

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
