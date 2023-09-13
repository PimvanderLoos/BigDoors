package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.List;
import java.util.function.Function;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.EntityHuman;

public class CommandList {

    public CommandList() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("list").executes((commandcontext) -> {
            return listPlayers((CommandListenerWrapper) commandcontext.getSource());
        })).then(net.minecraft.commands.CommandDispatcher.literal("uuids").executes((commandcontext) -> {
            return listPlayersWithUuids((CommandListenerWrapper) commandcontext.getSource());
        })));
    }

    private static int listPlayers(CommandListenerWrapper commandlistenerwrapper) {
        return format(commandlistenerwrapper, EntityHuman::getDisplayName);
    }

    private static int listPlayersWithUuids(CommandListenerWrapper commandlistenerwrapper) {
        return format(commandlistenerwrapper, (entityplayer) -> {
            return new ChatMessage("commands.list.nameAndId", new Object[]{entityplayer.getName(), entityplayer.getGameProfile().getId()});
        });
    }

    private static int format(CommandListenerWrapper commandlistenerwrapper, Function<EntityPlayer, IChatBaseComponent> function) {
        PlayerList playerlist = commandlistenerwrapper.getServer().getPlayerList();
        List<EntityPlayer> list = playerlist.getPlayers();
        IChatBaseComponent ichatbasecomponent = ChatComponentUtils.formatList(list, function);

        commandlistenerwrapper.sendSuccess(new ChatMessage("commands.list.players", new Object[]{list.size(), playerlist.getMaxPlayers(), ichatbasecomponent}), false);
        return list.size();
    }
}
