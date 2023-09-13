package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.commands.arguments.ArgumentMinecraftKeyRegistered;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.synchronization.CompletionProviders;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.protocol.game.PacketPlayOutStopSound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundCategory;

public class CommandStopSound {

    public CommandStopSound() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        RequiredArgumentBuilder<CommandListenerWrapper, EntitySelector> requiredargumentbuilder = (RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentEntity.players()).executes((commandcontext) -> {
            return stopSound((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), (SoundCategory) null, (MinecraftKey) null);
        })).then(net.minecraft.commands.CommandDispatcher.literal("*").then(net.minecraft.commands.CommandDispatcher.argument("sound", ArgumentMinecraftKeyRegistered.id()).suggests(CompletionProviders.AVAILABLE_SOUNDS).executes((commandcontext) -> {
            return stopSound((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), (SoundCategory) null, ArgumentMinecraftKeyRegistered.getId(commandcontext, "sound"));
        })));
        SoundCategory[] asoundcategory = SoundCategory.values();
        int i = asoundcategory.length;

        for (int j = 0; j < i; ++j) {
            SoundCategory soundcategory = asoundcategory[j];

            requiredargumentbuilder.then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal(soundcategory.getName()).executes((commandcontext) -> {
                return stopSound((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), soundcategory, (MinecraftKey) null);
            })).then(net.minecraft.commands.CommandDispatcher.argument("sound", ArgumentMinecraftKeyRegistered.id()).suggests(CompletionProviders.AVAILABLE_SOUNDS).executes((commandcontext) -> {
                return stopSound((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), soundcategory, ArgumentMinecraftKeyRegistered.getId(commandcontext, "sound"));
            })));
        }

        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("stopsound").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(requiredargumentbuilder));
    }

    private static int stopSound(CommandListenerWrapper commandlistenerwrapper, Collection<EntityPlayer> collection, @Nullable SoundCategory soundcategory, @Nullable MinecraftKey minecraftkey) {
        PacketPlayOutStopSound packetplayoutstopsound = new PacketPlayOutStopSound(minecraftkey, soundcategory);
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            entityplayer.connection.send(packetplayoutstopsound);
        }

        if (soundcategory != null) {
            if (minecraftkey != null) {
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.stopsound.success.source.sound", new Object[]{minecraftkey, soundcategory.getName()}), true);
            } else {
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.stopsound.success.source.any", new Object[]{soundcategory.getName()}), true);
            }
        } else if (minecraftkey != null) {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.stopsound.success.sourceless.sound", new Object[]{minecraftkey}), true);
        } else {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.stopsound.success.sourceless.any"), true);
        }

        return collection.size();
    }
}
