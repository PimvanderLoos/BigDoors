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
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.ScoreboardServer;
import net.minecraft.world.scores.ScoreboardObjective;

public class ArgumentScoreboardObjective implements ArgumentType<String> {

    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "*", "012");
    private static final DynamicCommandExceptionType ERROR_OBJECTIVE_NOT_FOUND = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("arguments.objective.notFound", new Object[]{object});
    });
    private static final DynamicCommandExceptionType ERROR_OBJECTIVE_READ_ONLY = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("arguments.objective.readonly", new Object[]{object});
    });

    public ArgumentScoreboardObjective() {}

    public static ArgumentScoreboardObjective objective() {
        return new ArgumentScoreboardObjective();
    }

    public static ScoreboardObjective getObjective(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        String s1 = (String) commandcontext.getArgument(s, String.class);
        ScoreboardServer scoreboardserver = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getScoreboard();
        ScoreboardObjective scoreboardobjective = scoreboardserver.getObjective(s1);

        if (scoreboardobjective == null) {
            throw ArgumentScoreboardObjective.ERROR_OBJECTIVE_NOT_FOUND.create(s1);
        } else {
            return scoreboardobjective;
        }
    }

    public static ScoreboardObjective getWritableObjective(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        ScoreboardObjective scoreboardobjective = getObjective(commandcontext, s);

        if (scoreboardobjective.getCriteria().isReadOnly()) {
            throw ArgumentScoreboardObjective.ERROR_OBJECTIVE_READ_ONLY.create(scoreboardobjective.getName());
        } else {
            return scoreboardobjective;
        }
    }

    public String parse(StringReader stringreader) throws CommandSyntaxException {
        return stringreader.readUnquotedString();
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        if (commandcontext.getSource() instanceof CommandListenerWrapper) {
            return ICompletionProvider.suggest((Iterable) ((CommandListenerWrapper) commandcontext.getSource()).getServer().getScoreboard().getObjectiveNames(), suggestionsbuilder);
        } else if (commandcontext.getSource() instanceof ICompletionProvider) {
            ICompletionProvider icompletionprovider = (ICompletionProvider) commandcontext.getSource();

            return icompletionprovider.customSuggestion(commandcontext, suggestionsbuilder);
        } else {
            return Suggestions.empty();
        }
    }

    public Collection<String> getExamples() {
        return ArgumentScoreboardObjective.EXAMPLES;
    }
}
