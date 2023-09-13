package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutSpawnPosition implements Packet<PacketListenerPlayOut> {

    public BlockPosition position;
    private float b;

    public PacketPlayOutSpawnPosition() {}

    public PacketPlayOutSpawnPosition(BlockPosition blockposition, float f) {
        this.position = blockposition;
        this.b = f;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.position = packetdataserializer.e();
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.position);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
