package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.MinecraftKey;

public class PacketPlayInCustomPayload implements Packet<PacketListenerPlayIn> {

    public static final MinecraftKey a = new MinecraftKey("brand");
    public MinecraftKey tag;
    public PacketDataSerializer data;

    public PacketPlayInCustomPayload() {}

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.tag = packetdataserializer.p();
        int i = packetdataserializer.readableBytes();

        if (i >= 0 && i <= 32767) {
            this.data = new PacketDataSerializer(packetdataserializer.readBytes(i));
        } else {
            throw new IOException("Payload may not be larger than 32767 bytes");
        }
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.tag);
        packetdataserializer.writeBytes((ByteBuf) this.data);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
        if (this.data != null) {
            this.data.release();
        }

    }
}
