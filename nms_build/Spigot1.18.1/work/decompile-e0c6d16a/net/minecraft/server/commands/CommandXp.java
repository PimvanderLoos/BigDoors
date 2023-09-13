package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.player.EntityHuman;

public class CommandXp {

    private static final SimpleCommandExceptionType ERROR_SET_POINTS_INVALID = new SimpleCommandExceptionType(new ChatMessage("commands.experience.set.points.invalid"));

    public CommandXp() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        LiteralCommandNode<CommandListenerWrapper> literalcommandnode = commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("experience").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.literal("add").then(net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentEntity.players()).then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("amount", IntegerArgumentType.integer()).executes((commandcontext) -> {
            return addExperience((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), IntegerArgumentType.getInteger(commandcontext, "amount"), CommandXp.Unit.POINTS);
        })).then(net.minecraft.commands.CommandDispatcher.literal("points").executes((commandcontext) -> {
            return addExperience((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), IntegerArgumentType.getInteger(commandcontext, "amount"), CommandXp.Unit.POINTS);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("levels").executes((commandcontext) -> {
            return addExperience((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), IntegerArgumentType.getInteger(commandcontext, "amount"), CommandXp.Unit.LEVELS);
        })))))).then(net.minecraft.commands.CommandDispatcher.literal("set").then(net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentEntity.players()).then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("amount", IntegerArgumentType.integer(0)).executes((commandcontext) -> {
            return setExperience((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), IntegerArgumentType.getInteger(commandcontext, "amount"), CommandXp.Unit.POINTS);
        })).then(net.minecraft.commands.CommandDispatcher.literal("points").executes((commandcontext) -> {
            return setExperience((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), IntegerArgumentType.getInteger(commandcontext, "amount"), CommandXp.Unit.POINTS);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("levels").executes((commandcontext) -> {
            return setExperience((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), IntegerArgumentType.getInteger(commandcontext, "amount"), CommandXp.Unit.LEVELS);
        })))))).then(net.minecraft.commands.CommandDispatcher.literal("query").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentEntity.player()).then(net.minecraft.commands.CommandDispatcher.literal("points").executes((commandcontext) -> {
            return queryExperience((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayer(commandcontext, "targets"), CommandXp.Unit.POINTS);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("levels").executes((commandcontext) -> {
            return queryExperience((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayer(commandcontext, "targets"), CommandXp.Unit.LEVELS);
        })))));

        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("xp").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).redirect(literalcommandnode));
    }

    private static int queryExperience(CommandListenerWrapper commandlistenerwrapper, EntityPlayer entityplayer, CommandXp.Unit commandxp_unit) {
        int i = commandxp_unit.query.applyAsInt(entityplayer);

        commandlistenerwrapper.sendSuccess(new ChatMessage("commands.experience.query." + commandxp_unit.name, new Object[]{entityplayer.getDisplayName(), i}), false);
        return i;
    }

    private static int addExperience(CommandListenerWrapper commandlistenerwrapper, Collection<? extends EntityPlayer> collection, int i, CommandXp.Unit commandxp_unit) {
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            commandxp_unit.add.accept(entityplayer, i);
        }

        if (collection.size() == 1) {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.experience.add." + commandxp_unit.name + ".success.single", new Object[]{i, ((EntityPlayer) collection.iterator().next()).getDisplayName()}), true);
        } else {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.experience.add." + commandxp_unit.name + ".success.multiple", new Object[]{i, collection.size()}), true);
        }

        return collection.size();
    }

    private static int setExperience(CommandListenerWrapper commandlistenerwrapper, Collection<? extends EntityPlayer> collection, int i, CommandXp.Unit commandxp_unit) throws CommandSyntaxException {
        int j = 0;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            if (commandxp_unit.set.test(entityplayer, i)) {
                ++j;
            }
        }

        if (j == 0) {
            throw CommandXp.ERROR_SET_POINTS_INVALID.create();
        } else {
            if (collection.size() == 1) {
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.experience.set." + commandxp_unit.name + ".success.single", new Object[]{i, ((EntityPlayer) collection.iterator().next()).getDisplayName()}), true);
            } else {
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.experience.set." + commandxp_unit.name + ".success.multiple", new Object[]{i, collection.size()}), true);
            }

            return collection.size();
        }
    }

    private static enum Unit {

        POINTS("points", EntityHuman::giveExperiencePoints, (entityplayer, integer) -> {
            if (integer >= entityplayer.getXpNeededForNextLevel()) {
                return false;
            } else {
                entityplayer.setExperiencePoints(integer);
                return true;
            }
        }, (entityplayer) -> {
            return MathHelper.floor(entityplayer.experienceProgress * (float) entityplayer.getXpNeededForNextLevel());
        }), LEVELS("levels", EntityPlayer::giveExperienceLevels, (entityplayer, integer) -> {
            entityplayer.setExperienceLevels(integer);
            return true;
        }, (entityplayer) -> {
            return entityplayer.experienceLevel;
        });

        public final BiConsumer<EntityPlayer, Integer> add;
        public final BiPredicate<EntityPlayer, Integer> set;
        public final String name;
        final ToIntFunction<EntityPlayer> query;

        private Unit(String s, BiConsumer biconsumer, BiPredicate bipredicate, ToIntFunction tointfunction) {
            this.add = biconsumer;
            this.name = s;
            this.set = bipredicate;
            this.query = tointfunction;
        }
    }
}
