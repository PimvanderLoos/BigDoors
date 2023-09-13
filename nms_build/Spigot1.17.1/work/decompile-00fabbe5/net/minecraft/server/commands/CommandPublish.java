package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.util.HttpUtilities;
import net.minecraft.world.level.EnumGamemode;

public class CommandPublish {

    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new ChatMessage("commands.publish.failed"));
    private static final DynamicCommandExceptionType ERROR_ALREADY_PUBLISHED = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.publish.alreadyPublished", new Object[]{object});
    });

    public CommandPublish() {}

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("publish").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(4);
        })).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), HttpUtilities.a());
        })).then(net.minecraft.commands.CommandDispatcher.a("port", (ArgumentType) IntegerArgumentType.integer(0, 65535)).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), IntegerArgumentType.getInteger(commandcontext, "port"));
        })));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, int i) throws CommandSyntaxException {
        if (commandlistenerwrapper.getServer().o()) {
            throw CommandPublish.ERROR_ALREADY_PUBLISHED.create(commandlistenerwrapper.getServer().getPort());
        } else if (!commandlistenerwrapper.getServer().a((EnumGamemode) null, false, i)) {
            throw CommandPublish.ERROR_FAILED.create();
        } else {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.publish.success", new Object[]{i}), true);
            return i;
        }
    }
}
