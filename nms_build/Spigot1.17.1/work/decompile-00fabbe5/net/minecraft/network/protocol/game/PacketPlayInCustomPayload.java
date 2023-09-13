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
        this.identifier = packetdataserializer.q();
        int i = packetdataserializer.readableBytes();

        if (i >= 0 && i <= 32767) {
            this.data = new PacketDataSerializer(packetdataserializer.readBytes(i));
        } else {
            throw new IllegalArgumentException("Payload may not be larger than 32767 bytes");
        }
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.identifier);
        packetdataserializer.writeBytes((ByteBuf) this.data);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
        this.data.release();
    }

    public MinecraftKey b() {
        return this.identifier;
    }

    public PacketDataSerializer c() {
        return this.data;
    }
}
