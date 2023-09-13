package net.minecraft.network.protocol.game;

import java.util.Collection;
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
        this.items = NonNullList.a(nonnulllist.size(), ItemStack.EMPTY);

        for (int k = 0; k < nonnulllist.size(); ++k) {
            this.items.set(k, ((ItemStack) nonnulllist.get(k)).cloneItemStack());
        }

        this.carriedItem = itemstack.cloneItemStack();
    }

    public PacketPlayOutWindowItems(PacketDataSerializer packetdataserializer) {
        this.containerId = packetdataserializer.readUnsignedByte();
        this.stateId = packetdataserializer.j();
        this.items = (List) packetdataserializer.a(NonNullList::a, PacketDataSerializer::o);
        this.carriedItem = packetdataserializer.o();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeByte(this.containerId);
        packetdataserializer.d(this.stateId);
        packetdataserializer.a((Collection) this.items, PacketDataSerializer::a);
        packetdataserializer.a(this.carriedItem);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public int b() {
        return this.containerId;
    }

    public List<ItemStack> c() {
        return this.items;
    }

    public ItemStack d() {
        return this.carriedItem;
    }

    public int e() {
        return this.stateId;
    }
}
