package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.item.ItemStack;

public class PacketPlayInSetCreativeSlot implements Packet<PacketListenerPlayIn> {

    private int slot;
    private ItemStack b;

    public PacketPlayInSetCreativeSlot() {
        this.b = ItemStack.b;
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.slot = packetdataserializer.readShort();
        this.b = packetdataserializer.n();
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeShort(this.slot);
        packetdataserializer.a(this.b);
    }

    public int b() {
        return this.slot;
    }

    public ItemStack getItemStack() {
        return this.b;
    }
}
