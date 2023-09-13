package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.ChatMessage;

public class ArgumentUUID implements ArgumentType<UUID> {

    public static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("argument.uuid.invalid"));
    private static final Collection<String> b = Arrays.asList("dd12be42-52a9-4a91-a8a1-11c01849e498");
    private static final Pattern c = Pattern.compile("^([-A-Fa-f0-9]+)");

    public ArgumentUUID() {}

    public static UUID a(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return (UUID) commandcontext.getArgument(s, UUID.class);
    }

    public static ArgumentUUID a() {
        return new ArgumentUUID();
    }

    public UUID parse(StringReader stringreader) throws CommandSyntaxException {
        String s = stringreader.getRemaining();
        Matcher matcher = ArgumentUUID.c.matcher(s);

        if (matcher.find()) {
            String s1 = matcher.group(1);

            try {
                UUID uuid = UUID.fromString(s1);

                stringreader.setCursor(stringreader.getCursor() + s1.length());
                return uuid;
            } catch (IllegalArgumentException illegalargumentexception) {
                ;
            }
        }

        throw ArgumentUUID.a.create();
    }

    public Collection<String> getExamples() {
        return ArgumentUUID.b;
    }
}
