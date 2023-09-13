package net.minecraft.commands.arguments;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.SignableCommand;

public record ArgumentSignatures(List<ArgumentSignatures.a> entries) {

    public static final ArgumentSignatures EMPTY = new ArgumentSignatures(List.of());
    private static final int MAX_ARGUMENT_COUNT = 8;
    private static final int MAX_ARGUMENT_NAME_LENGTH = 16;

    public ArgumentSignatures(PacketDataSerializer packetdataserializer) {
        this((List) packetdataserializer.readCollection(PacketDataSerializer.limitValue(ArrayList::new, 8), ArgumentSignatures.a::new));
    }

    @Nullable
    public MessageSignature get(String s) {
        Iterator iterator = this.entries.iterator();

        ArgumentSignatures.a argumentsignatures_a;

        do {
            if (!iterator.hasNext()) {
                return null;
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

    public static ArgumentSignatures signCommand(SignableCommand<?> signablecommand, ArgumentSignatures.b argumentsignatures_b) {
        List<ArgumentSignatures.a> list = signablecommand.arguments().stream().map((signablecommand_a) -> {
            MessageSignature messagesignature = argumentsignatures_b.sign(signablecommand_a.value());

            return messagesignature != null ? new ArgumentSignatures.a(signablecommand_a.name(), messagesignature) : null;
        }).filter(Objects::nonNull).toList();

        return new ArgumentSignatures(list);
    }

    public static record a(String name, MessageSignature signature) {

        public a(PacketDataSerializer packetdataserializer) {
            this(packetdataserializer.readUtf(16), MessageSignature.read(packetdataserializer));
        }

        public void write(PacketDataSerializer packetdataserializer) {
            packetdataserializer.writeUtf(this.name, 16);
            MessageSignature.write(packetdataserializer, this.signature);
        }
    }

    @FunctionalInterface
    public interface b {

        @Nullable
        MessageSignature sign(String s);
    }
}
