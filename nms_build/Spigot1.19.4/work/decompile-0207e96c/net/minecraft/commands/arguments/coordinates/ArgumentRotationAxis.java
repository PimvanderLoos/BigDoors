package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.core.EnumDirection;
import net.minecraft.network.chat.IChatBaseComponent;

public class ArgumentRotationAxis implements ArgumentType<EnumSet<EnumDirection.EnumAxis>> {

    private static final Collection<String> EXAMPLES = Arrays.asList("xyz", "x");
    private static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType(IChatBaseComponent.translatable("arguments.swizzle.invalid"));

    public ArgumentRotationAxis() {}

    public static ArgumentRotationAxis swizzle() {
        return new ArgumentRotationAxis();
    }

    public static EnumSet<EnumDirection.EnumAxis> getSwizzle(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return (EnumSet) commandcontext.getArgument(s, EnumSet.class);
    }

    public EnumSet<EnumDirection.EnumAxis> parse(StringReader stringreader) throws CommandSyntaxException {
        EnumSet enumset = EnumSet.noneOf(EnumDirection.EnumAxis.class);

        while (stringreader.canRead() && stringreader.peek() != ' ') {
            char c0 = stringreader.read();
            EnumDirection.EnumAxis enumdirection_enumaxis;

            switch (c0) {
                case 'x':
                    enumdirection_enumaxis = EnumDirection.EnumAxis.X;
                    break;
                case 'y':
                    enumdirection_enumaxis = EnumDirection.EnumAxis.Y;
                    break;
                case 'z':
                    enumdirection_enumaxis = EnumDirection.EnumAxis.Z;
                    break;
                default:
                    throw ArgumentRotationAxis.ERROR_INVALID.create();
            }

            if (enumset.contains(enumdirection_enumaxis)) {
                throw ArgumentRotationAxis.ERROR_INVALID.create();
            }

            enumset.add(enumdirection_enumaxis);
        }

        return enumset;
    }

    public Collection<String> getExamples() {
        return ArgumentRotationAxis.EXAMPLES;
    }
}
