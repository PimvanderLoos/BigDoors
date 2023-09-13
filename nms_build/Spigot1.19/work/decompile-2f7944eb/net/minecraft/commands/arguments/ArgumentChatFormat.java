package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.EnumChatFormat;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.network.chat.IChatBaseComponent;

public class ArgumentChatFormat implements ArgumentType<EnumChatFormat> {

    private static final Collection<String> EXAMPLES = Arrays.asList("red", "green");
    public static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("argument.color.invalid", object);
    });

    private ArgumentChatFormat() {}

    public static ArgumentChatFormat color() {
        return new ArgumentChatFormat();
    }

    public static EnumChatFormat getColor(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return (EnumChatFormat) commandcontext.getArgument(s, EnumChatFormat.class);
    }

    public EnumChatFormat parse(StringReader stringreader) throws CommandSyntaxException {
        String s = stringreader.readUnquotedString();
        EnumChatFormat enumchatformat = EnumChatFormat.getByName(s);

        if (enumchatformat != null && !enumchatformat.isFormat()) {
            return enumchatformat;
        } else {
            throw ArgumentChatFormat.ERROR_INVALID_VALUE.create(s);
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        return ICompletionProvider.suggest((Iterable) EnumChatFormat.getNames(true, false), suggestionsbuilder);
    }

    public Collection<String> getExamples() {
        return ArgumentChatFormat.EXAMPLES;
    }
}
