package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.commands.CommandException;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.commands.arguments.ArgumentMinecraftKeyRegistered;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.level.EntityPlayer;

public class CommandAdvancement {

    private static final SuggestionProvider<CommandListenerWrapper> a = (commandcontext, suggestionsbuilder) -> {
        Collection<Advancement> collection = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getAdvancementData().getAdvancements();

        return ICompletionProvider.a(collection.stream().map(Advancement::getName), suggestionsbuilder);
    };

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("advancement").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.a("grant").then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("targets", (ArgumentType) ArgumentEntity.d()).then(net.minecraft.commands.CommandDispatcher.a("only").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("advancement", (ArgumentType) ArgumentMinecraftKeyRegistered.a()).suggests(CommandAdvancement.a).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), CommandAdvancement.Action.GRANT, a(ArgumentMinecraftKeyRegistered.a(commandcontext, "advancement"), CommandAdvancement.Filter.ONLY));
        })).then(net.minecraft.commands.CommandDispatcher.a("criterion", (ArgumentType) StringArgumentType.greedyString()).suggests((commandcontext, suggestionsbuilder) -> {
            return ICompletionProvider.b((Iterable) ArgumentMinecraftKeyRegistered.a(commandcontext, "advancement").getCriteria().keySet(), suggestionsbuilder);
        }).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), CommandAdvancement.Action.GRANT, ArgumentMinecraftKeyRegistered.a(commandcontext, "advancement"), StringArgumentType.getString(commandcontext, "criterion"));
        }))))).then(net.minecraft.commands.CommandDispatcher.a("from").then(net.minecraft.commands.CommandDispatcher.a("advancement", (ArgumentType) ArgumentMinecraftKeyRegistered.a()).suggests(CommandAdvancement.a).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), CommandAdvancement.Action.GRANT, a(ArgumentMinecraftKeyRegistered.a(commandcontext, "advancement"), CommandAdvancement.Filter.FROM));
        })))).then(net.minecraft.commands.CommandDispatcher.a("until").then(net.minecraft.commands.CommandDispatcher.a("advancement", (ArgumentType) ArgumentMinecraftKeyRegistered.a()).suggests(CommandAdvancement.a).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), CommandAdvancement.Action.GRANT, a(ArgumentMinecraftKeyRegistered.a(commandcontext, "advancement"), CommandAdvancement.Filter.UNTIL));
        })))).then(net.minecraft.commands.CommandDispatcher.a("through").then(net.minecraft.commands.CommandDispatcher.a("advancement", (ArgumentType) ArgumentMinecraftKeyRegistered.a()).suggests(CommandAdvancement.a).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), CommandAdvancement.Action.GRANT, a(ArgumentMinecraftKeyRegistered.a(commandcontext, "advancement"), CommandAdvancement.Filter.THROUGH));
        })))).then(net.minecraft.commands.CommandDispatcher.a("everything").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), CommandAdvancement.Action.GRANT, ((CommandListenerWrapper) commandcontext.getSource()).getServer().getAdvancementData().getAdvancements());
        }))))).then(net.minecraft.commands.CommandDispatcher.a("revoke").then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("targets", (ArgumentType) ArgumentEntity.d()).then(net.minecraft.commands.CommandDispatcher.a("only").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("advancement", (ArgumentType) ArgumentMinecraftKeyRegistered.a()).suggests(CommandAdvancement.a).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), CommandAdvancement.Action.REVOKE, a(ArgumentMinecraftKeyRegistered.a(commandcontext, "advancement"), CommandAdvancement.Filter.ONLY));
        })).then(net.minecraft.commands.CommandDispatcher.a("criterion", (ArgumentType) StringArgumentType.greedyString()).suggests((commandcontext, suggestionsbuilder) -> {
            return ICompletionProvider.b((Iterable) ArgumentMinecraftKeyRegistered.a(commandcontext, "advancement").getCriteria().keySet(), suggestionsbuilder);
        }).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), CommandAdvancement.Action.REVOKE, ArgumentMinecraftKeyRegistered.a(commandcontext, "advancement"), StringArgumentType.getString(commandcontext, "criterion"));
        }))))).then(net.minecraft.commands.CommandDispatcher.a("from").then(net.minecraft.commands.CommandDispatcher.a("advancement", (ArgumentType) ArgumentMinecraftKeyRegistered.a()).suggests(CommandAdvancement.a).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), CommandAdvancement.Action.REVOKE, a(ArgumentMinecraftKeyRegistered.a(commandcontext, "advancement"), CommandAdvancement.Filter.FROM));
        })))).then(net.minecraft.commands.CommandDispatcher.a("until").then(net.minecraft.commands.CommandDispatcher.a("advancement", (ArgumentType) ArgumentMinecraftKeyRegistered.a()).suggests(CommandAdvancement.a).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), CommandAdvancement.Action.REVOKE, a(ArgumentMinecraftKeyRegistered.a(commandcontext, "advancement"), CommandAdvancement.Filter.UNTIL));
        })))).then(net.minecraft.commands.CommandDispatcher.a("through").then(net.minecraft.commands.CommandDispatcher.a("advancement", (ArgumentType) ArgumentMinecraftKeyRegistered.a()).suggests(CommandAdvancement.a).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), CommandAdvancement.Action.REVOKE, a(ArgumentMinecraftKeyRegistered.a(commandcontext, "advancement"), CommandAdvancement.Filter.THROUGH));
        })))).then(net.minecraft.commands.CommandDispatcher.a("everything").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), CommandAdvancement.Action.REVOKE, ((CommandListenerWrapper) commandcontext.getSource()).getServer().getAdvancementData().getAdvancements());
        })))));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Collection<EntityPlayer> collection, CommandAdvancement.Action commandadvancement_action, Collection<Advancement> collection1) {
        int i = 0;

        EntityPlayer entityplayer;

        for (Iterator iterator = collection.iterator(); iterator.hasNext(); i += commandadvancement_action.a(entityplayer, (Iterable) collection1)) {
            entityplayer = (EntityPlayer) iterator.next();
        }

        if (i == 0) {
            if (collection1.size() == 1) {
                if (collection.size() == 1) {
                    throw new CommandException(new ChatMessage(commandadvancement_action.a() + ".one.to.one.failure", new Object[]{((Advancement) collection1.iterator().next()).j(), ((EntityPlayer) collection.iterator().next()).getScoreboardDisplayName()}));
                } else {
                    throw new CommandException(new ChatMessage(commandadvancement_action.a() + ".one.to.many.failure", new Object[]{((Advancement) collection1.iterator().next()).j(), collection.size()}));
                }
            } else if (collection.size() == 1) {
                throw new CommandException(new ChatMessage(commandadvancement_action.a() + ".many.to.one.failure", new Object[]{collection1.size(), ((EntityPlayer) collection.iterator().next()).getScoreboardDisplayName()}));
            } else {
                throw new CommandException(new ChatMessage(commandadvancement_action.a() + ".many.to.many.failure", new Object[]{collection1.size(), collection.size()}));
            }
        } else {
            if (collection1.size() == 1) {
                if (collection.size() == 1) {
                    commandlistenerwrapper.sendMessage(new ChatMessage(commandadvancement_action.a() + ".one.to.one.success", new Object[]{((Advancement) collection1.iterator().next()).j(), ((EntityPlayer) collection.iterator().next()).getScoreboardDisplayName()}), true);
                } else {
                    commandlistenerwrapper.sendMessage(new ChatMessage(commandadvancement_action.a() + ".one.to.many.success", new Object[]{((Advancement) collection1.iterator().next()).j(), collection.size()}), true);
                }
            } else if (collection.size() == 1) {
                commandlistenerwrapper.sendMessage(new ChatMessage(commandadvancement_action.a() + ".many.to.one.success", new Object[]{collection1.size(), ((EntityPlayer) collection.iterator().next()).getScoreboardDisplayName()}), true);
            } else {
                commandlistenerwrapper.sendMessage(new ChatMessage(commandadvancement_action.a() + ".many.to.many.success", new Object[]{collection1.size(), collection.size()}), true);
            }

            return i;
        }
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Collection<EntityPlayer> collection, CommandAdvancement.Action commandadvancement_action, Advancement advancement, String s) {
        int i = 0;

        if (!advancement.getCriteria().containsKey(s)) {
            throw new CommandException(new ChatMessage("commands.advancement.criterionNotFound", new Object[]{advancement.j(), s}));
        } else {
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                if (commandadvancement_action.a(entityplayer, advancement, s)) {
                    ++i;
                }
            }

            if (i == 0) {
                if (collection.size() == 1) {
                    throw new CommandException(new ChatMessage(commandadvancement_action.a() + ".criterion.to.one.failure", new Object[]{s, advancement.j(), ((EntityPlayer) collection.iterator().next()).getScoreboardDisplayName()}));
                } else {
                    throw new CommandException(new ChatMessage(commandadvancement_action.a() + ".criterion.to.many.failure", new Object[]{s, advancement.j(), collection.size()}));
                }
            } else {
                if (collection.size() == 1) {
                    commandlistenerwrapper.sendMessage(new ChatMessage(commandadvancement_action.a() + ".criterion.to.one.success", new Object[]{s, advancement.j(), ((EntityPlayer) collection.iterator().next()).getScoreboardDisplayName()}), true);
                } else {
                    commandlistenerwrapper.sendMessage(new ChatMessage(commandadvancement_action.a() + ".criterion.to.many.success", new Object[]{s, advancement.j(), collection.size()}), true);
                }

                return i;
            }
        }
    }

    private static List<Advancement> a(Advancement advancement, CommandAdvancement.Filter commandadvancement_filter) {
        List<Advancement> list = Lists.newArrayList();

        if (commandadvancement_filter.f) {
            for (Advancement advancement1 = advancement.b(); advancement1 != null; advancement1 = advancement1.b()) {
                list.add(advancement1);
            }
        }

        list.add(advancement);
        if (commandadvancement_filter.g) {
            a(advancement, (List) list);
        }

        return list;
    }

    private static void a(Advancement advancement, List<Advancement> list) {
        Iterator iterator = advancement.e().iterator();

        while (iterator.hasNext()) {
            Advancement advancement1 = (Advancement) iterator.next();

            list.add(advancement1);
            a(advancement1, list);
        }

    }

    static enum Filter {

        ONLY(false, false), THROUGH(true, true), FROM(false, true), UNTIL(true, false), EVERYTHING(true, true);

        private final boolean f;
        private final boolean g;

        private Filter(boolean flag, boolean flag1) {
            this.f = flag;
            this.g = flag1;
        }
    }

    static enum Action {

        GRANT("grant") {
            @Override
            protected boolean a(EntityPlayer entityplayer, Advancement advancement) {
                AdvancementProgress advancementprogress = entityplayer.getAdvancementData().getProgress(advancement);

                if (advancementprogress.isDone()) {
                    return false;
                } else {
                    Iterator iterator = advancementprogress.getRemainingCriteria().iterator();

                    while (iterator.hasNext()) {
                        String s = (String) iterator.next();

                        entityplayer.getAdvancementData().grantCriteria(advancement, s);
                    }

                    return true;
                }
            }

            @Override
            protected boolean a(EntityPlayer entityplayer, Advancement advancement, String s) {
                return entityplayer.getAdvancementData().grantCriteria(advancement, s);
            }
        },
        REVOKE("revoke") {
            @Override
            protected boolean a(EntityPlayer entityplayer, Advancement advancement) {
                AdvancementProgress advancementprogress = entityplayer.getAdvancementData().getProgress(advancement);

                if (!advancementprogress.b()) {
                    return false;
                } else {
                    Iterator iterator = advancementprogress.getAwardedCriteria().iterator();

                    while (iterator.hasNext()) {
                        String s = (String) iterator.next();

                        entityplayer.getAdvancementData().revokeCritera(advancement, s);
                    }

                    return true;
                }
            }

            @Override
            protected boolean a(EntityPlayer entityplayer, Advancement advancement, String s) {
                return entityplayer.getAdvancementData().revokeCritera(advancement, s);
            }
        };

        private final String c;

        private Action(String s) {
            this.c = "commands.advancement." + s;
        }

        public int a(EntityPlayer entityplayer, Iterable<Advancement> iterable) {
            int i = 0;
            Iterator iterator = iterable.iterator();

            while (iterator.hasNext()) {
                Advancement advancement = (Advancement) iterator.next();

                if (this.a(entityplayer, advancement)) {
                    ++i;
                }
            }

            return i;
        }

        protected abstract boolean a(EntityPlayer entityplayer, Advancement advancement);

        protected abstract boolean a(EntityPlayer entityplayer, Advancement advancement, String s);

        protected String a() {
            return this.c;
        }
    }
}
