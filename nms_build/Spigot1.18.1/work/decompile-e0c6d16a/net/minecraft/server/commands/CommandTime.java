package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Iterator;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentTime;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.level.WorldServer;

public class CommandTime {

    public CommandTime() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("time").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("set").then(net.minecraft.commands.CommandDispatcher.literal("day").executes((commandcontext) -> {
            return setTime((CommandListenerWrapper) commandcontext.getSource(), 1000);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("noon").executes((commandcontext) -> {
            return setTime((CommandListenerWrapper) commandcontext.getSource(), 6000);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("night").executes((commandcontext) -> {
            return setTime((CommandListenerWrapper) commandcontext.getSource(), 13000);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("midnight").executes((commandcontext) -> {
            return setTime((CommandListenerWrapper) commandcontext.getSource(), 18000);
        }))).then(net.minecraft.commands.CommandDispatcher.argument("time", ArgumentTime.time()).executes((commandcontext) -> {
            return setTime((CommandListenerWrapper) commandcontext.getSource(), IntegerArgumentType.getInteger(commandcontext, "time"));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("add").then(net.minecraft.commands.CommandDispatcher.argument("time", ArgumentTime.time()).executes((commandcontext) -> {
            return addTime((CommandListenerWrapper) commandcontext.getSource(), IntegerArgumentType.getInteger(commandcontext, "time"));
        })))).then(((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("query").then(net.minecraft.commands.CommandDispatcher.literal("daytime").executes((commandcontext) -> {
            return queryTime((CommandListenerWrapper) commandcontext.getSource(), getDayTime(((CommandListenerWrapper) commandcontext.getSource()).getLevel()));
        }))).then(net.minecraft.commands.CommandDispatcher.literal("gametime").executes((commandcontext) -> {
            return queryTime((CommandListenerWrapper) commandcontext.getSource(), (int) (((CommandListenerWrapper) commandcontext.getSource()).getLevel().getGameTime() % 2147483647L));
        }))).then(net.minecraft.commands.CommandDispatcher.literal("day").executes((commandcontext) -> {
            return queryTime((CommandListenerWrapper) commandcontext.getSource(), (int) (((CommandListenerWrapper) commandcontext.getSource()).getLevel().getDayTime() / 24000L % 2147483647L));
        }))));
    }

    private static int getDayTime(WorldServer worldserver) {
        return (int) (worldserver.getDayTime() % 24000L);
    }

    private static int queryTime(CommandListenerWrapper commandlistenerwrapper, int i) {
        commandlistenerwrapper.sendSuccess(new ChatMessage("commands.time.query", new Object[]{i}), false);
        return i;
    }

    public static int setTime(CommandListenerWrapper commandlistenerwrapper, int i) {
        Iterator iterator = commandlistenerwrapper.getServer().getAllLevels().iterator();

        while (iterator.hasNext()) {
            WorldServer worldserver = (WorldServer) iterator.next();

            worldserver.setDayTime((long) i);
        }

        commandlistenerwrapper.sendSuccess(new ChatMessage("commands.time.set", new Object[]{i}), true);
        return getDayTime(commandlistenerwrapper.getLevel());
    }

    public static int addTime(CommandListenerWrapper commandlistenerwrapper, int i) {
        Iterator iterator = commandlistenerwrapper.getServer().getAllLevels().iterator();

        while (iterator.hasNext()) {
            WorldServer worldserver = (WorldServer) iterator.next();

            worldserver.setDayTime(worldserver.getDayTime() + (long) i);
        }

        int j = getDayTime(commandlistenerwrapper.getLevel());

        commandlistenerwrapper.sendSuccess(new ChatMessage("commands.time.set", new Object[]{j}), true);
        return j;
    }
}
