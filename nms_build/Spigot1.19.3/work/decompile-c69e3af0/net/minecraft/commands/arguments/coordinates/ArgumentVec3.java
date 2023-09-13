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
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.phys.Vec3D;

public class ArgumentVec3 implements ArgumentType<IVectorPosition> {

    private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "0.1 -0.5 .9", "~0.5 ~1 ~-5");
    public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(IChatBaseComponent.translatable("argument.pos3d.incomplete"));
    public static final SimpleCommandExceptionType ERROR_MIXED_TYPE = new SimpleCommandExceptionType(IChatBaseComponent.translatable("argument.pos.mixed"));
    private final boolean centerCorrect;

    public ArgumentVec3(boolean flag) {
        this.centerCorrect = flag;
    }

    public static ArgumentVec3 vec3() {
        return new ArgumentVec3(true);
    }

    public static ArgumentVec3 vec3(boolean flag) {
        return new ArgumentVec3(flag);
    }

    public static Vec3D getVec3(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return ((IVectorPosition) commandcontext.getArgument(s, IVectorPosition.class)).getPosition((CommandListenerWrapper) commandcontext.getSource());
    }

    public static IVectorPosition getCoordinates(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return (IVectorPosition) commandcontext.getArgument(s, IVectorPosition.class);
    }

    public IVectorPosition parse(StringReader stringreader) throws CommandSyntaxException {
        return (IVectorPosition) (stringreader.canRead() && stringreader.peek() == '^' ? ArgumentVectorPosition.parse(stringreader) : VectorPosition.parseDouble(stringreader, this.centerCorrect));
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
                object = ((ICompletionProvider) commandcontext.getSource()).getAbsoluteCoordinates();
            }

            return ICompletionProvider.suggestCoordinates(s, (Collection) object, suggestionsbuilder, CommandDispatcher.createValidator(this::parse));
        }
    }

    public Collection<String> getExamples() {
        return ArgumentVec3.EXAMPLES;
    }
}
