package net.minecraft.server;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ArgumentTag implements ArgumentType<ArgumentTag.a> {

    private static final Collection<String> a = Arrays.asList(new String[] { "foo", "foo:bar", "#foo"});
    private static final DynamicCommandExceptionType b = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("arguments.function.tag.unknown", new Object[] { object});
    });
    private static final DynamicCommandExceptionType c = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("arguments.function.unknown", new Object[] { object});
    });

    public ArgumentTag() {}

    public static ArgumentTag a() {
        return new ArgumentTag();
    }

    public ArgumentTag.a a(StringReader stringreader) throws CommandSyntaxException {
        MinecraftKey minecraftkey;

        if (stringreader.canRead() && stringreader.peek() == 35) {
            stringreader.skip();
            minecraftkey = MinecraftKey.a(stringreader);
            return (commandcontext) -> {
                Tag tag = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getFunctionData().g().a(minecraftkey);

                if (tag == null) {
                    throw ArgumentTag.b.create(minecraftkey.toString());
                } else {
                    return tag.a();
                }
            };
        } else {
            minecraftkey = MinecraftKey.a(stringreader);
            return (commandcontext) -> {
                CustomFunction customfunction = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getFunctionData().a(minecraftkey);

                if (customfunction == null) {
                    throw ArgumentTag.c.create(minecraftkey.toString());
                } else {
                    return Collections.singleton(customfunction);
                }
            };
        }
    }

    public static Collection<CustomFunction> a(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        return ((ArgumentTag.a) commandcontext.getArgument(s, ArgumentTag.a.class)).create(commandcontext);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        StringReader stringreader = new StringReader(suggestionsbuilder.getInput());

        stringreader.setCursor(suggestionsbuilder.getStart());
        ArgumentParserItemStack argumentparseritemstack = new ArgumentParserItemStack(stringreader, true);

        try {
            argumentparseritemstack.h();
        } catch (CommandSyntaxException commandsyntaxexception) {
            ;
        }

        return argumentparseritemstack.a(suggestionsbuilder);
    }

    public Collection<String> getExamples() {
        return ArgumentTag.a;
    }

    public Object parse(StringReader stringreader) throws CommandSyntaxException {
        return this.a(stringreader);
    }

    public interface a {

        Collection<CustomFunction> create(CommandContext<CommandListenerWrapper> commandcontext) throws CommandSyntaxException;
    }
}
