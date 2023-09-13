package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutUpdateTime implements Packet<PacketListenerPlayOut> {

    private final long gameTime;
    private final long dayTime;

    public PacketPlayOutUpdateTime(long i, long j, boolean flag) {
        this.gameTime = i;
        long k = j;

        if (!flag) {
            k = -j;
            if (k == 0L) {
                k = -1L;
            }
        }

        this.dayTime = k;
    }

    public PacketPlayOutUpdateTime(PacketDataSerializer packetdataserializer) {
        this.gameTime = packetdataserializer.readLong();
        this.dayTime = packetdataserializer.readLong();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeLong(this.gameTime);
        packetdataserializer.writeLong(this.dayTime);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public long b() {
        return this.gameTime;
    }

    public long c() {
        return this.dayTime;
    }
}
