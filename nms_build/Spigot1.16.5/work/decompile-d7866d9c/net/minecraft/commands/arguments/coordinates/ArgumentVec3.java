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
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.world.phys.Vec3D;

public class ArgumentVec3 implements ArgumentType<IVectorPosition> {

    private static final Collection<String> c = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "0.1 -0.5 .9", "~0.5 ~1 ~-5");
    public static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("argument.pos3d.incomplete"));
    public static final SimpleCommandExceptionType b = new SimpleCommandExceptionType(new ChatMessage("argument.pos.mixed"));
    private final boolean d;

    public ArgumentVec3(boolean flag) {
        this.d = flag;
    }

    public static ArgumentVec3 a() {
        return new ArgumentVec3(true);
    }

    public static ArgumentVec3 a(boolean flag) {
        return new ArgumentVec3(flag);
    }

    public static Vec3D a(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        return ((IVectorPosition) commandcontext.getArgument(s, IVectorPosition.class)).a((CommandListenerWrapper) commandcontext.getSource());
    }

    public static IVectorPosition b(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return (IVectorPosition) commandcontext.getArgument(s, IVectorPosition.class);
    }

    public IVectorPosition parse(StringReader stringreader) throws CommandSyntaxException {
        return (IVectorPosition) (stringreader.canRead() && stringreader.peek() == '^' ? ArgumentVectorPosition.a(stringreader) : VectorPosition.a(stringreader, this.d));
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
                object = ((ICompletionProvider) commandcontext.getSource()).t();
            }

            return ICompletionProvider.a(s, (Collection) object, suggestionsbuilder, CommandDispatcher.a(this::parse));
        }
    }

    public Collection<String> getExamples() {
        return ArgumentVec3.c;
    }
}
