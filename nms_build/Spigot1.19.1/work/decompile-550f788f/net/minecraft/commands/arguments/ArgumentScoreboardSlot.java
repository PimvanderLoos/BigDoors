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
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.scores.Scoreboard;

public class ArgumentScoreboardSlot implements ArgumentType<Integer> {

    private static final Collection<String> EXAMPLES = Arrays.asList("sidebar", "foo.bar");
    public static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("argument.scoreboardDisplaySlot.invalid", object);
    });

    private ArgumentScoreboardSlot() {}

    public static ArgumentScoreboardSlot displaySlot() {
        return new ArgumentScoreboardSlot();
    }

    public static int getDisplaySlot(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return (Integer) commandcontext.getArgument(s, Integer.class);
    }

    public Integer parse(StringReader stringreader) throws CommandSyntaxException {
        String s = stringreader.readUnquotedString();
        int i = Scoreboard.getDisplaySlotByName(s);

        if (i == -1) {
            throw ArgumentScoreboardSlot.ERROR_INVALID_VALUE.create(s);
        } else {
            return i;
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        return ICompletionProvider.suggest(Scoreboard.getDisplaySlotNames(), suggestionsbuilder);
    }

    public Collection<String> getExamples() {
        return ArgumentScoreboardSlot.EXAMPLES;
    }
}
