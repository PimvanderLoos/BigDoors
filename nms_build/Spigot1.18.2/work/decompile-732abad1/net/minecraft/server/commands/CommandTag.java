package net.minecraft.server.commands;

import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.world.entity.Entity;

public class CommandTag {

    private static final SimpleCommandExceptionType ERROR_ADD_FAILED = new SimpleCommandExceptionType(new ChatMessage("commands.tag.add.failed"));
    private static final SimpleCommandExceptionType ERROR_REMOVE_FAILED = new SimpleCommandExceptionType(new ChatMessage("commands.tag.remove.failed"));

    public CommandTag() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("tag").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentEntity.entities()).then(net.minecraft.commands.CommandDispatcher.literal("add").then(net.minecraft.commands.CommandDispatcher.argument("name", StringArgumentType.word()).executes((commandcontext) -> {
            return addTag((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntities(commandcontext, "targets"), StringArgumentType.getString(commandcontext, "name"));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("remove").then(net.minecraft.commands.CommandDispatcher.argument("name", StringArgumentType.word()).suggests((commandcontext, suggestionsbuilder) -> {
            return ICompletionProvider.suggest((Iterable) getTags(ArgumentEntity.getEntities(commandcontext, "targets")), suggestionsbuilder);
        }).executes((commandcontext) -> {
            return removeTag((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntities(commandcontext, "targets"), StringArgumentType.getString(commandcontext, "name"));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("list").executes((commandcontext) -> {
            return listTags((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntities(commandcontext, "targets"));
        }))));
    }

    private static Collection<String> getTags(Collection<? extends Entity> collection) {
        Set<String> set = Sets.newHashSet();
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            set.addAll(entity.getTags());
        }

        return set;
    }

    private static int addTag(CommandListenerWrapper commandlistenerwrapper, Collection<? extends Entity> collection, String s) throws CommandSyntaxException {
        int i = 0;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (entity.addTag(s)) {
                ++i;
            }
        }

        if (i == 0) {
            throw CommandTag.ERROR_ADD_FAILED.create();
        } else {
            if (collection.size() == 1) {
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.tag.add.success.single", new Object[]{s, ((Entity) collection.iterator().next()).getDisplayName()}), true);
            } else {
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.tag.add.success.multiple", new Object[]{s, collection.size()}), true);
            }

            return i;
        }
    }

    private static int removeTag(CommandListenerWrapper commandlistenerwrapper, Collection<? extends Entity> collection, String s) throws CommandSyntaxException {
        int i = 0;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (entity.removeTag(s)) {
                ++i;
            }
        }

        if (i == 0) {
            throw CommandTag.ERROR_REMOVE_FAILED.create();
        } else {
            if (collection.size() == 1) {
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.tag.remove.success.single", new Object[]{s, ((Entity) collection.iterator().next()).getDisplayName()}), true);
            } else {
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.tag.remove.success.multiple", new Object[]{s, collection.size()}), true);
            }

            return i;
        }
    }

    private static int listTags(CommandListenerWrapper commandlistenerwrapper, Collection<? extends Entity> collection) {
        Set<String> set = Sets.newHashSet();
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            set.addAll(entity.getTags());
        }

        if (collection.size() == 1) {
            Entity entity1 = (Entity) collection.iterator().next();

            if (set.isEmpty()) {
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.tag.list.single.empty", new Object[]{entity1.getDisplayName()}), false);
            } else {
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.tag.list.single.success", new Object[]{entity1.getDisplayName(), set.size(), ChatComponentUtils.formatList(set)}), false);
            }
        } else if (set.isEmpty()) {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.tag.list.multiple.empty", new Object[]{collection.size()}), false);
        } else {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.tag.list.multiple.success", new Object[]{collection.size(), set.size(), ChatComponentUtils.formatList(set)}), false);
        }

        return set.size();
    }
}
