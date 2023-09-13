package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.function.Consumer;
import net.minecraft.FileUtils;
import net.minecraft.SharedConstants;
import net.minecraft.SystemReport;
import net.minecraft.SystemUtils;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.FileZipper;
import net.minecraft.util.TimeRange;
import net.minecraft.util.profiling.MethodProfilerResults;
import net.minecraft.util.profiling.MethodProfilerResultsEmpty;
import net.minecraft.util.profiling.metrics.storage.MetricsPersister;
import org.slf4j.Logger;

public class PerfCommand {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final SimpleCommandExceptionType ERROR_NOT_RUNNING = new SimpleCommandExceptionType(IChatBaseComponent.translatable("commands.perf.notRunning"));
    private static final SimpleCommandExceptionType ERROR_ALREADY_RUNNING = new SimpleCommandExceptionType(IChatBaseComponent.translatable("commands.perf.alreadyRunning"));

    public PerfCommand() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("perf").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(4);
        })).then(net.minecraft.commands.CommandDispatcher.literal("start").executes((commandcontext) -> {
            return startProfilingDedicatedServer((CommandListenerWrapper) commandcontext.getSource());
        }))).then(net.minecraft.commands.CommandDispatcher.literal("stop").executes((commandcontext) -> {
            return stopProfilingDedicatedServer((CommandListenerWrapper) commandcontext.getSource());
        })));
    }

    private static int startProfilingDedicatedServer(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
        MinecraftServer minecraftserver = commandlistenerwrapper.getServer();

        if (minecraftserver.isRecordingMetrics()) {
            throw PerfCommand.ERROR_ALREADY_RUNNING.create();
        } else {
            Consumer<MethodProfilerResults> consumer = (methodprofilerresults) -> {
                whenStopped(commandlistenerwrapper, methodprofilerresults);
            };
            Consumer<Path> consumer1 = (path) -> {
                saveResults(commandlistenerwrapper, path, minecraftserver);
            };

            minecraftserver.startRecordingMetrics(consumer, consumer1);
            commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable("commands.perf.started"), false);
            return 0;
        }
    }

    private static int stopProfilingDedicatedServer(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
        MinecraftServer minecraftserver = commandlistenerwrapper.getServer();

        if (!minecraftserver.isRecordingMetrics()) {
            throw PerfCommand.ERROR_NOT_RUNNING.create();
        } else {
            minecraftserver.finishRecordingMetrics();
            return 0;
        }
    }

    private static void saveResults(CommandListenerWrapper commandlistenerwrapper, Path path, MinecraftServer minecraftserver) {
        String s = String.format(Locale.ROOT, "%s-%s-%s", SystemUtils.getFilenameFormattedDateTime(), minecraftserver.getWorldData().getLevelName(), SharedConstants.getCurrentVersion().getId());

        String s1;

        try {
            s1 = FileUtils.findAvailableName(MetricsPersister.PROFILING_RESULTS_DIR, s, ".zip");
        } catch (IOException ioexception) {
            commandlistenerwrapper.sendFailure(IChatBaseComponent.translatable("commands.perf.reportFailed"));
            PerfCommand.LOGGER.error("Failed to create report name", ioexception);
            return;
        }

        FileZipper filezipper = new FileZipper(MetricsPersister.PROFILING_RESULTS_DIR.resolve(s1));

        try {
            filezipper.add(Paths.get("system.txt"), minecraftserver.fillSystemReport(new SystemReport()).toLineSeparatedString());
            filezipper.add(path);
        } catch (Throwable throwable) {
            try {
                filezipper.close();
            } catch (Throwable throwable1) {
                throwable.addSuppressed(throwable1);
            }

            throw throwable;
        }

        filezipper.close();

        try {
            org.apache.commons.io.FileUtils.forceDelete(path.toFile());
        } catch (IOException ioexception1) {
            PerfCommand.LOGGER.warn("Failed to delete temporary profiling file {}", path, ioexception1);
        }

        commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable("commands.perf.reportSaved", s1), false);
    }

    private static void whenStopped(CommandListenerWrapper commandlistenerwrapper, MethodProfilerResults methodprofilerresults) {
        if (methodprofilerresults != MethodProfilerResultsEmpty.EMPTY) {
            int i = methodprofilerresults.getTickDuration();
            double d0 = (double) methodprofilerresults.getNanoDuration() / (double) TimeRange.NANOSECONDS_PER_SECOND;

            commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable("commands.perf.stopped", String.format(Locale.ROOT, "%.2f", d0), i, String.format(Locale.ROOT, "%.2f", (double) i / d0)), false);
        }
    }
}
