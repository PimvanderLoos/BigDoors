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
import net.minecraft.server.ScoreboardServer;
import net.minecraft.world.scores.ScoreboardTeam;

public class ArgumentScoreboardTeam implements ArgumentType<String> {

    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "123");
    private static final DynamicCommandExceptionType ERROR_TEAM_NOT_FOUND = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("team.notFound", object);
    });

    public ArgumentScoreboardTeam() {}

    public static ArgumentScoreboardTeam team() {
        return new ArgumentScoreboardTeam();
    }

    public static ScoreboardTeam getTeam(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        String s1 = (String) commandcontext.getArgument(s, String.class);
        ScoreboardServer scoreboardserver = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getScoreboard();
        ScoreboardTeam scoreboardteam = scoreboardserver.getPlayerTeam(s1);

        if (scoreboardteam == null) {
            throw ArgumentScoreboardTeam.ERROR_TEAM_NOT_FOUND.create(s1);
        } else {
            return scoreboardteam;
        }
    }

    public String parse(StringReader stringreader) throws CommandSyntaxException {
        return stringreader.readUnquotedString();
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        return commandcontext.getSource() instanceof ICompletionProvider ? ICompletionProvider.suggest((Iterable) ((ICompletionProvider) commandcontext.getSource()).getAllTeams(), suggestionsbuilder) : Suggestions.empty();
    }

    public Collection<String> getExamples() {
        return ArgumentScoreboardTeam.EXAMPLES;
    }
}
