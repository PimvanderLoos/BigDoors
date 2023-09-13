package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentChat;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.server.players.PlayerList;

public class CommandSay {

    public CommandSay() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("say").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.argument("message", ArgumentChat.message()).executes((commandcontext) -> {
            ArgumentChat.resolveChatMessage(commandcontext, "message", (playerchatmessage) -> {
                CommandListenerWrapper commandlistenerwrapper = (CommandListenerWrapper) commandcontext.getSource();
                PlayerList playerlist = commandlistenerwrapper.getServer().getPlayerList();

                playerlist.broadcastChatMessage(playerchatmessage, commandlistenerwrapper, ChatMessageType.bind(ChatMessageType.SAY_COMMAND, commandlistenerwrapper));
            });
            return 1;
        })));
    }
}
