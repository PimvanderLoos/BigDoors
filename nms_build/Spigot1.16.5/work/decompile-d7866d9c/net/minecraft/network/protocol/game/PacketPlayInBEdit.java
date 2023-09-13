package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.item.ItemStack;

public class PacketPlayInBEdit implements Packet<PacketListenerPlayIn> {

    private ItemStack a;
    private boolean b;
    private int c;

    public PacketPlayInBEdit() {}

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.n();
        this.b = packetdataserializer.readBoolean();
        this.c = packetdataserializer.i();
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.a);
        packetdataserializer.writeBoolean(this.b);
        packetdataserializer.d(this.c);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public ItemStack b() {
        return this.a;
    }

    public boolean c() {
        return this.b;
    }

    public int d() {
        return this.c;
    }
}
