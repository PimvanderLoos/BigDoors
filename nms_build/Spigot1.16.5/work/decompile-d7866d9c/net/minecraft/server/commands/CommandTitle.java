package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentChatComponent;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutTitle;
import net.minecraft.server.level.EntityPlayer;

public class CommandTitle {

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("title").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("targets", (ArgumentType) ArgumentEntity.d()).then(net.minecraft.commands.CommandDispatcher.a("clear").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"));
        }))).then(net.minecraft.commands.CommandDispatcher.a("reset").executes((commandcontext) -> {
            return b((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"));
        }))).then(net.minecraft.commands.CommandDispatcher.a("title").then(net.minecraft.commands.CommandDispatcher.a("title", (ArgumentType) ArgumentChatComponent.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), ArgumentChatComponent.a(commandcontext, "title"), PacketPlayOutTitle.EnumTitleAction.TITLE);
        })))).then(net.minecraft.commands.CommandDispatcher.a("subtitle").then(net.minecraft.commands.CommandDispatcher.a("title", (ArgumentType) ArgumentChatComponent.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), ArgumentChatComponent.a(commandcontext, "title"), PacketPlayOutTitle.EnumTitleAction.SUBTITLE);
        })))).then(net.minecraft.commands.CommandDispatcher.a("actionbar").then(net.minecraft.commands.CommandDispatcher.a("title", (ArgumentType) ArgumentChatComponent.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), ArgumentChatComponent.a(commandcontext, "title"), PacketPlayOutTitle.EnumTitleAction.ACTIONBAR);
        })))).then(net.minecraft.commands.CommandDispatcher.a("times").then(net.minecraft.commands.CommandDispatcher.a("fadeIn", (ArgumentType) IntegerArgumentType.integer(0)).then(net.minecraft.commands.CommandDispatcher.a("stay", (ArgumentType) IntegerArgumentType.integer(0)).then(net.minecraft.commands.CommandDispatcher.a("fadeOut", (ArgumentType) IntegerArgumentType.integer(0)).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), IntegerArgumentType.getInteger(commandcontext, "fadeIn"), IntegerArgumentType.getInteger(commandcontext, "stay"), IntegerArgumentType.getInteger(commandcontext, "fadeOut"));
        })))))));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Collection<EntityPlayer> collection) {
        PacketPlayOutTitle packetplayouttitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.CLEAR, (IChatBaseComponent) null);
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            entityplayer.playerConnection.sendPacket(packetplayouttitle);
        }

        if (collection.size() == 1) {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.title.cleared.single", new Object[]{((EntityPlayer) collection.iterator().next()).getScoreboardDisplayName()}), true);
        } else {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.title.cleared.multiple", new Object[]{collection.size()}), true);
        }

        return collection.size();
    }

    private static int b(CommandListenerWrapper commandlistenerwrapper, Collection<EntityPlayer> collection) {
        PacketPlayOutTitle packetplayouttitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.RESET, (IChatBaseComponent) null);
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            entityplayer.playerConnection.sendPacket(packetplayouttitle);
        }

        if (collection.size() == 1) {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.title.reset.single", new Object[]{((EntityPlayer) collection.iterator().next()).getScoreboardDisplayName()}), true);
        } else {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.title.reset.multiple", new Object[]{collection.size()}), true);
        }

        return collection.size();
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Collection<EntityPlayer> collection, IChatBaseComponent ichatbasecomponent, PacketPlayOutTitle.EnumTitleAction packetplayouttitle_enumtitleaction) throws CommandSyntaxException {
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            entityplayer.playerConnection.sendPacket(new PacketPlayOutTitle(packetplayouttitle_enumtitleaction, ChatComponentUtils.filterForDisplay(commandlistenerwrapper, ichatbasecomponent, entityplayer, 0)));
        }

        if (collection.size() == 1) {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.title.show." + packetplayouttitle_enumtitleaction.name().toLowerCase(Locale.ROOT) + ".single", new Object[]{((EntityPlayer) collection.iterator().next()).getScoreboardDisplayName()}), true);
        } else {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.title.show." + packetplayouttitle_enumtitleaction.name().toLowerCase(Locale.ROOT) + ".multiple", new Object[]{collection.size()}), true);
        }

        return collection.size();
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Collection<EntityPlayer> collection, int i, int j, int k) {
        PacketPlayOutTitle packetplayouttitle = new PacketPlayOutTitle(i, j, k);
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            entityplayer.playerConnection.sendPacket(packetplayouttitle);
        }

        if (collection.size() == 1) {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.title.times.single", new Object[]{((EntityPlayer) collection.iterator().next()).getScoreboardDisplayName()}), true);
        } else {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.title.times.multiple", new Object[]{collection.size()}), true);
        }

        return collection.size();
    }
}
