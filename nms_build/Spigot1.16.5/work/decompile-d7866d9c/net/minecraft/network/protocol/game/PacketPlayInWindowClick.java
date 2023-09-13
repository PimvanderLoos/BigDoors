package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.inventory.InventoryClickType;
import net.minecraft.world.item.ItemStack;

public class PacketPlayInWindowClick implements Packet<PacketListenerPlayIn> {

    private int a;
    private int slot;
    private int button;
    private short d;
    private ItemStack item;
    private InventoryClickType shift;

    public PacketPlayInWindowClick() {
        this.item = ItemStack.b;
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.readByte();
        this.slot = packetdataserializer.readShort();
        this.button = packetdataserializer.readByte();
        this.d = packetdataserializer.readShort();
        this.shift = (InventoryClickType) packetdataserializer.a(InventoryClickType.class);
        this.item = packetdataserializer.n();
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeByte(this.a);
        packetdataserializer.writeShort(this.slot);
        packetdataserializer.writeByte(this.button);
        packetdataserializer.writeShort(this.d);
        packetdataserializer.a((Enum) this.shift);
        packetdataserializer.a(this.item);
    }

    public int b() {
        return this.a;
    }

    public int c() {
        return this.slot;
    }

    public int d() {
        return this.button;
    }

    public short e() {
        return this.d;
    }

    public ItemStack f() {
        return this.item;
    }

    public InventoryClickType g() {
        return this.shift;
    }
}
