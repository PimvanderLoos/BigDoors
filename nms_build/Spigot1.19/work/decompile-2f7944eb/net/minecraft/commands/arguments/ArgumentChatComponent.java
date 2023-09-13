package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;

public class ArgumentChatComponent implements ArgumentType<IChatBaseComponent> {

    private static final Collection<String> EXAMPLES = Arrays.asList("\"hello world\"", "\"\"", "\"{\"text\":\"hello world\"}", "[\"\"]");
    public static final DynamicCommandExceptionType ERROR_INVALID_JSON = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("argument.component.invalid", object);
    });

    private ArgumentChatComponent() {}

    public static IChatBaseComponent getComponent(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return (IChatBaseComponent) commandcontext.getArgument(s, IChatBaseComponent.class);
    }

    public static ArgumentChatComponent textComponent() {
        return new ArgumentChatComponent();
    }

    public IChatBaseComponent parse(StringReader stringreader) throws CommandSyntaxException {
        try {
            IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.ChatSerializer.fromJson(stringreader);

            if (ichatmutablecomponent == null) {
                throw ArgumentChatComponent.ERROR_INVALID_JSON.createWithContext(stringreader, "empty");
            } else {
                return ichatmutablecomponent;
            }
        } catch (Exception exception) {
            String s = exception.getCause() != null ? exception.getCause().getMessage() : exception.getMessage();

            throw ArgumentChatComponent.ERROR_INVALID_JSON.createWithContext(stringreader, s);
        }
    }

    public Collection<String> getExamples() {
        return ArgumentChatComponent.EXAMPLES;
    }
}
