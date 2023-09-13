package net.minecraft.server.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentChat;
import net.minecraft.commands.arguments.ArgumentProfile;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.players.GameProfileBanEntry;
import net.minecraft.server.players.GameProfileBanList;

public class CommandBan {

    private static final SimpleCommandExceptionType ERROR_ALREADY_BANNED = new SimpleCommandExceptionType(new ChatMessage("commands.ban.failed"));

    public CommandBan() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("ban").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(3);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentProfile.gameProfile()).executes((commandcontext) -> {
            return banPlayers((CommandListenerWrapper) commandcontext.getSource(), ArgumentProfile.getGameProfiles(commandcontext, "targets"), (IChatBaseComponent) null);
        })).then(net.minecraft.commands.CommandDispatcher.argument("reason", ArgumentChat.message()).executes((commandcontext) -> {
            return banPlayers((CommandListenerWrapper) commandcontext.getSource(), ArgumentProfile.getGameProfiles(commandcontext, "targets"), ArgumentChat.getMessage(commandcontext, "reason"));
        }))));
    }

    private static int banPlayers(CommandListenerWrapper commandlistenerwrapper, Collection<GameProfile> collection, @Nullable IChatBaseComponent ichatbasecomponent) throws CommandSyntaxException {
        GameProfileBanList gameprofilebanlist = commandlistenerwrapper.getServer().getPlayerList().getBans();
        int i = 0;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            GameProfile gameprofile = (GameProfile) iterator.next();

            if (!gameprofilebanlist.isBanned(gameprofile)) {
                GameProfileBanEntry gameprofilebanentry = new GameProfileBanEntry(gameprofile, (Date) null, commandlistenerwrapper.getTextName(), (Date) null, ichatbasecomponent == null ? null : ichatbasecomponent.getString());

                gameprofilebanlist.add(gameprofilebanentry);
                ++i;
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.ban.success", new Object[]{ChatComponentUtils.getDisplayName(gameprofile), gameprofilebanentry.getReason()}), true);
                EntityPlayer entityplayer = commandlistenerwrapper.getServer().getPlayerList().getPlayer(gameprofile.getId());

                if (entityplayer != null) {
                    entityplayer.connection.disconnect(new ChatMessage("multiplayer.disconnect.banned"));
                }
            }
        }

        if (i == 0) {
            throw CommandBan.ERROR_ALREADY_BANNED.create();
        } else {
            return i;
        }
    }
}
