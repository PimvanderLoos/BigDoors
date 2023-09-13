package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentChat;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.players.PlayerList;

public class CommandTell {

    public CommandTell() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        LiteralCommandNode<CommandListenerWrapper> literalcommandnode = commanddispatcher.register((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("msg").then(net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentEntity.players()).then(net.minecraft.commands.CommandDispatcher.argument("message", ArgumentChat.message()).executes((commandcontext) -> {
            Collection<EntityPlayer> collection = ArgumentEntity.getPlayers(commandcontext, "targets");

            if (!collection.isEmpty()) {
                ArgumentChat.resolveChatMessage(commandcontext, "message", (playerchatmessage) -> {
                    sendMessage((CommandListenerWrapper) commandcontext.getSource(), collection, playerchatmessage);
                });
            }

            return collection.size();
        }))));

        commanddispatcher.register((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("tell").redirect(literalcommandnode));
        commanddispatcher.register((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("w").redirect(literalcommandnode));
    }

    private static void sendMessage(CommandListenerWrapper commandlistenerwrapper, Collection<EntityPlayer> collection, PlayerChatMessage playerchatmessage) {
        ChatMessageType.a chatmessagetype_a = ChatMessageType.bind(ChatMessageType.MSG_COMMAND_INCOMING, commandlistenerwrapper);
        OutgoingChatMessage outgoingchatmessage = OutgoingChatMessage.create(playerchatmessage);
        boolean flag = false;

        boolean flag1;

        for (Iterator iterator = collection.iterator(); iterator.hasNext(); flag |= flag1 && playerchatmessage.isFullyFiltered()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();
            ChatMessageType.a chatmessagetype_a1 = ChatMessageType.bind(ChatMessageType.MSG_COMMAND_OUTGOING, commandlistenerwrapper).withTargetName(entityplayer.getDisplayName());

            commandlistenerwrapper.sendChatMessage(outgoingchatmessage, false, chatmessagetype_a1);
            flag1 = commandlistenerwrapper.shouldFilterMessageTo(entityplayer);
            entityplayer.sendChatMessage(outgoingchatmessage, flag1, chatmessagetype_a);
        }

        if (flag) {
            commandlistenerwrapper.sendSystemMessage(PlayerList.CHAT_FILTERED_FULL);
        }

    }
}
