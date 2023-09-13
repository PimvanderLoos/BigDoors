package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutAutoRecipe implements Packet<PacketListenerPlayOut> {

    private int a;
    private IRecipe b;

    public PacketPlayOutAutoRecipe() {}

    public PacketPlayOutAutoRecipe(int i, IRecipe irecipe) {
        this.a = i;
        this.b = irecipe;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.readByte();
        this.b = CraftingManager.a(packetdataserializer.g());
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeByte(this.a);
        packetdataserializer.d(CraftingManager.a(this.b));
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
