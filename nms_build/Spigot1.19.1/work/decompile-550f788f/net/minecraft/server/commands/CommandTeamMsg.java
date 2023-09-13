package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Iterator;
import java.util.List;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentChat;
import net.minecraft.network.chat.ChatClickable;
import net.minecraft.network.chat.ChatHoverable;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.ChatModifier;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.network.chat.OutgoingPlayerChatMessage;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.ScoreboardTeam;

public class CommandTeamMsg {

    private static final ChatModifier SUGGEST_STYLE = ChatModifier.EMPTY.withHoverEvent(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, IChatBaseComponent.translatable("chat.type.team.hover"))).withClickEvent(new ChatClickable(ChatClickable.EnumClickAction.SUGGEST_COMMAND, "/teammsg "));
    private static final SimpleCommandExceptionType ERROR_NOT_ON_TEAM = new SimpleCommandExceptionType(IChatBaseComponent.translatable("commands.teammsg.failed.noteam"));

    public CommandTeamMsg() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        LiteralCommandNode<CommandListenerWrapper> literalcommandnode = commanddispatcher.register((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("teammsg").then(net.minecraft.commands.CommandDispatcher.argument("message", ArgumentChat.message()).executes((commandcontext) -> {
            ArgumentChat.a argumentchat_a = ArgumentChat.getChatMessage(commandcontext, "message");

            try {
                return sendMessage((CommandListenerWrapper) commandcontext.getSource(), argumentchat_a);
            } catch (Exception exception) {
                argumentchat_a.consume((CommandListenerWrapper) commandcontext.getSource());
                throw exception;
            }
        })));

        commanddispatcher.register((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("tm").redirect(literalcommandnode));
    }

    private static int sendMessage(CommandListenerWrapper commandlistenerwrapper, ArgumentChat.a argumentchat_a) throws CommandSyntaxException {
        Entity entity = commandlistenerwrapper.getEntityOrException();
        ScoreboardTeam scoreboardteam = (ScoreboardTeam) entity.getTeam();

        if (scoreboardteam == null) {
            throw CommandTeamMsg.ERROR_NOT_ON_TEAM.create();
        } else {
            IChatMutableComponent ichatmutablecomponent = scoreboardteam.getFormattedDisplayName().withStyle(CommandTeamMsg.SUGGEST_STYLE);
            ChatMessageType.a chatmessagetype_a = ChatMessageType.bind(ChatMessageType.TEAM_MSG_COMMAND_INCOMING, commandlistenerwrapper).withTargetName(ichatmutablecomponent);
            ChatMessageType.a chatmessagetype_a1 = ChatMessageType.bind(ChatMessageType.TEAM_MSG_COMMAND_OUTGOING, commandlistenerwrapper).withTargetName(ichatmutablecomponent);
            List<EntityPlayer> list = commandlistenerwrapper.getServer().getPlayerList().getPlayers().stream().filter((entityplayer) -> {
                return entityplayer == entity || entityplayer.getTeam() == scoreboardteam;
            }).toList();

            argumentchat_a.resolve(commandlistenerwrapper, (playerchatmessage) -> {
                OutgoingPlayerChatMessage outgoingplayerchatmessage = OutgoingPlayerChatMessage.create(playerchatmessage);
                boolean flag = playerchatmessage.isFullyFiltered();
                boolean flag1 = false;

                EntityPlayer entityplayer;
                boolean flag2;

                for (Iterator iterator = list.iterator(); iterator.hasNext(); flag1 |= flag && flag2 && entityplayer != entity) {
                    entityplayer = (EntityPlayer) iterator.next();
                    ChatMessageType.a chatmessagetype_a2 = entityplayer == entity ? chatmessagetype_a1 : chatmessagetype_a;

                    flag2 = commandlistenerwrapper.shouldFilterMessageTo(entityplayer);
                    entityplayer.sendChatMessage(outgoingplayerchatmessage, flag2, chatmessagetype_a2);
                }

                if (flag1) {
                    commandlistenerwrapper.sendSystemMessage(PlayerList.CHAT_FILTERED_FULL);
                }

                outgoingplayerchatmessage.sendHeadersToRemainingPlayers(commandlistenerwrapper.getServer().getPlayerList());
            });
            return list.size();
        }
    }
}
