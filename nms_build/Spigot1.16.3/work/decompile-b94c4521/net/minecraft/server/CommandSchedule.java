package net.minecraft.server;

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

public class CommandSchedule {

    private static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("commands.schedule.same_tick"));
    private static final DynamicCommandExceptionType b = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.schedule.cleared.failure", new Object[]{object});
    });
    private static final SuggestionProvider<CommandListenerWrapper> c = (commandcontext, suggestionsbuilder) -> {
        return ICompletionProvider.b((Iterable) ((CommandListenerWrapper) commandcontext.getSource()).getServer().getSaveData().H().u().a(), suggestionsbuilder);
    };

    public static void a(com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> com_mojang_brigadier_commanddispatcher) {
        com_mojang_brigadier_commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandDispatcher.a("schedule").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(CommandDispatcher.a("function").then(CommandDispatcher.a("function", (ArgumentType) ArgumentTag.a()).suggests(CommandFunction.a).then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) CommandDispatcher.a("time", (ArgumentType) ArgumentTime.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentTag.b(commandcontext, "function"), IntegerArgumentType.getInteger(commandcontext, "time"), true);
        })).then(CommandDispatcher.a("append").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentTag.b(commandcontext, "function"), IntegerArgumentType.getInteger(commandcontext, "time"), false);
        }))).then(CommandDispatcher.a("replace").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentTag.b(commandcontext, "function"), IntegerArgumentType.getInteger(commandcontext, "time"), true);
        })))))).then(CommandDispatcher.a("clear").then(CommandDispatcher.a("function", (ArgumentType) StringArgumentType.greedyString()).suggests(CommandSchedule.c).executes((commandcontext) -> {
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
