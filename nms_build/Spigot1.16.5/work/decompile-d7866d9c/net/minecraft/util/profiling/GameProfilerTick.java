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
    private final LongSupplier b;
    private final long c;
    private int d;
    private final File e;
    private GameProfilerFillerActive f;

    public GameProfilerFiller a() {
        this.f = new MethodProfiler(this.b, () -> {
            return this.d;
        }, false);
        ++this.d;
        return this.f;
    }

    public void b() {
        if (this.f != GameProfilerDisabled.a) {
            MethodProfilerResults methodprofilerresults = this.f.d();

            this.f = GameProfilerDisabled.a;
            if (methodprofilerresults.g() >= this.c) {
                File file = new File(this.e, "tick-results-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt");

                methodprofilerresults.a(file);
                GameProfilerTick.LOGGER.info("Recorded long tick -- wrote info to: {}", file.getAbsolutePath());
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
