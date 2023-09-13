package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutEntityEquipment implements Packet<PacketListenerPlayOut> {

    private int a;
    private EnumItemSlot b;
    private ItemStack c;

    public PacketPlayOutEntityEquipment() {
        this.c = ItemStack.a;
    }

    public PacketPlayOutEntityEquipment(int i, EnumItemSlot enumitemslot, ItemStack itemstack) {
        this.c = ItemStack.a;
        this.a = i;
        this.b = enumitemslot;
        this.c = itemstack.cloneItemStack();
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.g();
        this.b = (EnumItemSlot) packetdataserializer.a(EnumItemSlot.class);
        this.c = packetdataserializer.k();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.d(this.a);
        packetdataserializer.a((Enum) this.b);
        packetdataserializer.a(this.c);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
