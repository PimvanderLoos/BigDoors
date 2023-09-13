package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.IChatBaseComponent;

public class ArgumentRotation implements ArgumentType<IVectorPosition> {

    private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "~ ~", "~-5 ~5");
    public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(IChatBaseComponent.translatable("argument.rotation.incomplete"));

    public ArgumentRotation() {}

    public static ArgumentRotation rotation() {
        return new ArgumentRotation();
    }

    public static IVectorPosition getRotation(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return (IVectorPosition) commandcontext.getArgument(s, IVectorPosition.class);
    }

    public IVectorPosition parse(StringReader stringreader) throws CommandSyntaxException {
        int i = stringreader.getCursor();

        if (!stringreader.canRead()) {
            throw ArgumentRotation.ERROR_NOT_COMPLETE.createWithContext(stringreader);
        } else {
            ArgumentParserPosition argumentparserposition = ArgumentParserPosition.parseDouble(stringreader, false);

            if (stringreader.canRead() && stringreader.peek() == ' ') {
                stringreader.skip();
                ArgumentParserPosition argumentparserposition1 = ArgumentParserPosition.parseDouble(stringreader, false);

                return new VectorPosition(argumentparserposition1, argumentparserposition, new ArgumentParserPosition(true, 0.0D));
            } else {
                stringreader.setCursor(i);
                throw ArgumentRotation.ERROR_NOT_COMPLETE.createWithContext(stringreader);
            }
        }
    }

    public Collection<String> getExamples() {
        return ArgumentRotation.EXAMPLES;
    }
}
