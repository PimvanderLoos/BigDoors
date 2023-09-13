package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.CustomFunction;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;

public class ArgumentTag implements ArgumentType<ArgumentTag.a> {

    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "#foo");
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("arguments.function.tag.unknown", object);
    });
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_FUNCTION = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("arguments.function.unknown", object);
    });

    public ArgumentTag() {}

    public static ArgumentTag functions() {
        return new ArgumentTag();
    }

    public ArgumentTag.a parse(StringReader stringreader) throws CommandSyntaxException {
        final MinecraftKey minecraftkey;

        if (stringreader.canRead() && stringreader.peek() == '#') {
            stringreader.skip();
            minecraftkey = MinecraftKey.read(stringreader);
            return new ArgumentTag.a() {
                @Override
                public Collection<CustomFunction> create(CommandContext<CommandListenerWrapper> commandcontext) throws CommandSyntaxException {
                    return ArgumentTag.getFunctionTag(commandcontext, minecraftkey);
                }

                @Override
                public Pair<MinecraftKey, Either<CustomFunction, Collection<CustomFunction>>> unwrap(CommandContext<CommandListenerWrapper> commandcontext) throws CommandSyntaxException {
                    return Pair.of(minecraftkey, Either.right(ArgumentTag.getFunctionTag(commandcontext, minecraftkey)));
                }
            };
        } else {
            minecraftkey = MinecraftKey.read(stringreader);
            return new ArgumentTag.a() {
                @Override
                public Collection<CustomFunction> create(CommandContext<CommandListenerWrapper> commandcontext) throws CommandSyntaxException {
                    return Collections.singleton(ArgumentTag.getFunction(commandcontext, minecraftkey));
                }

                @Override
                public Pair<MinecraftKey, Either<CustomFunction, Collection<CustomFunction>>> unwrap(CommandContext<CommandListenerWrapper> commandcontext) throws CommandSyntaxException {
                    return Pair.of(minecraftkey, Either.left(ArgumentTag.getFunction(commandcontext, minecraftkey)));
                }
            };
        }
    }

    static CustomFunction getFunction(CommandContext<CommandListenerWrapper> commandcontext, MinecraftKey minecraftkey) throws CommandSyntaxException {
        return (CustomFunction) ((CommandListenerWrapper) commandcontext.getSource()).getServer().getFunctions().get(minecraftkey).orElseThrow(() -> {
            return ArgumentTag.ERROR_UNKNOWN_FUNCTION.create(minecraftkey.toString());
        });
    }

    static Collection<CustomFunction> getFunctionTag(CommandContext<CommandListenerWrapper> commandcontext, MinecraftKey minecraftkey) throws CommandSyntaxException {
        Collection<CustomFunction> collection = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getFunctions().getTag(minecraftkey);

        if (collection == null) {
            throw ArgumentTag.ERROR_UNKNOWN_TAG.create(minecraftkey.toString());
        } else {
            return collection;
        }
    }

    public static Collection<CustomFunction> getFunctions(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        return ((ArgumentTag.a) commandcontext.getArgument(s, ArgumentTag.a.class)).create(commandcontext);
    }

    public static Pair<MinecraftKey, Either<CustomFunction, Collection<CustomFunction>>> getFunctionOrTag(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        return ((ArgumentTag.a) commandcontext.getArgument(s, ArgumentTag.a.class)).unwrap(commandcontext);
    }

    public Collection<String> getExamples() {
        return ArgumentTag.EXAMPLES;
    }

    public interface a {

        Collection<CustomFunction> create(CommandContext<CommandListenerWrapper> commandcontext) throws CommandSyntaxException;

        Pair<MinecraftKey, Either<CustomFunction, Collection<CustomFunction>>> unwrap(CommandContext<CommandListenerWrapper> commandcontext) throws CommandSyntaxException;
    }
}
