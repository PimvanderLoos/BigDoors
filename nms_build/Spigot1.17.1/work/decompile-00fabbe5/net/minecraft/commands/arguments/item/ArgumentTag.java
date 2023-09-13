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
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.tags.Tag;

public class ArgumentTag implements ArgumentType<ArgumentTag.a> {

    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "#foo");
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("arguments.function.tag.unknown", new Object[]{object});
    });
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_FUNCTION = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("arguments.function.unknown", new Object[]{object});
    });

    public ArgumentTag() {}

    public static ArgumentTag a() {
        return new ArgumentTag();
    }

    public ArgumentTag.a parse(StringReader stringreader) throws CommandSyntaxException {
        final MinecraftKey minecraftkey;

        if (stringreader.canRead() && stringreader.peek() == '#') {
            stringreader.skip();
            minecraftkey = MinecraftKey.a(stringreader);
            return new ArgumentTag.a() {
                @Override
                public Collection<CustomFunction> a(CommandContext<CommandListenerWrapper> commandcontext) throws CommandSyntaxException {
                    Tag<CustomFunction> tag = ArgumentTag.b(commandcontext, minecraftkey);

                    return tag.getTagged();
                }

                @Override
                public Pair<MinecraftKey, Either<CustomFunction, Tag<CustomFunction>>> b(CommandContext<CommandListenerWrapper> commandcontext) throws CommandSyntaxException {
                    return Pair.of(minecraftkey, Either.right(ArgumentTag.b(commandcontext, minecraftkey)));
                }
            };
        } else {
            minecraftkey = MinecraftKey.a(stringreader);
            return new ArgumentTag.a() {
                @Override
                public Collection<CustomFunction> a(CommandContext<CommandListenerWrapper> commandcontext) throws CommandSyntaxException {
                    return Collections.singleton(ArgumentTag.a(commandcontext, minecraftkey));
                }

                @Override
                public Pair<MinecraftKey, Either<CustomFunction, Tag<CustomFunction>>> b(CommandContext<CommandListenerWrapper> commandcontext) throws CommandSyntaxException {
                    return Pair.of(minecraftkey, Either.left(ArgumentTag.a(commandcontext, minecraftkey)));
                }
            };
        }
    }

    static CustomFunction a(CommandContext<CommandListenerWrapper> commandcontext, MinecraftKey minecraftkey) throws CommandSyntaxException {
        return (CustomFunction) ((CommandListenerWrapper) commandcontext.getSource()).getServer().getFunctionData().a(minecraftkey).orElseThrow(() -> {
            return ArgumentTag.ERROR_UNKNOWN_FUNCTION.create(minecraftkey.toString());
        });
    }

    static Tag<CustomFunction> b(CommandContext<CommandListenerWrapper> commandcontext, MinecraftKey minecraftkey) throws CommandSyntaxException {
        Tag<CustomFunction> tag = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getFunctionData().b(minecraftkey);

        if (tag == null) {
            throw ArgumentTag.ERROR_UNKNOWN_TAG.create(minecraftkey.toString());
        } else {
            return tag;
        }
    }

    public static Collection<CustomFunction> a(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        return ((ArgumentTag.a) commandcontext.getArgument(s, ArgumentTag.a.class)).a(commandcontext);
    }

    public static Pair<MinecraftKey, Either<CustomFunction, Tag<CustomFunction>>> b(CommandContext<CommandListenerWrapper> commandcontext, String s) throws CommandSyntaxException {
        return ((ArgumentTag.a) commandcontext.getArgument(s, ArgumentTag.a.class)).b(commandcontext);
    }

    public Collection<String> getExamples() {
        return ArgumentTag.EXAMPLES;
    }

    public interface a {

        Collection<CustomFunction> a(CommandContext<CommandListenerWrapper> commandcontext) throws CommandSyntaxException;

        Pair<MinecraftKey, Either<CustomFunction, Tag<CustomFunction>>> b(CommandContext<CommandListenerWrapper> commandcontext) throws CommandSyntaxException;
    }
}
