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

    private static final Collection<String> b = Arrays.asList("0", "~", "~-5");
    public static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("argument.angle.incomplete"));

    public ArgumentAngle() {}

    public static ArgumentAngle a() {
        return new ArgumentAngle();
    }

    public static float a(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return ((ArgumentAngle.a) commandcontext.getArgument(s, ArgumentAngle.a.class)).a((CommandListenerWrapper) commandcontext.getSource());
    }

    public ArgumentAngle.a parse(StringReader stringreader) throws CommandSyntaxException {
        if (!stringreader.canRead()) {
            throw ArgumentAngle.a.createWithContext(stringreader);
        } else {
            boolean flag = ArgumentParserPosition.b(stringreader);
            float f = stringreader.canRead() && stringreader.peek() != ' ' ? stringreader.readFloat() : 0.0F;

            return new ArgumentAngle.a(f, flag);
        }
    }

    public Collection<String> getExamples() {
        return ArgumentAngle.b;
    }

    public static final class a {

        private final float a;
        private final boolean b;

        private a(float f, boolean flag) {
            this.a = f;
            this.b = flag;
        }

        public float a(CommandListenerWrapper commandlistenerwrapper) {
            return MathHelper.g(this.b ? this.a + commandlistenerwrapper.i().j : this.a);
        }
    }
}
