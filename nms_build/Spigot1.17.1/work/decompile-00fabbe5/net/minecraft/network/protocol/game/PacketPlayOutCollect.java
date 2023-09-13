package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutCollect implements Packet<PacketListenerPlayOut> {

    private final int itemId;
    private final int playerId;
    private final int amount;

    public PacketPlayOutCollect(int i, int j, int k) {
        this.itemId = i;
        this.playerId = j;
        this.amount = k;
    }

    public PacketPlayOutCollect(PacketDataSerializer packetdataserializer) {
        this.itemId = packetdataserializer.j();
        this.playerId = packetdataserializer.j();
        this.amount = packetdataserializer.j();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.itemId);
        packetdataserializer.d(this.playerId);
        packetdataserializer.d(this.amount);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public int b() {
        return this.itemId;
    }

    public int c() {
        return this.playerId;
    }

    public int d() {
        return this.amount;
    }
}
