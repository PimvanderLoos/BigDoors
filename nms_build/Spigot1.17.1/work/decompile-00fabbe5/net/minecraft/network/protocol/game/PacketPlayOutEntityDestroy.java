package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutEntityDestroy implements Packet<PacketListenerPlayOut> {

    private final IntList entityIds;

    public PacketPlayOutEntityDestroy(IntList intlist) {
        this.entityIds = new IntArrayList(intlist);
    }

    public PacketPlayOutEntityDestroy(int... aint) {
        this.entityIds = new IntArrayList(aint);
    }

    public PacketPlayOutEntityDestroy(PacketDataSerializer packetdataserializer) {
        this.entityIds = packetdataserializer.a();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.entityIds);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public IntList b() {
        return this.entityIds;
    }
}
