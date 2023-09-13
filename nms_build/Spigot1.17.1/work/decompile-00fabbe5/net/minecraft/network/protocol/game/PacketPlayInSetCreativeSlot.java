package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.item.ItemStack;

public class PacketPlayInSetCreativeSlot implements Packet<PacketListenerPlayIn> {

    private final int slotNum;
    private final ItemStack itemStack;

    public PacketPlayInSetCreativeSlot(int i, ItemStack itemstack) {
        this.slotNum = i;
        this.itemStack = itemstack.cloneItemStack();
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public PacketPlayInSetCreativeSlot(PacketDataSerializer packetdataserializer) {
        this.slotNum = packetdataserializer.readShort();
        this.itemStack = packetdataserializer.o();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeShort(this.slotNum);
        packetdataserializer.a(this.itemStack);
    }

    public int b() {
        return this.slotNum;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }
}
