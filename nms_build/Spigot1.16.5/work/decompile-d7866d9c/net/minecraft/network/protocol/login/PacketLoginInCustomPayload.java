package net.minecraft.network.protocol.login;

import java.io.IOException;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketLoginInCustomPayload implements Packet<PacketLoginInListener> {

    private int a;
    private PacketDataSerializer b;

    public PacketLoginInCustomPayload() {}

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.i();
        if (packetdataserializer.readBoolean()) {
            int i = packetdataserializer.readableBytes();

            if (i < 0 || i > 1048576) {
                throw new IOException("Payload may not be larger than 1048576 bytes");
            }

            this.b = new PacketDataSerializer(packetdataserializer.readBytes(i));
        } else {
            this.b = null;
        }

    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.d(this.a);
        if (this.b != null) {
            packetdataserializer.writeBoolean(true);
            packetdataserializer.writeBytes(this.b.copy());
        } else {
            packetdataserializer.writeBoolean(false);
        }

    }

    public void a(PacketLoginInListener packetlogininlistener) {
        packetlogininlistener.a(this);
    }
}
