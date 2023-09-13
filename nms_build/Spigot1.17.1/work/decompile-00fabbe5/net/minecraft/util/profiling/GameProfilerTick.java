package net.minecraft.util.profiling;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.LongSupplier;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameProfilerTick {

    private static final Logger LOGGER = LogManager.getLogger();
    private final LongSupplier realTime;
    private final long saveThreshold;
    private int tick;
    private final File location;
    private GameProfilerFillerActive profiler;

    public GameProfilerTick(LongSupplier longsupplier, String s, long i) {
        this.profiler = GameProfilerDisabled.INSTANCE;
        this.realTime = longsupplier;
        this.location = new File("debug", s);
        this.saveThreshold = i;
    }

    public GameProfilerFiller a() {
        this.profiler = new MethodProfiler(this.realTime, () -> {
            return this.tick;
        }, false);
        ++this.tick;
        return this.profiler;
    }

    public void b() {
        if (this.profiler != GameProfilerDisabled.INSTANCE) {
            MethodProfilerResults methodprofilerresults = this.profiler.d();

            this.profiler = GameProfilerDisabled.INSTANCE;
            if (methodprofilerresults.g() >= this.saveThreshold) {
                File file = this.location;
                SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
                Date date = new Date();
                File file1 = new File(file, "tick-results-" + simpledateformat.format(date) + ".txt");

                methodprofilerresults.a(file1.toPath());
                GameProfilerTick.LOGGER.info("Recorded long tick -- wrote info to: {}", file1.getAbsolutePath());
            }

        }
    }

    @Nullable
    public static GameProfilerTick a(String s) {
        return null;
    }

    public static GameProfilerFiller a(GameProfilerFiller gameprofilerfiller, @Nullable GameProfilerTick gameprofilertick) {
        return gameprofilertick != null ? GameProfilerFiller.a(gameprofilertick.a(), gameprofilerfiller) : gameprofilerfiller;
    }
}
