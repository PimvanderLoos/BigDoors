package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.SystemUtils;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.CustomFunction;
import net.minecraft.commands.ICommandListener;
import net.minecraft.commands.arguments.item.ArgumentTag;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.CustomFunctionData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.TimeRange;
import net.minecraft.util.profiling.MethodProfilerResults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandDebug {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final SimpleCommandExceptionType ERROR_NOT_RUNNING = new SimpleCommandExceptionType(new ChatMessage("commands.debug.notRunning"));
    private static final SimpleCommandExceptionType ERROR_ALREADY_RUNNING = new SimpleCommandExceptionType(new ChatMessage("commands.debug.alreadyRunning"));

    public CommandDebug() {}

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("debug").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(3);
        })).then(net.minecraft.commands.CommandDispatcher.a("start").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource());
        }))).then(net.minecraft.commands.CommandDispatcher.a("stop").executes((commandcontext) -> {
            return b((CommandListenerWrapper) commandcontext.getSource());
        }))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("function").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(3);
        })).then(net.minecraft.commands.CommandDispatcher.a("name", (ArgumentType) ArgumentTag.a()).suggests(CommandFunction.SUGGEST_FUNCTION).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentTag.a(commandcontext, "name"));
        }))));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
        MinecraftServer minecraftserver = commandlistenerwrapper.getServer();

        if (minecraftserver.bb()) {
            throw CommandDebug.ERROR_ALREADY_RUNNING.create();
        } else {
            minecraftserver.bc();
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.debug.started"), true);
            return 0;
        }
    }

    private static int b(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
        MinecraftServer minecraftserver = commandlistenerwrapper.getServer();

        if (!minecraftserver.bb()) {
            throw CommandDebug.ERROR_NOT_RUNNING.create();
        } else {
            MethodProfilerResults methodprofilerresults = minecraftserver.bd();
            double d0 = (double) methodprofilerresults.g() / (double) TimeRange.NANOSECONDS_PER_SECOND;
            double d1 = (double) methodprofilerresults.f() / d0;

            commandlistenerwrapper.sendMessage(new ChatMessage("commands.debug.stopped", new Object[]{String.format(Locale.ROOT, "%.2f", d0), methodprofilerresults.f(), String.format("%.2f", d1)}), true);
            return (int) d1;
        }
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Collection<CustomFunction> collection) {
        int i = 0;
        MinecraftServer minecraftserver = commandlistenerwrapper.getServer();
        SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
        Date date = new Date();
        String s = "debug-trace-" + simpledateformat.format(date) + ".txt";

        try {
            Path path = minecraftserver.c("debug").toPath();

            Files.createDirectories(path);
            BufferedWriter bufferedwriter = Files.newBufferedWriter(path.resolve(s), StandardCharsets.UTF_8);

            try {
                PrintWriter printwriter = new PrintWriter(bufferedwriter);

                CustomFunction customfunction;
                CommandDebug.a commanddebug_a;

                for (Iterator iterator = collection.iterator(); iterator.hasNext(); i += commandlistenerwrapper.getServer().getFunctionData().a(customfunction, commandlistenerwrapper.a((ICommandListener) commanddebug_a).b(2), commanddebug_a)) {
                    customfunction = (CustomFunction) iterator.next();
                    printwriter.println(customfunction.a());
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
            commandlistenerwrapper.sendFailureMessage(new ChatMessage("commands.debug.function.traceFailed"));
        }

        if (collection.size() == 1) {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.debug.function.success.single", new Object[]{i, ((CustomFunction) collection.iterator().next()).a(), s}), true);
        } else {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.debug.function.success.multiple", new Object[]{i, collection.size(), s}), true);
        }

        return i;
    }

    private static class a implements ICommandListener, CustomFunctionData.c {

        public static final int INDENT_OFFSET = 1;
        private final PrintWriter output;
        private int lastIndent;
        private boolean waitingForResult;

        a(PrintWriter printwriter) {
            this.output = printwriter;
        }

        private void a(int i) {
            this.b(i);
            this.lastIndent = i;
        }

        private void b(int i) {
            for (int j = 0; j < i + 1; ++j) {
                this.output.write("    ");
            }

        }

        private void e() {
            if (this.waitingForResult) {
                this.output.println();
                this.waitingForResult = false;
            }

        }

        @Override
        public void a(int i, String s) {
            this.e();
            this.a(i);
            this.output.print("[C] ");
            this.output.print(s);
            this.waitingForResult = true;
        }

        @Override
        public void a(int i, String s, int j) {
            if (this.waitingForResult) {
                this.output.print(" -> ");
                this.output.println(j);
                this.waitingForResult = false;
            } else {
                this.a(i);
                this.output.print("[R = ");
                this.output.print(j);
                this.output.print("] ");
                this.output.println(s);
            }

        }

        @Override
        public void a(int i, MinecraftKey minecraftkey, int j) {
            this.e();
            this.a(i);
            this.output.print("[F] ");
            this.output.print(minecraftkey);
            this.output.print(" size=");
            this.output.println(j);
        }

        @Override
        public void b(int i, String s) {
            this.e();
            this.a(i + 1);
            this.output.print("[E] ");
            this.output.print(s);
        }

        @Override
        public void sendMessage(IChatBaseComponent ichatbasecomponent, UUID uuid) {
            this.e();
            this.b(this.lastIndent + 1);
            this.output.print("[M] ");
            if (uuid != SystemUtils.NIL_UUID) {
                this.output.print(uuid);
                this.output.print(": ");
            }

            this.output.println(ichatbasecomponent.getString());
        }

        @Override
        public boolean shouldSendSuccess() {
            return true;
        }

        @Override
        public boolean shouldSendFailure() {
            return true;
        }

        @Override
        public boolean shouldBroadcastCommands() {
            return false;
        }

        @Override
        public boolean c_() {
            return true;
        }
    }
}
