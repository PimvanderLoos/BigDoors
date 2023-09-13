package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.item.ItemStack;

public class PacketPlayOutSetSlot implements Packet<PacketListenerPlayOut> {

    public static final int CARRIED_ITEM = -1;
    public static final int PLAYER_INVENTORY = -2;
    private final int containerId;
    private final int stateId;
    private final int slot;
    private final ItemStack itemStack;

    public PacketPlayOutSetSlot(int i, int j, int k, ItemStack itemstack) {
        this.containerId = i;
        this.stateId = j;
        this.slot = k;
        this.itemStack = itemstack.copy();
    }

    public PacketPlayOutSetSlot(PacketDataSerializer packetdataserializer) {
        this.containerId = packetdataserializer.readByte();
        this.stateId = packetdataserializer.readVarInt();
        this.slot = packetdataserializer.readShort();
        this.itemStack = packetdataserializer.readItem();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeByte(this.containerId);
        packetdataserializer.writeVarInt(this.stateId);
        packetdataserializer.writeShort(this.slot);
        packetdataserializer.writeItem(this.itemStack);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleContainerSetSlot(this);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public int getSlot() {
        return this.slot;
    }

    public ItemStack getItem() {
        return this.itemStack;
    }

    public int getStateId() {
        return this.stateId;
    }
}
