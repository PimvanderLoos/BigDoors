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
import net.minecraft.network.chat.OutgoingPlayerChatMessage;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;

public class CommandTell {

    public CommandTell() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        LiteralCommandNode<CommandListenerWrapper> literalcommandnode = commanddispatcher.register((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("msg").then(net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentEntity.players()).then(net.minecraft.commands.CommandDispatcher.argument("message", ArgumentChat.message()).executes((commandcontext) -> {
            ArgumentChat.a argumentchat_a = ArgumentChat.getChatMessage(commandcontext, "message");

            try {
                return sendMessage((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), argumentchat_a);
            } catch (Exception exception) {
                argumentchat_a.consume((CommandListenerWrapper) commandcontext.getSource());
                throw exception;
            }
        }))));

        commanddispatcher.register((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("tell").redirect(literalcommandnode));
        commanddispatcher.register((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("w").redirect(literalcommandnode));
    }

    private static int sendMessage(CommandListenerWrapper commandlistenerwrapper, Collection<EntityPlayer> collection, ArgumentChat.a argumentchat_a) {
        ChatMessageType.a chatmessagetype_a = ChatMessageType.bind(ChatMessageType.MSG_COMMAND_INCOMING, commandlistenerwrapper);

        argumentchat_a.resolve(commandlistenerwrapper, (playerchatmessage) -> {
            OutgoingPlayerChatMessage outgoingplayerchatmessage = OutgoingPlayerChatMessage.create(playerchatmessage);
            boolean flag = playerchatmessage.isFullyFiltered();
            Entity entity = commandlistenerwrapper.getEntity();
            boolean flag1 = false;

            EntityPlayer entityplayer;
            boolean flag2;

            for (Iterator iterator = collection.iterator(); iterator.hasNext(); flag1 |= flag && flag2 && entityplayer != entity) {
                entityplayer = (EntityPlayer) iterator.next();
                ChatMessageType.a chatmessagetype_a1 = ChatMessageType.bind(ChatMessageType.MSG_COMMAND_OUTGOING, commandlistenerwrapper).withTargetName(entityplayer.getDisplayName());

                commandlistenerwrapper.sendChatMessage(outgoingplayerchatmessage, false, chatmessagetype_a1);
                flag2 = commandlistenerwrapper.shouldFilterMessageTo(entityplayer);
                entityplayer.sendChatMessage(outgoingplayerchatmessage, flag2, chatmessagetype_a);
            }

            if (flag1) {
                commandlistenerwrapper.sendSystemMessage(PlayerList.CHAT_FILTERED_FULL);
            }

            outgoingplayerchatmessage.sendHeadersToRemainingPlayers(commandlistenerwrapper.getServer().getPlayerList());
        });
        return collection.size();
    }
}
