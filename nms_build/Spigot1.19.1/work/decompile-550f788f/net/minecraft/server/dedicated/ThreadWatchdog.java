package net.minecraft.server.dedicated;

import com.google.common.collect.Streams;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportSystemDetails;
import net.minecraft.SystemUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.DispenserRegistry;
import net.minecraft.world.level.GameRules;
import org.slf4j.Logger;

public class ThreadWatchdog implements Runnable {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final long MAX_SHUTDOWN_TIME = 10000L;
    private static final int SHUTDOWN_STATUS = 1;
    private final DedicatedServer server;
    private final long maxTickTime;

    public ThreadWatchdog(DedicatedServer dedicatedserver) {
        this.server = dedicatedserver;
        this.maxTickTime = dedicatedserver.getMaxTickLength();
    }

    public void run() {
        while (this.server.isRunning()) {
            long i = this.server.getNextTickTime();
            long j = SystemUtils.getMillis();
            long k = j - i;

            if (k > this.maxTickTime) {
                ThreadWatchdog.LOGGER.error(LogUtils.FATAL_MARKER, "A single server tick took {} seconds (should be max {})", String.format(Locale.ROOT, "%.2f", (float) k / 1000.0F), String.format(Locale.ROOT, "%.2f", 0.05F));
                ThreadWatchdog.LOGGER.error(LogUtils.FATAL_MARKER, "Considering it to be crashed, server will forcibly shutdown.");
                ThreadMXBean threadmxbean = ManagementFactory.getThreadMXBean();
                ThreadInfo[] athreadinfo = threadmxbean.dumpAllThreads(true, true);
                StringBuilder stringbuilder = new StringBuilder();
                Error error = new Error("Watchdog");
                ThreadInfo[] athreadinfo1 = athreadinfo;
                int l = athreadinfo.length;

                for (int i1 = 0; i1 < l; ++i1) {
                    ThreadInfo threadinfo = athreadinfo1[i1];

                    if (threadinfo.getThreadId() == this.server.getRunningThread().getId()) {
                        error.setStackTrace(threadinfo.getStackTrace());
                    }

                    stringbuilder.append(threadinfo);
                    stringbuilder.append("\n");
                }

                CrashReport crashreport = new CrashReport("Watching Server", error);

                this.server.fillSystemReport(crashreport.getSystemReport());
                CrashReportSystemDetails crashreportsystemdetails = crashreport.addCategory("Thread Dump");

                crashreportsystemdetails.setDetail("Threads", (Object) stringbuilder);
                CrashReportSystemDetails crashreportsystemdetails1 = crashreport.addCategory("Performance stats");

                crashreportsystemdetails1.setDetail("Random tick rate", () -> {
                    return ((GameRules.GameRuleInt) this.server.getWorldData().getGameRules().getRule(GameRules.RULE_RANDOMTICKING)).toString();
                });
                crashreportsystemdetails1.setDetail("Level stats", () -> {
                    return (String) Streams.stream(this.server.getAllLevels()).map((worldserver) -> {
                        ResourceKey resourcekey = worldserver.dimension();

                        return resourcekey + ": " + worldserver.getWatchdogStats();
                    }).collect(Collectors.joining(",\n"));
                });
                DispenserRegistry.realStdoutPrintln("Crash report:\n" + crashreport.getFriendlyReport());
                File file = new File(new File(this.server.getServerDirectory(), "crash-reports"), "crash-" + SystemUtils.getFilenameFormattedDateTime() + "-server.txt");

                if (crashreport.saveToFile(file)) {
                    ThreadWatchdog.LOGGER.error("This crash report has been saved to: {}", file.getAbsolutePath());
                } else {
                    ThreadWatchdog.LOGGER.error("We were unable to save this crash report to disk.");
                }

                this.exit();
            }

            try {
                Thread.sleep(i + this.maxTickTime - j);
            } catch (InterruptedException interruptedexception) {
                ;
            }
        }

    }

    private void exit() {
        try {
            Timer timer = new Timer();

            timer.schedule(new TimerTask() {
                public void run() {
                    Runtime.getRuntime().halt(1);
                }
            }, 10000L);
            System.exit(1);
        } catch (Throwable throwable) {
            Runtime.getRuntime().halt(1);
        }

    }
}
