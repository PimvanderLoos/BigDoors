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
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.ChatModifier;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.ScoreboardTeam;

public class CommandTeamMsg {

    private static final ChatModifier SUGGEST_STYLE = ChatModifier.EMPTY.withHoverEvent(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, new ChatMessage("chat.type.team.hover"))).withClickEvent(new ChatClickable(ChatClickable.EnumClickAction.SUGGEST_COMMAND, "/teammsg "));
    private static final SimpleCommandExceptionType ERROR_NOT_ON_TEAM = new SimpleCommandExceptionType(new ChatMessage("commands.teammsg.failed.noteam"));

    public CommandTeamMsg() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        LiteralCommandNode<CommandListenerWrapper> literalcommandnode = commanddispatcher.register((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("teammsg").then(net.minecraft.commands.CommandDispatcher.argument("message", ArgumentChat.message()).executes((commandcontext) -> {
            return sendMessage((CommandListenerWrapper) commandcontext.getSource(), ArgumentChat.getMessage(commandcontext, "message"));
        })));

        commanddispatcher.register((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("tm").redirect(literalcommandnode));
    }

    private static int sendMessage(CommandListenerWrapper commandlistenerwrapper, IChatBaseComponent ichatbasecomponent) throws CommandSyntaxException {
        Entity entity = commandlistenerwrapper.getEntityOrException();
        ScoreboardTeam scoreboardteam = (ScoreboardTeam) entity.getTeam();

        if (scoreboardteam == null) {
            throw CommandTeamMsg.ERROR_NOT_ON_TEAM.create();
        } else {
            IChatMutableComponent ichatmutablecomponent = scoreboardteam.getFormattedDisplayName().withStyle(CommandTeamMsg.SUGGEST_STYLE);
            List<EntityPlayer> list = commandlistenerwrapper.getServer().getPlayerList().getPlayers();
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                if (entityplayer == entity) {
                    entityplayer.sendMessage(new ChatMessage("chat.type.team.sent", new Object[]{ichatmutablecomponent, commandlistenerwrapper.getDisplayName(), ichatbasecomponent}), entity.getUUID());
                } else if (entityplayer.getTeam() == scoreboardteam) {
                    entityplayer.sendMessage(new ChatMessage("chat.type.team.text", new Object[]{ichatmutablecomponent, commandlistenerwrapper.getDisplayName(), ichatbasecomponent}), entity.getUUID());
                }
            }

            return list.size();
        }
    }
}
