package net.minecraft.gametest.framework;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.network.chat.ChatComponentText;

public class GameTestHarnessTestClassArgument implements ArgumentType<String> {

    private static final Collection<String> a = Arrays.asList("techtests", "mobtests");

    public GameTestHarnessTestClassArgument() {}

    public String parse(StringReader stringreader) throws CommandSyntaxException {
        String s = stringreader.readUnquotedString();

        if (GameTestHarnessRegistry.b(s)) {
            return s;
        } else {
            ChatComponentText chatcomponenttext = new ChatComponentText("No such test class: " + s);

            throw new CommandSyntaxException(new SimpleCommandExceptionType(chatcomponenttext), chatcomponenttext);
        }
    }

    public static GameTestHarnessTestClassArgument a() {
        return new GameTestHarnessTestClassArgument();
    }

    public static String a(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return (String) commandcontext.getArgument(s, String.class);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        return ICompletionProvider.b(GameTestHarnessRegistry.b().stream(), suggestionsbuilder);
    }

    public Collection<String> getExamples() {
        return GameTestHarnessTestClassArgument.a;
    }
}
