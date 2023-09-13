package net.minecraft.commands.arguments;

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
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.scores.ScoreboardScore;

public class ArgumentMathOperation implements ArgumentType<ArgumentMathOperation.a> {

    private static final Collection<String> EXAMPLES = Arrays.asList("=", ">", "<");
    private static final SimpleCommandExceptionType ERROR_INVALID_OPERATION = new SimpleCommandExceptionType(IChatBaseComponent.translatable("arguments.operation.invalid"));
    private static final SimpleCommandExceptionType ERROR_DIVIDE_BY_ZERO = new SimpleCommandExceptionType(IChatBaseComponent.translatable("arguments.operation.div0"));

    public ArgumentMathOperation() {}

    public static ArgumentMathOperation operation() {
        return new ArgumentMathOperation();
    }

    public static ArgumentMathOperation.a getOperation(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return (ArgumentMathOperation.a) commandcontext.getArgument(s, ArgumentMathOperation.a.class);
    }

    public ArgumentMathOperation.a parse(StringReader stringreader) throws CommandSyntaxException {
        if (!stringreader.canRead()) {
            throw ArgumentMathOperation.ERROR_INVALID_OPERATION.create();
        } else {
            int i = stringreader.getCursor();

            while (stringreader.canRead() && stringreader.peek() != ' ') {
                stringreader.skip();
            }

            return getOperation(stringreader.getString().substring(i, stringreader.getCursor()));
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        return ICompletionProvider.suggest(new String[]{"=", "+=", "-=", "*=", "/=", "%=", "<", ">", "><"}, suggestionsbuilder);
    }

    public Collection<String> getExamples() {
        return ArgumentMathOperation.EXAMPLES;
    }

    private static ArgumentMathOperation.a getOperation(String s) throws CommandSyntaxException {
        return (ArgumentMathOperation.a) (s.equals("><") ? (scoreboardscore, scoreboardscore1) -> {
            int i = scoreboardscore.getScore();

            scoreboardscore.setScore(scoreboardscore1.getScore());
            scoreboardscore1.setScore(i);
        } : getSimpleOperation(s));
    }

    private static ArgumentMathOperation.b getSimpleOperation(String s) throws CommandSyntaxException {
        byte b0 = -1;

        switch (s.hashCode()) {
            case 60:
                if (s.equals("<")) {
                    b0 = 6;
                }
                break;
            case 61:
                if (s.equals("=")) {
                    b0 = 0;
                }
                break;
            case 62:
                if (s.equals(">")) {
                    b0 = 7;
                }
                break;
            case 1208:
                if (s.equals("%=")) {
                    b0 = 5;
                }
                break;
            case 1363:
                if (s.equals("*=")) {
                    b0 = 3;
                }
                break;
            case 1394:
                if (s.equals("+=")) {
                    b0 = 1;
                }
                break;
            case 1456:
                if (s.equals("-=")) {
                    b0 = 2;
                }
                break;
            case 1518:
                if (s.equals("/=")) {
                    b0 = 4;
                }
        }

        switch (b0) {
            case 0:
                return (i, j) -> {
                    return j;
                };
            case 1:
                return (i, j) -> {
                    return i + j;
                };
            case 2:
                return (i, j) -> {
                    return i - j;
                };
            case 3:
                return (i, j) -> {
                    return i * j;
                };
            case 4:
                return (i, j) -> {
                    if (j == 0) {
                        throw ArgumentMathOperation.ERROR_DIVIDE_BY_ZERO.create();
                    } else {
                        return MathHelper.floorDiv(i, j);
                    }
                };
            case 5:
                return (i, j) -> {
                    if (j == 0) {
                        throw ArgumentMathOperation.ERROR_DIVIDE_BY_ZERO.create();
                    } else {
                        return MathHelper.positiveModulo(i, j);
                    }
                };
            case 6:
                return Math::min;
            case 7:
                return Math::max;
            default:
                throw ArgumentMathOperation.ERROR_INVALID_OPERATION.create();
        }
    }

    @FunctionalInterface
    public interface a {

        void apply(ScoreboardScore scoreboardscore, ScoreboardScore scoreboardscore1) throws CommandSyntaxException;
    }

    @FunctionalInterface
    private interface b extends ArgumentMathOperation.a {

        int apply(int i, int j) throws CommandSyntaxException;

        @Override
        default void apply(ScoreboardScore scoreboardscore, ScoreboardScore scoreboardscore1) throws CommandSyntaxException {
            scoreboardscore.setScore(this.apply(scoreboardscore.getScore(), scoreboardscore1.getScore()));
        }
    }
}
