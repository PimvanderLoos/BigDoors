package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentChatComponent;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.EntityPlayer;

public class CommandTitle {

    public CommandTitle() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("title").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentEntity.players()).then(net.minecraft.commands.CommandDispatcher.literal("clear").executes((commandcontext) -> {
            return clearTitle((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"));
        }))).then(net.minecraft.commands.CommandDispatcher.literal("reset").executes((commandcontext) -> {
            return resetTitle((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"));
        }))).then(net.minecraft.commands.CommandDispatcher.literal("title").then(net.minecraft.commands.CommandDispatcher.argument("title", ArgumentChatComponent.textComponent()).executes((commandcontext) -> {
            return showTitle((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), ArgumentChatComponent.getComponent(commandcontext, "title"), "title", ClientboundSetTitleTextPacket::new);
        })))).then(net.minecraft.commands.CommandDispatcher.literal("subtitle").then(net.minecraft.commands.CommandDispatcher.argument("title", ArgumentChatComponent.textComponent()).executes((commandcontext) -> {
            return showTitle((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), ArgumentChatComponent.getComponent(commandcontext, "title"), "subtitle", ClientboundSetSubtitleTextPacket::new);
        })))).then(net.minecraft.commands.CommandDispatcher.literal("actionbar").then(net.minecraft.commands.CommandDispatcher.argument("title", ArgumentChatComponent.textComponent()).executes((commandcontext) -> {
            return showTitle((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), ArgumentChatComponent.getComponent(commandcontext, "title"), "actionbar", ClientboundSetActionBarTextPacket::new);
        })))).then(net.minecraft.commands.CommandDispatcher.literal("times").then(net.minecraft.commands.CommandDispatcher.argument("fadeIn", IntegerArgumentType.integer(0)).then(net.minecraft.commands.CommandDispatcher.argument("stay", IntegerArgumentType.integer(0)).then(net.minecraft.commands.CommandDispatcher.argument("fadeOut", IntegerArgumentType.integer(0)).executes((commandcontext) -> {
            return setTimes((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), IntegerArgumentType.getInteger(commandcontext, "fadeIn"), IntegerArgumentType.getInteger(commandcontext, "stay"), IntegerArgumentType.getInteger(commandcontext, "fadeOut"));
        })))))));
    }

    private static int clearTitle(CommandListenerWrapper commandlistenerwrapper, Collection<EntityPlayer> collection) {
        ClientboundClearTitlesPacket clientboundcleartitlespacket = new ClientboundClearTitlesPacket(false);
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            entityplayer.connection.send(clientboundcleartitlespacket);
        }

        if (collection.size() == 1) {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.title.cleared.single", new Object[]{((EntityPlayer) collection.iterator().next()).getDisplayName()}), true);
        } else {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.title.cleared.multiple", new Object[]{collection.size()}), true);
        }

        return collection.size();
    }

    private static int resetTitle(CommandListenerWrapper commandlistenerwrapper, Collection<EntityPlayer> collection) {
        ClientboundClearTitlesPacket clientboundcleartitlespacket = new ClientboundClearTitlesPacket(true);
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            entityplayer.connection.send(clientboundcleartitlespacket);
        }

        if (collection.size() == 1) {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.title.reset.single", new Object[]{((EntityPlayer) collection.iterator().next()).getDisplayName()}), true);
        } else {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.title.reset.multiple", new Object[]{collection.size()}), true);
        }

        return collection.size();
    }

    private static int showTitle(CommandListenerWrapper commandlistenerwrapper, Collection<EntityPlayer> collection, IChatBaseComponent ichatbasecomponent, String s, Function<IChatBaseComponent, Packet<?>> function) throws CommandSyntaxException {
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            entityplayer.connection.send((Packet) function.apply(ChatComponentUtils.updateForEntity(commandlistenerwrapper, ichatbasecomponent, entityplayer, 0)));
        }

        if (collection.size() == 1) {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.title.show." + s + ".single", new Object[]{((EntityPlayer) collection.iterator().next()).getDisplayName()}), true);
        } else {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.title.show." + s + ".multiple", new Object[]{collection.size()}), true);
        }

        return collection.size();
    }

    private static int setTimes(CommandListenerWrapper commandlistenerwrapper, Collection<EntityPlayer> collection, int i, int j, int k) {
        ClientboundSetTitlesAnimationPacket clientboundsettitlesanimationpacket = new ClientboundSetTitlesAnimationPacket(i, j, k);
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            entityplayer.connection.send(clientboundsettitlesanimationpacket);
        }

        if (collection.size() == 1) {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.title.times.single", new Object[]{((EntityPlayer) collection.iterator().next()).getDisplayName()}), true);
        } else {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.title.times.multiple", new Object[]{collection.size()}), true);
        }

        return collection.size();
    }
}
