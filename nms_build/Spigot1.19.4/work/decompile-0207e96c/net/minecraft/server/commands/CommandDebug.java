package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.logging.LogUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import net.minecraft.SystemUtils;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.CustomFunction;
import net.minecraft.commands.ICommandListener;
import net.minecraft.commands.arguments.item.ArgumentTag;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.CustomFunctionData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.TimeRange;
import net.minecraft.util.profiling.MethodProfilerResults;
import org.slf4j.Logger;

public class CommandDebug {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final SimpleCommandExceptionType ERROR_NOT_RUNNING = new SimpleCommandExceptionType(IChatBaseComponent.translatable("commands.debug.notRunning"));
    private static final SimpleCommandExceptionType ERROR_ALREADY_RUNNING = new SimpleCommandExceptionType(IChatBaseComponent.translatable("commands.debug.alreadyRunning"));

    public CommandDebug() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("debug").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(3);
        })).then(net.minecraft.commands.CommandDispatcher.literal("start").executes((commandcontext) -> {
            return start((CommandListenerWrapper) commandcontext.getSource());
        }))).then(net.minecraft.commands.CommandDispatcher.literal("stop").executes((commandcontext) -> {
            return stop((CommandListenerWrapper) commandcontext.getSource());
        }))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("function").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(3);
        })).then(net.minecraft.commands.CommandDispatcher.argument("name", ArgumentTag.functions()).suggests(CommandFunction.SUGGEST_FUNCTION).executes((commandcontext) -> {
            return traceFunction((CommandListenerWrapper) commandcontext.getSource(), ArgumentTag.getFunctions(commandcontext, "name"));
        }))));
    }

    private static int start(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
        MinecraftServer minecraftserver = commandlistenerwrapper.getServer();

        if (minecraftserver.isTimeProfilerRunning()) {
            throw CommandDebug.ERROR_ALREADY_RUNNING.create();
        } else {
            minecraftserver.startTimeProfiler();
            commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable("commands.debug.started"), true);
            return 0;
        }
    }

    private static int stop(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
        MinecraftServer minecraftserver = commandlistenerwrapper.getServer();

        if (!minecraftserver.isTimeProfilerRunning()) {
            throw CommandDebug.ERROR_NOT_RUNNING.create();
        } else {
            MethodProfilerResults methodprofilerresults = minecraftserver.stopTimeProfiler();
            double d0 = (double) methodprofilerresults.getNanoDuration() / (double) TimeRange.NANOSECONDS_PER_SECOND;
            double d1 = (double) methodprofilerresults.getTickDuration() / d0;

            commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable("commands.debug.stopped", String.format(Locale.ROOT, "%.2f", d0), methodprofilerresults.getTickDuration(), String.format(Locale.ROOT, "%.2f", d1)), true);
            return (int) d1;
        }
    }

    private static int traceFunction(CommandListenerWrapper commandlistenerwrapper, Collection<CustomFunction> collection) {
        int i = 0;
        MinecraftServer minecraftserver = commandlistenerwrapper.getServer();
        String s = "debug-trace-" + SystemUtils.getFilenameFormattedDateTime() + ".txt";

        try {
            Path path = minecraftserver.getFile("debug").toPath();

            Files.createDirectories(path);
            BufferedWriter bufferedwriter = Files.newBufferedWriter(path.resolve(s), StandardCharsets.UTF_8);

            try {
                PrintWriter printwriter = new PrintWriter(bufferedwriter);

                CustomFunction customfunction;
                CommandDebug.a commanddebug_a;

                for (Iterator iterator = collection.iterator(); iterator.hasNext(); i += commandlistenerwrapper.getServer().getFunctions().execute(customfunction, commandlistenerwrapper.withSource(commanddebug_a).withMaximumPermission(2), commanddebug_a)) {
                    customfunction = (CustomFunction) iterator.next();
                    printwriter.println(customfunction.getId());
                    commanddebug_a = new CommandDebug.a(printwriter);
                }
            } catch (Throwable throwable) {
                if (bufferedwriter != null) {
                    try {
                        bufferedwriter.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                }

                throw throwable;
            }

            if (bufferedwriter != null) {
                bufferedwriter.close();
            }
        } catch (IOException | UncheckedIOException uncheckedioexception) {
            CommandDebug.LOGGER.warn("Tracing failed", uncheckedioexception);
            commandlistenerwrapper.sendFailure(IChatBaseComponent.translatable("commands.debug.function.traceFailed"));
        }

        if (collection.size() == 1) {
            commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable("commands.debug.function.success.single", i, ((CustomFunction) collection.iterator().next()).getId(), s), true);
        } else {
            commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable("commands.debug.function.success.multiple", i, collection.size(), s), true);
        }

        return i;
    }

    private static class a implements CustomFunctionData.c, ICommandListener {

        public static final int INDENT_OFFSET = 1;
        private final PrintWriter output;
        private int lastIndent;
        private boolean waitingForResult;

        a(PrintWriter printwriter) {
            this.output = printwriter;
        }

        private void indentAndSave(int i) {
            this.printIndent(i);
            this.lastIndent = i;
        }

        private void printIndent(int i) {
            for (int j = 0; j < i + 1; ++j) {
                this.output.write("    ");
            }

        }

        private void newLine() {
            if (this.waitingForResult) {
                this.output.println();
                this.waitingForResult = false;
            }

        }

        @Override
        public void onCommand(int i, String s) {
            this.newLine();
            this.indentAndSave(i);
            this.output.print("[C] ");
            this.output.print(s);
            this.waitingForResult = true;
        }

        @Override
        public void onReturn(int i, String s, int j) {
            if (this.waitingForResult) {
                this.output.print(" -> ");
                this.output.println(j);
                this.waitingForResult = false;
            } else {
                this.indentAndSave(i);
                this.output.print("[R = ");
                this.output.print(j);
                this.output.print("] ");
                this.output.println(s);
            }

        }

        @Override
        public void onCall(int i, MinecraftKey minecraftkey, int j) {
            this.newLine();
            this.indentAndSave(i);
            this.output.print("[F] ");
            this.output.print(minecraftkey);
            this.output.print(" size=");
            this.output.println(j);
        }

        @Override
        public void onError(int i, String s) {
            this.newLine();
            this.indentAndSave(i + 1);
            this.output.print("[E] ");
            this.output.print(s);
        }

        @Override
        public void sendSystemMessage(IChatBaseComponent ichatbasecomponent) {
            this.newLine();
            this.printIndent(this.lastIndent + 1);
            this.output.print("[M] ");
            this.output.println(ichatbasecomponent.getString());
        }

        @Override
        public boolean acceptsSuccess() {
            return true;
        }

        @Override
        public boolean acceptsFailure() {
            return true;
        }

        @Override
        public boolean shouldInformAdmins() {
            return false;
        }

        @Override
        public boolean alwaysAccepts() {
            return true;
        }
    }
}
