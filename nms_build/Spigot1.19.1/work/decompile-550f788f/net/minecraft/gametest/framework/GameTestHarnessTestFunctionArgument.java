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
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;

public class GameTestHarnessTestFunctionArgument implements ArgumentType<GameTestHarnessTestFunction> {

    private static final Collection<String> EXAMPLES = Arrays.asList("techtests.piston", "techtests");

    public GameTestHarnessTestFunctionArgument() {}

    public GameTestHarnessTestFunction parse(StringReader stringreader) throws CommandSyntaxException {
        String s = stringreader.readUnquotedString();
        Optional<GameTestHarnessTestFunction> optional = GameTestHarnessRegistry.findTestFunction(s);

        if (optional.isPresent()) {
            return (GameTestHarnessTestFunction) optional.get();
        } else {
            IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.literal("No such test: " + s);

            throw new CommandSyntaxException(new SimpleCommandExceptionType(ichatmutablecomponent), ichatmutablecomponent);
        }
    }

    public static GameTestHarnessTestFunctionArgument testFunctionArgument() {
        return new GameTestHarnessTestFunctionArgument();
    }

    public static GameTestHarnessTestFunction getTestFunction(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return (GameTestHarnessTestFunction) commandcontext.getArgument(s, GameTestHarnessTestFunction.class);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        Stream<String> stream = GameTestHarnessRegistry.getAllTestFunctions().stream().map(GameTestHarnessTestFunction::getTestName);

        return ICompletionProvider.suggest(stream, suggestionsbuilder);
    }

    public Collection<String> getExamples() {
        return GameTestHarnessTestFunctionArgument.EXAMPLES;
    }
}
