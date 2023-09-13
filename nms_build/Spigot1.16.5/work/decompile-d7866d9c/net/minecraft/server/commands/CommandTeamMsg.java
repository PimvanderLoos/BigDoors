package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
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

    private static final ChatModifier a = ChatModifier.a.setChatHoverable(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, new ChatMessage("chat.type.team.hover"))).setChatClickable(new ChatClickable(ChatClickable.EnumClickAction.SUGGEST_COMMAND, "/teammsg "));
    private static final SimpleCommandExceptionType b = new SimpleCommandExceptionType(new ChatMessage("commands.teammsg.failed.noteam"));

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        LiteralCommandNode<CommandListenerWrapper> literalcommandnode = commanddispatcher.register((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("teammsg").then(net.minecraft.commands.CommandDispatcher.a("message", (ArgumentType) ArgumentChat.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentChat.a(commandcontext, "message"));
        })));

        commanddispatcher.register((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("tm").redirect(literalcommandnode));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, IChatBaseComponent ichatbasecomponent) throws CommandSyntaxException {
        Entity entity = commandlistenerwrapper.g();
        ScoreboardTeam scoreboardteam = (ScoreboardTeam) entity.getScoreboardTeam();

        if (scoreboardteam == null) {
            throw CommandTeamMsg.b.create();
        } else {
            IChatMutableComponent ichatmutablecomponent = scoreboardteam.d().c(CommandTeamMsg.a);
            List<EntityPlayer> list = commandlistenerwrapper.getServer().getPlayerList().getPlayers();
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                if (entityplayer == entity) {
                    entityplayer.sendMessage(new ChatMessage("chat.type.team.sent", new Object[]{ichatmutablecomponent, commandlistenerwrapper.getScoreboardDisplayName(), ichatbasecomponent}), entity.getUniqueID());
                } else if (entityplayer.getScoreboardTeam() == scoreboardteam) {
                    entityplayer.sendMessage(new ChatMessage("chat.type.team.text", new Object[]{ichatmutablecomponent, commandlistenerwrapper.getScoreboardDisplayName(), ichatbasecomponent}), entity.getUniqueID());
                }
            }

            return list.size();
        }
    }
}
