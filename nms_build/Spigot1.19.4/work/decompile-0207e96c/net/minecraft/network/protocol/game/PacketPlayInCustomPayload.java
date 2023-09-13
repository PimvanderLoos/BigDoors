package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.MinecraftKey;

public class PacketPlayInCustomPayload implements Packet<PacketListenerPlayIn> {

    private static final int MAX_PAYLOAD_SIZE = 32767;
    public static final MinecraftKey BRAND = new MinecraftKey("brand");
    public final MinecraftKey identifier;
    public final PacketDataSerializer data;

    public PacketPlayInCustomPayload(MinecraftKey minecraftkey, PacketDataSerializer packetdataserializer) {
        this.identifier = minecraftkey;
        this.data = packetdataserializer;
    }

    public PacketPlayInCustomPayload(PacketDataSerializer packetdataserializer) {
        this.identifier = packetdataserializer.readResourceLocation();
        int i = packetdataserializer.readableBytes();

        if (i >= 0 && i <= 32767) {
            this.data = new PacketDataSerializer(packetdataserializer.readBytes(i));
        } else {
            throw new IllegalArgumentException("Payload may not be larger than 32767 bytes");
        }
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeResourceLocation(this.identifier);
        packetdataserializer.writeBytes((ByteBuf) this.data);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleCustomPayload(this);
        this.data.release();
    }

    public MinecraftKey getIdentifier() {
        return this.identifier;
    }

    public PacketDataSerializer getData() {
        return this.data;
    }
}
