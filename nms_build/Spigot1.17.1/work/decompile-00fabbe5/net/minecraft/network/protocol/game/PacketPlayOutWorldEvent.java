package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutWorldEvent implements Packet<PacketListenerPlayOut> {

    private final int type;
    private final BlockPosition pos;
    private final int data;
    private final boolean globalEvent;

    public PacketPlayOutWorldEvent(int i, BlockPosition blockposition, int j, boolean flag) {
        this.type = i;
        this.pos = blockposition.immutableCopy();
        this.data = j;
        this.globalEvent = flag;
    }

    public PacketPlayOutWorldEvent(PacketDataSerializer packetdataserializer) {
        this.type = packetdataserializer.readInt();
        this.pos = packetdataserializer.f();
        this.data = packetdataserializer.readInt();
        this.globalEvent = packetdataserializer.readBoolean();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeInt(this.type);
        packetdataserializer.a(this.pos);
        packetdataserializer.writeInt(this.data);
        packetdataserializer.writeBoolean(this.globalEvent);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public boolean b() {
        return this.globalEvent;
    }

    public int c() {
        return this.type;
    }

    public int d() {
        return this.data;
    }

    public BlockPosition e() {
        return this.pos;
    }
}
