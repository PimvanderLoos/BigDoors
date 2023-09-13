package net.minecraft.util.profiling.metrics.storage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.SystemUtils;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.CSVWriter;
import net.minecraft.util.profiling.MethodProfilerResults;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.util.profiling.metrics.MetricSampler;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MetricsPersister {

    public static final Path PROFILING_RESULTS_DIR = Paths.get("debug/profiling");
    public static final String METRICS_DIR_NAME = "metrics";
    public static final String DEVIATIONS_DIR_NAME = "deviations";
    public static final String PROFILING_RESULT_FILENAME = "profiling.txt";
    private static final Logger LOGGER = LogManager.getLogger();
    private final String rootFolderName;

    public MetricsPersister(String s) {
        this.rootFolderName = s;
    }

    public Path a(Set<MetricSampler> set, Map<MetricSampler, List<RecordedDeviation>> map, MethodProfilerResults methodprofilerresults) {
        try {
            Files.createDirectories(MetricsPersister.PROFILING_RESULTS_DIR);
        } catch (IOException ioexception) {
            throw new UncheckedIOException(ioexception);
        }

        try {
            Path path = Files.createTempDirectory("minecraft-profiling");

            path.toFile().deleteOnExit();
            Files.createDirectories(MetricsPersister.PROFILING_RESULTS_DIR);
            Path path1 = path.resolve(this.rootFolderName);
            Path path2 = path1.resolve("metrics");

            this.a(set, path2);
            if (!map.isEmpty()) {
                this.a(map, path1.resolve("deviations"));
            }

            this.a(methodprofilerresults, path1);
            return path;
        } catch (IOException ioexception1) {
            throw new UncheckedIOException(ioexception1);
        }
    }

    private void a(Set<MetricSampler> set, Path path) {
        if (set.isEmpty()) {
            throw new IllegalArgumentException("Expected at least one sampler to persist");
        } else {
            Map<MetricCategory, List<MetricSampler>> map = (Map) set.stream().collect(Collectors.groupingBy(MetricSampler::e));

            map.forEach((metriccategory, list) -> {
                this.a(metriccategory, list, path);
            });
        }
    }

    private void a(MetricCategory metriccategory, List<MetricSampler> list, Path path) {
        String s = metriccategory.a();
        Path path1 = path.resolve(SystemUtils.a(s, MinecraftKey::b) + ".csv");
        BufferedWriter bufferedwriter = null;

        try {
            Files.createDirectories(path1.getParent());
            bufferedwriter = Files.newBufferedWriter(path1, StandardCharsets.UTF_8);
            CSVWriter.a csvwriter_a = CSVWriter.a();

            csvwriter_a.a("@tick");
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                MetricSampler metricsampler = (MetricSampler) iterator.next();

                csvwriter_a.a(metricsampler.d());
            }

            CSVWriter csvwriter = csvwriter_a.a((Writer) bufferedwriter);
            List<MetricSampler.b> list1 = (List) list.stream().map(MetricSampler::f).collect(Collectors.toList());
            int i = list1.stream().mapToInt(MetricSampler.b::a).summaryStatistics().getMin();
            int j = list1.stream().mapToInt(MetricSampler.b::b).summaryStatistics().getMax();

            for (int k = i; k <= j; ++k) {
                Stream<String> stream = list1.stream().map((metricsampler_b) -> {
                    return String.valueOf(metricsampler_b.a(k));
                });
                Object[] aobject = Stream.concat(Stream.of(String.valueOf(k)), stream).toArray((l) -> {
                    return new String[l];
                });

                csvwriter.a(aobject);
            }

            MetricsPersister.LOGGER.info("Flushed metrics to {}", path1);
        } catch (Exception exception) {
            MetricsPersister.LOGGER.error("Could not save profiler results to {}", path1, exception);
        } finally {
            IOUtils.closeQuietly(bufferedwriter);
        }

    }

    private void a(Map<MetricSampler, List<RecordedDeviation>> map, Path path) {
        DateTimeFormatter datetimeformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss.SSS", Locale.UK).withZone(ZoneId.systemDefault());

        map.forEach((metricsampler, list) -> {
            list.forEach((recordeddeviation) -> {
                String s = datetimeformatter.format(recordeddeviation.timestamp);
                Path path1 = path.resolve(SystemUtils.a(metricsampler.d(), MinecraftKey::b)).resolve(String.format(Locale.ROOT, "%d@%s.txt", recordeddeviation.tick, s));

                recordeddeviation.profilerResultAtTick.a(path1);
            });
        });
    }

    private void a(MethodProfilerResults methodprofilerresults, Path path) {
        methodprofilerresults.a(path.resolve("profiling.txt"));
    }
}
