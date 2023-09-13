package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.coordinates.ArgumentVec2;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec2F;

public class CommandWorldBorder {

    private static final SimpleCommandExceptionType ERROR_SAME_CENTER = new SimpleCommandExceptionType(new ChatMessage("commands.worldborder.center.failed"));
    private static final SimpleCommandExceptionType ERROR_SAME_SIZE = new SimpleCommandExceptionType(new ChatMessage("commands.worldborder.set.failed.nochange"));
    private static final SimpleCommandExceptionType ERROR_TOO_SMALL = new SimpleCommandExceptionType(new ChatMessage("commands.worldborder.set.failed.small"));
    private static final SimpleCommandExceptionType ERROR_TOO_BIG = new SimpleCommandExceptionType(new ChatMessage("commands.worldborder.set.failed.big", new Object[]{5.9999968E7D}));
    private static final SimpleCommandExceptionType ERROR_SAME_WARNING_TIME = new SimpleCommandExceptionType(new ChatMessage("commands.worldborder.warning.time.failed"));
    private static final SimpleCommandExceptionType ERROR_SAME_WARNING_DISTANCE = new SimpleCommandExceptionType(new ChatMessage("commands.worldborder.warning.distance.failed"));
    private static final SimpleCommandExceptionType ERROR_SAME_DAMAGE_BUFFER = new SimpleCommandExceptionType(new ChatMessage("commands.worldborder.damage.buffer.failed"));
    private static final SimpleCommandExceptionType ERROR_SAME_DAMAGE_AMOUNT = new SimpleCommandExceptionType(new ChatMessage("commands.worldborder.damage.amount.failed"));

    public CommandWorldBorder() {}

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("worldborder").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.a("add").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("distance", (ArgumentType) DoubleArgumentType.doubleArg(-5.9999968E7D, 5.9999968E7D)).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ((CommandListenerWrapper) commandcontext.getSource()).getWorld().getWorldBorder().getSize() + DoubleArgumentType.getDouble(commandcontext, "distance"), 0L);
        })).then(net.minecraft.commands.CommandDispatcher.a("time", (ArgumentType) IntegerArgumentType.integer(0)).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ((CommandListenerWrapper) commandcontext.getSource()).getWorld().getWorldBorder().getSize() + DoubleArgumentType.getDouble(commandcontext, "distance"), ((CommandListenerWrapper) commandcontext.getSource()).getWorld().getWorldBorder().j() + (long) IntegerArgumentType.getInteger(commandcontext, "time") * 1000L);
        }))))).then(net.minecraft.commands.CommandDispatcher.a("set").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("distance", (ArgumentType) DoubleArgumentType.doubleArg(-5.9999968E7D, 5.9999968E7D)).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), DoubleArgumentType.getDouble(commandcontext, "distance"), 0L);
        })).then(net.minecraft.commands.CommandDispatcher.a("time", (ArgumentType) IntegerArgumentType.integer(0)).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), DoubleArgumentType.getDouble(commandcontext, "distance"), (long) IntegerArgumentType.getInteger(commandcontext, "time") * 1000L);
        }))))).then(net.minecraft.commands.CommandDispatcher.a("center").then(net.minecraft.commands.CommandDispatcher.a("pos", (ArgumentType) ArgumentVec2.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentVec2.a(commandcontext, "pos"));
        })))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("damage").then(net.minecraft.commands.CommandDispatcher.a("amount").then(net.minecraft.commands.CommandDispatcher.a("damagePerBlock", (ArgumentType) FloatArgumentType.floatArg(0.0F)).executes((commandcontext) -> {
            return b((CommandListenerWrapper) commandcontext.getSource(), FloatArgumentType.getFloat(commandcontext, "damagePerBlock"));
        })))).then(net.minecraft.commands.CommandDispatcher.a("buffer").then(net.minecraft.commands.CommandDispatcher.a("distance", (ArgumentType) FloatArgumentType.floatArg(0.0F)).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), FloatArgumentType.getFloat(commandcontext, "distance"));
        }))))).then(net.minecraft.commands.CommandDispatcher.a("get").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource());
        }))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("warning").then(net.minecraft.commands.CommandDispatcher.a("distance").then(net.minecraft.commands.CommandDispatcher.a("distance", (ArgumentType) IntegerArgumentType.integer(0)).executes((commandcontext) -> {
            return b((CommandListenerWrapper) commandcontext.getSource(), IntegerArgumentType.getInteger(commandcontext, "distance"));
        })))).then(net.minecraft.commands.CommandDispatcher.a("time").then(net.minecraft.commands.CommandDispatcher.a("time", (ArgumentType) IntegerArgumentType.integer(0)).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), IntegerArgumentType.getInteger(commandcontext, "time"));
        })))));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, float f) throws CommandSyntaxException {
        WorldBorder worldborder = commandlistenerwrapper.getWorld().getWorldBorder();

        if (worldborder.getDamageBuffer() == (double) f) {
            throw CommandWorldBorder.ERROR_SAME_DAMAGE_BUFFER.create();
        } else {
            worldborder.setDamageBuffer((double) f);
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.worldborder.damage.buffer.success", new Object[]{String.format(Locale.ROOT, "%.2f", f)}), true);
            return (int) f;
        }
    }

    private static int b(CommandListenerWrapper commandlistenerwrapper, float f) throws CommandSyntaxException {
        WorldBorder worldborder = commandlistenerwrapper.getWorld().getWorldBorder();

        if (worldborder.getDamageAmount() == (double) f) {
            throw CommandWorldBorder.ERROR_SAME_DAMAGE_AMOUNT.create();
        } else {
            worldborder.setDamageAmount((double) f);
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.worldborder.damage.amount.success", new Object[]{String.format(Locale.ROOT, "%.2f", f)}), true);
            return (int) f;
        }
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, int i) throws CommandSyntaxException {
        WorldBorder worldborder = commandlistenerwrapper.getWorld().getWorldBorder();

        if (worldborder.getWarningTime() == i) {
            throw CommandWorldBorder.ERROR_SAME_WARNING_TIME.create();
        } else {
            worldborder.setWarningTime(i);
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.worldborder.warning.time.success", new Object[]{i}), true);
            return i;
        }
    }

    private static int b(CommandListenerWrapper commandlistenerwrapper, int i) throws CommandSyntaxException {
        WorldBorder worldborder = commandlistenerwrapper.getWorld().getWorldBorder();

        if (worldborder.getWarningDistance() == i) {
            throw CommandWorldBorder.ERROR_SAME_WARNING_DISTANCE.create();
        } else {
            worldborder.setWarningDistance(i);
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.worldborder.warning.distance.success", new Object[]{i}), true);
            return i;
        }
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper) {
        double d0 = commandlistenerwrapper.getWorld().getWorldBorder().getSize();

        commandlistenerwrapper.sendMessage(new ChatMessage("commands.worldborder.get", new Object[]{String.format(Locale.ROOT, "%.0f", d0)}), false);
        return MathHelper.floor(d0 + 0.5D);
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Vec2F vec2f) throws CommandSyntaxException {
        WorldBorder worldborder = commandlistenerwrapper.getWorld().getWorldBorder();

        if (worldborder.getCenterX() == (double) vec2f.x && worldborder.getCenterZ() == (double) vec2f.y) {
            throw CommandWorldBorder.ERROR_SAME_CENTER.create();
        } else {
            worldborder.setCenter((double) vec2f.x, (double) vec2f.y);
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.worldborder.center.success", new Object[]{String.format(Locale.ROOT, "%.2f", vec2f.x), String.format("%.2f", vec2f.y)}), true);
            return 0;
        }
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, double d0, long i) throws CommandSyntaxException {
        WorldBorder worldborder = commandlistenerwrapper.getWorld().getWorldBorder();
        double d1 = worldborder.getSize();

        if (d1 == d0) {
            throw CommandWorldBorder.ERROR_SAME_SIZE.create();
        } else if (d0 < 1.0D) {
            throw CommandWorldBorder.ERROR_TOO_SMALL.create();
        } else if (d0 > 5.9999968E7D) {
            throw CommandWorldBorder.ERROR_TOO_BIG.create();
        } else {
            if (i > 0L) {
                worldborder.transitionSizeBetween(d1, d0, i);
                if (d0 > d1) {
                    commandlistenerwrapper.sendMessage(new ChatMessage("commands.worldborder.set.grow", new Object[]{String.format(Locale.ROOT, "%.1f", d0), Long.toString(i / 1000L)}), true);
                } else {
                    commandlistenerwrapper.sendMessage(new ChatMessage("commands.worldborder.set.shrink", new Object[]{String.format(Locale.ROOT, "%.1f", d0), Long.toString(i / 1000L)}), true);
                }
            } else {
                worldborder.setSize(d0);
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.worldborder.set.immediate", new Object[]{String.format(Locale.ROOT, "%.1f", d0)}), true);
            }

            return (int) (d0 - d1);
        }
    }
}
