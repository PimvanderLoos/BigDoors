package net.minecraft.server;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Iterator;
import java.util.List;

public class CommandTeamMsg {

    private static final ChatModifier a = ChatModifier.b.setChatHoverable(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, new ChatMessage("chat.type.team.hover"))).setChatClickable(new ChatClickable(ChatClickable.EnumClickAction.SUGGEST_COMMAND, "/teammsg "));
    private static final SimpleCommandExceptionType b = new SimpleCommandExceptionType(new ChatMessage("commands.teammsg.failed.noteam"));

    public static void a(com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> com_mojang_brigadier_commanddispatcher) {
        LiteralCommandNode<CommandListenerWrapper> literalcommandnode = com_mojang_brigadier_commanddispatcher.register((LiteralArgumentBuilder) CommandDispatcher.a("teammsg").then(CommandDispatcher.a("message", (ArgumentType) ArgumentChat.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentChat.a(commandcontext, "message"));
        })));

        com_mojang_brigadier_commanddispatcher.register((LiteralArgumentBuilder) CommandDispatcher.a("tm").redirect(literalcommandnode));
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
