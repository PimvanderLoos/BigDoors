package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.item.ItemStack;

public class PacketPlayInSetCreativeSlot implements Packet<PacketListenerPlayIn> {

    private final int slotNum;
    private final ItemStack itemStack;

    public PacketPlayInSetCreativeSlot(int i, ItemStack itemstack) {
        this.slotNum = i;
        this.itemStack = itemstack.copy();
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleSetCreativeModeSlot(this);
    }

    public PacketPlayInSetCreativeSlot(PacketDataSerializer packetdataserializer) {
        this.slotNum = packetdataserializer.readShort();
        this.itemStack = packetdataserializer.readItem();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeShort(this.slotNum);
        packetdataserializer.writeItem(this.itemStack);
    }

    public int getSlotNum() {
        return this.slotNum;
    }

    public ItemStack getItem() {
        return this.itemStack;
    }
}
