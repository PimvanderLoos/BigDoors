package net.minecraft.server.commands;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.ChatMessage;

public class CommandHelp {

    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new ChatMessage("commands.help.failed"));

    public CommandHelp() {}

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("help").executes((commandcontext) -> {
            Map<CommandNode<CommandListenerWrapper>, String> map = commanddispatcher.getSmartUsage(commanddispatcher.getRoot(), (CommandListenerWrapper) commandcontext.getSource());
            Iterator iterator = map.values().iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();

                ((CommandListenerWrapper) commandcontext.getSource()).sendMessage(new ChatComponentText("/" + s), false);
            }

            return map.size();
        })).then(net.minecraft.commands.CommandDispatcher.a("command", (ArgumentType) StringArgumentType.greedyString()).executes((commandcontext) -> {
            ParseResults<CommandListenerWrapper> parseresults = commanddispatcher.parse(StringArgumentType.getString(commandcontext, "command"), (CommandListenerWrapper) commandcontext.getSource());

            if (parseresults.getContext().getNodes().isEmpty()) {
                throw CommandHelp.ERROR_FAILED.create();
            } else {
                Map<CommandNode<CommandListenerWrapper>, String> map = commanddispatcher.getSmartUsage(((ParsedCommandNode) Iterables.getLast(parseresults.getContext().getNodes())).getNode(), (CommandListenerWrapper) commandcontext.getSource());
                Iterator iterator = map.values().iterator();

                while (iterator.hasNext()) {
                    String s = (String) iterator.next();
                    CommandListenerWrapper commandlistenerwrapper = (CommandListenerWrapper) commandcontext.getSource();
                    String s1 = parseresults.getReader().getString();

                    commandlistenerwrapper.sendMessage(new ChatComponentText("/" + s1 + " " + s), false);
                }

                return map.size();
            }
        })));
    }
}
