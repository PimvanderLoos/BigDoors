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
        this.pos = blockposition.immutable();
        this.data = j;
        this.globalEvent = flag;
    }

    public PacketPlayOutWorldEvent(PacketDataSerializer packetdataserializer) {
        this.type = packetdataserializer.readInt();
        this.pos = packetdataserializer.readBlockPos();
        this.data = packetdataserializer.readInt();
        this.globalEvent = packetdataserializer.readBoolean();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeInt(this.type);
        packetdataserializer.writeBlockPos(this.pos);
        packetdataserializer.writeInt(this.data);
        packetdataserializer.writeBoolean(this.globalEvent);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleLevelEvent(this);
    }

    public boolean isGlobalEvent() {
        return this.globalEvent;
    }

    public int getType() {
        return this.type;
    }

    public int getData() {
        return this.data;
    }

    public BlockPosition getPos() {
        return this.pos;
    }
}
