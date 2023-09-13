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
import net.minecraft.world.phys.Vec2F;
import net.minecraft.world.phys.Vec3D;

public class ArgumentVec2 implements ArgumentType<IVectorPosition> {

    private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "~ ~", "0.1 -0.5", "~1 ~-2");
    public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(IChatBaseComponent.translatable("argument.pos2d.incomplete"));
    private final boolean centerCorrect;

    public ArgumentVec2(boolean flag) {
        this.centerCorrect = flag;
    }

    public static ArgumentVec2 vec2() {
        return new ArgumentVec2(true);
    }

    public static ArgumentVec2 vec2(boolean flag) {
        return new ArgumentVec2(flag);
    }

    public static Vec2F getVec2(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        Vec3D vec3d = ((IVectorPosition) commandcontext.getArgument(s, IVectorPosition.class)).getPosition((CommandListenerWrapper) commandcontext.getSource());

        return new Vec2F((float) vec3d.x, (float) vec3d.z);
    }

    public IVectorPosition parse(StringReader stringreader) throws CommandSyntaxException {
        int i = stringreader.getCursor();

        if (!stringreader.canRead()) {
            throw ArgumentVec2.ERROR_NOT_COMPLETE.createWithContext(stringreader);
        } else {
            ArgumentParserPosition argumentparserposition = ArgumentParserPosition.parseDouble(stringreader, this.centerCorrect);

            if (stringreader.canRead() && stringreader.peek() == ' ') {
                stringreader.skip();
                ArgumentParserPosition argumentparserposition1 = ArgumentParserPosition.parseDouble(stringreader, this.centerCorrect);

                return new VectorPosition(argumentparserposition, new ArgumentParserPosition(true, 0.0D), argumentparserposition1);
            } else {
                stringreader.setCursor(i);
                throw ArgumentVec2.ERROR_NOT_COMPLETE.createWithContext(stringreader);
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
                object = ((ICompletionProvider) commandcontext.getSource()).getAbsoluteCoordinates();
            }

            return ICompletionProvider.suggest2DCoordinates(s, (Collection) object, suggestionsbuilder, CommandDispatcher.createValidator(this::parse));
        }
    }

    public Collection<String> getExamples() {
        return ArgumentVec2.EXAMPLES;
    }
}
