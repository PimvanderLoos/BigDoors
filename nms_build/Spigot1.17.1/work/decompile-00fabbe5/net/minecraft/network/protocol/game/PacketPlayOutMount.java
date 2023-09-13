package net.minecraft.network.protocol.game;

import java.util.List;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;

public class PacketPlayOutMount implements Packet<PacketListenerPlayOut> {

    private final int vehicle;
    private final int[] passengers;

    public PacketPlayOutMount(Entity entity) {
        this.vehicle = entity.getId();
        List<Entity> list = entity.getPassengers();

        this.passengers = new int[list.size()];

        for (int i = 0; i < list.size(); ++i) {
            this.passengers[i] = ((Entity) list.get(i)).getId();
        }

    }

    public PacketPlayOutMount(PacketDataSerializer packetdataserializer) {
        this.vehicle = packetdataserializer.j();
        this.passengers = packetdataserializer.c();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.vehicle);
        packetdataserializer.a(this.passengers);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public int[] b() {
        return this.passengers;
    }

    public int c() {
        return this.vehicle;
    }
}
