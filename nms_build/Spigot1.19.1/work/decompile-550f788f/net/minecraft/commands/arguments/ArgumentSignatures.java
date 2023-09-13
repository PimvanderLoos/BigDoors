package net.minecraft.commands.arguments;

import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.PreviewableCommand;

public record ArgumentSignatures(List<ArgumentSignatures.a> entries) {

    public static final ArgumentSignatures EMPTY = new ArgumentSignatures(List.of());
    private static final int MAX_ARGUMENT_COUNT = 8;
    private static final int MAX_ARGUMENT_NAME_LENGTH = 16;

    public ArgumentSignatures(PacketDataSerializer packetdataserializer) {
        this((List) packetdataserializer.readCollection(PacketDataSerializer.limitValue(ArrayList::new, 8), ArgumentSignatures.a::new));
    }

    public MessageSignature get(String s) {
        Iterator iterator = this.entries.iterator();

        ArgumentSignatures.a argumentsignatures_a;

        do {
            if (!iterator.hasNext()) {
                return MessageSignature.EMPTY;
            }

            argumentsignatures_a = (ArgumentSignatures.a) iterator.next();
        } while (!argumentsignatures_a.name.equals(s));

        return argumentsignatures_a.signature;
    }

    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeCollection(this.entries, (packetdataserializer1, argumentsignatures_a) -> {
            argumentsignatures_a.write(packetdataserializer1);
        });
    }

    public static boolean hasSignableArguments(PreviewableCommand<?> previewablecommand) {
        return previewablecommand.arguments().stream().anyMatch((previewablecommand_a) -> {
            return previewablecommand_a.previewType() instanceof SignedArgument;
        });
    }

    public static ArgumentSignatures signCommand(PreviewableCommand<?> previewablecommand, ArgumentSignatures.b argumentsignatures_b) {
        List<ArgumentSignatures.a> list = collectPlainSignableArguments(previewablecommand).stream().map((pair) -> {
            MessageSignature messagesignature = argumentsignatures_b.sign((String) pair.getFirst(), (String) pair.getSecond());

            return new ArgumentSignatures.a((String) pair.getFirst(), messagesignature);
        }).toList();

        return new ArgumentSignatures(list);
    }

    public static List<Pair<String, String>> collectPlainSignableArguments(PreviewableCommand<?> previewablecommand) {
        List<Pair<String, String>> list = new ArrayList();
        Iterator iterator = previewablecommand.arguments().iterator();

        while (iterator.hasNext()) {
            PreviewableCommand.a<?> previewablecommand_a = (PreviewableCommand.a) iterator.next();
            PreviewedArgument previewedargument = previewablecommand_a.previewType();

            if (previewedargument instanceof SignedArgument) {
                SignedArgument<?> signedargument = (SignedArgument) previewedargument;
                String s = getSignableText(signedargument, previewablecommand_a.parsedValue());

                list.add(Pair.of(previewablecommand_a.name(), s));
            }
        }

        return list;
    }

    private static <T> String getSignableText(SignedArgument<T> signedargument, ParsedArgument<?, ?> parsedargument) {
        return signedargument.getSignableText(parsedargument.getResult());
    }

    public static record a(String name, MessageSignature signature) {

        public a(PacketDataSerializer packetdataserializer) {
            this(packetdataserializer.readUtf(16), new MessageSignature(packetdataserializer));
        }

        public void write(PacketDataSerializer packetdataserializer) {
            packetdataserializer.writeUtf(this.name, 16);
            this.signature.write(packetdataserializer);
        }
    }

    @FunctionalInterface
    public interface b {

        MessageSignature sign(String s, String s1);
    }
}
