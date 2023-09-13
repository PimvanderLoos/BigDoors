package net.minecraft.server.commands;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.profiling.MethodProfilerResults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandDebug {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final SimpleCommandExceptionType b = new SimpleCommandExceptionType(new ChatMessage("commands.debug.notRunning"));
    private static final SimpleCommandExceptionType c = new SimpleCommandExceptionType(new ChatMessage("commands.debug.alreadyRunning"));
    @Nullable
    private static final FileSystemProvider d = (FileSystemProvider) FileSystemProvider.installedProviders().stream().filter((filesystemprovider) -> {
        return filesystemprovider.getScheme().equalsIgnoreCase("jar");
    }).findFirst().orElse((Object) null);

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("debug").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(3);
        })).then(net.minecraft.commands.CommandDispatcher.a("start").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource());
        }))).then(net.minecraft.commands.CommandDispatcher.a("stop").executes((commandcontext) -> {
            return b((CommandListenerWrapper) commandcontext.getSource());
        }))).then(net.minecraft.commands.CommandDispatcher.a("report").executes((commandcontext) -> {
            return c((CommandListenerWrapper) commandcontext.getSource());
        })));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
        MinecraftServer minecraftserver = commandlistenerwrapper.getServer();

        if (minecraftserver.aS()) {
            throw CommandDebug.c.create();
        } else {
            minecraftserver.aT();
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.debug.started", new Object[]{"Started the debug profiler. Type '/debug stop' to stop it."}), true);
            return 0;
        }
    }

    private static int b(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
        MinecraftServer minecraftserver = commandlistenerwrapper.getServer();

        if (!minecraftserver.aS()) {
            throw CommandDebug.b.create();
        } else {
            MethodProfilerResults methodprofilerresults = minecraftserver.aU();
            File file = new File(minecraftserver.c("debug"), "profile-results-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt");

            methodprofilerresults.a(file);
            float f = (float) methodprofilerresults.g() / 1.0E9F;
            float f1 = (float) methodprofilerresults.f() / f;

            commandlistenerwrapper.sendMessage(new ChatMessage("commands.debug.stopped", new Object[]{String.format(Locale.ROOT, "%.2f", f), methodprofilerresults.f(), String.format("%.2f", f1)}), true);
            return MathHelper.d(f1);
        }
    }

    private static int c(CommandListenerWrapper commandlistenerwrapper) {
        MinecraftServer minecraftserver = commandlistenerwrapper.getServer();
        String s = "debug-report-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date());

        try {
            Path path = minecraftserver.c("debug").toPath();

            Files.createDirectories(path);
            Path path1;

            if (!SharedConstants.d && CommandDebug.d != null) {
                path1 = path.resolve(s + ".zip");
                FileSystem filesystem = CommandDebug.d.newFileSystem(path1, ImmutableMap.of("create", "true"));
                Throwable throwable = null;

                try {
                    minecraftserver.a(filesystem.getPath("/"));
                } catch (Throwable throwable1) {
                    throwable = throwable1;
                    throw throwable1;
                } finally {
                    if (filesystem != null) {
                        if (throwable != null) {
                            try {
                                filesystem.close();
                            } catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        } else {
                            filesystem.close();
                        }
                    }

                }
            } else {
                path1 = path.resolve(s);
                minecraftserver.a(path1);
            }

            commandlistenerwrapper.sendMessage(new ChatMessage("commands.debug.reportSaved", new Object[]{s}), false);
            return 1;
        } catch (IOException ioexception) {
            CommandDebug.LOGGER.error("Failed to save debug dump", ioexception);
            commandlistenerwrapper.sendFailureMessage(new ChatMessage("commands.debug.reportFailed"));
            return 0;
        }
    }
}
