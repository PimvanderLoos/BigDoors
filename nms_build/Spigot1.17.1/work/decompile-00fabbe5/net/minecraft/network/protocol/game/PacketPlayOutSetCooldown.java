package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.item.Item;

public class PacketPlayOutSetCooldown implements Packet<PacketListenerPlayOut> {

    private final Item item;
    private final int duration;

    public PacketPlayOutSetCooldown(Item item, int i) {
        this.item = item;
        this.duration = i;
    }

    public PacketPlayOutSetCooldown(PacketDataSerializer packetdataserializer) {
        this.item = Item.getById(packetdataserializer.j());
        this.duration = packetdataserializer.j();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(Item.getId(this.item));
        packetdataserializer.d(this.duration);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public Item b() {
        return this.item;
    }

    public int c() {
        return this.duration;
    }
}
