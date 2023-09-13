package net.minecraft.server;

import java.io.IOException;

public class PacketPlayInAutoRecipe implements Packet<PacketListenerPlayIn> {

    private int a;
    private IRecipe b;
    private boolean c;

    public PacketPlayInAutoRecipe() {}

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.readByte();
        this.b = CraftingManager.a(packetdataserializer.g());
        this.c = packetdataserializer.readBoolean();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeByte(this.a);
        packetdataserializer.d(CraftingManager.a(this.b));
        packetdataserializer.writeBoolean(this.c);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public int a() {
        return this.a;
    }

    public IRecipe b() {
        return this.b;
    }

    public boolean c() {
        return this.c;
    }
}
