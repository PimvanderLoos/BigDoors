package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.item.crafting.IRecipe;

public class PacketPlayInRecipeDisplayed implements Packet<PacketListenerPlayIn> {

    private final MinecraftKey recipe;

    public PacketPlayInRecipeDisplayed(IRecipe<?> irecipe) {
        this.recipe = irecipe.getKey();
    }

    public PacketPlayInRecipeDisplayed(PacketDataSerializer packetdataserializer) {
        this.recipe = packetdataserializer.q();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.recipe);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public MinecraftKey b() {
        return this.recipe;
    }
}
