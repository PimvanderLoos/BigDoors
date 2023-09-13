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
        this.recipe = irecipe.getId();
    }

    public PacketPlayOutAutoRecipe(PacketDataSerializer packetdataserializer) {
        this.containerId = packetdataserializer.readByte();
        this.recipe = packetdataserializer.readResourceLocation();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeByte(this.containerId);
        packetdataserializer.writeResourceLocation(this.recipe);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handlePlaceRecipe(this);
    }

    public MinecraftKey getRecipe() {
        return this.recipe;
    }

    public int getContainerId() {
        return this.containerId;
    }
}
