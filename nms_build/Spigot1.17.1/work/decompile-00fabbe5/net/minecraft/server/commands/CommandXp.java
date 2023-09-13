package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
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

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        LiteralCommandNode<CommandListenerWrapper> literalcommandnode = commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("experience").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.a("add").then(net.minecraft.commands.CommandDispatcher.a("targets", (ArgumentType) ArgumentEntity.d()).then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("amount", (ArgumentType) IntegerArgumentType.integer()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), IntegerArgumentType.getInteger(commandcontext, "amount"), CommandXp.Unit.POINTS);
        })).then(net.minecraft.commands.CommandDispatcher.a("points").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), IntegerArgumentType.getInteger(commandcontext, "amount"), CommandXp.Unit.POINTS);
        }))).then(net.minecraft.commands.CommandDispatcher.a("levels").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), IntegerArgumentType.getInteger(commandcontext, "amount"), CommandXp.Unit.LEVELS);
        })))))).then(net.minecraft.commands.CommandDispatcher.a("set").then(net.minecraft.commands.CommandDispatcher.a("targets", (ArgumentType) ArgumentEntity.d()).then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("amount", (ArgumentType) IntegerArgumentType.integer(0)).executes((commandcontext) -> {
            return b((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), IntegerArgumentType.getInteger(commandcontext, "amount"), CommandXp.Unit.POINTS);
        })).then(net.minecraft.commands.CommandDispatcher.a("points").executes((commandcontext) -> {
            return b((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), IntegerArgumentType.getInteger(commandcontext, "amount"), CommandXp.Unit.POINTS);
        }))).then(net.minecraft.commands.CommandDispatcher.a("levels").executes((commandcontext) -> {
            return b((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), IntegerArgumentType.getInteger(commandcontext, "amount"), CommandXp.Unit.LEVELS);
        })))))).then(net.minecraft.commands.CommandDispatcher.a("query").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("targets", (ArgumentType) ArgumentEntity.c()).then(net.minecraft.commands.CommandDispatcher.a("points").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.e(commandcontext, "targets"), CommandXp.Unit.POINTS);
        }))).then(net.minecraft.commands.CommandDispatcher.a("levels").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.e(commandcontext, "targets"), CommandXp.Unit.LEVELS);
        })))));

        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("xp").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).redirect(literalcommandnode));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, EntityPlayer entityplayer, CommandXp.Unit commandxp_unit) {
        int i = commandxp_unit.query.applyAsInt(entityplayer);

        commandlistenerwrapper.sendMessage(new ChatMessage("commands.experience.query." + commandxp_unit.name, new Object[]{entityplayer.getScoreboardDisplayName(), i}), false);
        return i;
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Collection<? extends EntityPlayer> collection, int i, CommandXp.Unit commandxp_unit) {
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            commandxp_unit.add.accept(entityplayer, i);
        }

        if (collection.size() == 1) {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.experience.add." + commandxp_unit.name + ".success.single", new Object[]{i, ((EntityPlayer) collection.iterator().next()).getScoreboardDisplayName()}), true);
        } else {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.experience.add." + commandxp_unit.name + ".success.multiple", new Object[]{i, collection.size()}), true);
        }

        return collection.size();
    }

    private static int b(CommandListenerWrapper commandlistenerwrapper, Collection<? extends EntityPlayer> collection, int i, CommandXp.Unit commandxp_unit) throws CommandSyntaxException {
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
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.experience.set." + commandxp_unit.name + ".success.single", new Object[]{i, ((EntityPlayer) collection.iterator().next()).getScoreboardDisplayName()}), true);
            } else {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.experience.set." + commandxp_unit.name + ".success.multiple", new Object[]{i, collection.size()}), true);
            }

            return collection.size();
        }
    }

    private static enum Unit {

        POINTS("points", EntityHuman::giveExp, (entityplayer, integer) -> {
            if (integer >= entityplayer.getExpToLevel()) {
                return false;
            } else {
                entityplayer.a(integer);
                return true;
            }
        }, (entityplayer) -> {
            return MathHelper.d(entityplayer.experienceProgress * (float) entityplayer.getExpToLevel());
        }), LEVELS("levels", EntityPlayer::levelDown, (entityplayer, integer) -> {
            entityplayer.b(integer);
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
