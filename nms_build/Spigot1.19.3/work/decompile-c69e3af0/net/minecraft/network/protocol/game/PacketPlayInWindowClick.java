package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.function.IntFunction;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.inventory.InventoryClickType;
import net.minecraft.world.item.ItemStack;

public class PacketPlayInWindowClick implements Packet<PacketListenerPlayIn> {

    private static final int MAX_SLOT_COUNT = 128;
    private final int containerId;
    private final int stateId;
    private final int slotNum;
    private final int buttonNum;
    private final InventoryClickType clickType;
    private final ItemStack carriedItem;
    private final Int2ObjectMap<ItemStack> changedSlots;

    public PacketPlayInWindowClick(int i, int j, int k, int l, InventoryClickType inventoryclicktype, ItemStack itemstack, Int2ObjectMap<ItemStack> int2objectmap) {
        this.containerId = i;
        this.stateId = j;
        this.slotNum = k;
        this.buttonNum = l;
        this.clickType = inventoryclicktype;
        this.carriedItem = itemstack;
        this.changedSlots = Int2ObjectMaps.unmodifiable(int2objectmap);
    }

    public PacketPlayInWindowClick(PacketDataSerializer packetdataserializer) {
        this.containerId = packetdataserializer.readByte();
        this.stateId = packetdataserializer.readVarInt();
        this.slotNum = packetdataserializer.readShort();
        this.buttonNum = packetdataserializer.readByte();
        this.clickType = (InventoryClickType) packetdataserializer.readEnum(InventoryClickType.class);
        IntFunction<Int2ObjectOpenHashMap<ItemStack>> intfunction = PacketDataSerializer.limitValue(Int2ObjectOpenHashMap::new, 128);

        this.changedSlots = Int2ObjectMaps.unmodifiable((Int2ObjectMap) packetdataserializer.readMap(intfunction, (packetdataserializer1) -> {
            return Integer.valueOf(packetdataserializer1.readShort());
        }, PacketDataSerializer::readItem));
        this.carriedItem = packetdataserializer.readItem();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeByte(this.containerId);
        packetdataserializer.writeVarInt(this.stateId);
        packetdataserializer.writeShort(this.slotNum);
        packetdataserializer.writeByte(this.buttonNum);
        packetdataserializer.writeEnum(this.clickType);
        packetdataserializer.writeMap(this.changedSlots, PacketDataSerializer::writeShort, PacketDataSerializer::writeItem);
        packetdataserializer.writeItem(this.carriedItem);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleContainerClick(this);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public int getSlotNum() {
        return this.slotNum;
    }

    public int getButtonNum() {
        return this.buttonNum;
    }

    public ItemStack getCarriedItem() {
        return this.carriedItem;
    }

    public Int2ObjectMap<ItemStack> getChangedSlots() {
        return this.changedSlots;
    }

    public InventoryClickType getClickType() {
        return this.clickType;
    }

    public int getStateId() {
        return this.stateId;
    }
}
