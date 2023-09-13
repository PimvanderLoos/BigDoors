package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentChat;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.players.IpBanEntry;
import net.minecraft.server.players.IpBanList;

public class CommandBanIp {

    public static final Pattern a = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    private static final SimpleCommandExceptionType b = new SimpleCommandExceptionType(new ChatMessage("commands.banip.invalid"));
    private static final SimpleCommandExceptionType c = new SimpleCommandExceptionType(new ChatMessage("commands.banip.failed"));

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("ban-ip").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(3);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("target", (ArgumentType) StringArgumentType.word()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), StringArgumentType.getString(commandcontext, "target"), (IChatBaseComponent) null);
        })).then(net.minecraft.commands.CommandDispatcher.a("reason", (ArgumentType) ArgumentChat.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), StringArgumentType.getString(commandcontext, "target"), ArgumentChat.a(commandcontext, "reason"));
        }))));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, String s, @Nullable IChatBaseComponent ichatbasecomponent) throws CommandSyntaxException {
        Matcher matcher = CommandBanIp.a.matcher(s);

        if (matcher.matches()) {
            return b(commandlistenerwrapper, s, ichatbasecomponent);
        } else {
            EntityPlayer entityplayer = commandlistenerwrapper.getServer().getPlayerList().getPlayer(s);

            if (entityplayer != null) {
                return b(commandlistenerwrapper, entityplayer.v(), ichatbasecomponent);
            } else {
                throw CommandBanIp.b.create();
            }
        }
    }

    private static int b(CommandListenerWrapper commandlistenerwrapper, String s, @Nullable IChatBaseComponent ichatbasecomponent) throws CommandSyntaxException {
        IpBanList ipbanlist = commandlistenerwrapper.getServer().getPlayerList().getIPBans();

        if (ipbanlist.a(s)) {
            throw CommandBanIp.c.create();
        } else {
            List<EntityPlayer> list = commandlistenerwrapper.getServer().getPlayerList().b(s);
            IpBanEntry ipbanentry = new IpBanEntry(s, (Date) null, commandlistenerwrapper.getName(), (Date) null, ichatbasecomponent == null ? null : ichatbasecomponent.getString());

            ipbanlist.add(ipbanentry);
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.banip.success", new Object[]{s, ipbanentry.getReason()}), true);
            if (!list.isEmpty()) {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.banip.info", new Object[]{list.size(), EntitySelector.a(list)}), true);
            }

            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                entityplayer.playerConnection.disconnect(new ChatMessage("multiplayer.disconnect.ip_banned"));
            }

            return list.size();
        }
    }
}
