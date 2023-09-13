package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandDispatcher;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.BlockPosition2D;

public class ArgumentVec2I implements ArgumentType<IVectorPosition> {

    private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "~ ~", "~1 ~-2", "^ ^", "^-1 ^0");
    public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(IChatBaseComponent.translatable("argument.pos2d.incomplete"));

    public ArgumentVec2I() {}

    public static ArgumentVec2I columnPos() {
        return new ArgumentVec2I();
    }

    public static BlockPosition2D getColumnPos(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        BlockPosition blockposition = ((IVectorPosition) commandcontext.getArgument(s, IVectorPosition.class)).getBlockPos((CommandListenerWrapper) commandcontext.getSource());

        return new BlockPosition2D(blockposition.getX(), blockposition.getZ());
    }

    public IVectorPosition parse(StringReader stringreader) throws CommandSyntaxException {
        int i = stringreader.getCursor();

        if (!stringreader.canRead()) {
            throw ArgumentVec2I.ERROR_NOT_COMPLETE.createWithContext(stringreader);
        } else {
            ArgumentParserPosition argumentparserposition = ArgumentParserPosition.parseInt(stringreader);

            if (stringreader.canRead() && stringreader.peek() == ' ') {
                stringreader.skip();
                ArgumentParserPosition argumentparserposition1 = ArgumentParserPosition.parseInt(stringreader);

                return new VectorPosition(argumentparserposition, new ArgumentParserPosition(true, 0.0D), argumentparserposition1);
            } else {
                stringreader.setCursor(i);
                throw ArgumentVec2I.ERROR_NOT_COMPLETE.createWithContext(stringreader);
            }
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        if (!(commandcontext.getSource() instanceof ICompletionProvider)) {
            return Suggestions.empty();
        } else {
            String s = suggestionsbuilder.getRemaining();
            Object object;

            if (!s.isEmpty() && s.charAt(0) == '^') {
                object = Collections.singleton(ICompletionProvider.b.DEFAULT_LOCAL);
            } else {
                object = ((ICompletionProvider) commandcontext.getSource()).getRelevantCoordinates();
            }

            return ICompletionProvider.suggest2DCoordinates(s, (Collection) object, suggestionsbuilder, CommandDispatcher.createValidator(this::parse));
        }
    }

    public Collection<String> getExamples() {
        return ArgumentVec2I.EXAMPLES;
    }
}
