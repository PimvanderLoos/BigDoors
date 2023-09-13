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
        this.entityIds = packetdataserializer.readIntIdList();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeIntIdList(this.entityIds);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleRemoveEntities(this);
    }

    public IntList getEntityIds() {
        return this.entityIds;
    }
}
