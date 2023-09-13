package net.minecraft.server;

import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

public class ArgumentChatComponent implements ArgumentType<IChatBaseComponent> {

    private static final Collection<String> b = Arrays.asList(new String[] { "\"hello world\"", "\"\"", "\"{\"text\":\"hello world\"}", "[\"\"]"});
    public static final DynamicCommandExceptionType a = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("argument.component.invalid", new Object[] { object});
    });

    private ArgumentChatComponent() {}

    public static IChatBaseComponent a(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return (IChatBaseComponent) commandcontext.getArgument(s, IChatBaseComponent.class);
    }

    public static ArgumentChatComponent a() {
        return new ArgumentChatComponent();
    }

    public IChatBaseComponent a(StringReader stringreader) throws CommandSyntaxException {
        try {
            IChatBaseComponent ichatbasecomponent = IChatBaseComponent.ChatSerializer.a(stringreader);

            if (ichatbasecomponent == null) {
                throw ArgumentChatComponent.a.createWithContext(stringreader, "empty");
            } else {
                return ichatbasecomponent;
            }
        } catch (JsonParseException jsonparseexception) {
            String s = jsonparseexception.getCause() != null ? jsonparseexception.getCause().getMessage() : jsonparseexception.getMessage();

            throw ArgumentChatComponent.a.createWithContext(stringreader, s);
        }
    }

    public Collection<String> getExamples() {
        return ArgumentChatComponent.b;
    }

    public Object parse(StringReader stringreader) throws CommandSyntaxException {
        return this.a(stringreader);
    }
}
