package net.minecraft.server;

import java.io.IOException;

public class PacketPlayInRecipeDisplayed implements Packet<PacketListenerPlayIn> {

    private MinecraftKey a;

    public PacketPlayInRecipeDisplayed() {}

    public PacketPlayInRecipeDisplayed(IRecipe<?> irecipe) {
        this.a = irecipe.getKey();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.p();
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.a);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public MinecraftKey b() {
        return this.a;
    }
}
