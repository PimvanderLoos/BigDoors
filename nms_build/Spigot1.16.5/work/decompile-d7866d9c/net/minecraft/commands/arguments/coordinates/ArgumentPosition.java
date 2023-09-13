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
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.level.WorldServer;

public class ArgumentPosition implements ArgumentType<IVectorPosition> {

    private static final Collection<String> c = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "~0.5 ~1 ~-5");
    public static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("argument.pos.unloaded"));
    public static final SimpleCommandExceptionType b = new SimpleCommandExceptionType(new ChatMessage("argument.pos.outofworld"));

    public ArgumentPosition() {}

    public static ArgumentPosition a() {
        return new ArgumentPosition();
    }

    public static BlockPosition a(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        BlockPosition blockposition = ((IVectorPosition) commandcontext.getArgument(s, IVectorPosition.class)).c((CommandListenerWrapper) commandcontext.getSource());

        if (!((CommandListenerWrapper) commandcontext.getSource()).getWorld().isLoaded(blockposition)) {
            throw ArgumentPosition.a.create();
        } else {
            ((CommandListenerWrapper) commandcontext.getSource()).getWorld();
            if (!WorldServer.isValidLocation(blockposition)) {
                throw ArgumentPosition.b.create();
            } else {
                return blockposition;
            }
        }
    }

    public static BlockPosition b(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        return ((IVectorPosition) commandcontext.getArgument(s, IVectorPosition.class)).c((CommandListenerWrapper) commandcontext.getSource());
    }

    public IVectorPosition parse(StringReader stringreader) throws CommandSyntaxException {
        return (IVectorPosition) (stringreader.canRead() && stringreader.peek() == '^' ? ArgumentVectorPosition.a(stringreader) : VectorPosition.a(stringreader));
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandcontext, SuggestionsBuilder suggestionsbuilder) {
        if (!(commandcontext.getSource() instanceof ICompletionProvider)) {
            return Suggestions.empty();
        } else {
            String s = suggestionsbuilder.getRemaining();
            Object object;

            if (!s.isEmpty() && s.charAt(0) == '^') {
                object = Collections.singleton(ICompletionProvider.a.a);
            } else {
                object = ((ICompletionProvider) commandcontext.getSource()).s();
            }

            return ICompletionProvider.a(s, (Collection) object, suggestionsbuilder, CommandDispatcher.a(this::parse));
        }
    }

    public Collection<String> getExamples() {
        return ArgumentPosition.c;
    }
}
