package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.coordinates.ArgumentParserPosition;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.util.MathHelper;

public class ArgumentAngle implements ArgumentType<ArgumentAngle.a> {

    private static final Collection<String> EXAMPLES = Arrays.asList("0", "~", "~-5");
    public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(new ChatMessage("argument.angle.incomplete"));
    public static final SimpleCommandExceptionType ERROR_INVALID_ANGLE = new SimpleCommandExceptionType(new ChatMessage("argument.angle.invalid"));

    public ArgumentAngle() {}

    public static ArgumentAngle a() {
        return new ArgumentAngle();
    }

    public static float a(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return ((ArgumentAngle.a) commandcontext.getArgument(s, ArgumentAngle.a.class)).a((CommandListenerWrapper) commandcontext.getSource());
    }

    public ArgumentAngle.a parse(StringReader stringreader) throws CommandSyntaxException {
        if (!stringreader.canRead()) {
            throw ArgumentAngle.ERROR_NOT_COMPLETE.createWithContext(stringreader);
        } else {
            boolean flag = ArgumentParserPosition.b(stringreader);
            float f = stringreader.canRead() && stringreader.peek() != ' ' ? stringreader.readFloat() : 0.0F;

            if (!Float.isNaN(f) && !Float.isInfinite(f)) {
                return new ArgumentAngle.a(f, flag);
            } else {
                throw ArgumentAngle.ERROR_INVALID_ANGLE.createWithContext(stringreader);
            }
        }
    }

    public Collection<String> getExamples() {
        return ArgumentAngle.EXAMPLES;
    }

    public static final class a {

        private final float angle;
        private final boolean isRelative;

        a(float f, boolean flag) {
            this.angle = f;
            this.isRelative = flag;
        }

        public float a(CommandListenerWrapper commandlistenerwrapper) {
            return MathHelper.g(this.isRelative ? this.angle + commandlistenerwrapper.i().y : this.angle);
        }
    }
}
