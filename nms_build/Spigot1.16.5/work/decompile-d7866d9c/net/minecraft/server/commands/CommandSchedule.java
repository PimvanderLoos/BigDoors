package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.CustomFunction;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.commands.arguments.ArgumentTime;
import net.minecraft.commands.arguments.item.ArgumentTag;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.timers.CustomFunctionCallback;
import net.minecraft.world.level.timers.CustomFunctionCallbackTag;
import net.minecraft.world.level.timers.CustomFunctionCallbackTimerQueue;

public class CommandSchedule {

    private static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("commands.schedule.same_tick"));
    private static final DynamicCommandExceptionType b = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.schedule.cleared.failure", new Object[]{object});
    });
    private static final SuggestionProvider<CommandListenerWrapper> c = (commandcontext, suggestionsbuilder) -> {
        return ICompletionProvider.b((Iterable) ((CommandListenerWrapper) commandcontext.getSource()).getServer().getSaveData().H().u().a(), suggestionsbuilder);
    };

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("schedule").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.a("function").then(net.minecraft.commands.CommandDispatcher.a("function", (ArgumentType) ArgumentTag.a()).suggests(CommandFunction.a).then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("time", (ArgumentType) ArgumentTime.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentTag.b(commandcontext, "function"), IntegerArgumentType.getInteger(commandcontext, "time"), true);
        })).then(net.minecraft.commands.CommandDispatcher.a("append").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentTag.b(commandcontext, "function"), IntegerArgumentType.getInteger(commandcontext, "time"), false);
        }))).then(net.minecraft.commands.CommandDispatcher.a("replace").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentTag.b(commandcontext, "function"), IntegerArgumentType.getInteger(commandcontext, "time"), true);
        })))))).then(net.minecraft.commands.CommandDispatcher.a("clear").then(net.minecraft.commands.CommandDispatcher.a("function", (ArgumentType) StringArgumentType.greedyString()).suggests(CommandSchedule.c).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), StringArgumentType.getString(commandcontext, "function"));
        }))));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Pair<MinecraftKey, Either<CustomFunction, Tag<CustomFunction>>> pair, int i, boolean flag) throws CommandSyntaxException {
        if (i == 0) {
            throw CommandSchedule.a.create();
        } else {
            long j = commandlistenerwrapper.getWorld().getTime() + (long) i;
            MinecraftKey minecraftkey = (MinecraftKey) pair.getFirst();
            CustomFunctionCallbackTimerQueue<MinecraftServer> customfunctioncallbacktimerqueue = commandlistenerwrapper.getServer().getSaveData().H().u();

            ((Either) pair.getSecond()).ifLeft((customfunction) -> {
                String s = minecraftkey.toString();

                if (flag) {
                    customfunctioncallbacktimerqueue.a(s);
                }

                customfunctioncallbacktimerqueue.a(s, j, new CustomFunctionCallback(minecraftkey));
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.schedule.created.function", new Object[]{minecraftkey, i, j}), true);
            }).ifRight((tag) -> {
                String s = "#" + minecraftkey.toString();

                if (flag) {
                    customfunctioncallbacktimerqueue.a(s);
                }

                customfunctioncallbacktimerqueue.a(s, j, new CustomFunctionCallbackTag(minecraftkey));
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.schedule.created.tag", new Object[]{minecraftkey, i, j}), true);
            });
            return (int) Math.floorMod(j, 2147483647L);
        }
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, String s) throws CommandSyntaxException {
        int i = commandlistenerwrapper.getServer().getSaveData().H().u().a(s);

        if (i == 0) {
            throw CommandSchedule.b.create(s);
        } else {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.schedule.cleared.success", new Object[]{i, s}), true);
            return i;
        }
    }
}
