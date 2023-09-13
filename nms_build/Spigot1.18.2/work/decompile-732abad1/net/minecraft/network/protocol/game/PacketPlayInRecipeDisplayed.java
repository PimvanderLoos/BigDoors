package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.item.crafting.IRecipe;

public class PacketPlayInRecipeDisplayed implements Packet<PacketListenerPlayIn> {

    private final MinecraftKey recipe;

    public PacketPlayInRecipeDisplayed(IRecipe<?> irecipe) {
        this.recipe = irecipe.getId();
    }

    public PacketPlayInRecipeDisplayed(PacketDataSerializer packetdataserializer) {
        this.recipe = packetdataserializer.readResourceLocation();
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeResourceLocation(this.recipe);
    }

    public void handle(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.handleRecipeBookSeenRecipePacket(this);
    }

    public MinecraftKey getRecipe() {
        return this.recipe;
    }
}
