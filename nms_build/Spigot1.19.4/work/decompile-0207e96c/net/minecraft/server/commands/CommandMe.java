package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentChat;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.server.players.PlayerList;

public class CommandMe {

    public CommandMe() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("me").then(net.minecraft.commands.CommandDispatcher.argument("action", ArgumentChat.message()).executes((commandcontext) -> {
            ArgumentChat.resolveChatMessage(commandcontext, "action", (playerchatmessage) -> {
                CommandListenerWrapper commandlistenerwrapper = (CommandListenerWrapper) commandcontext.getSource();
                PlayerList playerlist = commandlistenerwrapper.getServer().getPlayerList();

                playerlist.broadcastChatMessage(playerchatmessage, commandlistenerwrapper, ChatMessageType.bind(ChatMessageType.EMOTE_COMMAND, commandlistenerwrapper));
            });
            return 1;
        })));
    }
}
