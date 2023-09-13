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
        this.id = packetdataserializer.readVarInt();
        this.pos = packetdataserializer.readBlockPos();
        this.progress = packetdataserializer.readUnsignedByte();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeVarInt(this.id);
        packetdataserializer.writeBlockPos(this.pos);
        packetdataserializer.writeByte(this.progress);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleBlockDestruction(this);
    }

    public int getId() {
        return this.id;
    }

    public BlockPosition getPos() {
        return this.pos;
    }

    public int getProgress() {
        return this.progress;
    }
}
