package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.item.crafting.IRecipe;

public class PacketPlayOutAutoRecipe implements Packet<PacketListenerPlayOut> {

    private final int containerId;
    private final MinecraftKey recipe;

    public PacketPlayOutAutoRecipe(int i, IRecipe<?> irecipe) {
        this.containerId = i;
        this.recipe = irecipe.getKey();
    }

    public PacketPlayOutAutoRecipe(PacketDataSerializer packetdataserializer) {
        this.containerId = packetdataserializer.readByte();
        this.recipe = packetdataserializer.q();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeByte(this.containerId);
        packetdataserializer.a(this.recipe);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public MinecraftKey b() {
        return this.recipe;
    }

    public int c() {
        return this.containerId;
    }
}
