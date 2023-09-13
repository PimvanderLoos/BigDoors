package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutBlockBreakAnimation implements Packet<PacketListenerPlayOut> {

    private final int id;
    private final BlockPosition pos;
    private final int progress;

    public PacketPlayOutBlockBreakAnimation(int i, BlockPosition blockposition, int j) {
        this.id = i;
        this.pos = blockposition;
        this.progress = j;
    }

    public PacketPlayOutBlockBreakAnimation(PacketDataSerializer packetdataserializer) {
        this.id = packetdataserializer.j();
        this.pos = packetdataserializer.f();
        this.progress = packetdataserializer.readUnsignedByte();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.id);
        packetdataserializer.a(this.pos);
        packetdataserializer.writeByte(this.progress);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public int b() {
        return this.id;
    }

    public BlockPosition c() {
        return this.pos;
    }

    public int d() {
        return this.progress;
    }
}
