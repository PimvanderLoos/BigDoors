package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.commands.arguments.ArgumentMinecraftKeyRegistered;
import net.minecraft.commands.synchronization.CompletionProviders;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.item.crafting.IRecipe;

public class CommandRecipe {

    private static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("commands.recipe.give.failed"));
    private static final SimpleCommandExceptionType b = new SimpleCommandExceptionType(new ChatMessage("commands.recipe.take.failed"));

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("recipe").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.a("give").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("targets", (ArgumentType) ArgumentEntity.d()).then(net.minecraft.commands.CommandDispatcher.a("recipe", (ArgumentType) ArgumentMinecraftKeyRegistered.a()).suggests(CompletionProviders.b).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), Collections.singleton(ArgumentMinecraftKeyRegistered.b(commandcontext, "recipe")));
        }))).then(net.minecraft.commands.CommandDispatcher.a("*").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), ((CommandListenerWrapper) commandcontext.getSource()).getServer().getCraftingManager().b());
        }))))).then(net.minecraft.commands.CommandDispatcher.a("take").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("targets", (ArgumentType) ArgumentEntity.d()).then(net.minecraft.commands.CommandDispatcher.a("recipe", (ArgumentType) ArgumentMinecraftKeyRegistered.a()).suggests(CompletionProviders.b).executes((commandcontext) -> {
            return b((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), Collections.singleton(ArgumentMinecraftKeyRegistered.b(commandcontext, "recipe")));
        }))).then(net.minecraft.commands.CommandDispatcher.a("*").executes((commandcontext) -> {
            return b((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), ((CommandListenerWrapper) commandcontext.getSource()).getServer().getCraftingManager().b());
        })))));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Collection<EntityPlayer> collection, Collection<IRecipe<?>> collection1) throws CommandSyntaxException {
        int i = 0;

        EntityPlayer entityplayer;

        for (Iterator iterator = collection.iterator(); iterator.hasNext(); i += entityplayer.discoverRecipes(collection1)) {
            entityplayer = (EntityPlayer) iterator.next();
        }

        if (i == 0) {
            throw CommandRecipe.a.create();
        } else {
            if (collection.size() == 1) {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.recipe.give.success.single", new Object[]{collection1.size(), ((EntityPlayer) collection.iterator().next()).getScoreboardDisplayName()}), true);
            } else {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.recipe.give.success.multiple", new Object[]{collection1.size(), collection.size()}), true);
            }

            return i;
        }
    }

    private static int b(CommandListenerWrapper commandlistenerwrapper, Collection<EntityPlayer> collection, Collection<IRecipe<?>> collection1) throws CommandSyntaxException {
        int i = 0;

        EntityPlayer entityplayer;

        for (Iterator iterator = collection.iterator(); iterator.hasNext(); i += entityplayer.undiscoverRecipes(collection1)) {
            entityplayer = (EntityPlayer) iterator.next();
        }

        if (i == 0) {
            throw CommandRecipe.b.create();
        } else {
            if (collection.size() == 1) {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.recipe.take.success.single", new Object[]{collection1.size(), ((EntityPlayer) collection.iterator().next()).getScoreboardDisplayName()}), true);
            } else {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.recipe.take.success.multiple", new Object[]{collection1.size(), collection.size()}), true);
            }

            return i;
        }
    }
}
