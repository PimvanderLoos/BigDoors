package net.minecraft.util.profiling.jfr;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.server.DispenserRegistry;
import net.minecraft.util.profiling.jfr.parse.JfrStatsParser;
import net.minecraft.util.profiling.jfr.parse.JfrStatsResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.util.Supplier;

public class SummaryReporter {

    private static final Logger LOGGER = LogManager.getLogger();
    private final Runnable onDeregistration;

    protected SummaryReporter(Runnable runnable) {
        this.onDeregistration = runnable;
    }

    public void recordingStopped(@Nullable Path path) {
        if (path != null) {
            this.onDeregistration.run();
            infoWithFallback(() -> {
                return "Dumped flight recorder profiling to " + path;
            });

            JfrStatsResult jfrstatsresult;

            try {
                jfrstatsresult = JfrStatsParser.parse(path);
            } catch (Throwable throwable) {
                warnWithFallback(() -> {
                    return "Failed to parse JFR recording";
                }, throwable);
                return;
            }

            try {
                Objects.requireNonNull(jfrstatsresult);
                infoWithFallback(jfrstatsresult::asJson);
                String s = path.getFileName().toString();
                Path path1 = path.resolveSibling("jfr-report-" + StringUtils.substringBefore(s, ".jfr") + ".json");

                Files.writeString(path1, jfrstatsresult.asJson(), StandardOpenOption.CREATE);
                infoWithFallback(() -> {
                    return "Dumped recording summary to " + path1;
                });
            } catch (Throwable throwable1) {
                warnWithFallback(() -> {
                    return "Failed to output JFR report";
                }, throwable1);
            }

        }
    }

    private static void infoWithFallback(Supplier<String> supplier) {
        if (log4jIsActive()) {
            SummaryReporter.LOGGER.info(supplier);
        } else {
            DispenserRegistry.realStdoutPrintln((String) supplier.get());
        }

    }

    private static void warnWithFallback(Supplier<String> supplier, Throwable throwable) {
        if (log4jIsActive()) {
            SummaryReporter.LOGGER.warn(supplier, throwable);
        } else {
            DispenserRegistry.realStdoutPrintln((String) supplier.get());
            throwable.printStackTrace(DispenserRegistry.STDOUT);
        }

    }

    private static boolean log4jIsActive() {
        LoggerContext loggercontext = LogManager.getContext();

        if (loggercontext instanceof LifeCycle) {
            LifeCycle lifecycle = (LifeCycle) loggercontext;

            return !lifecycle.isStopped();
        } else {
            return true;
        }
    }
}
