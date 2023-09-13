package net.minecraft.server;

import com.google.common.collect.Sets;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Predicate;

public class CommandTag {

    private static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("commands.tag.add.failed", new Object[0]));
    private static final SimpleCommandExceptionType b = new SimpleCommandExceptionType(new ChatMessage("commands.tag.remove.failed", new Object[0]));

    public static void a(com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> com_mojang_brigadier_commanddispatcher) {
        com_mojang_brigadier_commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandDispatcher.a("tag").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) CommandDispatcher.a("targets", (ArgumentType) ArgumentEntity.b()).then(CommandDispatcher.a("add").then(CommandDispatcher.a("name", (ArgumentType) StringArgumentType.word()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.b(commandcontext, "targets"), StringArgumentType.getString(commandcontext, "name"));
        })))).then(CommandDispatcher.a("remove").then(CommandDispatcher.a("name", (ArgumentType) StringArgumentType.word()).suggests((commandcontext, suggestionsbuilder) -> {
            return ICompletionProvider.b(a(ArgumentEntity.b(commandcontext, "targets")), suggestionsbuilder);
        }).executes((commandcontext) -> {
            return b((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.b(commandcontext, "targets"), StringArgumentType.getString(commandcontext, "name"));
        })))).then(CommandDispatcher.a("list").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.b(commandcontext, "targets"));
        }))));
    }

    private static Collection<String> a(Collection<? extends Entity> collection) {
        HashSet hashset = Sets.newHashSet();
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            hashset.addAll(entity.getScoreboardTags());
        }

        return hashset;
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Collection<? extends Entity> collection, String s) throws CommandSyntaxException {
        int i = 0;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (entity.addScoreboardTag(s)) {
                ++i;
            }
        }

        if (i == 0) {
            throw CommandTag.a.create();
        } else {
            if (collection.size() == 1) {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.tag.add.success.single", new Object[] { s, ((Entity) collection.iterator().next()).getScoreboardDisplayName()}), true);
            } else {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.tag.add.success.multiple", new Object[] { s, Integer.valueOf(collection.size())}), true);
            }

            return i;
        }
    }

    private static int b(CommandListenerWrapper commandlistenerwrapper, Collection<? extends Entity> collection, String s) throws CommandSyntaxException {
        int i = 0;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (entity.removeScoreboardTag(s)) {
                ++i;
            }
        }

        if (i == 0) {
            throw CommandTag.b.create();
        } else {
            if (collection.size() == 1) {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.tag.remove.success.single", new Object[] { s, ((Entity) collection.iterator().next()).getScoreboardDisplayName()}), true);
            } else {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.tag.remove.success.multiple", new Object[] { s, Integer.valueOf(collection.size())}), true);
            }

            return i;
        }
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Collection<? extends Entity> collection) {
        HashSet hashset = Sets.newHashSet();
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            hashset.addAll(entity.getScoreboardTags());
        }

        if (collection.size() == 1) {
            Entity entity1 = (Entity) collection.iterator().next();

            if (hashset.isEmpty()) {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.tag.list.single.empty", new Object[] { entity1.getScoreboardDisplayName()}), false);
            } else {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.tag.list.single.success", new Object[] { entity1.getScoreboardDisplayName(), Integer.valueOf(hashset.size()), ChatComponentUtils.a((Collection) hashset)}), false);
            }
        } else if (hashset.isEmpty()) {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.tag.list.multiple.empty", new Object[] { Integer.valueOf(collection.size())}), false);
        } else {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.tag.list.multiple.success", new Object[] { Integer.valueOf(collection.size()), Integer.valueOf(hashset.size()), ChatComponentUtils.a((Collection) hashset)}), false);
        }

        return hashset.size();
    }
}
