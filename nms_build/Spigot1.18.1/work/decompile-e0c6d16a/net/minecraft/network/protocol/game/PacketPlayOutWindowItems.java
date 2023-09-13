package net.minecraft.network.protocol.game;

import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.item.ItemStack;

public class PacketPlayOutWindowItems implements Packet<PacketListenerPlayOut> {

    private final int containerId;
    private final int stateId;
    private final List<ItemStack> items;
    private final ItemStack carriedItem;

    public PacketPlayOutWindowItems(int i, int j, NonNullList<ItemStack> nonnulllist, ItemStack itemstack) {
        this.containerId = i;
        this.stateId = j;
        this.items = NonNullList.withSize(nonnulllist.size(), ItemStack.EMPTY);

        for (int k = 0; k < nonnulllist.size(); ++k) {
            this.items.set(k, ((ItemStack) nonnulllist.get(k)).copy());
        }

        this.carriedItem = itemstack.copy();
    }

    public PacketPlayOutWindowItems(PacketDataSerializer packetdataserializer) {
        this.containerId = packetdataserializer.readUnsignedByte();
        this.stateId = packetdataserializer.readVarInt();
        this.items = (List) packetdataserializer.readCollection(NonNullList::createWithCapacity, PacketDataSerializer::readItem);
        this.carriedItem = packetdataserializer.readItem();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeByte(this.containerId);
        packetdataserializer.writeVarInt(this.stateId);
        packetdataserializer.writeCollection(this.items, PacketDataSerializer::writeItem);
        packetdataserializer.writeItem(this.carriedItem);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleContainerContent(this);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public List<ItemStack> getItems() {
        return this.items;
    }

    public ItemStack getCarriedItem() {
        return this.carriedItem;
    }

    public int getStateId() {
        return this.stateId;
    }
}
