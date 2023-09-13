package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.function.Consumer;
import net.minecraft.FileUtils;
import net.minecraft.SharedConstants;
import net.minecraft.SystemReport;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.FileZipper;
import net.minecraft.util.TimeRange;
import net.minecraft.util.profiling.MethodProfilerResults;
import net.minecraft.util.profiling.metrics.storage.MetricsPersister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PerfCommand {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final SimpleCommandExceptionType ERROR_NOT_RUNNING = new SimpleCommandExceptionType(new ChatMessage("commands.perf.notRunning"));
    private static final SimpleCommandExceptionType ERROR_ALREADY_RUNNING = new SimpleCommandExceptionType(new ChatMessage("commands.perf.alreadyRunning"));

    public PerfCommand() {}

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("perf").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(4);
        })).then(net.minecraft.commands.CommandDispatcher.a("start").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource());
        }))).then(net.minecraft.commands.CommandDispatcher.a("stop").executes((commandcontext) -> {
            return b((CommandListenerWrapper) commandcontext.getSource());
        })));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
        MinecraftServer minecraftserver = commandlistenerwrapper.getServer();

        if (minecraftserver.aQ()) {
            throw PerfCommand.ERROR_ALREADY_RUNNING.create();
        } else {
            Consumer<MethodProfilerResults> consumer = (methodprofilerresults) -> {
                a(commandlistenerwrapper, methodprofilerresults);
            };
            Consumer<Path> consumer1 = (path) -> {
                a(commandlistenerwrapper, path, minecraftserver);
            };

            minecraftserver.a(consumer, consumer1);
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.perf.started"), false);
            return 0;
        }
    }

    private static int b(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
        MinecraftServer minecraftserver = commandlistenerwrapper.getServer();

        if (!minecraftserver.aQ()) {
            throw PerfCommand.ERROR_NOT_RUNNING.create();
        } else {
            minecraftserver.aS();
            return 0;
        }
    }

    private static void a(CommandListenerWrapper commandlistenerwrapper, Path path, MinecraftServer minecraftserver) {
        String s = String.format("%s-%s-%s", (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()), minecraftserver.getSaveData().getName(), SharedConstants.getGameVersion().getId());

        String s1;

        try {
            s1 = FileUtils.a(MetricsPersister.PROFILING_RESULTS_DIR, s, ".zip");
        } catch (IOException ioexception) {
            commandlistenerwrapper.sendFailureMessage(new ChatMessage("commands.perf.reportFailed"));
            PerfCommand.LOGGER.error(ioexception);
            return;
        }

        FileZipper filezipper = new FileZipper(MetricsPersister.PROFILING_RESULTS_DIR.resolve(s1));

        try {
            filezipper.a(Paths.get("system.txt"), minecraftserver.b(new SystemReport()).a());
            filezipper.a(path);
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

        commandlistenerwrapper.sendMessage(new ChatMessage("commands.perf.reportSaved", new Object[]{s1}), false);
    }

    private static void a(CommandListenerWrapper commandlistenerwrapper, MethodProfilerResults methodprofilerresults) {
        int i = methodprofilerresults.f();
        double d0 = (double) methodprofilerresults.g() / (double) TimeRange.NANOSECONDS_PER_SECOND;

        commandlistenerwrapper.sendMessage(new ChatMessage("commands.perf.stopped", new Object[]{String.format(Locale.ROOT, "%.2f", d0), i, String.format(Locale.ROOT, "%.2f", (double) i / d0)}), false);
    }
}
