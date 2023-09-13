package net.minecraft.gametest.framework;

import net.minecraft.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameTestHarnessLogger implements GameTestHarnessITestReporter {

    private static final Logger LOGGER = LogManager.getLogger();

    public GameTestHarnessLogger() {}

    @Override
    public void a(GameTestHarnessInfo gametestharnessinfo) {
        if (gametestharnessinfo.q()) {
            GameTestHarnessLogger.LOGGER.error(gametestharnessinfo.c() + " failed! " + SystemUtils.d(gametestharnessinfo.n()));
        } else {
            GameTestHarnessLogger.LOGGER.warn("(optional) " + gametestharnessinfo.c() + " failed. " + SystemUtils.d(gametestharnessinfo.n()));
        }

    }
}
