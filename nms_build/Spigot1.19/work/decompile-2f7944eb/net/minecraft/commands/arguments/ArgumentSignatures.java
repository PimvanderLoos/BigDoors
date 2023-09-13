package net.minecraft.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.util.MinecraftEncryption;

public record ArgumentSignatures(long salt, Map<String, byte[]> signatures) {

    private static final int MAX_ARGUMENT_COUNT = 8;
    private static final int MAX_ARGUMENT_NAME_LENGTH = 16;

    public ArgumentSignatures(PacketDataSerializer packetdataserializer) {
        this(packetdataserializer.readLong(), packetdataserializer.readMap(PacketDataSerializer.limitValue(HashMap::new, 8), (packetdataserializer1) -> {
            return packetdataserializer1.readUtf(16);
        }, PacketDataSerializer::readByteArray));
    }

    public static ArgumentSignatures empty() {
        return new ArgumentSignatures(0L, Map.of());
    }

    @Nullable
    public MinecraftEncryption.b get(String s) {
        byte[] abyte = (byte[]) this.signatures.get(s);

        return abyte != null ? new MinecraftEncryption.b(this.salt, abyte) : null;
    }

    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeLong(this.salt);
        packetdataserializer.writeMap(this.signatures, (packetdataserializer1, s) -> {
            packetdataserializer1.writeUtf(s, 16);
        }, PacketDataSerializer::writeByteArray);
    }

    public static Map<String, IChatBaseComponent> collectLastChildPlainSignableComponents(CommandContextBuilder<?> commandcontextbuilder) {
        CommandContextBuilder<?> commandcontextbuilder1 = commandcontextbuilder.getLastChild();
        Map<String, IChatBaseComponent> map = new Object2ObjectArrayMap();
        Iterator iterator = commandcontextbuilder1.getNodes().iterator();

        while (iterator.hasNext()) {
            ParsedCommandNode<?> parsedcommandnode = (ParsedCommandNode) iterator.next();
            CommandNode commandnode = parsedcommandnode.getNode();

            if (commandnode instanceof ArgumentCommandNode) {
                ArgumentCommandNode<?, ?> argumentcommandnode = (ArgumentCommandNode) commandnode;
                ArgumentType argumenttype = argumentcommandnode.getType();

                if (argumenttype instanceof SignedArgument) {
                    SignedArgument<?> signedargument = (SignedArgument) argumenttype;
                    ParsedArgument<?, ?> parsedargument = (ParsedArgument) commandcontextbuilder1.getArguments().get(argumentcommandnode.getName());

                    if (parsedargument != null) {
                        map.put(argumentcommandnode.getName(), getPlainComponentUnchecked(signedargument, parsedargument));
                    }
                }
            }
        }

        return map;
    }

    private static <T> IChatBaseComponent getPlainComponentUnchecked(SignedArgument<T> signedargument, ParsedArgument<?, ?> parsedargument) {
        return signedargument.getPlainSignableComponent(parsedargument.getResult());
    }
}
