package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
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

    private static final SuggestionProvider<CommandListenerWrapper> SUGGEST_ADVANCEMENTS = (commandcontext, suggestionsbuilder) -> {
        Collection<Advancement> collection = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getAdvancements().getAllAdvancements();

        return ICompletionProvider.suggestResource(collection.stream().map(Advancement::getId), suggestionsbuilder);
    };

    public CommandAdvancement() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("advancement").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.literal("grant").then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentEntity.players()).then(net.minecraft.commands.CommandDispatcher.literal("only").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("advancement", ArgumentMinecraftKeyRegistered.id()).suggests(CommandAdvancement.SUGGEST_ADVANCEMENTS).executes((commandcontext) -> {
            return perform((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), CommandAdvancement.Action.GRANT, getAdvancements(ArgumentMinecraftKeyRegistered.getAdvancement(commandcontext, "advancement"), CommandAdvancement.Filter.ONLY));
        })).then(net.minecraft.commands.CommandDispatcher.argument("criterion", StringArgumentType.greedyString()).suggests((commandcontext, suggestionsbuilder) -> {
            return ICompletionProvider.suggest((Iterable) ArgumentMinecraftKeyRegistered.getAdvancement(commandcontext, "advancement").getCriteria().keySet(), suggestionsbuilder);
        }).executes((commandcontext) -> {
            return performCriterion((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), CommandAdvancement.Action.GRANT, ArgumentMinecraftKeyRegistered.getAdvancement(commandcontext, "advancement"), StringArgumentType.getString(commandcontext, "criterion"));
        }))))).then(net.minecraft.commands.CommandDispatcher.literal("from").then(net.minecraft.commands.CommandDispatcher.argument("advancement", ArgumentMinecraftKeyRegistered.id()).suggests(CommandAdvancement.SUGGEST_ADVANCEMENTS).executes((commandcontext) -> {
            return perform((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), CommandAdvancement.Action.GRANT, getAdvancements(ArgumentMinecraftKeyRegistered.getAdvancement(commandcontext, "advancement"), CommandAdvancement.Filter.FROM));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("until").then(net.minecraft.commands.CommandDispatcher.argument("advancement", ArgumentMinecraftKeyRegistered.id()).suggests(CommandAdvancement.SUGGEST_ADVANCEMENTS).executes((commandcontext) -> {
            return perform((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), CommandAdvancement.Action.GRANT, getAdvancements(ArgumentMinecraftKeyRegistered.getAdvancement(commandcontext, "advancement"), CommandAdvancement.Filter.UNTIL));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("through").then(net.minecraft.commands.CommandDispatcher.argument("advancement", ArgumentMinecraftKeyRegistered.id()).suggests(CommandAdvancement.SUGGEST_ADVANCEMENTS).executes((commandcontext) -> {
            return perform((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), CommandAdvancement.Action.GRANT, getAdvancements(ArgumentMinecraftKeyRegistered.getAdvancement(commandcontext, "advancement"), CommandAdvancement.Filter.THROUGH));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("everything").executes((commandcontext) -> {
            return perform((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), CommandAdvancement.Action.GRANT, ((CommandListenerWrapper) commandcontext.getSource()).getServer().getAdvancements().getAllAdvancements());
        }))))).then(net.minecraft.commands.CommandDispatcher.literal("revoke").then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentEntity.players()).then(net.minecraft.commands.CommandDispatcher.literal("only").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("advancement", ArgumentMinecraftKeyRegistered.id()).suggests(CommandAdvancement.SUGGEST_ADVANCEMENTS).executes((commandcontext) -> {
            return perform((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), CommandAdvancement.Action.REVOKE, getAdvancements(ArgumentMinecraftKeyRegistered.getAdvancement(commandcontext, "advancement"), CommandAdvancement.Filter.ONLY));
        })).then(net.minecraft.commands.CommandDispatcher.argument("criterion", StringArgumentType.greedyString()).suggests((commandcontext, suggestionsbuilder) -> {
            return ICompletionProvider.suggest((Iterable) ArgumentMinecraftKeyRegistered.getAdvancement(commandcontext, "advancement").getCriteria().keySet(), suggestionsbuilder);
        }).executes((commandcontext) -> {
            return performCriterion((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), CommandAdvancement.Action.REVOKE, ArgumentMinecraftKeyRegistered.getAdvancement(commandcontext, "advancement"), StringArgumentType.getString(commandcontext, "criterion"));
        }))))).then(net.minecraft.commands.CommandDispatcher.literal("from").then(net.minecraft.commands.CommandDispatcher.argument("advancement", ArgumentMinecraftKeyRegistered.id()).suggests(CommandAdvancement.SUGGEST_ADVANCEMENTS).executes((commandcontext) -> {
            return perform((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), CommandAdvancement.Action.REVOKE, getAdvancements(ArgumentMinecraftKeyRegistered.getAdvancement(commandcontext, "advancement"), CommandAdvancement.Filter.FROM));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("until").then(net.minecraft.commands.CommandDispatcher.argument("advancement", ArgumentMinecraftKeyRegistered.id()).suggests(CommandAdvancement.SUGGEST_ADVANCEMENTS).executes((commandcontext) -> {
            return perform((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), CommandAdvancement.Action.REVOKE, getAdvancements(ArgumentMinecraftKeyRegistered.getAdvancement(commandcontext, "advancement"), CommandAdvancement.Filter.UNTIL));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("through").then(net.minecraft.commands.CommandDispatcher.argument("advancement", ArgumentMinecraftKeyRegistered.id()).suggests(CommandAdvancement.SUGGEST_ADVANCEMENTS).executes((commandcontext) -> {
            return perform((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), CommandAdvancement.Action.REVOKE, getAdvancements(ArgumentMinecraftKeyRegistered.getAdvancement(commandcontext, "advancement"), CommandAdvancement.Filter.THROUGH));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("everything").executes((commandcontext) -> {
            return perform((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), CommandAdvancement.Action.REVOKE, ((CommandListenerWrapper) commandcontext.getSource()).getServer().getAdvancements().getAllAdvancements());
        })))));
    }

    private static int perform(CommandListenerWrapper commandlistenerwrapper, Collection<EntityPlayer> collection, CommandAdvancement.Action commandadvancement_action, Collection<Advancement> collection1) {
        int i = 0;

        EntityPlayer entityplayer;

        for (Iterator iterator = collection.iterator(); iterator.hasNext(); i += commandadvancement_action.perform(entityplayer, (Iterable) collection1)) {
            entityplayer = (EntityPlayer) iterator.next();
        }

        if (i == 0) {
            if (collection1.size() == 1) {
                if (collection.size() == 1) {
                    throw new CommandException(new ChatMessage(commandadvancement_action.getKey() + ".one.to.one.failure", new Object[]{((Advancement) collection1.iterator().next()).getChatComponent(), ((EntityPlayer) collection.iterator().next()).getDisplayName()}));
                } else {
                    throw new CommandException(new ChatMessage(commandadvancement_action.getKey() + ".one.to.many.failure", new Object[]{((Advancement) collection1.iterator().next()).getChatComponent(), collection.size()}));
                }
            } else if (collection.size() == 1) {
                throw new CommandException(new ChatMessage(commandadvancement_action.getKey() + ".many.to.one.failure", new Object[]{collection1.size(), ((EntityPlayer) collection.iterator().next()).getDisplayName()}));
            } else {
                throw new CommandException(new ChatMessage(commandadvancement_action.getKey() + ".many.to.many.failure", new Object[]{collection1.size(), collection.size()}));
            }
        } else {
            if (collection1.size() == 1) {
                if (collection.size() == 1) {
                    commandlistenerwrapper.sendSuccess(new ChatMessage(commandadvancement_action.getKey() + ".one.to.one.success", new Object[]{((Advancement) collection1.iterator().next()).getChatComponent(), ((EntityPlayer) collection.iterator().next()).getDisplayName()}), true);
                } else {
                    commandlistenerwrapper.sendSuccess(new ChatMessage(commandadvancement_action.getKey() + ".one.to.many.success", new Object[]{((Advancement) collection1.iterator().next()).getChatComponent(), collection.size()}), true);
                }
            } else if (collection.size() == 1) {
                commandlistenerwrapper.sendSuccess(new ChatMessage(commandadvancement_action.getKey() + ".many.to.one.success", new Object[]{collection1.size(), ((EntityPlayer) collection.iterator().next()).getDisplayName()}), true);
            } else {
                commandlistenerwrapper.sendSuccess(new ChatMessage(commandadvancement_action.getKey() + ".many.to.many.success", new Object[]{collection1.size(), collection.size()}), true);
            }

            return i;
        }
    }

    private static int performCriterion(CommandListenerWrapper commandlistenerwrapper, Collection<EntityPlayer> collection, CommandAdvancement.Action commandadvancement_action, Advancement advancement, String s) {
        int i = 0;

        if (!advancement.getCriteria().containsKey(s)) {
            throw new CommandException(new ChatMessage("commands.advancement.criterionNotFound", new Object[]{advancement.getChatComponent(), s}));
        } else {
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                if (commandadvancement_action.performCriterion(entityplayer, advancement, s)) {
                    ++i;
                }
            }

            if (i == 0) {
                if (collection.size() == 1) {
                    throw new CommandException(new ChatMessage(commandadvancement_action.getKey() + ".criterion.to.one.failure", new Object[]{s, advancement.getChatComponent(), ((EntityPlayer) collection.iterator().next()).getDisplayName()}));
                } else {
                    throw new CommandException(new ChatMessage(commandadvancement_action.getKey() + ".criterion.to.many.failure", new Object[]{s, advancement.getChatComponent(), collection.size()}));
                }
            } else {
                if (collection.size() == 1) {
                    commandlistenerwrapper.sendSuccess(new ChatMessage(commandadvancement_action.getKey() + ".criterion.to.one.success", new Object[]{s, advancement.getChatComponent(), ((EntityPlayer) collection.iterator().next()).getDisplayName()}), true);
                } else {
                    commandlistenerwrapper.sendSuccess(new ChatMessage(commandadvancement_action.getKey() + ".criterion.to.many.success", new Object[]{s, advancement.getChatComponent(), collection.size()}), true);
                }

                return i;
            }
        }
    }

    private static List<Advancement> getAdvancements(Advancement advancement, CommandAdvancement.Filter commandadvancement_filter) {
        List<Advancement> list = Lists.newArrayList();

        if (commandadvancement_filter.parents) {
            for (Advancement advancement1 = advancement.getParent(); advancement1 != null; advancement1 = advancement1.getParent()) {
                list.add(advancement1);
            }
        }

        list.add(advancement);
        if (commandadvancement_filter.children) {
            addChildren(advancement, list);
        }

        return list;
    }

    private static void addChildren(Advancement advancement, List<Advancement> list) {
        Iterator iterator = advancement.getChildren().iterator();

        while (iterator.hasNext()) {
            Advancement advancement1 = (Advancement) iterator.next();

            list.add(advancement1);
            addChildren(advancement1, list);
        }

    }

    private static enum Action {

        GRANT("grant") {
            @Override
            protected boolean perform(EntityPlayer entityplayer, Advancement advancement) {
                AdvancementProgress advancementprogress = entityplayer.getAdvancements().getOrStartProgress(advancement);

                if (advancementprogress.isDone()) {
                    return false;
                } else {
                    Iterator iterator = advancementprogress.getRemainingCriteria().iterator();

                    while (iterator.hasNext()) {
                        String s = (String) iterator.next();

                        entityplayer.getAdvancements().award(advancement, s);
                    }

                    return true;
                }
            }

            @Override
            protected boolean performCriterion(EntityPlayer entityplayer, Advancement advancement, String s) {
                return entityplayer.getAdvancements().award(advancement, s);
            }
        },
        REVOKE("revoke") {
            @Override
            protected boolean perform(EntityPlayer entityplayer, Advancement advancement) {
                AdvancementProgress advancementprogress = entityplayer.getAdvancements().getOrStartProgress(advancement);

                if (!advancementprogress.hasProgress()) {
                    return false;
                } else {
                    Iterator iterator = advancementprogress.getCompletedCriteria().iterator();

                    while (iterator.hasNext()) {
                        String s = (String) iterator.next();

                        entityplayer.getAdvancements().revoke(advancement, s);
                    }

                    return true;
                }
            }

            @Override
            protected boolean performCriterion(EntityPlayer entityplayer, Advancement advancement, String s) {
                return entityplayer.getAdvancements().revoke(advancement, s);
            }
        };

        private final String key;

        Action(String s) {
            this.key = "commands.advancement." + s;
        }

        public int perform(EntityPlayer entityplayer, Iterable<Advancement> iterable) {
            int i = 0;
            Iterator iterator = iterable.iterator();

            while (iterator.hasNext()) {
                Advancement advancement = (Advancement) iterator.next();

                if (this.perform(entityplayer, advancement)) {
                    ++i;
                }
            }

            return i;
        }

        protected abstract boolean perform(EntityPlayer entityplayer, Advancement advancement);

        protected abstract boolean performCriterion(EntityPlayer entityplayer, Advancement advancement, String s);

        protected String getKey() {
            return this.key;
        }
    }

    private static enum Filter {

        ONLY(false, false), THROUGH(true, true), FROM(false, true), UNTIL(true, false), EVERYTHING(true, true);

        final boolean parents;
        final boolean children;

        private Filter(boolean flag, boolean flag1) {
            this.parents = flag;
            this.children = flag1;
        }
    }
}
