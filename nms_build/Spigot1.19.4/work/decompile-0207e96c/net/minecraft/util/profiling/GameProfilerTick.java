package net.minecraft.util.profiling;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.util.function.LongSupplier;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import org.slf4j.Logger;

public class GameProfilerTick {

    private static final Logger LOGGER = LogUtils.getLogger();
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

    public GameProfilerFiller startTick() {
        this.profiler = new MethodProfiler(this.realTime, () -> {
            return this.tick;
        }, false);
        ++this.tick;
        return this.profiler;
    }

    public void endTick() {
        if (this.profiler != GameProfilerDisabled.INSTANCE) {
            MethodProfilerResults methodprofilerresults = this.profiler.getResults();

            this.profiler = GameProfilerDisabled.INSTANCE;
            if (methodprofilerresults.getNanoDuration() >= this.saveThreshold) {
                File file = new File(this.location, "tick-results-" + SystemUtils.getFilenameFormattedDateTime() + ".txt");

                methodprofilerresults.saveResults(file.toPath());
                GameProfilerTick.LOGGER.info("Recorded long tick -- wrote info to: {}", file.getAbsolutePath());
            }

        }
    }

    @Nullable
    public static GameProfilerTick createTickProfiler(String s) {
        return null;
    }

    public static GameProfilerFiller decorateFiller(GameProfilerFiller gameprofilerfiller, @Nullable GameProfilerTick gameprofilertick) {
        return gameprofilertick != null ? GameProfilerFiller.tee(gameprofilertick.startTick(), gameprofilerfiller) : gameprofilerfiller;
    }
}
