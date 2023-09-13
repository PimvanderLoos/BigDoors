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
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.network.chat.ChatComponentText;

public class GameTestHarnessTestFunctionArgument implements ArgumentType<GameTestHarnessTestFunction> {

    private static final Collection<String> a = Arrays.asList("techtests.piston", "techtests");

    public GameTestHarnessTestFunctionArgument() {}

    public GameTestHarnessTestFunction parse(StringReader stringreader) throws CommandSyntaxException {
        String s = stringreader.readUnquotedString();
        Optional<GameTestHarnessTestFunction> optional = GameTestHarnessRegistry.d(s);

        if (optional.isPresent()) {
            return (GameTestHarnessTestFunction) optional.get();
        } else {
            ChatComponentText chatcomponenttext = new ChatComponentText("No such test: " + s);

            throw new CommandSyntaxException(new SimpleCommandExceptionType(chatcomponenttext), chatcomponenttext);
        }
    }

    public static GameTestHarnessTestFunctionArgument a() {
        return new GameTestHarnessTestFunctionArgument();
    }

    public static GameTestHarnessTestFunction a(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return (GameTestHarnessTestFunction) commandcontext.getArgument(s, GameTestHarnessTestFunction.class);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        Stream<String> stream = GameTestHarnessRegistry.a().stream().map(GameTestHarnessTestFunction::a);

        return ICompletionProvider.b(stream, suggestionsbuilder);
    }

    public Collection<String> getExamples() {
        return GameTestHarnessTestFunctionArgument.a;
    }
}
