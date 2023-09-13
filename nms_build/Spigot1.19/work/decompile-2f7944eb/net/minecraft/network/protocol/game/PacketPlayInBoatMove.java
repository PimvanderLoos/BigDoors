package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayInBoatMove implements Packet<PacketListenerPlayIn> {

    private final boolean left;
    private final boolean right;

    public PacketPlayInBoatMove(boolean flag, boolean flag1) {
        this.left = flag;
        this.right = flag1;
    }

    public PacketPlayInBoatMove(PacketDataSerializer packetdataserializer) {
        this.left = packetdataserializer.readBoolean();
        this.right = packetdataserializer.readBoolean();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeBoolean(this.left);
        packetdataserializer.writeBoolean(this.right);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handlePaddleBoat(this);
    }

    public boolean getLeft() {
        return this.left;
    }

    public boolean getRight() {
        return this.right;
    }
}
