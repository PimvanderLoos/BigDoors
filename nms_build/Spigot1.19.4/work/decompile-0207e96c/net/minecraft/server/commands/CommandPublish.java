package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.GameModeArgument;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.util.HttpUtilities;
import net.minecraft.world.level.EnumGamemode;

public class CommandPublish {

    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(IChatBaseComponent.translatable("commands.publish.failed"));
    private static final DynamicCommandExceptionType ERROR_ALREADY_PUBLISHED = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("commands.publish.alreadyPublished", object);
    });

    public CommandPublish() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("publish").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(4);
        })).executes((commandcontext) -> {
            return publish((CommandListenerWrapper) commandcontext.getSource(), HttpUtilities.getAvailablePort(), false, (EnumGamemode) null);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("allowCommands", BoolArgumentType.bool()).executes((commandcontext) -> {
            return publish((CommandListenerWrapper) commandcontext.getSource(), HttpUtilities.getAvailablePort(), BoolArgumentType.getBool(commandcontext, "allowCommands"), (EnumGamemode) null);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("gamemode", GameModeArgument.gameMode()).executes((commandcontext) -> {
            return publish((CommandListenerWrapper) commandcontext.getSource(), HttpUtilities.getAvailablePort(), BoolArgumentType.getBool(commandcontext, "allowCommands"), GameModeArgument.getGameMode(commandcontext, "gamemode"));
        })).then(net.minecraft.commands.CommandDispatcher.argument("port", IntegerArgumentType.integer(0, 65535)).executes((commandcontext) -> {
            return publish((CommandListenerWrapper) commandcontext.getSource(), IntegerArgumentType.getInteger(commandcontext, "port"), BoolArgumentType.getBool(commandcontext, "allowCommands"), GameModeArgument.getGameMode(commandcontext, "gamemode"));
        })))));
    }

    private static int publish(CommandListenerWrapper commandlistenerwrapper, int i, boolean flag, @Nullable EnumGamemode enumgamemode) throws CommandSyntaxException {
        if (commandlistenerwrapper.getServer().isPublished()) {
            throw CommandPublish.ERROR_ALREADY_PUBLISHED.create(commandlistenerwrapper.getServer().getPort());
        } else if (!commandlistenerwrapper.getServer().publishServer(enumgamemode, flag, i)) {
            throw CommandPublish.ERROR_FAILED.create();
        } else {
            commandlistenerwrapper.sendSuccess(getSuccessMessage(i), true);
            return i;
        }
    }

    public static IChatMutableComponent getSuccessMessage(int i) {
        IChatMutableComponent ichatmutablecomponent = ChatComponentUtils.copyOnClickText(String.valueOf(i));

        return IChatBaseComponent.translatable("commands.publish.started", ichatmutablecomponent);
    }
}
