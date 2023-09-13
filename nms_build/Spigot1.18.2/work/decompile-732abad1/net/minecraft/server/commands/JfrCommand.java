package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.minecraft.EnumChatFormat;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.ChatClickable;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.ChatHoverable;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.util.profiling.jfr.Environment;
import net.minecraft.util.profiling.jfr.JvmProfiler;

public class JfrCommand {

    private static final SimpleCommandExceptionType START_FAILED = new SimpleCommandExceptionType(new ChatMessage("commands.jfr.start.failed"));
    private static final DynamicCommandExceptionType DUMP_FAILED = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.jfr.dump.failed", new Object[]{object});
    });

    private JfrCommand() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("jfr").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(4);
        })).then(net.minecraft.commands.CommandDispatcher.literal("start").executes((commandcontext) -> {
            return startJfr((CommandListenerWrapper) commandcontext.getSource());
        }))).then(net.minecraft.commands.CommandDispatcher.literal("stop").executes((commandcontext) -> {
            return stopJfr((CommandListenerWrapper) commandcontext.getSource());
        })));
    }

    private static int startJfr(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
        Environment environment = Environment.from(commandlistenerwrapper.getServer());

        if (!JvmProfiler.INSTANCE.start(environment)) {
            throw JfrCommand.START_FAILED.create();
        } else {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.jfr.started"), false);
            return 1;
        }
    }

    private static int stopJfr(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
        try {
            Path path = Paths.get(".").relativize(JvmProfiler.INSTANCE.stop().normalize());
            Path path1 = commandlistenerwrapper.getServer().isPublished() && !SharedConstants.IS_RUNNING_IN_IDE ? path : path.toAbsolutePath();
            IChatMutableComponent ichatmutablecomponent = (new ChatComponentText(path.toString())).withStyle(EnumChatFormat.UNDERLINE).withStyle((chatmodifier) -> {
                return chatmodifier.withClickEvent(new ChatClickable(ChatClickable.EnumClickAction.COPY_TO_CLIPBOARD, path1.toString())).withHoverEvent(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, new ChatMessage("chat.copy.click")));
            });

            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.jfr.stopped", new Object[]{ichatmutablecomponent}), false);
            return 1;
        } catch (Throwable throwable) {
            throw JfrCommand.DUMP_FAILED.create(throwable.getMessage());
        }
    }
}
