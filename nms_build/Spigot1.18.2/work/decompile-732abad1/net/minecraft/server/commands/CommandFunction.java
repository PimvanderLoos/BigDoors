package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
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

    public static final SuggestionProvider<CommandListenerWrapper> SUGGEST_FUNCTION = (commandcontext, suggestionsbuilder) -> {
        CustomFunctionData customfunctiondata = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getFunctions();

        ICompletionProvider.suggestResource(customfunctiondata.getTagNames(), suggestionsbuilder, "#");
        return ICompletionProvider.suggestResource(customfunctiondata.getFunctionNames(), suggestionsbuilder);
    };

    public CommandFunction() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("function").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.argument("name", ArgumentTag.functions()).suggests(CommandFunction.SUGGEST_FUNCTION).executes((commandcontext) -> {
            return runFunction((CommandListenerWrapper) commandcontext.getSource(), ArgumentTag.getFunctions(commandcontext, "name"));
        })));
    }

    private static int runFunction(CommandListenerWrapper commandlistenerwrapper, Collection<CustomFunction> collection) {
        int i = 0;

        CustomFunction customfunction;

        for (Iterator iterator = collection.iterator(); iterator.hasNext(); i += commandlistenerwrapper.getServer().getFunctions().execute(customfunction, commandlistenerwrapper.withSuppressedOutput().withMaximumPermission(2))) {
            customfunction = (CustomFunction) iterator.next();
        }

        if (collection.size() == 1) {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.function.success.single", new Object[]{i, ((CustomFunction) collection.iterator().next()).getId()}), true);
        } else {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.function.success.multiple", new Object[]{i, collection.size()}), true);
        }

        return i;
    }
}
