package net.minecraft.server.dedicated;

import com.google.common.collect.Streams;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadWatchdog implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final long MAX_SHUTDOWN_TIME = 10000L;
    private static final int SHUTDOWN_STATUS = 1;
    private final DedicatedServer server;
    private final long maxTickTime;

    public ThreadWatchdog(DedicatedServer dedicatedserver) {
        this.server = dedicatedserver;
        this.maxTickTime = dedicatedserver.getMaxTickTime();
    }

    public void run() {
        while (this.server.isRunning()) {
            long i = this.server.aw();
            long j = SystemUtils.getMonotonicMillis();
            long k = j - i;

            if (k > this.maxTickTime) {
                ThreadWatchdog.LOGGER.fatal("A single server tick took {} seconds (should be max {})", String.format(Locale.ROOT, "%.2f", (float) k / 1000.0F), String.format(Locale.ROOT, "%.2f", 0.05F));
                ThreadWatchdog.LOGGER.fatal("Considering it to be crashed, server will forcibly shutdown.");
                ThreadMXBean threadmxbean = ManagementFactory.getThreadMXBean();
                ThreadInfo[] athreadinfo = threadmxbean.dumpAllThreads(true, true);
                StringBuilder stringbuilder = new StringBuilder();
                Error error = new Error("Watchdog");
                ThreadInfo[] athreadinfo1 = athreadinfo;
                int l = athreadinfo.length;

                for (int i1 = 0; i1 < l; ++i1) {
                    ThreadInfo threadinfo = athreadinfo1[i1];

                    if (threadinfo.getThreadId() == this.server.getThread().getId()) {
                        error.setStackTrace(threadinfo.getStackTrace());
                    }

                    stringbuilder.append(threadinfo);
                    stringbuilder.append("\n");
                }

                CrashReport crashreport = new CrashReport("Watching Server", error);

                this.server.b(crashreport.g());
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Thread Dump");

                crashreportsystemdetails.a("Threads", (Object) stringbuilder);
                CrashReportSystemDetails crashreportsystemdetails1 = crashreport.a("Performance stats");

                crashreportsystemdetails1.a("Random tick rate", () -> {
                    return ((GameRules.GameRuleInt) this.server.getSaveData().q().get(GameRules.RULE_RANDOMTICKING)).toString();
                });
                crashreportsystemdetails1.a("Level stats", () -> {
                    return (String) Streams.stream(this.server.getWorlds()).map((worldserver) -> {
                        ResourceKey resourcekey = worldserver.getDimensionKey();

                        return resourcekey + ": " + worldserver.H();
                    }).collect(Collectors.joining(",\n"));
                });
                DispenserRegistry.a("Crash report:\n" + crashreport.e());
                File file = new File(this.server.B(), "crash-reports");
                SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
                Date date = new Date();
                File file1 = new File(file, "crash-" + simpledateformat.format(date) + "-server.txt");

                if (crashreport.a(file1)) {
                    ThreadWatchdog.LOGGER.error("This crash report has been saved to: {}", file1.getAbsolutePath());
                } else {
                    ThreadWatchdog.LOGGER.error("We were unable to save this crash report to disk.");
                }

                this.a();
            }

            try {
                Thread.sleep(i + this.maxTickTime - j);
            } catch (InterruptedException interruptedexception) {
                ;
            }
        }

    }

    private void a() {
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
