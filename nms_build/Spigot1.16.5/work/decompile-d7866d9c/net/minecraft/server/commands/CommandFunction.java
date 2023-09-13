package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.CustomFunction;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.commands.arguments.item.ArgumentTag;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.CustomFunctionData;

public class CommandFunction {

    public static final SuggestionProvider<CommandListenerWrapper> a = (commandcontext, suggestionsbuilder) -> {
        CustomFunctionData customfunctiondata = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getFunctionData();

        ICompletionProvider.a(customfunctiondata.g(), suggestionsbuilder, "#");
        return ICompletionProvider.a(customfunctiondata.f(), suggestionsbuilder);
    };

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("function").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.a("name", (ArgumentType) ArgumentTag.a()).suggests(CommandFunction.a).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentTag.a(commandcontext, "name"));
        })));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Collection<CustomFunction> collection) {
        int i = 0;

        CustomFunction customfunction;

        for (Iterator iterator = collection.iterator(); iterator.hasNext(); i += commandlistenerwrapper.getServer().getFunctionData().a(customfunction, commandlistenerwrapper.a().b(2))) {
            customfunction = (CustomFunction) iterator.next();
        }

        if (collection.size() == 1) {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.function.success.single", new Object[]{i, ((CustomFunction) collection.iterator().next()).a()}), true);
        } else {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.function.success.multiple", new Object[]{i, collection.size()}), true);
        }

        return i;
    }
}
