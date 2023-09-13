package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutOpenSignEditor implements Packet<PacketListenerPlayOut> {

    private final BlockPosition pos;

    public PacketPlayOutOpenSignEditor(BlockPosition blockposition) {
        this.pos = blockposition;
    }

    public PacketPlayOutOpenSignEditor(PacketDataSerializer packetdataserializer) {
        this.pos = packetdataserializer.readBlockPos();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeBlockPos(this.pos);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleOpenSignEditor(this);
    }

    public BlockPosition getPos() {
        return this.pos;
    }
}
